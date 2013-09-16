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
        }

}
