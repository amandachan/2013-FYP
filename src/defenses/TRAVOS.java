package defenses;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import weka.core.Instance;
import weka.core.Instances;

import main.Parameter;

import distributions.BetaDistribution;
import distributions.PseudoRandom;

import agent.Buyer;
import agent.Seller;
import environment.Environment;


public class TRAVOS extends Defense{

	private static int numBins = 3;
	//private int [][] BScurrRating = null;

	@Override
	public void calculateReputation1(Buyer buyer1, Seller sid,
			ArrayList<Boolean> trustAdvisors) {
		// TODO Auto-generated method stub

	}

	@Override
	public ArrayList<Boolean> calculateReputation2(Buyer buyer, Seller sid,
			ArrayList<Boolean> trustAdvisors) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double calculateTrust(Seller seller, Buyer honestBuyer) {
		//readInstances();
		int sid = seller.getId();
		int bid = honestBuyer.getId();
		Instances transactions = ecommerce.getM_Transactions();
		//get the positive/negative rating for pairs of buyer and seller
		//int[][][] BSR = ((eCommerceB)m_eCommerce).getBuyerSellerRatingArray();
		double pos_BAforS = 0;
		double neg_BAforS = 0;	

		//step 1: positive and negative form the buyer
		 trustOfAdvisors = new ArrayList<Double>(); 
		 if (trustOfAdvisors.size()==0){
			 for(int h=0; h<ecommerce.getBuyerList().size(); h++){
				 trustOfAdvisors.add(0.0);
			 }
		 }
		Vector<Integer> stroedAdvisors = honestBuyer.getAdvisors();
		stroedAdvisors.clear();
		//step 2: add all the advisors's adjusted positive and negative rating
		int numBuyers =honestBuyer.getListOfBuyers().size();
		double mu_uniform = 0.5;  	int pos=0, neg=0;												//expectation of the uniform distribution
		double sigma_uniform = Math.sqrt(1.0 / 12);									//Variance of the uniform distribution		
		for(int i = 0; i < numBuyers; i++){
			int aid = i;			
			if(aid == bid)continue;			

			//calculate the reputation and variance of seller based on advisor

			for(int i1=0; i1<honestBuyer.getListOfBuyers().get(aid).getTrans().size(); i1++){
				if(honestBuyer.getBuyer(aid).getTrans().get(i1).getSeller().getId()==sid){
					for(int q=0; q <honestBuyer.getBuyer(aid).getTrans().get(i1).getRating().getCriteriaRatings().size(); q++){
						if (honestBuyer.getBuyer(aid).getTrans().get(i1).getRating().getCriteriaRatings().get(q).getCriteriaRatingValue()==-1){
							//System.out.println("Enters bsr0");
							neg ++;
						}
						if (honestBuyer.getBuyer(aid).getTrans().get(i1).getRating().getCriteriaRatings().get(q).getCriteriaRatingValue()==1){
							//System.out.println("Enters bsr1");
							pos ++;
						}
					}
				}
			}

			if(pos == 0 && neg == 0) 	{
				trustOfAdvisors.set(aid, 0.5);
				continue;
			}
			double mu_AS = (pos + 1.0) / (pos + neg + 2.0);
			double sigma_AS = Math.sqrt( (pos + 1) * (neg + 1) / ((pos + neg + 2) * (pos + neg + 2) * (pos + neg + 3) ) );
			Buyer advisor = honestBuyer.getListOfBuyers().get(aid);
			//adjust the alpha and beta values	
			double rho = relationshipBA_S( honestBuyer,  advisor,  seller);	
			trustOfAdvisors.set(aid, rho);
			double mu_adj = (1 - rho) * mu_uniform  + rho * mu_AS;
			double sigma_adj = (1 - rho) * sigma_uniform  + rho * sigma_AS;
			double tmp = (mu_adj * (1 - mu_adj)) / (sigma_adj * sigma_adj) - 1.0;
			double pos_AS_adj =  mu_adj * tmp - 1.0;
			double neg_AS_adj =  (1.0 - mu_adj) * tmp - 1.0;

			//add the adjust positive and negative values into the buyers.
			pos_BAforS += pos_AS_adj;
			neg_BAforS += neg_AS_adj;

			//set the trust for advisors;
			stroedAdvisors.add(aid);
			honestBuyer.setTrustAdvisor(aid, rho);
		}
		honestBuyer.calculateAverageTrusts(sid);  //get the average trust of advisors based on seller

		double rep_BAforS = (pos_BAforS + 1.0 * Parameter.m_laplace) / (pos_BAforS + neg_BAforS + 2.0 * Parameter.m_laplace);
		return rep_BAforS;
	}

