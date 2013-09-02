
public abstract class Defense {
	protected String type;
	protected Agent ag;
	abstract Rating calculateReputation(Buyer b, Seller s);
	abstract Rating calculateReputation(Buyer b1, Buyer b2);
	abstract Rating calculateReputation(Buyer b, Product p);
}