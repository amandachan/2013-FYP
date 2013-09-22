package defenses;
import java.util.ArrayList;
import java.util.Vector;


import distributions.BetaDistribution;
import distributions.PseudoRandom;
import environment.Environment;

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

	public Seller chooseSeller(Buyer honestBuyer, Environment ec) {
		this.ecommerce = ec;
		//System.out.println("in chooseseller method");
		//calculate the trust values on target seller		
		ArrayList<Double> trustValues = new ArrayList<Double>();
		ArrayList<Double> mccValues = new ArrayList<Double>();
		Seller s = new Seller();
		for (int k = 0; k < 2; k++) {				
			int sid = Parameter.TARGET_DISHONEST_SELLER;
			if (k == 1)sid = Parameter.TARGET_HONEST_SELLER;

			trustValues.add(k,calculateTrust(honestBuyer.getSeller(sid),honestBuyer));


			mccValues.add(k,calculateMCCofAdvisorTrust(sid));


		}
		//update the daily reputation difference

		ecommerce.updateDailyReputationDiff(trustValues);
		ecommerce.updateDailyMCC(mccValues);

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
		return honestBuyer.getSeller(sellerid);
	}

	public double calculateTrust(Seller sid, Buyer honestBuyer){
		int bid = honestBuyer.getId();
		double rep_aBS = 0.5;


		int aid =0;
		//find buyers that have transaction with seller
		for (int j = 0; j < totalBuyers; j++) {
			aid = j;
			if (aid == (Parameter.NO_OF_DISHONEST_BUYERS + Parameter.NO_OF_HONEST_BUYERS)) 
				break;
			trustAdvisors.add(true);
			//search through buyer's transactions
			for (int i=0; i<honestBuyer.getTrans().size(); i++){
				boolean checkTrans= false;
				//transaction with seller exists
				if (honestBuyer.getTrans().get(i).getSeller().getId()==sid.getId()){
					checkTrans=true;
				}
				if (checkTrans==false){
					trustOfAdvisors.set(aid, 0.5);
					trustAdvisors.add(aid, false);
				}
			}

		}
		//boolean iterative = true;
		do {
			iterative = false;
			//calculate reputation for seller based on all buyers
			calculateReputation1(honestBuyer, sid);
			calculateReputation2(honestBuyer, sid);
		} while(iterative);

		ArrayList<Integer> storedAdvisors = honestBuyer.getAdvisors();
		storedAdvisors.clear(); double bsr0=0; double bsr1=0;
		ArrayList<Double> np_BAforS = new ArrayList<Double>(2);	
		for(int i=0; i<2; i++){
			np_BAforS.add(i, 0.0);
		}
		for (int n = 0; n < totalBuyers; n++) {
			aid = n;
			if (aid == bid)continue;  //ignore its own rating
			if (trustAdvisors.get(aid)== false)continue; //buyer no transaction with seller

			trustOfAdvisors.set(aid,  1.0);

			for(int f=0; f<honestBuyer.getBuyer(aid).getTrans().size(); f++){
				//System.out.println("BSR0 " );

				if(honestBuyer.getBuyer(aid).getTrans().get(f).getSeller().getId()==sid.getId() && f==0){
					bsr0 = np_BAforS.get(0) + honestBuyer.getBuyer(aid).getTrans().get(0).getRating().getCriteriaRatings().get(0).getCriteriaRatingValue();
				}
				if(honestBuyer.getBuyer(aid).getTrans().get(f).getSeller().getId()==sid.getId() && f==1){
					bsr1 = np_BAforS.get(1) + honestBuyer.getBuyer(aid).getTrans().get(1).getRating().getCriteriaRatings().get(0).getCriteriaRatingValue();
				}
			}

			np_BAforS.set(0, bsr0);
			np_BAforS.set(1, bsr1);

			//System.out.println(np_BAforS.get(0) );
			//consider the trust of advisors to two target sellers in duopoly e-marketplaces
			storedAdvisors.add(aid);
			honestBuyer.setTrustAdvisor(aid, 1.0);
		}
		honestBuyer.calculateAverageTrusts(sid.getId());  //get the average trust of advisors based on seller

		rep_aBS = (np_BAforS.get(1) + 1.0 * Parameter.m_laplace) / (np_BAforS.get(0) + np_BAforS.get(1) + 2.0 * Parameter.m_laplace);
		return rep_aBS;
	}

	// step 1: calculate the reputation for seller based on all buyers.
	public void calculateReputation1(Buyer b, Seller sid){
		ArrayList<Double> BS_npSum = new ArrayList<Double>();
		for(int i=0; i<2; i++){
			BS_npSum.add(i, 0.0);
		}
		double bsr0 =0; double bsr1=0;
		for (int m = 0; m < totalBuyers; m++) {
			int aid = m;
			if (aid == b.getId())continue;  //ignore its own rating
			if (trustAdvisors.get(aid) == false)continue;	//no transaction with seller
			for(int i=0; i<b.getListOfBuyers().get(aid).getTrans().size(); i++){
				if(b.getBuyer(aid).getTrans().get(i).getSeller().getId()==sid.getId() && i==0){
					bsr0 = BS_npSum.get(0) + b.getBuyer(aid).getTrans().get(0).getRating().getCriteriaRatings().get(0).getCriteriaRatingValue();
				}
				if(b.getBuyer(aid).getTrans().get(i).getSeller().getId()==sid.getId() && i==1){
					bsr1 = BS_npSum.get(1) + b.getBuyer(aid).getTrans().get(1).getRating().getCriteriaRatings().get(0).getCriteriaRatingValue();
				}
			}
			BS_npSum.add(0, bsr0);
			BS_npSum.add(1, bsr1);
			//BS_npSum[0] += BSR[aid][sid][0];
			//BS_npSum[1] += BSR[aid][sid][1];
			//System.out.println("HH");
		}
		rep_aBS = (BS_npSum.get(1) + 1.0) / (BS_npSum.get(0) + BS_npSum.get(1) + 2.0);
	}

	// step 2: calculate the reputation for seller based on one buyer		
	public void calculateReputation2(Buyer b, Seller sid){
		for (int j = 0; j < totalBuyers; j++) {
			int aid = j;
			double bsr0=0; double bsr1=0;
			if (trustAdvisors.get(aid)== false)continue;	

			for(int i=0; i<b.getBuyer(aid).getTrans().size(); i++){
				if(b.getBuyer(aid).getTrans().get(i).getSeller().getId()==sid.getId() && i==0){
					bsr0 = b.getBuyer(aid).getTrans().get(0).getRating().getCriteriaRatings().get(0).getCriteriaRatingValue();
				}
				if(b.getBuyer(aid).getTrans().get(i).getSeller().getId()==sid.getId() && i==1){
					bsr1 =b.getBuyer(aid).getTrans().get(1).getRating().getCriteriaRatings().get(0).getCriteriaRatingValue();
				}
			}

			BetaDistribution BDist_BrefS = new BetaDistribution((double) (bsr1 + 1), (double) (bsr0 + 1));
			double l = BDist_BrefS.getProbabilityOfQuantile(quantile);
			double u = BDist_BrefS.getProbabilityOfQuantile(1 - quantile);
			if (rep_aBS < l || rep_aBS > u) {
				// remove this buyer from the honest list
				trustAdvisors.set(aid, false);
				//since a buyer is removed from the list, reputation is calculated again (do while loop)
				iterative = true;
			}
		}
	}


}
