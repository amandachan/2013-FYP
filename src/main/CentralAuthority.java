package main;

import agent.*;

import java.text.DecimalFormat;
import java.util.*;
import java.io.*;
import java.lang.*;

import defenses.eBay;
import distributions.*;
import environment.*;
import main.*;

public class CentralAuthority {

	private Environment env;
	private String m_defenseName;
	private String m_attackName;
	private ArrayList<Buyer> m_buyers = new ArrayList<Buyer>();
	private ArrayList<Seller> m_sellers = new ArrayList<Seller>();
	private BankBalance bankbalance ;
	ArrayList<Double> defenseTime_day= new ArrayList<Double>();
	ArrayList<Double> honest_avgWt;
	ArrayList<Double> dishonest_avgWt;
	ArrayList transList;
	private int noOfTransHS =0, noOfTransDHS =0;


	public CentralAuthority(){          
		honest_avgWt = new ArrayList();
		dishonest_avgWt = new ArrayList();
		env = new EnvironmentR();
		for(int i=0; i<Parameter.NO_OF_DAYS; i++){
			defenseTime_day.add(i,0.0);
			dishonest_avgWt.add(i,0.0);
			honest_avgWt.add(i,0.0);

		}
		bankbalance = new BankBalance();

	}




	public void setUpEnvironment(String attackName, String defenseName)throws Exception{
		env.eCommerceSetting(attackName, defenseName);
	}

