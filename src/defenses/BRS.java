package defenses;
import java.util.ArrayList;
import java.util.Vector;


import distributions.BetaDistribution;
import distributions.PseudoRandom;
import environment.Environment;

import main.MCC;
import main.Parameter;
import main.Product;
import main.Rating;
import agent.Buyer;
import agent.Seller;

public class BRS extends Defense{
	private double quantile = 0.01;
	private double rep_aBS = 0.5;		
	
	// private ArrayList<Boolean>trustAdvisors = new ArrayList<Boolean>();


	private boolean iterative;

	public Seller chooseSeller(Buyer honestBuyer, Environment ec) {
		this.ecommerce = ec;
		//System.out.println("in chooseseller method");
		//calculate the trust values on target seller		
		ArrayList<Double> trustValues = new ArrayList<Double>();
		ArrayList<Double> mccValues = new ArrayList<Double>();

		if (trustValues.size()==0){
			for(int i=0; i<2; i++){
				trustValues.add(0.0);
				mccValues.add(0.0);
			}
		}
		Seller s = new Seller();
		for (int k = 0; k < 2; k++) {				
			int sid = Parameter.TARGET_DISHONEST_SELLER;
			if (k == 1)sid = Parameter.TARGET_HONEST_SELLER;

			trustValues.set(k,calculateTrust(honestBuyer.getSeller(sid),honestBuyer));
			mccValues.set(k, ecommerce.getMcc().calculateMCC(sid, trustOfAdvisors));

		}
		//update the daily reputation difference
		ecommerce.updateDailyReputationDiff(trustValues);
		ecommerce.getMcc().updateDailyMCC(mccValues,ecommerce.getDay());

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
		 trustOfAdvisors = new ArrayList<Double>(); 
		 if (trustOfAdvisors.size()==0){
			 for(int h=0; h<totalBuyers; h++){
				 trustOfAdvisors.add(0.0);
			 }
		 }
		//double rep_aBS = 0.5;

		ArrayList<Boolean>trustAdvisors = new ArrayList<Boolean>();

		int aid =0; boolean checkTrans = false;
		//find buyers that have transaction with seller
		for (int j = 0; j < totalBuyers; j++) {
			aid = j;
			if (aid == (Parameter.NO_OF_DISHONEST_BUYERS + Parameter.NO_OF_HONEST_BUYERS)) 
				break;
			trustAdvisors.add(aid,true);
			//search through buyer's transactions
			for (int i=0; i<honestBuyer.getTrans().size(); i++){
				//transaction with seller exists
				if (honestBuyer.getTrans().get(i).getSeller().getId()==sid.getId()){
					checkTrans=true;
			}
			}
			if (checkTrans==false){
				trustOfAdvisors.set(aid, 0.5);
				trustAdvisors.set(aid, false);
			}

		}
		//boolean iterative = true;
		do {
			iterative = false;
			//calculate reputation for seller based on all buyers
			calculateReputation1(honestBuyer, sid, trustAdvisors);
			trustAdvisors = calculateReputation2(honestBuyer, sid, trustAdvisors);
		} while(iterative);

		ArrayList<Integer> storedAdvisors = honestBuyer.getAdvisors();
		storedAdvisors.clear(); double bsr0=0; double bsr1=0;
		ArrayList<Double> np_BAforS = new ArrayList<Double>(2);	
		if (np_BAforS.size()==0){
			for(int i=0; i<2; i++){
				np_BAforS.add(i, 0.0);
			}
		}
		for (int n = 0; n < totalBuyers; n++) {
			aid = n;
			if (aid == bid)continue;  //ignore its own rating
			if (trustAdvisors.get(aid)== false)continue; //buyer no transaction with seller

			trustOfAdvisors.set(aid,  1.0);

			for(int i=0; i<honestBuyer.getListOfBuyers().get(aid).getTrans().size(); i++){
				if(honestBuyer.getBuyer(aid).getTrans().get(i).getSeller().getId()==sid.getId()){
					for(int q=0; q <honestBuyer.getBuyer(aid).getTrans().get(i).getRating().getCriteriaRatings().size(); q++){
						if (honestBuyer.getBuyer(aid).getTrans().get(i).getRating().getCriteriaRatings().get(q).getCriteriaRatingValue()==-1){
							//System.out.println("Enters bsr0");
							bsr0 ++;
						}
						if (honestBuyer.getBuyer(aid).getTrans().get(i).getRating().getCriteriaRatings().get(q).getCriteriaRatingValue()==1){
							//System.out.println("Enters bsr1");
							bsr1 ++;
						}
					}
				}
			}

			trustOfAdvisors.set(aid, 1.0);

			storedAdvisors.add(aid);
			honestBuyer.setTrustAdvisor(aid, 1.0);
		}
		np_BAforS.set(0, bsr0);
		np_BAforS.set(1, bsr1);
		honestBuyer.calculateAverageTrusts(sid.getId());  //get the average trust of advisors based on seller

		rep_aBS = (np_BAforS.get(1) + 1.0 * Parameter.m_laplace) / (np_BAforS.get(0) + np_BAforS.get(1) + 2.0 * Parameter.m_laplace);
		//.out.println(rep_aBS);
		return rep_aBS;
	}

