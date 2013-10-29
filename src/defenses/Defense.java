package defenses;

import java.util.ArrayList;
import java.util.HashMap;

import weka.core.Instance;
import environment.*;

import main.Product;
import main.Rating;
import main.Transaction;
import agent.Agent;
import agent.Buyer;
import agent.Seller;
import main.Parameter;


public abstract class Defense {
	protected Environment ecommerce = null;
	protected int day;
	protected int dhBuyer = Parameter.NO_OF_DISHONEST_BUYERS;
	protected int hBuyer = Parameter.NO_OF_HONEST_BUYERS;
	protected int dhSeller = Parameter.NO_OF_DISHONEST_SELLERS;
	protected int hSeller = Parameter.NO_OF_HONEST_SELLERS;
	protected String defenseName = null;
	protected int totalBuyers = dhBuyer + hBuyer;
	protected int totalSellers = dhSeller + hSeller;
	protected int m_NumInstances;	
	protected ArrayList<Double> trustOfAdvisors;

	//protected int[][][] BSR;   		// to store the [buyer][seller][binary rating -1, 1]
	// store the trustworthiness of advisors;
	// for statistic features
	protected ArrayList<Double> rtimes = new ArrayList<Double>();

	//repuation for seller based on all buyer
	public abstract void calculateReputation1(Buyer buyer1, Seller sid, ArrayList<Boolean> trustAdvisors);
	//reputation for seller based on one buyer
	public abstract ArrayList<Boolean> calculateReputation2(Buyer buyer, Seller sid, ArrayList<Boolean> trustAdvisors);
	//public abstract Rating calculateReputation3(int b, int p);
	public abstract double calculateTrust(Seller seller, Buyer honestBuyer);
	public abstract Seller chooseSeller(Buyer b, Environment ec);

	protected ArrayList<Integer>cmVals = new ArrayList<Integer>();
	public void seteCommerce(Environment ec){
		ecommerce = ec; 
		for(int i=0; i<4; i++){
			cmVals.add(i, 0);
		}

	}

	//perform the defense model
	public double giveFairRating(Instance inst){
		// step 1: insert rating from honest buyer		
		String bHonestVal = inst.stringValue(Parameter.m_bHonestIdx);
		// find the dishonest buyer in <day>, give unfair rating
		if (bHonestVal == Parameter.agent_dishonest){
			System.out.println("error, must be honest buyer");
		}
		int sVal = (int)(inst.value(Parameter.m_sidIdx));
		double fairRating = ecommerce.getSellersTrueRating(sVal); 
		//System.out.println("HELLO");
		//System.out.println(ecommerce.getSellersTrueRating(sVal));
		// add the rating to instances
		inst.setValue(Parameter.m_ratingIdx, fairRating);	

		//update the eCommerce information
		ecommerce.getM_Transactions().add(new Instance(inst));	
		//ecommerce.(inst);
		return fairRating;
	}

}