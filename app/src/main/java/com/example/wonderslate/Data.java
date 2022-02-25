package com.example.wonderslate;

import java.io.Serializable;

public class Data implements Serializable {

    private byte[] file;
    private int number;

    public Data(byte[] file, int number) {
        this.file = file;
        this.number= number;
    }

    public byte[] getFile() {
        return file;
    }

    public void setFile(byte[] file) {
        this.file = file;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }
}
