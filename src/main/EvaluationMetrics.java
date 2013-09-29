package main;

import java.util.HashMap;

import agent.Seller;

public class EvaluationMetrics {

	private double mae;
	private double mcc;
	private double robustness;
	private double bankBalance;
	private CentralAuthority ca;

	public EvaluationMetrics(CentralAuthority ca){
		this.ca = ca;
	}

	/*public double calculateMAEofSellerReputation(int criteria,HashMap<Seller,Double> m_SellersTrueRep) {

		double MAE = 0.0;

		for (int i = Parameter.NO_OF_DISHONEST_BUYERS; i < (Parameter.NO_OF_HONEST_BUYERS+Parameter.NO_OF_DISHONEST_BUYERS); i++) {
			//only honest buyers use trust models to predict the reputation of sellers;
			int bid = i;
			for(int j = 0; j < (Parameter.NO_OF_DISHONEST_SELLERS+Parameter.NO_OF_HONEST_SELLERS); j++){
				int sid = j;                       
				double S_rep = m_SellersTrueRep.get(ca.getEnv().getSellerList().get(j));
				//double S_rep=sellersTrueRep[j][criteria];
				double S_repPredict = calculateReputation(bid, sid);

				MAE += Math.abs(S_rep - S_repPredict);
			}                
		}
		MAE /= (m_NumHB * m_NumSellers);

		return MAE;
	}*/




}
