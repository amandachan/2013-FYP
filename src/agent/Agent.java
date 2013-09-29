package agent;

import java.util.ArrayList;

import weka.core.Instance;
import weka.core.Instances;

import main.Account;
import main.Transaction;

import defenses.*;
import environment.*;

import attacks.*;


public abstract class Agent {

	protected int id;
	protected boolean ishonest;
	protected int day;
	protected Instances history;
	protected Environment ecommerce = null;
	protected ArrayList<Seller> listOfSellers = new ArrayList<Seller>();
	protected ArrayList<Buyer> listOfBuyers = new ArrayList<Buyer>();
	protected String defenseName = null;
	protected String attackName = null;
	protected Attack attackModel;


	protected Defense defenseModel = null;
	//previously BSR[][][]. Transaction stores the buyer's rating to sellers also.
	protected ArrayList<Transaction> trans = new ArrayList<Transaction>();
	protected Account account;
	protected double credits=0.0;

	
	
	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public Agent() { 
		account = new Account();
		account.create();
		attackModel = new AlwaysUnfair();
		defenseModel = new BRS();

		//history = new Instances(ecommerce.getM_Transactions());
	}

	public void setGlobalInformation(ArrayList<Seller> sellers, ArrayList<Buyer> buyers){
		this.listOfBuyers = buyers;
		this.listOfSellers = sellers;
	}

	public double getCredits(){
		return credits;
	}

	public void setCredits(double credits){
		this.credits=credits;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	
	public Account getAccountDetails(){  //added by neel
		return account;
	}

	public int getDay() {
		return day;
	}

	public void setDay(int day) {
		this.day = day;
	}

	public Instances getHistory() {
		return history;
	}

	public void setHistory(Instances history) {
		this.history = history;
	}

	public Environment getEcommerce() {
		return ecommerce;
	}

	public void setEcommerce(EnvironmentS ecommerce) {
		this.ecommerce = ecommerce;
	}

	public ArrayList<Seller> getListOfSellers() {
		return listOfSellers;
	}

	public void setListOfSellers(ArrayList<Seller> listOfSellers) {
		this.listOfSellers = listOfSellers;
	}

	public ArrayList<Buyer> getListOfBuyers() {
		return listOfBuyers;
	}

	public void setListOfBuyers(ArrayList<Buyer> listOfBuyers) {
		this.listOfBuyers = listOfBuyers;
	}

	public String getDefenseName() {
		return defenseName;
	}

	public void setDefenseName(String defenseName) {
		this.defenseName = defenseName;
	}

	public String getAttackName() {
		return attackName;
	}

	public void setAttackName(String attackName) {
		this.attackName = attackName;
	}

	public Attack getAttackModel() {
		return attackModel;
	}

	public void setAttackModel(Attack attackModel) {
		this.attackModel = attackModel;
	}

	public Defense getDefenseModel() {
		return defenseModel;
	}

	public void setDefenseModel(Defense defenseModel) {
		this.defenseModel = defenseModel;
	}

	public ArrayList<Transaction> getTrans() {
		return trans;
	}

	public void setTrans(ArrayList<Transaction> trans) {
		this.trans = trans;
	}

	public boolean isIshonest() {
		return ishonest;
	}

	public void setIshonest(boolean ishonest) {
		this.ishonest = ishonest;
	}

	public void addInstance(Instance inst){
		history.add(inst);
	}

	public Seller getSeller(int sid){
		return listOfSellers.get(sid);
	}

	public Buyer getBuyer(int bid){
		return listOfBuyers.get(bid);
	}

	public void setEcommerce(Environment ecommerce) {
		this.ecommerce = ecommerce;
		history = new Instances(ecommerce.getM_Transactions());
	}


}
