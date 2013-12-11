package defenses;

import java.util.ArrayList;
import java.util.Vector;

import weka.clusterers.Clusterer;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

import main.Parameter;
import distributions.PseudoRandom;

import agent.Buyer;
import agent.Seller;
import environment.Environment;

public class IClub extends Defense{
	// parameter setting for DBSCAN
		private int minPts = 1; // m_minPts = 6
		private double eps = 0.3; // m_eps = 0.9
		// parameter for iCLUB
		private int epsilon = 1; // m_iclubEpsilon = 3;

		private int mnrLen = Parameter.RATING_MULTINOMINAL.length;
		// to save time;
	
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

	@Override
	public double calculateTrust(Seller seller, Buyer honestBuyer) {
		int bid = honestBuyer.getId();
		int sid = seller.getId();
		int numOfRatings = 0;
		trustOfAdvisors = new ArrayList<Double>(); 
		if (trustOfAdvisors.size()==0){
			for(int h=0; h<ecommerce.getBuyerList().size(); h++){
				trustOfAdvisors.add(0.0);
			}
		}

		/* if (Parameter.RATING_TYPE.compareTo("binary") == 0) {*/

		for(int i=0; i<honestBuyer.getTrans().size(); i++){
			if(honestBuyer.getTrans().get(i).getSeller().getId()==sid){
				for(int q=0; q <honestBuyer.getTrans().get(i).getRating().getCriteriaRatings().size(); q++){
					if (honestBuyer.getTrans().get(i).getRating().getCriteriaRatings().get(q).getCriteriaRatingValue()==-1){
						numOfRatings ++;
					}
					if (honestBuyer.getTrans().get(i).getRating().getCriteriaRatings().get(q).getCriteriaRatingValue()==1){
						numOfRatings ++;
					}
				}
			}
		}

		/*	} else {
				BSR = ((eCommerceM) m_eCommerce).getBuyerSellerRatingArray();
				int mnrLen = Parameter.RATING_MULTINOMINAL.length;
				for (int j = 0; j < mnrLen; j++) {
					numRatings_BS += BSR[bid][sid][j];
				}
			}*/

		Vector advisor = null;
		if (numOfRatings >= epsilon) {
			advisor = local(honestBuyer, seller);
			// System.out.println("using local DBSCAN");
		} else {
			advisor = global(honestBuyer, seller);
		}

		// calculate the reputation from advisor
		if (advisor == null || advisor.size() == 0)
			return 0.5;

		double rep_aBS = 0.5;
		//if (Parameter.RATING_TYPE.compareTo("binary") == 0) {
		rep_aBS = trustBasedAdvisorsB(honestBuyer, seller, advisor);
		/*} else {
			rep_aBS = trustBasedAdvisorsM(bid, sid, advisor);
		}*/

		// get the trust for advisors
		Vector<Integer> storedAdvisors = honestBuyer.getAdvisors();
		storedAdvisors.clear();
		for (int i = 0; i < advisor.size(); i++) {
			int aid = Integer.parseInt(advisor.get(i).toString());
			if (aid == bid)
				continue;
			if (aid < 100) {
				trustOfAdvisors.set(aid,1.0);
			}		
			storedAdvisors.add(aid);
			honestBuyer.setTrustAdvisor(aid, 1.0);
		}
		honestBuyer.calculateAverageTrusts(sid); // get the average trust of
		return rep_aBS;
	}

	// for multinominal ratings;
	private double trustBasedAdvisorsM(Buyer b, Seller s, Vector advisor) {

		double rep_aBS = 0.5;
		int mnrLen = Parameter.RATING_MULTINOMINAL.length;
		int halfPos = mnrLen / 2;
		//int[][][] BSR = ((eCommerceM) m_eCommerce).getBuyerSellerRatingArray();		
		ArrayList<Double> BAforS = new ArrayList<Double>();
		for(int i=0; i<2; i++){
			BAforS.add(0.0);
		}

		int bid = b.getId();
		int sid = s.getId();
		for (int i = 0; i < advisor.size(); i++) {
			int neg =0, pos=0;

			//num of ratings - BSR[aid][sid][0/1]


			int aid = Integer.parseInt(advisor.get(i).toString());
			if (aid == bid)
				continue;

			for(int i1=0; i1<b.getListOfBuyers().get(aid).getTrans().size(); i1++){
				if(b.getListOfBuyers().get(aid).getTrans().get(i1).getSeller().getId()==sid){
					for(int q=0; q <b.getListOfBuyers().get(aid).getTrans().get(i1).getRating().getCriteriaRatings().size(); q++){
						if (b.getListOfBuyers().get(aid).getTrans().get(i1).getRating().getCriteriaRatings().get(q).getCriteriaRatingValue()==-1){
							neg ++;
						}
						if (b.getListOfBuyers().get(aid).getTrans().get(i1).getRating().getCriteriaRatings().get(q).getCriteriaRatingValue()==1){
							pos ++;
						}
					}
				}
			}

			for (int j = 0; j < mnrLen; j++) {
				if (j < halfPos) {
					BAforS.set(0, BAforS.get(0)+neg * Math.abs(halfPos - j));
				} else if (j > halfPos) {
					BAforS.set(1, BAforS.get(1)+pos * Math.abs(halfPos - j));
				}
			}
		}
		rep_aBS = (BAforS.get(1) + 1.0 * Parameter.m_laplace)
				/ (BAforS.get(0) + BAforS.get(1) + 2.0 * Parameter.m_laplace);

		return rep_aBS;
	}

