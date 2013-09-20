package agent;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Vector;

import weka.core.Instance;
import weka.core.Instances;

import main.Product;
import main.Rating;
import main.Transaction;

import defenses.BRS;
import defenses.Defense;
import distributions.PseudoRandom;
import main.Parameter;

import attacks.AlwaysUnfair;
import attacks.Attack;

public class Buyer extends Agent{
	private boolean ishonest;
	private ArrayList<Rating> ratingsToProducts;
	private ArrayList<Integer> productsPurchased;
	private ArrayList<Seller> sellersRated = new ArrayList<Seller>();

	//buyer's current rating (the most recent rating made by the buyer)
	private double currentRating;

	//for building buyer's trust network
	private ArrayList<Integer> advisors = new ArrayList<Integer>();
	private ArrayList<Double> trusts = new ArrayList<Double>();
	private double[][] m_SaverTA = new double[2][2];
	private int depthLimit = 4;
	private int neighborSize = 3;
	private ArrayList<Integer> trustNetwork = null;
	private double[] bounds = {0.0, 1.0};
	private double fitness;
	private int TNtype = 1; //0/1/2 means honest trust network/noise/collusive


	public Buyer (int id){
		
		this.id = id;

	}

	public Buyer(){
	}

	public boolean isIshonest() {
		return ishonest;
	}

	public void setIshonest(boolean ishonest) {
		this.ishonest = ishonest;
	}

	public ArrayList<Rating> getRatingsToProducts() {
		return ratingsToProducts;
	}

	public void setRatingsToProducts(ArrayList<Rating> ratingsToProducts) {
		this.ratingsToProducts = ratingsToProducts;
	}

	public ArrayList<Integer> getProductsPurchased() {
		return productsPurchased;
	}

	public void setProductsPurchased(ArrayList<Integer> productsPurchased) {
		this.productsPurchased = productsPurchased;
	}

	public ArrayList<Transaction> getBuyerTransactions(){
		ArrayList<Transaction> buyerTransactions = null;
		return buyerTransactions;
	}

	public ArrayList<Integer> getPurchasedProducts(){
		return productsPurchased;
	}

	//give rating
	public void rateSeller(int day){
		this.day = day;
		if(this.day > 0){//scan all the history information,
			for (int i = 0; i < history.numInstances(); i++) {
				Instance inst = history.instance(i);
				int dVal = (int) (inst.value(Parameter.m_dayIdx));
				// only complete the current day transaction
				if (dVal != this.day)continue;				
				if (this.ishonest == false) {
					attackModel.setEcommerce(ecommerce);
					currentRating = attackModel.giveUnfairRating(inst);
				} else {
					defenseModel.seteCommerce(ecommerce);
					defenseModel.giveFairRating(inst);

				}				

				//update sellers' history
				int sVal = (int)(inst.value(Parameter.m_sidIdx));
				listOfSellers.get(sVal).addInstance(new Instance(inst));
			}
		}		
	}

