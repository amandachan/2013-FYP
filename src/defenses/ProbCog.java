/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author NEEL
 */
package defenses;

import java.util.*;

import distributions.PseudoRandom;
import agent.*;
import environment.*;
import main.*;

import weka.core.Instance;
import weka.core.Instances;

public class ProbCog extends Defense{


    private double m_mu = Parameter.m_mu;               //parameter in ProbCog, default is 0.6

    private double[][] BS_expVal = null;    //the expect values from buyers
    private double[][] BS_relVal = null;    //the reliability values from buyers
    private double m_popQuality;
    //private Instances transactions = m_eCommerce.getTransactions();
    private int m_NumInstances;
    private int m_NumBuyers;
    private int m_NumSellers;
    private double[] m_trustA;
    private int [][][] m_BSR;

    //double[] m_Rtimes;



    private String m_DefenseName;

    public ProbCog(){

        this.m_DefenseName = "ProbCog";
        //store the performance instances
//      m_performanceInsts = GlobalFunctions.initialStatisticFeatures(m_DefenseName);
//      setInputParameter("mu", m_mu);
    }

    public ProbCog(int nb){

        this.m_DefenseName = "ProbCog";
        //store the performance instances
    //  m_performanceInsts = GlobalFunctions.initialStatisticFeatures(m_DefenseName);
    //  setInputParameter("mu", m_mu);
    }

