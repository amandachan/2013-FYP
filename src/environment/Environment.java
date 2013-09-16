package environment;
//import Transaction;

import java.util.ArrayList;
import main.*;

import main.Transaction;

import agent.Buyer;
import agent.Seller;


import java.util.ArrayList;
import weka.core.Instances;
import java.util.*;
import weka.core.*;
import java.lang.*;

public abstract class Environment {

	private ArrayList<Buyer> buyerList;
	private ArrayList<Seller> sellerList;
	private ArrayList<Transaction> transactionList;


        protected int m_NumAtts = 6;                        //[ day, buyer_id, buyer_is_honest, seller_id, seller_is_honest, rating]
    protected int m_Numdays;                                //days of transactions;
    protected int m_NumBuyers;
    protected int m_NumSellers;
    protected int m_NumRating;                        //binary, multinominal, real

    protected Instances m_Transactions = null;
    //attack name and defense name
    protected String m_AttackName = null;
    protected String m_DefenseName = null;

    //assign the true rating of sellers, binary: [-1, 0, 1], multinominal: [1,2,3,4,5], real: [0.0, 1.0]
    //protected double[] m_SellersTrueRating;



    protected HashMap<Seller,Double> m_SellersTrueRating; //added by neel
    //assign the true reputation of sellers
    //protected double[] m_SellersTrueRep;

    protected HashMap<Seller,Double> m_SellersTrueRep;

    protected int Day;
    //[day][all seller] sales
    //protected int[][] m_DailySales = null;



  //  protected HashMap<Seller,Integer> Seller_sales = new HashMap();
  //  protected HashMap<Seller,Integer> Seller_day = new HashMap();

    //[day][target dishonest/honest seller]: (|real reputation - predict reputation|)
    //protected double[][] m_DailyRep = null;
  //  protected HashMap<Seller,Double> m_DailyRep = new HashMap(); // based on the targeted honest/dishonest seller???
    //protected double[][] m_DailyRepDiff = null;
  //  protected HashMap<Seller,Double> m_DailyRepDiff = new HashMap();
    //protected double[][] m_DailyMCC = null;
  //  protected HashMap<Seller,Double> m_DailyMCC = new HashMap();

        protected Environment(){           // default constructor
            buyerList = new ArrayList();
            sellerList = new ArrayList();
            transactionList = new ArrayList();

        m_SellersTrueRating = new HashMap();
        m_SellersTrueRep = new HashMap();
        Day =0;
         }
        Environment(ArrayList<Buyer> buyerList,ArrayList<Seller> sellerList, ArrayList<Transaction> transactionList){
            this.buyerList = buyerList;
            this.sellerList = sellerList;               // maybe from the setUpEnvironment() under CentralAuthority
            this.transactionList = transactionList;     //or else we can invoke each of the set methods below to initialize
        }
        public void setDay(int day){
            this.Day = day;
        }
        public int getDay(){
            return this.Day;
        }

        protected void parameterSetting(String attackName, String defenseName){

        //set the number of dishonest/honest buyers
        int db = Parameter.NO_OF_DISHONEST_BUYERS;
        int hb = Parameter.NO_OF_HONEST_BUYERS;
        Parameter.NO_OF_DISHONEST_BUYERS = (db < hb) ? db : hb;
        Parameter.NO_OF_HONEST_BUYERS = (db > hb) ? db : hb;
        //setting the attack model
        if(Parameter.includeSybil(attackName)){
            Parameter.NO_OF_DISHONEST_BUYERS = (db > hb) ? db : hb;
            Parameter.NO_OF_HONEST_BUYERS = (db < hb) ? db : hb;
        }

        //get the setting from the public file <Para.java>
        m_Numdays = Parameter.NO_OF_DAYS;
        m_NumBuyers = Parameter.NO_OF_DISHONEST_BUYERS + Parameter.NO_OF_HONEST_BUYERS;
        m_NumSellers = Parameter.NO_OF_DISHONEST_SELLERS + Parameter.NO_OF_HONEST_SELLERS;

        //    m_SellersTrueRating = new double[m_NumSellers];
    //   m_SellersTrueRep = new double[m_NumSellers];

   //     m_SellersTrueRating = new HashMap();
   //     m_SellersTrueRep = new HashMap();

        //statistic of results
         Day = 0;
    //    m_DailySales = new int[m_Numdays + 1][m_NumSellers];

    //     m_DailyRep = new double[m_Numdays + 1][2];         NO NEED TO INTIALIZE SINCE WE ARE
   //     m_DailyRepDiff = new double[m_Numdays + 1][2];      USING HASHMAPS...WHICH CAN BE DYNAMICALLY
   //     m_DailyMCC = new double[m_Numdays + 1][2];          FILLED!

          m_AttackName = new String(attackName);
          m_DefenseName = new String(defenseName);

        System.out.println("entered environment");
        } // parameterSetting()

