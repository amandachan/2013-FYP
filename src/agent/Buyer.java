package agent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashMap;
import java.util.Map;

import main.Rating;
import main.Transaction;

import defenses.BRS;
import defenses.Defense;

import attacks.AlwaysUnfair;
import attacks.Attack;

public class Buyer extends Agent{
	private boolean ishonest;
	private Attack a;
	private Defense d;
	private ArrayList<Rating> ratingsToSellers;
	private ArrayList<Rating> ratingsToProducts;
	private ArrayList<Integer> productsPurchased;
	private ArrayList<Seller> sellersRated;

	public Buyer (int id, int reputation, boolean ishonest, String modelName){
		this.id = id;
		this.reputation = reputation;
		this.ishonest = ishonest;
	}

	public void chooseModel(){
		//dishnest buyer -- attack mode (testing with AlwaysUnfair mode)
		if (this.ishonest==false){
			a = new AlwaysUnfair();
			int sid = a.chooseSeller();
			int pid = a.chooseProduct(sid);
			double amount = a.buyProduct(pid);
			confirmPurchase(sid, pid, amount);
		}
		
		//honest buyer -- defense mode (testing with BRS mode)
		if(this.ishonest==true){
			d = new BRS();
			
		}
	}

	//create transaction that includes buyer, seller and product
	public void confirmPurchase(int sellerid, int productid, double amount){
		Transaction t = new Transaction(this.id, sellerid, productid, amount);
		productsPurchased.add(productid);
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

	public ArrayList<Integer> getPurchasedProducts(){
		return productsPurchased;
	}
}
