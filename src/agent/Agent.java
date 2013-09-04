package agent;

import java.util.ArrayList;

import main.Account;
import main.Transaction;

import defenses.Defense;

import attacks.Attack;


public abstract class Agent {

	protected int id;
	protected double reputation;
	protected String defenseName;
	protected String attackName;
	protected Attack attack;
	protected Defense defense;
	protected ArrayList<Transaction> trans;
	protected Account account;  
	
	public Agent() { 
		account = new Account(); // need to instantiate to avoid null pointer exception error
                                         // can't instantiate attack and defense since they are abstract classes
	}
	
	public Agent(int id, double reputation) {
		this.id = id;
		this.reputation = reputation;
	}
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public double getReputation() {
		return reputation;
	}
	
	public void setReputation(double reputation) {
		this.reputation = reputation;
	}
	public void setAccount(double balance){ //added by neel
            account.editBalance(balance);
        }
	public Account getAccountDetails(){  //added by neel
            return account;
        }
}
