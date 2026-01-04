package com.bank.ui.swinglab;

import java.awt.CardLayout;

import javax.swing.JMenu;
import javax.swing.JPanel;

public class AppMediator {

		private static int user_id;
		
		private static BankFrame bank;
		private static CardLayout cardLayout;
		private static JPanel cards;  

		public static JPanel getCards() {
			return cards;
		}

		public static void setCards(JPanel cards) {
			AppMediator.cards = cards;
		}

		public static  CardLayout getCardLayout() {
			return cardLayout;
		}

		public static void setCardLayout(CardLayout cardLayout) {
			AppMediator.cardLayout = cardLayout;
		}

		public static BankFrame getBank() {
			return bank;
		}

		public static void setBank(BankFrame bank) {
			AppMediator.bank = bank;
		}

		public static int getUser_id() {
			return user_id;
		}

		public static void setUser_id(int user_id) {
			AppMediator.user_id = user_id;
		}
}
