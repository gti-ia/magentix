/**   
 * RegisterProfileSkeleton.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.2 Apr 27, 2007 (04:35:37 IST)
 */
package wtp;

import com.hp.hpl.jena.db.*;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.*; 
import com.hp.hpl.jena.query.*;

import java.rmi.RemoteException;
import java.util.*;


/**
 * RegisterProfileSkeleton java skeleton for the axisService
 */
public class RegisterProfileSkeleton {

	public static final String DB_URL = "jdbc:mysql://localhost/thomas";
	public static final String DB_USER = "thomas";
	public static final String DB_PASSWD = "thomas";
	public static final String DB = "MySQL";
	public static final String DB_DRIVER = "com.mysql.jdbc.Driver";
	public static final Boolean DEBUG = true;

	// database connection parameters, with defaults
	private static String s_dbURL = DB_URL;
	private static String s_dbUser = DB_USER;
	private static String s_dbPw = DB_PASSWD;
	private static String s_dbType = DB;
	private static String s_dbDriver = DB_DRIVER;

	/**
	 * RegisterProfile 
	 * @param registerProfile contains the service goal and profile
	 * @return response contains serviceID and a control parameter
	 * @throws RemoteException
	 */

	public wtp.RegisterProfileResponse RegisterProfile(wtp.RegisterProfile registerProfile) {

		wtp.RegisterProfileResponse response = new wtp.RegisterProfileResponse();
		IDBConnection conn = null;
		OntModel m = null;
		//wtp.OWLSValidatorStub.OWLSValidator validator= null;
		//int validationResult=0;
		
		if (DEBUG) {
			System.out.println("RegisterProfile Service :");
			System.out.println("***ServiceGoal... "	+ registerProfile.getServiceGoal());
			System.out.println("***ServiceProfile... "	+ registerProfile.getServiceProfile());
		}
		
		//Check if the profile is correct
		/*try{
			OWLSValidatorStub stub = new OWLSValidatorStub();
			validator = new wtp.OWLSValidatorStub.OWLSValidator();
			validator.setURL(registerProfile.getServiceProfile());
			validationResult = stub.OWLSValidator(validator).get_return();	
		}catch(RemoteException e) {
			e.printStackTrace();
		}
		
		if(validationResult == 1){
		*/
			// ensure the JDBC driver class is loaded
			try {
				Class.forName(s_dbDriver);
			} catch (Exception e) {
				System.err.println("Failed to load the driver for the database: " + e.getMessage());
				System.err.println("Have you got the CLASSPATH set correctly?");
			}

		
			if (DEBUG) {
				System.out.println("File to load ... "	+ registerProfile.getServiceProfile());
			}
		
			// Create database connection
			try {
				conn = new DBConnection(s_dbURL, s_dbUser, s_dbPw, s_dbType);
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		
			ModelMaker maker = ModelFactory.createModelRDBMaker(conn);
			Model base = maker.createModel("http://example.org/ontologias");
			// now we plug that base model into an ontology model that also uses 
			// the given model maker to create storage for imported models
			OntModelSpec spec = new OntModelSpec(OntModelSpec.OWL_MEM);
			spec.setImportModelMaker(maker);
			m = ModelFactory.createOntologyModel(spec, base);
		
			//load the service profile in the database
			m.read(registerProfile.getServiceProfile());
			m.commit();
		
			if (DEBUG) {
				m.write(System.out, "N3");
			}
		
			//Query to get the profiles
			String queryStringProfiles =
				"prefix xsd: <http://www.w3.org/2001/XMLSchema#>"
				+ "prefix service: <http://www.daml.org/services/owl-s/1.1/Service.owl#>"
				+ "prefix proc: <http://www.daml.org/services/owl-s/1.1/Process.owl#>"
				+ "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>"
				+ "prefix actor: <http://www.daml.org/services/owl-s/1.1/ActorDefault.owl#>"
				+ "select ?x " 
				+ "where {" 
				+ "      ?x a       profile:Profile  ."
				+ "      }";

			Query queryProfiles = QueryFactory.create(queryStringProfiles);
		
			if (DEBUG) {
				System.out.println(queryProfiles.toString());
			}
		
			// Execute the query and obtain results
			QueryExecution qeProfiles = QueryExecutionFactory.create(queryProfiles, m);
			ResultSet Profilesresults = qeProfiles.execSelect();

			//Scan the list of Profiles
			for (Iterator j = Profilesresults; Profilesresults.hasNext();) {

				String result = Profilesresults.next().toString();

				StringTokenizer Tok = new StringTokenizer(result);
				Tok.nextToken("<");
				String ProfileID = Tok.nextToken(">");
				ProfileID = ProfileID.replace("<","");
				if (DEBUG) {
					System.out.println("ProfileID: " + ProfileID);
				}
			
				Tok = new StringTokenizer(result);
				Tok.nextToken("<");
				String profile = Tok.nextToken("#");
				profile = profile.replace("<","");
				
				if (DEBUG) {
					System.out.println("Profile " + profile);
				}

				//If the Name of the Service profile owl document is equal to the profile founded
				//we get its serviceID
				if (profile.equals(registerProfile.getServiceProfile())) {
					response.setServiceID(ProfileID);
					response.set_return(1);
				}
			}//end for
		
			// close the query
			qeProfiles.close();

			try {
				if (DEBUG) {
					System.out.println("Closing DB connection...");
				}
				conn.close();
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
			
		//}//end if
		
		//The profile is not valid
		/*else{
			// Answer
			response.set_return(0);
			response.setServiceID("");
			System.err.println("[ERROR]: the profile is not valid");
		}//end else
		*/
		//Close the model 
		m.close();
		return (response);
	
	}//end RegisterProfile

}//end class