            public Instances initialInstancesHeader(){

        FastVector attInfo = new FastVector();

        //attribute include: [day, p_id, buyer_id, buyer_is_honest, seller_id, seller_is_honest, rating]
        //for day information
        attInfo.addElement(new Attribute(Parameter.dayString));         //for time information, real

        //for buyer/advisor information, string, because the whitewashing problem
        FastVector attBuyer = new FastVector();
        if(Parameter.includeWhitewashing()){
            //more buyer in database
            m_NumBuyers = m_NumBuyers + Parameter.NO_OF_DAYS * Parameter.NO_OF_DISHONEST_BUYERS;
            for(int i = 0; i < m_NumBuyers; i++){
                String str = "b" + Integer.toString(i);
                attBuyer.addElement(str);
            }
        }else{
            for(int i = 0; i < m_NumBuyers; i++){
                String str = "b" + Integer.toString(i);
                attBuyer.addElement(str);
            }
        }
        attInfo.addElement(new Attribute(Parameter.buyerIdString, attBuyer));

        //for buyer/advisor dishonest/honest
        FastVector attbuyerHonest = new FastVector();
        attbuyerHonest.addElement(Parameter.agent_dishonest);                               //[dishonest, honest]
        attbuyerHonest.addElement(Parameter.agent_honest);
        attInfo.addElement(new Attribute(Parameter.buyerHonestyString, attbuyerHonest));

        //for sellers id, nominal
        FastVector attSeller = new FastVector();                //for seller information, nominal
        for(int i = 0; i < m_NumSellers; i++){
            String str = "s" + Integer.toString(i);
            attSeller.addElement(str);
        }
        attInfo.addElement(new Attribute(Parameter.sellerIdString, attSeller));

        //for seller dishonest/honest
//      FastVector attSellerHonest = new FastVector();
//      attSellerHonest.addElement(Parameter.agent_dishonest);
//      attSellerHonest.addElement(Parameter.agent_honest);
//      attInfo.addElement(new Attribute(Parameter.m_sHonestStr, attSellerHonest));
        //for seller dishonest/honest = true rating, more general type is real according to rating type
        attInfo.addElement(new Attribute(Parameter.sellerHonestyString));

        //for rating, nominal
//      if(Parameter.RATING_TYPE.compareTo("binary") == 0){
//          FastVector attRating = new FastVector();                                       //for rating information, nominal
//          for(int i = 0; i < Parameter.RATING_BINARY.length; i++){
//              attRating.addElement(Integer.toString(Parameter.RATING_BINARY[i]));
//          }
//          attInfo.addElement(new Attribute(Parameter.m_ratingStr, attRating));
//      }
        //for rating nominal, real
        attInfo.addElement(new Attribute(Parameter.ratingString));

        String instsName = new String("eCommerce.arff");
        Instances header = new Instances(instsName, attInfo, m_Numdays * (m_NumBuyers));
        //set the class index
//      header.setSellerIndex(header.numAttributes() - 1);

        return header;
    }