    public void readInstances(){

        //get the number of dishonest/honest buyers;
        //findSEparas(insts.relationName());
        Instances transactions = ecommerce.getM_Transactions();//ecommerce.getTransactions();
        m_NumBuyers = transactions.attribute(Parameter.m_bidIdx).numValues();
        m_NumSellers = transactions.attribute(Parameter.m_sidIdx).numValues();

        //Instance insts = new Instance(transactions.numAttributes());
        m_NumInstances = transactions.numInstances();
        m_trustA = new double[m_NumBuyers];                 //the initial trust values is 0;
        m_BSR =  new int[m_NumBuyers][m_NumSellers][2];
        //m_BSR = ((eCommerceB)m_eCommerce).getBuyerSellerRatingArray();
        BS_expVal = new double[m_NumBuyers][m_NumSellers];
        BS_relVal = new double[m_NumBuyers][m_NumSellers];
        //for statistic features
        //m_Rtimes = new double[Parameter.NO_OF_RUNTIMES];
        //System.out.println("m days: "+ m_day);

        //read the instances
        for(int i = 0; i < m_NumInstances; i++){
            Instance inst = transactions.instance(i);
            int bVal = (int)(inst.value(Parameter.m_bidIdx));
            int sVal = (int)(inst.value(Parameter.m_sidIdx));
            double rVal = inst.value(Parameter.m_ratingIdx);
        //  rVal = GlobalFunctions.rating_real2binary(rVal);
            if (rVal == Parameter.RATING_BINARY[0]) {//-1
                m_BSR[bVal][sVal][0]++;
            } else if (rVal == Parameter.RATING_BINARY[1]) {
                // nothing need to do;
            } else if (rVal == Parameter.RATING_BINARY[2]) {//1
                m_BSR[bVal][sVal][1]++;
            }
            //int dVal = (int)(inst.value(Parameter.m_dayIdx));
            //m_Rtimes[dVal]++;
        }

        for(int i = 0; i < m_NumBuyers; i++){
            int bid = i;
            for(int j = 0; j < m_NumSellers; j++){
                int sid = j;
                BS_expVal[bid][sid] = getExpectedValue(ecommerce.getBuyerList().get(bid),ecommerce.getSellerList().get(sid));//getExpectedValue(bid, sid);
                BS_relVal[bid][sid] = getBuyerReliabilityValue(ecommerce.getBuyerList().get(bid),ecommerce.getSellerList().get(sid));//getBuyerReliabilityValue(bid, sid);
            }
        }
    }

//
//  protected double calculateTrust(int bid, int aid){
//
//      double trust = 0.5;
//
//      return trust;
//  }
    //for read instances
    public double getExpectedValue(Buyer buyer,Seller seller){//private double getExpectedValue(int bid, int sid) {

       // double neg_b = m_BSR[bid][sid][0];
       // double pos_b = m_BSR[bid][sid][1];
        Transaction buyerTrans=buyer.getTrans().get(buyer.getId());
        double neg_b = buyerTrans.getRating().getCriteriaRatings().get(0).getCriteriaRatingValue();
        double pos_b = buyerTrans.getRating().getCriteriaRatings().get(1).getCriteriaRatingValue();
        double ev = 0.5;
        if(pos_b + neg_b != 0){
            ev = (pos_b + 1.0)/ (pos_b + neg_b + 2.0);
        }
        return ev;
    }
    //for read instances
    public double getBuyerReliabilityValue(Buyer buyer, Seller seller){//private double getBuyerReliabilityValue(int bid, int sid) {

        int q = 100;
        double[] interval = new double[q + 1];
        double[] value = new double[q + 1];
        double innerRes = 0.0;
        double outerRes = 0.0;
        // init interval variable.
        interval[0] = 0;
        for (int k = 1; k <= q; k++) {
            interval[k] = interval[k - 1] + 1.0 / q;
        }
        // *** For buyer(itself)
        Transaction buyerTrans=buyer.getTrans().get(buyer.getId());
        double s = buyerTrans.getRating().getCriteriaRatings().get(0).getCriteriaRatingValue();//int s = m_BSR[bid][sid][0];
        double r = buyerTrans.getRating().getCriteriaRatings().get(1).getCriteriaRatingValue(); //int r = m_BSR[bid][sid][1];
        if ((r != 0) || (s != 0)) {
            for (int i = 0; i < interval.length; i++) {
                value[i] = Math.pow(interval[i], r) * Math.pow(1 - interval[i], s);
            }
            // **Apply the rule for inner integral
            innerRes = value[0] + value[q];
            for (int i = 1; i < interval.length - 1; i++) {
                innerRes += 2.0 * value[i];
            }
            innerRes *= (0.5 * 1.0 / q);

            // ** outer integral Calculation: 1/2*abs(1/innerRes*x^r(1-x)^s -1)
            for (int i = 0; i < interval.length; i++) {
                double inverse = 1 / innerRes;
                value[i] = 0.5 * Math.abs(inverse * (Math.pow(interval[i], r) * Math.pow(1 - interval[i], s)) - 1);
            }
            // **Apply the rule for outer integral
            outerRes = value[0] + value[q];
            for (int i = 1; i < interval.length - 1; i++) {
                outerRes += 2.0 * value[i];
            }
            outerRes *= (0.5 * 1.0 / q);
        }

        return outerRes;
    }
    //used in FirstLayerClassification
    private double calcCompetency(int bid, int aid) {
        double competency;
        double disbelief = 0.0;;
        double uncertainty = 0.0;

        int validateSellers = 0;
        for(int j = 0; j < m_NumSellers ; j++){
            int sid = j;
            if(BS_relVal[bid][sid] == 0 || BS_relVal[aid][sid] == 0){
                continue;
            }
            validateSellers++;
            disbelief += Math.abs(BS_expVal[bid][sid] - BS_expVal[aid][sid]);
            uncertainty += Math.abs(BS_relVal[bid][sid] - BS_relVal[aid][sid]);
        }
        if(validateSellers != 0){
            competency = (1 - disbelief / validateSellers) * (1 - uncertainty / validateSellers);
        } else{
            competency = -9999;
        }

        return competency;
    }
    //used in calculate reputation
    private void FirstLayerClassification(int bid) {

        int firstlayerDishonestyPred = 0;
        //m_mu = 0.6; //default setting from zeinab, 1.2 * (beta + epsilon); //0.6
        for (int k = 0; k < m_NumBuyers; k++) {
            int aid = k;
            if(bid == aid)continue;
            double A_comeptenceVal = calcCompetency(bid, aid);
//          System.out.println("A competence = " + A_comeptenceVal);
            if(A_comeptenceVal < 0){
                //buyer and advisor has no common rating pair
                m_trustA[aid] = 0.5;
                continue;
            }
            if (1 - A_comeptenceVal > m_mu) {// predict as dishonest advisors;
                firstlayerDishonestyPred++;
                m_trustA[aid] = 0.0;
            } else {
                m_trustA[aid] = 1.0;
            }

        }
        m_popQuality = firstlayerDishonestyPred / m_NumBuyers;
    }

