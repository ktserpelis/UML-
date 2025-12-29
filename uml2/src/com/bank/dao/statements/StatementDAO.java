package com.bank.dao.statements;

import com.bank.model.statements.Statement;
import com.bank.storage.StorableList;

public interface StatementDAO {
    StorableList<Statement> loadStatementsForIban(String iban);
    void saveStatementsForIban(String iban, StorableList<Statement> statements, boolean append);


}
