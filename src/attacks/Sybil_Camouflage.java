/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author NEEL
 */
package attacks;


import agent.*;
import attacks.Attack;
import weka.core.Instance;
import weka.core.Instances;
import distributions.PseudoRandom;
import main.Parameter;

public class Sybil_Camouflage  extends Attack{

	//Attack parameter: no_of_dummies
	//After no_of_day_stay, the dishonest buyer just leaves and enters using a new account
	private double dishonest_buyer_percentage;

	public Sybil_Camouflage(){

		//      Parameter.NO_OF_DISHONEST_BUYERS = 14;
		//      Parameter.NO_OF_HONEST_BUYERS = 6;
		dishonest_buyer_percentage = (double)Parameter.NO_OF_DISHONEST_BUYERS/((double)Parameter.NO_OF_HONEST_BUYERS + (double)Parameter.NO_OF_DISHONEST_BUYERS);
		if (dishonest_buyer_percentage < 0.5){
			System.out.println("No a Sybil attack! Please ensure disnohest buyers are majority.");
			System.exit(0);
		}
	}

	public double giveUnfairRating(Instance inst){

		String bHonestVal = inst.stringValue(Parameter.m_bHonestIdx);
		// find the dishonest buyer in <day>, give unfair rating
		if (bHonestVal == Parameter.agent_honest){
			System.out.println("error, must be dishonest buyer");
		}
		int sVal = (int)(inst.value(Parameter.m_sidIdx));
		double rVal = 0.0;
		if(sVal == Parameter.TARGET_DISHONEST_SELLER ||sVal == Parameter.TARGET_HONEST_SELLER){
			//unfair ratings for target sellers
			double unfairRating = complementRating(sVal);
			rVal = unfairRating;
		}
		else if(sVal > Parameter.TARGET_DISHONEST_SELLER && sVal < Parameter.TARGET_HONEST_SELLER){
			//fair rating for common sellers
			rVal = ecommerce.getSellersTrueRating(sVal);
		} else{
			System.err.println("Not such rating");
		}
		// add the rating to instances
		inst.setValue(Parameter.m_ratingIdx, rVal);

		//update the eCommerce information
		if (ecommerce.getM_Transactions()!= null)
			ecommerce.getM_Transactions().add(new Instance(inst));

		return rVal;
	}

	/* public Instance perform_attack(int day, Buyer dishonestBuyer){

        this.day = day;
        Instances transactions = ecommerce.getM_Transactions();
                //ecommerce.getTransactions();
        //attack the target sellers with probability (Para.m_targetDomination), attack common sellers randomly with 1 - probability
        int chosen_seller;
        if(day < 0.2 * Parameter.NO_OF_DAYS){
            //for common seller, give fair rating
            chosen_seller = 1 + (int) (PseudoRandom.randDouble() * (Parameter.NO_OF_DISHONEST_SELLERS + Parameter.NO_OF_HONEST_SELLERS - 2));
        } else{
            //attack the target sellers with probability (Para.m_targetDomination), attack common sellers randomly with 1 - probability
            if(PseudoRandom.randDouble() < Parameter.m_dishonestBuyerOntargetSellerRatio){ //Para.m_targetDomination
                chosen_seller = (PseudoRandom.randDouble() < 0.5)? Parameter.TARGET_DISHONEST_SELLER:Parameter.TARGET_HONEST_SELLER;
            }else{
                //1 + [0, 18) = [1, 19) = [1, 18]
                chosen_seller = 1 + (int) (PseudoRandom.randDouble() * (Parameter.NO_OF_DISHONEST_SELLERS + Parameter.NO_OF_HONEST_SELLERS - 2));
            }
        }
        int sVal = chosen_seller;
        double sHonestVal = ecommerce.getSellersTrueRating(sVal);
        double rVal = Parameter.nullRating();

        //add instance
        int dVal = day + 1;
        int bVal = dishonestBuyer.getId();
        String bHonestVal = Parameter.agent_dishonest;
        Instance inst = new Instance(transactions.numAttributes());
        inst.setDataset(transactions);
        inst.setValue(Parameter.m_dayIdx, dVal);
        inst.setValue(Parameter.m_bidIdx, "b" + Integer.toString(bVal));
        inst.setValue(Parameter.m_bHonestIdx, bHonestVal);
        inst.setValue(Parameter.m_sidIdx, "s" + Integer.toString(sVal));
        inst.setValue(Parameter.m_sHonestIdx, sHonestVal);
        inst.setValue(Parameter.m_ratingIdx, rVal);

        return inst;
    }
	 */
	public Seller chooseSeller(Buyer b){

		int sellerid;
		if(b.getEcommerce().getDay() < 0.2 * Parameter.NO_OF_DAYS){ 
			//for common seller, give fair rating
			sellerid = 1 + (int) (PseudoRandom.randDouble() * (Parameter.NO_OF_DISHONEST_SELLERS + Parameter.NO_OF_HONEST_SELLERS - 2));
		} else{
			//attack the target sellers with probability (Para.m_targetDomination), attack common sellers randomly with 1 - probability
			if(PseudoRandom.randDouble() < Parameter.m_dishonestBuyerOntargetSellerRatio){ //Para.m_targetDomination
				sellerid = (PseudoRandom.randDouble() < 0.5)? Parameter.TARGET_DISHONEST_SELLER:Parameter.TARGET_HONEST_SELLER;
			}else{
				//1 + [0, 18) = [1, 19) = [1, 18]
				sellerid = 1 + (int) (PseudoRandom.randDouble() * (Parameter.NO_OF_DISHONEST_SELLERS + Parameter.NO_OF_HONEST_SELLERS - 2));
			}
		}	
		return b.getSeller(sellerid);

	}
	public String getParameterInfo(){
		String str = "(dishonest_buyer_percentage: " + dishonest_buyer_percentage + ")";
		str += " (comouflage)";
		return str;
	}
} //class SybilCamouflage