        public void assignTruth(){


         System.out.println("enters assign truth");
         System.out.println("is seller list empty?  "+this.sellerList.isEmpty());
        //assign the true rating for sellers
        if(Parameter.RATING_TYPE.compareTo("binary") == 0){
          for(int i = 0; i < Parameter.NO_OF_DISHONEST_SELLERS; i++){
              m_SellersTrueRating.put(this.sellerList.get(i),0.0);
          }

           /* for(int i = 0; i < Parameter.NO_OF_DISHONEST_SELLERS; i++){    COMMENTED BY NEEL
                //dishonest seller, rating = -1
                m_SellersTrueRating[i] = Parameter.RATING_BINARY[0];
                m_SellersTrueRep[i] = 0.0;
            }*/

          for(int i = Parameter.NO_OF_DISHONEST_SELLERS; i < m_NumSellers; i++){
              m_SellersTrueRating.put(this.sellerList.get(i),1.0);
          }

        /*  for(int i = Parameter.NO_OF_DISHONEST_SELLERS; i < m_NumSellers; i++){   COMMENTED BY NEEL
                //honest seller, rating = 1
                m_SellersTrueRating[i] = Parameter.RATING_BINARY[2];
                m_SellersTrueRep[i] = 1.0;
            }*/

        }
        else if(Parameter.RATING_TYPE.equalsIgnoreCase("multinomial")){

        int interval = m_NumSellers / (Parameter.RATING_MULTINOMINAL.length - 1);
            double[] trueRep = new double[]{0, 0.25, 0.5, 0.75, 1.0};
            int halfPos = Parameter.RATING_MULTINOMINAL.length / 2;
            for(int i = 0; i < m_NumSellers; i++){
                int ratingIdx = i / interval;
                if(ratingIdx >= halfPos)ratingIdx++;
                //[1..1, 2...,2, 4...4, 5...5]

                m_SellersTrueRating.put(this.sellerList.get(i),(double)Parameter.RATING_MULTINOMINAL[ratingIdx]); //need to check the casting...not sure!!
                m_SellersTrueRep.put(this.sellerList.get(i),trueRep[ratingIdx]);

                // m_SellersTrueRating[i] = Parameter.RATING_MULTINOMINAL[ratingIdx];
               // m_SellersTrueRep[i] = trueRep[ratingIdx];
            } //for loop


         /*  else if(Parameter.RATING_TYPE.compareTo("multinominal") == 0){
            int interval = m_NumSellers / (Parameter.RATING_MULTINOMINAL.length - 1);
            double[] trueRep = new double[]{0, 0.25, 0.5, 0.75, 1.0};
            int halfPos = Parameter.RATING_MULTINOMINAL.length / 2;
            for(int i = 0; i < m_NumSellers; i++){
                int ratingIdx = i / interval;
                if(ratingIdx >= halfPos)ratingIdx++;
                //[1..1, 2...,2, 4...4, 5...5]
                m_SellersTrueRating[i] = Parameter.RATING_MULTINOMINAL[ratingIdx];
                m_SellersTrueRep[i] = trueRep[ratingIdx];
            } */
        }

        else if(Parameter.RATING_TYPE.equalsIgnoreCase("real")){

            for(int i = 0; i < m_NumSellers; i++){
                if(i < m_NumSellers/2){
                    double interval = (Parameter.m_omega[0] - Parameter.RATING_REAL[0]) / (m_NumSellers/2 - 1);
                    m_SellersTrueRating.put(this.sellerList.get(i),(i*interval));
                    //m_SellersTrueRating[i] = i * interval;
                    m_SellersTrueRep.put(this.sellerList.get(i),(i*interval));
                    //m_SellersTrueRep[i] = i * interval;
                } else{
                    double interval = (Parameter.RATING_REAL[1] - Parameter.m_omega[1]) / (m_NumSellers/2 - 1);
                    m_SellersTrueRating.put(this.sellerList.get(i),(Parameter.m_omega[1] + (i - m_NumSellers/2) * interval));
                    //m_SellersTrueRating[i] = Parameter.m_omega[1] + (i - m_NumSellers/2) * interval;
                    m_SellersTrueRep.put(this.sellerList.get(i),(Parameter.m_omega[1] + (i - m_NumSellers/2) * interval));
                    // m_SellersTrueRep[i] = Parameter.m_omega[1] + (i - m_NumSellers/2) * interval;
                }
            }
        }

        /*else if(Parameter.RATING_TYPE.compareTo("real") == 0){
            for(int i = 0; i < m_NumSellers; i++){
                if(i < m_NumSellers/2){
                    double interval = (Parameter.m_omega[0] - Parameter.RATING_REAL[0]) / (m_NumSellers/2 - 1);
                    m_SellersTrueRating[i] = i * interval;
                    m_SellersTrueRep[i] = i * interval;
                } else{
                    double interval = (Parameter.RATING_REAL[1] - Parameter.m_omega[1]) / (m_NumSellers/2 - 1);
                    m_SellersTrueRating[i] = Parameter.m_omega[1] + (i - m_NumSellers/2) * interval;
                    m_SellersTrueRep[i] = Parameter.m_omega[1] + (i - m_NumSellers/2) * interval;
                }

            }
        }*/

        else{
            System.out.println("no such type of rating existent");
        }
    } // assignTruth()

