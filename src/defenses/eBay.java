package defenses;

import java.util.ArrayList;
import java.util.HashMap;

import main.Parameter;
import distributions.PseudoRandom;

import environment.Environment;

import agent.Buyer;
import agent.Seller;

public class eBay extends Defense{

	@Override
	public void calculateReputation1(Buyer buyer1, Seller sid,
			ArrayList<Boolean> trustAdvisors) {		
	}

	@Override
	public ArrayList<Boolean> calculateReputation2(Buyer buyer, Seller sid,
			ArrayList<Boolean> trustAdvisors) {
		return null;
	}

	public double calculateTrust(Seller seller, Buyer honestBuyer) {
		double positive = ecommerce.getPositiveRatings().get(seller);
		double negative = ecommerce.getNegativeRatings().get(seller);
		double trust = positive - negative;
		return trust;
	}

	public Seller chooseSeller(Buyer b, Environment ec) {
		this.ecommerce = ec;	
		ArrayList<Double> trustValues = new ArrayList<Double>();
		ArrayList<Double> mccValues = new ArrayList<Double>();

		if (trustValues.size()==0){
			for(int i=0; i<2; i++){
				trustValues.add(0.0);
				mccValues.add(0.0);
			}
		}
		
		for (int k = 0; k < 2; k++) {				
			int sid = Parameter.TARGET_DISHONEST_SELLER;
			if (k == 1)sid = Parameter.TARGET_HONEST_SELLER;			
			trustValues.set(k,calculateTrust(b.getSeller(sid),b));
		//	mccValues.set(k, ecommerce.getMcc().calculateMCC(sid, trustOfAdvisors));

		}
		ecommerce.updateDailyReputationDiff(trustValues);
		ecommerce.getMcc().updateDailyMCC(mccValues,ecommerce.getDay());
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
