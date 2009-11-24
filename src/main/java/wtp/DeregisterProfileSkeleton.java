

    /**
     * DeregisterProfileSkeleton.java
     *
     * This file was auto-generated from WSDL
     * by the Apache Axis2 version: 1.2 Apr 27, 2007 (04:35:37 IST)
     */
    package wtp; 
    import com.hp.hpl.jena.db.DBConnection;
    import com.hp.hpl.jena.db.IDBConnection;
    import com.hp.hpl.jena.ontology.OntModel;
    import com.hp.hpl.jena.query.Query;
    import com.hp.hpl.jena.update.*;
    import com.hp.hpl.jena.query.QueryExecution;
    import com.hp.hpl.jena.query.QueryExecutionFactory;
    import com.hp.hpl.jena.query.QueryFactory;
    import com.hp.hpl.jena.query.QuerySolution;
    import com.hp.hpl.jena.query.QuerySolutionMap;
    import com.hp.hpl.jena.query.ResultSet;
    import com.hp.hpl.jena.rdf.model.Model;
    import com.hp.hpl.jena.rdf.model.ModelFactory;
    import com.hp.hpl.jena.rdf.model.ModelMaker;
    import com.hp.hpl.jena.ontology.OntModelSpec;
    import java.util.*; 
import java.io.*;