	// for binary ratings;
	private double trustBasedAdvisorsB(Buyer b, Seller s, Vector advisor) {

		double rep_aBS = 0.5;
		//int[][][] BSR = ((eCommerceB) m_eCommerce).getBuyerSellerRatingArray();
		ArrayList<Double> BAforS = new ArrayList<Double>();
		for(int i=0; i<2; i++){
			BAforS.add(0.0);
		}
		int pos=0, neg=0;
		int sid = s.getId();
		int bid = b.getId();
		for (int i = 0; i < advisor.size(); i++) {
			int aid = Integer.parseInt(advisor.get(i).toString());
			if (aid == bid)
				continue;
			for(int i1=0; i1<b.getListOfBuyers().get(aid).getTrans().size(); i1++){
				if(b.getListOfBuyers().get(aid).getTrans().get(i1).getSeller().getId()==sid){
					for(int q=0; q <b.getListOfBuyers().get(aid).getTrans().get(i1).getRating().getCriteriaRatings().size(); q++){
						if (b.getListOfBuyers().get(aid).getTrans().get(i1).getRating().getCriteriaRatings().get(q).getCriteriaRatingValue()==-1){
							neg ++;
						}
						if (b.getListOfBuyers().get(aid).getTrans().get(i1).getRating().getCriteriaRatings().get(q).getCriteriaRatingValue()==1){
							pos ++;
						}
					}
				}
			}
			BAforS.set(0, BAforS.get(0) + neg);
			BAforS.set(1, BAforS.get(1) + pos);
		}
		rep_aBS = (BAforS.get(1) + 1.0 * Parameter.m_laplace)
				/ (BAforS.get(0) + BAforS.get(1) + 2.0 * Parameter.m_laplace);

		return rep_aBS;
	}

	private Vector global(Buyer b, Seller s) {
		int bid = b.getId();
		int sid = s.getId();
		// step 1: find the local advisor for seller which have transaction with
		// buyer <bid>, and merge the local advisor
		int numSellers = s.getListOfSellers().size();
		ArrayList<Vector> localadvisor_BSref = new ArrayList<Vector>();
		for(int i=0; i<numSellers; i++){
			localadvisor_BSref.add(null);
		}
		for (int i = 0; i < numSellers; i++) {
			int Sref = i;
			if (Sref == sid)
				continue;
			localadvisor_BSref.set(i, local(b, s.getSeller(Sref)));
		}
		// merge such local advisor for reference sellers based on buyer
		Vector localadvisor_BSref_merge = new Vector(); // W_F in paper
		for (int i = 0; i < numSellers; i++) {
			int Sref = i;
			if (Sref == sid || localadvisor_BSref.get(i) == null)
				continue;
			localadvisor_BSref_merge = localadvisor_BSref.get(i);
		}
		for (int i = 0; i < numSellers; i++) {
			int Sref = i;
			if (Sref == sid || localadvisor_BSref.get(i) == null)
				continue;
			localadvisor_BSref_merge = interSection(localadvisor_BSref_merge,localadvisor_BSref.get(i));
		}

		// step 2: find the local advisor for (bid, sid), merge the from local
		// advisor and local reference advisor
		ArrayList<Vector> globaladvisor = DBSCAN(b, s); // W_F_j in paper
		if (globaladvisor == null)
			return null;
		for (int i = 0; i < globaladvisor.size(); i++) {
			Vector localadvisor_BS_cluster = globaladvisor.get(i); // W_c in paper
			// ignore the local information;
			if (localadvisor_BS_cluster.contains(bid + ""))
				localadvisor_BS_cluster.remove(bid + "");
			if (localadvisor_BSref_merge.size() > 0) {
				globaladvisor.set(i,interSection(localadvisor_BSref_merge,localadvisor_BS_cluster));
			} else {
				globaladvisor.set(i,localadvisor_BS_cluster);
			}
		}

		// step 3: find the maximum intersection
		ArrayList<Integer> gaSize = new ArrayList<Integer>();
		for(int i=0; i<globaladvisor.size(); i++){
			gaSize.add(0);
		}
		int maxSize = -Integer.MAX_VALUE;
		for (int i = 0; i < globaladvisor.size(); i++) {
			gaSize.set(i, globaladvisor.get(i).size());
			if (gaSize.get(i) > maxSize) {
				maxSize = gaSize.get(i);
			}
		}
		ArrayList<Integer> gaMaxsize = new ArrayList<Integer>();
		for(int i=0; i<globaladvisor.size(); i++){
			gaMaxsize.add(0);
		}
		int gaMaxsizeIdx = 0;
		for (int i = 0; i < globaladvisor.size(); i++) {
			if (gaSize.get(i)== maxSize) {
				gaMaxsize.set(gaMaxsizeIdx++, i);
			}
		}
		int q = gaMaxsize.get(PseudoRandom.randInt(0, gaMaxsizeIdx - 1));

		Vector globaladvisor_BS = globaladvisor.get(q);
		return globaladvisor_BS;
	}

