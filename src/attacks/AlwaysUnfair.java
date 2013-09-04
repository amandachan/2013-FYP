package attacks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import main.Parameter;
import main.Product;

import distributions.PseudoRandom;


public class AlwaysUnfair extends Attack{

	private Random randomGenerator;

	public int chooseSeller(){
		//attack the target sellers with probability (Para.m_targetDomination), attack common sellers randomly with 1 - probability
		int sellerid;
		if(PseudoRandom.randDouble() < Parameter.m_dishonestBuyerOntargetSellerRatio){ //Para.m_targetDomination
			sellerid = (PseudoRandom.randDouble() < 0.5)? Parameter.TARGET_DISHONEST_SELLER:Parameter.TARGET_HONEST_SELLER;
		} else{
			//1 + [0, 18) = [1, 19) = [1, 18]
			sellerid = 1 + (int) (PseudoRandom.randDouble() * (Parameter.NO_OF_DISHONEST_SELLERS + Parameter.NO_OF_HONEST_SELLERS - 2));
		}
		return sellerid;
	}

	public int chooseProduct(int sellerid){
		
		//get hashmap of seller id & product id
		HashMap<Integer, ArrayList<Integer>> prodList = new HashMap<Integer, ArrayList<Integer>>();
		Product p = new Product();
		prodList = p.getProductid();
		
		//get list of products sold by the seller
		ArrayList<Integer> prodid = new ArrayList<Integer>();
		prodid = prodList.get(sellerid);
		
		//randomly select product from list of products
		int pid = randomGenerator.nextInt(prodid.size());
		return pid;

	}

	public double buyProduct(int productid){

		//get hashmap of product id and sale price
		HashMap<Integer, Double> priceList = new HashMap<Integer, Double>();
		Product p = new Product();
		priceList = p.getSaleprice();
		
		//get price of product
		double price = priceList.get(productid);
		return price;
	}

	public void giveUnfairRating(int id){

	}





}