	@Override
	public Seller chooseSeller(Buyer honestBuyer, Environment ec) {
		this.ecommerce = ec;
		//System.out.println("in chooseseller method");
		//calculate the trust values on target seller		
		ArrayList<Double> trustValues = new ArrayList<Double>();
		ArrayList<Double> mccValues = new ArrayList<Double>();
		ArrayList<Double> FNRValues = new ArrayList<Double>();
		ArrayList<Double> FPRValues = new ArrayList<Double>();
		ArrayList<Double> accValues = new ArrayList<Double>();
		ArrayList<Double> precValues = new ArrayList<Double>();
		ArrayList<Double> fValues = new ArrayList<Double>();
		ArrayList<Double> TPRValues = new ArrayList<Double>();


		if (trustValues.size()==0){
			for(int i=0; i<2; i++){
				trustValues.add(0.0);
				mccValues.add(0.0);
				FNRValues.add(0.0);
				accValues.add(0.0);
				FPRValues.add(0.0);
				precValues.add(0.0);
				fValues.add(0.0);
				TPRValues.add(0.0);

			}
		}
		Seller s = new Seller();
		for (int k = 0; k < 2; k++) {				
			int sid = Parameter.TARGET_DISHONEST_SELLER;
			if (k == 1)sid = Parameter.TARGET_HONEST_SELLER;

			trustValues.set(k,calculateTrust(honestBuyer.getSeller(sid),honestBuyer));
			mccValues.set(k, ecommerce.getMcc().calculateMCC(sid, trustOfAdvisors));
			FNRValues.set(k, ecommerce.getMcc().calculateFNR(sid, trustOfAdvisors));
			accValues.set(k, ecommerce.getMcc().calculateAccuracy(sid, trustOfAdvisors));
			FPRValues.set(k, ecommerce.getMcc().calculateFPR(sid, trustOfAdvisors));
			precValues.set(k, ecommerce.getMcc().calculatePrecision(sid, trustOfAdvisors));
			fValues.set(k, ecommerce.getMcc().calculateF(sid, trustOfAdvisors));
			TPRValues.set(k, ecommerce.getMcc().calculateTPR(sid, trustOfAdvisors));



		}
		//update the daily reputation difference
		ecommerce.updateDailyReputationDiff(trustValues);
		ecommerce.getMcc().updateDailyMCC(mccValues,ecommerce.getDay());
		ecommerce.getMcc().updateDailyFNR(FNRValues,ecommerce.getDay());
		ecommerce.getMcc().updateDailyAcc(accValues,ecommerce.getDay());
		ecommerce.getMcc().updateDailyFPR(FPRValues,ecommerce.getDay());
		ecommerce.getMcc().updateDailyPrec(precValues,ecommerce.getDay());
		ecommerce.getMcc().updateDailyF(fValues,ecommerce.getDay());

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


	private double relationshipBA_S(Buyer b, Buyer advisor, Seller s) {
		int bid = b.getId();
		int sid = s.getId();
		int aid = advisor.getId();
		//get the positive/negative rating for pairs of buyer and seller

		// step 1: find the reputation bin for advisor to seller
		double neg=0;
		double pos=0;

		for(int i=0; i<advisor.getTrans().size(); i++){
			if(advisor.getTrans().get(i).getSeller().getId()==sid){
				for(int q=0; q <advisor.getTrans().get(i).getRating().getCriteriaRatings().size(); q++){
					if (advisor.getTrans().get(i).getRating().getCriteriaRatings().get(q).getCriteriaRatingValue()==-1){
						//System.out.println("Enters bsr0");
						neg ++;
					}
					if (advisor.getTrans().get(i).getRating().getCriteriaRatings().get(q).getCriteriaRatingValue()==1){
						//System.out.println("Enters bsr1");
						pos ++;
					}
				}
			}
		}

		double rep = ((double) (pos + 1))	/ ((double) (pos + neg + 2));
		// find the bin that the expected reputation falls into
		double lb = 0;
		double ub = 0;
		for (int i = 0; i < numBins; i++) {
			lb = ((double) (i)) / ((double) (numBins));
			ub = ((double) (i + 1)) / ((double) (numBins));
			if (rep >= lb  && rep < ub)
				break;
		}

		// step 2: find the relationship between the seller and advisor, (1. reputation \in [bin of rep_AS], 2. number of transactions)
		// go through the sellers that ever rated by the advisor calculate the expected reputation of those sellers and
		// find out the ones that fall between lb and ub		
		int neg_BAforSref = 0;
		int pos_BAforSref = 0;
		// sellers in the rating_A
		HashMap<Seller, Integer> numOfRatings_pos = new HashMap<Seller, Integer>();
		HashMap<Seller, Integer> numOfRatings_neg = new HashMap<Seller, Integer>();

		for(int i=0; i<s.getListOfSellers().size(); i++){
			int possum=0, negsum=0;
			for(int j=0; j<advisor.getTrans().size(); j++){
				if(advisor.getTrans().get(j).getSeller() == s.getListOfSellers().get(i)){
					for(int q=0; q <advisor.getBuyer(aid).getTrans().get(j).getRating().getCriteriaRatings().size(); q++){
						if (advisor.getTrans().get(j).getRating().getCriteriaRatings().get(q).getCriteriaRatingValue()==-1){
							//System.out.println("Enters bsr0");
							negsum ++;
						}
						if (advisor.getTrans().get(j).getRating().getCriteriaRatings().get(q).getCriteriaRatingValue()==1){
							//System.out.println("Enters bsr1");
							possum ++;
						}
					}			
				}
			}
			numOfRatings_pos.put(s.getSeller(i), possum);
			numOfRatings_neg.put(s.getSeller(i), negsum);
		}
		double currentRating = 0; int pos1=0, neg1=0;
		for(int i=0; i<numOfRatings_pos.size(); i++){
			int Sref = i;
			//only consider the reference seller; ignore the null rating for reference seller based on advisor
			if(Sref == sid) continue;
			//the recently rating for reference seller based on buyer
			for(int i1=0; i1<b.getTrans().size(); i1++){
				if(b.getTrans().get(i1).getSeller().getId()==Sref){
					for(int q=0; q <b.getTrans().get(i1).getRating().getCriteriaRatings().size(); q++){
						currentRating = b.getTrans().get(i1).getRating().getCriteriaRatings().get(q).getCriteriaRatingValue();
					}

				}
			}

			if(currentRating == Parameter.RATING_BINARY[1])
				continue;			

			for(int i1=0; i1<b.getListOfBuyers().get(aid).getTrans().size(); i1++){
				if(b.getBuyer(aid).getTrans().get(i1).getSeller().getId()==Sref){
					for(int q=0; q <b.getBuyer(aid).getTrans().get(i1).getRating().getCriteriaRatings().size(); q++){
						if (b.getBuyer(aid).getTrans().get(i1).getRating().getCriteriaRatings().get(q).getCriteriaRatingValue()==-1){
							//System.out.println("Enters bsr0");
							neg1 ++;
						}
						if (b.getBuyer(aid).getTrans().get(i1).getRating().getCriteriaRatings().get(q).getCriteriaRatingValue()==1){
							//System.out.println("Enters bsr1");
							pos1 ++;
						}
					}
				}
			}


			double rep_ASref = ((double) (pos1 + 1)) / ((double) (pos1 + neg1 + 2));
			// if the reputation (advisor to the reference seller)is between the bin
			//1. reputation \in [bin of rep_AS]
			if (rep_ASref >= lb && rep_ASref <= ub) {				 
				// 2. the rating from the buyer's view
				if (currentRating == Parameter.RATING_BINARY[0]) {
					neg_BAforSref++;
				} else if (currentRating == Parameter.RATING_BINARY[2]) { 
					pos_BAforSref++;
				}
			}
		}


		for(int i=0; i<numOfRatings_neg.size(); i++){
			int Sref = i;
			//only consider the reference seller; ignore the null rating for reference seller based on advisor
			if(Sref == sid) continue;
			//the recently rating for reference seller based on buyer
			for(int i1=0; i1<b.getTrans().size(); i1++){
				if(b.getTrans().get(i1).getSeller().getId()==Sref){
					for(int q=0; q <b.getTrans().get(i1).getRating().getCriteriaRatings().size(); q++){
						currentRating = b.getTrans().get(i1).getRating().getCriteriaRatings().get(q).getCriteriaRatingValue();
					}

				}
			}

			if(currentRating == Parameter.RATING_BINARY[1])
				continue;			

			for(int i1=0; i1<b.getListOfBuyers().get(aid).getTrans().size(); i1++){
				if(b.getBuyer(aid).getTrans().get(i1).getSeller().getId()==Sref){
					for(int q=0; q <b.getBuyer(aid).getTrans().get(i1).getRating().getCriteriaRatings().size(); q++){
						if (b.getBuyer(aid).getTrans().get(i1).getRating().getCriteriaRatings().get(q).getCriteriaRatingValue()==-1){
							//System.out.println("Enters bsr0");
							neg1 ++;
						}
						if (b.getBuyer(aid).getTrans().get(i1).getRating().getCriteriaRatings().get(q).getCriteriaRatingValue()==1){
							//System.out.println("Enters bsr1");
							pos1 ++;
						}
					}
				}
			}


			double rep_ASref = ((double) (pos1 + 1)) / ((double) (pos1 + neg1 + 2));
			// if the reputation (advisor to the reference seller)is between the bin
			//1. reputation \in [bin of rep_AS]
			if (rep_ASref >= lb && rep_ASref <= ub) {				 
				// 2. the rating from the buyer's view
				if (currentRating == Parameter.RATING_BINARY[0]) {
					neg_BAforSref++;
				} else if (currentRating == Parameter.RATING_BINARY[2]) { 
					pos_BAforSref++;
				}
			}
		}


		BetaDistribution distBA_S = new BetaDistribution(pos_BAforSref + 1, neg_BAforSref + 1);
		double relationshipBA_S = distBA_S.CDF(ub) - distBA_S.CDF(lb);

		return relationshipBA_S;
	}


	public void readInstances(){

	}

}
