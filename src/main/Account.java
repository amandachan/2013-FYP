package main;

import java.util.ArrayList;

import agent.Agent;
import agent.Buyer;
import agent.Seller;


public class Account {

	private double balance;
	private ArrayList<Transaction> transactionList = new ArrayList<Transaction>();
	
	public void create(){
		balance = 100.0;
	}
	
	//buyer
	public void editBalance(double saleprice, Transaction t){ 
		this.balance = this.balance - saleprice;
		transactionList.add(t);
	}
	
	//seller
	public void addToBalance(double saleprice){
		this.balance = this.balance + saleprice;
	}

	public void credits(Seller s){
		double newBal=0;
		if(s.isIshonest() == true){
			 newBal = balance + 1.0;
		}
		else{
			 newBal = balance - 1.0;
		}
		this.setBalance(newBal);
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