	private Vector interSection(Vector av, Vector bv) {

		Vector isv = new Vector();
		for (int i = 0; i < av.size(); i++) {
			Object ab_element = av.get(i);
			if (bv.contains(ab_element) && isv.contains(ab_element) == false) {
				isv.add(ab_element);
			}
		}

		return isv;
	}

	private Vector local(Buyer b, Seller s) {
		int bid = b.getId();
		int sid = s.getId();
		// get the cluster assignment by DBSCAN
		ArrayList<Vector> buyersclubs = DBSCAN(b,s);
		// return the clusters, not
		// include the noise
		// instances

		// find the bid, sid belong to which cluster
		if (buyersclubs == null) {
			return null;
		}

		int cIdx_BS = -1; // cluster index of (bid, sid), -1 means the noise
		for (int i = 0; i < buyersclubs.size(); i++) {
			if (buyersclubs.get(i).contains(bid + "")) {
				cIdx_BS = i;
				break;
			}
		}
		// find the advisor for cluster_BS
		if (cIdx_BS == -1) { // cIdx_BS == -1, because there is not such advisor
			// cluster (sid), include the (bid, sid).
			return null;
		}
		// System.out.println("cluster for (bid, sid) = " + cIdx_BS);
		Vector localadvisor_BS = buyersclubs.get(cIdx_BS);
		// ignore the buyer itself
		localadvisor_BS.remove(bid + "");
		if (localadvisor_BS.size() == 0) {
			localadvisor_BS = null;
		}
		return localadvisor_BS;
	}

