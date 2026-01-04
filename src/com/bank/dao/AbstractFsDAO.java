package com.bank.dao;

import com.bank.storage.CSVStorageManager;
import com.bank.storage.Storable;
import com.bank.storage.StorableList;
import com.bank.storage.UnMarshalingException;

import java.io.FileNotFoundException;
import java.io.IOException;

public abstract class AbstractFsDAO<T extends Storable> {

    protected final CSVStorageManager storage = CSVStorageManager.getInstance();
    private final String fileName;

    protected AbstractFsDAO(String fileName) {
        this.fileName = fileName;
    }

    protected StorableList<T> loadList() {
        StorableList<T> list = new StorableList<>();

        try {
            storage.loadObject(list, fileName);
        }
        catch (FileNotFoundException e) {
            // Αν δεν υπάρχει το αρχείο επιστρέφουμε άδεια λίστα
            System.out.println("[DAO] File not found: " + fileName + " (returning empty list)");
        }
        catch (IOException | UnMarshalingException e) {
            throw new RuntimeException("DAO load failed for file: " + fileName, e);
        }

        return list;
    }

    protected void saveList(StorableList<T> list) {
        try {
            storage.storeObject(list, fileName, false);
        }
        catch (IOException e) {
            throw new RuntimeException("DAO save failed for file: " + fileName, e);
        }
    }
}
