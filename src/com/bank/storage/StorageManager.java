package com.bank.storage;

import java.io.FileNotFoundException;
import java.io.IOException;

public interface StorageManager {
     void loadObject(Storable s, String filePath) throws UnMarshalingException, IOException, FileNotFoundException;
     void storeObject(Storable s, String filePath, boolean append)  throws IOException;
}
