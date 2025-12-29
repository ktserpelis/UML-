package com.bank.storage;

import java.io.BufferedReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class CSVStorageManager implements StorageManager {
    private static CSVStorageManager instance;
    private String storagePath;

    private CSVStorageManager() {
        this.storagePath = "./data/";
    }

    public static CSVStorageManager getInstance() {
        if (instance == null) {
            instance = new CSVStorageManager();
        }
        return instance;
    }

    public String getStoragePath() {
        return storagePath;
    }

    public void setStoragePath(String storagePath) {
        this.storagePath = storagePath;
    }

    @Override
    public void loadObject(Storable storable, String fileName)
            throws UnMarshalingException, IOException, FileNotFoundException{
        String filePath = storagePath + fileName;
        File file = new File(filePath);
        if (!file.exists()) {
            throw new FileNotFoundException("File " + filePath + " does not exist");
        }
        if (!file.canRead() || !file.isFile()) {
            throw new IOException("File " + filePath + " cannot be read");
        }
        BufferedReader fileBufferReader = null;
        try {
            fileBufferReader = new BufferedReader(new FileReader(file));
            String line;
            StringBuffer sb = new StringBuffer("");
            while ((line = fileBufferReader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            storable.unmarshal(sb.toString());
        } finally {
            try {
                fileBufferReader.close();
            } catch (IOException e) {
            }
        }
    }

    @Override
    public void storeObject(Storable storable, String fileName , boolean append) throws IOException {
        String filePath = storagePath + fileName;
        FileWriter writer = null;
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                file.createNewFile();
            }
            writer = new FileWriter(file,append);

            writer.write(storable.marshal());
        } finally {
            try {
                writer.close();
            }catch(IOException ex) {

            }
        }
    }


}