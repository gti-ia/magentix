package mWaterWeb.bdConnection;


import java.io.StringReader;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import jason.asSemantics.Agent;
import jason.asSemantics.IntendedMeans;
import jason.asSemantics.Unifier;
import jason.asSyntax.ASSyntax;
import jason.asSyntax.Atom;
import jason.asSyntax.ListTerm;
import jason.asSyntax.ListTermImpl;
import jason.asSyntax.Literal;
import jason.asSyntax.LiteralImpl;
import jason.asSyntax.NumberTermImpl;
import jason.asSyntax.PlanBody;
import jason.asSyntax.PlanBodyImpl;
import jason.asSyntax.StringTerm;
import jason.asSyntax.StringTermImpl;
import jason.asSyntax.Term;
import jason.asSyntax.Trigger;
import jason.asSyntax.VarTerm;
import jason.asSyntax.PlanBody.BodyType;
import jason.asSyntax.parser.ParseException;
import jason.asSyntax.parser.TokenMgrError;
import jason.bb.JDBCPersistentBB;

/**
 * @author bexy
 *
 * This solution doesn't use the class "DatabaseAPI" 
 */

public class mWaterBB extends JDBCPersistentBB {
	private static Logger logger     = Logger.getLogger(mWaterBB.class.getName());
	private Hashtable<String,FieldObj> mwFieldFunctors = new Hashtable<String,FieldObj>();
	
	/**
	 * Hash table with the functors as keys and the fields name as values
	 * */
	private static Hashtable<String,FieldObj> mwFunctorsFields = new Hashtable<String,FieldObj>();
	private Hashtable<String,String> mwFunctors = new Hashtable<String,String>();

	
	DatabaseAPI dbAPI;
	Agent myag;
	
	private class FieldObj{
		String id = "";
		int type = Types.NULL ;
		String functor = "";
		public FieldObj(String idName,int objType,String objFunctor){
			id = idName;
			type = objType;
			functor = objFunctor;
		}
	}
	

	public void init(Agent ag, String[] args) {
		myag = ag;
		dbAPI = new DatabaseAPI();

		try {
			dbAPI.OpenConnection(args[1], args[2], args[3]);
		} catch (DataManagementException e) {
			e.printStackTrace();
		}

        initializemwFieldFunctors();
        initializemwFunctors();
        super.init(ag, args);
		

	}


	private void initializemwFunctors() {

		mwFunctors.put("water_user","wateruser" );
		mwFunctors.put("accredited_user","accrediteduser");
		mwFunctors.put("water_right","waterright");
		mwFunctors.put("trading_table","tradingtable");
		mwFunctors.put("configuration","configuration");
		mwFunctors.put("water_market","watermarket");
		mwFunctors.put("trading_hall","tradinghall");
		mwFunctors.put("agreement","agreement");
		mwFunctors.put("transfer_agreement","transferagreement");
		mwFunctors.put("contract","contract");
		mwFunctors.put("agreed_contract","agreedcontract");
		mwFunctors.put("trading_table_new_id","");
		mwFunctors.put("recruited_participant","recruitedparticipant");
		mwFunctors.put("protocol_type","protocoltype");
		mwFunctors.put("water_market","mwatermarket");
		mwFunctors.put("general_water_right","generalwaterright");
		mwFunctors.put("waterright_tt","tradingtable_has_waterright");
	}

	private void initializeFieldFunctor(String functor,String field, int type, String objFunctor){
		FieldObj obj ;
		obj = new FieldObj(functor,type,objFunctor);
		mwFieldFunctors.put(objFunctor+"."+field, obj);
		obj = new FieldObj(field,type,objFunctor);
		mwFunctorsFields.put( objFunctor+"."+functor,obj);	
		//System.out.println(objFunctor+"."+functor+" /// "+mwFunctorsFields.get(objFunctor+"."+functor));
	}
	
