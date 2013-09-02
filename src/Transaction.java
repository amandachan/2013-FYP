import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;


public class Transaction {

	private int buyer;
	private int seller;
	private int product;
	private String time;
	private double amountPaid;
	private double rating;
	
	public Transaction(int buyer, int seller, int product, double amountPaid){
		this.buyer = buyer;
		this.seller = seller;
		this.product = product;
		this.amountPaid = amountPaid;
    	Calendar cal = Calendar.getInstance();
    	cal.getTime();
    	SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
    	time = sdf.format(cal.getTime());
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

	public String getTime() {
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
