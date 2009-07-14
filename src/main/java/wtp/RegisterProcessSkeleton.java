/**
 * RegisterProcessSkeleton.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.2 Apr 27, 2007 (04:35:37 IST)
 */
package wtp;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.rmi.RemoteException;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.io.*;
import java.net.*;

import com.hp.hpl.jena.db.DBConnection;
import com.hp.hpl.jena.db.IDBConnection;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.ModelMaker;
import com.hp.hpl.jena.db.*;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.query.*;


/**
 * RegisterProcessSkeleton java skeleton for the axisService
 */
public class RegisterProcessSkeleton {

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
	 * RegisterProcess
	 * @param registerProcess contains the service Id and the service process and grounding
	 * @return response contains 1:OK otherwise 0
	 */
	public wtp.RegisterProcessResponse RegisterProcess(wtp.RegisterProcess registerProcess) {

		wtp.RegisterProcessResponse response = new wtp.RegisterProcessResponse();
		IDBConnection conn = null;
		OntModel m = null;
		
		if (DEBUG) {
			System.out.println("RegisterProcess Service :");
			System.out.println("***ServiceID... "+ registerProcess.getServiceID());
			System.out.println("***ServiceModel... "+ registerProcess.getServiceModel());
		}
		
		// ensure the JDBC driver class is loaded
		try {
			Class.forName(s_dbDriver);
		} catch (Exception e) {
			System.err.println("Failed to load the driver for the database: "+ e.getMessage());
			System.err.println("Have you got the CLASSPATH set correctly?");
		}

		// Create database connection
		try {
			conn = new DBConnection(s_dbURL, s_dbUser, s_dbPw, s_dbType);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}

		StringTokenizer Tok = new StringTokenizer(registerProcess.getServiceID());
		String profile = Tok.nextToken("#");
		
		if (DEBUG) {
			System.out.println("Profile " + profile);
		}
		
		/*ConcatFiles(registerProcess.getServiceModel(),profile);
		
		wtp.OWLSValidatorStub.OWLSValidator validator= null;
		int validationResult=0;
		try{
			OWLSValidatorStub stub = new OWLSValidatorStub();
			validator = new wtp.OWLSValidatorStub.OWLSValidator();
			validator.setURL("file:///home/usuario/Escritorio/ThomasSF/RegisterProcess/merge.owl");
			validationResult = stub.OWLSValidator(validator).get_return();	
		}catch(RemoteException e) {
			e.printStackTrace();
		}
		
		if(validationResult == 1){
		*/
			ModelMaker maker = ModelFactory.createModelRDBMaker(conn);
			ModelRDB model = (ModelRDB) maker.openModel("http://example.org/ontologias");

			// use the model maker to get the base model as a persistent model 
			// strict=false, so we get an existing model by that name if it exists
			// or create a new one
			if (DEBUG) {
				System.out.println("File to load... "
					+ registerProcess.getServiceModel());
			}

			// now we plug that base model into an ontology model that also uses
			// the given model maker to create storage for imported models
			OntModelSpec spec = new OntModelSpec(OntModelSpec.OWL_MEM);
			spec.setImportModelMaker(maker);
		    m = ModelFactory.createOntologyModel(spec, model);
		
			m.read(registerProcess.getServiceModel());
			m.commit();
			
			if (DEBUG) {
				m.write(System.out, "N3");
			}
			
			//Query to get the service name
			String queryStringProcessID ="prefix xsd: <http://www.w3.org/2001/XMLSchema#>"
				+ "prefix service: <http://www.daml.org/services/owl-s/1.1/Service.owl#>"
				+ "prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>"
				+ "prefix prof: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>"
				+ "prefix actor: <http://www.daml.org/services/owl-s/1.1/ActorDefault.owl#>"
				+ "select ?x " 
				+ "where {" 
				+ "    ?x a       process:AtomicProcess  "
				+ "      }";
		
			Query queryProcess = QueryFactory.create(queryStringProcessID);
		
			// Execute the query and obtain results
			QueryExecution qeProcess = QueryExecutionFactory.create(queryProcess, m);
			ResultSet ProcessResults = qeProcess.execSelect();
		
		
			//Scan the list of Profiles
			for (Iterator j = ProcessResults; ProcessResults.hasNext();) {

				String result = ProcessResults.next().toString();
				Tok = new StringTokenizer(result);
				Tok.nextToken("<");
				String ModelID = Tok.nextToken(">");
				ModelID = ModelID.replace("<", "");
				if (DEBUG) {
					System.out.println("ModelID: " + ModelID);
				}
			
				Tok = new StringTokenizer(result);
				Tok.nextToken("<");
				String process = Tok.nextToken("#");
				process = process.replace("<","");
				
				if (DEBUG) {
					System.out.println("Process " + process);
				}
			
				//If the Name of the Service profile owl document is equal to the profile founded
				//we get its serviceID
				if (process.equals(registerProcess.getServiceModel())) {
					response.setServiceModelID(ModelID);
					response.set_return(1);
				}
			}
			// close the query
			qeProcess.close();
		//}//end if validation
		/*else{
			
			response.set_return(0);
			response.setServiceModelID("");
			System.err.println("[ERROR]: the process is not valid");
		}*/
			
	
		try {
			if (DEBUG) {
				System.out.println("Closing DB connection...");
			}
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}	
		
		//Close the model 
		m.close();
		return (response);

	}
	