        public void agentSetting(String attackName, String defenseName) throws ClassNotFoundException, NoSuchMethodException, SecurityException{

        System.out.println("enters agent Setting");

        int numBuyers = Parameter.NO_OF_DISHONEST_BUYERS + Parameter.NO_OF_HONEST_BUYERS;
        int numSellers = Parameter.NO_OF_DISHONEST_SELLERS+Parameter.NO_OF_HONEST_SELLERS;
       // int numSellers = getSellerListSize();

        //m_buyers = new Buyer[numBuyers];  do we need this??? We are using arraylists.
        //m_sellers = new Seller[numSellers];

        for(int i = 0; i < numBuyers; i++){
            int bid = i;
            if(bid < Parameter.NO_OF_DISHONEST_BUYERS || bid <= numBuyers){

                Buyer b = new Buyer();
                b.setId(bid);b.setAttackName(attackName);b.attackSetting(attackName);
                b.setIshonest(false);
              
              //  b.setListOfSellers(sellerList);
                buyerList.add(b);
                  b.setListOfBuyers(buyerList);
                //buyerList.add(new Buyer(bid,false,attackName));
                //m_buyers[i] = new Buyer(bid, false, attackName, m_eCommerce);
            } if(bid < Parameter.NO_OF_HONEST_BUYERS || bid <= numBuyers){
                Buyer b = new Buyer();
                b.setId(bid);b.setIshonest(true);b.setDefenseName(defenseName);b.defenseSetting(defenseName);
                buyerList.add(b);
                //buyerList.add(new Buyer(bid,true,defenseName));
                //m_buyers[i] = new Buyer(bid, true, defenseName, m_eCommerce);
            }

        }
        for(int k = 0; k < numSellers; k++){
            int sid = k;
            if(sid < Parameter.NO_OF_DISHONEST_SELLERS){

                Seller s = new Seller();
                s.setId(sid);s.setIshonest(false);
                sellerList.add(s);
                //sellerList.add(new Seller(bid,false));
                // m_sellers[k] = new Seller(sid, false, m_eCommerce);
            } else{

                Seller s = new Seller();
                s.setId(sid);s.setIshonest(true);
                sellerList.add(s);
               // sellerList.add(new Seller(bid,true));
                //m_sellers[k] = new Seller(sid, true, m_eCommerce);
            }
        }

        //set up the global information for buyers;
      //  for(int i = 0; i < numBuyers; i++){           DO WE NEED THIS?????
      //      m_buyers[i].setGlobalInformation(m_buyers, m_sellers);
      //  }

        //for the evolutionary algorithm
       /* if(Parameter.includeEA(m_defenseName) || Parameter.includeWMA(m_defenseName)){
            int tnType = 1;
            if(m_defenseName.equalsIgnoreCase("ea0")){
                tnType = 0;
            } else if(m_defenseName.equalsIgnoreCase("ea1")){
                tnType = 1;
            } else if(m_defenseName.equalsIgnoreCase("ea2")){
                tnType = 2;
            }
            for(int i = 0; i < numBuyers; i++){
                m_buyers[i].InitialTrustNetwork(tnType);
            }
            for(int i = 0; i < numBuyers; i++){
                int day = 0;
                m_buyers[i].resetWitness(day);
            }
        }*/

        System.out.println("seller list size "+sellerList.size());
    } //agentSetting
       public String getDefenseName(){
           return m_DefenseName;
       }

       public Environment getEnvironment(){
           return this;
       }


        public void AddBuyerToList(Buyer buyer){   //dynamic way of adding a buyer to the list
            buyerList.add(buyer);
        }
        public void AddSellerToList(Seller seller){  //dynamic way of adding seller to the list
            sellerList.add(seller);
        }
        public void AddTransactionToList(Transaction transaction){
            transactionList.add(transaction);
        }
        public int getBuyerListSize(){
            return buyerList.size();
        }
        public int getSellerListSize(){
            return sellerList.size();
        }
        public int getTransactionListSize(){
            return transactionList.size();
        }
        public void setBuyerList(ArrayList<Buyer> buyerList){
            this.buyerList = buyerList;
        }
        public void setSellerList(ArrayList<Seller> sellerList){
            this.sellerList = sellerList;
        }
        public void setTransactionList(ArrayList<Transaction> transactionList){
            this.transactionList = transactionList;
        }
        public ArrayList<Buyer> getBuyerList(){
            return buyerList;
        }
        public ArrayList<Seller> getSellerList(){
            return sellerList;
        }
        public ArrayList<Transaction> getTransactionList(){
            return transactionList;
        }

       public abstract void eCommerceSetting(String attackName,String defenseName);
        abstract Instances generateInstances();

	abstract void importConfigSettings();
	abstract void createEnvironment();
	abstract void saveWekaInstances();

}//class

