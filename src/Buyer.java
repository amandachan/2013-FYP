import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashMap;
import java.util.Map;


public class Buyer extends Agent{
	private boolean ishonest;
	private ArrayList<Rating> ratingsToSellers;
	private ArrayList<Rating> ratingsToProducts;
	private ArrayList<Product> productsPurchased;
	private ArrayList<Seller> sellersRated;

	public Buyer (int id, int reputation, boolean ishonest, String modelName){
		this.id = id;
		this.reputation = reputation;
		this.ishonest = ishonest;
		if(ishonest==false){
			this.attackName = modelName;
		}
		else
			this.defenseName = modelName;
	}

	public Seller chooseSeller(int day){
		int sellerid = 0;

		//sellect seller where buyer is dishonest (attack)
		switch(this.attackName){
		case "SybilWhitewashing":
		case "Sybil":
		case "Whitewashing":
		case "AlwaysUnfair":
			if(PseudoRandom.randDouble() < Parameter.m_dishonestBuyerOntargetSellerRatio){ //Para.m_targetDomination
				sellerid = (PseudoRandom.randDouble() < 0.5)? Parameter.TARGET_DISHONEST_SELLER:Parameter.TARGET_HONEST_SELLER;
			} else{
				sellerid = 1 + (int) (PseudoRandom.randDouble() * (Parameter.NO_OF_DISHONEST_SELLERS + Parameter.NO_OF_HONEST_SELLERS - 2));
			}
			break;
		case "SybilCamouflage":
		case "Camouflage":
			if(day < 0.2 * Parameter.NO_OF_DAYS){ 
				sellerid = 1 + (int) (PseudoRandom.randDouble() * (Parameter.NO_OF_DISHONEST_SELLERS + Parameter.NO_OF_HONEST_SELLERS - 2));
			}
			else{
				if(PseudoRandom.randDouble() < Parameter.m_dishonestBuyerOntargetSellerRatio){ //Para.m_targetDomination
					sellerid = (PseudoRandom.randDouble() < 0.5)? Parameter.TARGET_DISHONEST_SELLER:Parameter.TARGET_HONEST_SELLER;
				}
				else{
					sellerid = 1 + (int) (PseudoRandom.randDouble() * (Parameter.NO_OF_DISHONEST_SELLERS + Parameter.NO_OF_HONEST_SELLERS - 2));
				}
			}
			break;

		}

		//select seller where buyer is honest (defense)

		//return seller selected
		Seller s = null;
		s = s.getSeller(sellerid);
		return s;
	}



	public void rateSeller(Seller s){
		//rate seller where buyer is dishonest (attack)
		if (this.ishonest==false){
			attack.giveUnfairRating(s.id);
		}
		//rate seller where buyer is honest (defense)

		//update list of sellers that buyer has rated
		sellersRated.add(s);
	}

	public Product chooseProduct(Seller s){

		//depending on seller selected, get a product from hashmap
		Product p1 = new Product();
		Product p = p1.getProduct(s.id);
		return p;
	}




	public void buyProduct(Seller s, Product p, int pid){

		//add buyer, seller & product info as a transaction record
		Transaction t = new Transaction(s.id, this.id, p.getProduct(s.id).hashCode(), p.getSalePrice(s));

		//update list of products purchased
		productsPurchased.add(p);
	}

	public ArrayList<Rating> getRatingsToSellers() {
		return ratingsToSellers;
	}

	public void setRatingsToSellers(ArrayList<Rating> ratingsToSellers) {
		this.ratingsToSellers = ratingsToSellers;
	}

	public ArrayList<Transaction> getBuyerTransactions(){
		ArrayList<Transaction> buyerTransactions = null;
		return buyerTransactions;
	}

	public ArrayList<Product> getPurchasedProducts(){
		return productsPurchased;
	}
}
