import java.util.ArrayList;


public abstract class Environment {

	private ArrayList<Buyer> buyerList;
	private ArrayList<Seller> sellerList;
	private ArrayList<Transaction> transactionList;

        protected Environment(){           // default constructor
            buyerList = new ArrayList();
            sellerList = new ArrayList();
            transactionList = new ArrayList();
        }
        Environment(ArrayList<Buyer> buyerList,ArrayList<Seller> sellerList, ArrayList<Transaction> transactionList){
            this.buyerList = buyerList;
            this.sellerList = sellerList;               // maybe from the setUpEnvironment() under CentralAuthority
            this.transactionList = transactionList;     //or else we can invoke each of the set methods below to initialize
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
	
	abstract void importConfigSettings();
	abstract void createEnvironment();
	abstract void saveWekaInstances();
	
}//class
