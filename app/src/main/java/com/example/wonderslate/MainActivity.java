package com.example.wonderslate;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;

import com.example.wonderslate.DB.DBHandler;
import com.example.wonderslate.databinding.ActivityMainBinding;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static final int PICK_PDF = 9544;

    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    private Uri selectedFile;
    private DBHandler dbHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        dbHandler = new DBHandler(MainActivity.this);
        verifyStoragePermissions(MainActivity.this);
        binding.upload.setOnClickListener(view -> uploadToSQL());
        binding.download.setOnClickListener(view -> {
            try {
                downloadFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

    }


    private void uploadToSQL() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf");
        startActivityForResult(Intent.createChooser(intent, "Open Chooser"), PICK_PDF);
    }

    private void downloadFile() throws IOException {
        Intent intent = new Intent(this, ListActivity.class);
        startActivity(intent);

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_PDF) {
            if (resultCode == RESULT_OK) {
                selectedFile = data.getData();
                String selectedPath = FileUtils.getPath(this, selectedFile);
                try {
                    saveToDB(selectedPath);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void saveToDB(String selectedFile) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(selectedFile));
        dbHandler.addFile(bytes);
        Toast.makeText(MainActivity.this, R.string.Success, Toast.LENGTH_SHORT).show();
    }


    public static void verifyStoragePermissions(Activity activity) {
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

}