	public ArrayList simulateEnvironment(int noOfRuns, String attackName, String defenseName, boolean dailyPrint) throws ClassNotFoundException, NoSuchMethodException, SecurityException,Exception{


		//.out.println("enters simulateEnvironment "+attackName+"  "+defenseName);

		m_attackName = new String(attackName);
		m_defenseName = new String(defenseName);
		setUpEnvironment(attackName, defenseName);   

		transList = new ArrayList();
		m_buyers=env.getBuyerList();
		m_sellers=env.getSellerList();

		for (int i=0; i<m_sellers.size(); i++){
			env.getPositiveRatings().put(m_sellers.get(i), 0.0);
			env.getNegativeRatings().put(m_sellers.get(i), 0.0);
		}
		for (int day = 0; day < 10; day++){                                                   
			//step 2: Attack model (dishonest buyers)               
			//			attack(day);            
			System.out.println(day);
			//step 3: Defense model (honest buyers)
			long defensetimeStart = new Date().getTime();
			defense(day);   
			long defensetimeEnd = new Date().getTime();

			defenseTime_day.set(day,(-defensetimeStart + defensetimeEnd) / 1000.0 );

			env.setDay(day); //update to next day
			avgerWeights(day);

			bankbalance.updateDailyBankBalance(day, m_buyers);
			bankbalance.printDailyBalance(day);

			if (day%5==0){
				try {


					File file = new File(m_defenseName + m_attackName + "Trans.txt");

					// if file doesnt exists, then create it
					if (!file.exists()) {
						file.createNewFile();
					}

					int honesttrans = env.getSellerList().get(Parameter.TARGET_HONEST_SELLER).getSales();
					int dishonestrans = env.getSellerList().get(Parameter.TARGET_DISHONEST_SELLER).getSales();


					FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
					BufferedWriter bw = new BufferedWriter(fw);
					bw.write(day + " | " + dishonestrans + " | " + honesttrans + "\n");
					bw.close();


				} catch (IOException e) {
					e.printStackTrace();
				}
			}


			//			System.out.println("BEST BUYER IS: " + s + " banlance: " + bestBuyer);
			//						System.out.println("MAE for day " + day + " is " + env.getDailyRepDiff().get(0) + " " + env.getDailyRepDiff().get(1));
			//						System.out.println("MCC for day " + day + " is " + env.getMcc().getDailyMCC(day).get(0) + " " + env.getMcc().getDailyMCC(day).get(1));
			//						System.out.println("FNR for day " + day + " is " + env.getMcc().getDailyFNR(day).get(0) + " " + env.getMcc().getDailyFNR(day).get(1));
			//						System.out.println("FPR for day " + day + " is " + env.getMcc().getDailyFPR(day).get(0) + " " + env.getMcc().getDailyFPR(day).get(1));
			//						System.out.println("TPR for day " + day + " is " + env.getMcc().getDailyTPR(day).get(0) + " " + env.getMcc().getDailyTPR(day).get(1));
			//						System.out.println("Accuracy for day " + day + " is " + env.getMcc().getDailyAcc(day).get(0) + " " + env.getMcc().getDailyAcc(day).get(1));
			//						System.out.println("Precision for day " + day + " is " + env.getMcc().getDailyPrec(day).get(0) + " " + env.getMcc().getDailyPrec(day).get(1));
			//						System.out.println("F-Measure for day " + day + " is " + env.getMcc().getDailyF(day).get(0) + " " + env.getMcc().getDailyF(day).get(1));
			//						System.out.println();
			//
			//						try {
			//			
			//							String file1name = m_defenseName + m_attackName + noOfRuns + "MAE";
			//							String file2name = m_defenseName + m_attackName + noOfRuns + "MCC";
			//							String file3name = m_defenseName + m_attackName + noOfRuns + "FNR";
			//							String file4name = m_defenseName + m_attackName + noOfRuns + "FPR";
			//							String file5name = m_defenseName + m_attackName + noOfRuns + "TPR";
			//							String file6name = m_defenseName + m_attackName + noOfRuns + "Accuracy";
			//							String file7name = m_defenseName + m_attackName + noOfRuns + "Precision";
			//							String file8name = m_defenseName + m_attackName + noOfRuns + "F-Measure";
			//			
			//							File file1 = new File(file1name + ".txt");
			//							File file2 = new File(file2name + ".txt");
			//							File file3 = new File(file3name + ".txt");
			//							File file4 = new File(file4name + ".txt");
			//							File file5 = new File(file5name + ".txt");
			//							File file6 = new File(file6name + ".txt");
			//							File file7 = new File(file7name + ".txt");
			//							File file8 = new File(file8name + ".txt");
			//			
			//			
			//							// if file doesnt exists, then create it
			//							if (!file1.exists()) {
			//								file1.createNewFile();
			//							}
			//							if (!file2.exists()) {
			//								file2.createNewFile();
			//							}
			//							if (!file3.exists()) {
			//								file3.createNewFile();
			//							}
			//							if (!file4.exists()) {
			//								file4.createNewFile();
			//							}
			//							if (!file5.exists()) {
			//								file5.createNewFile();
			//							}
			//							if (!file6.exists()) {
			//								file6.createNewFile();
			//							}
			//							if (!file7.exists()) {
			//								file7.createNewFile();
			//							}
			//							if (!file8.exists()) {
			//								file8.createNewFile();
			//							}
			//			
			//							FileWriter fw1 = new FileWriter(file1.getAbsoluteFile(),true);
			//							FileWriter fw2 = new FileWriter(file2.getAbsoluteFile(),true);
			//							FileWriter fw3 = new FileWriter(file3.getAbsoluteFile(),true);
			//							FileWriter fw4 = new FileWriter(file4.getAbsoluteFile(),true);
			//							FileWriter fw5 = new FileWriter(file5.getAbsoluteFile(),true);
			//							FileWriter fw6 = new FileWriter(file6.getAbsoluteFile(),true);
			//							FileWriter fw7 = new FileWriter(file7.getAbsoluteFile(),true);
			//							FileWriter fw8 = new FileWriter(file8.getAbsoluteFile(),true);
			//			
			//							BufferedWriter bw1 = new BufferedWriter(fw1);
			//							bw1.write(env.getDailyRepDiff().get(0) + " " + " " + env.getDailyRepDiff().get(1) + "\n");
			//							bw1.close();
			//			
			//							BufferedWriter bw2 = new BufferedWriter(fw2);
			//							bw2.write(env.getMcc().getDailyMCC(day).get(0) + " " + " " + env.getMcc().getDailyMCC(day).get(1) + "\n");
			//							bw2.close();
			//			
			//							BufferedWriter bw3 = new BufferedWriter(fw3);
			//							bw3.write(env.getMcc().getDailyFNR(day).get(0) + " " + " " + env.getMcc().getDailyFNR(day).get(1) + "\n");
			//							bw3.close();
			//			
			//							BufferedWriter bw4 = new BufferedWriter(fw4);
			//							bw4.write(env.getMcc().getDailyFPR(day).get(0) + " " + " " + env.getMcc().getDailyFPR(day).get(1) + "\n");
			//							bw4.close();
			//			
			//							BufferedWriter bw5 = new BufferedWriter(fw5);
			//							bw5.write(env.getMcc().getDailyTPR(day).get(0) + " " + " " +  env.getMcc().getDailyTPR(day).get(1) + "\n");
			//							bw5.close();
			//			
			//							BufferedWriter bw6 = new BufferedWriter(fw6);
			//							bw6.write(env.getMcc().getDailyAcc(day).get(0) + " " + " " + env.getMcc().getDailyAcc(day).get(1) + "\n");
			//							bw6.close();
			//			
			//							BufferedWriter bw7 = new BufferedWriter(fw7);
			//							bw7.write(env.getMcc().getDailyPrec(day).get(0) + " " + " " + env.getMcc().getDailyPrec(day).get(1) + "\n");
			//							bw7.close();
			//			
			//							BufferedWriter bw8 = new BufferedWriter(fw8);
			//							bw8.write(env.getMcc().getDailyF(day).get(0) + " " + " " + env.getMcc().getDailyF(day).get(1) + "\n");
			//							bw8.close();
			//			
			//						} catch (IOException e) {
			//							e.printStackTrace();
			//						}
			//			

		}


		//		try {
		//
		//			String filename = m_defenseName + m_attackName + "Overall";
		//
		//			File file = new File(filename + ".txt");
		//
		//			// if file doesnt exists, then create it
		//			if (!file.exists()) {
		//				file.createNewFile();
		//						FileWriter fw = new FileWriter(file.getAbsoluteFile(),true);
		//						BufferedWriter bw = new BufferedWriter(fw);
		//						bw.write("Defense Name: " + m_defenseName + " " + " " + "Attack Name: " + m_attackName + "\n");
		//						bw.write("Results of evaluation metrics on day " + env.getDay() + "\n\n");
		//						bw.write(" ______________________________________________________________________ \n");
		//						bw.write("|   Evaluation Metrics   " + "|   Negative Ratings   " + "|   Positive Ratings   |\n");
		//						bw.write("|________________________|______________________|______________________|\n");
		//		
		//						bw.close();
		//
		//			}
		//
		//
		//					FileWriter fw = new FileWriter(file.getAbsoluteFile(),true);
		//					int day =  Parameter.NO_OF_DAYS-1;
		//					BufferedWriter bw = new BufferedWriter(fw);
		//					bw.write("|           MAE          " + "|  " + formatResults(env.getDailyRepDiff().get(0))  + " |  " + formatResults(env.getDailyRepDiff().get(1)) +" |\n");
		//					bw.write("|________________________|______________________|______________________|\n");
		//		
		//					bw.write("|           MCC          " + "|  " + formatResults(env.getMcc().getDailyMCC(day).get(0))  + " |  " + formatResults(env.getMcc().getDailyMCC(day).get(1)) +" |\n");
		//					bw.write("|________________________|______________________|______________________|\n");
		//		
		//					bw.write("|           FNR          " + "|  " + formatResults(env.getMcc().getDailyFNR(day).get(0))  + " |  " + formatResults(env.getMcc().getDailyFNR(day).get(1)) +" |\n");
		//					bw.write("|________________________|______________________|______________________|\n");
		//		
		//					bw.write("|           FPR          " + "|  " + formatResults(env.getMcc().getDailyFPR(day).get(0))  + " |  " + formatResults(env.getMcc().getDailyFPR(day).get(1)) +" |\n");
		//					bw.write("|________________________|______________________|______________________|\n");
		//		
		//					//bw.write("|           TPR          " + "|  " + formatResults(env.getMcc().getDailyTPR(day).get(0))  + " |  " + formatResults(env.getMcc().getDailyTPR(day).get(1)) +" |\n");
		//				//	bw.write("|________________________|______________________|______________________|\n");
		//		
		//					bw.write("|        Accuracy        " + "|  " + formatResults(env.getMcc().getDailyAcc(day).get(0))  + " |  " + formatResults(env.getMcc().getDailyAcc(day).get(1)) +" |\n");
		//					bw.write("|________________________|______________________|______________________|\n");
		//		
		//					bw.write("|        Precision       " + "|  " + formatResults(env.getMcc().getDailyPrec(day).get(0))  + " |  " + formatResults(env.getMcc().getDailyPrec(day).get(1)) +" |\n");
		//					bw.write("|________________________|______________________|______________________|\n");
		//		
		//					bw.write("|        F-Measure       " + "|  " + formatResults(env.getMcc().getDailyF(day).get(0))  + " |  " + formatResults(env.getMcc().getDailyF(day).get(1)) +" |\n");
		//					bw.write("|________________________|______________________|______________________|\n");
		//		
		//					bw.close();
		//
		//
		//		} catch (IOException e) {
		//			e.printStackTrace();
		//		}
		//		String filename = m_defenseName + m_attackName + "Overall.txt";
		//
		//		BufferedReader in = new BufferedReader(new FileReader(filename));
		//		String line;
		//		while((line = in.readLine()) != null)
		//		{
		//		    System.out.println(line);
		//		}


		return transList;
	}


