package com.bank.managers;

import com.bank.dao.factory.DAOFactory;
import com.bank.dao.statements.StatementDAO;
import com.bank.model.statements.Statement;
import com.bank.patterns.observer.TransactionEvent;
import com.bank.patterns.observer.TransactionObserver;
import com.bank.storage.StorableList;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class StatementManager implements TransactionObserver {
    private static StatementManager instance;

    private final StatementDAO statementDAO;
    private final Map<String, StorableList<Statement>> statementsByIban;


    private StatementManager() {
        DAOFactory factory = DAOFactory.getDAOFactory(DAOFactory.FS);
        this.statementDAO = factory.getStatementDAO();
        this.statementsByIban = new HashMap<>();
        
        // Register as observer
        TransactionManager.getInstance().addObserver(this);
    }

    public static StatementManager getInstance() {
        if (instance == null) {
            instance = new StatementManager();
        }
        return instance;
    }


    // =====================================================================
    // Αποθήκευση / φόρτωση
    // =====================================================================

    /**
     * Αποθηκεύει ΟΛΑ τα statements για ΟΛΑ τα IBAN
     * σε ξεχωριστά αρχεία statements/<iban>.csv
     *
     * Προσοχή: χρησιμοποιεί append=true (όπως είχες),
     * άρα αν καλέσεις storeStatements πολλές φορές, τα statements
     * μπορεί να γραφτούν διπλά στο csv. Αν δεν θες αυτό,
     * απλά άλλαξε το false.
     */
    public void storeStatements() {
        for (Map.Entry<String, StorableList<Statement>> statements : statementsByIban.entrySet()) {
            String iban = statements.getKey();
            StorableList<Statement> list = statements.getValue();
            statementDAO.saveStatementsForIban(iban, list, false);
        }
    }

    /**
     * Φορτώνει statements για ένα IBAN από αρχείο
     * και ενημερώνει τον in-memory χάρτη.
     */
    public void loadStatementsFromFile(String iban) {
        StorableList<Statement> list = statementDAO.loadStatementsForIban(iban);
        statementsByIban.put(iban, list);
    }

    // =====================================================================
    // Basic API
    // =====================================================================

    public void addStatement(String iban, Statement statement) {
        StorableList<Statement> statements = statementsByIban.get(iban);
        if (statements==null) {
            statements = new StorableList<>();
            statementsByIban.put(iban, statements);
        }
        statements.add(0, statement);
    }

    public StorableList<Statement> getStatementsForIban(String iban) {
        if (statementsByIban.containsKey(iban)) {
            return statementsByIban.get(iban);
        } else {
            return new StorableList<>();
        }
    }

    public void showStatements(String iban) {
        StorableList<Statement> statements = getStatementsForIban(iban);
        if (statements.isEmpty()) {
            System.out.println("No statements for iban: " + iban);
            return;
        }

        for (Statement s : statements) {
            System.out.println(s);
        }
    }

    @Override
    public void onTransactionExecuted(TransactionEvent event) {
        Statement s = new Statement(
            event.getType(),
            event.getDate(),
            event.getAmount(),
            event.getDescription(),
            event.getTransactor(),
            event.getStatementSenderIban(),
            event.getStatementReceiverIban(),
            event.getBalanceAfter()
        );
        addStatement(event.getTargetIban(), s);
    }

    // =====================================================================
    // Δημιουργία Statements από Transactions
    // =====================================================================

    public void createIndividualStatement(String type,LocalDate date ,double amount, String description, String transactor,
    String senderIban, String receiverIban, double balanceAfter) {

        Statement s = new Statement(type, date, amount, description, transactor, senderIban,
                receiverIban, balanceAfter);

        addStatement(senderIban, s);
    }

    //dhmiourgia antikeimenoy statement k prso8hkh sthn lista
    public void createCompanyStatement(String type,LocalDate date , double amount, String description, String transactor,
                 String senderIban, String receiverIban,  double balanceAfter) {

        Statement s = new Statement(type, date, amount, description, transactor, senderIban,
                receiverIban, balanceAfter);

        addStatement(receiverIban, s);
    }
}