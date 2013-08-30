import java.util.ArrayList;


public abstract class Environment {

	private ArrayList<Buyer> buyerList;
	private ArrayList<Seller> sellerList;
	private ArrayList<Transaction> transactionList;
	
	abstract void importConfigSettings();
	abstract void createEnvironment();
	abstract void saveWekaInstances();
	
}