	private void initializemwFieldFunctors() {
		
		//Strings for the mwFunctorsFields. Key: functor, value: field name and obj type
		//Strings for the mwFieldFunctors. Key: field, value: functor name and obj type
		initializeFieldFunctor( "id","id",Types.NUMERIC,"water_user" );
		initializeFieldFunctor( "name","name",Types.VARCHAR,"water_user" );
		initializeFieldFunctor( "user_type","user_type",Types.NUMERIC,"water_user" );
		initializeFieldFunctor( "seller_timeout","seller_timeout",Types.NUMERIC,"water_user" );
		initializeFieldFunctor( "seller_price","seller_price",Types.NUMERIC,"water_user" );
		initializeFieldFunctor( "seller_percentage","seller_percentage",Types.NUMERIC,"water_user" );
		initializeFieldFunctor( "seller_wt","seller_wt",Types.NUMERIC,"water_user" );
		initializeFieldFunctor( "seller_th","seller_th",Types.NUMERIC,"water_user" );
		initializeFieldFunctor( "buyer_bid","buyer_bid",Types.NUMERIC,"water_user" );
		initializeFieldFunctor( "buyer_enter","buyer_enter",Types.NUMERIC,"water_user" );
		initializeFieldFunctor( "buyer_cont_enact","buyer_cont_enact",Types.NUMERIC,"water_user" );
		
		initializeFieldFunctor( "trading_table_id" ,"trading_table" ,Types.NUMERIC,"recruited_participant");
		initializeFieldFunctor( "wmarket" ,"mwater_market" ,Types.NUMERIC,"recruited_participant");
		initializeFieldFunctor( "configuration_id" ,"configuration_id" ,Types.NUMERIC,"recruited_participant");
		initializeFieldFunctor( "user_id" ,"user" ,Types.NUMERIC,"recruited_participant");
		initializeFieldFunctor( "invitation_condition" ,"invitation_condition" ,Types.VARCHAR,"recruited_participant");
		initializeFieldFunctor( "invitation_date" ,"invitation_date" ,Types.DATE,"recruited_participant");
		initializeFieldFunctor( "accepted" ,"accepted" ,Types.BIT,"recruited_participant");
		initializeFieldFunctor( "acceptance_date" ,"acceptance_date" ,Types.DATE,"recruited_participant");
		initializeFieldFunctor( "number_of_participations" ,"number_of_participations" ,Types.NUMERIC,"recruited_participant");		
		
		initializeFieldFunctor( "id" ,"user" ,Types.NUMERIC,"accredited_user");
		initializeFieldFunctor( "wmarket" ,"mwater_market" ,Types.NUMERIC,"accredited_user");		
		initializeFieldFunctor( "trust_value" ,"trust_value" ,Types.NUMERIC,"accredited_user");
		initializeFieldFunctor( "sanction_value","sanction_value" ,Types.VARCHAR,"accredited_user");
		initializeFieldFunctor( "name","name",Types.VARCHAR,"accredited_user" );
		initializeFieldFunctor( "user_type","user_type",Types.NUMERIC,"accredited_user" );
		initializeFieldFunctor( "seller_timeout","seller_timeout",Types.NUMERIC,"accredited_user" );
		initializeFieldFunctor( "seller_price","seller_price",Types.NUMERIC,"accredited_user" );
		initializeFieldFunctor( "seller_percentage","seller_percentage",Types.NUMERIC,"accredited_user" );
		initializeFieldFunctor( "seller_wt","seller_wt",Types.NUMERIC,"accredited_user" );
		initializeFieldFunctor( "seller_th","seller_th",Types.NUMERIC,"accredited_user" );
		initializeFieldFunctor( "buyer_bid","buyer_bid",Types.NUMERIC,"accredited_user" );
		initializeFieldFunctor( "buyer_enter","buyer_enter",Types.NUMERIC,"accredited_user" );
		initializeFieldFunctor( "buyer_cont_enact","buyer_cont_enact",Types.NUMERIC,"accredited_user" );
		
		initializeFieldFunctor( "owner" ,"owner" ,Types.NUMERIC,"water_right");
		initializeFieldFunctor( "id","id" ,Types.NUMERIC,"water_right");
		initializeFieldFunctor( "authorized_extraction_flow","authorized_extraction_flow" ,Types.NUMERIC,"water_right");
		initializeFieldFunctor( "general_water_right", "general_water_right",Types.NUMERIC,"water_right");
		initializeFieldFunctor( "authorization_date","authorization_date" ,Types.DATE,"water_right");
		initializeFieldFunctor( "authorized","authorized" ,Types.BIT,"water_right");
		initializeFieldFunctor( "type_of_water","type_of_water" ,Types.VARCHAR,"water_right");
		initializeFieldFunctor( "initial_date_for_extraction","initial_date_for_extraction" ,Types.DATE,"water_right");
		initializeFieldFunctor( "final_date_for_extraction","final_date_for_extraction" ,Types.DATE,"water_right");
		initializeFieldFunctor( "aggregation_right","aggregation_right" ,Types.NUMERIC,"water_right");
		initializeFieldFunctor( "season_unit","season_unit" ,Types.NUMERIC,"water_right");
		initializeFieldFunctor( "season","season" ,Types.NUMERIC,"water_right");

		initializeFieldFunctor( "id" ,"id" ,Types.NUMERIC,"general_water_right");
		initializeFieldFunctor( "authorized","authorized" ,Types.BIT,"water_right");
		initializeFieldFunctor( "authorized_extraction_flow","authorized_extraction_flow" ,Types.NUMERIC,"general_water_right");
		initializeFieldFunctor( "authorization_date", "authorization_date",Types.DATE,"general_water_right");
		initializeFieldFunctor( "owner","owner" ,Types.NUMERIC,"general_water_right");
		initializeFieldFunctor( "owner_initial_date","owner_initial_date" ,Types.DATE,"general_water_right");
		initializeFieldFunctor( "owner_final_date","owner_final_date" ,Types.DATE,"general_water_right");
		 
		initializeFieldFunctor( "id",  "id",Types.NUMERIC,"water_market");
		initializeFieldFunctor( "description","description" ,Types.VARCHAR,"water_market");				
		initializeFieldFunctor( "version", "version",Types.VARCHAR,"water_market");
		
		initializeFieldFunctor( "configuration_id",  "configuration_id",Types.NUMERIC,"trading_table");
		initializeFieldFunctor( "wmarket","mwater_market" ,Types.NUMERIC,"trading_table");				
		initializeFieldFunctor( "id", "trading_table_id",Types.NUMERIC,"trading_table");
		initializeFieldFunctor( "configuration_id",  "configuration_id",Types.NUMERIC,"trading_table_new_id");
		initializeFieldFunctor( "wmarket","mwater_market" ,Types.NUMERIC,"trading_table_new_id");				
		initializeFieldFunctor( "id", "trading_table_id",Types.NUMERIC,"trading_table_new_id");
		initializeFieldFunctor( "opening_date","opening_date" ,Types.DATE ,"trading_table");
		initializeFieldFunctor( "closing_date","closing_date" ,Types.DATE,"trading_table");
		initializeFieldFunctor( "conditions","conditions" ,Types.VARCHAR,"trading_table");
		initializeFieldFunctor( "access_type","access_type" ,Types.VARCHAR,"trading_table");
		initializeFieldFunctor( "deal","deal" ,Types.VARCHAR,"trading_table");
		initializeFieldFunctor( "protocol_parameters","protocol_parameters" ,Types.VARCHAR,"trading_table");
		initializeFieldFunctor( "num_iter_until_agreem" ,"num_iter_until_agreem" ,Types.NUMERIC,"trading_table");
		initializeFieldFunctor( "time_until_agreem", "time_until_agreem",Types.NUMERIC,"trading_table");
		initializeFieldFunctor( "num_participants", "num_participants",Types.NUMERIC,"trading_table");
		initializeFieldFunctor( "opening_user", "opening_user",Types.NUMERIC,"trading_table");
		initializeFieldFunctor( "protocol_type",  "protocol_type",Types.NUMERIC,"trading_table");
		initializeFieldFunctor( "role_when_opening_table" ,"role_when_opening_table" ,Types.VARCHAR,"trading_table");
		initializeFieldFunctor( "number_of_opener_participations" ,"number_of_opener_participations" ,Types.NUMERIC,"trading_table");
		initializeFieldFunctor( "th_id", "trading_hall_id",Types.NUMERIC,"trading_table");
		
		initializeFieldFunctor( "id", "trading_hall_id",Types.NUMERIC,"trading_hall");
		
		initializeFieldFunctor( "id","id" ,Types.NUMERIC,"configuration");
		initializeFieldFunctor( "description",  "description",Types.VARCHAR,"configuration");
		initializeFieldFunctor( "simulation_date","simulation_date" ,Types.DATE,"configuration");
		initializeFieldFunctor( "negotiation_protocol","negotiation_proto" ,Types.CHAR,"configuration");
		initializeFieldFunctor( "group_selected","group_selected" ,Types.NUMERIC,"configuration");
		initializeFieldFunctor( "initial_date","initial_date" ,Types.DATE,"configuration");
		initializeFieldFunctor( "final_date" ,"final_date" ,Types.DATE,"configuration");
		initializeFieldFunctor( "seller_timeout", "seller_timeout",Types.NUMERIC,"configuration");
		initializeFieldFunctor( "seller_price", "seller_price",Types.NUMERIC,"configuration");
		initializeFieldFunctor( "seller_percentage","seller_percentage" ,Types.NUMERIC,"configuration");
		initializeFieldFunctor( "seller_wt","seller_wt" ,Types.NUMERIC,"configuration");
		initializeFieldFunctor( "buyer_bid", "buyer_bid" ,Types.NUMERIC,"configuration");
		initializeFieldFunctor( "buyer_enter","buyer_enter"  ,Types.NUMERIC,"configuration");
		initializeFieldFunctor( "buyer_cont_enact", "buyer_cont_enact",Types.NUMERIC,"configuration");
		initializeFieldFunctor( "ba_agr_val", "ba_agr_val",Types.NUMERIC,"configuration");
		initializeFieldFunctor( "ba_entitlement", "ba_entitlement",Types.NUMERIC,"configuration");
		initializeFieldFunctor( "mf_cont_enact","mf_cont_enact" ,Types.NUMERIC,"configuration");
		initializeFieldFunctor( "mf_accred","mf_accred" ,Types.NUMERIC,"configuration");
		initializeFieldFunctor( "seller_th", "seller_th",Types.NUMERIC,"configuration");
		
		initializeFieldFunctor( "id","id" ,Types.NUMERIC,"water_market");
		initializeFieldFunctor( "description","description" ,Types.VARCHAR,"water_market");
		initializeFieldFunctor( "version", "version",Types.VARCHAR,"water_market");
		
		initializeFieldFunctor( "id", "id",Types.NUMERIC,"agreement");
		initializeFieldFunctor( "signature_date","signature_date",Types.DATE,"agreement");
		initializeFieldFunctor( "state", "state",Types.VARCHAR,"agreement");

		initializeFieldFunctor( "id", "id",Types.NUMERIC,"transfer_agreement");
		initializeFieldFunctor( "signature_date", "signature_date",Types.DATE,"transfer_agreement");
		initializeFieldFunctor( "state", "state",Types.VARCHAR,"transfer_agreement");
		initializeFieldFunctor( "agreed_price","agreed_price",Types.NUMERIC,"transfer_agreement");
		initializeFieldFunctor( "aggregation_agreement", "aggregation_agreement",Types.NUMERIC,"transfer_agreement");
		initializeFieldFunctor( "buyer_id", "buyer_id",Types.NUMERIC,"transfer_agreement");		
		initializeFieldFunctor( "water_right_id", "waterright_id",Types.NUMERIC,"transfer_agreement");		
		initializeFieldFunctor( "trading_table_id", "trading_table_id",Types.NUMERIC,"transfer_agreement");		
		initializeFieldFunctor( "wmarket", "mwater_market",Types.NUMERIC,"transfer_agreement");		
		initializeFieldFunctor( "configuration_id", "configuration_id",Types.NUMERIC,"transfer_agreement");		

		initializeFieldFunctor( "id", "id",Types.NUMERIC,"contract");	
		initializeFieldFunctor( "description", "description",Types.VARCHAR,"contract");		
		initializeFieldFunctor( "state", "state",Types.VARCHAR,"contract");		
		initializeFieldFunctor( "activation_date", "activation_date",Types.DATE,"contract");		
		initializeFieldFunctor( "expiration_date", "expiration_date",Types.DATE,"contract");		
		initializeFieldFunctor( "isPrivate", "private",Types.BIT,"contract");		

		initializeFieldFunctor( "id", "id",Types.NUMERIC,"agreed_contract");
		initializeFieldFunctor( "intended_water_use", "intended_water_use",Types.VARCHAR,"agreed_contract");
		initializeFieldFunctor( "successful", "successful",Types.BIT,"agreed_contract");
		initializeFieldFunctor( "authorisation_date", "authorisation_date",Types.DATE,"agreed_contract");
		initializeFieldFunctor( "reasons_for_negation", "reasons_for_negation",Types.VARCHAR,"agreed_contract");
		initializeFieldFunctor( "agreement_id", "agreement",Types.NUMERIC,"agreed_contract");

		initializeFieldFunctor( "id",  "id",Types.NUMERIC,"protocol_type");
		initializeFieldFunctor( "type_name","name" ,Types.VARCHAR,"protocol_type");	
		
		initializeFieldFunctor( "trading_table_id",  "trading_table",Types.NUMERIC,"waterright_tt");
		initializeFieldFunctor( "wmarket",  "mwater_market",Types.NUMERIC,"waterright_tt");
		initializeFieldFunctor( "configuration_id",  "configuration_id",Types.NUMERIC,"waterright_tt");
		initializeFieldFunctor( "water_right_id",  "water_right",Types.NUMERIC,"waterright_tt");
	}

