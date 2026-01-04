package com.bank.ui.swinglab;

import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

class BankFrame extends JFrame implements ActionListener{
	JMenuItem miLogin,miDashboard,miAccounts,miAbout,miExit;
	JMenu nav;
	
	
	public BankFrame() {
        setTitle("Bank of TUC e-Banking");
        setSize(900, 450);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // center on screen
        setVisible(true);
        
        // --- Menu bar ---
        JMenuBar bar = new JMenuBar();
        nav = new JMenu("Navigate");
        miLogin = new JMenuItem("Login");
        miDashboard = new JMenuItem("Dashboard");
        miAccounts = new JMenuItem("Accounts");
        miAbout = new JMenuItem("About");
        miExit = new JMenuItem("Exit");
        
        miDashboard.setVisible(false);
        miAccounts.setVisible(false);

        nav.add(miLogin);
        nav.add(miDashboard);
        nav.add(miAccounts);
        nav.add(miAbout);
        nav.addSeparator();
        nav.add(miExit);

        bar.add(nav);
        setJMenuBar(bar);
        
        buildPanels();
  
        AppMediator.setBank(this);
    }
	
	public void enableUserMenu() {
		miDashboard.setVisible(true);
        miAccounts.setVisible(true);
        miLogin.setVisible(false);
	}
	
	public void disableUserMenu() {
		miDashboard.setVisible(false);
        miAccounts.setVisible(false);
        miLogin.setVisible(true);
	}
    
    public void buildPanels() {
    	// --- Cards container (pages) ---
    	CardLayout cardLayout = new CardLayout();
    	JPanel cards = new JPanel(cardLayout);
    	AppMediator.setCardLayout(cardLayout);
    	AppMediator.setCards(cards);

    	// create panels (we'll implement below)
    	LoginPanel loginPanel = new LoginPanel();
    	DashboardPanel dashboardPanel = new DashboardPanel();
    	AccountsPanel accountsPanel = new AccountsPanel();
    	AboutPanel aboutPanel = new AboutPanel();

    	// register the panels with names
    	cards.add(loginPanel, "login");
    	cards.add(dashboardPanel, "dashboard");
    	cards.add(accountsPanel, "accounts");
    	cards.add(aboutPanel, "about");

    	add(cards); // add to frame content
    	
    	miLogin.addActionListener(this);
    	miDashboard.addActionListener(this);
    	miAccounts.addActionListener(this);
    	miAbout.addActionListener(this);
    	miExit.addActionListener(this);
  
    }

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource()==miLogin)
			AppMediator.getCardLayout().show(AppMediator.getCards(), "login");
		else if (e.getSource()==miDashboard)
			AppMediator.getCardLayout().show(AppMediator.getCards(), "dashboard");
		else if (e.getSource()==miAccounts) 
			AppMediator.getCardLayout().show(AppMediator.getCards(), "accounts");
		else if (e.getSource()==miAbout) 
			AppMediator.getCardLayout().show(AppMediator.getCards(), "about");
		else if (e.getSource()==miExit) 
			System.exit(0);
	}
}
