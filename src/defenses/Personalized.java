package defenses;

import java.util.ArrayList;
import java.util.Vector;

import distributions.PseudoRandom;

import main.Parameter;

import agent.Buyer;
import agent.Seller;
import environment.Environment;

public class Personalized extends Defense {


	private double gamma = 0.8;
	private double epsilon = 0.25;
	private double inconsistency = 0.1;
	private int timewindows = Parameter.m_timewindows;
	private double numratingBA = 00;
	private ArrayList<Integer> sr = null;

	private double privateTrustAdvisor(Buyer b, Buyer a) {

		int sameRatingH = b.getSameRatingH().get(a.getId());
		int sameRatingDH = b.getSameRatingDH().get(a.getId());

		double priTr_BA = 0.0;
		double neg_BA =sameRatingDH;
		double pos_BA = sameRatingH;
		priTr_BA = (pos_BA + 1.0) / (pos_BA + neg_BA + 2.0);
		numratingBA = pos_BA + neg_BA;

		return priTr_BA;
	}

	private double publicTrustAdvisor(Buyer a){		

		//calculate the public trust for advisor based on buyer
		double pos_BA = 0.0;	//number of consistent ratings
		double neg_BA = 0.0;	//number of all ratings
		int pos =0, neg=0;
		//estimate every sellers average rating		
		for(int i = 0; i < a.getListOfSellers().size(); i++){
			int Sref = a.getListOfSellers().get(i).getId();

			//step 1: calculate the reputation for reference seller by advisor	
			for(int j=0; j<a.getTrans().size(); j++){
				if(a.getTrans().get(j).getSeller().getId() == Sref){
					for(int k=0; k<a.getTrans().get(j).getRating().getCriteriaRatings().size(); k++){
						if (a.getTrans().get(j).getRating().getCriteriaRatings().get(k).getCriteriaRatingValue()==-1){
							neg ++;
						}
						if (a.getTrans().get(j).getRating().getCriteriaRatings().get(k).getCriteriaRatingValue()==1){
							pos ++;
						}					
					}
				}
			}

			double rep_ASref = (pos + 1.0) / (neg + pos + 2.0);

			//step 2: estimate the average rating for seller in current day, from all buyers.	

			int spos = 0, sneg =0;
			for(int j=0; j<a.getListOfBuyers().size(); j++){

				for (int k=0; k<a.getListOfBuyers().get(j).getTrans().size(); k++){
					if(a.getListOfBuyers().get(j).getTrans().get(k).getSeller().getId() == Sref){
						for(int m=0; m<a.getListOfBuyers().get(j).getTrans().get(k).getRating().getCriteriaRatings().size(); m++){

							if (a.getListOfBuyers().get(j).getTrans().get(k).getRating().getCriteriaRatings().get(m).getCriteriaRatingValue()==-1){
								sneg ++;
							}
							else if (a.getListOfBuyers().get(j).getTrans().get(k).getRating().getCriteriaRatings().get(m).getCriteriaRatingValue()==1){
								spos ++;
							}					
						}
					}
				}
			}

			double neg_aBSref = sneg - neg;
			double pos_aBSref = spos - pos;
			double rep_aBSref = (pos_aBSref + 1.0) / (pos_aBSref + neg_aBSref + 2.0);

			//step 3: compare the reputation based on advisor and reputation based on other buyers
			if(Math.abs(rep_ASref - rep_aBSref) < inconsistency){
				pos_BA++;
			}else{
				neg_BA++;
			}
		}
		double pubTr_BA = (pos_BA + 1.0) / (pos_BA + neg_BA + 2.0);
		//		double pubTr_BA = (pos_BA + Parameter.m_laplace) / (pos_BA + neg_BA + 2.0 * Parameter.m_laplace);

		return pubTr_BA;
	}


	private double trustAdvisor(Buyer b, Buyer a){		

		double trustAdvisor = 0.0;	
		//rigorous according to the paper
		double priTr_BA = privateTrustAdvisor(b, a);

		//	double Nmin = 3;
		double Nmin = -(1.0 / (2.0 * epsilon * epsilon)) * Math.log((1.0 - gamma) / 2.0);
		double w = 0;		
		if(numratingBA >= Nmin){
			w = 1.0;
			trustAdvisor = priTr_BA;
		}else{
			w = ((double) (numratingBA)) / (Nmin);
			double pubTr_BA = publicTrustAdvisor(a);
			trustAdvisor = w * priTr_BA + (1 - w) * pubTr_BA;
		}		

		return trustAdvisor;
	}

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
		int bid = honestBuyer.getId();
		 trustOfAdvisors = new ArrayList<Double>(); 
		 if (trustOfAdvisors.size()==0){
			 for(int h=0; h<ecommerce.getBuyerList().size(); h++){
				 trustOfAdvisors.add(0.0);
			 }
		 }
		double pos_BAforS = 0.0;
		double neg_BAforS = 0.0;	
		//int[][][] BSR = ((eCommerceB)m_eCommerce).getBuyerSellerRatingArray();		
		int numBuyers = honestBuyer.getListOfBuyers().size();

