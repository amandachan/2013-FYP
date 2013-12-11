package main;

import java.util.ArrayList;
import java.util.HashMap;

import agent.Buyer;

public class BankBalance extends EvaluationMetrics{
	
	HashMap<Integer, ArrayList<Double> > bankBalance;
	
	public BankBalance(){
		bankBalance = new HashMap<Integer, ArrayList<Double>>();
	}
	

	public HashMap<Integer, ArrayList<Double>> getBankBalance() {
		return bankBalance;
	}


	public void setBankBalance(HashMap<Integer, ArrayList<Double>> bankBalance) {
		this.bankBalance = bankBalance;
	}


	public void updateDailyBankBalance(int day, ArrayList<Buyer> buyerList){
		ArrayList<Double> bankBal = new ArrayList<Double>();
		for(int i=0; i<buyerList.size(); i++){
			bankBal.add(buyerList.get(i).getAccount().getBalance());
		}
		bankBalance.put(day, bankBal);
	}
	
	public void printDailyBalance(int day){
		double bal = 0; int id=Integer.MAX_VALUE;
		ArrayList<Double> buyerDailyBal = bankBalance.get(day);
		for(int i=0; i<buyerDailyBal.size(); i++){
			if(buyerDailyBal.get(i) > bal){
				bal = buyerDailyBal.get(i);
				id = i;
			}
		}
		System.out.println("Buyer " + id + " has the biggest account balance of " + bal + " for day " + day);
	}
}
