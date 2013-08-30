
public class Transaction {

	private int buyer;
	private int seller;
	private int product;
	private int time;
	private double amountPaid;
	private double rating;
	
	public Transaction(int buyer, int seller, int product, int amountPaid){
		this.buyer = buyer;
		this.seller = seller;
		this.product = product;
		this.amountPaid = amountPaid;
	}
	public void create(){

	}
	
	public void edit(){
		
	}
	
	public Transaction view(){
		Transaction t = null;
		return t;
	}

	public int getBuyer() {
		return buyer;
	}

	public int getSeller() {
		return seller;
	}

	public int getTime() {
		return time;
	}

	public double getAmountPaid() {
		return amountPaid;
	}

	public double getRating() {
		return rating;
	}
	
	public Transaction getTransaction(){
		Transaction t = null;
		return t;
	}
	
}