		Vector<Integer> stroedAdvisors = honestBuyer.getAdvisors();
		stroedAdvisors.clear(); int neg=0, pos=0;
		for(int i = 0; i < numBuyers; i++){
			int aid = i;
			if(aid == bid){
				trustOfAdvisors.set(aid, 1.0);
			}

			for(int j=0; j<honestBuyer.getListOfBuyers().get(aid).getTrans().size(); j++){
				if(honestBuyer.getListOfBuyers().get(aid).getTrans().get(j).getSeller().getId() == seller.getId()){
					for(int k=0; k<honestBuyer.getListOfBuyers().get(aid).getTrans().get(j).getRating().getCriteriaRatings().size(); k++){
						if(honestBuyer.getListOfBuyers().get(aid).getTrans().get(j).getRating().getCriteriaRatings().get(k).getCriteriaRatingValue() == -1){
							neg++;
						}
						else if (honestBuyer.getListOfBuyers().get(aid).getTrans().get(j).getRating().getCriteriaRatings().get(k).getCriteriaRatingValue()==1){
							pos++;
						}
					}
				}
			}

			if (neg ==0 && pos==0) {
				//no transaction with seller
				trustOfAdvisors.set(aid, 0.5);
				continue; 
			}
			else{
				double trustAdvisor = trustAdvisor( honestBuyer, honestBuyer.getListOfBuyers().get(aid));
				trustOfAdvisors.set(aid, trustAdvisor);
				neg_BAforS += trustAdvisor * neg;
				pos_BAforS += trustAdvisor * pos;
				//set the trust for advisors;
				//set the trust for advisors;
				stroedAdvisors.add(aid);
				honestBuyer.setTrustAdvisor(aid, trustAdvisor);
			}
		}
		honestBuyer.calculateAverageTrusts(seller.getId());  //get the average trust of advisors based on seller		
		double rep_BAforS = (pos_BAforS + 1.0 * Parameter.m_laplace) / (pos_BAforS + neg_BAforS + 2.0 * Parameter.m_laplace);		

		return rep_BAforS;
	}


	protected double calculateReputation(Buyer b, Seller s){		
		double pos_BAforS = 0.0;
		double neg_BAforS = 0.0;	
		int bid = b.getId();
		int sid = s.getId();
		int neg=0, pos=0;
		for (int i = 0; i < b.getListOfBuyers().size(); i++) {
			//System.out.println("m_NumBuyers"+m_NumBuyers);
			int aid = i;
			if (aid == bid)	continue; 
			
			for(int j=0; j<b.getListOfBuyers().get(aid).getTrans().size(); j++){
				if(b.getListOfBuyers().get(aid).getTrans().get(j).getSeller().getId() == s.getId()){
					for(int k=0; k<b.getListOfBuyers().get(aid).getTrans().get(j).getRating().getCriteriaRatings().size(); k++){
						if(b.getListOfBuyers().get(aid).getTrans().get(j).getRating().getCriteriaRatings().get(k).getCriteriaRatingValue() == -1){
							neg++;
						}
						else if (b.getListOfBuyers().get(aid).getTrans().get(j).getRating().getCriteriaRatings().get(k).getCriteriaRatingValue()==1){
							pos++;
						}
					}
				}
			}
			if (neg == 0 && pos == 0) {
				//no transaction with seller
				trustOfAdvisors.set(aid,  0.5);
				continue; 
			}

			double trustAdvisor = trustAdvisor(b, b.getListOfBuyers().get(aid));
			trustOfAdvisors.set(aid, trustAdvisor);
			neg_BAforS += trustAdvisor *neg;
			//System.out.println("neg_BAforS"+ neg_BAforS);
			pos_BAforS += trustAdvisor * pos;
			//System.out.println("pos_BAforS"+ pos_BAforS);
		}
		double rep_BAforS = 0.5;
		if (pos_BAforS + neg_BAforS == 0.0) {
			rep_BAforS = 0.5;
		} else {
			rep_BAforS = pos_BAforS / (pos_BAforS + neg_BAforS);
		}	

		//change to predict reputation with real ratings, code by siweiJiang, 2012-10-08
		//rep_BAforS = getReputationWithRealRatings(bid, sid);

		return rep_BAforS;
	}	

	@Override
	public Seller chooseSeller(Buyer b, Environment ec) {
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

			trustValues.set(k,calculateTrust(b.getSeller(sid),b));
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
		return b.getSeller(sellerid);
	}

}
