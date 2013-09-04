package agent;

import java.util.ArrayList;

import main.Product;
import main.Rating;



public class Seller extends Agent{

	private ArrayList<Rating> ratingsToBuyers;
	private ArrayList<Rating> ratingsFromBuyers;
	private ArrayList<Product> productsOnSale;
	private ArrayList<Buyer> buyersRated;
	private ArrayList<Buyer> buyersRatedMe;
	private Product product;
	
	public ArrayList<Rating> getRatingsToBuyers() {
		return ratingsToBuyers;
	}
	
	public void setRatingsToBuyers(ArrayList<Rating> ratingsToBuyers) {
		this.ratingsToBuyers = ratingsToBuyers;
	}
	
	public ArrayList<Rating> getRatingsFromBuyers() {
		return ratingsFromBuyers;
	}
	
	public void setRatingsFromBuyers(ArrayList<Rating> ratingsFromBuyers) {
		this.ratingsFromBuyers = ratingsFromBuyers;
	}

	public ArrayList<Buyer> getBuyersRatedMe() {
		return buyersRatedMe;
	}

	public void setBuyersRatedMe(ArrayList<Buyer> buyersRatedMe) {
		this.buyersRatedMe = buyersRatedMe;
	}

	public ArrayList<Buyer> getBuyersRated() {
		return buyersRated;
	}

	public void setBuyersRated(ArrayList<Buyer> buyersRated) {
		this.buyersRated = buyersRated;
	}
	
	public Seller getSeller(int id){
		Seller s = null;
		if (this.id == id){
			s = this;
		}
		return s;
	}
	
}