	private ArrayList<Vector> DBSCAN(Buyer b, Seller s) {
		// return the clusters of bid = the advisor buyers (be similar with the
		// bid) for sid
		int bid = b.getId();
		int sid = s.getId();
		int rLen = 0;
		//if (Parameter.RATING_TYPE.compareTo("binary") == 0) {
		rLen = Parameter.RATING_BINARY.length - 1;
		/*} else {
			BSR = ((eCommerceM) m_eCommerce).getBuyerSellerRatingArray();
			rLen = Parameter.RATING_MULTINOMINAL.length;
		}*/
		int numBuyers = b.getListOfBuyers().size();

		// create a new instances;
		FastVector attInfo = new FastVector();
		// attribute include: [neg, pos] or [1, 2, 3, 4, 5], 3 = null
		for (int i = 0; i < rLen; i++) {
			attInfo.addElement(new Attribute("rating" + Integer.toString(i + 1)));
		}
		Instances header = new Instances("ratings.arff", attInfo, numBuyers);
		Instances ratings = new Instances(header);

		ArrayList<Boolean> validatebid = new ArrayList<Boolean>();
		for(int i=0; i<numBuyers; i++){
			validatebid.add(null);
		}


		int pos=0, neg=0;

		for (int i = 0; i < numBuyers; i++) {
			int Bref = i;
			double sum = 0.0;
			int count =0;


			for (int j = 0; j < rLen; j++) {

				for(int i1=0; i1<b.getListOfBuyers().get(Bref).getTrans().size(); i1++){
					if(b.getListOfBuyers().get(Bref).getTrans().get(i1).getSeller().getId()==sid){
						for(int q=0; q <b.getListOfBuyers().get(Bref).getTrans().get(i1).getRating().getCriteriaRatings().size(); q++){
							if (j==0 && b.getListOfBuyers().get(Bref).getTrans().get(i1).getRating().getCriteriaRatings().get(q).getCriteriaRatingValue()==-1){
								sum ++;
								neg++;
							}
							if (j==1 && b.getListOfBuyers().get(Bref).getTrans().get(i1).getRating().getCriteriaRatings().get(q).getCriteriaRatingValue()==1){
								sum ++;
								pos++;
							}
						}
					}
				}

			}
			if (sum == 0) {
				validatebid.set(i,false);
			} else {
				Instance inst = new Instance(ratings.numAttributes());
				inst.setDataset(ratings);
				for (int j = 0; j < rLen; j++) {
					if(j==0){
						inst.setValue(j, neg / sum);

					}
					else 
						inst.setValue(j, pos/ sum);
				}
				validatebid.set(i,true);
				ratings.add(inst);
			}
		}
		if (ratings.numInstances() == 0) {
			return null;
		}

		try {
			// initialize the dbscan parameters
			Clusterer dbscan = new weka.clusterers.DBScan();
			((weka.clusterers.DBScan) dbscan).setMinPoints(minPts);
			((weka.clusterers.DBScan) dbscan).setEpsilon(eps);
			// build cluster
			dbscan.buildClusterer(ratings);

			// find the cluster has difference buyers;
			ArrayList<Vector> clubs = new ArrayList<Vector>();
			for (int i=0; i<dbscan.numberOfClusters(); i++){
				clubs.add(null);
			}
			for (int i = 0; i < clubs.size(); i++) {
				clubs.set(i,new Vector());
			}
			int inst_idx = -1;
			for (int i = 0; i < numBuyers; i++) {
				if (validatebid.get(i) == false)
					continue;
				inst_idx++;
				Instance inst = ratings.instance(inst_idx);
				int cnum = -1;
				try {
					cnum = dbscan.clusterInstance(inst);
					// System.out.println(inst.toString() + "   " + cnum);
				} catch (Exception e) {
					cnum = -1; // -1 means noise
					// System.out.println(inst.toString() + "   " + "NOISE");
				}
				if (cnum != -1) {
					clubs.get(cnum).add(i + "");
				}
			}

			return clubs;

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public Seller chooseSeller(Buyer honestBuyer, Environment ec) {
		this.ecommerce = ec;
		//System.out.println("in chooseseller method");
		//calculate the trust values on target seller		
		ArrayList<Double> trustValues = new ArrayList<Double>();
		ArrayList<Double> mccValues = new ArrayList<Double>();
		ArrayList<Double> FNRValues = new ArrayList<Double>();
		ArrayList<Double> FPRValues = new ArrayList<Double>();
		ArrayList<Double> accValues = new ArrayList<Double>();
		ArrayList<Double> precValues = new ArrayList<Double>();
		ArrayList<Double> fValues = new ArrayList<Double>();
		ArrayList<Double> TPRValues = new ArrayList<Double>();


		if (trustValues.size()==0){
			for(int i=0; i<2; i++){
				trustValues.add(0.0);
				mccValues.add(0.0);
				FNRValues.add(0.0);
				accValues.add(0.0);
				FPRValues.add(0.0);
				precValues.add(0.0);
				fValues.add(0.0);
				TPRValues.add(0.0);

			}
		}
		Seller s = new Seller();
		for (int k = 0; k < 2; k++) {				
			int sid = Parameter.TARGET_DISHONEST_SELLER;
			if (k == 1)sid = Parameter.TARGET_HONEST_SELLER;

			trustValues.set(k,calculateTrust(honestBuyer.getSeller(sid),honestBuyer));
			mccValues.set(k, ecommerce.getMcc().calculateMCC(sid, trustOfAdvisors));
			FNRValues.set(k, ecommerce.getMcc().calculateFNR(sid, trustOfAdvisors));
			accValues.set(k, ecommerce.getMcc().calculateAccuracy(sid, trustOfAdvisors));
			FPRValues.set(k, ecommerce.getMcc().calculateFPR(sid, trustOfAdvisors));
			precValues.set(k, ecommerce.getMcc().calculatePrecision(sid, trustOfAdvisors));
			fValues.set(k, ecommerce.getMcc().calculateF(sid, trustOfAdvisors));
			TPRValues.set(k, ecommerce.getMcc().calculateTPR(sid, trustOfAdvisors));



		}
		//update the daily reputation difference
		ecommerce.updateDailyReputationDiff(trustValues);
		ecommerce.getMcc().updateDailyMCC(mccValues,ecommerce.getDay());
		ecommerce.getMcc().updateDailyFNR(FNRValues,ecommerce.getDay());
		ecommerce.getMcc().updateDailyAcc(accValues,ecommerce.getDay());
		ecommerce.getMcc().updateDailyFPR(FPRValues,ecommerce.getDay());
		ecommerce.getMcc().updateDailyPrec(precValues,ecommerce.getDay());
		ecommerce.getMcc().updateDailyF(fValues,ecommerce.getDay());

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

}
