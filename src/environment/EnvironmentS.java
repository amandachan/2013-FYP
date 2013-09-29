package environment;
import agent.*;
import weka.core.*;
import java.util.*;

public class EnvironmentS extends Environment{

      //private Environment commerce;

      //to deal with binary ratings, defense models = "nodefense", "brs", "travos", "personalized",

    //store the array double [buyers][sellers][ratings]
    private HashMap<Buyer,Seller> m_BS;

   //  private HashMap<HashMap<Buyer,Seller>,Double> m_BSR;
     // private int[][][] m_BSR = null;
    //store the array double [sellers][ratings]
   // private int[][] m_SR = null;
   private HashMap<Seller,Double> m_SR;
    //for travos, store the current rating [buyers][sellers]
    //private int[][] m_BS_currRating = null;
 //  private HashMap<HashMap<Buyer,Seller>,Integer> m_BS_currRating;
   //for personalized approach, store the same / not same rating [buyers][advisor][not same/same] for all the sellers;
  //  private int[][][] m_BA_sameRatings = null;
    //for EA, //validate sellers and advisors
    private Vector<Integer>[] m_validateSellers = null;
    private Vector<Integer>[] m_validateAdvisors = null;


       public void eCommerceSetting(String attackName,String defenseName){

           System.out.println("enters eCommerce");

           parameterSetting(attackName, defenseName);
           generateInstances();
         try{
           agentSetting(attackName,defenseName);
         }
         catch(Exception e){
             
         }
           assignTruth();
           /*if(Parameter.includeICLUB(defenseName)){
            if (Parameter.RATING_TYPE.equalsIgnoreCase("binary")) {
                commerce.parameterSetting(attackName, defenseName);
            } else if (Parameter.RATING_TYPE.equalsIgnoreCase("multinominal")) {
                commerce.parameterSetting(attackName, defenseName);
            } else{
                m_eCommerce = new eCommerceM(attackName, defenseName);
            }
        } else if(Parameter.includeWMA(m_defenseName)){
            m_eCommerce = new eCommerceR(attackName, defenseName);
        } else if(Parameter.includeEA(m_defenseName)){
            if (Parameter.RATING_TYPE.equalsIgnoreCase("binary")) {
                m_eCommerce = new eCommerceB(attackName, defenseName);
            } else if (Parameter.RATING_TYPE.equalsIgnoreCase("multinominal")) {
                m_eCommerce = new eCommerceM(attackName, defenseName);
            } else if (Parameter.RATING_TYPE.equalsIgnoreCase("real")) {
                m_eCommerce = new eCommerceR(attackName, defenseName);
            }
        } else{
            //for binary ratings, nondefense, BRS, iCLUB, TRAVOS, personalized,
            m_eCommerce = new eCommerceB(attackName, defenseName);
        } */
       // m_eCommerce.generateInstances();
    }
       public Instances generateInstances(){

           System.out.println("enters generateInstances");
           //initialize the header information of instances
       Instances header = initialInstancesHeader();
       Instances header2 = initialInstancesHeader2();
        m_Transactions = new Instances(header); //protected variable declared under Environment class.
        balances = new Instances(header2);
        System.out.println(m_Transactions);
        System.out.println(balances);
       //remove the above comment lines later...WE NEED the statements
       // dont remove statements

         // assignTruth();

        //initialize the double array;
        //m_BSR = new int[m_NumBuyers][m_NumSellers][2];
     //   m_BSR = new HashMap();
        //m_SR = new int[m_NumSellers][2];
        m_SR = new HashMap();

        m_BS = new HashMap();
        // ** NEED TO ADD THE CODE FOR GENERATE INSTANCES
        //    USED IN ecommerceM,R and B.
        // **

     /*   if(Parameter.includeTRAVOS(m_DefenseName)){   //from ecommerceB
            //m_BS_currRating  = new int[m_NumBuyers][m_NumSellers];
            m_BS_currRating = new HashMap();
            for(int i = 0; i < m_NumBuyers; i++){
                for(int j = 0; j < m_NumSellers; j++){
                    //m_BS_currRating[i][j] = Parameter.RATING_BINARY[1];   //null rating
                 m_BS_currRating.put(m_BS, Parameter.RATING_BINARY[1]);
                }
            }

       }
      */
        return m_Transactions;
       } //generateInstances

	@Override
	public void importConfigSettings() {
		// TODO Auto-generated method stub

	}

	@Override
	public void createEnvironment() {
		// TODO Auto-generated method stub

	}

	@Override
	public void saveWekaInstances() {
		// TODO Auto-generated method stub

	}

}


