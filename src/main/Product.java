package main;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import agent.Seller;

public class Product {
	private int id;
	private double price;
	private Seller s;
	private ArrayList<Product> listOfProducts = new ArrayList<Product>();
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}
	public Seller getS() {
		return s;
	}
	public void setS(Seller s) {
		this.s = s;
	}
	public ArrayList<Product> getListOfProducts() {
		return listOfProducts;
	}
	public void setListOfProducts(ArrayList<Product> listOfProducts) {
		this.listOfProducts = listOfProducts;
	}
	
	
	
	
}
