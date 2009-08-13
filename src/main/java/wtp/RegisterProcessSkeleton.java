 /**
 * RegisterProcessSkeleton.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.2 Apr 27, 2007 (04:35:37 IST)
 */
package wtp;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.RemoteException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Properties;
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

import java.util.*;

import persistence.DataBaseInterface;


/**
 * RegisterProcessSkeleton java skeleton for the axisService
 */
public class RegisterProcessSkeleton {


	public static final Boolean DEBUG = true;


	// database connection parameters, with defaults
	private static String s_dbURL;
	private static String s_dbUser;
	private static String s_dbPw;
	private static String s_dbType;
	private static String s_dbDriver;

	/**
	 * RegisterProcess
	 * @param registerProcess:
	 *  service id: string, serviceprofileid
	 *  service model: string, urlprocess
	 *  agent id: string
	 * @return 
	 * 	flag: 1:ok , 0: bad news
	 *  service implementation id: serviceprofileid@agentid
	 */
	public wtp.RegisterProcessResponse RegisterProcess(wtp.RegisterProcess registerProcess) {

		
		wtp.RegisterProcessResponse response = new wtp.RegisterProcessResponse();

		if (DEBUG) {
			System.out.println("RegisterProcess Service :");
			System.out.println("***AgentID... " + registerProcess.getAgentID());
			System.out.println("***ServiceID... "+ registerProcess.getServiceID());
			System.out.println("***ServiceModel... "+ registerProcess.getServiceModel());
		}

		// Register de serviceimplementationid in the DB
		persistence.DataBaseInterface thomasBD = new DataBaseInterface();
		String serviceprocessid = thomasBD.AddNewProcess(registerProcess.getServiceModel(), registerProcess.getServiceID(),registerProcess.getAgentID());
		
		 
		if (serviceprocessid != null) {

			/////////////
			////JENA/////
			/////////////
			IDBConnection conn = null;
			OntModel m = null;

			Properties properties = new Properties();

			try {
				properties.loadFromXML(RegisterProcessSkeleton.class.getResourceAsStream("/"+"THOMASDemoConfiguration.xml"));
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
			ModelRDB model = (ModelRDB) maker.openModel("http://example.org/ontologias");

			// use the model maker to get the base model as a persistent model
			// strict=false, so we get an existing model by that name if it
			// exists
			// or create a new one
			String urlprocess = registerProcess.getServiceModel();
			StringTokenizer Tok = new StringTokenizer(urlprocess);
			urlprocess = Tok.nextToken("#");
			String processname = Tok.nextToken();

			
				System.out.println("URL profile: "+ urlprocess);
				System.out.println("profile name: "+ processname);
			if (DEBUG) {
				System.out.println("File to load... "+ urlprocess);
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

			try {
				if (DEBUG) {
					System.out.println("Closing DB connection...");
				}
				conn.close();
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}

			// Close the model
			m.close();
			response.setServiceModelID(serviceprocessid);
			response.set_return(1);

		} else {
			response.setServiceModelID("[Error]: the service profile does not exists");
			response.set_return(0);
		}

		return (response);

	}
	


}
