package environment;


import weka.core.*;
import java.io.*;
import java.util.*;

import weka.core.Instances;
import weka.core.converters.ArffSaver;
import java.text.*;
import java.util.*;
import agent.*;
import main.*;
import weka.core.converters.ArffLoader.ArffReader;

public class EnvironmentR extends Environment{


	

	public void eCommerceSetting(String attackName,String defenseName){

		//parameterSetting(attackName,defenseName);
		//generateInstances();
		System.out.println("enters real environment");
		try{
			agentSetting(attackName,defenseName);
		}
		catch(Exception e){}
		parameterSetting(attackName,defenseName);
		generateInstances();
		assignTruth();
	}//eCommerceSetting


	@Override
	protected void parameterSetting(String attackName,String defenseName){

		System.out.println("enters parameter setting2");

		m_Numdays = 1;//Parameter.NO_OF_DAYS;
		m_NumBuyers = getNumofBuyers();//Parameter.NO_OF_DISHONEST_BUYERS + Parameter.NO_OF_HONEST_BUYERS;
		m_NumSellers = getNumofSellers();//Parameter.NO_OF_DISHONEST_SELLERS + Parameter.NO_OF_HONEST_SELLERS;
		Day = 1;
		//	m_AttackName = new String(attackName);
		m_DefenseName = new String(defenseName);

	}//parameterSetting

	public Instances generateInstances(){
		System.out.println("enters generateInstances");
		//initialize the header information of instances
		Instances header = initialInstancesHeader();
		Instances header2 = initialInstancesHeader2();
		m_Transactions = new Instances(header); //protected variable declared under Environment class.
		balances = new Instances(header2);
		System.out.println(m_Transactions);
		System.out.println(balances);

		return m_Transactions;
	}

	@Override
	public void assignTruth(){

		int interval = m_NumSellers / (Parameter.RATING_MULTINOMINAL.length - 1);
		double[] trueRep = new double[]{0, 0.25, 0.5, 0.75, 1.0};
		int halfPos = Parameter.RATING_MULTINOMINAL.length / 2;
		for(int i = 0; i < m_NumSellers; i++){
			int ratingIdx = i / interval;
			if(ratingIdx >= halfPos)ratingIdx++;
			//[1..1, 2...,2, 4...4, 5...5]

			m_SellersTrueRating.put(this.sellerList.get(i),(double)Parameter.RATING_MULTINOMINAL[ratingIdx]); //need to check the casting...not sure!!
			m_SellersTrueRep.put(this.sellerList.get(i),trueRep[ratingIdx]);

		} 

	}//assignTruth

