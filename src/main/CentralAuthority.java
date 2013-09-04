package main;
import agent.Buyer;
import agent.Seller;

public class CentralAuthority {

	private environment environmentType;
	
	//attack and defense name
	private String defenseType;
	private String attackType;	
	private buyerList<Buyer> buyers;
	private sellerList<Seller> sellers;

	//sellers sales, reputation, reputation difference per day
	private int[][] dailySales;
	private double[][] dailyRep;
	private double[][] dailyRepDiff;
	
	//Defense model cost time per day
	private double[] defenseTime_day;
	private double[][] dailyAvgWeights;
	private double[][] dailyMCC;
	
	 public void setUpEnvironment(String attackName, String defenseName){
		 
		 if(Parameter.includeICLUB(defenseName)){			
				if (Parameter.RATING_TYPE.equalsIgnoreCase("binary")) {
					environmentType = new eCommerceB(attackName, defenseName);
				} else if (Parameter.RATING_TYPE.equalsIgnoreCase("multinominal")) {
					environmentType = new eCommerceM(attackName, defenseName);
				} else{
					environmentType = new eCommerceM(attackName, defenseName);
				}
			} else if(Parameter.includeWMA(m_defenseName)){
				environmentType = new eCommerceR(attackName, defenseName);
			} else if(Parameter.includeEA(m_defenseName)){
				if (Parameter.RATING_TYPE.equalsIgnoreCase("binary")) {
					environmentType = new eCommerceB(attackName, defenseName);
				} else if (Parameter.RATING_TYPE.equalsIgnoreCase("multinominal")) {
					environmentType = new eCommerceM(attackName, defenseName);
				} else if (Parameter.RATING_TYPE.equalsIgnoreCase("real")) {
					environmentType = new eCommerceR(attackName, defenseName);
				} 	
			} else{
				//for binary ratings, nondefense, BRS, iCLUB, TRAVOS, personalized,
				environmentType = new eCommerceB(attackName, defenseName);
			}
		 	environmentType.generateInstances();
		 	
		 	
		 	//agent setting
		 	int numBuyers = Parameter.NO_OF_DISHONEST_BUYERS + Parameter.NO_OF_HONEST_BUYERS;
			int numSellers = environmentType.getNumberSellers(); 
			for(int i = 0; i < numBuyers; i++){
				int bid = i;
				if(bid < Parameter.NO_OF_DISHONEST_BUYERS || bid >= numBuyers){
					buyers.add(i,new Buyer(bid, false, attackName, environmentType));
				} else{
					buyers.add(i, new Buyer(bid, true, defenseName, environmentType));
				}
				
			}
			for(int k = 0; k < numSellers; k++){
				int sid = k;
				if(sid < Parameter.NO_OF_DISHONEST_SELLERS){
					sellers.add(k,new Seller(sid, false, environmentType));
				} else{
					sellers.add(k,new Seller(sid, true, environmentType));
				}			
			}
			
			//set up the global information for buyers;
			for(int i = 0; i < numBuyers; i++){			
				buyers.get(i).setGlobalInformation(buyers, sellers);
			}
			
			//for the evolutionary algorithm
			if(Parameter.includeEA(defenseType) || Parameter.includeWMA(defenseType)){
				int tnType = 1;
				if(defenseType.equalsIgnoreCase("ea0")){
					tnType = 0;
				} else if(defenseType.equalsIgnoreCase("ea1")){
					tnType = 1;
				} else if(defenseType.equalsIgnoreCase("ea2")){
					tnType = 2;
				} 
				for(int i = 0; i < numBuyers; i++){			
					buyers.get(i).InitialTrustNetwork(tnType);
				}
				for(int i = 0; i < numBuyers; i++){		
					int day = 0;
					buyers.get(i).resetWitness(day);
				}
			}		
		 
	 }
	 
	 public void simulateEnvironment(){
		 
	 }
	 
	 public void evaluateDefenses(){
		 
	 }
	 
	 public void showOutput(){
		 
	 }
	 
	 public void exportToDB(){
		 
	 }
	 
	 public void importFromDB(){
		 
	 }
	 
	 public void getCentralReputation(){
		 
	 }
	 
	 public void displaySuggestions(){
		 
	 }
	 
	 
}
