package main;
 
import java.text.DecimalFormat;
import java.util.*;
import java.io.*;
import java.lang.*;
 
import environment.Environment;
 
public class CentralAuthority {
 
    private Environment env;
    private String m_defenseName;
    private String m_attackName;
    private ArrayList<Buyer> m_buyers;
    private ArrayList<Seller> m_sellers
     
    //sellers sales, reputation, reputation difference per day
    private int[][] m_dailySales;
    private double[][] m_dailyRep;
    private double[][] m_dailyRepDiff;
    //Defense model cost time per day
    private double[] defenseTime_day;
    private double[][] m_dailyAvgWeights;
    private double[][] m_dailyMCC;
 
    public CentralAuthority(){          
            m_dailySales = new int[Parameter.NO_OF_DAYS + 1][Parameter.NO_OF_DISHONEST_SELLERS + Parameter.NO_OF_HONEST_SELLERS];
            m_dailyRep  = new double[Parameter.NO_OF_DAYS + 1][2];
            m_dailyMCC= new double[Parameter.NO_OF_DAYS + 1][2];
            m_dailyRepDiff  = new double[Parameter.NO_OF_DAYS + 1][2];
            defenseTime_day = new double[Parameter.NO_OF_DAYS + 1];
            m_dailyAvgWeights = new double[Parameter.NO_OF_DAYS + 1][2];
    }   
         
      
    public void setUpEnvironment(String attackName, String defenseName){
        env.createEnvironment(attackName, defenseName);
        env.agentSetting(attackName, defenseName);
    }
      
    public ArrayList simulateEnvironment(String attackName, String defenseName, boolean dailyPrint) throws ClassNotFoundException, NoSuchMethodException, SecurityException{            
             
        //step 1: initialize initialize the e commerce
        m_attackName = new String(attackName);
        m_defenseName = new String(defenseName);
        setUpEnvironment(attackName, defenseName);      
        ArrayList transList = new ArrayList();
        //TODO discuss about the name specification
        m_buyers=env.getBuyerList();
        m_sellers=env.getSellerList();
         
        //Day 0 is the eve of Day 1 (only for agents' decision making), transactions start on Day 1             
        for (int day = 0; day <= Parameter.NO_OF_DAYS; day++){                                                   
                     
            if (day != 0){
                for (int count = 0; count<m_buyers.size();count++){
                    m_buyers.get(count).setCredits(m_buyers.get(count).getCredits()+Parameter.CREDITS_PER_TURN);
                }
            }
                     
            for (int a = 0; a<PseudoRandom.randInt(1, Parameter.transaction_limit);a++){
                DecimalFormat roundoff = new DecimalFormat("#.##");
                Transaction trans = new Transaction(day);               
                double cost = trans.getProductQty()*trans.getPrice();
                cost = Double.valueOf(roundoff.format(cost));
                if (cost>m_buyers.get(trans.getBuyerID()).getCredits()){
                    trans.setRemarks("Transaction Failed: Buyer not enough Credit");
                } else {
                    trans.setRemarks("Transaction Successful");
                    m_buyers.get(trans.getBuyerID()).setCredits(m_buyers.get(trans.getBuyerID()).getCredits()-cost);
                }
                transList.add(trans);
            }
                     
            //step 2: Attack model (dishonest buyers)               
            attack(day);            
                     
            //step 3: Defense model (honest buyers)
            long defensetimeStart = new Date().getTime();
            defense(day);   
            long defensetimeEnd = new Date().getTime();
             
            //TODO how to store transaction
            defenseTime_day[day] = (-defensetimeStart + defensetimeEnd) / 1000.0;               
            m_dailySales = m_eCommerce.getDailySales(); 
            m_dailyRep[day] = m_eCommerce.getDailyReputation();
            m_dailyRepDiff[day] = m_eCommerce.getDailyReputationDiff();
            m_dailyMCC[day] = m_eCommerce.getDailyMCC();
            env.setDay(day); //update to next day
            avgerWeights(day);
            if(dailyPrint){
                //print the transactions for different sellers
                System.out.print("Day " + day + ": ");                  
                System.out.print("   |sellers' transactions|: ");
                for (int i = 0; i < Parameter.NO_OF_HONEST_SELLERS   + Parameter.NO_OF_DISHONEST_SELLERS; i++) {
                    if(i == Parameter.NO_OF_DISHONEST_SELLERS){
                        System.out.print(", ");
                    }                       
                    System.out.print(" " + m_dailySales[day][i]);                   
                }       
                System.out.print("   |rep MAE|: " + m_dailyRep[day][0] + "  " + m_dailyRep[day][1]);
                System.out.print("   |repDiff MAE|: " + m_dailyRepDiff[day][0] + "  " + m_dailyRepDiff[day][1]);
                System.out.println(" avgWeights: " + m_dailyAvgWeights[day][0] + "  " + m_dailyAvgWeights[day][1]);
            }               
        }
     
        return transList;
    }
     
    private void attack(int day){
         
        int numBuyers = Parameter.NO_OF_DISHONEST_BUYERS + Parameter.NO_OF_HONEST_BUYERS;
        //Attack model (dishonest buyers), give rating/ perform attack              
        for(int i = 0; i < numBuyers; i++){
            int bid = i;
            if(m_buyers.get(bid).getIsHonest() == false){
                m_buyers.get(bid).giveRating(day);
            }
        }
        for(int i = 0; i < numBuyers; i++){
            int bid = i;
            if(m_buyers.get(bid).getIsHonest() == false){
                m_buyers.get(bid).perform_model(day);
            }
        }
    }
     
