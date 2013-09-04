package main;

import java.util.ArrayList;

import agent.Agent;


public class Account {
	private Agent ag;
	private double balance;
	private ArrayList<Transaction> transactionList;
	
	public void create(){
		
	}
	
	public void editBalance(double balance){
		this.balance = balance;
	}
}
