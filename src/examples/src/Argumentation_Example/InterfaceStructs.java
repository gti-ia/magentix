package Argumentation_Example;


public class InterfaceStructs {
	
	public class Technician{
		public String getQuantity() {
			return quantity;
		}

		public void setQuantity(String quantity) {
			this.quantity = quantity;
		}

		public String getBaseID() {
			return baseID;
		}

		public void setBaseID(String baseID) {
			this.baseID = baseID;
		}

		public String getRole() {
			return role;
		}

		public void setRole(String role) {
			this.role = role;
		}

		public String getGroup() {
			return group;
		}

		public void setGroup(String group) {
			this.group = group;
		}

		public String getGroupStrategy() {
			return groupStrategy;
		}

		public void setGroupStrategy(String groupStrategy) {
			this.groupStrategy = groupStrategy;
		}

		public String getDecisionStrategy() {
			return decisionStrategy;
		}

		public void setDecisionStrategy(String decisionStrategy) {
			this.decisionStrategy = decisionStrategy;
		}

		public String getInitialDataPath() {
			return initialDataPath;
		}

		public void setInitialDataPath(String initialDataPath) {
			this.initialDataPath = initialDataPath;
		}

		public int getPersuasiveness() {
			return persuasiveness;
		}

		public void setPersuasiveness(int persuasiveness) {
			this.persuasiveness = persuasiveness;
		}

		public int getSupport() {
			return support;
		}

		public void setSupport(int support) {
			this.support = support;
		}

		public int getRisk() {
			return risk;
		}

		public void setRisk(int risk) {
			this.risk = risk;
		}

		public int getAttack() {
			return attack;
		}

		public void setAttack(int attack) {
			this.attack = attack;
		}

		public int getEfficiency() {
			return efficiency;
		}

		public void setEfficiency(int efficiency) {
			this.efficiency = efficiency;
		}

		public int getExplanatoryPower() {
			return explanatoryPower;
		}

		public void setExplanatoryPower(int explanatoryPower) {
			this.explanatoryPower = explanatoryPower;
		}

		String quantity;
		String baseID;
		String role;
		String group;
		String groupStrategy;
		String decisionStrategy;
		String initialDataPath;
		int persuasiveness, support, risk, attack, efficiency, explanatoryPower;
		
		public Technician(){
			quantity="";
			baseID = role = group = groupStrategy = decisionStrategy = initialDataPath = "";
			persuasiveness = support = risk = attack = efficiency = explanatoryPower = 0;
		}
		
		
		
		public Technician(String quantity, String baseID, String role, String group, String groupStrategy, String decisionStrategy,
				String initialDataPath, int persuasiveness, int support, int risk, int attack, int efficiency,
				int explanatoryPower) {
			super();
			this.quantity = quantity;
			this.baseID = baseID;
			this.role = role;
			this.group = group;
			this.groupStrategy = groupStrategy;
			this.decisionStrategy = decisionStrategy;
			this.initialDataPath = initialDataPath;
			this.persuasiveness = persuasiveness;
			this.support = support;
			this.risk = risk;
			this.attack = attack;
			this.efficiency = efficiency;
			this.explanatoryPower = explanatoryPower;
		}

		public String toString(){
			return quantity+" "+baseID+" "+role+" "+group+" "+groupStrategy+" "+decisionStrategy+" "+initialDataPath+" "+
		persuasiveness+" "+support+" "+risk+" "+attack+" "+efficiency+" "+explanatoryPower;
		}
		
	}
	
//	public class Problem{
//		public class Question{
//			int id;
//			String answer;
//		}
//		
//		Question [] questions;
//		
//		@Override
//		public String toString(){
//			if(questions==null){
//				return "";
//			}
//			String str="";
//			for(int i=0;i<questions.length;i++){
//				str+=questions[i].id + "=" + questions[i].answer+" || ";
//			}
//			
//			return str;
//		}
//	}
	
//	public class Problem{
//		String id;
//		String answer;
//		
//		public Problem() {
//			id="";
//			answer="";
//		}
//		
//		public String toString(){
//			return "id: "+id+" answer: "+answer;
//		}
//	}
	
	public class CallCentre {
		
		String type;
		String traceID;
		String techniciansIDs;
		String problem;
		String technicians;
		
		
		public CallCentre(){
			type="";
			traceID="";
			techniciansIDs="";
			problem="";
			technicians="";
		}
	}
	
	public class Solution {
		String type;
		String trace;
		String traceID;
		String dateMillis;
		String techniciansIDs;
		String problem;
		String technicians;
		String solution;
		String promotedValue;
		String error;
		String solTechnicians;
		
		public Solution(){
			type="";
			trace="";
			traceID="";
			dateMillis="";
			techniciansIDs="";
			problem="";
			technicians="";
			solution="";
			promotedValue="";
			error="";
			solTechnicians="";
		}
	}
	
	public class InJSONObject {

		// Interactive work
		public String conversation_id;
		public String agent_name;
	    
		//public Technician[] technicians;
		public CallCentre content;
	    
	    /**
	    * Constructor of the class
	    */
	    public InJSONObject() {
	       
	    }
	}
	
	
	public class OutJSONObject {
		
		public Solution result;
		/**
		 * Constructor of the class
		 */
		public OutJSONObject() {
			
			result=new Solution();
		}
	}
}
