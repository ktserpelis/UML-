package com.bank.dao.statements;

import com.bank.model.statements.Statement;
import com.bank.storage.CSVStorageManager;
import com.bank.storage.StorableList;
import com.bank.storage.UnMarshalingException;
import java.io.FileNotFoundException;
import java.io.IOException;

public class FsStatementDAO implements StatementDAO {
    private static final String STATEMENTS_DIR = "statements/";
    private final CSVStorageManager storage = CSVStorageManager.getInstance();

    @Override
    public StorableList<Statement> loadStatementsForIban(String iban) {
        String filePath = STATEMENTS_DIR + iban + ".csv";
        StorableList<Statement> list = new StorableList<>();

        try {
            storage.loadObject(list, filePath);
        } catch (FileNotFoundException e) {
            System.out.println("[StatementDAO] No statements file for iban " + iban + " (" + filePath + ")");
        } catch (IOException | UnMarshalingException e) {
            throw new RuntimeException("Error loading statements for iban " + iban + " from " + filePath, e);
        }

        return list;
    }

    @Override
    public void saveStatementsForIban(String iban, StorableList<Statement> statements, boolean append) {
        String filePath = STATEMENTS_DIR + iban + ".csv";

        try {
            storage.storeObject(statements, filePath, append);
        } catch (IOException e) {
            throw new RuntimeException("Error saving statements for iban " + iban + " to " + filePath, e);
        }
    }
}
