package main;

import java.util.ArrayList;

import agent.Agent;


public class Account {

	private double balance;
	private ArrayList<Transaction> transactionList = new ArrayList<Transaction>();
	
	public void create(){
		balance = 100.0;
	}
	
	public void editBalance(double saleprice, Transaction t){ 
		this.balance = this.balance - saleprice;
		transactionList.add(t);
	}
	
	public void addToBalance(double saleprice){
		this.balance = this.balance + saleprice;
	}

	public double getBalance() {
		return balance;
	}

	public void setBalance(double balance) {
		this.balance = balance;
	}

	public ArrayList<Transaction> getTransactionList() {
		return transactionList;
	}

	public void setTransactionList(ArrayList<Transaction> transactionList) {
		this.transactionList = transactionList;
	}
	
	
}