	private String formatResults(double result){
		if(result>=0){
			DecimalFormat df = new DecimalFormat("0.00000000000000000");			
			return df.format(result);
		}
		else
		{
			DecimalFormat df = new DecimalFormat("0.0000000000000000");			
			return df.format(result);
		}
	}

	private void attack(int day){

		int numBuyers = Parameter.NO_OF_DISHONEST_BUYERS + Parameter.NO_OF_HONEST_BUYERS;

		m_buyers = env.getBuyerList();



		//System.out.println(m_buyers.get(index))
		for(int i = 0; i < numBuyers; i++){
			int bid = i;

			if(m_buyers.get(bid).isIshonest() == false){
				if(m_attackName.equalsIgnoreCase("Whitewashing") || m_attackName.equalsIgnoreCase("Sybil_Whitewashing")){
					if (day > 0) {
						//System.out.println(bid);

						bid = (Parameter.NO_OF_DISHONEST_BUYERS + Parameter.NO_OF_HONEST_BUYERS) + (day - 1) * Parameter.NO_OF_DISHONEST_BUYERS + bid;			
						bid %=100;
						//System.out.println(bid);
					}
				}
				m_buyers.get(bid).addTransaction(day);
			}
		}



	}

	private void defense(int day){

		int numBuyers = Parameter.NO_OF_DISHONEST_BUYERS + Parameter.NO_OF_HONEST_BUYERS;

		if(env instanceof EnvironmentS){
			for(int i = 0; i < numBuyers; i++){
				int bid = i;
				if(m_buyers.get(bid).isIshonest() == true){
					m_buyers.get(bid).addTransaction(day);
				}
			}
		}

		else if (env instanceof EnvironmentR){
			int count=0;
			while(count != env.getUserID().size() && count != env.getProductID().size() && count != env.getScore().size()){
				
				if(env.getDate().get(count) == env.getDay()-1){
					Buyer b = env.getBuyerList().get(env.getStringToBuyer().get(env.getUserID().get(count)));
					Seller s = env.getSellerList().get(env.getStringToSeller().get(env.getProductID().get(count)));
					b.addTransaction(day, s);
				}
				count++;
			}
		}

	}

