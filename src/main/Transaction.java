package main;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import agent.Buyer;
import agent.Seller;


public class Transaction {

	private Buyer buyer;
	private Seller seller;
	private int product;
	private String time;
	private double amountPaid;
	private Rating rating;
	
	public Transaction(){

	}
	
	public void create(Buyer buyer, Seller seller, int product, double amountPaid, double value, int cid){
		this.buyer = buyer;
		this.seller = seller;
		this.product = product;
		this.amountPaid = amountPaid;
    	Calendar cal = Calendar.getInstance();
    	cal.getTime();
    	SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
    	time = sdf.format(cal.getTime());
    	this.rating = new Rating();
    	rating.create(seller, buyer, value, cid);
	}
	
	public void edit(){
		
	}
	
	public Transaction view(){
		Transaction t = null;
		return t;
	}

	public Buyer getBuyer() {
		return buyer;
	}

	public Seller getSeller() {
		return seller;
	}

	public String getTime() {
		return time;
	}

	public double getAmountPaid() {
		return amountPaid;
	}

	public Rating getRating() {
		return rating;
	}
	

	
	public Transaction getTransaction(){
		Transaction t = null;
		return t;
	}
	
}