    private void SecondLayeradvisersClassification() {
        //when the rating to sellers has variance, then second model is useful.

        double beta = 0.0;      //the variance to seller
        double Inf_cog = 0.5;   //default setting from zeinab
        double beta1 = -1 * beta;
        double epsilon = (1 - m_popQuality) * Inf_cog * java.lang.Math.pow(Math.E, beta1);

        // System.out.println("beta:" + df.format(beta) +" ,epsilon:"+
        // df.format(epsilon) );
        for (int k = 0; k <m_NumBuyers; k++) {
            int aid = k;
            double A_diff = 0.0;
//in our model, there is no rating variance to sellers.
            if(A_diff <= beta + epsilon){
                m_trustA[aid] = 1.0;
            }else{
                m_trustA[aid] = 0.0;
            }
        }
    }
//initially this was calculateReputation(int bid, int sid)
public double calculateTrust(Seller seller,Buyer honestBuyer){

            //get parameter settings;
            //m_mu = ((Double)this.getInputParameter("mu")).doubleValue();
           readInstances();
           int bid = honestBuyer.getId();
           int sid = seller.getId();
           Transaction buyerTrans;

        m_trustA = new double[m_NumBuyers];
            for (int k = 0; k < m_NumBuyers; k++) {
                int aid = k;
                buyerTrans=honestBuyer.getTrans().get(bid);
               if(buyerTrans.getRating().getCriteriaRatings().get(0).getCriteriaRatingValue()==0 
                        && buyerTrans.getRating().getCriteriaRatings().get(1).getCriteriaRatingValue()==0){// if(m_BSR[aid][sid][0] == 0 && m_BSR[aid][sid][1] == 0){
                    //no transaction with seller
                    m_trustA[aid] = 0.5;
                }
            }
            //get the negative/positive rating for pairs of buyer and seller with advisors help
            double neg_BAforS = 0;
            double pos_BAforS = 0;

            FirstLayerClassification(bid);


            //is this the second layer classification? seems like
            for (int k = 0; k < m_NumBuyers; k++) {
                int aid = k;
                buyerTrans = honestBuyer.getTrans().get(bid);
                if(bid == aid)continue;
                if(buyerTrans.getRating().getCriteriaRatings().get(0).getCriteriaRatingValue()==0 
                        && buyerTrans.getRating().getCriteriaRatings().get(1).getCriteriaRatingValue()==0){//if(m_BSR[aid][sid][0] == 0 && m_BSR[aid][sid][1] == 0){
                    //no transaction with seller
                    m_trustA[aid] = 0.5;
                    continue;
                }
                neg_BAforS += m_trustA[aid] * m_BSR[aid][sid][0];
                pos_BAforS += m_trustA[aid] * m_BSR[aid][sid][1];
            }

            double rep_BAforS = 0.5;
            if(pos_BAforS + neg_BAforS == 0.0){
                rep_BAforS = 0.5;
        }else{
                rep_BAforS = pos_BAforS / (pos_BAforS + neg_BAforS);
            }

    //change to predict reputation with real ratings, code by siweiJiang, 2012-10-08
        //  rep_BAforS = getReputationWithRealRatings(bid, sid);



            return rep_BAforS;
}

//  @Override
//  protected double calculateTrust(Buyer honestBuyer, int sid) {
//  //to fill in steps
//    //Step 1 The first layer approach
//
//
//
//  //Step 2 The second layer approach
//
//
//      return 0;
//

	public Seller chooseSeller(Buyer honestBuyer, Environment ec) {
		this.ecommerce = ec;
		//System.out.println("in chooseseller method");
		//calculate the trust values on target seller
		ArrayList<Double> trustValues = new ArrayList<Double>();
		ArrayList<Double> mccValues = new ArrayList<Double>();
		Seller s = new Seller();
		for (int k = 0; k < 2; k++) {
			int sid = Parameter.TARGET_DISHONEST_SELLER;
			if (k == 1)sid = Parameter.TARGET_HONEST_SELLER;

			trustValues.add(k,calculateTrust(honestBuyer.getSeller(sid),honestBuyer));


		//	mccValues.add(k,calculateMCCofAdvisorTrust(sid));


		}
		//update the daily reputation difference

		ecommerce.updateDailyReputationDiff(trustValues);
		//ecommerce.updateDailyMCC(mccValues);

		//select seller with the maximum trust values from the two target sellers
		int sellerid = Parameter.TARGET_DISHONEST_SELLER;
		if(trustValues.get(0) < trustValues.get(1)){
			sellerid = Parameter.TARGET_HONEST_SELLER;
		} else if (trustValues.get(0) == trustValues.get(1)){
			sellerid = (PseudoRandom.randDouble() < 0.5)?Parameter.TARGET_DISHONEST_SELLER:Parameter.TARGET_HONEST_SELLER;
		}
		if(PseudoRandom.randDouble() > Parameter.m_honestBuyerOntargetSellerRatio){
			sellerid = 1 + (int)(PseudoRandom.randDouble() * (Parameter.TARGET_DISHONEST_SELLER + Parameter.TARGET_HONEST_SELLER - 2));
		}
		return honestBuyer.getSeller(sellerid);
	}


        public void calculateReputation1(Buyer buyer,Seller seller){}
        public void calculateReputation2(Buyer buyer, Seller seller){}

		@Override
		public void calculateReputation1(Buyer buyer1, Seller sid,
				ArrayList<Boolean> trustAdvisors) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public ArrayList<Boolean> calculateReputation2(Buyer buyer, Seller sid,
				ArrayList<Boolean> trustAdvisors) {
			// TODO Auto-generated method stub
			return null;
		}

}//class
