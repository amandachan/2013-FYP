/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author NEEL
 */
package defenses;

import java.util.*;

import agent.*;
import weka.core.Instance;
import weka.core.Instances;
import distributions.PseudoRandom;
import environment.*;
import main.*;

//just the mean of reputation, there is no iterative procedure comaring with BRS
public class NoDefense extends Defense{

    public NoDefense(){
    }

    public double calculateTrust(Seller sellerid,Buyer honestBuyer){

        double neg_BS=0,pos_BS=0;
        int bid = honestBuyer.getId();
       // int[][][] BSR = ecommerce.getM_SellersTrueRating();//.getBuyerSellerRatingArray();
       // int[][] SR = ((eCommerceB)m_eCommerce).getSellerRatingArray();
        double SR=ecommerce.getSellersTrueRating(sellerid.getId());
        Transaction buyerTrans=honestBuyer.getTrans().get(bid);
       //double tr= buyerTrans.getRating().getCriteriaRatings().get(0).getCriteriaRatingValue();
        if(sellerid.isIshonest()==true){
        neg_BS = SR - buyerTrans.getRating().getCriteriaRatings().get(0).getCriteriaRatingValue(); //neg_BS = SR[sid][0] - BSR[bid][sid][0];
        }
        else{
        pos_BS =SR - buyerTrans.getRating().getCriteriaRatings().get(1).getCriteriaRatingValue(); //pos_BS = SR[sid][1] - BSR[bid][sid][1];
        }
        double rep_aBS = (pos_BS + 1.0 * Parameter.m_laplace) / (neg_BS + pos_BS + 2.0 * Parameter.m_laplace);

        return rep_aBS;
    }

    public Seller chooseSeller(Buyer honestBuyer, Environment ec){
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


		//	mccValues.add(k,calculateMCCofAdvisorTrust(sid));


		}
		//update the daily reputation difference

		ecommerce.updateDailyReputationDiff(trustValues);
		//ecommerce.updateDailyMCC(mccValues);

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

public void calculateReputation1(Buyer buyer, Seller sid){}

public  void calculateReputation2(Buyer buyer, Seller sid){}

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

}//class