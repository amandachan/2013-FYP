package main;

import java.util.HashMap;

import agent.Seller;

public class MAE extends EvaluationMetrics{
	protected HashMap<Integer, Double> dailyRepDiffHonestSeller = new HashMap<Integer, Double>();
	protected HashMap<Integer, Double> dailyRepDiffDishonestSeller = new HashMap<Integer, Double>();

	public MAE(){
		for (int i=0; i<Parameter.NO_OF_DAYS; i++){
			dailyRepDiffHonestSeller.put(i, 0.0);
			dailyRepDiffDishonestSeller.put(i,0.0);
		}
	}

	public HashMap<Integer, Double> getDailyRepDiffHonestSeller() {
		return dailyRepDiffHonestSeller;
	}
	public void setDailyRepDiffHonestSeller(
			HashMap<Integer, Double> dailyRepDiffHonestSeller) {
		this.dailyRepDiffHonestSeller = dailyRepDiffHonestSeller;
	}
	public HashMap<Integer, Double> getDailyRepDiffDishonestSeller() {
		return dailyRepDiffDishonestSeller;
	}
	public void setDailyRepDiffDishonestSeller(
			HashMap<Integer, Double> dailyRepDiffDishonestSeller) {
		this.dailyRepDiffDishonestSeller = dailyRepDiffDishonestSeller;
	}


}
