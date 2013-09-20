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
        private int day;
        private int quantity;
        private double price;
        private String remarks;

        public String getRemarks(){
            return remarks;
        }

        public void setRemarks(String remarks){
            this.remarks = remarks;
        }

        public double getPrice(){
            return price;
        }

        public void setPrice(double price){
            this.price = price;
        }

        public int getQuantity(){
            return quantity;
        }

        public void setQuantity(int quantity){
            this.quantity=quantity;
        }

        public int getDay(){
            return day;
        }

        public void setDay(int day){
            this.day = day;
        }
	
	public Transaction(){

	}
	
	public void create(Buyer buyer, Seller seller, int product, int quantity, double price, int day, double amountPaid, double value, int cid){
		this.buyer = buyer;
		this.seller = seller;
		this.product = product;
		this.amountPaid = quantity * price;
                this.price = price;
                this.day = day;
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

        public void setAmountPaid(){
            this.amountPaid = getQuantity() * getPrice();
        }

	public Rating getRating() {
		return rating;
	}
	

	
	public Transaction getTransaction(){
		Transaction t = null;
		return t;
	}
	
}
