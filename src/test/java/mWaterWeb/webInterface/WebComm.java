package mWaterWeb.webInterface;

//
// Allow communication web agent-platform agent
//

public class WebComm {

	public class Invitation{
		public String table_id;
		public String wmarket_id;
		public String user_id;
		public String configuration_id;
		public String invitation_Condition;
		public String invitation_Date;
		
		public Invitation(String atable_id, String awmarket_id , String auser_id, 
				String aconfiguration_id, String invDate, String invCondition){
			table_id = atable_id;
			wmarket_id = awmarket_id;
			user_id = auser_id;
			configuration_id = aconfiguration_id;
			invitation_Condition = invCondition;
			invitation_Date = invDate;
			
		}
		
	}
	
	public class TradingTable{
		public String table_id;
		public String wmarket_id;
		public String configuration_id;
		public String opening_Date;
		public String conditions;
		public String opening_User;
		public String protocol_Type;
		
		public TradingTable(String atable_id, String awmarket_id , String aconfiguration_id,
				 String aopeningDate,  String aconditions, String aopeningUser, String aprotocolType){
			table_id = atable_id;
			wmarket_id = awmarket_id;
			configuration_id = aconfiguration_id;
			opening_Date = aopeningDate;
			conditions = aconditions;
			opening_User = aopeningUser;
			protocol_Type = aprotocolType;
		}
		
	}
	
	public class WaterRight{
		public String id;
		public String owner;
		//public String wmarket_id;
		public String authorized_extraction_flow;
		public String authorization_date;
		public String type_of_water;
		public String initial_date_for_extraction;
		public String final_date_for_extraction;
		
		public WaterRight(String aid, String aowner,  String aauthorized_extraction_flow,
				 String aauthorization_date,  String atype_of_water, String ainitial_date_for_extraction, 
				 String afinal_date_for_extraction){
			id = aid;
			owner = aowner;
			//wmarket_id = awmarket_id;
			authorized_extraction_flow = aauthorized_extraction_flow;
			authorization_date = aauthorization_date;
			type_of_water = atype_of_water;
			initial_date_for_extraction = ainitial_date_for_extraction;
			final_date_for_extraction = afinal_date_for_extraction;
		}
		
	}
	
	public class TradingAgreement{
		public WaterRight water_right;
		public String buyer;
		public String seller;
		public String price;
		public String table_id;
		
		public TradingAgreement(WaterRight wr, String abuyer, String aseller , String aprice, String tableid){
			water_right = wr;
			buyer = abuyer;
			seller = aseller;
			price = aprice;
			table_id = tableid;
		}
		
	}
	
	public class UserData {
	       // In
	       public String userName;
	       public String wmarket;

	       /**
	        * Constructor of the class
	        */
	       public UserData() {
	    	   
		       userName = "";
		       wmarket= "";
    	   
	       }
	}
	
	public class TradingHallData {
	       // In
	       public String table_id;
	       public String wmarket;
	       public String userName;
	      // public String rol;

	       /**
	        * Constructor of the class
	        */
	       public TradingHallData() {
	    	   
	    	    table_id = "";
		        wmarket = "";
		        userName =""; 
		        //rol = "";
	       }
	}

	public class NewTableData {
	       // In
	       public String wmarket;
	       public String th_id;
	       public String rol_when_opening;
	       public String protocol_type_id;
	       public int participants =0 ;
	       public String[] water_rights_ids;

	       /**
	        * Constructor of the class
	        */
	       public NewTableData(int wrNumber) {
		       wmarket = "";
		       th_id = "";
		       rol_when_opening = "";
		       protocol_type_id = "";
		       water_rights_ids = new String[wrNumber];
	       }
	}
	
	public class AuctionData {
	       // In
	       public String protocol_id;
	       public String conversation_id;  //empty the first time
	       public String userName;
	       public String table_id;
	       public String wmarket;
	       public String water_right;	       
	       public AuctionData() {
	    	   protocol_id = "";
	    	   conversation_id = "";
	    	   userName = "";
	    	   table_id = "";
		       wmarket = "";
		       water_right = "";	
	       }
	}
	
	public class BidData {
	       // In
	       public boolean accepted;
	       public String conversation_id;  //empty the first time
	       public String water_right_id;
	       public BidData() {
	    	   accepted = false;
	    	   conversation_id = "";
	    	   water_right_id = "";
	       }
	}
	
