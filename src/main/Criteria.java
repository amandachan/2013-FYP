package main;

public class Criteria {

	private double value;
	private int id; //unsure what this id represents
	
	public Criteria(double value, int id){
		this.id = id;
		this.value = value;
	}
	
	public double getCriteriaRatingValue() {
		return value;
	}
	public void setCriteriaRatingValue(double value) {
		this.value = value;
	}
	public int getCriteriaId() {
		return id;
	}
	public void setCriteriaId(int id) {
		this.id = id;
	}
	
	
}