	/**
	 * Returns the functor of the corresponding literal given the field identifier. 
	 * Otherwise returns the field identifier given as parameter.
	 * */
	public String getFieldFunctorFromFieldID(String fieldID, String mwFunctor){
		String result = "";
		if ((mwFieldFunctors.get(mwFunctor+"."+fieldID)!=null)&&
			(mwFieldFunctors.get(mwFunctor+"."+fieldID).functor.compareTo(mwFunctor)==0)){
			result = mwFieldFunctors.get(mwFunctor+"."+fieldID).id;
			}else{
				result = fieldID;
			}
		return result;
	}
	
	/**
	 * Returns the field identifier given the functor of the corresponding literal. 
	 * Otherwise returns the functor given as parameter.
	 * */
	public String getFieldIDFromFunctor(String fieldFunctor, String mwFunctor){
		String result = "";
		if ((mwFunctorsFields.get(mwFunctor+"."+fieldFunctor)!=null)&&
			(mwFunctorsFields.get(mwFunctor+"."+fieldFunctor).functor.compareTo(mwFunctor)==0)){
			result = mwFunctorsFields.get(mwFunctor+"."+fieldFunctor).id;
			}else{
				result = fieldFunctor;
			}
		return result;
	}

	/**
	 * Returns the field type given the field identifier. 
	 * Otherwise returns NULL.
	 * */
	public int getFieldTypeFromFieldID(String fieldID, String mwFunctor){
		int result = Types.NULL;
		if ((mwFieldFunctors.get(mwFunctor+"."+fieldID)!=null)&&
			(mwFieldFunctors.get(mwFunctor+"."+fieldID).functor.compareTo(mwFunctor)==0)){
			result = mwFieldFunctors.get(mwFunctor+"."+fieldID).type;
			}
		return result;
	}
	
	public Iterator<Literal> getCandidateBeliefs(Literal l, Unifier u) {
		logger.fine("--- getCandidateBeliefs(Literal l, Unifier u))");
		String functor = l.getFunctor();
		//ResultSet rs = null;
		Iterator<Literal> result = null;
		if (mwFunctors.containsKey(functor)){
			try {
				//rs = mapFunctortoResultSet(functor,l.getTerms(), u);
				String xml = mapFunctortoXML(functor, l.getTerms(), u);
				//result = resultSetToLiteralIterator(rs,l.getFunctor());
				if (xml!=null)
					//{
					result = XMLtoLiteralIterator(xml,functor );
					/*if (functor.compareTo("accredited_user")==0)
					{
						Iterator<Literal> tmp = XMLtoLiteralIterator(xml,functor );
						logger.info("###"+tmp.next().toString());
					}
					}*/
			} catch (DataManagementException e) {
				logger.log(Level.SEVERE, "SQL Error in getRelevant for "+l, e);
			}
		}else
			result = super.getCandidateBeliefs(l, u);

		return result;
	}
	
	
	/**
	 * Retrieves the value of the field if it is found in the list of terms args.
	 * otherwise returns "". 
	 * */
	private String searchFieldValueInTermList(String literalFunctor,int termIndex, List<Term> args){
		String value = null;

		String Fieldfunctor = "";
		Term currentTerm = null;
		Term currentValue = null;

		currentTerm = args.get(termIndex);
		if ((currentTerm!=null)&&(!currentTerm.isVar())&&(currentTerm.isLiteral())){
			currentValue = ((LiteralImpl)currentTerm).getTerm(0);
			Fieldfunctor = ((LiteralImpl)currentTerm).getFunctor();

			if (!currentValue.isVar())
			{ 
				if ((mwFunctorsFields.get(literalFunctor+"."+Fieldfunctor).type==Types.VARCHAR)||
						(mwFunctorsFields.get(literalFunctor+"."+Fieldfunctor).type==Types.CHAR))
				{ //The value is a string
					
					try {
						StringTermImpl tmpTerm = (StringTermImpl)ASSyntax.parseTerm(currentValue.toString());
						value = tmpTerm.getString();
					} catch (ParseException e) {
						value = "\""+currentValue.toString()+"\"";
					}
					
					if (value.compareTo("")!=0) value = "\""+value+"\"";
				}else {
					if (mwFunctorsFields.get(literalFunctor+"."+Fieldfunctor).type==Types.DATE)
					{//The value is a date
						value = ListTerm2Date(currentValue);
						if (value!=null) value = "\""+value+"\"";
					}
					else value = (currentValue).toString();
				}
			}
		}
		return value;
	}
	

