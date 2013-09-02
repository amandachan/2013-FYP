import java.util.ArrayList;


public abstract class Agent {

	protected int id;
	protected double reputation;
	protected String defenseName;
	protected String attackName;
	protected Attack attack;
	protected Defense defense;
	protected ArrayList<Transaction> trans;
	protected Account acc;
	
	public Agent() { 
		
	}
	
	public Agent(int id, double reputation) {
		this.id = id;
		this.reputation = reputation;
	}
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public double getReputation() {
		return reputation;
	}
	
	public void setReputation(double reputation) {
		this.reputation = reputation;
	}
	
	
}