	private void avgerWeights(int day){

		int db = Parameter.NO_OF_DISHONEST_BUYERS;
		int hb = Parameter.NO_OF_HONEST_BUYERS; 


		//these code for trust models: trustworthiness for local/partial advisors;
		if(Parameter.includeWMA(m_defenseName) || Parameter.includeEA(m_defenseName)){
			int numDA = 0; //number of dishonest advisors;
			int numHA = 0; //number of honest advisors
			honest_avgWt.set(day,(double)0);
			honest_avgWt.set(day, (double)0);


			for(int i = Parameter.NO_OF_DISHONEST_BUYERS; i < Parameter.NO_OF_DISHONEST_BUYERS + hb; i++){
				int bid = i;

				ArrayList<Double> weights_BA = new ArrayList<Double>();
				// weights_BA.add(m_buyers.get(bid).getTrusts());
				for (int j=0; j<m_buyers.get(bid).getTrusts().size(); j++){
					weights_BA.add(m_buyers.get(bid).getTrusts().get(j));
				}
				weights_BA.add(m_buyers.get(bid).getTrusts().get(i));
				ArrayList<Buyer> advisors = new ArrayList<Buyer>();
				// advisors.add(m_buyers.get(bid).getAdvisors());
				for (int j=0;j<m_buyers.get(bid).getAdvisors().size();j++){


					advisors.add(m_buyers.get(bid).getAdvisor(j));

				}
				for(int j = 0; j < advisors.size(); j++){                    
					int aid = advisors.get(j).getId(); double oldWt;
					if(aid == bid)continue;
					if(aid < Parameter.NO_OF_DISHONEST_BUYERS || aid >= Parameter.NO_OF_DISHONEST_BUYERS + hb){
						// m_dailyAvgWeights[day][0] += weights_BA.get(aid);

						oldWt = dishonest_avgWt.get(day);
						oldWt +=weights_BA.get(aid);
						dishonest_avgWt.set(day, oldWt);
						numDA++;

					} else{
						oldWt = honest_avgWt.get(day);
						oldWt +=weights_BA.get(aid);
						honest_avgWt.set(day, oldWt);
						numHA++;
					}
				}

			}
			if (numDA != 0) {
				//  double wt =  (dishonest_avgWt.get(day))/ numDA;
				dishonest_avgWt.set(day,(dishonest_avgWt.get(day)/ numDA));
				// m_dailyAvgWeights[day][0] /= (numDA);
			}           
			if (numHA != 0) {
				// m_dailyAvgWeights[day][1] /= (numHA);
				honest_avgWt.set(day,(honest_avgWt.get(day)/numHA));
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
					ArrayList <Double> SaverTA = new ArrayList<Double>();
					//  SaverTA.add(m_buyers.get(bid).getAverageTrusts(sid));

					double array1 [] =new double[m_buyers.get(bid).getAverageTrusts(sid).length]; //check
					array1 = m_buyers.get(bid).getAverageTrusts(sid); //check
					for(int j=0;j<array1.length;j++)  //check
						SaverTA.add(array1[j]);  //check

					if(SaverTA.get(0) >= 0){//dishonest advisors
						//System.out.println(dishonest_avgWt.get(day));
						dishonest_avgWt.set(day,dishonest_avgWt.get(day) +SaverTA.get(0));
						//m_dailyAvgWeights[day][0] += SaverTA[0];
						numDA++;
					}
					if(SaverTA.get(1) >= 0){//dishonest advisors
						honest_avgWt.set(day, honest_avgWt.get(day)+SaverTA.get(1));
						// m_dailyAvgWeights[day][1] += SaverTA[1];
						numHA++;
					}
				}   
			}       

			if (numDA != 0) {
				dishonest_avgWt.set(day,(dishonest_avgWt.get(day)/ numDA));
				//m_dailyAvgWeights[day][0] /= (numDA);
			}           
			if (numHA != 0) {
				honest_avgWt.set(day,(honest_avgWt.get(day)/ numDA));
				// m_dailyAvgWeights[day][1] /= (numHA);
			}   
		}       

	}

	public void evaluateDefenses(ArrayList<String> defenseNames, ArrayList<String> attackNames, String evaluateName) throws Exception,ClassNotFoundException, NoSuchMethodException, SecurityException{
		double averageMAEdh=0, averageMCCdh=0, averageFNRdh=0, averageFPRdh=0, averageTPRdh=0, averageAccdh=0, averagePrecdh = 0,averageFdh=0;
		double averageMAEh=0, averageMCCh=0, averageFNRh=0, averageFPRh=0, averageTPRh=0, averageAcch=0, averagePrech =0, averageFh=0;
		double averageRbosutness=0;
		int runtimes = Parameter.NO_OF_RUNTIMES;                    //runtimes =  50
		transList = new ArrayList();
		//output the result: [|transactions|, time]
		//      double[][][][] results = new double[runtimes][defenseNames.length][attackNames.length][2];
		//bankbalance = new BankBalance();

		for(int i = 0; i < 1; i++){
			//bankbalance = new BankBalance();

			//			for(int j = 0; j < defenseNames.size(); j++){            
			//				for(int k = 0; k < attackNames.size(); k++){                 
			//            System.err.print("  runtimes = " + i + ",   defense = " + defenseNames[j] + ",   attack = " + attackNames[k]);

			long start = new Date().getTime();
			// [true / false] means print/not daily result     
			transList = simulateEnvironment(i,attackNames.get(0), defenseNames.get(0), false);
			long end = new Date().getTime();
			System.err.println("   time =  " + (end - start)/1000.0 + " s");


			averageMAEdh += env.getDailyRepDiff().get(0);
			averageMAEh += env.getDailyRepDiff().get(1);
			averageMCCdh += env.getMcc().getDailyMCC(env.getDay()-1).get(0); 
			averageMCCh += env.getMcc().getDailyMCC(env.getDay()-1).get(1);
			averageFNRdh += env.getMcc().getDailyFNR(env.getDay()-1).get(0);
			averageFNRh += env.getMcc().getDailyFNR(env.getDay()-1).get(1);
			averageFPRdh += env.getMcc().getDailyFPR(env.getDay()-1).get(0); 
			averageFPRh += env.getMcc().getDailyFPR(env.getDay()-1).get(1);
			averageTPRdh += env.getMcc().getDailyTPR(env.getDay()-1).get(0);
			averageTPRh += env.getMcc().getDailyTPR(env.getDay()-1).get(1);
			averageAccdh += env.getMcc().getDailyAcc(env.getDay()-1).get(0);
			averageAcch += env.getMcc().getDailyAcc(env.getDay()-1).get(1);
			averagePrecdh += env.getMcc().getDailyPrec(env.getDay()-1).get(0); 
			averagePrech += env.getMcc().getDailyPrec(env.getDay()-1).get(1);
			averageFdh += env.getMcc().getDailyF(env.getDay()-1).get(0) ;
			averageFh += env.getMcc().getDailyF(env.getDay()-1).get(1);
			//int noOfTransHS =0, noOfTransDHS = 0;
			//			for(int k=0; k<env.getSellerList().size(); k++){
			//				if (k<Parameter.NO_OF_DISHONEST_SELLERS){
			//					if(env.getSellerList().get(k).getDailysales().get(env.getDay()-1)!=null){
			//						noOfTransDHS += env.getSellerList().get(k).getDailysales().get(env.getDay()-1);
			//					}
			//				}
			//				else{
			//					if(env.getSellerList().get(k).getDailysales().get(env.getDay()-1)!= null){
			//						noOfTransHS += env.getSellerList().get(k).getDailysales().get(env.getDay()-1);
			//					}
			//				}
			//			}
			//double result = noOfTransDHS - noOfTransHS;
			//noOfTransDHS =0; noOfTransHS=0;
			//	double theoricialBound1 = ((Parameter.NO_OF_DAYS-2) * Parameter.NO_OF_HONEST_BUYERS * Parameter.m_honestBuyerOntargetSellerRatio);
			//double theoricialBound2 = ((Parameter.NO_OF_DAYS) * Parameter.NO_OF_DISHONEST_BUYERS * Parameter.m_honestBuyerOntargetSellerRatio);
			//double theoricialBound = theoricialBound1 > theoricialBound2? theoricialBound1: theoricialBound2;
			//			if(m_attackName.contains("Sybil")){
			//				theoricialBound = theoricialBound1 < theoricialBound2? theoricialBound1: theoricialBound2;
			//			
			//			} 
			//			noOfTransHS += env.getSellerList().get(Parameter.TARGET_HONEST_SELLER).getSales();
			//			noOfTransDHS += env.getSellerList().get(Parameter.TARGET_DISHONEST_SELLER).getSales();
			//
			//			double theoricialBound1 = (Parameter.NO_OF_DAYS * Parameter.NO_OF_HONEST_BUYERS * Parameter.m_honestBuyerOntargetSellerRatio);
			//			double theoricialBound2 = (Parameter.NO_OF_DAYS  * Parameter.NO_OF_DISHONEST_BUYERS * Parameter.m_honestBuyerOntargetSellerRatio);
			//			double theoricialBound = theoricialBound1 > theoricialBound2? theoricialBound1: theoricialBound2;
			//			if(m_attackName.contains("Sybil")){
			//				theoricialBound = theoricialBound1 < theoricialBound2? theoricialBound1: theoricialBound2;
			//
			//			} 
			//			double results = noOfTransHS - noOfTransDHS;
			//			averageRbosutness += (results /theoricialBound); 
			//			noOfTransDHS=0;
			//			noOfTransHS=0;

		}




		try {

			String file1name = m_defenseName + m_attackName  + "Overall";

			File file1 = new File(file1name + ".txt");


			// if file doesnt exists, then create it
			if (!file1.exists()) {
				file1.createNewFile();
			}

			FileWriter fw = new FileWriter(file1.getAbsoluteFile(),true);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write("Defense Name: " + m_defenseName + " " + " " + "Attack Name: " + m_attackName + "\n");
			bw.write("Average Results of Evaluation Metrics for 100 days \n\n");
			bw.write(" _______________________________________________________________________________________________________\n");
			bw.write("|     Evaluation Metrics      " + "|          Dishonest Seller          " + "|           Honest Seller            |\n");
			bw.write("|_____________________________|____________________________________|____________________________________|\n");
			bw.write("|            MAE              " + "|         " + formatResults(averageMAEdh)  + "        |         " + formatResults(averageMAEh) +"        |\n");
			bw.write("|_____________________________|____________________________________|____________________________________|\n");
			bw.write("|            MCC              " + "|         " + formatResults(averageMCCdh)  + "        |         " + formatResults(averageMCCh) +"        |\n");
			bw.write("|_____________________________|____________________________________|____________________________________|\n");
			bw.write("|            FNR              " + "|         " + formatResults(averageFNRdh)  + "        |         " + formatResults(averageFNRh) +"        |\n");
			bw.write("|_____________________________|____________________________________|____________________________________|\n");
			bw.write("|            FNR              " + "|         " + formatResults(averageFPRdh)  + "        |         " + formatResults(averageFPRh) +"        |\n");
			bw.write("|_____________________________|____________________________________|____________________________________|\n");
			//bw.write("|           TPR              " + "|         " + formatResults(averageTPRdh/Parameter.NO_OF_RUNTIMES)  + "        |         " + formatResults(averageTPRh/Parameter.NO_OF_RUNTIMES) +"        |\n");
			//bw.write("|_____________________________|____________________________________|____________________________________|\n");
			bw.write("|          Accuracy           " + "|         " + formatResults(averageAccdh)  + "        |         " + formatResults(averageAcch) +"        |\n");
			bw.write("|_____________________________|____________________________________|____________________________________|\n");
			bw.write("|         Precision           " + "|         " + formatResults(averagePrecdh)  + "        |         " + formatResults(averagePrech) +"        |\n");
			bw.write("|_____________________________|____________________________________|____________________________________|\n");
			bw.write("|         F-Measure           " + "|         " + formatResults(averageFdh)  + "        |         " + formatResults(averageFh) +"        |\n");
			bw.write("|_____________________________|____________________________________|____________________________________|\n\n");

			//			bw.write("_____________________________|____________________________________\n");
			//			bw.write("|         Robustness          " + "|         " + formatResults(averageRbosutness/runtimes)  + "        |\n");
			//			bw.write("|_____________________________|____________________________________|\n");


			bw.close();



		} catch (IOException e) {
			e.printStackTrace();
		}






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


	public Environment getEnv() {
		return env;
	}


	public void setEnv(Environment env) {
		this.env = env;
	}


}