	/**
	 * Retrieves the value of the field if it is found in the list of terms args,
	 * otherwise returns ""
	 * */
	public static String searchFieldValueInTermList(String literalFunctor,String Fieldfunctor, List<Term> args, Unifier u){
		String value = "";
		//int i = 0;
		//Term currentTerm = null;
		Term currentValue = null;
		//boolean found = false;

		//	while (i<args.size()&&(!found)){
		//	currentTerm = args.get(i);
		//if ((!currentTerm.isVar())&&(currentTerm.isLiteral())){
		//	currentValue = ((LiteralImpl)currentTerm).getTerm(0);
		//	if (u.unifies(Literal.parseLiteral(Fieldfunctor+"(_)" ), args.get(i)))
		//{
		//found = true;
		//ogger.info("Literal: "+literalFunctor+" Fieldfunctor:  "+Fieldfunctor);
		currentValue = searchMatchingTermValInTermList(Fieldfunctor,args,u);

		if (!currentValue.isVar())
		{
			//logger.info("Literal: "+literalFunctor+" currentvalue:  "+currentValue.toString());
			if ((mwFunctorsFields.get(literalFunctor+"."+Fieldfunctor).type==Types.VARCHAR)||
					(mwFunctorsFields.get(literalFunctor+"."+Fieldfunctor).type==Types.CHAR))
			{ //The value is a string
				
				try {
					Term formatedValue = ASSyntax.parseTerm(currentValue.toString());
					value = ((StringTermImpl)formatedValue).getString();
				} catch (ParseException e) {
					Term formatedValue = Literal.parse(currentValue.toString());
					value = "\""+formatedValue.toString()+"\"";
				}
				
				if (value.compareTo("")!=0) value = "\""+value+"\"";
			}else {
				if (mwFunctorsFields.get(literalFunctor+"."+Fieldfunctor).type==Types.DATE)  
				{//The value is a date
					value = ListTerm2Date(currentValue);
					if (value!=null) value = "\""+value+"\"";
				}
				else value = (currentValue).toString();
			}
		}
		//}
		//}
		//	i++;
		//}
		return value;
	}
	
	/**
	 * Retrieves the value of the field if it is found in the list of terms args,
	 * otherwise returns null
	 * */
	private String getFieldValueFromTermList(String literalFunctor, String FieldFunctor, List<Term> args, Unifier u){
		String result = searchFieldValueInTermList(literalFunctor, FieldFunctor, args, u);
		if (result!=null)
		{
			result = (result.compareTo("")!=0 ? result : null);
		}
		return result;
	}

	private static Term searchMatchingTermValInTermList(String Fieldfunctor, List<Term> args, Unifier u){
		int i = 0;
		Term currentTerm = null;
		Term result = null;
		boolean found = false;
		while (i<args.size()&&(!found)){
			currentTerm = args.get(i);
			if (currentTerm.isLiteral()){
				//currentValue = ((LiteralImpl)currentTerm).getTerm(0);
				if (u.unifies(Literal.parseLiteral(Fieldfunctor+"(_)" ), args.get(i)))
				{
					found = true;
					result = ((LiteralImpl)currentTerm).getTerm(0);
				}
			}
			i++;
		}
		return result;
	}
	/** translates a SQL date into a term like "[D,M,Y]" */
   /* private Term Date2ListTerm(Date date)  {
        ListTerm resultDate = new ListTermImpl();
        if (date!=null){
			Calendar time = Calendar.getInstance(); 
			time.setTime(date);
			resultDate.add(new NumberTermImpl(time.get(Calendar.DAY_OF_MONTH)));
			resultDate.add(new NumberTermImpl(time.get(Calendar.MONTH)));
			resultDate.add(new NumberTermImpl(time.get(Calendar.YEAR)));
		}
        return resultDate;
    }*/
    
	/** translates a SQL date into a term like "[D,M,Y]" */
    private Term strDate2ListTerm(String date)  {
        ListTerm resultDate = new ListTermImpl();
        int y,m,d,temp;
        if ((date!="")&&(date!=null)){
    		StringTokenizer str = new StringTokenizer(date);
    		temp= Integer.parseInt(str.nextToken("-"));
    		if (temp>1000)//if it is a year
    		{
    			y=temp;
    			m=Integer.parseInt(str.nextToken("-"));
    			d=Integer.parseInt(str.nextToken("-"));
    		}
    		else
			{
				d=temp;
				m=Integer.parseInt(str.nextToken("-"));
				y=Integer.parseInt(str.nextToken("-"));
			}
			resultDate.add(new NumberTermImpl(d));
			resultDate.add(new NumberTermImpl(m));
			resultDate.add(new NumberTermImpl(y));
		}
        return resultDate;
    }
    
	/** translates a term like "[D,M,Y]" into a SQL date*/
    private static String ListTerm2Date(Term date)  {
        String resultDate = null;
        if (date!=null)
        {
        	ListTerm formatedDate = ListTermImpl.parseList(date.toString());
        	if ((formatedDate!=null)&&(formatedDate.size()==3)){
        		resultDate = formatedDate.get(2).toString()+"-";
        		resultDate = resultDate+formatedDate.get(1).toString()+"-";
        		resultDate = resultDate+formatedDate.get(0).toString();
        	}
        }
        return resultDate;
    }
	
