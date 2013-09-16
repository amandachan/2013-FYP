package main;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class Product {
	private Random randomGenerator;
	private int noOfProducts;
	//key is seller id, value is a list of product id sold by the seller
	private HashMap<Integer, ArrayList<Integer>> productid = new HashMap<Integer, ArrayList<Integer>>();
	//key is product id, value is sale price
	private HashMap<Integer, Double> saleprice = new HashMap<Integer, Double>();
//Product(){
 ///   productid.put(1, null)
//}

	public int getProduct(int sellerid){
		//get list of products sold by the seller
		ArrayList<Integer> prodid = new ArrayList<Integer>();
		prodid = productid.get(sellerid);
		//randomly select product from list of products
        int productid = randomGenerator.nextInt(prodid.size());
        return productid;
	}
	
	public int getNoOfProducts() {
		return noOfProducts;
	}


	public void setNoOfProducts(int noOfProducts) {
		this.noOfProducts = noOfProducts;
	}


	public HashMap<Integer, ArrayList<Integer>> getProductid() {
		return productid;
	}


	public void setProductid(HashMap<Integer, ArrayList<Integer>> productid) {
		this.productid = productid;
	}


	public HashMap<Integer, Double> getSaleprice() {
		return saleprice;
	}


	public void setSaleprice(HashMap<Integer, Double> saleprice) {
		this.saleprice = saleprice;
	}


	public double getProductPrice(int productid){
		double price = saleprice.get(productid);
		return price;
	}
	
	
}