    private void defense(int day){
         
        int numBuyers = Parameter.NO_OF_DISHONEST_BUYERS + Parameter.NO_OF_HONEST_BUYERS;
        //Attack model (dishonest buyers), give rating/ perform defense             
        for(int i = 0; i < numBuyers; i++){
            int bid = i;
            if(m_buyers.get(bid).getIsHonest() == true){
                m_buyers.get(bid).giveRating(day);
            }
        }
        for(int i = 0; i < numBuyers; i++){
            int bid = i;
            if(m_buyers.get(bid).getIsHonest() == true){
                m_buyers.get(bid).perform_model(day);
            }
        }
    }
     
    private void avgerWeights(int day){
         
        int db = Parameter.NO_OF_DISHONEST_BUYERS;
        int hb = Parameter.NO_OF_HONEST_BUYERS; 
        if(Parameter.includeWhitewashing()){
            db = db + (day) * db;
        }       
         
        //these code for trust models: trustworthiness for local/partial advisors;
        if(Parameter.includeWMA(m_defenseName) || Parameter.includeEA(m_defenseName)){
            int numDA = 0; //number of dishonest advisors;
            int numHA = 0; //number of honest advisors;
            for(int i = Parameter.NO_OF_DISHONEST_BUYERS; i < Parameter.NO_OF_DISHONEST_BUYERS + hb; i++){
                int bid = i;
                ArrayList<> weights_BA;
                weights_BA.add(m_buyers.get(bid).getTrusts());
                ArrayList <Buyer> advisors = m_buyers.get(bid).getAdvisors();             
                for(int j = 0; j < advisors.size(); j++){                    
                    int aid = advisors.get(j);
                    if(aid == bid)continue;
                    if(aid < Parameter.NO_OF_DISHONEST_BUYERS || aid >= Parameter.NO_OF_DISHONEST_BUYERS + hb){
                        m_dailyAvgWeights[day][0] += weights_BA.get(aid);
                        numDA++;
                    } else{
                        m_dailyAvgWeights[day][1] += weights_BA.get(aid);
                        numHA++;
                    }
                }               
            }
            if (numDA != 0) {
                m_dailyAvgWeights[day][0] /= (numDA);
            }           
            if (numHA != 0) {
                m_dailyAvgWeights[day][1] /= (numHA);       
            }
        }
        else{
            //these code for trust models: trustworthiness for all advisors;
            int numDA = 0;
            int numHA = 0;
            for(int i = Parameter.NO_OF_DISHONEST_BUYERS; i < Parameter.NO_OF_DISHONEST_BUYERS + hb; i++){
                int bid = i;                
                for (int k = 0; k < 2; k++) {                
                    int sid = Parameter.TARGET_DISHONEST_SELLER;
                    if (k == 1)sid = Parameter.TARGET_HONEST_SELLER;
                    ArrayList <> SaverTA;
                    SaverTA.add(m_buyers.get(bid).getAverageTrusts(sid));
                    if(SaverTA.get(0) >= 0){//dishonest advisors
                        m_dailyAvgWeights[day][0] += SaverTA[0];
                        numDA++;
                    }
                    if(SaverTA.get(1) >= 0){//dishonest advisors
                        m_dailyAvgWeights[day][1] += SaverTA[1];
                        numHA++;
                    }
                }   
            }       
 
            if (numDA != 0) {
                m_dailyAvgWeights[day][0] /= (numDA);
            }           
            if (numHA != 0) {
                m_dailyAvgWeights[day][1] /= (numHA);       
            }   
        }       
         
    }
     
    public void evaluateDefenses(ArrayList<String> defenseNames, ArrayList<String> attackNames, String evaluateName) throws ClassNotFoundException, NoSuchMethodException, SecurityException{
         
        int runtimes = Parameter.NO_OF_RUNTIMES;                    //runtimes =  50
         
        //output the result: [|transactions|, time]
        double[][][][] results = new double[runtimes][defenseNames.length][attackNames.length][2];
        for(int i = 0; i < runtimes; i++){
            for(int j = 0; j < defenseNames.size(); j++){            
                for(int k = 0; k < attackNames.size(); k++){                 
                    System.err.print("  runtimes = " + i + ",   defense = " + defenseNames[j] + ",   attack = " + attackNames[k]);
                                     
                    long start = new Date().getTime();
                     // [true / false] means print/not daily result     
                    transList = simulateEnvironment(attackNames.get(k), defenseNames.get(j), false); 
                    long end = new Date().getTime();
                    System.err.println("   time =  " + (end - start)/1000.0 + " s");
                    //TODO ADD LATER: results[i][j][k] = generate_report(i); //report include the run count                 
                }
            }           
        }
     
        //TODO ADD LATER: getStatistic(results, defenseNames, attackNames, evaluateName);
 
    }
 
      
     public void showOutput(){
          
     }
      
     public void exportToDB(){
          
     }
      
     public void importFromDB(){
          
     }
      
     public void getCentralReputation(){
          
     }
      
     public void displaySuggestions(){
          
     }
      
      
}