	/*private ResultSet mapFunctortoResultSet(String functor, List<Term> args, Unifier u)throws DataManagementException {
		ResultSet result = null;
		try {
			if(functor.compareTo("water_user")==0){
				String id = searchFieldValueInTermList("id",args,u);
				if (id.compareTo("")!=0)
					result = QueryDB(SQL.GET_WATERUSER_BY_ID(id));
				else
					//result=XMLtoResultSet(dbAPI.getWaterUsers());
					result = QueryDB(SQL.GET_WATERUSERS());
			}
			if(functor.compareTo("water_right")==0){
				String owner = searchFieldValueInTermList("owner",args,u);
				//result=XMLtoResultSet(dbAPI.getWaterRightsByOwner(owner));
				result = QueryDB(SQL.GET_WATERRIGHTS_BY_OWNER(owner));
			}
			if(functor.compareTo("trading_table")==0){
				String configuration_id = searchFieldValueInTermList("configuration_id",args,u);
				String market = searchFieldValueInTermList("mwater_market",args,u);
				//result=XMLtoResultSet(dbAPI.getTradingTables(configuration_id, market));
				result = QueryDB(SQL.GET_TRADINGTABLES(configuration_id, market));
			}
	
			if(functor.compareTo("accredited_user")==0){
				String user_id = searchFieldValueInTermList("id",args,u);
				String wmarket = searchFieldValueInTermList("wmarket",args,u);
				String query;
				if ((user_id.compareTo("")==0)&&(wmarket.compareTo("")==0))
					query = SQL.GET_ACCREDITED_USERS();
				else
					query= SQL.GET_ACCREDITED_USERS_BY_IDS(user_id, wmarket);
				result = QueryDB(query);
			}			
		} catch (SQLException e) {
			throw new DataManagementException();
		}
		return result;
	}*/
	
	
	private String mapFunctortoXML(String functor, List<Term> args, Unifier u) throws DataManagementException{
		String result = null;
		
		try{
			if(functor.compareTo("water_user")==0){
				String id = searchFieldValueInTermList(functor,"id",args,u);
				if (id.compareTo("")!=0)
					result = dbAPI.getWaterUserbyId(id);
				else
					result = dbAPI.getWaterUsers();
			}
			if(functor.compareTo("water_right")==0){
				String id = searchFieldValueInTermList(functor,"id",args,u);
				String owner = searchFieldValueInTermList(functor,"owner",args,u);
				//result=XMLtoResultSet(dbAPI.getWaterRightsByOwner(owner));
				result = dbAPI.getWaterRightsByOwner(owner,id);

			}
			if(functor.compareTo("waterright_tt")==0){
				String table = searchFieldValueInTermList(functor,"trading_table_id",args,u);
				String wright = searchFieldValueInTermList(functor,"water_right_id",args,u);
				String wmarket = searchFieldValueInTermList(functor,"wmarket",args,u);
				String confid = searchFieldValueInTermList(functor,"configuration_id",args,u);
				result = dbAPI.getWaterHasTradintable(table,wright,wmarket,confid);

			}
			if(functor.compareTo("general_water_right")==0){
				String owner = searchFieldValueInTermList(functor,"owner",args,u);
				//result=XMLtoResultSet(dbAPI.getWaterRightsByOwner(owner));
				result = dbAPI.getGeneralWaterRightsByOwner(owner);
				
			}			
			if(functor.compareTo("water_market")==0){
				String id = searchFieldValueInTermList(functor,"id",args,u);
				//result=XMLtoResultSet(dbAPI.getWaterRightsByOwner(owner));
				result = dbAPI.getWaterMarketByID(id);
			}
			if(functor.compareTo("trading_table")==0){
				String configuration_id = searchFieldValueInTermList(functor,"configuration_id",args,u);
				String market = searchFieldValueInTermList(functor,"wmarket",args,u);
				String id = searchFieldValueInTermList(functor,"id",args,u);
				//result=XMLtoResultSet(dbAPI.getTradingTables(configuration_id, market));
				result = dbAPI.getTradingTables(configuration_id, market, id);
			}
	
			if(functor.compareTo("accredited_user")==0){
				String user_id = searchFieldValueInTermList("water_user","id",args,u);
				String wmarket = searchFieldValueInTermList(functor,"wmarket",args,u);
				//OJO: AGREGADO A API!!!
				
				result = dbAPI.getAccreditedWaterUserbyId(user_id, wmarket);
			}		
			
			if(functor.compareTo("configuration")==0){
				String id = searchFieldValueInTermList(functor,"id",args,u);
				result = dbAPI.getConfiguration(id);
			}
			
			if(functor.compareTo("trading_table_new_id")==0){
				String wmarket = searchFieldValueInTermList(functor,"wmarket",args,u);
				String config_id = searchFieldValueInTermList(functor,"configuration_id",args,u);

				result = dbAPI.getTradingTableNewID(config_id,wmarket);
			}
			
			if(functor.compareTo("recruited_participant")==0){

				String wmarket = searchFieldValueInTermList(functor,"wmarket",args,u);
				String config_id = searchFieldValueInTermList(functor,"configuration_id",args,u);
				String user_id = searchFieldValueInTermList(functor,"user_id",args,u);
				String trading_table_id = searchFieldValueInTermList(functor,"trading_table_id",args,u);
				result = dbAPI.getRecruitedParticipants(trading_table_id, config_id, wmarket, user_id);
			}
			
			if(functor.compareTo("protocol_type")==0){
				String id = searchFieldValueInTermList(functor,"id",args,u);
				String name = searchFieldValueInTermList(functor,"type_name",args,u);
				result = dbAPI.getProtocolType(id, name);
			}

			if (functor.compareTo("transfer_agreement")==0){
				String id = searchFieldValueInTermList(functor, "id", args,u);
				result = dbAPI.getTransferAgreement(id);
			}
			
			if(functor.compareTo("+configuration")==0){
				
				String description =  getFieldValueFromTermList("configuration","description",args,u);
				
				String simulation_date = getFieldValueFromTermList("configuration","simulation_date",args,u);
				String negotiation_protocol = getFieldValueFromTermList("configuration","negotiation_protocol",args,u);
				
				String group_selected = getFieldValueFromTermList("configuration","group_selected",args,u);
				String initial_date = getFieldValueFromTermList("configuration","initial_date",args,u);
			
				String final_date = getFieldValueFromTermList("configuration","final_date",args,u);
				String seller_timeout = getFieldValueFromTermList("configuration","seller_timeout",args,u);
				String seller_price = getFieldValueFromTermList("configuration","seller_price",args,u);
				String seller_percentage = getFieldValueFromTermList("configuration","seller_percentage",args,u);
				String seller_wt = getFieldValueFromTermList("configuration","seller_wt",args,u);
				String buyer_bid = getFieldValueFromTermList("configuration","buyer_bid",args,u);
			
				String buyer_enter = getFieldValueFromTermList("configuration","buyer_enter",args,u);
				String buyer_cont_enact = getFieldValueFromTermList("configuration","buyer_cont_enact",args,u);
				String ba_agr_val = getFieldValueFromTermList("configuration","ba_agr_val",args,u);
				String ba_entitlement = getFieldValueFromTermList("configuration","ba_entitlement",args,u);
				String mf_cont_enact = getFieldValueFromTermList("configuration","mf_cont_enact",args,u);
				String mf_accred = getFieldValueFromTermList("configuration","mf_accred",args,u);
				String seller_th = getFieldValueFromTermList("configuration","seller_th",args,u);
				
				result = dbAPI.insertConfiguration(description, simulation_date, negotiation_protocol, 
						group_selected, initial_date, final_date, seller_timeout, seller_price, seller_percentage, 
						seller_wt, buyer_bid, buyer_enter, buyer_cont_enact, ba_agr_val, ba_entitlement, mf_cont_enact, mf_accred, seller_th);

			}
			if(functor.compareTo("+water_market")==0){
				String description = getFieldValueFromTermList("water_market","description",args,u);
				String version = getFieldValueFromTermList("water_market","version",args,u);
				result = dbAPI.insertWaterMarket(description, version);

			}		
			if(functor.compareTo("+waterright_tt")==0){
				String table = searchFieldValueInTermList(functor,"trading_table_id",args,u);
				String wright = searchFieldValueInTermList(functor,"water_right_id",args,u);
				String wmarket = searchFieldValueInTermList(functor,"wmarket",args,u);
				String confid = searchFieldValueInTermList(functor,"configuration_id",args,u);
				result = dbAPI.insertWaterHasTradintable(table,wright,wmarket,confid);

			}
			if(functor.compareTo("+trading_hall")==0){
				result = dbAPI.insertTradingHall();
			}		
			if(functor.compareTo("+trading_table")==0){
				String configuration_id = getFieldValueFromTermList("trading_table","configuration_id",args,u);
				String mwater_market = getFieldValueFromTermList("trading_table","wmarket",args,u);
				String trading_table_id = getFieldValueFromTermList("trading_table","id",args,u);
				String opening_date = getFieldValueFromTermList("trading_table","opening_date",args,u);
				String closing_date = getFieldValueFromTermList("trading_table","closing_date",args,u);
				String conditions = getFieldValueFromTermList("trading_table","conditions",args,u);
				String access_type = getFieldValueFromTermList("trading_table","access_type",args,u);
				String deal = getFieldValueFromTermList("trading_table","deal",args,u);
				String protocol_parameters = getFieldValueFromTermList("trading_table","protocol_parameters",args,u);
				String num_iter_until_agreem = getFieldValueFromTermList("trading_table","num_iter_until_agreem",args,u);
				String time_until_agreem = getFieldValueFromTermList("trading_table","time_until_agreem",args,u);
				String num_participants = getFieldValueFromTermList("trading_table","num_participants",args,u);
				String opening_user = getFieldValueFromTermList("trading_table","opening_user",args,u);
				String protocol_type = getFieldValueFromTermList("trading_table","protocol_type",args,u);
				String role_when_opening_table = getFieldValueFromTermList("trading_table","role_when_opening_table",args,u);
				String number_of_opener_participations = getFieldValueFromTermList("trading_table","number_of_opener_participations",args,u);
				String trading_hall_id = getFieldValueFromTermList("trading_table","th_id",args,u);
				result = dbAPI.insertTradingTable(configuration_id, mwater_market, trading_table_id, 
						opening_date, closing_date, conditions, access_type, deal, protocol_parameters, num_iter_until_agreem, 
						time_until_agreem, num_participants, opening_user, protocol_type, role_when_opening_table, 
						number_of_opener_participations, trading_hall_id);
			}		

			if(functor.compareTo("+transfer_agreement")==0){
				String signature_date= getFieldValueFromTermList("transfer_agreement","signature_date",args,u);
				String state= getFieldValueFromTermList("transfer_agreement","state",args,u);
				String agreed_price = getFieldValueFromTermList("transfer_agreement","agreed_price",args,u);
				String aggregation_agreement = getFieldValueFromTermList("transfer_agreement","aggregation_agreement",args,u);
				String buyer_id = getFieldValueFromTermList("transfer_agreement","buyer_id",args,u);
				String waterright_id = getFieldValueFromTermList("transfer_agreement","water_right_id",args,u);
				String trading_table_id = getFieldValueFromTermList("transfer_agreement","trading_table_id",args,u);
				String mwater_market = getFieldValueFromTermList("transfer_agreement","wmarket",args,u);
				String configuration_id = getFieldValueFromTermList("transfer_agreement","configuration_id",args,u);
				result = dbAPI.insertTransferAgreement(signature_date, state, 
						agreed_price, aggregation_agreement, buyer_id, waterright_id, 
						trading_table_id, mwater_market, configuration_id);

			}		

			if(functor.compareTo("+agreed_contract")==0){
				String description = getFieldValueFromTermList("agreed_contract","description",args,u);
				String state = getFieldValueFromTermList("agreed_contract","state",args,u);
				String activation_date = getFieldValueFromTermList("agreed_contract","activation_date",args,u);
				String expiration_date = getFieldValueFromTermList("agreed_contract","expiration_date",args,u);
				String isPrivate = getFieldValueFromTermList("agreed_contract","isPrivate",args,u);
				String intended_water_use = getFieldValueFromTermList("agreed_contract","intended_water_use",args,u);
				String successful = getFieldValueFromTermList("agreed_contract","successful",args,u);
				String authorisation_date = getFieldValueFromTermList("agreed_contract","authorisation_date",args,u);
				String reasons_for_negation = getFieldValueFromTermList("agreed_contract","reasons_for_negation",args,u);
				String agreement = getFieldValueFromTermList("agreed_contract","agreement_id",args,u);
				result = dbAPI.insertAgreedContract(description, state, activation_date, 
						expiration_date, isPrivate, intended_water_use, successful, 
						authorisation_date, reasons_for_negation, agreement);
			}
	
			if(functor.compareTo("+accredited_user")==0){
				//logger.info("*****");
				String user_id = getFieldValueFromTermList("accredited_user","id",args,u);
				String wmarket = getFieldValueFromTermList("accredited_user","wmarket",args,u);
				String trust_value = getFieldValueFromTermList("accredited_user","trust_value",args,u);
				String sanction_value = getFieldValueFromTermList("accredited_user","sanction_value",args,u);
				//logger.info("***** - user_id: "+user_id+" wmarket: "+wmarket+" trust_value: "+trust_value+" sanction_value "+sanction_value);
				result = dbAPI.insertAccreditedUser(user_id, wmarket, trust_value,sanction_value);
			}

			if(functor.compareTo("+recruited_participant")==0){
				String trading_table = getFieldValueFromTermList("recruited_participant","trading_table_id",args,u);
				String wmarket = getFieldValueFromTermList("recruited_participant","wmarket",args,u);
				String configuration_id = getFieldValueFromTermList("recruited_participant","configuration_id",args,u);
				String user = getFieldValueFromTermList("recruited_participant","user_id",args,u);
				String invitation_condition = getFieldValueFromTermList("recruited_participant","invitation_condition",args,u);
				String invitation_date = getFieldValueFromTermList("recruited_participant","invitation_date",args,u);
				String accepted = getFieldValueFromTermList("recruited_participant","accepted",args,u);
				String acceptance_date = getFieldValueFromTermList("recruited_participant","acceptance_date",args,u);
				String number_of_participations = getFieldValueFromTermList("recruited_participant","number_of_participations",args,u);
				result = dbAPI.insertRecruitedParticipants(trading_table, wmarket, configuration_id, user, invitation_condition, invitation_date, 
						accepted, acceptance_date, number_of_participations);
			}
		} catch(NullPointerException npe) {
			logger.log(Level.SEVERE,"Arguments required for retrieving the information from the beliefs base are not correct.");
		} catch(IndexOutOfBoundsException iobe) {
			logger.log(Level.SEVERE,"Number of arguments incorrect for retrieving the information from the beliefs base.");
		} catch (DataManagementException e) {
			 //logger.log(Level.SEVERE, "SQL Error in functor "+functor);
			throw e;
		}		

		return result;
	}
	
