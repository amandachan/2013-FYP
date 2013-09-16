package defenses;

import java.util.ArrayList;
import java.util.HashMap;

import weka.core.Instance;
import environment.*;

import main.Product;
import main.Rating;
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
	
	//protected int[][][] BSR;   		// to store the [buyer][seller][binary rating -1, 1]
	protected ArrayList<Double> trustOfAdvisors = new ArrayList<Double>(); 
	// store the trustworthiness of advisors;
	// for statistic features
	protected ArrayList<Double> rtimes = new ArrayList<Double>();
	
	//repuation for seller based on all buyer
	public abstract void calculateReputation1(Buyer buyer1, int sid);
	//reputation for seller based on one buyer
	public abstract void calculateReputation2(Buyer buyer, int sid);
	//public abstract Rating calculateReputation3(int b, int p);
	public abstract double calculateTrust(int seller, Buyer honestBuyer);
	public abstract int chooseSeller(Buyer b);
	
	
	public void seteCommerce(Environment ec){
		ecommerce = ec; 
		
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
		double fairRating = 0; //*****ecommerce.getSellersTrueRating(sVal);
		// add the rating to instances
		inst.setValue(Parameter.m_ratingIdx, fairRating);	
		
		//update the eCommerce information
//*****		ecommerce.getTransactions().add(new Instance(inst));	
//*****		ecommerce.updateArray(inst);
		return fairRating;
	}
	
	//----- to be moved to evaluation metrics in future --------------------------------------------------
	
	public double calculateMCCofAdvisorTrust(int sid) {
		double MCC = 0.0;
		double tp, fn, fp, tn;
		tp = fn = fp = tn = 0;
		for (int j = 0; j < totalSellers; j++) {
			if(j!=sid)continue;
			//	m_trustA[bid] = 0.5; // to avoid compare itself in confusion matrix				
			ArrayList<Integer> cvals = new ArrayList<Integer>();
			cvals = cofusionMatrix();
			tp += cvals.get(0);
			fn += cvals.get(1);
			fp += cvals.get(2);
			tn += cvals.get(3);
			//}
		}
		MCC = (tp * tn - fp * fn)/ Math.sqrt((tp + fp) * (tp + fn) * (tn + fp) * (tn + fn));
		if (Double.isNaN(MCC)) {
			MCC = -1.0;
		}
		return MCC;
	}
	
	private ArrayList<Integer> cofusionMatrix() {
		// true positive, false negative, false positive, true negative,
		ArrayList<Integer> cmVals = new ArrayList<Integer>();
		for(int i=0; i<4; i++){
			cmVals.add(i, 0);
		}
		for (int k = 0; k < totalBuyers; k++) {
			int aid = k;
			int value = 0;
			if (aid >= dhBuyer && aid < totalBuyers) { // ground truth: honest advisors
				if (trustOfAdvisors.get(aid) > 0.5){ // true positive
					value = cmVals.get(0) +1;
					cmVals.set(0, value);
				}
				else if (trustOfAdvisors.get(aid) < 0.5) {// false negative
					value = cmVals.get(1) +1;
					cmVals.set(1, value);
				} 
				else { // ground truth: dishonest advisors
					if (trustOfAdvisors.get(aid) > 0.5){ // false positive
						value = cmVals.get(2) + 1;
						cmVals.set(2, value);
					}
					else if (trustOfAdvisors.get(aid) < 0.5){ // true negative
						value = cmVals.get(3) + 1;
						cmVals.set(3, value);
					}
				}
			}
		}
		return cmVals;
	}
}