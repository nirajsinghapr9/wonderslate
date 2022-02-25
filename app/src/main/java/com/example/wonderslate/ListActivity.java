package com.example.wonderslate;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.wonderslate.DB.DBHandler;
import com.example.wonderslate.databinding.ActivityListBinding;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class ListActivity extends AppCompatActivity implements MainAdapter.ClickListener {

    private ActivityListBinding binding;
    private ArrayList<Data> data;
    private MainAdapter adapter;
    private DBHandler dbHandler;
    ProgressDialog progressDoalog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_list);
        fetchData();
    }

    private void fetchData() {
        dbHandler = new DBHandler(ListActivity.this);

        data = dbHandler.readData();
        adapter = new MainAdapter(this, data);
        adapter.setOnClickListener(this);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(adapter);
    }

    @Override
    public void onClickViewOrder(int position, Data data) {
        int count = dbHandler.numberOfRows();
        File file = new File(this.getFilesDir().getAbsolutePath(), "test.pdf");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(data.getFile());
            fos.close();
            showLoader(file);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(ListActivity.this, R.string.error_msg, Toast.LENGTH_SHORT).show();
        }
    }

    public void openFile(File url) throws IOException {
        String extension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(url).toString());
        String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        Intent intent = new Intent(Intent.ACTION_VIEW);

        intent.setFlags(intent.FLAG_ACTIVITY_CLEAR_TOP | intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = FileProvider.getUriForFile(ListActivity.this, ListActivity.this.getApplicationContext().getPackageName(), url);
        try {
            intent.setDataAndType(uri, mimeType);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(Intent.createChooser(intent, "choseFile"));
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(ListActivity.this, "File not found, Upload new", Toast.LENGTH_SHORT).show();
        }
    }

    private void showLoader(File file) {
        final ProgressDialog dialog = new ProgressDialog(ListActivity.this);
        dialog.setTitle("Downloading pdf");
        dialog.setMessage("Please wait..");
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.show();

        long delayInMillis = 500;
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                dialog.dismiss();
                try{
                    openFile(file);
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        }, delayInMillis);
    }

}

