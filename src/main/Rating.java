package main;
import java.util.ArrayList;


public class Rating {

	private ArrayList<Criteria> criteriaRatings;
	private int rater;
	private int ratee;
	
	public void create(int sid, int bid){
		this.rater = bid;
		this.ratee = sid;
		
	}
	
	public Rating getRating(){
		Rating r= null;
		return r;
	}
	
	
}
