package main;
import java.util.ArrayList;

import agent.Buyer;
import agent.Seller;


public class Rating {

	private ArrayList<Criteria> criteriaRatings;
	private Buyer rater;
	private Seller ratee;
	
	public Rating(){
	criteriaRatings = new ArrayList<Criteria>();
	}
	
	public ArrayList<Criteria> getCriteriaRatings() {
		return criteriaRatings;
	}

	public void setCriteriaRatings(ArrayList<Criteria> criteriaRatings) {
		this.criteriaRatings = criteriaRatings;
	}

	public void create(Seller sid, Buyer bid, double value, int id){
		this.rater = bid;
		this.ratee = sid;
		Criteria c = new Criteria(value ,id);
		criteriaRatings.add(c);
		
	}
	
	public Rating getRating(){
		Rating r= null;
		return r;
	}
	
	
}