	/*public Iterator<Literal> resultSetToLiteralIterator(ResultSet rs,String functor){
		List<Literal> result = new ArrayList<Literal>();

		ResultSetMetaData rsmd;
		try {
			rsmd = rs.getMetaData();
			int colCount = rsmd.getColumnCount();
			String columnName;
			Literal ldb ;
			Literal auxLiteral;

			while (rs.next()) // for each record
			{
				ldb = new LiteralImpl(functor);
				for (int c = 1; c <= colCount; c++) // for each column
				{
				
	            Term parsed = null;
	            columnName = rsmd.getColumnName(c);		
	            switch (rsmd.getColumnType(c)) {
	            case Types.INTEGER:
	            case Types.FLOAT:
	            case Types.DECIMAL:
	            case Types.DOUBLE:
	                parsed = new NumberTermImpl(rs.getDouble(c));
	                break;	            	
	            case Types.NUMERIC:
	            case Types.REAL:
	                parsed = new NumberTermImpl(rs.getDouble(c));
	                break;
				case Types.DATE:
					parsed = Date2ListTerm(rs.getDate(c));
					break;
	            case Types.TIMESTAMP:
	                //parsed = timestamp2structure(rs.getTimestamp(c));
	            	parsed = Date2ListTerm(rs.getDate(c));
	                break;

	            default:
	                String sc = rs.getString(c);
	                if (sc == null || sc.trim().length() == 0) {
	                    parsed = new StringTermImpl("");
	                } else if (Character.isUpperCase(sc.charAt(0))) {
	                    // there is no var at BB
	                    parsed = new StringTermImpl(sc);
	                } else {
	                    try {
	                        parsed = ASSyntax.parseTerm(sc);
	                    
	                        // if the parsed term is not equals to sc, try it as string
	                        if (!parsed.toString().equals(sc))
	                            parsed = ASSyntax.parseTerm(sc = "\"" + sc + "\"");
	                    } catch (ParseException e) {
	                        // can not be parsed, be a string
	                        parsed = new StringTermImpl(sc);
	                    } catch (TokenMgrError e) {
	                        // can not be parsed, be a string
	                        parsed = new StringTermImpl(sc);                        
	                    }
	                }
	                break;
	            }

				auxLiteral = new LiteralImpl(  getFieldFunctorFromFieldID(columnName, functor));
	            auxLiteral.addTerm(parsed);
	            ldb.addTerm(auxLiteral);
			}
				ldb.addAnnot(new LiteralImpl(Literal.parseLiteral("source(self)")));
				result.add(ldb);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		if (result.size()==0){return null;}
		else {return result.iterator();}
	}*/
	