	//set attack model
	public Attack attackSetting(String attackName){
		Attack attackModel= new AlwaysUnfair();
		try{
			Class<?> class1 = Class.forName("attacks."+attackName.trim());
			Constructor<?> cons = class1.getDeclaredConstructor();
			cons.setAccessible(true);
			attackModel = (Attack) cons.newInstance();
		}
		catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		}
		catch (InstantiationException ex) {
			ex.printStackTrace();
		}
		catch (IllegalAccessException ex) {
			ex.printStackTrace();
		}
		catch (InvocationTargetException ex) {
			ex.printStackTrace();
		}
		catch (NoSuchMethodException ex) {
			ex.printStackTrace();
		}
		//set the eCommerce
		attackModel.setEcommerce(ecommerce);
		return attackModel;
	}

	//set defense model
	public Defense defenseSetting(String defenseName) throws ClassNotFoundException, NoSuchMethodException, SecurityException{
		defenseModel=  new BRS();
		try{
			Class class1 = Class.forName("defenses."+defenseName.trim());
			Constructor cons = class1.getDeclaredConstructor();
			cons.setAccessible(true);
			defenseModel = (Defense) cons.newInstance();
		}
		catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		}
		catch (InstantiationException ex) {
			ex.printStackTrace();
		}
		catch (IllegalAccessException ex) {
			ex.printStackTrace();
		}
		catch (InvocationTargetException ex) {
			ex.printStackTrace();
		}
		catch (NoSuchMethodException ex) {
			ex.printStackTrace();
		}
		// set the eCommerce
		defenseModel.seteCommerce(ecommerce);
		return defenseModel;
	}

	//choose whether to perform attack or defense depending on buyer's honesty
	public void chooseModel(String modelName) throws ClassNotFoundException, NoSuchMethodException, SecurityException{
		if(ishonest == false){
			//dishonest buyer
			attackName = new String(modelName);
			attackModel = attackSetting(modelName);
		} else{
			//honest buyer
			defenseName = new String(modelName);
			defenseModel = defenseSetting(modelName);	
		}
	}

	//randomly choose product that is sold by the chosen seller
	public int chooseProduct(int sellerid){
		Random randomGenerator = new Random();

		//get hashmap of seller id & product id
		HashMap<Integer, ArrayList<Integer>> prodList = new HashMap<Integer, ArrayList<Integer>>();
		Product p = new Product();
		prodList = p.getProductid();

		//get list of products sold by the seller
		ArrayList<Integer> prodid = new ArrayList<Integer>();
		prodid = prodList.get(sellerid);

		//randomly select product from list of products
		int pid = randomGenerator.nextInt(prodid.size());
		return pid;

	}

	//retrieve product price based on chosen product
	public double buyProduct(int productid){

		//get hashmap of product id and sale price
		HashMap<Integer, Double> priceList = new HashMap<Integer, Double>();
		Product p = new Product();
		priceList = p.getSaleprice();

		//get price of product
		double price = priceList.get(productid);
		return price;
	}

	//create transaction that includes buyer, seller and product
	public Instance addTransaction(int day){
		this.day = day;
		Instances transactions = ecommerce.getM_Transactions();
		double rVal = Parameter.nullRating();
		int dVal, bVal, sVal, productid; 
		Seller s1 = null;
		double salePrice;
		String bHonestVal = null;
		if (ishonest==false){ //attack
			//select seller and product, then create transaction

			s1 = attackModel.chooseSeller(this);
			bHonestVal = Parameter.agent_dishonest;
		}
		else if (ishonest==true){//defense
			s1 = defenseModel.chooseSeller(this);
			bHonestVal = Parameter.agent_honest;

		}
		//productid = chooseProduct(s1);
		//salePrice = buyProduct(productid);
		Transaction t = new Transaction();
		rateSeller(day);
		t.create(this, s1, 1, 1, 1.0, day, 1.0, currentRating, s1.getId());
		sellersRated.add(s1);
		//productsPurchased.add(productid);
		trans.add(t);
		ecommerce.updateTransactionList(t);
		sVal = s1.getId();

		double sHonestVal = ecommerce.getSellersTrueRating(s1.getId());
		//add instance, update the array in e-commerce
		dVal = day + 1;
		bVal = this.getId();
		Instance inst = new Instance(transactions.numAttributes());
		inst.setDataset(transactions);
		inst.setValue(Parameter.m_dayIdx, dVal);
		inst.setValue(Parameter.m_bidIdx, "b" + Integer.toString(bVal)); 
		inst.setValue(Parameter.m_bHonestIdx, bHonestVal);
		inst.setValue(Parameter.m_sidIdx, "s" + Integer.toString(sVal));
		inst.setValue(Parameter.m_sHonestIdx, sHonestVal);			
		inst.setValue(Parameter.m_ratingIdx, rVal);	
		this.addInstance(new Instance(inst));
		
		System.out.println("Seller ID: " + s1.getId() + " Buyer ID: " + this.getId() + " Rating: " + currentRating + " Day: " + day);
		return inst;
	}

	//----the below is used to build buyer's trust network of advisors-------------------------------

	public void calculateAverageTrusts(int sid){
		int index = 0; //only 0/1, means dishonest/honest duopoly seller
		if(sid == Parameter.TARGET_HONEST_SELLER){
			index = 1;
		}
		m_SaverTA[index][0] = 0.0;
		m_SaverTA[index][1] = 0.0;
		int numDA = 0; //number of dishonest advisors;
		int numHA = 0; //number of honest advisors;
		int hb = Parameter.NO_OF_HONEST_BUYERS;	
		for(int j = 0; j < advisors.size(); j++){					
			int aid = advisors.get(j);
			if(aid == this.id)continue;
			if(aid < Parameter.NO_OF_DISHONEST_BUYERS || aid >= Parameter.NO_OF_DISHONEST_BUYERS + hb){
				m_SaverTA[index][0] += trusts.get(aid);
				numDA++;
			} 
			else{
				m_SaverTA[index][1] += trusts.get(aid);
				numHA++;
			}
		}				
		if (numDA != 0) {
			m_SaverTA[index][0] /= (numDA);
		}
		if (numHA != 0) {
			m_SaverTA[index][1] /= (numHA);		
		}
	}

	private ArrayList<Integer> maxFastSort(ArrayList<Double> x, int m) {
		trusts.set(this.id, 0.0);
		int len = x.size();
		if(len > Parameter.NO_OF_DISHONEST_BUYERS + Parameter.NO_OF_HONEST_BUYERS){
			len = Parameter.NO_OF_DISHONEST_BUYERS + Parameter.NO_OF_HONEST_BUYERS + (day - 1) * Parameter.NO_OF_DISHONEST_BUYERS;
		}
		ArrayList<Integer> idx = new ArrayList<Integer>();
		for (int j = 0; j < len; j++) {
			idx.set(j, j);
		}
		for (int i = 0; i < m; i++) {
			for (int j = i + 1; j < len; j++) {
				if (x.get(idx.get(i)) < x.get(idx.get(j))) {
					int id = idx.get(i);
					idx.set(i, idx.get(j));
					idx.set(j, id);
				} // if
			}
		} // for
		trusts.set(id, 1.0);		
		return idx;
	}

	private void selectReliableNeighbor(){
		//select neighbor with high weight from the witness	
		trusts.set(id, 0.0);		
		ArrayList<Integer> idx = maxFastSort(trusts, neighborSize);
		setTrustNetwork(idx);
		trusts.set(id, 1.0);
	}

	public void resetWitness(int day){
		if(day > 0){
			selectReliableNeighbor();
		}
		advisors.clear();
		int depth = 1;
		buildTrustNet(this, depth, advisors);
	}

	public void setAverageTrusts(int sid, double[] averTA){
		int index = 0; //only 0/1, means dishonest/hoenst duopoly seller
		if(sid == Parameter.TARGET_HONEST_SELLER){
			index = 1;
		}
		m_SaverTA[index][0] = averTA[0];
		m_SaverTA[index][1] = averTA[1];
	}

	public double[] getAverageTrusts(int sid){
		int index = 0; //only 0/1, means dishonest/hoenst duopoly seller
		if(sid == Parameter.TARGET_HONEST_SELLER){
			index = 1;
		}
		return m_SaverTA[index];
	}

	public Buyer getAdvisor(int aid){
		int db = Parameter.NO_OF_DISHONEST_BUYERS;
		int hb = Parameter.NO_OF_HONEST_BUYERS;
		if(aid >= db + hb){
			aid = (aid - (db + hb)) % db;
		}
		return listOfBuyers.get(aid);
	}

	public void setTrustNetwork(ArrayList<Integer> sn){
		for(int i = 0; i < neighborSize; i++){
			trustNetwork.set(i, sn.get(i));
		}
	}

	public ArrayList<Integer> getTrustNetwork(){
		return trustNetwork;
	}

	public void setTrustAdvisor(int aid, double trust){
		if(trusts.size()==0){
			for(int i=0; i<listOfBuyers.size(); i++){
				trusts.add(i, 0.0);
			}
		}
		trusts.set(aid, trust);
	}

	public void setTrusts(ArrayList<Double> ws){
		for(int i = 0; i < trusts.size(); i++){
			trusts.set(i,ws.get(i));
		}
	}

	public ArrayList<Double> getTrusts(){
		return trusts;
	}

	private void findNeighbors(int type){
		//two types = 0/1, from the limit/whole
		ArrayList<Integer> neighbor = new ArrayList<Integer>();
		int numBuyers = listOfBuyers.size(); 	
		if(type == 0){//for dishonest buyers; collusion
			ArrayList<Integer> limit = new ArrayList<Integer>();
			int db = Parameter.NO_OF_DISHONEST_BUYERS;
			int hb = Parameter.NO_OF_HONEST_BUYERS;	
			if(TNtype == 0){//honest trust network
				for (int i = 0; i < numBuyers; i++) {						
					if (i >= db && i < db + hb) {
						limit.add(i);
					}
				}
			} 
			else { //dishonest trust network
				for (int i = 0; i < numBuyers; i++) {
					if (i < db || i >= db + hb) {
						limit.add(i);
					}
				}
			}
			int numLimit = limit.size();	
			for (int i = 0; i < neighborSize; i++) {
				int addID = 0;
				do {
					int rnd = PseudoRandom.randInt(0, numLimit - 1);
					addID = limit.get(rnd);
				} while (neighbor.contains(addID));
				neighbor.add(addID);
				trustNetwork.set(i, addID);
			}
		} 
		else{//for honest buyers
			numBuyers = Parameter.NO_OF_DISHONEST_BUYERS + Parameter.NO_OF_HONEST_BUYERS;
			for (int i = 0; i < neighborSize; i++) {
				int addID = 0;
				do {
					addID = PseudoRandom.randInt(0, numBuyers- 1);
				} while (neighbor.contains(addID));
				neighbor.add(addID);
				trustNetwork.set(i, addID);
			}
		}		
	}

	private void buildTrustNet(Buyer buyer, int depth, ArrayList<Integer> advisors){ 
		if(depth > depthLimit)return;			
		ArrayList<Integer> sn = buyer.getTrustNetwork();
		for(int i = 0; i < sn.size(); i++){
			int aid = sn.get(i);				
			if(advisors.contains(aid) == false){
				advisors.add(aid);
				Buyer advisor = buyer.getAdvisor(aid);
				buildTrustNet(advisor, depth + 1, advisors);
			}
		}	
	}

	public void setDepthNeighborSize(int depth, int neighborSize){
		depthLimit = depth;
		this.neighborSize = neighborSize;
	}

	public void InitialTrustNetwork(int snType){
		if(Parameter.includeEA(ecommerce.getDefenseName()) || Parameter.includeWMA(ecommerce.getDefenseName())){
			TNtype = snType;
			//set the trust Network, trusts, and fitness
			trustNetwork = new ArrayList<Integer>();
			int numBuyers = listOfBuyers.size();
			trusts = new ArrayList<Double>();			
			if(this.ishonest == false){
				findNeighbors(0);			
				if (TNtype == 0) { // honest weight and fitness;
					int db = Parameter.NO_OF_DISHONEST_BUYERS;
					int hb = Parameter.NO_OF_HONEST_BUYERS;
					for (int i = 0; i < numBuyers; i++) {
						if (i < db || i >= db + hb) {
							trusts.set(i, 0.0);
						} 
						else {
							trusts.set(i,1.0);
						}					
					}
					trusts.set(id,1.0);
					fitness = 1.0;
				} 
				else if (TNtype == 1) { // noise weight and fitness;
					for (int i = 0; i < numBuyers; i++) {					
						trusts.set(i,PseudoRandom.randDouble(bounds[0], bounds[1]));						
					}
					trusts.add(id,1.0);
					fitness = PseudoRandom.randDouble(0, 1);
				} 
				else {//collusive	
					int db = Parameter.NO_OF_DISHONEST_BUYERS;
					int hb = Parameter.NO_OF_HONEST_BUYERS;
					for (int i = 0; i < numBuyers; i++) {								
						if (i < db || i >= db + hb) {
							trusts.set(i, 1.0);
						} 
						else {
							trusts.set(i, 0.0);
						}
					}
					fitness = 1.0;
				}
			} 
			else{
				findNeighbors(1);
				for (int i = 0; i < numBuyers; i++) {		
					trusts.set(i,PseudoRandom.randDouble(bounds[0], bounds[1]));
				}
				trusts.set(id,1.0);
				fitness = 0.0;
			}		
			//initial witnesses
			advisors = new ArrayList<Integer>();			
		}
	}

	public ArrayList<Integer> getAdvisors() {
		return advisors;
	}

	public void setAdvisors(ArrayList<Integer> advisors) {
		this.advisors = advisors;
	}

}