	@Override
	public void agentSetting(String attackName,String defenseName) throws ClassNotFoundException, NoSuchMethodException, SecurityException{
		sellerBankBalance = new HashMap<Seller, Double>();
		buyerBankBalance = new HashMap<Buyer, Double>();


		positiveRatings = new HashMap<Seller, Double>();
		negativeRatings = new HashMap<Seller, Double>();

		dailyMCCDishonestSeller = new HashMap<Integer, Double>();
		dailyRepHonestSeller = new HashMap<Integer, Double>();
		dailyRepDishonestSeller = new HashMap<Integer, Double>();
		dailyRepDiffHonestSeller = new HashMap<Integer, Double>();
		dailyRepDiffDishonestSeller = new HashMap<Integer, Double>();
		mccValues = new ArrayList<Double>();
		trustOfAdvisors = new ArrayList<Double>();
		mcc = new MCC();
		sameRatingH = new HashMap<Integer, Integer>();
		sameRatingDH = new HashMap<Integer, Integer>();

		buyerList = new ArrayList<Buyer>();
		sellerList = new ArrayList<Seller>();
		transactionList = new ArrayList<Transaction>();
		productList = new ArrayList<Product>();

		m_SellersTrueRating = new HashMap<Seller, Double>();
		m_SellersTrueRep = new HashMap<Seller, Double>();
		Day =0;

		for(int i=0; i<Parameter.NO_OF_DAYS; i++){
			dailyRepDishonestSeller.put(i, 0.0);
			dailyRepHonestSeller.put(i,0.0);
			dailyRepDiffDishonestSeller.put(i,0.0);
			dailyRepDiffHonestSeller.put(i,0.0);

		}

		String line = ""; String token ="";int count=0;
		Scanner sc;
		try {
			sc = new Scanner(new File("Toys_&_Games.txt"));

			int sellerid =0, buyerid=0;
			while(sc.hasNext()){
				line=sc.next();
				if(line.equalsIgnoreCase("product/productID:")){
					token = sc.next();
					if(stringToSeller.get(token) == null){
						stringToSeller.put(token, sellerid);
						sellerid++;
					}
					productID.add(token); //arraylist
					// noDupProdID.add(token); //hashset
				}
				//System.out.println(sc.next());
				if(line.equalsIgnoreCase("review/userID:")){
					token=sc.next();
					if(stringToBuyer.get(token) == null){
						//	System.out.println(token);

						stringToBuyer.put(token, buyerid);
						buyerid++;
					}
					userID.add(token);//arraylist
					// noDupUserID.add(token); //hashset
				}

				if(line.equalsIgnoreCase("review/score:"))
					score.add(Double.parseDouble(sc.next())) ; //arraylist
				if(line.equalsIgnoreCase("review/time:")){
					String unixDate = sc.next();
					long timestamp = Long.parseLong(unixDate);
					timestamp*=1000;

					Date date1 = new Date(timestamp); // *1000 is to convert minutes to milliseconds
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z"); // the format of your date
					String formattedDate = sdf.format(date1);
					//sdf.setTimeZone(TimeZone.getTimeZone("GMT-4"));
					//System.out.println(formattedDate);
					//System.out.println(Integer.parseInt(formattedDate.substring(5,7)));
					int year =Integer.parseInt(formattedDate.substring(0,4));
					int month = Integer.parseInt(formattedDate.substring(5,7));

					if(year == 2013 && month>=01 && month <=6)
						date.add(1);
					if(year == 2013 && month>=7 && month <=12)
						date.add(2);
					if(year == 2012 && month>=1 && month <=6)
						date.add(3);
					if(year == 2012 && month>=7 && month <=12)
						date.add(4);
					if(year == 2011 && month>=1 && month <=6)
						date.add(5);
					if(year == 2011 && month>=7 && month <=12)
						date.add(6);
					if(year == 2010 && month>=1 && month <=6)
						date.add(7);
					if(year == 2010 && month>=7 && month <=12)
						date.add(8);
					if(year <= 2009)
						date.add(9);
					else
						date.add(0);
					//date.add(new java.util.Date(timestamp));
				}


				count++;

				//System.out.println(count);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//System.out.println("number of products "+noDupProdID.size());
		//System.out.println("number of unique users "+noDupUserID.size());

		initialInstancesHeader(count);

		//=new Instances();
		double vals[] = new double[4];
		//     System.out.println("enters create data"+vals.length);

		int j=0;
		while(j<productID.size()){   //for(int i=0;i<count;i++){
			//data.attribute(0).addStringValue(line)//addStringValue(, i)

			vals[0]=data.attribute(0).addStringValue(productID.get(j));
			vals[1]=data.attribute(1).addStringValue(userID.get(j));
			vals[2]=score.get(j);
			vals[3]=date.get(j);

			j++;
			data.add(new Instance(1.0,vals));
			vals = new double[4];

		}//for


		try {
			createARFFfile();
			readARFF();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//int id=0;
		int buyerID=0;
		for(int i=0;i<stringToBuyer.size();i++){

			//System.out.println(i);
			Buyer b=new Buyer();
			b.setId(i);

			b.setEcommerce(this);
			b.getAccount().setBalance(1000.0);
			b.defenseSetting(defenseName);
			b.setDefenseName(defenseName);

			buyerList.add(b);

		}
		for(int i=0;i<stringToSeller.size();i++){

			Seller s=new Seller();
			s.setId(i);
			s.setEcommerce(this);

			s.getAccount().setBalance(0.0);
			sellerList.add(s);

		}
		for(int i=0; i<sellerList.size(); i++){
			Product p = new Product();
			p.setId(i);
			p.setPrice(i);
			p.setS(sellerList.get(i));
			productList.add(p);
			sellerList.get(i).addProductToList(p);
		}
		//System.out.println("HELLO PRODCUT LIST " + productList.size());
		for(int i=0; i<productList.size(); i++){
			productList.get(i).setListOfProducts(productList);
		}

		for(int i=0; i<buyerList.size(); i++){
			buyerList.get(i).setListOfBuyers(buyerList);
			buyerList.get(i).setListOfSellers(sellerList);
		}
		for(int i=0; i<sellerList.size(); i++){
			sellerList.get(i).setListOfBuyers(buyerList);
			sellerList.get(i).setListOfSellers(sellerList);

		}
	}

	public  void initialInstancesHeader(int count){

		FastVector attInfo = new FastVector();

		//attribute include: [day, p_id, buyer_id, buyer_is_honest, seller_id, seller_is_honest, rating]


		attInfo.addElement(new Attribute("ProductID",(FastVector)null));
		attInfo.addElement(new Attribute("UserID",(FastVector)null));
		attInfo.addElement(new Attribute("Score"));
		attInfo.addElement(new Attribute("Date"));



		String instsName = new String("eCommerce.arff");
		Instances header = new Instances(instsName, attInfo, count);
		//Instances header = new Instances(instsName, attInfo, m_Numdays * (m_NumBuyers));
		//set the class index
		//      header.setSellerIndex(header.numAttributes() - 1);

		data=new Instances("eCommerce.arff",attInfo,0);

		//return header;
	}

	public void createARFFfile()throws Exception{
		ArffSaver  saver = new ArffSaver();
		saver.setInstances(data);
		saver.setFile(new File("c:/Users/NEEL/Desktop/shop.arff"));

		saver.writeBatch();
	}
	//	public static void RemoveDuplicate2(ArrayList<String> productID, ArrayList<String> userID){
	//
	//		System.out.println("enters remove duplicate 2");
	//
	//		// UniqueSellers = new ArrayList<String>();
	//		Iterator<String> sellerIterator = productID.iterator();
	//
	//		while(sellerIterator.hasNext()){
	//
	//			String duplicate = sellerIterator.next();
	//			if(UniqueSellers.contains(duplicate))
	//				sellerIterator.remove();
	//			else
	//				UniqueSellers.add(duplicate);
	//		}
	//		//  System.out.println(UniqueSellers);
	//
	//		// UniqueBuyers = new ArrayList<String>();
	//		Iterator<String> buyerIterator = userID.iterator();
	//
	//		while(buyerIterator.hasNext()){
	//
	//			String duplicate = buyerIterator.next();
	//			if(UniqueBuyers.contains(duplicate))
	//				buyerIterator.remove();
	//			else
	//				UniqueBuyers.add(duplicate);
	//		}
	//
	//
	//	}

	public void readARFF()throws Exception{


		System.out.println("enters readARFF");

		BufferedReader br= new BufferedReader(new FileReader("c:/Users/NEEL/Desktop/shop.arff"));
		ArffReader arff = new ArffReader(br, 1000);
		Instances Ldata = arff.getStructure();
		Ldata.setClassIndex(Ldata.numAttributes() - 1);
		Instance inst;

		String seller=""; String buyer="";
		while ((inst = arff.readInstance(Ldata)) != null) {

			// if(inst.equals("Date"))
			// System.out.println(inst.toString());
			Ldata.add(inst); 
			if(inst.value(3)==1.0)
			{

				productID.add(inst.stringValue(0));userID.add(inst.stringValue(1)); score.add(inst.value(2));

			}
		} //while loop


	}

	public  int getNumofBuyers(){
		return buyerList.size();//UniqueBuyers.size();
	}
	public  int getNumofSellers(){
		return sellerList.size();//UniqueSellers.size();
	}
	// } // agentSetting

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