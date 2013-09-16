package attacks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import agent.Buyer;
import agent.Seller;

import weka.core.Instance;
import weka.core.Instances;

import main.Parameter;
import main.Product;
import main.Rating;

import distributions.PseudoRandom;


public class AlwaysUnfair extends Attack{
	
	//randomly choose seller
	public int chooseSeller(){
		//attack the target sellers with probability (Para.m_targetDomination), attack common sellers randomly with 1 - probability
		int sellerid;
		if(PseudoRandom.randDouble() < Parameter.m_dishonestBuyerOntargetSellerRatio){ //Para.m_targetDomination
			sellerid = (PseudoRandom.randDouble() < 0.5)? Parameter.TARGET_DISHONEST_SELLER:Parameter.TARGET_HONEST_SELLER;
		} else{
			//1 + [0, 18) = [1, 19) = [1, 18]
			sellerid = 1 + (int) (PseudoRandom.randDouble() * (Parameter.NO_OF_DISHONEST_SELLERS + Parameter.NO_OF_HONEST_SELLERS - 2));
		}
		//Seller s = new Seller();
                System.out.println("seller id is "+sellerid);
		//return s.getSeller(sellerid);
                return sellerid;
	}

	//give unfair rating to seller
	public double giveUnfairRating(Instance inst){
		String bHonestVal = inst.stringValue(Parameter.m_bHonestIdx);				
		// find the dishonest buyer in <day>, give unfair rating
		if (bHonestVal == Parameter.agent_honest){
			System.out.println("error, must be dishonest buyer");
		}
		//rating is always unfair
		int sVal = (int)(inst.value(Parameter.m_sidIdx));
		double unfairRating = complementRating(sVal);
		double rVal = unfairRating;		
		
		// add the unfair rating to instances		
		inst.setValue(Parameter.m_ratingIdx, rVal);
		
		//update the eCommerce information
//*****		ecommerce.getTransactions().add(new Instance(inst));	
//*****		ecommerce.updateArray(inst);
		return rVal;
	}


}
