package com.bank.ui.swinglab;

import java.awt.BorderLayout;
import javax.swing.*;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

class DashboardPanel extends JPanel implements ActionListener{
	
	JButton btnAccounts, btnAbout, btnLogout;
    
    DashboardPanel() {
        setLayout(new BorderLayout(10,10)); //(int hgap, int vgap)
        setBorder(BorderFactory.createEmptyBorder(16,16,16,16));

        JLabel title = new JLabel("Dashboard", SwingConstants.LEFT);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 20f));
        add(title, BorderLayout.NORTH);

        JPanel center = new JPanel(new GridLayout(1, 3, 10, 10)); // (rows, cols, hgap, vgap)
        btnAccounts = new JButton("Show Accounts");
        btnAbout = new JButton("About");
        btnLogout = new JButton("Logout");

        center.add(btnAccounts);
        center.add(btnAbout);
        center.add(btnLogout);
        
        center.setPreferredSize(new Dimension(200, center.getPreferredSize().height));
        
        add(center, BorderLayout.CENTER);

        // events
        btnAccounts.addActionListener(this);
        btnAbout.addActionListener(this);
        btnLogout.addActionListener(this);

    }

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource()==btnAccounts)
			AppMediator.getCardLayout().show(AppMediator.getCards(), "accounts");
		else if (e.getSource()==btnAbout)
			AppMediator.getCardLayout().show(AppMediator.getCards(), "about");
		else if (e.getSource()==btnLogout) {
			AppMediator.getCardLayout().show(AppMediator.getCards(), "login");
			AppMediator.getBank().disableUserMenu();
			AppMediator.setUser_id(0);
		}
	}
}

