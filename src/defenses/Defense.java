package defenses;
import main.Product;
import main.Rating;
import agent.Agent;
import agent.Buyer;
import agent.Seller;


public abstract class Defense {
	protected String type;
	protected Agent ag;
	public abstract Rating calculateReputation(Buyer b, Seller s);
	public abstract Rating calculateReputation(Buyer b1, Buyer b2);
	public abstract Rating calculateReputation(Buyer b, Product p);
	public abstract void performDefense();
}