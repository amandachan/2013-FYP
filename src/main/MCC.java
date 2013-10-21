package main;

import java.util.ArrayList;
import java.util.HashMap;

public class MCC extends EvaluationMetrics{

	private HashMap<Integer, Double> dailyMCChonest = new HashMap<Integer,Double>();
	private HashMap<Integer, Double> dailyMCCdishonest = new HashMap<Integer,Double>();

	public MCC(){
		for (int i=0; i<Parameter.NO_OF_DAYS; i++){
			dailyMCChonest.put(i, 0.0);
			dailyMCCdishonest.put(i,0.0);
		}
	}

	public HashMap<Integer, Double> getDailyMCChonest() {
		return dailyMCChonest;
	}

	public void setDailyMCChonest(HashMap<Integer, Double> dailyMCChonest) {
		this.dailyMCChonest = dailyMCChonest;
	}

	public HashMap<Integer, Double> getDailyMCCdishonest() {
		return dailyMCCdishonest;
	}

	public void setDailyMCCdishonest(HashMap<Integer, Double> dailyMCCdishonest) {
		this.dailyMCCdishonest = dailyMCCdishonest;
	}

	private ArrayList<Integer> cofusionMatrix(ArrayList<Double>trustOfAdvisors) {
		// true positive, false negative, false positive, true negative,
		ArrayList<Integer> cmvals = new ArrayList<Integer>();
		for(int i=0; i<4; i++){
			cmvals.add(0);
		}
		for (int k = 0; k < (Parameter.NO_OF_DISHONEST_BUYERS+Parameter.NO_OF_HONEST_BUYERS); k++) {
			int aid = k;
			if (aid >= Parameter.NO_OF_DISHONEST_BUYERS && aid < (Parameter.NO_OF_DISHONEST_BUYERS+Parameter.NO_OF_HONEST_BUYERS)) { // ground truth: honest advisors
				if (trustOfAdvisors.get(aid) > 0.5) // true positive
				{
					cmvals.set(0, cmvals.get(0)+1);	
				}
				else if (trustOfAdvisors.get(aid) < 0.5) // false negative
				{
					cmvals.set(1, cmvals.get(1)+1);					
				}
			} else { // ground truth: dishonest advisors
				if (trustOfAdvisors.get(aid)> 0.5) // false positive
				{
					cmvals.set(2, cmvals.get(2)+1);					
				}
				else if (trustOfAdvisors.get(aid) < 0.5) // true negative
				{
					cmvals.set(3, cmvals.get(3)+1);						
				}
			}
		}
		//System.out.println("ARGH " + cmvals.get(0) + " " + cmvals.get(1) + " " + cmvals.get(2) + " " + cmvals.get(3));
		return cmvals;
	}

	public double calculateMCC(int sid, ArrayList<Double> trustOfAdvisors) {

		double MCC = 0.0;
		double tp, fn, fp, tn;
		tp = fn = fp = tn = 0;

		for (int j = 0; j < (Parameter.NO_OF_DISHONEST_SELLERS+Parameter.NO_OF_HONEST_SELLERS); j++) {
			if(j!=sid)continue;
			ArrayList<Integer> cvals= cofusionMatrix(trustOfAdvisors);	
			tp += cvals.get(0);
			fn += cvals.get(1);
			fp += cvals.get(2);
			tn += cvals.get(3);
		} 

		MCC = (tp * tn - fp * fn)
				/ Math.sqrt((tp + fp) * (tp + fn) * (tn + fp) * (tn + fn));
		if (Double.isNaN(MCC)) {
			MCC = -1.0;
		}
		//.out.println("MCC: "+MCC);
		return MCC;
	}


	public ArrayList<Double> getDailyMCC(int day){
		ArrayList<Double> dailymcc = new ArrayList<Double>();
		dailymcc.add(dailyMCCdishonest.get(day) / (Parameter.NO_OF_HONEST_BUYERS));
		dailymcc.add(dailyMCChonest.get(day) / (Parameter.NO_OF_HONEST_BUYERS));
		return dailymcc;
	}
	
	public void updateDailyMCC(ArrayList<Double> mccVals, int day){
		dailyMCCdishonest.put(day, dailyMCCdishonest.get(day)+mccVals.get(0));
		dailyMCChonest.put(day,  dailyMCChonest.get(day)+mccVals.get(1));
	}
}
