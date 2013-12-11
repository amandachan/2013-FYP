package main;
import java.util.*;
public class Testbed {

	/**
	 * @param args
	 */
	public static void main(String[] args)throws Exception {
		// TODO Auto-generated method stub
		CentralAuthority c = new CentralAuthority();
		//	c.setUpEnvironment("sybil","BRS");
		//        c.simulateEnvironment("sybil","BRS", true);
		System.out.println("enters testBed");
		ArrayList<String> def = new ArrayList<String>();
		def.add("BRS");
		ArrayList<String> attack = new ArrayList();
		attack.add("AlwaysUnfair");
		c.evaluateDefenses(def, attack, "FYP");
		
//		c = new CentralAuthority();
//		ArrayList<String> def1 = new ArrayList<String>();
//		def1.add("TRAVOS");
//		ArrayList<String> attack1 = new ArrayList();
//		attack1.add("Camouflage");
//		c.evaluateDefenses(def1, attack1, "FYP");
//		
//		c = new CentralAuthority();
//		
//		ArrayList<String> def2 = new ArrayList<String>();
//		def2.add("TRAVOS");
//		ArrayList<String> attack2 = new ArrayList();
//		attack2.add("Sybil_Camouflage");
//		c.evaluateDefenses(def2, attack2, "FYP");
//		c = new CentralAuthority();
//		ArrayList<String> def3 = new ArrayList<String>();
//		def3.add("TRAVOS");
//		ArrayList<String> attack3 = new ArrayList();
//		attack3.add("Sybil_Whitewashing");
//		c.evaluateDefenses(def3, attack3, "FYP");
//		
//		c = new CentralAuthority();
//		ArrayList<String> def4 = new ArrayList<String>();
//		def4.add("TRAVOS");
//		ArrayList<String> attack4 = new ArrayList();
//		attack4.add("Sybil");
//		c.evaluateDefenses(def4, attack4, "FYP");
//		
//		c = new CentralAuthority();
//		ArrayList<String> def5 = new ArrayList<String>();
//		def5.add("TRAVOS");
//		ArrayList<String> attack5 = new ArrayList();
//		attack5.add("Whitewashing");
//		c.evaluateDefenses(def5, attack5, "FYP");
		
	}

}
