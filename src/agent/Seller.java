package agent;

import java.util.ArrayList;

import main.Product;
import main.Rating;



public class Seller extends Agent{

	private ArrayList<Rating> ratingsToBuyers;
	private ArrayList<Rating> ratingsFromBuyers;
	private ArrayList<Product> productsOnSale = new ArrayList<Product>();
	private ArrayList<Buyer> buyersRated;
	private ArrayList<Buyer> buyersRatedMe;

       
	public Seller(){
	}
	public void addProductToList(Product p){
		productsOnSale.add(p);
	}
	public ArrayList<Product> getProductsOnSale() {
		return productsOnSale;
	}

	public void setProductsOnSale(ArrayList<Product> productsOnSale) {
		this.productsOnSale = productsOnSale;
	}

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
	
	
}
