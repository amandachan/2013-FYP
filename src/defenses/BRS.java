package defenses;
import java.util.ArrayList;
import java.util.Vector;


import distributions.BetaDistribution;
import distributions.PseudoRandom;

import main.Parameter;
import main.Product;
import main.Rating;
import agent.Buyer;
import agent.Seller;

public class BRS extends Defense{
	private double quantile = 0.01;
	private ArrayList<Boolean>trustAdvisors = new ArrayList<Boolean>();
	private double rep_aBS = 0.5;		

	private boolean iterative;

	public void performDefense(int day, Buyer honestBuyer) {
		this.day = day;	
		int bid = honestBuyer.getId();
		//calculate the trust values on target seller			
		ArrayList<Double> trustValues = new ArrayList<Double>();
		ArrayList<Double> mccValues = new ArrayList<Double>();
		for (int k = 0; k < 2; k++) {				
			int sid = Parameter.TARGET_DISHONEST_SELLER;
			if (k == 1)sid = Parameter.TARGET_HONEST_SELLER;
			trustValues.add(calculateTrust(sid,honestBuyer));
			mccValues.add(calculateMCCofAdvisorTrust(sid));

		}
		//update the daily reputation difference

		//select seller with the maximum trust values from the two target sellers
		int sellerid = Parameter.TARGET_DISHONEST_SELLER;
		if(trustValues.get(0) < trustValues.get(1)){
			sellerid = Parameter.TARGET_HONEST_SELLER;
		} else if (trustValues.get(0) == trustValues.get(1)){
			sellerid = (PseudoRandom.randDouble() < 0.5)?Parameter.TARGET_DISHONEST_SELLER:Parameter.TARGET_HONEST_SELLER;
		}
		if(PseudoRandom.randDouble() > Parameter.m_honestBuyerOntargetSellerRatio){ 
			sellerid = 1 + (int)(PseudoRandom.randDouble() * (Parameter.TARGET_DISHONEST_SELLER + Parameter.TARGET_HONEST_SELLER - 2));
		}			

		//get all the attribute values;
		int dayValue = day + 1;
		int buyerValue = bid;
		String bHonestVal = Parameter.agent_honest;  
		int sellerValue = sellerid;
		//instances here...
	}

	public double calculateTrust(int sid, Buyer honestBuyer) {
		trustOfAdvisors = new ArrayList<Double>(); 
		int bid = honestBuyer.getId();
		//int[][][] BSR = ((eCommerceB)m_eCommerce).getBuyerSellerRatingArray();
		//int numBuyers = m_eCommerce.getNumberBuyers();

		// find buyers that have transactions with seller (advisors)
		buyerSellerTrans(sid);

		iterative = true;
		do{
			//calculate reputation for seller based on all buyers
			Rating r1 = new Rating();
			r1 = calculateReputation_SnAllB(bid, sid);

			// calculate reputation of seller based on one buyer
			Rating r2 = new Rating();
			r2 = calculateReputation_SnB(bid, sid);

		}while (iterative);

		ArrayList<Double> BAforS = new ArrayList<Double>();	
		for (int j = 0; j < totalBuyers; j++) {
			int aid = j;
			if (aid == bid)continue;  //ignore its own rating
			if (trustAdvisors.get(aid) == false)continue; //buyer no transaction with seller
			trustOfAdvisors.set(aid, 1.0);
			double ba0 = BAforS.get(0) + BSR[aid][sid][0];
			double ba1 = BAforS.get(1) + BSR[aid][sid][1];			
			//consider the trust of advisors to two target sellers in duopoly e-marketplaces
			//stroedAdvisors.add(aid);
			//honestBuyer.setTrustAdvisor(aid, 1.0);
		}
		//honestBuyer.calculateAverageTrusts(sid);  //get the average trust of advisors based on seller
		rep_aBS = (BAforS.get(1) + 1.0 * Parameter.m_laplace) / (BAforS.get(0) + BAforS.get(1) + 2.0 * Parameter.m_laplace);

		return rep_aBS;
	}

	//calculate reputation for seller based on one buyer
	public Rating calculateReputation_SnB(int bid, int sid){
		for (int j = 0; j < totalBuyers; j++) {
			int aid = j;
			if (trustAdvisors.get(aid)==false)continue;				
			BetaDistribution BDist_BrefS = new BetaDistribution((double) (BSR[aid][sid][1] + 1), (double) (BSR[aid][sid][0] + 1));
			double l = BDist_BrefS.getProbabilityOfQuantile(quantile);
			double u = BDist_BrefS.getProbabilityOfQuantile(1 - quantile);
			if (rep_aBS < l || rep_aBS > u) {
				// remove this buyer from the honest list
				trustAdvisors.set(aid, false);
				//since a buyer is removed from the list, reputation is calculated again (do while loop)
				iterative = true;
			}

		}
		Rating r = new Rating();
		r.create(sid, bid);
		return r;
	}

	//calculate reputation for seller based on all buyers
	public Rating calculateReputation_SnAllB(int bid, int sid) {
		ArrayList<Integer> BS_Sum = new ArrayList<Integer>();
		for (int j = 0; j < totalBuyers; j++) {
			int aid = j;
			if (aid == bid)continue;  //ignore its own rating
			if (trustAdvisors.get(aid) == false)continue;	//no transaction with seller	
			int sum0 = BS_Sum.get(0) + BSR[aid][sid][0];
			int sum1 = BS_Sum.get(1) + BSR[aid][sid][0];
			BS_Sum.set(0, sum0);
			BS_Sum.set(1, sum1);
		}
		rep_aBS = (BS_Sum.get(1) + 1.0) / (BS_Sum.get(0) + BS_Sum.get(1) + 2.0);		
		Rating r = new Rating();
		r.create(sid, bid);
		return r;
	}

	//find buyers that have transactions with seller
	public void buyerSellerTrans(int sid){
		for (int j = 0; j < totalBuyers; j++) {
			int aid = j;
			if (aid == (Parameter.NO_OF_DISHONEST_BUYERS + Parameter.NO_OF_HONEST_BUYERS)) 
				break;
			trustAdvisors.set(aid, true);
			if(BSR[aid][sid][0] == 0 && BSR[aid][sid][1] == 0){
				//no transaction with seller
				trustOfAdvisors.set(aid, 0.5);
				trustAdvisors.set(aid, false);			}
		}
	}


	private ArrayList<Integer> cofusionMatrix() {

		// true positive, false negative, false positive, true negative,
		ArrayList<Integer> cmVals = new ArrayList<Integer>();

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

}