	public class UserProfile {
	       // Out
	       public TradingTable[] invitations;
	       public TradingTable[] tradingTables;
	       public boolean registeredUser;
	       public String wmarket;
	 
	       /**
	        * Constructor of the class
	        */
	       public UserProfile(int invitNumber, int tablesNumber, String awmarket) {
		       registeredUser = false;
		       if (invitNumber>0)
		    	   invitations = new TradingTable[invitNumber];
		       if (tablesNumber>0)
		    	   tradingTables = new TradingTable[tablesNumber];
		       wmarket = awmarket;
	       }
	}
	
	public class AuctionState {
	       // Out
	       public String conversation_id;
	       public String bid;
	       public String water_right_id;
	       public boolean finished;
	       public String[] participants;
	       public TradingAgreement agreement ; //has value when finished is true
	       public String winner;
	       public String winnerbid;
	 
	       /**
	        * Constructor of the class
	        */
	       public AuctionState(int partNumber) {
		       conversation_id = "";
		       bid = "";
		       water_right_id = "";
		       winnerbid = "";
		       winner = "";
	    	   finished = false;
		       if (partNumber>0)
		    	   participants = new String[partNumber];
		       agreement = new TradingAgreement(null, "", "", "","");
	       }
	}
	
	
	public class ParticipantProfile {
	       // Out
	       public WaterRight[] water_rights;
	       public TradingTable tt;
	       public String rol;
	 
	       /**
	        * Constructor of the class
	        */
	       public ParticipantProfile(int wrNumber, String arol) {
	    	   rol = arol;
		       if (wrNumber>0)
		    	   water_rights = new WaterRight[wrNumber];
	       }
	}
	
	public class InJsonObject {
		
		public String agent_name;
	    public String purpose;
	    
	    public InJsonObject() {
	    	agent_name = purpose = "";
	    }	    
	}
	
	public class OutJsonObject {

	    public String purpose;
	    
	    public OutJsonObject() {
	    	purpose = "";
	    }	    
	}

	
	public class AccreditationInJSONObject extends InJsonObject{

		public UserData content;
	       
	    /**
	    * Constructor of the class
	    */
	    public AccreditationInJSONObject() {
	    	super();
	    	content = new UserData();
	    }
	}
	
	public class AccreditationOutJSONObject extends OutJsonObject{
		
		public UserProfile content;
		
		/**
		 * Constructor of the class
		 */
		public AccreditationOutJSONObject(int invNumber, int tablesNumber, String awmarket) {
			super();
			content = new UserProfile(invNumber, tablesNumber, awmarket);
		}
	}
	
	public class JoinTableInJSONObject extends InJsonObject{

		public TradingHallData content;
	       
	    /**
	    * Constructor of the class
	    */
	    public JoinTableInJSONObject() {
	    	super();
	    	content = new TradingHallData();
	    }
	}
	
	public class JoinTableOutJSONObject extends OutJsonObject{
		
		public ParticipantProfile content;
		
		/**
		 * Constructor of the class
		 */
		public JoinTableOutJSONObject(int wrNumber, String rol) {
			super();
			content = new ParticipantProfile(wrNumber, rol);
		}
	}
	
	public class NewTableInJSONObject extends InJsonObject{

		public NewTableData content;
	       
	    /**
	    * Constructor of the class
	    */
	    public NewTableInJSONObject(int wrNumber) {
	    	super();
	    	content = new NewTableData(wrNumber);
	    }
	}
	
	public class NewTableOutJSONObject extends OutJsonObject{
		
		public boolean content;
		
		/**
		 * Constructor of the class
		 */
		public NewTableOutJSONObject() {
			super();
			content = false;
		}
	}
	
	public class AuctionInJSONObject extends InJsonObject{
		public AuctionData content;
	    public AuctionInJSONObject() {
	    	super();
	    	content = new AuctionData();
	    }
	}
	
	public class AuctionOutJSONObject extends OutJsonObject{
		public AuctionState content;
		public AuctionOutJSONObject(int partNumber) {
			super();
			content = new AuctionState(partNumber);
		}
	}
	
	public class BidUpInJSONObject extends InJsonObject{
		public BidData content;
	    public BidUpInJSONObject() {
	    	super();
	    	content = new BidData();
	    }
	}
}