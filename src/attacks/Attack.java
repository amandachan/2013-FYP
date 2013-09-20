package attacks;

import environment.*;
import agent.Buyer;
import agent.Seller;
import weka.core.Instance;
import main.Parameter;
import main.Product;
import main.Rating;

public abstract class Attack {
	protected int day;
	protected Environment ecommerce = null;
	public abstract double giveUnfairRating(Instance inst);
	public abstract Seller chooseSeller(Buyer b);


	public Environment getEcommerce() {
		return ecommerce;
	}
	public void setEcommerce(Environment ecommerce) {
		this.ecommerce = ecommerce;
	}
	
	//inverse the rating to make it unfair rating
	public double complementRating(int sid){
		double trueRating= ecommerce.getSellersTrueRating(sid);
		double cRating = 1.0;
		if(Parameter.RATING_TYPE.equalsIgnoreCase("binary")){
			cRating = -trueRating;
		} else if(Parameter.RATING_TYPE.equalsIgnoreCase("multinominal")){
			cRating = Parameter.RATING_MULTINOMINAL.length + 1 - trueRating;
		} else if(Parameter.RATING_TYPE.equalsIgnoreCase("real")){
			cRating = 1.0 - trueRating;
		} else{
			System.out.println("not such type of rating");
		}

		return cRating;
	}

}
