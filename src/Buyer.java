import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashMap;
import java.util.Map;





public class Buyer extends Agent{

	private ArrayList<Rating> ratingsToSellers;
	private ArrayList<Rating> ratingsToProducts;
	private ArrayList<Product> productsPurchased;
	private ArrayList<Seller> sellersRated;
	private Seller ss;

	public Seller chooseSeller(){
		Seller s = null;
		return s;
	}



	public void rateSeller(){

	}

	public Product chooseProduct(){
		Product p = null;
		return p;
	}




	public void buyProduct(){

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
