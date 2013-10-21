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

	//----- to be moved to evaluation metrics in future --------------------------------------------------



/*	private int[] cofusionMatrix() {

		// true positive, false negative, false positive, true negative,
		int[] cmVals = new int[4];

		for (int k = 0; k < totalBuyers; k++) {
			int aid = k;
			if (aid >= Parameter.NO_OF_DISHONEST_BUYERS && aid < totalBuyers) { // ground truth: honest advisors


				if (trustOfAdvisors.get(aid) > 0.5) // true positive
					cmVals[0]++;
				else if (trustOfAdvisors.get(aid)< 0.5) // false negative
					cmVals[1]++;
			} else { // ground truth: dishonest advisors
				if (trustOfAdvisors.get(aid) > 0.5) // false positive
					cmVals[2]++;
				else if (trustOfAdvisors.get(aid) < 0.5) // true negative
					cmVals[3]++;
			}
		}

		return cmVals;
	}*/

	/*public double calculateMCCofAdvisorTrust(int sid) {

		double MCC = 0.0;
		double tp, fn, fp, tn;
		tp = fn = fp = tn = 0;
		//System.out.println("m_NumDB:"+m_NumDB);
		//System.out.println("m_NumBuyers:"+m_NumBuyers);
		//for (int i = m_NumDB; i < m_NumBuyers;i++) {
		//System.out.println("iNSIDE");
		//int bid = i;

		for (int j = 0; j < totalSellers; j++) {
			if(j!=sid)continue;
			//	m_trustA[bid] = 0.5; // to avoid compare itself in confusion matrix				
			int[] cvals = cofusionMatrix();
			//				System.out.println("(bid " + bid + ", sid " + sid + ") = " + cvals[0] + ", " + cvals[1] + ", " + cvals[2] + ", " + cvals[3]);
			tp += cvals[0];
			fn += cvals[1];
			fp += cvals[2];
			tn += cvals[3];
			//}
		}

		MCC = (tp * tn - fp * fn)
				/ Math.sqrt((tp + fp) * (tp + fn) * (tn + fp) * (tn + fn));
		//System.out.println("\ntp="+tp+"\ttn="+tn+"\tfp="+fp+"\tfn="+fn+"\tmcc="+MCC);
		if (Double.isNaN(MCC)) {
			MCC = -1.0;
		}
		//System.out.println("MCC:"+MCC);
		return MCC;
	}*/
}