	// step 1: calculate the reputation for seller based on all buyers.
	public void calculateReputation1(Buyer b, Seller sid, ArrayList<Boolean> trustAdvisors){
		ArrayList<Double> BS_npSum = new ArrayList<Double>();
		if (BS_npSum.size()==0){
			for(int i=0; i<2; i++){
				BS_npSum.add(i, 0.0);
			}
		}
		double bsr0 =0; double bsr1=0;
		for (int m = 0; m < totalBuyers; m++) {
			int aid = m;
			if (aid == b.getId())continue;  //ignore its own rating
			if (trustAdvisors.get(aid) == false)continue;	//no transaction with seller
			for(int i=0; i<b.getListOfBuyers().get(aid).getTrans().size(); i++){
				if(b.getBuyer(aid).getTrans().get(i).getSeller().getId()==sid.getId()){
					for(int q=0; q <b.getBuyer(aid).getTrans().get(i).getRating().getCriteriaRatings().size(); q++){
						if (b.getBuyer(aid).getTrans().get(i).getRating().getCriteriaRatings().get(q).getCriteriaRatingValue()==-1){
							bsr0 ++;
						}
						if (b.getBuyer(aid).getTrans().get(i).getRating().getCriteriaRatings().get(q).getCriteriaRatingValue()==1){
							bsr1 ++;
						}
					}
				}

			}
			//bsr0 += BS_npSum.get(0);
			//bsr1 += BS_npSum.get(1);

		}
		BS_npSum.set(0, bsr0);
		BS_npSum.set(1, bsr1);
		rep_aBS = (BS_npSum.get(1) + 1.0) / (BS_npSum.get(0) + BS_npSum.get(1) + 2.0);
		//System.out.println("1: " + rep_aBS);
	}

	// step 2: calculate the reputation for seller based on one buyer		
	public ArrayList<Boolean> calculateReputation2(Buyer b, Seller sid, ArrayList<Boolean> trustAdvisors){
		for (int j = 0; j < totalBuyers; j++) {
			int aid = j;
			double bsr0=0; double bsr1=0;
			if (trustAdvisors.get(aid)== false)continue;	

			for(int i=0; i<b.getListOfBuyers().get(aid).getTrans().size(); i++){
				if(b.getBuyer(aid).getTrans().get(i).getSeller().getId()==sid.getId()){
					for(int q=0; q <b.getBuyer(aid).getTrans().get(i).getRating().getCriteriaRatings().size(); q++){
						if (b.getBuyer(aid).getTrans().get(i).getRating().getCriteriaRatings().get(q).getCriteriaRatingValue()==-1){
							bsr0 ++;
						}
						if (b.getBuyer(aid).getTrans().get(i).getRating().getCriteriaRatings().get(q).getCriteriaRatingValue()==1){
							bsr1 ++;
						}
					}
				}

			}

			BetaDistribution BDist_BrefS = new BetaDistribution((double) (bsr1 + 1), (double) (bsr0 + 1));
			double l = BDist_BrefS.getProbabilityOfQuantile(quantile);
			double u = BDist_BrefS.getProbabilityOfQuantile(1 - quantile);
			if (rep_aBS < l || rep_aBS > u) {
				trustAdvisors.set(aid, false);
				iterative = true;
			}
		}
		return trustAdvisors;
	}


}
