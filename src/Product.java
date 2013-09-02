import java.util.HashMap;


public class Product {

	private HashMap<Seller, Integer> salePrice;

	public double getSalePrice(Seller s) {
		double price = salePrice.get(s.id);
		return price;
	}

	public Product getProduct(int id){
		Product p = null;
		return p;
	}
	
}