	@Override
	public boolean add(Literal l) {
		logger.fine("--- add(Literal "+l.toString()+")");
		Unifier currentIntentionUnifier = null;

		String functor = l.getFunctor();
		String xml = null;
		boolean result = false;
		if (mwFunctors.containsKey(functor)){
			Unifier u = new Unifier();
			//logger.info("Antes del getKeyFunctorFrommwFunctor ... functor: "+functor);
			List<String> keysfunctors = getKeyFunctorFrommwFunctor(functor);
			//logger.info("Despues del getKeyFunctorFrommwFunctor ... keyfunctor: "+keysfunctors.get(0));
			boolean allKeysPresent = true;  int k = 0;
			String keyValue = "";
			while ((allKeysPresent)&&(k<keysfunctors.size())){
				keyValue = getFieldValueFromTermList(functor, keysfunctors.get(k), l.getTerms(), u);
				if ((keyValue==null)||(keyValue.compareTo("")==0))
					allKeysPresent = false;
				k++;
			}

			//Cheking if a belief with the same id exists
			if (allKeysPresent)
			{
				//logger.info("Antes del getCandidateBeliefs ... ");
				Iterator<Literal> candBel = getCandidateBeliefs(l, u);
				//logger.info("Despues del getCandidateBeliefs ... ");
				//There are matching beliefs on db
				if ((candBel!=null)&&(candBel.hasNext()))
				{
					Literal firstBel = candBel.next();
					
					if (!candBel.hasNext())
						//logger.info("*********HAY CREENCIAS CANDIDATAS PARA... "+l.toString());
						return  update(l, firstBel, functor);
						//logger.info("Despues del update ... ");
						
				}
			}
			
			try 
			{//updating id variable in belief base
				//logger.info("Antes del xml ... ");
				xml = mapFunctortoXML("+"+functor,l.getTerms(), u);
				//logger.info("Despues del xml ... ");
				String id = dbAPI.Extract_GeneratedKey_From_XML_String(xml);
				//logger.info("Despues del xml ... id: "+id);
				//Literal newLiteral = updateLiteralId(l,id, u);
				try
				{currentIntentionUnifier = myag.getTS().getC().getSelectedIntention().peek().getUnif();}
				catch (Exception e){//logger.info("Error getting the current intention unifier");
				}
				if (currentIntentionUnifier!=null) //Trying to unify ID in the literal to add
				{
					Term newkeyValue = null;
					Term keyTerm = searchMatchingTermValInTermList(keysfunctors.get(0),l.getTerms(),currentIntentionUnifier);
					if ((keyTerm!=null)&&(keyTerm.isVar()))
					try {
						newkeyValue = ASSyntax.parseTerm(id);
						currentIntentionUnifier.unifiesNoUndo(new VarTerm(keyTerm.toString()), newkeyValue);
						//logger.info("Unificando "+keyTerm.toString()+" con valor "+newkeyValue+" keysfunctors.get(0) "+keysfunctors.get(0));
					} catch (ParseException e) {
						newkeyValue = Literal.parse(id);
						currentIntentionUnifier.unifiesNoUndo(new VarTerm(keyTerm.toString()), newkeyValue);
					}
				}
				
				result = true;
			} catch (DataManagementException e) {
				logger.log(Level.SEVERE, "SQL Error in add for "+l, e);
			}

		}else
			result = super.add(l);

		return result;

	}
	
	public List<String> getKeyFunctorFrommwFunctor(String functor){
		List<String> result = new ArrayList<String>();
		if ((mwFunctors.containsKey(functor))&&(functor.compareTo("water_user")==0)){
			result.add("id");
		}
		if ((mwFunctors.containsKey(functor))&&(functor.compareTo("accredited_user")==0)){
			result.add("id");
			result.add("wmarket");
		}
		if ((mwFunctors.containsKey(functor))&&(functor.compareTo("water_right")==0)){
			result.add("id");
		}
		if ((mwFunctors.containsKey(functor))&&(functor.compareTo("trading_table")==0)){
			result.add("id");
			result.add("wmarket");
			result.add("configuration_id");
		}
		if ((mwFunctors.containsKey(functor))&&(functor.compareTo("configuration")==0)){
			result.add("id");
		}
		if ((mwFunctors.containsKey(functor))&&(functor.compareTo("water_market")==0)){
			result.add("id");
		}
		if ((mwFunctors.containsKey(functor))&&(functor.compareTo("trading_hall")==0)){
			result.add("id");
		}
		if ((mwFunctors.containsKey(functor))&&(functor.compareTo("agreement")==0)){
			result.add("id");
		}
		if ((mwFunctors.containsKey(functor))&&(functor.compareTo("transfer_agreement")==0)){
			result.add("id");
		}
		if ((mwFunctors.containsKey(functor))&&(functor.compareTo("contract")==0)){
			result.add("id");
		}
		if ((mwFunctors.containsKey(functor))&&(functor.compareTo("agreed_contract")==0)){
			result.add("id");
		}
		if ((mwFunctors.containsKey(functor))&&(functor.compareTo("protocol_type")==0)){
			result.add("id");
		}
		if ((mwFunctors.containsKey(functor))&&(functor.compareTo("recruited_participant")==0)){
			result.add("user_id");
			result.add("trading_table_id");
			result.add("wmarket");
			result.add("configuration_id");
		}
		if ((mwFunctors.containsKey(functor))&&(functor.compareTo("waterright_tt")==0)){
			result.add("trading_table_id");
			result.add("wmarket");
			result.add("configuration_id");
			result.add("water_right");
		}
		return result;
		
	}

	/**
	 *  Returns the same literal given as parameter */
	/*private Literal updateLiteralId(Literal l, String idvalue, Unifier u) {
		String functor = l.getFunctor();
		Literal result = new LiteralImpl(functor);
		List<Term> newTerms = null;
		
		List<String> keyid= getKeyFunctorFrommwFunctor(functor);
		
		if ((keyid.size()>0)&&(keyid.get(0).compareTo("")!=0)) newTerms = substituteTermInArgList(keyid.get(0),idvalue, l.getTerms());
		
		if (newTerms!=null)
		{
			result.addTerms(newTerms );
			return result;
		};
		
		return l;
	}*/
	
	
	private List<Term>  substituteTermInArgList(String matchFunctor, String newValue, List<Term> terms){
		int i = 0;


		if ((terms!=null)&(terms.size()>0)){
			String currentFunctor ;
			do
			{
				currentFunctor = ((LiteralImpl)terms.get(i)).getFunctor();
				i++;
			}while ((currentFunctor.compareTo(matchFunctor)!=0)&&(i<terms.size()));
			if (currentFunctor.compareTo(matchFunctor)==0){
				terms.remove(i-1);
				terms.add(i-1,Literal.parseLiteral(matchFunctor+"("+newValue+")"));
				return terms;
			}
		}
		return null;
	}

	/**
	 * Sends a query to the database and returns its result. The connection must be open.
	 * 
	 * @param query SQL sentence in {@code String} format.
	 * 
	 * @return {@code ResultSet}
	 *  
	 * @throws SQLException 
	 */
	/*private ResultSet QueryDB(String query) throws SQLException
	{
		if (conn != null)
		{
			try 
			{
				return this.conn.createStatement().executeQuery(query);
			} 
			catch (SQLException e) 
			{
				throw e;
			}
		}
		
		return null;
	}*/
	
	

