package defenses;

import java.util.ArrayList;
import java.util.HashMap;

import main.Product;
import main.Rating;
import agent.Agent;
import agent.Buyer;
import agent.Seller;
import main.Parameter;


public abstract class Defense {
	
	protected int day;
	protected int dhBuyer = Parameter.NO_OF_DISHONEST_BUYERS;
	protected int hBuyer = Parameter.NO_OF_HONEST_BUYERS;
	protected int dhSeller = Parameter.NO_OF_DISHONEST_SELLERS;
	protected int hSeller = Parameter.NO_OF_HONEST_SELLERS;
	protected String defenseName = null;
	protected int totalBuyers = dhBuyer + hBuyer;
	protected int totalSellers = dhSeller + hSeller;
//	protected int m_NumInstances;	
	
	protected int[][][] BSR;   		// to store the [buyer][seller][binary rating -1, 1]
	protected ArrayList<Double> trustOfAdvisors; 		// store the trustworthiness of advisors;
	// for statistic features
	protected ArrayList<Double> rtimes;
	
	
	//repuation for seller based on one buyer
	public abstract Rating calculateReputation_SnB(int bid, int sid);
	//reputation for seller based on all buyers
	public abstract Rating calculateReputation_SnAllB(int bid, int sid);
	//public abstract Rating calculateReputation3(int b, int p);
	public abstract void performDefense(int day, Buyer honestBuyer);
	public abstract double calculateTrust(int sellerid, Buyer honestBuyer);
}