	/**
	 * ConcatFiles
	 * @param process contains the service process URL
	 * @param profile contains the service profile URL
	 * @return 
	 * @throws IOException
	 */
	void ConcatFiles(String process, String profile){
	
		try {

			// service process and profile URLs
			URL urlProcess = new URL(process);
			URL urlProfile = new URL(profile);
		
			// Buffers 
			BufferedReader in = null;
			BufferedWriter out = new BufferedWriter(new FileWriter("/home/usuario/Escritorio/ThomasSF/RegisterProcess/merge.owl"));
		
			try {
				//PROCESS
				// read the process and put it in the buffer
				in = new BufferedReader(new InputStreamReader(urlProcess.openStream()));
			} catch(Throwable t){}
			
	
			// Transform the buffer content into a text
			String inputLine;
			String inputText="";

			// Analyze the lines in the bufferReader
			// and transform them into string lines
			while ((inputLine = in.readLine()) != null){
				if(!inputLine.contains("</rdf:RDF>")/*&& (!inputLine.contains("<process:hasServer"))*/ )                 
					/*if (inputLine.contains("<process:hasParticipant")){//<process:hasParticipant rdf:ID="SearchCheapHotelAgentA">
						in.readLine();//<rdfs:label>Agente A es el provider</rdfs:label>
						in.readLine();//<process:parameterType rdf:datatype="http://www.w3.org/2001/XMLSchema#anyURI">http://www.w3.org/2001/XMLSchema#string</process:parameterType>
						in.readLine();//</process:hasParticipant>
					}
					else*/ if (inputLine.contains("<service:presents")){
						StringTokenizer Tok = new StringTokenizer(inputLine);
						String serviceProfile= Tok.nextToken("#");
						serviceProfile=Tok.nextToken("\"/>");
						inputText = inputText + "<service:presents rdf:resource=\""+serviceProfile+"\"/>";
					}
					else
						inputText = inputText + inputLine;
			}

			// Show the URL content
			if(DEBUG){
				System.out.println("El contenido de la URL es: " + inputText);
			}
			
			
			//PROFILE
			// read the process and put it in the buffer
			try {
				in = new BufferedReader(new InputStreamReader(urlProfile.openStream()));
			} catch(Throwable t){}
      
			// Analyze the lines in the bufferReader
			// and transform them into string lines 
			while ((inputLine = in.readLine()) != null){
				if (inputLine.contains("<profile:")||inputLine.contains("</profile:"))
					inputText = inputText + inputLine;
				else if(inputLine.contains("<service:"))
					inputText = inputText + inputLine;
					
			}
			
			// Show the URL content
			inputText = inputText +"</rdf:RDF> ";
			if(DEBUG){
				System.out.println("El contenido de la URL es: " + inputText);
			}
			
			out.write(inputText);
		    out.close();
			in.close();
				
		} catch (IOException ioe) {
			System.err.println("[ERROR] IO");
		}
    
	}//end concatFiles

}