	private Iterator<Literal> XMLtoLiteralIterator(String xml, String functor){
		List<Literal> result = new ArrayList<Literal>();

		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder;
			builder = factory.newDocumentBuilder();
			InputSource is = new InputSource();
			is.setCharacterStream(new StringReader(xml));   

			Document doc = builder.parse(is);
			NodeList items = doc.getElementsByTagName("Results").item(0).getChildNodes();
			NodeList fields;
			Term parsed = null;
			Literal auxLiteral;

			int fieldType ;
			String fieldName;
			String fieldValue;
					
			for (int i=0;i<items.getLength();i++){ //for each element
				Literal ldb = new LiteralImpl(functor) ;

				fields = items.item(i).getChildNodes();
				for (int j=0;j<fields.getLength();j++){  //for each field
					fieldName = fields.item(j).getNodeName();
					if (fields.item(j).getChildNodes().item(0)!=null)
					{
						fieldValue = fields.item(j).getChildNodes().item(0).getNodeValue();
						if (fieldValue.compareTo("null")==0) fieldValue = null;
					}
					else
						fieldValue = null;

					//fields.item(j).getChildNodes().item(0).getNodeType()
					fieldType = getFieldTypeFromFieldID(fieldName, functor);
					//logger.info(" fieldName: "+fieldName+" fieldValue: "+fieldValue+" fieldType:"+fieldType);
		            switch (fieldType) {
		            case Types.INTEGER:
		            case Types.FLOAT:
		            case Types.DECIMAL:
		            case Types.DOUBLE:
						if (fieldValue!=null)
							parsed = new NumberTermImpl(Double.parseDouble(fieldValue));
						else
							parsed = new NumberTermImpl(0);
		                break;	            	
		            case Types.NUMERIC:
						if (fieldValue!=null)
							parsed = new NumberTermImpl(Double.parseDouble(fieldValue));
   						else
							parsed = new NumberTermImpl(0);
						break;	 
		            case Types.REAL:
		               	if (fieldValue!=null)
							parsed = new NumberTermImpl(Double.parseDouble(fieldValue));
						else
							parsed = new NumberTermImpl(0);
		                break;
					case Types.DATE:
						parsed = strDate2ListTerm(fieldValue);
						break;
		            case Types.TIMESTAMP:
		                //parsed = timestamp2structure(rs.getTimestamp(c));
		            	parsed = strDate2ListTerm(fieldValue);
		                break;
		            case Types.VARCHAR:
						if (fieldValue!=null)
							parsed = ASSyntax.parseTerm(fieldValue = "\"" + fieldValue + "\"");
						else
							parsed = new StringTermImpl("");
		                break;
		            case Types.CHAR:
						if (fieldValue!=null)
							parsed = ASSyntax.parseTerm(fieldValue = "\"" + fieldValue + "\"");
						else
							parsed = new StringTermImpl("");
		                break;		                
		            default:
						String sc = fieldValue;
		                if (sc == null || sc.trim().length() == 0) {
		                    parsed = new StringTermImpl("");
		                } else {
		                    try {
		                        parsed = ASSyntax.parseTerm(sc);
		                        // if the parsed term is not equals to sc, try it as string
		                        if (!parsed.toString().equals(sc))
		                            parsed = ASSyntax.parseTerm(sc = "\"" + sc + "\"");
		                    } catch (ParseException e) {
		                        // can not be parsed, be a string
		                        parsed = new StringTermImpl(sc);
		                    } catch (TokenMgrError e) {
		                        // can not be parsed, be a string
		                        parsed = new StringTermImpl(sc);                        
		                    }
		                }
		                break;
		            }

					auxLiteral = new LiteralImpl(  getFieldFunctorFromFieldID(fieldName, functor));
		            auxLiteral.addTerm(parsed);
		            ldb.addTerm(auxLiteral);
				}

				ldb.addAnnot(new LiteralImpl(Literal.parseLiteral("source(self)")));
				result.add(ldb);
				//logger.info("+++ "+ldb.toString());
				}
			
			return result.iterator();
			}
		catch (Exception e) 
		{
			return null;	
		}


	} 
	
	public boolean update(Literal newl, Literal currentl, String functor){

		if (mwFunctors.containsKey(functor)&&(mwFunctors.get(functor).compareTo("")!=0)){
			Statement stmt = null;
			try {
				stmt = conn.createStatement();
				String updSet = getUpdateSet(newl, currentl, functor);

				if (updSet.trim().compareTo("")!=0) //there is something to update
					stmt.executeUpdate("update "+mwFunctors.get(functor)+getUpdateSet(newl, currentl, functor)+getRemoveUpdateWhere(currentl));
					
				//logger.info("update "+mwFunctors.get(functor)+"|"+getUpdateSet(newl, currentl, functor)+"|"+getRemoveUpdateWhere(currentl));
				return true;                    
			} catch (SQLException e) {
				logger.log(Level.SEVERE, "SQL Error", e);
			} finally {
				try {
					stmt.close();
				} catch (Exception e) {
					logger.log(Level.WARNING, "SQL Error closing connection", e);                    
				}
			}
		}
		return false;
	}
	

	public boolean remove(Literal l) {
		String functor = l.getFunctor(); 
		if (mwFunctors.containsKey(functor)&&(mwFunctors.get(functor).compareTo("")!=0)){

			Unifier u = new Unifier();
			Iterator<Literal> bl = getCandidateBeliefs(l, u);

			if ((bl != null)&&(bl.hasNext())) {
				Statement stmt = null;
				try {
					stmt = conn.createStatement();
					stmt.executeUpdate("delete from "+mwFunctors.get(functor)+getRemoveUpdateWhere(l));
					//logger.info("delete from "+mwFunctors.get(functor)+getRemoveUpdateWhere(l));
					return true;                    
				} catch (SQLException e) {
					logger.log(Level.SEVERE, "SQL Error", e);
				} finally {
					try {
						stmt.close();
					} catch (Exception e) {
						logger.log(Level.WARNING, "SQL Error closing connection", e);                    
					}
				}


			}
		}else
			return super.remove(l);
		return false;
	}
	
	/** 
	 * returns the set clausule given the old literal and the new literal. */ 
	protected String getUpdateSet(Literal newl, Literal currentl, String literalFunctor)  {

		if ((newl!=null)&&(currentl!=null)&&(newl.getFunctor().compareTo(currentl.getFunctor())==0)&&
				(newl.getArity()==currentl.getArity())&&
				(newl.getFunctor().compareTo(literalFunctor)==0)){ //both literals are comparable
			int i = 0;
			boolean allTermMatch = true; //is true only if all terms in both literals match
			StringBuilder q = new StringBuilder(" ");
			String separator = " set ";
			String fieldNamefunctor = "";
			Term newt;
			Term currentt;
			String newv="";
			String currentv = "";

			List<String> ids = getKeyFunctorFrommwFunctor(literalFunctor);
			
			
			while((i<newl.getArity())&&(allTermMatch))
			{
				newt = newl.getTerm(i);  //term in the way 'fieldName(FieldValue)'
				currentt = currentl.getTerm(i);
				if ((newt.isLiteral())&&(currentt.isLiteral())&&
						(((Literal)newt).getFunctor().compareTo(((Literal)currentt).getFunctor())==0)) {
					
					fieldNamefunctor = ((Literal)newt).getFunctor();
					newv = searchFieldValueInTermList(literalFunctor,i,newl.getTerms());
					currentv = searchFieldValueInTermList(literalFunctor,i,currentl.getTerms());
					
					//if the term is an id it is not updated
					//if there is no new value it is not added to be updated
					if ((newv!=null)&&(!ids.contains(fieldNamefunctor))) 
					{
						if ((currentv!=null)&&(currentv.compareTo(newv)!=0)) //if values differ
						{
							q.append(separator);
							q.append(getFieldIDFromFunctor(fieldNamefunctor,literalFunctor) + " = " + newv);
							separator = ", ";
						}else{
							if ((currentv==null)&&(currentv!=newv))
							{
								q.append(separator);
								q.append(getFieldIDFromFunctor(fieldNamefunctor,literalFunctor) + " = " + newv);
								separator = ", ";
							}
						}
					}
				}else
					allTermMatch=false;
				i++;
			}
			//System.out.println(q.toString());
			if (allTermMatch) 
				{
				return q.toString();
				}
		}
		return "";// add nothing in the clausule 
	}
		
	/** 
	 * returns the where clausule for a select for literal l */ 
	protected String getRemoveUpdateWhere(Literal l)  {

			StringBuilder q = new StringBuilder(" where ");
			String and = "";
			String fieldNamefunctor = "";
			String fieldValue = "";
			String literalFunctor = l.getFunctor();
			// for all ground terms of l
			for (int i = 0; i < l.getArity(); i++) {
				Term t = l.getTerm(i);  //term in the way 'fieldName(FieldValue)'
				if (t.isLiteral()) {
					fieldNamefunctor = ((LiteralImpl)t).getFunctor();
					//fieldValue = ((LiteralImpl)t).getTerm(0).toString();
					
					fieldValue = searchFieldValueInTermList(literalFunctor,i,l.getTerms());
					if ((fieldValue!=null)&&(fieldValue.compareTo("")!=0))
					{
						q.append(and);
						q.append(getFieldIDFromFunctor(fieldNamefunctor,literalFunctor) + " = " + fieldValue);
					}
					
					and = " and ";
				}
			}

			//System.out.println(q.toString());
			if (and.length() > 0) 
				return q.toString();
			else
				return "";// add nothing in the clausule 
		}	

	
}