import persistence.DataBaseInterface;
    
    /**
     *  DeregisterProfileSkeleton java skeleton for the axisService
     */
    public class DeregisterProfileSkeleton{
        
 
    	public static final Boolean DEBUG = true;

    	// database connection parameters, with defaults
    	private static String s_dbURL;
    	private static String s_dbUser;
    	private static String s_dbPw;
    	private static String s_dbType;
    	private static String s_dbDriver;
    	
    	
        /**
         * DeregisterProfile
         * @param DeregisterProfile contains two elements: service id (is a string: service profile id) 
         * and agent id (is a string).
         * @return DeregisterProfileResponse contains an element: return indicates if an error occurs (
         * 0: ok, 1:error).
         */
         public wtp.DeregisterProfileResponse DeregisterProfile ( wtp.DeregisterProfile deregisterProfile )
         {
        	 
        	 wtp.DeregisterProfileResponse response = new wtp.DeregisterProfileResponse();
        	 if (DEBUG) {
     			System.out.println("DeregisterProfile Service :");
     			System.out.println("***ServiceID..."+ deregisterProfile.getServiceID());
     			System.out.println("***AgentID..."+ deregisterProfile.getAgentID());
     		}
        	 
        	// Get the service process 
        	 persistence.DataBaseInterface thomasBD = new DataBaseInterface();
        	 
        	 String urlprofile = thomasBD.GetServiceProfileURL(deregisterProfile.getServiceID());
        	 String profilename = thomasBD.GetServiceProfileName(deregisterProfile.getServiceID());
        	 Boolean hasProcess = thomasBD.CheckIfProfileHasProcess(deregisterProfile.getServiceID());
             if (urlprofile!=null && profilename!=null && !hasProcess) {
	 
        		 // Cargamos los valores desde un archivo .xml
        		 Properties properties = new Properties();

        		 try {
        			 properties.loadFromXML(DeregisterProfileSkeleton.class.getResourceAsStream("/"+ "THOMASDemoConfiguration.xml"));
        			 for (Enumeration e = properties.keys(); e.hasMoreElements();) {
        				 // Obtenemos el objeto
        				 Object obj = e.nextElement();
        				 if (obj.toString().equalsIgnoreCase("DB_URL")) {
        					 s_dbURL = properties.getProperty(obj.toString());
        				 } else if (obj.toString().equalsIgnoreCase("DB_USER")) {
        					 s_dbUser = properties.getProperty(obj.toString());
        				 } else if (obj.toString().equalsIgnoreCase("DB_PASSWD")) {
        					 s_dbPw = properties.getProperty(obj.toString());
        				 } else if (obj.toString().equalsIgnoreCase("DB")) {
        					 s_dbType = properties.getProperty(obj.toString());
        				 } else if (obj.toString().equalsIgnoreCase("DB_DRIVER")) {
        					 s_dbDriver = properties.getProperty(obj.toString());
        				 }
        			 }

        		 } catch (IOException e) {
        			 System.out.print(e);
        		 }

        		 IDBConnection conn = null;

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

        		 ModelMaker maker = ModelFactory.createModelRDBMaker(conn);

        		 // use the model maker to get the base model as a persistent model
        		 // strict=false, so we get an existing model by that name if it
        		 // exists
        		 // or create a new one

        		 Model base = maker.createModel("http://example.org/ontologias");
        		 // now we plug that base model into an ontology model that also uses
        		 // the given model maker to create storage for imported models
        		 OntModel m = ModelFactory.createOntologyModel(getModelSpec(maker),base);

        		 
        		 

        		 if (DEBUG) {
        			 System.out.println("URL profile: " + urlprofile);
        			 System.out.println("profile name: " + profilename);
        		 }

        		 // Delete profile tuples where the property is profile
        		 String update = "prefix xsd: <http://www.w3.org/2001/XMLSchema#>"
					+ "prefix service: <http://www.daml.org/services/owl-s/1.1/Service.owl#>"
					+ "prefix proc: <http://www.daml.org/services/owl-s/1.1/Process.owl#>"
					+ "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>"
					+ "prefix mind: <" + urlprofile + "#>" +

					"delete {?x ?y ?z}" + "where" + "{" + "mind:" + profilename
					+ " ?y ?z" + " filter ( ?y = profile:hasInput "
					+ "|| ?y = profile:hasOutput "
					+ "|| ?y = profile:serviceName "
					+ "|| ?y = service:isPresentedBy "
					+ "|| ?y = service:presents " + "|| ?z = profile:Profile "
					+ ")" + "?x ?y ?z}";

        		 // Query to get the service name related with the profile
        		 String queryStringServiceName = "prefix xsd: <http://www.w3.org/2001/XMLSchema#>"
					+ "prefix service: <http://www.daml.org/services/owl-s/1.1/Service.owl#>"
					+ "prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>"
					+ "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>"
					+ "prefix actor: <http://www.daml.org/services/owl-s/1.1/ActorDefault.owl#>"
					+ "prefix mind: <"
					+ urlprofile
					+ "#>"
					+ "select ?x "
					+ "where {"
					+ "mind:"
					+ profilename
					+ " service:presentedBy ?x " + "}";

        		 Query queryServiceName = QueryFactory.create(queryStringServiceName);

        		 // Execute the query and obtain results
        		 QueryExecution qeServiceName = QueryExecutionFactory.create(queryServiceName, m);
        		 ResultSet resultsServiceName = qeServiceName.execSelect();
        		 if (resultsServiceName.hasNext()) {
        			 System.out.println("no es null");
        		 }
        		 String ServiceName = resultsServiceName.next().toString();
        		 System.out.println("Service Name: " + ServiceName);

        		 // Query Result format
        		 // ( ?x =
        		 // <http://paracetamol.dsic.upv.es:8080/SF/OWLS/SearchCheapHotelProfile.owl#SearchCheapHotelService>
        		 // )
        		 StringTokenizer Tok = new StringTokenizer(ServiceName);
        		 String urlname = Tok.nextToken("#");
        		 System.out.println("urlname: " + urlname);
        		 String servicename = Tok.nextToken();
        		 System.out.println("servicename: " + servicename);
        		 Tok = new StringTokenizer(servicename);
        		 String name = Tok.nextToken(">");

        		 if (DEBUG) {
        			 System.out.println("Service name: " + name);
        		 }

        		 // Delete tuples where the property is service (it is related with
        		 // the profile)
        		 String update2 = "prefix xsd: <http://www.w3.org/2001/XMLSchema#>"
					+ "prefix service: <http://www.daml.org/services/owl-s/1.1/Service.owl#>"
					+ "prefix proc: <http://www.daml.org/services/owl-s/1.1/Process.owl#>"
					+ "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>"
					+ "prefix mind: <" + urlprofile + "#>" +

					"delete {?x ?y ?z}" + "where" + "{" + "mind:" + name
					+ " ?y ?z" + " filter ( ?z = service:Service "
					+ "|| ?y = service:presents " + ")" + "?x ?y ?z}";

        		 // Execute the query and obtain results
        		 QuerySolution querysol = new QuerySolutionMap();
        		 UpdateAction.parseExecute(update, m, querysol);
        		 UpdateAction.parseExecute(update2, m, querysol);

        		 if (DEBUG) {
        			 System.out.println("DB after delete profile ... ");
        			 m.write(System.out, "N3");
        		 }

        		 m.commit();
        		 m.close();

        		
        		 
        		 thomasBD.DeleteProfile(deregisterProfile.getServiceID());
        		 response.set_return(1);
        		 try {
        			 if (DEBUG) {
        				 System.out.println("Closing DB connection...");
        			 }
        			 conn.close();
        		 } catch (Exception e) {
        			 e.printStackTrace();
        			 System.exit(1);
        		 }
        		 return (response);
        	 } else {
        		 response.set_return(0);
        		 return (response);
        	 }
        	 
        	 
         }

         
         
         
	public OntModelSpec getModelSpec(ModelMaker maker) {
		// create a spec for the new ont model that will use no inference over
		// models made by the given maker (which is where we get the persistent models
		// from)
		OntModelSpec spec = new OntModelSpec(OntModelSpec.OWL_MEM);
		spec.setImportModelMaker(maker);

		return spec;
	}
}