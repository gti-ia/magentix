package es.upv.dsic.gti_ia.sfnew;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Properties;
import java.util.StringTokenizer;

import com.hp.hpl.jena.db.DBConnection;
import com.hp.hpl.jena.db.IDBConnection;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.QuerySolutionMap;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.ModelMaker;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.update.UpdateAction;

import edu.emory.mathcs.backport.java.util.Collections;

/**
 * This class implements the Service Facilitator (SF) services. 
 * @author Jaume Jordan
 *
 */
public class SFinterface {

	private static boolean DEBUG = true;

	private IDBConnection conn;

	public SFinterface(){
		super();
		conn=JenaDBConnection();
	}
	
	/*
	 * Observaciones:
	 * - Valorar si hay que incluir los diferentes process en Jena, ya que pueden aportar 
	 * resultados distintos, estar ligados a cada grounding y tener nombres de parámetros
	 * distintos (lo que podría dar problemas también con el profile)
	 * - Es necesario enganchar el proveedor con su grounding, para poder ejecutar luego??
	 * - Las búsquedas deberían incluir el nombre del servicio, su descripción... etc, para
	 * que fueran más reales. Ahora mismo, dos servicios de compra y venta, podrían tener los
	 * mismos parámetros y los alinearíamos como si fueran el mismo...
	 */


	public void testQuery(){
		

	}

	
	

	/**
	 * Search in the Jena DB if it exists a registered service with the same given inputs and outputs
	 * @param inputs of the service to search specified as parameter type
	 * @param outputs of the service to search specified as parameter type
	 * @return the profile URI if it exists an equal registered service, or an empty String if not
	 */
	private String searchRegisteredServices(ArrayList<String> inputs, ArrayList<String> outputs){

		//search the inputs

		ModelMaker maker = ModelFactory.createModelRDBMaker(conn);
		Model base = maker.createModel("http://example.org/ontologias");
		OntModel m = ModelFactory.createOntologyModel(getModelSpec(maker), base);

		ArrayList<String> candidates=new ArrayList<String>();
		boolean firstInput=true;
		Iterator<String> iterInputs=inputs.iterator();
		while(iterInputs.hasNext()){

			String in=iterInputs.next();

			ArrayList<String> candN=new ArrayList<String>();

			String queryStr =
					"prefix xsd: <http://www.w3.org/2001/XMLSchema#>"+
							"prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>"+
							"prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>"+
							"select ?x where { ?x a process:Input ; process:parameterType "+in+" . }";

			Query query = QueryFactory.create(queryStr);

			// Execute the query and obtain results
			QueryExecution querySearchInputs = QueryExecutionFactory.create(query, m);
			ResultSet resultsSearchInputs = querySearchInputs.execSelect();

			if (resultsSearchInputs != null) {

				while (resultsSearchInputs.hasNext()) {
					QuerySolution sol=resultsSearchInputs.next();

					Resource resource=sol.getResource("x");
					String param=resource.getURI();
					String cand=getProfileURI(param, true);
					
					if(!candN.contains(cand))
						candN.add(cand);

				}//end for 
			}//end if

			if(!candidates.isEmpty()){
				candidates.retainAll(candN);
			}
			if(firstInput){
				candidates.addAll(candN);
				firstInput=false;
			}


		}

		//search the outputs
		Iterator<String> iterOutputs=outputs.iterator();
		while(iterOutputs.hasNext()){

			String out=iterOutputs.next();

			ArrayList<String> candN=new ArrayList<String>();

			String queryStr =
					"prefix xsd: <http://www.w3.org/2001/XMLSchema#>"+
							"prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>"+
							"prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>"+
							"select ?x where { ?x a process:Output ; process:parameterType "+out+" . }";

			Query query = QueryFactory.create(queryStr);

			// Execute the query and obtain results
			QueryExecution querySearchOutputs = QueryExecutionFactory.create(query, m);
			ResultSet resultsSearchOutputs = querySearchOutputs.execSelect();

			if (resultsSearchOutputs != null) {

				while (resultsSearchOutputs.hasNext()) {
					QuerySolution sol=resultsSearchOutputs.next();

					Resource resource=sol.getResource("x");
					String param=resource.getURI();
					String cand=getProfileURI(param, false);
					
					if(!candN.contains(cand))
						candN.add(cand);

				}//end for 
			}//end if

			if(!candidates.isEmpty()){
				candidates.retainAll(candN);

			}

		}


		if(candidates.isEmpty()){
			return "";
		}
		else{//check if the inputs and outputs are the same exactly 
			//(could arrive here having a service with more inputs 
			//than the specified as parameter and will be different services) 


			
			ArrayList<String> inputsCandidate=getInputs(candidates.get(0),null);
			ArrayList<String> outputsCandidate=getOutputs(candidates.get(0),null);

			ArrayList<String> inputParamTypeCand=getInputParameterTypes(inputsCandidate,null);
			ArrayList<String> outputParamTypeCand=getOutputParameterTypes(outputsCandidate,null);

			if(inputParamTypeCand.size()!=inputs.size())
				return "";
			if(outputParamTypeCand.size()!=outputs.size())
				return "";

			Iterator<String> iterInputCand=inputParamTypeCand.iterator();
			while(iterInputCand.hasNext()){
				String inCand=iterInputCand.next();
				boolean found=false;
				
				Iterator<String> iterInput=inputs.iterator();
				while(iterInput.hasNext()){
					String in=iterInput.next();
				
					if(inCand.equalsIgnoreCase(in)){
						found=true;
						break;
					}
				}
				if(!found)
					return "";
			}

			Iterator<String> iterOutputCand=outputParamTypeCand.iterator();
			while(iterOutputCand.hasNext()){
				String outCand=iterOutputCand.next();
				boolean found=false;
				
				Iterator<String> iterOutput=outputs.iterator();
				while(iterOutput.hasNext()){
					String out=iterOutput.next();
				
					if(outCand.equalsIgnoreCase(out)){
						found=true;
						break;
					}
				}
				if(!found)
					return "";
			}

			return candidates.get(0);
		}
	}


	/**
	 * Returns the profile URI of the given parameter name. 
	 * @param paramName parameter name of a service profile
	 * @param input indicate if the param is an input (true) or output (false)
	 * @return the profile URI of the given parameter name. 
	 */
	private String getProfileURI(String paramName, boolean input){
		String res="";

		ModelMaker maker = ModelFactory.createModelRDBMaker(conn);
		Model base = maker.createModel("http://example.org/ontologias");
		OntModel m = ModelFactory.createOntologyModel(getModelSpec(maker), base);

		String queryStringSearchName;
		if(input)
			queryStringSearchName =
			"prefix xsd: <http://www.w3.org/2001/XMLSchema#>"+
					"prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>"+
					"prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>"+
					"select ?x where { ?x profile:hasInput <"+paramName+"> }";
		else
			queryStringSearchName =
			"prefix xsd: <http://www.w3.org/2001/XMLSchema#>"+
					"prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>"+
					"prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>"+
					"select ?x where { ?x profile:hasOutput <"+paramName+"> }";

		Query querySearchName = QueryFactory.create(queryStringSearchName);

		QueryExecution qeSearchName = QueryExecutionFactory.create(querySearchName, m);
		ResultSet resultsSearchName = qeSearchName.execSelect();

		if (resultsSearchName != null) {

			while(resultsSearchName.hasNext()) {
				res = resultsSearchName.next().getResource("x").toString();
				
			} 

		}//end if
		else{
			System.out.println("resultsSearchName is null");
		}

		// close the query
		qeSearchName.close();
		

		return res; 
	}

	/**
	 * Returns the inputs of the given service profile
	 * @param serviceProfile URI to extract data from
	 * @param serviceURL it specifies the service URL if the query is not to the Jena DB. <code>null</code> to query the Jena DB model. 
	 * @return the inputs of the given service profile
	 */
	private ArrayList<String> getInputs(String serviceProfile, String serviceURL){
		ArrayList<String> inputs=new ArrayList<String>();

		ModelMaker maker;
		Model base;
		OntModel m;
		String queryStringSearchName;
		if(serviceURL==null){
			maker = ModelFactory.createModelRDBMaker(conn);
			base = maker.createModel("http://example.org/ontologias");
			m = ModelFactory.createOntologyModel(getModelSpec(maker), base);
			queryStringSearchName = "prefix xsd: <http://www.w3.org/2001/XMLSchema#>"+
					"prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>"+
					"prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>"+
					"select ?x where { <"+serviceProfile+"> profile:hasInput ?x }";
		}
		else{
			maker = ModelFactory.createMemModelMaker();
			base = maker.createModel("http://example.org/ontologias");
			m = ModelFactory.createOntologyModel(getModelSpec(maker), base);
			m.read(serviceURL);
			queryStringSearchName = "prefix xsd: <http://www.w3.org/2001/XMLSchema#>"+
					"prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>"+
					"prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>"+
					"select ?x where { ?y profile:hasInput ?x }";
		}
		
		Query querySearchName = QueryFactory.create(queryStringSearchName);

		QueryExecution qeSearchName = QueryExecutionFactory.create(querySearchName, m);
		ResultSet resultsSearchName = qeSearchName.execSelect();

		if (resultsSearchName != null) {

			while(resultsSearchName.hasNext()) {
				String res = resultsSearchName.next().getResource("x").toString();
				inputs.add(res);
			} 

		}//end if
		else{
			System.out.println("resultsSearchName is null");
		}

		qeSearchName.close();
		m.close();

		return inputs;
	}

	/**
	 * Returns the inputs parameter type of the given inputs.
	 * @param inputs names to obtain their parameter type
	 * @param serviceURL it specifies the service URL if the query is not to the Jena DB. <code>null</code> to query the Jena DB model.
	 * @return the inputs parameter type of the given inputs
	 */
	private ArrayList<String> getInputParameterTypes(ArrayList<String> inputs, String serviceURL){
		ArrayList<String> inputParamsRegistered=new ArrayList<String>();

		ModelMaker maker;
		Model base;
		OntModel m;
		if(serviceURL==null){
			maker = ModelFactory.createModelRDBMaker(conn);
			base = maker.createModel("http://example.org/ontologias");
			m = ModelFactory.createOntologyModel(getModelSpec(maker), base);
		}
		else{
			maker = ModelFactory.createMemModelMaker();
			base = maker.createModel("http://example.org/ontologias");
			m = ModelFactory.createOntologyModel(getModelSpec(maker), base);
			m.read(serviceURL);
		}

		Iterator<String> iterInputs=inputs.iterator();
		while(iterInputs.hasNext()){

			String in=iterInputs.next();
			String queryStr =
					"prefix xsd: <http://www.w3.org/2001/XMLSchema#>"+
							"prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>"+
							"prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>"+
							"select ?x where { <"+in+"> a process:Input ; process:parameterType ?x . }";

			Query query = QueryFactory.create(queryStr);

			// Execute the query and obtain results
			QueryExecution querySearchInputs = QueryExecutionFactory.create(query, m);
			ResultSet resultsSearchInputs = querySearchInputs.execSelect();

			if (resultsSearchInputs != null) {

				while (resultsSearchInputs.hasNext()) {
					QuerySolution sol=resultsSearchInputs.next();

					String param="\""+sol.getLiteral("x").getString()+"\"^^"+
							sol.getLiteral("x").getDatatypeURI().replace("http://www.w3.org/2001/XMLSchema#", "xsd:");
					//System.out.println(param);
					inputParamsRegistered.add(param);

				}//end for 
			}//end if


			// close the query
			querySearchInputs.close();
		}

		return inputParamsRegistered;
	}

	/**
	 * Returns the specification in OWL-S of the given input as a process part
	 * @param input URI to extract data from
	 * @return the specification in OWL-S of the given input as a process part
	 */
	private String getInputOWLS(String input){
		String inputOWLS="";
		String param="";

		ModelMaker maker = ModelFactory.createModelRDBMaker(conn);
		Model base = maker.createModel("http://example.org/ontologias");
		OntModel m = ModelFactory.createOntologyModel(getModelSpec(maker), base);

		String queryStringSearchName =
				"prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>"+
						"prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>"+
						"select ?x where { <"+input+"> a process:Input ; process:parameterType ?x . }";

		Query querySearchName = QueryFactory.create(queryStringSearchName);

		QueryExecution qeSearchName = QueryExecutionFactory.create(querySearchName, m);
		ResultSet resultsSearchName = qeSearchName.execSelect();


		if (resultsSearchName != null) {

			if(resultsSearchName.hasNext()) {
				param=resultsSearchName.next().getLiteral("x").toString();
			} 

		}//end if
		else{
			System.out.println("resultsSearchName is null");
		}
		StringTokenizer strToken=new StringTokenizer(param, "^^");
		String paramType=strToken.nextToken();
		String paramDataType= strToken.nextToken();

		inputOWLS="<process:Input rdf:ID=\""+input+"\">"+"\n"+
				"<process:parameterType rdf:datatype=\""+paramDataType+"\">"+paramType+"</process:parameterType>"+"\n"+
				"</process:Input>";

		return inputOWLS;

	}

	/**
	 * Returns the outputs of the given service profile
	 * @param serviceProfile URI to extract data from
	 * @param serviceURL it specifies the service URL if the query is not to the Jena DB. <code>null</code> to query the Jena DB model.
	 * @return the outputs of the given service profile
	 */
	private ArrayList<String> getOutputs(String serviceProfile, String serviceURL){
		ArrayList<String> outputs=new ArrayList<String>();

		ModelMaker maker;
		Model base;
		OntModel m;
		String queryStringSearchName;
		if(serviceURL==null){
			maker = ModelFactory.createModelRDBMaker(conn);
			base = maker.createModel("http://example.org/ontologias");
			m = ModelFactory.createOntologyModel(getModelSpec(maker), base);
			queryStringSearchName = "prefix xsd: <http://www.w3.org/2001/XMLSchema#>"+
					"prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>"+
					"prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>"+
					"select ?x where { <"+serviceProfile+"> profile:hasOutput ?x }";
		}
		else{
			maker = ModelFactory.createMemModelMaker();
			base = maker.createModel("http://example.org/ontologias");
			m = ModelFactory.createOntologyModel(getModelSpec(maker), base);
			m.read(serviceURL);
			queryStringSearchName = "prefix xsd: <http://www.w3.org/2001/XMLSchema#>"+
					"prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>"+
					"prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>"+
					"select ?x where { ?y profile:hasOutput ?x }";
		}

		Query querySearchName = QueryFactory.create(queryStringSearchName);

		QueryExecution qeSearchName = QueryExecutionFactory.create(querySearchName, m);
		ResultSet resultsSearchName = qeSearchName.execSelect();

		if (resultsSearchName != null) {

			while(resultsSearchName.hasNext()) {
				String res = resultsSearchName.next().getResource("x").toString();
				outputs.add(res);
			} 

		}//end if
		else{
			System.out.println("resultsSearchName is null");
		}

		qeSearchName.close();
		m.close();

		return outputs;
	}

	/**
	 * Returns the outputs parameter type of the given outputs.
	 * @param outputs names to obtain their parameter type
	 * @param serviceURL it specifies the service URL if the query is not to the Jena DB. <code>null</code> to query the Jena DB model.
	 * @return the outputs parameter type of the given outputs
	 */
	private ArrayList<String> getOutputParameterTypes(ArrayList<String> outputs, String serviceURL){
		ArrayList<String> outputParamsRegistered=new ArrayList<String>();

		ModelMaker maker;
		Model base;
		OntModel m;
		if(serviceURL==null){
			maker = ModelFactory.createModelRDBMaker(conn);
			base = maker.createModel("http://example.org/ontologias");
			m = ModelFactory.createOntologyModel(getModelSpec(maker), base);
		}
		else{
			maker = ModelFactory.createMemModelMaker();
			base = maker.createModel("http://example.org/ontologias");
			m = ModelFactory.createOntologyModel(getModelSpec(maker), base);
			m.read(serviceURL);
		}

		Iterator<String> iterOutputs=outputs.iterator();
		while(iterOutputs.hasNext()){

			String out=iterOutputs.next();
			String queryStr =
					"prefix xsd: <http://www.w3.org/2001/XMLSchema#>"+
							"prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>"+
							"prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>"+
							"select ?x where { <"+out+"> a process:Output ; process:parameterType ?x . }";

			Query query = QueryFactory.create(queryStr);

			// Execute the query and obtain results
			QueryExecution querySearchInputs = QueryExecutionFactory.create(query, m);
			ResultSet resultsSearchInputs = querySearchInputs.execSelect();

			if (resultsSearchInputs != null) {

				while (resultsSearchInputs.hasNext()) {
					QuerySolution sol=resultsSearchInputs.next();

					String param="\""+sol.getLiteral("x").getString()+"\"^^"+
							sol.getLiteral("x").getDatatypeURI().replace("http://www.w3.org/2001/XMLSchema#", "xsd:");
					outputParamsRegistered.add(param);

				}//end for 
			}//end if


			// close the query
			querySearchInputs.close();
		}

		return outputParamsRegistered;
	}

	/**
	 * Returns the specification in OWL-S of the given output
	 * @param output URI to extract data from
	 * @return the specification in OWL-S of the given output
	 */
	private String getOutputOWLS(String output){
		String outputOWLS="";
		String param="";

		ModelMaker maker = ModelFactory.createModelRDBMaker(conn);
		Model base = maker.createModel("http://example.org/ontologias");
		OntModel m = ModelFactory.createOntologyModel(getModelSpec(maker), base);

		String queryStringSearchName =
				"prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>"+
						"prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>"+
						"select ?x where { <"+output+"> a process:Output ; process:parameterType ?x . }";

		Query querySearchName = QueryFactory.create(queryStringSearchName);

		QueryExecution qeSearchName = QueryExecutionFactory.create(querySearchName, m);
		ResultSet resultsSearchName = qeSearchName.execSelect();


		if (resultsSearchName != null) {

			if(resultsSearchName.hasNext()) {
				param=resultsSearchName.next().getLiteral("x").toString();
			} 

		}//end if
		else{
			System.out.println("resultsSearchName is null");
		}
		StringTokenizer strToken=new StringTokenizer(param, "^^");
		String paramType=strToken.nextToken();
		String paramDataType= strToken.nextToken();

		outputOWLS="<process:Output rdf:ID=\""+output+"\">"+"\n"+
				"<process:parameterType rdf:datatype=\""+paramDataType+"\">"+paramType+"</process:parameterType>"+"\n"+
				"</process:Output>";

		return outputOWLS;

	}


	/**
	 * Writes the OWL-S specification of the providers and groundings of the given service URL in a file given as parameter. 
	 * This specification is attached (by the profile specification) to the given registered profile of the Jena DB.
	 * @param serviceURL URL of the service to extract the providers and groundings
	 * @param registeredProfile in the Jena DB to attach the new providers
	 * @param fileName to store the OWL-S specification created with the providers and groundings
	 */
	private void writeProvidersGroundingOWLSFile(String serviceURL, String registeredProfile, String fileName){
		
		StringTokenizer tokenProfile=new StringTokenizer(registeredProfile, "#");
		String urlBase=tokenProfile.nextToken();
		String regProfileName=tokenProfile.nextToken();
		
		try {

			FileWriter fstream = new FileWriter(fileName);
			BufferedWriter out = new BufferedWriter(fstream);

			out.write("<?xml version=\"1.0\" encoding=\"WINDOWS-1252\"?>"+"\n"+
					"<rdf:RDF  xmlns:owl       = \"http://www.w3.org/2002/07/owl#\""+"\n"+
					"xmlns:rdfs      = \"http://www.w3.org/2000/01/rdf-schema#\""+"\n"+
					"xmlns:rdf       = \"http://www.w3.org/1999/02/22-rdf-syntax-ns#\""+"\n"+
					"xmlns:xsd       = \"http://www.w3.org/2001/XMLSchema#\""+"\n"+
					"xmlns:service   = \"http://www.daml.org/services/owl-s/1.1/Service.owl#\""+"\n"+
					"xmlns:process   = \"http://www.daml.org/services/owl-s/1.1/Process.owl#\""+"\n"+
					"xmlns:profile    = \"http://www.daml.org/services/owl-s/1.1/Profile.owl#\""+"\n"+
					"xmlns:grounding = \"http://www.daml.org/services/owl-s/1.1/Grounding.owl#\""+"\n"+
					"xmlns:provider = \"http://127.0.0.1/ontology/provider.owl#\""+"\n"+
					"xml:base        = \""+urlBase+"\">"+"\n");
			
			out.write("<profile:Profile rdf:ID=\""+regProfileName+"\">\n");
			
			StringTokenizer tokenRegServ=new StringTokenizer(registeredProfile, "#");
			String baseURI=tokenRegServ.nextToken();
			
			String registeredServiceURI=getServiceURI(registeredProfile, null);
			String profile=getProfileURIfromURL(serviceURL);
			
			// it is not necessary to check if the providers are already registered, Jena does not write them two times
			String providersOWLS=getProvidersOWLS(profile, serviceURL);
			
			System.out.println("profile="+profile+"\nProvidersOWLS:\n"+providersOWLS);
			
			
			
			out.write(providersOWLS);
			out.write("</profile:Profile>\n");
			
			String groundsOWLS=getGroundingsOWLSfromFile(profile, serviceURL, registeredServiceURI, baseURI);
			
			System.out.println("groundsOWLS:\n"+groundsOWLS);
			
			out.write(groundsOWLS);
			out.write("</rdf:RDF>\n");
			
			out.close();

		} catch (IOException e) {
			
			e.printStackTrace();
		}

	}
	
	
	/**
	 * The RegisterService tries to register the service that is specified as parameter. If it is already registered, it registers
	 * the new providers and groundings.
	 * @param serviceURL is the original URL of the OWL-S specification of the service
	 * @return a {@link String} describing if the service has been entirely registered or the number of groundings added to an 
	 * already registered service profile
	 */
	public String RegisterService(String serviceURL){


		ArrayList<String> inputs=getInputs(null, serviceURL);
		ArrayList<String> outputs=getOutputs(null, serviceURL);

		ArrayList<String> inputsParams=getInputParameterTypes(inputs,serviceURL);
		ArrayList<String> outputParams=getOutputParameterTypes(outputs,serviceURL);

		String regServiceProfile=searchRegisteredServices(inputsParams, outputParams);
		if(!regServiceProfile.equalsIgnoreCase("")){
			System.out.println("Service already registered: "+regServiceProfile);
			int nGrounds=0;

			//obtain the registered wsdl docs and the one that has to be registered. 
			//If the new wsdl doc is different, it is registered

			ArrayList<String> wsdlsToRegister=getWSDLDocumentFromServiceURL(serviceURL);

			String serviceURI=getServiceURI(regServiceProfile,null);
			ArrayList<String> groundings=getGroundings(serviceURI,null);
			Iterator<String> iterGrounds=groundings.iterator();
			String groundingURI="";
			boolean found=false;
			while(iterGrounds.hasNext()){
				groundingURI=iterGrounds.next();
				String atomicProcessGrounding=getAtomicProcessGrounding(groundingURI,null);
				String WSDLDocandDatatype=getWSDLDocument(atomicProcessGrounding,null);
				Iterator<String> iterWsdlsToRegister=wsdlsToRegister.iterator();
				while(iterWsdlsToRegister.hasNext()){
					String wsdlToRegister=iterWsdlsToRegister.next();
					if(WSDLDocandDatatype.equalsIgnoreCase(wsdlToRegister)){
						found=true;
						break;
					}
				}
				if(found)
					break;
			}
			if(!found){//register the grounding
				System.out.println("Register new grounding");
				nGrounds++;

				String fileName="tmp.owls";
				writeProvidersGroundingOWLSFile(serviceURL, regServiceProfile, fileName);
				
				ModelMaker maker = ModelFactory.createModelRDBMaker(conn);
				Model base = maker.createModel("http://example.org/ontologias");
				OntModelSpec spec = new OntModelSpec(OntModelSpec.OWL_MEM);
				spec.setImportModelMaker(maker);
				OntModel m = ModelFactory.createOntologyModel(spec, base);

				try {
					
					File file=new File(fileName);
					InputStream in = new FileInputStream(file);

					m.read(in, "");
					m.commit();

				} catch (FileNotFoundException e) {

					e.printStackTrace();
				}

			}
			else{
				System.out.println("Not register this grounding, already registered");
			}

			
			return nGrounds + " groundings registered to service profile: "+regServiceProfile;

		}
		else{
			ModelMaker maker = ModelFactory.createModelRDBMaker(conn);
			Model base = maker.createModel("http://example.org/ontologias");
			// now we plug that base model into an ontology model that also uses 
			// the given model maker to create storage for imported models
			OntModelSpec spec = new OntModelSpec(OntModelSpec.OWL_MEM);
			spec.setImportModelMaker(maker);
			OntModel m = ModelFactory.createOntologyModel(spec, base);

			//load the service profile in the database
			m.read(serviceURL);
			m.commit();

			//Close the model 
			m.close();


			System.out.println("Service registered: "+serviceURL);
			return "Service registered: "+serviceURL;
		}



	}
	
	
	/**
	 * Returns the profile URI of the service specification in the given service URL
	 * @param serviceURL to extract data from
	 * @return the profile URI of the service specification in the given service URL
	 */
	private String getProfileURIfromURL(String serviceURL){
		
		ModelMaker maker = ModelFactory.createMemModelMaker();
		Model base = maker.createModel("http://example.org/ontologias");
		OntModel m = ModelFactory.createOntologyModel(getModelSpec(maker), base);
		
		m.read(serviceURL);
		
		
		String queryStringSearchName =
				"prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>"+
				"prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>"+
				"select ?x where { ?x a profile:Profile }";

		Query querySearchName = QueryFactory.create(queryStringSearchName);

		QueryExecution qeSearchName = QueryExecutionFactory.create(querySearchName, m);
		ResultSet resultsSearchName = qeSearchName.execSelect();
		
		String profile="";

		if (resultsSearchName != null) {

			if(resultsSearchName.hasNext()) {
				profile = resultsSearchName.next().getResource("x").toString();

			} 

		}//end if
		else{
			System.out.println("resultsSearchName is null");
		}

		return profile;
		
	}

	/**
	 * Returns the profile service name of the given service profile
	 * @param serviceProfile  URI to extract data from
	 * @return the profile service name of the given service profile
	 */
	private String getProfileServiceName(String serviceProfile){
		String serviceName="";

		ModelMaker maker = ModelFactory.createModelRDBMaker(conn);
		Model base = maker.createModel("http://example.org/ontologias");
		OntModel m = ModelFactory.createOntologyModel(getModelSpec(maker), base);

		String queryStringSearchName =
				"prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>"+
						"prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>"+
						"select ?x where { "+"<"+serviceProfile+">"+" profile:serviceName ?x }";

		Query querySearchName = QueryFactory.create(queryStringSearchName);

		QueryExecution qeSearchName = QueryExecutionFactory.create(querySearchName, m);
		ResultSet resultsSearchName = qeSearchName.execSelect();


		if (resultsSearchName != null) {

			while(resultsSearchName.hasNext()) {
				serviceName = resultsSearchName.next().getLiteral("x").toString();

			} 

		}//end if
		else{
			System.out.println("resultsSearchName is null");
		}

		return serviceName;

	}

	/**
	 * Returns the profile text description of the given service profile
	 * @param serviceProfile URI to extract data from
	 * @return the profile text description of the given service profile
	 */
	private String getProfileTextDescription(String serviceProfile){
		String textDescription="";

		ModelMaker maker = ModelFactory.createModelRDBMaker(conn);
		Model base = maker.createModel("http://example.org/ontologias");
		OntModel m = ModelFactory.createOntologyModel(getModelSpec(maker), base);

		String queryStringSearchName =
				"prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>"+
						"prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>"+
						"select ?x where { "+"<"+serviceProfile+">"+" profile:textDescription ?x }";

		Query querySearchName = QueryFactory.create(queryStringSearchName);

		QueryExecution qeSearchName = QueryExecutionFactory.create(querySearchName, m);
		ResultSet resultsSearchName = qeSearchName.execSelect();


		if (resultsSearchName != null) {

			while(resultsSearchName.hasNext()) {
				textDescription = resultsSearchName.next().getLiteral("x").toString();

			} 

		}//end if
		else{
			System.out.println("resultsSearchName is null");
		}

		return textDescription;

	}

	/**
	 * Returns the service URI
	 * @param serviceProfile  URI to extract data from
	 * @param serviceURL it specifies the service URL if the query is not to the Jena DB. <code>null</code> to query the Jena DB model.
	 * @return the service URI
	 */
	private String getServiceURI(String serviceProfile, String serviceURL){
		String serviceURI="";
		
		ModelMaker maker;
		Model base;
		OntModel m;
		if(serviceURL==null){
			maker = ModelFactory.createModelRDBMaker(conn);
			base = maker.createModel("http://example.org/ontologias");
			m = ModelFactory.createOntologyModel(getModelSpec(maker), base);
		}
		else{
			maker = ModelFactory.createMemModelMaker();
			base = maker.createModel("http://example.org/ontologias");
			m = ModelFactory.createOntologyModel(getModelSpec(maker), base);
			m.read(serviceURL);
		}
		
		

		String queryStringSearchName =
				"prefix service: <http://www.daml.org/services/owl-s/1.1/Service.owl#>"+
						"select ?x where { ?x service:presents <"+serviceProfile+"> }";

		Query querySearchName = QueryFactory.create(queryStringSearchName);

		QueryExecution qeSearchName = QueryExecutionFactory.create(querySearchName, m);
		ResultSet resultsSearchName = qeSearchName.execSelect();


		if (resultsSearchName != null) {

			if(resultsSearchName.hasNext()) {
				serviceURI=resultsSearchName.next().getResource("x").toString();
			} 

		}//end if
		else{
			System.out.println("resultsSearchName is null");
		}

		qeSearchName.close();
		m.close();

		return serviceURI;
	}
	
	
	/**
	 * Returns the service process URI of the given service profile
	 * @param serviceProfile URI to extract data from
	 * @param serviceURL it specifies the service URL if the query is not to the Jena DB. <code>null</code> to query the Jena DB model
	 * @return the service process URI of the given service profile
	 */
	private String getServiceProcess(String serviceProfile, String serviceURL){
		String serviceProcess="";
		
		ModelMaker maker;
		Model base;
		OntModel m;
		if(serviceURL==null){
			maker = ModelFactory.createModelRDBMaker(conn);
			base = maker.createModel("http://example.org/ontologias");
			m = ModelFactory.createOntologyModel(getModelSpec(maker), base);
		}
		else{
			maker = ModelFactory.createMemModelMaker();
			base = maker.createModel("http://example.org/ontologias");
			m = ModelFactory.createOntologyModel(getModelSpec(maker), base);
			m.read(serviceURL);
		}
		
		

		String queryStringSearchName =
				"prefix service: <http://www.daml.org/services/owl-s/1.1/Service.owl#>"+
				"prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>"+
						"select ?x where { ?x  a process:AtomicProcess }";
		
		Query querySearchName = QueryFactory.create(queryStringSearchName);

		QueryExecution qeSearchName = QueryExecutionFactory.create(querySearchName, m);
		ResultSet resultsSearchName = qeSearchName.execSelect();


		if (resultsSearchName != null) {

			if(resultsSearchName.hasNext()) {
				serviceProcess=resultsSearchName.next().getResource("x").toString();
			} 

		}//end if
		else{
			System.out.println("resultsSearchName is null");
		}

		qeSearchName.close();
		m.close();

		return serviceProcess;
	}

	/**
	 * Returns an {@link ArrayList} with the grounding URIs of the given service URI 
	 * @param serviceURI to extract data from
	 * @param serviceURL it specifies the service URL if the query is not to the Jena DB. <code>null</code> to query the Jena DB model
	 * @return an {@link ArrayList} with the grounding URIs of the given service URI
	 */
	private ArrayList<String> getGroundings(String serviceURI, String serviceURL){
		ArrayList<String> groundings=new ArrayList<String>();

		ModelMaker maker;
		Model base;
		OntModel m;
		if(serviceURL==null){
			maker = ModelFactory.createModelRDBMaker(conn);
			base = maker.createModel("http://example.org/ontologias");
			m = ModelFactory.createOntologyModel(getModelSpec(maker), base);
		}
		else{
			maker = ModelFactory.createMemModelMaker();
			base = maker.createModel("http://example.org/ontologias");
			m = ModelFactory.createOntologyModel(getModelSpec(maker), base);
			m.read(serviceURL);
		}

		String queryStringSearchName =
				"prefix service: <http://www.daml.org/services/owl-s/1.1/Service.owl#>"+
						"select ?x where { ?x service:supportedBy <"+serviceURI+"> }";

		Query querySearchName = QueryFactory.create(queryStringSearchName);

		QueryExecution qeSearchName = QueryExecutionFactory.create(querySearchName, m);
		ResultSet resultsSearchName = qeSearchName.execSelect();


		if (resultsSearchName != null) {

			while(resultsSearchName.hasNext()) {
				String ground=resultsSearchName.next().getResource("x").toString();
				groundings.add(ground);
			} 

		}//end if
		else{
			System.out.println("resultsSearchName is null");
		}

		qeSearchName.close();
		m.close();

		return groundings;
	}

	/**
	 * Returns the atomic process grounding URI of the given grounding URI
	 * @param groundingURI to extract data from
	 * @param serviceURL it specifies the service URL if the query is not to the Jena DB. <code>null</code> to query the Jena DB model
	 * @return the atomic process grounding URI of the given grounding URI
	 */
	private String getAtomicProcessGrounding(String groundingURI, String serviceURL){
		String atomicProcessGrounding="";

		ModelMaker maker;
		Model base;
		OntModel m;
		if(serviceURL==null){
			maker = ModelFactory.createModelRDBMaker(conn);
			base = maker.createModel("http://example.org/ontologias");
			m = ModelFactory.createOntologyModel(getModelSpec(maker), base);
		}
		else{
			maker = ModelFactory.createMemModelMaker();
			base = maker.createModel("http://example.org/ontologias");
			m = ModelFactory.createOntologyModel(getModelSpec(maker), base);
			m.read(serviceURL);
		}

		String queryStringSearchName =
				"prefix grounding: <http://www.daml.org/services/owl-s/1.1/Grounding.owl#>"+
						"select ?x where { <"+groundingURI+"> grounding:hasAtomicProcessGrounding ?x }";

		Query querySearchName = QueryFactory.create(queryStringSearchName);

		QueryExecution qeSearchName = QueryExecutionFactory.create(querySearchName, m);
		ResultSet resultsSearchName = qeSearchName.execSelect();


		if (resultsSearchName != null) {

			if(resultsSearchName.hasNext()) {
				atomicProcessGrounding=resultsSearchName.next().getResource("x").toString();
			} 

		}//end if
		else{
			System.out.println("resultsSearchName is null");
		}

		qeSearchName.close();
		m.close();

		return atomicProcessGrounding;
	}

	/**
	 * Returns the WSDL document URI of the given atomic process grounding URI
	 * @param atomicProcessGrounding URI to extract data from
	 * @param serviceURL it specifies the service URL if the query is not to the Jena DB. <code>null</code> to query the Jena DB model
	 * @return the WSDL document URI of the given atomic process grounding URI
	 */
	private String getWSDLDocument(String atomicProcessGrounding, String serviceURL){
		String WSDLDoc="";

		ModelMaker maker;
		Model base;
		OntModel m;
		if(serviceURL==null){
			maker = ModelFactory.createModelRDBMaker(conn);
			base = maker.createModel("http://example.org/ontologias");
			m = ModelFactory.createOntologyModel(getModelSpec(maker), base);
		}
		else{
			maker = ModelFactory.createMemModelMaker();
			base = maker.createModel("http://example.org/ontologias");
			m = ModelFactory.createOntologyModel(getModelSpec(maker), base);
			m.read(serviceURL);
		}
		
		String queryStringSearchName =
				"prefix grounding: <http://www.daml.org/services/owl-s/1.1/Grounding.owl#>"+
						"select ?x where { <"+atomicProcessGrounding+"> grounding:wsdlDocument ?x }";

		Query querySearchName = QueryFactory.create(queryStringSearchName);

		QueryExecution qeSearchName = QueryExecutionFactory.create(querySearchName, m);
		ResultSet resultsSearchName = qeSearchName.execSelect();


		if (resultsSearchName != null) {

			if(resultsSearchName.hasNext()) {
				WSDLDoc=resultsSearchName.next().getLiteral("x").toString();
			} 

		}//end if
		else{
			System.out.println("resultsSearchName is null");
		}

		qeSearchName.close();
		m.close();

		return WSDLDoc;
	}


	/**
	 * Returns the WSDL document URI of the given service URL
	 * @param serviceURL to extract data from
	 * @return the WSDL document URI of the given service URL
	 */
	private ArrayList<String> getWSDLDocumentFromServiceURL(String serviceURL){
		ArrayList<String> WSDLDocs=new ArrayList<String>();

		ModelMaker maker = ModelFactory.createMemModelMaker();
		Model base = maker.createModel("http://example.org/ontologias");
		// now we plug that base model into an ontology model that also uses 
		// the given model maker to create storage for imported models
		OntModelSpec spec = new OntModelSpec(OntModelSpec.OWL_MEM);
		spec.setImportModelMaker(maker);
		OntModel m = ModelFactory.createOntologyModel(spec, base);
		//load the service profile in the database
		m.read(serviceURL);

		String queryStringSearchName =
				"prefix grounding: <http://www.daml.org/services/owl-s/1.1/Grounding.owl#>"+
						"select ?x where { ?y grounding:wsdlDocument ?x }";

		Query querySearchName = QueryFactory.create(queryStringSearchName);

		QueryExecution qeSearchName = QueryExecutionFactory.create(querySearchName, m);
		ResultSet resultsSearchName = qeSearchName.execSelect();


		if (resultsSearchName != null) {

			if(resultsSearchName.hasNext()) {
				String wsdldoc=resultsSearchName.next().getLiteral("x").toString();
				WSDLDocs.add(wsdldoc);
				//System.out.println("wsdldoc="+WSDLDoc);
			} 

		}//end if
		else{
			System.out.println("resultsSearchName is null");
		}

		qeSearchName.close();
		m.close();

		return WSDLDocs;
	}

	/**
	 * Returns a String containing the specification in OWL-S of all groundings of the given service profile
	 * @param serviceProfile URI to extract data from
	 * @return a String containing the specification in OWL-S of all groundings of the given service profile
	 */
	private String getGroundingsOWLS(String serviceProfile){
		String groundsOWLS="";
		String serviceURI=getServiceURI(serviceProfile,null);
		ArrayList<String> groundings=getGroundings(serviceURI,null);
		Iterator<String> iterGrounds=groundings.iterator();
		while(iterGrounds.hasNext()){
			String groundingURI=iterGrounds.next();
			String atomicProcessGrounding=getAtomicProcessGrounding(groundingURI,null);
			String WSDLDocandDatatype=getWSDLDocument(atomicProcessGrounding,null);
			StringTokenizer token=new StringTokenizer(WSDLDocandDatatype,"^^");
			String wsdlDoc=token.nextToken();
			String wsdlDatatype=token.nextToken();
			StringTokenizer tokenGroundURI=new StringTokenizer(groundingURI,"#");
			tokenGroundURI.nextToken();
			String groundingURIName=tokenGroundURI.nextToken();
			
			StringTokenizer tokenAtomicProcess=new StringTokenizer(atomicProcessGrounding, "#");
			tokenAtomicProcess.nextToken();
			String atomicProcessGroundingName=tokenAtomicProcess.nextToken();//baseURI+"#"+tokenAtomicProcess.nextToken();
			
			groundsOWLS+="<grounding:WsdlGrounding rdf:ID=\""+groundingURIName+"\">\n"+ //"<grounding:WsdlGrounding rdf:ID=\""+baseURI+"#"+groundingURIName+"\">\n"+
					"<service:supportedBy rdf:resource=\""+"#"+serviceURI+"\"/> \n"+
					"<grounding:hasAtomicProcessGrounding>\n"+
					"<grounding:WsdlAtomicProcessGrounding rdf:ID=\""+atomicProcessGroundingName+"\"/>"+
					"</grounding:hasAtomicProcessGrounding>\n"+
					"</grounding:WsdlGrounding>\n";

			groundsOWLS+="<grounding:WsdlAtomicProcessGrounding rdf:about=\""+"#"+atomicProcessGroundingName+"\">\n"+
					"<grounding:wsdlDocument rdf:datatype=\""+wsdlDatatype+"\"\n>"+//the symbol ">" must go after the "\n", 
					//if not, when Jena adds the info to the DB puts some "" and spaces that causes problems to queries
					wsdlDoc+"</grounding:wsdlDocument>\n";
			String owlsProcess=getGroundOwlsProcess(atomicProcessGrounding, null);
			groundsOWLS+="<grounding:owlsProcess rdf:resource=\""+owlsProcess+"\"/>\n";
			
			String groundInputMessage=getGroundingInputMessage(atomicProcessGrounding, null);
			StringTokenizer inputMessageTok=new StringTokenizer(
					groundInputMessage, "^^");
			String inputMessage=inputMessageTok.nextToken();
			String inputMessageDatatype=inputMessageTok.nextToken();
			groundsOWLS+="<grounding:wsdlInputMessage rdf:datatype=\""+inputMessageDatatype+"\">"+inputMessage+
				    "</grounding:wsdlInputMessage>\n";
			StringTokenizer outputMessageTok=new StringTokenizer(
					getGroundingOutputMessage(atomicProcessGrounding, null), "^^");
			String outputMessage=outputMessageTok.nextToken();
			String outputMessageDatatype=outputMessageTok.nextToken();
			groundsOWLS+="<grounding:wsdlOutputMessage rdf:datatype=\""+outputMessageDatatype+"\">"+outputMessage+
				    "</grounding:wsdlOutputMessage>\n";
			
			
			ArrayList<GroundingInOutput> groundInputs=
					getGroundingInOutputs(atomicProcessGrounding, null, true);
			Iterator<GroundingInOutput> iterIns=groundInputs.iterator();
			while(iterIns.hasNext()){
				GroundingInOutput in=iterIns.next();
				
				groundsOWLS+="<grounding:wsdlInput>\n<grounding:WsdlInputMessageMap>\n";
				groundsOWLS+="<grounding:owlsParameter rdf:resource=\""+in.getOwlsParameter()+"\"/>\n";
				
				StringTokenizer messagePartTok=new StringTokenizer(in.getWsdlMessagePart(), "^^");
				String messagePart=messagePartTok.nextToken();
				String messagePartDatatype=messagePartTok.nextToken();
				groundsOWLS+="<grounding:wsdlMessagePart rdf:datatype=\""+messagePartDatatype+"\">"+messagePart+"</grounding:wsdlMessagePart>\n";
				
				groundsOWLS+="<grounding:xsltTransformationString>"+in.getXsltTransformationString()+"</grounding:xsltTransformationString>\n";
		        groundsOWLS+="</grounding:WsdlInputMessageMap>\n</grounding:wsdlInput>\n";
			}
	        
			ArrayList<GroundingInOutput> groundOutputs=
					getGroundingInOutputs(atomicProcessGrounding, null, false);
			Iterator<GroundingInOutput> iterOuts=groundOutputs.iterator();
			while(iterOuts.hasNext()){
				GroundingInOutput out=iterOuts.next();
				
		        groundsOWLS+="<grounding:wsdlOutput>\n<grounding:WsdlOutputMessageMap>\n";
				groundsOWLS+="<grounding:owlsParameter rdf:resource=\""+out.getOwlsParameter()+"\"/>\n";
				
				StringTokenizer messagePartTok=new StringTokenizer(out.getWsdlMessagePart(), "^^");
				String messagePart=messagePartTok.nextToken();
				String messagePartDatatype=messagePartTok.nextToken();
				groundsOWLS+="<grounding:wsdlMessagePart rdf:datatype=\""+messagePartDatatype+"\">"+messagePart+"</grounding:wsdlMessagePart>\n";
				
				groundsOWLS+="<grounding:xsltTransformationString>"+out.getXsltTransformationString()+"</grounding:xsltTransformationString>\n";
		        groundsOWLS+="</grounding:WsdlOutputMessageMap>\n</grounding:wsdlOutput>\n";
			}
			
			String groundOperation=getGroundOperation(atomicProcessGrounding, null);
			StringTokenizer operationTok=new StringTokenizer(groundOperation, "^^");
			String operation=operationTok.nextToken();
			String operationDatatype=operationTok.nextToken();
			groundsOWLS+="<grounding:wsdlOperation>\n<grounding:WsdlOperationRef>\n";
			groundsOWLS+="<grounding:operation rdf:datatype=\""+operationDatatype+"\">"+operation+"</grounding:operation>\n";
			
			String groundPortType=getGroundPortType(atomicProcessGrounding, null);
			StringTokenizer portTypeTok=new StringTokenizer(groundPortType, "^^");
			String portType=portTypeTok.nextToken();
			String portTypeDatatype=portTypeTok.nextToken();
			groundsOWLS+="<grounding:portType rdf:datatype=\""+portTypeDatatype+"\">"+portType+"</grounding:portType>\n";
			groundsOWLS+="</grounding:WsdlOperationRef>\n</grounding:wsdlOperation>\n";
			
			
			groundsOWLS+="</grounding:WsdlAtomicProcessGrounding>\n";
			
			
		}

		return groundsOWLS;
	}
	
	/**
	 * Returns a {@link String} containing the specification in OWL-S of all groundings of the given service URL
	 * @param serviceProfile URI to extract data from
	 * @param serviceURL where the orginal OWL-S specification of the service is
	 * @param registeredServiceURI the service URI of the corresponding registered service in the Jena DB
	 * @param baseURI of the registered service in the Jena DB
	 * @return a {@link String} containing the specification in OWL-S of all groundings of the given service URL
	 */
	private String getGroundingsOWLSfromFile(String serviceProfile, String serviceURL, String registeredServiceURI, String baseURI){
		String groundsOWLS="";
		String serviceURI=getServiceURI(serviceProfile,serviceURL);
		ArrayList<String> groundings=getGroundings(serviceURI,serviceURL);
		Iterator<String> iterGrounds=groundings.iterator();
		while(iterGrounds.hasNext()){
			String groundingURI=iterGrounds.next();
			String atomicProcessGrounding=getAtomicProcessGrounding(groundingURI, serviceURL);
			String WSDLDocandDatatype=getWSDLDocument(atomicProcessGrounding,serviceURL);
			StringTokenizer token=new StringTokenizer(WSDLDocandDatatype,"^^");
			String wsdlDoc=token.nextToken();
			String wsdlDatatype=token.nextToken();
			StringTokenizer tokenGroundURI=new StringTokenizer(groundingURI,"#");
			tokenGroundURI.nextToken();
			String groundingURIName=tokenGroundURI.nextToken();
			
			StringTokenizer tokenRegServURI=new StringTokenizer(registeredServiceURI, "#");
			tokenRegServURI.nextToken();
			String registeredServiceURIName=tokenRegServURI.nextToken();
			
			StringTokenizer tokenAtomicProcess=new StringTokenizer(atomicProcessGrounding, "#");
			tokenAtomicProcess.nextToken();
			String atomicProcessGroundingName=tokenAtomicProcess.nextToken();//baseURI+"#"+tokenAtomicProcess.nextToken();
			
			groundsOWLS+="<grounding:WsdlGrounding rdf:ID=\""+groundingURIName+"\">\n"+ //"<grounding:WsdlGrounding rdf:ID=\""+baseURI+"#"+groundingURIName+"\">\n"+
					"<service:supportedBy rdf:resource=\""+"#"+registeredServiceURIName+"\"/> \n"+
					"<grounding:hasAtomicProcessGrounding>\n"+
					"<grounding:WsdlAtomicProcessGrounding rdf:ID=\""+atomicProcessGroundingName+"\"/>"+
					"</grounding:hasAtomicProcessGrounding>\n"+
					"</grounding:WsdlGrounding>\n";

			groundsOWLS+="<grounding:WsdlAtomicProcessGrounding rdf:about=\""+"#"+atomicProcessGroundingName+"\">\n"+
					"<grounding:wsdlDocument rdf:datatype=\""+wsdlDatatype+"\"\n>"+//the symbol ">" must go after the "\n", 
					//if not, when Jena adds the info to the DB puts some "" and spaces that causes problems to queries
					wsdlDoc+"</grounding:wsdlDocument>\n";
			String owlsProcess=getGroundOwlsProcess(atomicProcessGrounding, serviceURL);
			groundsOWLS+="<grounding:owlsProcess rdf:resource=\""+owlsProcess+"\"/>\n";
			
			String groundInputMessage=getGroundingInputMessage(atomicProcessGrounding, serviceURL);
			StringTokenizer inputMessageTok=new StringTokenizer(
					groundInputMessage, "^^");
			String inputMessage=inputMessageTok.nextToken();
			String inputMessageDatatype=inputMessageTok.nextToken();
			groundsOWLS+="<grounding:wsdlInputMessage rdf:datatype=\""+inputMessageDatatype+"\">"+inputMessage+
				    "</grounding:wsdlInputMessage>\n";
			StringTokenizer outputMessageTok=new StringTokenizer(
					getGroundingOutputMessage(atomicProcessGrounding, serviceURL), "^^");
			String outputMessage=outputMessageTok.nextToken();
			String outputMessageDatatype=outputMessageTok.nextToken();
			groundsOWLS+="<grounding:wsdlOutputMessage rdf:datatype=\""+outputMessageDatatype+"\">"+outputMessage+
				    "</grounding:wsdlOutputMessage>\n";
			
			
			ArrayList<GroundingInOutput> groundInputs=
					getGroundingInOutputs(atomicProcessGrounding, serviceURL, true);
			Iterator<GroundingInOutput> iterIns=groundInputs.iterator();
			while(iterIns.hasNext()){
				GroundingInOutput in=iterIns.next();
				
				groundsOWLS+="<grounding:wsdlInput>\n<grounding:WsdlInputMessageMap>\n";
				groundsOWLS+="<grounding:owlsParameter rdf:resource=\""+in.getOwlsParameter()+"\"/>\n";
				
				StringTokenizer messagePartTok=new StringTokenizer(in.getWsdlMessagePart(), "^^");
				String messagePart=messagePartTok.nextToken();
				String messagePartDatatype=messagePartTok.nextToken();
				groundsOWLS+="<grounding:wsdlMessagePart rdf:datatype=\""+messagePartDatatype+"\">"+messagePart+"</grounding:wsdlMessagePart>\n";
				
				groundsOWLS+="<grounding:xsltTransformationString>"+in.getXsltTransformationString()+"</grounding:xsltTransformationString>\n";
		        groundsOWLS+="</grounding:WsdlInputMessageMap>\n</grounding:wsdlInput>\n";
			}
	        
			ArrayList<GroundingInOutput> groundOutputs=
					getGroundingInOutputs(atomicProcessGrounding, serviceURL, false);
			Iterator<GroundingInOutput> iterOuts=groundOutputs.iterator();
			while(iterOuts.hasNext()){
				GroundingInOutput out=iterOuts.next();
				
		        groundsOWLS+="<grounding:wsdlOutput>\n<grounding:WsdlOutputMessageMap>\n";
				groundsOWLS+="<grounding:owlsParameter rdf:resource=\""+out.getOwlsParameter()+"\"/>\n";
				
				StringTokenizer messagePartTok=new StringTokenizer(out.getWsdlMessagePart(), "^^");
				String messagePart=messagePartTok.nextToken();
				String messagePartDatatype=messagePartTok.nextToken();
				groundsOWLS+="<grounding:wsdlMessagePart rdf:datatype=\""+messagePartDatatype+"\">"+messagePart+"</grounding:wsdlMessagePart>\n";
				
				groundsOWLS+="<grounding:xsltTransformationString>"+out.getXsltTransformationString()+"</grounding:xsltTransformationString>\n";
		        groundsOWLS+="</grounding:WsdlOutputMessageMap>\n</grounding:wsdlOutput>\n";
			}
			
			String groundOperation=getGroundOperation(atomicProcessGrounding, serviceURL);
			StringTokenizer operationTok=new StringTokenizer(groundOperation, "^^");
			String operation=operationTok.nextToken();
			String operationDatatype=operationTok.nextToken();
			groundsOWLS+="<grounding:wsdlOperation>\n<grounding:WsdlOperationRef>\n";
			groundsOWLS+="<grounding:operation rdf:datatype=\""+operationDatatype+"\">"+operation+"</grounding:operation>\n";
			
			String groundPortType=getGroundPortType(atomicProcessGrounding, serviceURL);
			StringTokenizer portTypeTok=new StringTokenizer(groundPortType, "^^");
			String portType=portTypeTok.nextToken();
			String portTypeDatatype=portTypeTok.nextToken();
			groundsOWLS+="<grounding:portType rdf:datatype=\""+portTypeDatatype+"\">"+portType+"</grounding:portType>\n";
			groundsOWLS+="</grounding:WsdlOperationRef>\n</grounding:wsdlOperation>\n";
			
			
			groundsOWLS+="</grounding:WsdlAtomicProcessGrounding>\n";
		}

		return groundsOWLS;
	}

	/**
	 * Returns the grounding input message of the given atomic process grounding URI
	 * @param atomicProcessGrounding URI to extract data from
	 * @param serviceURL it specifies the service URL if the query is not to the Jena DB. <code>null</code> to query the Jena DB model
	 * @return the grounding input message of the given atomic process grounding URI
	 */
	private String getGroundingInputMessage(String atomicProcessGrounding, String serviceURL){
		String inputMessage="";
		ModelMaker maker;
		Model base;
		OntModel m;
		if(serviceURL==null){
			maker = ModelFactory.createModelRDBMaker(conn);
			base = maker.createModel("http://example.org/ontologias");
			m = ModelFactory.createOntologyModel(getModelSpec(maker), base);
		}
		else{
			maker = ModelFactory.createMemModelMaker();
			base = maker.createModel("http://example.org/ontologias");
			m = ModelFactory.createOntologyModel(getModelSpec(maker), base);
			m.read(serviceURL);
		}

		String queryStringSearchName =
				"prefix grounding: <http://www.daml.org/services/owl-s/1.1/Grounding.owl#>"+
						"select ?x where { <"+atomicProcessGrounding+"> grounding:wsdlInputMessage ?x }";

		Query querySearchName = QueryFactory.create(queryStringSearchName);

		QueryExecution qeSearchName = QueryExecutionFactory.create(querySearchName, m);
		ResultSet resultsSearchName = qeSearchName.execSelect();


		if (resultsSearchName != null) {

			if(resultsSearchName.hasNext()) {
				inputMessage=resultsSearchName.next().getLiteral("x").toString();
			} 

		}//end if
		else{
			System.out.println("resultsSearchName is null");
		}

		qeSearchName.close();
		m.close();
		
		return inputMessage;
	}
	
	/**
	 * Returns the grounding output message of the given atomic process grounding URI
	 * @param atomicProcessGrounding URI to extract data from
	 * @param serviceURL it specifies the service URL if the query is not to the Jena DB. <code>null</code> to query the Jena DB model
	 * @return the grounding output message of the given atomic process grounding URI
	 */
	private String getGroundingOutputMessage(String atomicProcessGrounding, String serviceURL){
		String outputMessage="";
		ModelMaker maker;
		Model base;
		OntModel m;
		if(serviceURL==null){
			maker = ModelFactory.createModelRDBMaker(conn);
			base = maker.createModel("http://example.org/ontologias");
			m = ModelFactory.createOntologyModel(getModelSpec(maker), base);
		}
		else{
			maker = ModelFactory.createMemModelMaker();
			base = maker.createModel("http://example.org/ontologias");
			m = ModelFactory.createOntologyModel(getModelSpec(maker), base);
			m.read(serviceURL);
		}

		String queryStringSearchName =
				"prefix grounding: <http://www.daml.org/services/owl-s/1.1/Grounding.owl#>"+
						"select ?x where { <"+atomicProcessGrounding+"> grounding:wsdlOutputMessage ?x }";

		Query querySearchName = QueryFactory.create(queryStringSearchName);

		QueryExecution qeSearchName = QueryExecutionFactory.create(querySearchName, m);
		ResultSet resultsSearchName = qeSearchName.execSelect();


		if (resultsSearchName != null) {

			if(resultsSearchName.hasNext()) {
				outputMessage=resultsSearchName.next().getLiteral("x").toString();
			} 

		}//end if
		else{
			System.out.println("resultsSearchName is null");
		}

		qeSearchName.close();
		m.close();
		
		return outputMessage;
	}
	
	/**
	 * Returns the grounding owls process of the given atomic process grounding
	 * @param atomicProcessGrounding URI to extract data from
	 * @param serviceURL it specifies the service URL if the query is not to the Jena DB. <code>null</code> to query the Jena DB model
	 * @return the grounding owls process of the given atomic process grounding
	 */
	private String getGroundOwlsProcess(String atomicProcessGrounding, String serviceURL){
		
		String owlsProcess="";
		ModelMaker maker;
		Model base;
		OntModel m;
		if(serviceURL==null){
			maker = ModelFactory.createModelRDBMaker(conn);
			base = maker.createModel("http://example.org/ontologias");
			m = ModelFactory.createOntologyModel(getModelSpec(maker), base);
		}
		else{
			maker = ModelFactory.createMemModelMaker();
			base = maker.createModel("http://example.org/ontologias");
			m = ModelFactory.createOntologyModel(getModelSpec(maker), base);
			m.read(serviceURL);
		}

		String queryStringSearchName =
				"prefix grounding: <http://www.daml.org/services/owl-s/1.1/Grounding.owl#>"+
						"select ?x where { <"+atomicProcessGrounding+"> grounding:owlsProcess ?x }";

		Query querySearchName = QueryFactory.create(queryStringSearchName);

		QueryExecution qeSearchName = QueryExecutionFactory.create(querySearchName, m);
		ResultSet resultsSearchName = qeSearchName.execSelect();


		if (resultsSearchName != null) {

			if(resultsSearchName.hasNext()) {
				owlsProcess=resultsSearchName.next().getResource("x").toString();
			} 

		}//end if
		else{
			System.out.println("resultsSearchName is null");
		}

		qeSearchName.close();
		m.close();
		
		return owlsProcess;
		
	}
	
	/**
	 * Returns an {@link ArrayList} of {@link GroundingInOutput} with the data of the grounding inputs or outputs of the given atomic process grounding
	 * @param atomicProcessGrounding URI to extract data from
	 * @param serviceURL it specifies the service URL if the query is not to the Jena DB. <code>null</code> to query the Jena DB model
	 * @param input <code>true</code> to extract the data of the inputs, <code>false</code> to extract the data of the outputs
	 * @return an {@link ArrayList} of {@link GroundingInOutput} with the data of the grounding inputs or outputs of the given atomic process grounding
	 */
	private ArrayList<GroundingInOutput> getGroundingInOutputs(String atomicProcessGrounding, String serviceURL, boolean input){
		
		ArrayList<GroundingInOutput> groundInputs=new ArrayList<GroundingInOutput>();
		
		ModelMaker maker;
		Model base;
		OntModel m;
		if(serviceURL==null){
			maker = ModelFactory.createModelRDBMaker(conn);
			base = maker.createModel("http://example.org/ontologias");
			m = ModelFactory.createOntologyModel(getModelSpec(maker), base);
		}
		else{
			maker = ModelFactory.createMemModelMaker();
			base = maker.createModel("http://example.org/ontologias");
			m = ModelFactory.createOntologyModel(getModelSpec(maker), base);
			m.read(serviceURL);
		}

		String wsdlInputOutput="";
		if(input){
			wsdlInputOutput="grounding:wsdlInput";
		}
		else{
			wsdlInputOutput="grounding:wsdlOutput";
		}
		
		String queryStringSearchName =
				"prefix grounding: <http://www.daml.org/services/owl-s/1.1/Grounding.owl#>"+
						"select ?x ?y ?z where { <"+atomicProcessGrounding+"> "+wsdlInputOutput+
						"[  grounding:owlsParameter ?x ; grounding:wsdlMessagePart ?y ; grounding:xsltTransformationString ?z ] }";

		Query querySearchName = QueryFactory.create(queryStringSearchName);

		QueryExecution qeSearchName = QueryExecutionFactory.create(querySearchName, m);
		ResultSet resultsSearchName = qeSearchName.execSelect();

		String owlsParameter="",wsdlMessagePart="",xsltTransformationString="";
		if (resultsSearchName != null) {

			while(resultsSearchName.hasNext()) {
				QuerySolution qSol=resultsSearchName.next();
				owlsParameter=qSol.getResource("x").toString();
				wsdlMessagePart=qSol.getLiteral("y").toString();
				xsltTransformationString=qSol.getLiteral("z").toString();
				GroundingInOutput groundIn=new GroundingInOutput(owlsParameter, wsdlMessagePart, xsltTransformationString);
				groundInputs.add(groundIn);
			} 

		}//end if
		else{
			System.out.println("resultsSearchName is null");
		}

		qeSearchName.close();
		m.close();
		
		return groundInputs;
	}
	
	
	/**
	 * Returns the grounding operation of the given atomic process grounding
	 * @param atomicProcessGrounding URI to extract data from
	 * @param serviceURL it specifies the service URL if the query is not to the Jena DB. <code>null</code> to query the Jena DB model
	 * @return the grounding operation of the given atomic process grounding
	 */
	private String getGroundOperation(String atomicProcessGrounding, String serviceURL){
		
		String operation="";
		ModelMaker maker;
		Model base;
		OntModel m;
		if(serviceURL==null){
			maker = ModelFactory.createModelRDBMaker(conn);
			base = maker.createModel("http://example.org/ontologias");
			m = ModelFactory.createOntologyModel(getModelSpec(maker), base);
		}
		else{
			maker = ModelFactory.createMemModelMaker();
			base = maker.createModel("http://example.org/ontologias");
			m = ModelFactory.createOntologyModel(getModelSpec(maker), base);
			m.read(serviceURL);
		}

		String queryStringSearchName =
				"prefix grounding: <http://www.daml.org/services/owl-s/1.1/Grounding.owl#>"+
						"select ?x where { <"+atomicProcessGrounding+"> grounding:wsdlOperation"+
						"[  grounding:operation ?x ; ] }";

		Query querySearchName = QueryFactory.create(queryStringSearchName);

		QueryExecution qeSearchName = QueryExecutionFactory.create(querySearchName, m);
		ResultSet resultsSearchName = qeSearchName.execSelect();


		if (resultsSearchName != null) {

			if(resultsSearchName.hasNext()) {
				operation=resultsSearchName.next().getLiteral("x").toString();
			} 

		}//end if
		else{
			System.out.println("resultsSearchName is null");
		}

		qeSearchName.close();
		m.close();
		
		return operation;
	}
	
	/**
	 * Returns the grounding port type of the given atomic process grounding
	 * @param atomicProcessGrounding URI to extract data from
	 * @param serviceURL it specifies the service URL if the query is not to the Jena DB. <code>null</code> to query the Jena DB model
	 * @return the grounding port type of the given atomic process grounding
	 */
	private String getGroundPortType(String atomicProcessGrounding, String serviceURL){
		
		String portType="";
		ModelMaker maker;
		Model base;
		OntModel m;
		if(serviceURL==null){
			maker = ModelFactory.createModelRDBMaker(conn);
			base = maker.createModel("http://example.org/ontologias");
			m = ModelFactory.createOntologyModel(getModelSpec(maker), base);
		}
		else{
			maker = ModelFactory.createMemModelMaker();
			base = maker.createModel("http://example.org/ontologias");
			m = ModelFactory.createOntologyModel(getModelSpec(maker), base);
			m.read(serviceURL);
		}

		String queryStringSearchName =
				"prefix grounding: <http://www.daml.org/services/owl-s/1.1/Grounding.owl#>"+
						"select ?x where { <"+atomicProcessGrounding+"> grounding:wsdlOperation"+
						"[  grounding:portType ?x ; ] }";

		Query querySearchName = QueryFactory.create(queryStringSearchName);

		QueryExecution qeSearchName = QueryExecutionFactory.create(querySearchName, m);
		ResultSet resultsSearchName = qeSearchName.execSelect();


		if (resultsSearchName != null) {

			if(resultsSearchName.hasNext()) {
				portType=resultsSearchName.next().getLiteral("x").toString();
			} 

		}//end if
		else{
			System.out.println("resultsSearchName is null");
		}

		qeSearchName.close();
		m.close();
		
		return portType;
	}
	
	
	/**
	 * Implementation of the SF service Get Service. It returns an OWL-S specification with the information of the given service.
	 * @param serviceProfile URI to extract data from
	 * @return an OWL-S specification with the information of the given service.
	 */
	public String getService(String serviceProfile){
		String owlsService="";

		//headers
		owlsService+="<?xml version=\"1.0\" encoding=\"WINDOWS-1252\"?>"+"\n"+
				"<rdf:RDF  xmlns:owl       = \"http://www.w3.org/2002/07/owl#\""+"\n"+
				"xmlns:rdfs      = \"http://www.w3.org/2000/01/rdf-schema#\""+"\n"+
				"xmlns:rdf       = \"http://www.w3.org/1999/02/22-rdf-syntax-ns#\""+"\n"+
				"xmlns:xsd       = \"http://www.w3.org/2001/XMLSchema#\""+"\n"+
				"xmlns:service   = \"http://www.daml.org/services/owl-s/1.1/Service.owl#\""+"\n"+
				"xmlns:process   = \"http://www.daml.org/services/owl-s/1.1/Process.owl#\""+"\n"+
				"xmlns:profile    = \"http://www.daml.org/services/owl-s/1.1/Profile.owl#\""+"\n"+
				"xmlns:grounding = \"http://www.daml.org/services/owl-s/1.1/Grounding.owl#\""+"\n"+
				"xmlns:provider = \"http://127.0.0.1/ontology/provider.owl#\">"+"\n";

		//profile descriptions...
		//profile:serviceName
		//profile:textDescription
		owlsService+="<profile:Profile rdf:ID=\""+serviceProfile+"\">\n"+
				"<profile:serviceName xml:lang=\"en\">"+
				getProfileServiceName(serviceProfile)+"</profile:serviceName>"+"\n";

		owlsService+="<profile:textDescription xml:lang=\"en\">"+
				getProfileTextDescription(serviceProfile)+"</profile:textDescription>"+"\n";

		//providers
		//profile:contactInformation
		//a       provider:Provider
		owlsService+=getProvidersOWLS(serviceProfile, null);

		//inputs and outputs of a process with their types
		String processInputs="", processOutputs="";
		
		ArrayList<String> inputs=getInputs(serviceProfile,null);
		Iterator<String> iterInputs=inputs.iterator();
		while(iterInputs.hasNext()){
			//profile:hasInput
			String in=iterInputs.next();
			owlsService+="<profile:hasInput rdf:resource=\""+in+ "\"/>"+"\n";
			processInputs+=getInputOWLS(in);


		}
		
		ArrayList<String> outputs=getOutputs(serviceProfile,null);
		Iterator<String> iterOutputs=outputs.iterator();
		while(iterOutputs.hasNext()){
			//profile:hasOutput
			String out=iterOutputs.next();
			owlsService+="<profile:hasOutput rdf:resource=\""+out+ "\"/>"+"\n";
			processOutputs+=getOutputOWLS(out);

		}
		owlsService+="</profile:Profile>";

		//process:parameterType
		//process:parameterType

		owlsService+=processInputs+processOutputs;


		//all the groundings
		owlsService+=getGroundingsOWLS(serviceProfile);

		owlsService+="</rdf:RDF>";
		return owlsService;
	}

	/**
	 * Removes a provider from a registered service in the Jena DB
	 * @param serviceProfile URI of the service to remove the provider
	 * @param providerName of the provider to remove
	 */
	public void removeProvider(String serviceProfile, String providerName){
		
		StringTokenizer tokenServiceProf=new StringTokenizer(serviceProfile, "#");
		String baseURI=tokenServiceProf.nextToken();
		String profileName=tokenServiceProf.nextToken();
		
		ModelMaker maker= ModelFactory.createModelRDBMaker(conn);
		Model base= maker.createModel("http://example.org/ontologias");
		OntModel m= ModelFactory.createOntologyModel(getModelSpec(maker), base);
		
		
		//Delete provider
		String delete= 
				"prefix xsd: <http://www.w3.org/2001/XMLSchema#>" +
				"prefix service: <http://www.daml.org/services/owl-s/1.1/Service.owl#>" +
				"prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>" +  
				"prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>" +
				"prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
				"prefix mind: <"+baseURI+"#>" +
				"delete {?x ?y ?z} " +
				"where" +
				"{mind:" +profileName+" ?y ?z" +
				" filter ( ?y = profile:contactInformation " +
				"&& ?z = <"+baseURI+"#"+providerName+">" +
				")" +
				"?x ?y ?z}";
		
		String delete2= 
				"prefix xsd: <http://www.w3.org/2001/XMLSchema#>" +
				"prefix service: <http://www.daml.org/services/owl-s/1.1/Service.owl#>" +
				"prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>" +  
				"prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>" +
				"prefix provider: <http://127.0.0.1/ontology/provider.owl#>"+
				"prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
				"prefix mind: <"+baseURI+"#>" +
				"delete {?x ?y ?z} " +
				"where" +
				"{mind:" +providerName+" ?y ?z" +
				" filter ( ( ?y = provider:entityID " +
				"|| ?y = provider:entityType " +
				"|| ?y = provider:language " +
				"|| ?y = provider:performative "+ 
				"|| ?z = provider:Provider )"+ 
				"&& ?x = <"+baseURI+"#"+providerName+">" +
				")" +
				"?x ?y ?z}";

		
		QuerySolution querysol=new QuerySolutionMap();
		UpdateAction.parseExecute(delete, m, querysol);
		UpdateAction.parseExecute(delete2, m, querysol);

	}

	/**
	 * Registers the services specified in the given file name if they are in the URL: http://127.0.0.1/services/1.1/
	 * @param fileName with service names
	 */
	public void registerNServices(String fileName){
		File file = null;
		FileReader fr = null;
		BufferedReader br = null;
		String baseURL="http://127.0.0.1/services/1.1/";
		try {

			file = new File (fileName);
			fr = new FileReader (file);
			br = new BufferedReader(fr);

			// Lectura del fichero
			String line;
			while((line=br.readLine())!=null){
				System.out.println(line);
				RegisterService(baseURL+line);
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}


	/**
	 * Deregister service of the SF. It removes a complete service given its service profile URI.
	 * @param serviceProfile URI of the service to deregister
	 */
	public void deregisterService(String serviceProfile){

		StringTokenizer servProfTok=new StringTokenizer(serviceProfile, "#");
		String baseURL=servProfTok.nextToken();
		String profileName=servProfTok.nextToken();
		
		String serviceURI=getServiceURI(serviceProfile, null);
		StringTokenizer tokServURI=new StringTokenizer(serviceURI, "#");
		tokServURI.nextToken();
		String serviceName=tokServURI.nextToken();
		
		String serviceprocess=getServiceProcess(serviceProfile, null);
		StringTokenizer tokServProc = new StringTokenizer(serviceprocess,"#");
		tokServProc.nextToken();
		String processName = tokServProc.nextToken();

		ModelMaker maker = ModelFactory.createModelRDBMaker(conn);
		Model base = maker.createModel("http://example.org/ontologias");
		OntModel m = ModelFactory.createOntologyModel(getModelSpec(maker), base);

		ArrayList<String> providers=getProviders(serviceProfile);
		Iterator<String> iterProvs=providers.iterator();
		while(iterProvs.hasNext()){
			String provider=iterProvs.next();
			StringTokenizer tokProv=new StringTokenizer(provider, "#");
			tokProv.nextToken();
			String providerName=tokProv.nextToken();
			removeProvider(serviceProfile, providerName);
		}
		
		//TODO buscar por wsdl:Input o [ a       grounding:WsdlInputMessageMap ;
//        grounding:owlsParameter
//        <http://127.0.0.1/services/1.1/calculateSunriseTime.owls#_LATITUDE> ;
//grounding:wsdlMessagePart
//        "http://127.0.0.1/wsdl/CalculateSunrise#_LATITUDE"^^xsd:anyURI ;
//grounding:xsltTransformationString
//        "None (XSL)"
//] ; 
//		para eliminarlo 
		
		RemoveProcess(baseURL, processName, serviceProfile, serviceURI, serviceProfile, m);

		m.commit();

		// Delete profile tuples where the property is profile
		String update =
				"prefix xsd: <http://www.w3.org/2001/XMLSchema#>"
						+ "prefix service: <http://www.daml.org/services/owl-s/1.1/Service.owl#>"
						+ "prefix proc: <http://www.daml.org/services/owl-s/1.1/Process.owl#>"
						+ "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>"
						+ "prefix mind: <" + baseURL + "#>" +
						"delete {?x ?y ?z}" + "where" + "{" + "mind:"
						+ profileName + " ?y ?z"
						+ " filter ( ?y = profile:hasInput "
						+ "|| ?y = profile:hasOutput "
						+ "|| ?y = profile:serviceName "
						+ "|| ?y = profile:textDescription "
						+ "|| ?y = profile:has_process "
						+ "|| ?y = service:isPresentedBy "
						+ "|| ?y = service:presents "
						+ "|| ?y = profile:contactInformation "
						+ "|| ?z = profile:Profile " + ")" + "?x ?y ?z}";

		String update2 =
				"prefix xsd: <http://www.w3.org/2001/XMLSchema#>"
						+ "prefix service: <http://www.daml.org/services/owl-s/1.1/Service.owl#>"
						+ "prefix proc: <http://www.daml.org/services/owl-s/1.1/Process.owl#>"
						+ "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>"
						+ "prefix mind: <" + baseURL + "#>" +
						"delete {?x ?y ?z}" + "where" + "{" + "mind:"
						+ serviceName + " ?y ?z"
						+ " filter ( ?z = service:Service "
						+ "|| ?y = service:presents " + ")"
						+ "?x ?y ?z}";
		
		String update3= 
				"prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
				"prefix owl: <http://www.w3.org/2002/07/owl#>" +
				"delete {?x ?y ?z} " +
				"where" +
				"{ <"+baseURL+"> ?y ?z" +
				" filter ( ( ?y = owl:imports" +
				"|| ?z = owl:Ontology )"+ 
				"&& ?x = <"+baseURL +">"+
				")" +
				"?x ?y ?z}";

		
		// Execute the query and obtain results
		QuerySolution querysol = new QuerySolutionMap();
		UpdateAction.parseExecute(update, m, querysol);
		UpdateAction.parseExecute(update2, m, querysol);
		UpdateAction.parseExecute(update3, m, querysol);

		m.commit();

	}

	
	/**
	 * Returns an {@link ArrayList} with the provider URIs of the given service profile URI
	 * @param serviceProfile URI to extract its providers
	 * @return an {@link ArrayList} with the provider URIs of the given service profile URI
	 */
	private ArrayList<String> getProviders(String serviceProfile){
		ArrayList<String> providers=new ArrayList<String>();
		
		ModelMaker maker = ModelFactory.createModelRDBMaker(conn);
		Model base = maker.createModel("http://example.org/ontologias");
		OntModel m = ModelFactory.createOntologyModel(getModelSpec(maker), base);
		
		String queryStringSearchName =
				"prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>"+
						"prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>"+
						"select ?x where { "+"<"+serviceProfile+">"+" profile:contactInformation ?x }";

		Query querySearchName = QueryFactory.create(queryStringSearchName);

		QueryExecution qeSearchName = QueryExecutionFactory.create(querySearchName, m);
		ResultSet resultsSearchName = qeSearchName.execSelect();

		if (resultsSearchName != null) {

			while(resultsSearchName.hasNext()) {
				String providerURI=resultsSearchName.next().getResource("x").toString();
				providers.add(providerURI);
			}
		}
		
		return providers;
		
	}
	
	/**
	 * Returns the OWL-S specification of all providers and their information of the given service profile URI
	 * @param serviceProfile URI to extract its providers
	 * @param serviceURL it specifies the service URL if the query is not to the Jena DB. <code>null</code> to query the Jena DB model
	 * @return the OWL-S specification of all providers and their information of the given service profile URI
	 */
	private String getProvidersOWLS(String serviceProfile, String serviceURL){

		String providersOWLS="";
		ModelMaker maker;
		Model base;
		OntModel m;
		if(serviceURL==null){
			maker = ModelFactory.createModelRDBMaker(conn);
			base = maker.createModel("http://example.org/ontologias");
			m = ModelFactory.createOntologyModel(getModelSpec(maker), base);
		}
		else{
			maker = ModelFactory.createMemModelMaker();
			base = maker.createModel("http://example.org/ontologias");
			m = ModelFactory.createOntologyModel(getModelSpec(maker), base);
			m.read(serviceURL);
		}

		String queryStringSearchName =
				"prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>"+
						"prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>"+
						"select ?x where { "+"<"+serviceProfile+">"+" profile:contactInformation ?x }";

		Query querySearchName = QueryFactory.create(queryStringSearchName);

		QueryExecution qeSearchName = QueryExecutionFactory.create(querySearchName, m);
		ResultSet resultsSearchName = qeSearchName.execSelect();

		String providerURI="", entityID="", entityType="", language="", performative="";

		if (resultsSearchName != null) {

			while(resultsSearchName.hasNext()) {
				providerURI = resultsSearchName.next().getResource("x").toString();


				entityID=getProviderParameter(providerURI, "entityID", serviceURL);
				entityType=getProviderParameter(providerURI, "entityType", serviceURL);
				language=getProviderParameter(providerURI, "language", serviceURL);
				performative=getProviderParameter(providerURI, "performative", serviceURL);

				StringTokenizer tokenProvURI=new StringTokenizer(providerURI,"#");
				tokenProvURI.nextToken();
				String providerName=tokenProvURI.nextToken();
				
				providersOWLS+="<profile:contactInformation>"+"\n"+
						"<provider:Provider rdf:ID=\""+providerName+"\">"+"\n"+
						"<provider:entityID rdf:datatype=\"^^xsd;string\">"+entityID+"</provider:entityID>"+"\n"+
						"<provider:entityType rdf:datatype=\"^^xsd;string\">"+entityType+"</provider:entityType>"+"\n"+
						"<provider:language rdf:datatype=\"^^xsd;string\">"+language+"</provider:language>"+"\n"+
						"<provider:performative rdf:datatype=\"^^xsd;string\">"+performative+"</provider:performative>"+"\n"+
						"</provider:Provider>"+"\n"+
						"</profile:contactInformation>"+"\n";

			} 


		}//end if
		else{
			System.out.println("resultsSearchName is null");
		}

		// close the query
		qeSearchName.close();
		m.close();

		return providersOWLS;
	}


	/**
	 * Returns the value of the given provider parameter 
	 * @param providerURI to extract its parameter
	 * @param parameter of the provider to extract
	 * @param serviceURL it specifies the service URL if the query is not to the Jena DB. <code>null</code> to query the Jena DB model
	 * @return the value of the given provider parameter 
	 */
	private String getProviderParameter(String providerURI, String parameter, String serviceURL){

		ModelMaker maker;
		Model base;
		OntModel m;
		if(serviceURL==null){
			maker = ModelFactory.createModelRDBMaker(conn);
			base = maker.createModel("http://example.org/ontologias");
			m = ModelFactory.createOntologyModel(getModelSpec(maker), base);
		}
		else{
			maker = ModelFactory.createMemModelMaker();
			base = maker.createModel("http://example.org/ontologias");
			m = ModelFactory.createOntologyModel(getModelSpec(maker), base);
			m.read(serviceURL);
		}

		String queryStringSearchName2 =
				"prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>"+
						"prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>"+
						"prefix provider: <http://127.0.0.1/ontology/provider.owl#>"+
						"select ?x where { <"+providerURI+"> provider:"+parameter+" ?x }";

		Query querySearchName2 = QueryFactory.create(queryStringSearchName2);

		QueryExecution qeSearchName2 = QueryExecutionFactory.create(querySearchName2, m);
		ResultSet resultsSearchName2 = qeSearchName2.execSelect();
		String result="";
		if (resultsSearchName2 != null) {

			if(resultsSearchName2.hasNext()) {
				String str = resultsSearchName2.next().getLiteral("x").toString();
				StringTokenizer strToken=new StringTokenizer(str, "^^");
				result=strToken.nextToken();
			}
		}
		return result;
	}



	//hacer una consulta por cada entrada deseada, guardando los servicios que tengan al menos una entrada (sin repetirlos en la lista)
	//de cada servicio, comprobar exactamente cuantas entradas coinciden, y después las salidas
	//sumar con algún tipo de ponderación

	/**
	 * The SearchService receives as parameters two lists with the data types of the inputs and outputs desired. 
	 * With these parameters, the service searches in the Jena DB and returns the more similar services, that is, 
	 * the services that have the same (or almost the same) data types as inputs and outputs, weighted in function 
	 * of the amount of similarity.
	 * @param inputs list of the desired input parameter type to search a similar servie. Example: \"http://127.0.0.1/ontology/books.owl#Novel\"^^xsd:anyURI
	 * @param outputs list of the desired output parameter type to search a similar servie. Example: \"http://127.0.0.1/ontology/books.owl#Novel\"^^xsd:anyURI
	 * @return a {@link Profile} {@link ArrayList} with the similar registered services with their similarity degree
	 */
	public ArrayList<Profile> SearchService(ArrayList<String> inputs, ArrayList<String> outputs){


		ModelMaker maker = ModelFactory.createModelRDBMaker(conn);
		Model base = maker.createModel("http://example.org/ontologias");
		OntModel m = ModelFactory.createOntologyModel(getModelSpec(maker), base);

		ArrayList<String> candidates=new ArrayList<String>();

		//the service searches each input and add to a list each service that has an equal input as a candidate
		Iterator<String> iterInputs=inputs.iterator();
		while(iterInputs.hasNext()){



			String in=iterInputs.next();

			String queryStr =
					"prefix xsd: <http://www.w3.org/2001/XMLSchema#>"+
							"prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>"+
							"prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>"+
							"select ?x where { ?x a process:Input ; process:parameterType "+in+" . }";

			Query query = QueryFactory.create(queryStr);

			// Execute the query and obtain results
			QueryExecution querySearchInputs = QueryExecutionFactory.create(query, m);
			ResultSet resultsSearchInputs = querySearchInputs.execSelect();

			if (resultsSearchInputs != null) {
				
				while (resultsSearchInputs.hasNext()) {
					
					QuerySolution sol=resultsSearchInputs.next();


					Resource resource=sol.getResource("x");
					String cand=resource.getURI();
					if(!candidates.contains(cand))
						candidates.add(cand);

					System.out.println("candidate: "+cand);



				}//end for 
			}//end if

			// close the query
			querySearchInputs.close();

		}





		//store a list of profiles with their similarity weights to the service
		ArrayList<Profile> profiles=new ArrayList<Profile>();

		Iterator<String> iterCandidates=candidates.iterator();
		while(iterCandidates.hasNext()){
			String cand=iterCandidates.next();

			String queryStr =
					"prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>"+
							"prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>"+
							"select ?x where { ?x profile:hasInput "+"<"+cand+">"+" }";

			Query query = QueryFactory.create(queryStr);

			// Execute the query and obtain results
			QueryExecution querySearchInputs = QueryExecutionFactory.create(query, m);
			ResultSet resultsSearchInputs = querySearchInputs.execSelect();

			if (resultsSearchInputs != null && resultsSearchInputs.hasNext()) {
				QuerySolution sol=resultsSearchInputs.next();

				Resource resource=sol.getResource("x");
				String profileURL=resource.getURI();
				Profile profile= new Profile(profileURL, 0f);
				if(!profiles.contains(profile))
					profiles.add(profile);

				System.out.println("profile: "+profile.getUrl());
			}

		}

		
		//For each candidate, search its inputs and outputs, and if the input/output is the same to the searched, 
		//the similarity degree (similarityToAsked=sameInputs+sameOutputs/searchInputs+searchOutputs) 
		//of the asked in the search will be greater
		//Also, the similarity degree to the found service is obtained with this Formula: 
		//similarityToFoundService=sameInputs+sameOutputs/foundServiceInputs+foundServiceOutputs
		//The final similarity degree is calculated by Formula: 
		//similarityDegree=0.5*similarityToAsked+0.5*similarityToFoundService
		//taking into account the two similarity degrees defined previously.
		
		Iterator<Profile> iterProfiles=profiles.iterator();
		while(iterProfiles.hasNext()){
			Profile profile=iterProfiles.next();
			int inputsProfile=0;
			int outputsProfile=0;

			//SEARCH INPUTS
			String queryStr =
					"prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>"+
							"prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>"+
							"select ?x where { "+"<"+profile.getUrl()+">"+" profile:hasInput ?x }";

			Query query = QueryFactory.create(queryStr);

			// Execute the query and obtain results
			QueryExecution querySearchInputs = QueryExecutionFactory.create(query, m);
			ResultSet resultsSearchInputs = querySearchInputs.execSelect();


			if (resultsSearchInputs != null){

				while(resultsSearchInputs.hasNext()) {
					inputsProfile++;
					QuerySolution sol=resultsSearchInputs.next();

					Resource resource=sol.getResource("x");
					String input=resource.getURI();

					//explore all inputs (searching their type) to find out if it is in the service
					String queryStrType =
							"prefix xsd: <http://www.w3.org/2001/XMLSchema#>"+
									"prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>"+
									"prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>"+
									"select ?x where { <"+input+"> a process:Input ; process:parameterType ?x . }";

					Query queryType = QueryFactory.create(queryStrType);

					// Execute the query and obtain results
					QueryExecution querySearchInputType = QueryExecutionFactory.create(queryType, m);
					ResultSet resultsSearchInputType = querySearchInputType.execSelect();

					if (resultsSearchInputType != null && resultsSearchInputType.hasNext()) {
						QuerySolution sol2=resultsSearchInputType.next();

						Literal literal2=sol2.getLiteral("x");
						String parameterType=literal2.getString();
						System.out.println("\t\tparameterType: "+parameterType);
						Iterator<String> iterInputsSearch=inputs.iterator();
						while(iterInputsSearch.hasNext()){
							String inputSearch=iterInputsSearch.next();
							String inputSearchModif=inputSearch.replaceAll("\"", "");
							StringTokenizer stringTokenizer=new StringTokenizer(inputSearchModif, "^^");
							inputSearchModif=stringTokenizer.nextToken();
							if(inputSearchModif.equalsIgnoreCase(parameterType)){
								profile.setSuitability(profile.getSuitability()+1);
								break;
							}
						}

					}

					System.out.println("\tinput: "+input);
				}
			}


			//SEARCH OUTPUTS
			String queryStrOutputs =
					"prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>"+
							"prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>"+
							"select ?x where { "+"<"+profile.getUrl()+">"+" profile:hasOutput ?x }";

			Query queryOutputs = QueryFactory.create(queryStrOutputs);

			// Execute the query and obtain results
			QueryExecution querySearchOutputs = QueryExecutionFactory.create(queryOutputs, m);
			ResultSet resultsSearchOutputs = querySearchOutputs.execSelect();

			if (resultsSearchOutputs != null){

				while(resultsSearchOutputs.hasNext()) {
					outputsProfile++;
					QuerySolution sol=resultsSearchOutputs.next();

					Resource resource=sol.getResource("x");
					String output=resource.getURI();

					//explore all outputs (searching their type) to find out if it is in the service
					String queryStrType =
							"prefix xsd: <http://www.w3.org/2001/XMLSchema#>"+
									"prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>"+
									"prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>"+
									"select ?x where { <"+output+"> a process:Output ; process:parameterType ?x . }";

					Query queryType = QueryFactory.create(queryStrType);

					// Execute the query and obtain results
					QueryExecution querySearchOutputType = QueryExecutionFactory.create(queryType, m);
					ResultSet resultsSearchOutputType = querySearchOutputType.execSelect();

					if (resultsSearchOutputType != null && resultsSearchOutputType.hasNext()) {
						QuerySolution sol2=resultsSearchOutputType.next();

						Literal literal2=sol2.getLiteral("x");
						String parameterType=literal2.getString();
						System.out.println("\t\tparameterType: "+parameterType);
						Iterator<String> iterOutputsSearch=outputs.iterator();
						while(iterOutputsSearch.hasNext()){
							String outputSearch=iterOutputsSearch.next();
							String outputSearchModif=outputSearch.replaceAll("\"", "");
							StringTokenizer stringTokenizer=new StringTokenizer(outputSearchModif, "^^");
							outputSearchModif=stringTokenizer.nextToken();
							if(outputSearchModif.equalsIgnoreCase(parameterType)){
								profile.setSuitability(profile.getSuitability()+1);
								break;
							}
						}

					}

					System.out.println("\toutput: "+output);
				}
			}

			System.out.println(profile.getSuitability()+" "+inputs.size()+" "+outputs.size());

			//obtain the final similarity degree of the profile
			float similarityToAsked=profile.getSuitability()/(inputs.size()+outputs.size());
			float similarityToFoundService=profile.getSuitability()/(inputsProfile+outputsProfile);
			profile.setSuitability(0.5f*similarityToAsked+0.5f*similarityToFoundService);

			System.out.println(profile.getUrl()+" -> "+profile.getSuitability());

		}

		//sort the found candidate profiles by their similarity
		Collections.sort(profiles);
		
		return profiles;
		
	}

	/**
	 * Removes the given process from the Jena DB
	 * @param urlProcessDoc
	 * @param processname
	 * @param urlProfileService
	 * @param urlProcessService
	 * @param serviceProfile
	 * @param m Jena DB Model
	 */
	private void RemoveProcess(String urlProcessDoc, String processname, String urlProfileService, 
			String urlProcessService, String serviceProfile, OntModel m){

		if (DEBUG) {
			System.out.println("Removing Process ... ");
		}
		
		String processGround = GetServiceGrounding(urlProcessDoc, processname,urlProcessService, m);
		String processGroundWSDL = GetServiceWSDLGrounding(urlProcessDoc, processGround, m);
		String WsdlURL = GetServiceWSDLGroundingDoc(urlProcessDoc, processGroundWSDL, m);

		// Delete Process tuples	
		DeleteWSDLMessagePart(urlProcessDoc, WsdlURL, m);
		DeleteWSDLOperation(urlProcessDoc, WsdlURL, m);
		DeleteWSDLPortType(urlProcessDoc, WsdlURL, m);
		DeleteProcessInputs(urlProcessDoc, processname, WsdlURL, m);
		DeleteProcessOutputs(urlProcessDoc, processname, WsdlURL, m);
		DeleteProcessGrounding(urlProcessDoc, processname, processGround, processGroundWSDL, m);
		DeleteProcess(urlProcessDoc, processname, m);

		
	}// end RemoveProcess


//	/**
//	 * GetServiceProfile
//	 * @param String urlProcessDoc
//	 * @param String processname
//	 * @param OntModel m
//	 * @return 
//	 */ 
//	private String GetServiceProcess(String urlProcessDoc, String processname, OntModel m){
//		//Query to get the service profile
//		String queryStringProfile =
//				"prefix xsd: <http://www.w3.org/2001/XMLSchema#>"
//						+ "prefix service: <http://www.daml.org/services/owl-s/1.1/Service.owl#>"
//						+ "prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>"
//						+ "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>"
//						+ "prefix actor: <http://www.daml.org/services/owl-s/1.1/ActorDefault.owl#>"
//						+ "prefix mind: <"+urlProcessDoc+"#>" 
//						+ "select ?x " 
//						+ "where {" 
//						+ "mind:"+processname+" service:describes ?x " 
//						+ "}";
//
//		Query queryProfile = QueryFactory.create(queryStringProfile);
//
//		// Execute the query and obtain results
//		QueryExecution qeProfile = QueryExecutionFactory.create(queryProfile, m);
//		ResultSet resultProfile = qeProfile.execSelect();
//		if(resultProfile.hasNext()){
//			// To get the name of the profile
//			String result = resultProfile.next().toString();
//			StringTokenizer Tok = new StringTokenizer(result);
//			String processProfileResult = Tok.nextToken("=");
//			String processProfile = Tok.nextToken();
//			processProfile = processProfile.replace(")", "");
//			if (DEBUG) {
//				System.out.println("Process Profile: " + processProfile);
//			}
//
//			return(processProfile);
//		}
//		else{
//			if (DEBUG) {
//				System.out.println("There is not Process Profile!!");
//			}
//			return null;
//		}
//
//	}//end GetServiceProfile 

	/**
	 * GetServiceGrounding
	 * @param String urlProcessDoc
	 * @param String processname
	 * @param String processProfile
	 * @param OntModel m
	 */ 
	private String GetServiceGrounding(String urlProcessDoc, String processname, String processProfile, OntModel m){

		// Query to get the service Grounding
		String queryStringProcessGround =
				"prefix xsd: <http://www.w3.org/2001/XMLSchema#>"
						+ "prefix service: <http://www.daml.org/services/owl-s/1.1/Service.owl#>"
						+ "prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>"
						+ "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>"
						+ "prefix actor: <http://www.daml.org/services/owl-s/1.1/ActorDefault.owl#>"
						+ "prefix mind: <"+urlProcessDoc+"#>" 
						+ "select ?x " 
						+ "where {" 
						+ "?x service:supportedBy <"+processProfile+">"
						+ "}";

		Query queryProcessGround = QueryFactory.create(queryStringProcessGround);

		// Execute the query and obtain results
		QueryExecution qeProcessGround = QueryExecutionFactory.create(queryProcessGround, m);
		ResultSet resultProcessGround = qeProcessGround.execSelect();
		// To take the grounding
		String result = resultProcessGround.next().toString();
		StringTokenizer Tok = new StringTokenizer(result);
		//String processGroundResult = Tok.nextToken("=");
		Tok.nextToken("=");
		String processGround = Tok.nextToken();
		processGround = processGround.replace(")", "");

		if (DEBUG) {
			System.out.println("Process Ground: " + processGround);
		}

		return(processGround);

	} //end GetServiceGrounding



	/**
	 * GetServiceWSDLGrounding
	 * @param String urlProcessDoc
	 * @param String processGround
	 * @param OntModel m
	 */ 
	private String GetServiceWSDLGrounding(String urlProcessDoc, String processGround, OntModel m){

		// Query to get the service WSDLGrounding
		String queryStringProcessGroundWSDL =
				"prefix xsd: <http://www.w3.org/2001/XMLSchema#>"
						+ "prefix service: <http://www.daml.org/services/owl-s/1.1/Service.owl#>"
						+ "prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>"
						+ "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>"
						+ "prefix grounding: <http://www.daml.org/services/owl-s/1.1/Grounding.owl#>" 
						+ "prefix actor: <http://www.daml.org/services/owl-s/1.1/ActorDefault.owl#>"
						+ "prefix mind: <"+urlProcessDoc+"#>" 
						+ "select ?x " 
						+ "where {" 
						+ processGround +" grounding:hasAtomicProcessGrounding ?x" 
						+ "}";

		Query queryProcessGroundWSDL = QueryFactory.create(queryStringProcessGroundWSDL);

		// Execute the query and obtain results
		QueryExecution qeProcessGroundWSDL = QueryExecutionFactory.create(queryProcessGroundWSDL, m);
		ResultSet resultProcessGroundWSDL = qeProcessGroundWSDL.execSelect();

		// To take the WSDL Grounding		
		String result = resultProcessGroundWSDL.next().toString();
		StringTokenizer Tok = new StringTokenizer(result);
		//String processGroundWSDLResult = Tok.nextToken("=");
		Tok.nextToken("=");
		String processGroundWSDL = Tok.nextToken();
		processGroundWSDL = processGroundWSDL.replace(")", "");

		if (DEBUG) {
			System.out.println("Process Ground WSDL: " + processGroundWSDL);
		}
		return(processGroundWSDL);

	}//end GetServiceWSDLGrounding



	/**
	 * GetServiceWSDLGroundingDoc
	 * @param String urlProcessDoc
	 * @param String processGroundWSDL
	 * @param OntModel m
	 */ 
	private String GetServiceWSDLGroundingDoc(String urlProcessDoc, String processGroundWSDL, OntModel m){

		// Query to get the service WSDLGrounding Document
		String queryStringDocWSDL =
				"prefix xsd: <http://www.w3.org/2001/XMLSchema#>"
						+ "prefix service: <http://www.daml.org/services/owl-s/1.1/Service.owl#>"
						+ "prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>"
						+ "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>"
						+ "prefix grounding: <http://www.daml.org/services/owl-s/1.1/Grounding.owl#>" 
						+ "prefix actor: <http://www.daml.org/services/owl-s/1.1/ActorDefault.owl#>"
						+ " select ?x " 
						+ "where {" 
						+ processGroundWSDL +" grounding:wsdlInputMessage ?x" 
						+ "}";

		System.out.println("queryStringDocWSDL: "+queryStringDocWSDL);
		Query queryDocWSDL = QueryFactory.create(queryStringDocWSDL);

		// Execute the query and obtain results
		QueryExecution qeDocWSDL = QueryExecutionFactory.create(queryDocWSDL, m);
		ResultSet resultDocWSDL = qeDocWSDL.execSelect();


		// To take the WSDL Grounding Document
		String result = resultDocWSDL.next().toString();
		StringTokenizer Tok = new StringTokenizer(result);
		//String DocWSDLResult = Tok.nextToken("=");
		Tok.nextToken("=");
		String DocWSDL = Tok.nextToken();
		DocWSDL = DocWSDL.replace(")", "");

		if (DEBUG) {
			System.out.println("DOC WSDL: " + DocWSDL);
		}
		// To take the URL of the WSDL Document
		Tok = new StringTokenizer(DocWSDL);
		String DocURL = Tok.nextToken("?");
		DocURL = DocURL.replace("\"", " ");
		if (DEBUG) {
			System.out.println("DocURL: " + DocURL);
		}
		Tok = new StringTokenizer(DocURL, "#");
		String DocPartURL = Tok.nextToken().trim();
		System.out.println("DocPartURL: "+DocPartURL);	

		return DocPartURL;
	}//end GetServiceWSDLGroundingDoc



//	/**
//	 * DeleteProfile
//	 * @param urlProfileService
//	 * @param Profile
//	 * @param m
//	 * @return
//	 */
//	private void DeleteProfile(String urlProfileService,String Profile, OntModel m){
//
//		if (DEBUG) {
//			System.out.println("Delete Profile... "+urlProfileService);
//		}
//
//		//Delete profile tuples where the property is profile
//		String update= 
//				"prefix xsd: <http://www.w3.org/2001/XMLSchema#>" +
//						"prefix service: <http://www.daml.org/services/owl-s/1.1/Service.owl#>" +
//						"prefix proc: <http://www.daml.org/services/owl-s/1.1/Process.owl#>" +  
//						"prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>" + 
//						"delete {?x ?y ?z}" +
//						"where" +
//						"{" +Profile+" ?y ?z" +
//						" filter ( ?y = profile:hasInput " +
//						"|| ?y = profile:hasOutput " +
//						"|| ?y = profile:serviceName " +
//						"|| ?y = service:isPresentedBy " +
//						"|| ?y = service:presentedBy " +
//						"|| ?y = profile:textDescription " +
//						"|| ?y = profile:has_process " +
//						"|| ?y = service:presents " +
//						"|| ?z = profile:Profile " +
//						")" +
//						"?x ?y ?z}";
//
//
//		//Delete tuples where the property is service (it is related with the profile)
//		String update2= 
//				"prefix xsd: <http://www.w3.org/2001/XMLSchema#>" +
//						"prefix service: <http://www.daml.org/services/owl-s/1.1/Service.owl#>" +
//						"prefix proc: <http://www.daml.org/services/owl-s/1.1/Process.owl#>" +  
//						"prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>" + 
//						"delete {?x ?y ?z}" +
//						"where" +
//						"{ <"+urlProfileService+"> ?y ?z" +
//						" filter ( ?z = service:Service " +
//						"|| ?y = service:presents " +
//						")" +
//						"?x ?y ?z}";
//
//
//		// Execute the query and obtain results
//		QuerySolution querysol=new QuerySolutionMap();
//		UpdateAction.parseExecute(update, m, querysol);
//		UpdateAction.parseExecute(update2, m, querysol);
//
//
//	}// end DeleteProfile





	/**
	 * DeleteWSDLMessagePart
	 * @param urlProcessDoc
	 * @param WsdlURL
	 * @param m
	 * @return 
	 */
	private void DeleteWSDLMessagePart(String urlProcessDoc, String WsdlURL, OntModel m){


		// Query to get the service input and output tuples related with the property grounding:wsdlMessagePart
		String queryStringWSDLMsgMap =
				"prefix xsd: <http://www.w3.org/2001/XMLSchema#>"
						+ "prefix service: <http://www.daml.org/services/owl-s/1.1/Service.owl#>"
						+ "prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>"
						+ "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>"
						+ "prefix grounding: <http://www.daml.org/services/owl-s/1.1/Grounding.owl#>" 
						+ "prefix actor: <http://www.daml.org/services/owl-s/1.1/ActorDefault.owl#>"
						+ "prefix mind: <"+urlProcessDoc+"#>" 
						+ "select ?y " 
						+ "where {" 
						+ "?x grounding:wsdlMessagePart ?y " 
						+ "}";

		Query queryWSDLMsgMap = QueryFactory.create(queryStringWSDLMsgMap);
		WsdlURL=WsdlURL.replace(" ", "");

		// Execute the query and obtain the WsdlMessageMap value  
		QueryExecution qeWSDLMsgMap = QueryExecutionFactory.create(queryWSDLMsgMap, m);
		ResultSet resultsqeWSDLMsgMap = qeWSDLMsgMap.execSelect();

		// For each WsdlMessageMap, all the tuples related with the service are deleted
		if (resultsqeWSDLMsgMap != null) {

			while (resultsqeWSDLMsgMap.hasNext()) {

				// To take only the WsdlMessageMap 
				String result = resultsqeWSDLMsgMap.next().toString();
				// If the url related with the WsdlMessageMap property contains the url of the service,
				// the tuple should be deleted

				if(result.contains((CharSequence)WsdlURL)){
					StringTokenizer Tok = new StringTokenizer(result);
					String wsdlMsg = Tok.nextToken("=");
					wsdlMsg = Tok.nextToken();
					wsdlMsg = wsdlMsg.replace(")", "");
					System.out.println("wsdlMSG : "+wsdlMsg);

					// Delete the WsdlMessageMap tuple related with the service grounding
					String updateWSDLMessageMap= 
							"prefix xsd: <http://www.w3.org/2001/XMLSchema#>" +
									"prefix service: <http://www.daml.org/services/owl-s/1.1/Service.owl#>" +
									"prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>" +  
									"prefix grounding: <http://www.daml.org/services/owl-s/1.1/Grounding.owl#>" +
									"prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>" +
									"prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
									"prefix mind: <"+urlProcessDoc+"#>" +
									"delete {?x ?y ?z}" +
									"where" +
									"{?x ?y "+wsdlMsg +
									" filter ( ?y = grounding:wsdlMessagePart " +
									")" +
									"?x ?y ?z}";

					// Execute the query 
					QuerySolution querysol=new QuerySolutionMap();
					UpdateAction.parseExecute(updateWSDLMessageMap, m, querysol);

				}// end if 
			}// end for
		}// end if
	}// DeleteWSDLMessagePart


	/**
	 * DeleteWSDLPortType
	 * @param urlProcessDoc
	 * @param WsdlURL
	 * @param m
	 * @return
	 */
	private void DeleteWSDLPortType(String urlProcessDoc, String WsdlURL, OntModel m){


		// Query to get the service tuples related with the property grounding:portType 
		String queryStringWSDLPort =
				"prefix xsd: <http://www.w3.org/2001/XMLSchema#>"
						+ "prefix service: <http://www.daml.org/services/owl-s/1.1/Service.owl#>"
						+ "prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>"
						+ "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>"
						+ "prefix grounding: <http://www.daml.org/services/owl-s/1.1/Grounding.owl#>" 
						+ "prefix actor: <http://www.daml.org/services/owl-s/1.1/ActorDefault.owl#>"
						+ "prefix mind: <"+urlProcessDoc+"#>" 
						+ "select ?y " 
						+ "where {" 
						+ "?x grounding:portType ?y " 
						+ "}";

		Query queryWSDLPort = QueryFactory.create(queryStringWSDLPort);

		// Execute the query 
		QueryExecution qeWSDLPort = QueryExecutionFactory.create(queryWSDLPort, m);
		ResultSet resultsqeWSDLPort = qeWSDLPort.execSelect();
		WsdlURL=WsdlURL.replace(" ", "");

		// For each input, all the tuples with the property grounding:portType related with the service are deleted
		if (resultsqeWSDLPort != null) {

			while (resultsqeWSDLPort.hasNext()) {

				// To take only the url associated with the property grounding:portType
				String result = resultsqeWSDLPort.next().toString();
				// If the url contains the url of the service, this tuple should be delete
				if(result.contains((CharSequence)WsdlURL)){
					StringTokenizer Tok = new StringTokenizer(result);
					String wsdlPort = Tok.nextToken("=");
					wsdlPort = Tok.nextToken();
					wsdlPort = wsdlPort.replace(")", "");

					System.out.println("wsdlPort : "+wsdlPort);
					//Delete the tuple 
					String updateWSDLPort= 
							"prefix xsd: <http://www.w3.org/2001/XMLSchema#>" +
									"prefix service: <http://www.daml.org/services/owl-s/1.1/Service.owl#>" +
									"prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>" +  
									"prefix grounding: <http://www.daml.org/services/owl-s/1.1/Grounding.owl#>" +
									"prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>" +
									"prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
									"prefix mind: <"+urlProcessDoc+"#>" +
									"delete {?x ?y ?z}" +
									"where" +
									"{?x ?y "+wsdlPort +
									" filter ( ?y = grounding:portType " +
									")" +
									"?x ?y ?z}";

					// Execute the query 
					QuerySolution querysol=new QuerySolutionMap();
					UpdateAction.parseExecute(updateWSDLPort, m, querysol);

				}// end if 
			}// end for
		}// end if
	}// DeleteWSDLPortType



	/**
	 * DeleteWSDLOperation
	 * @param urlProcessDoc
	 * @param WsdlURL
	 * @param m
	 * @return
	 */
	private void DeleteWSDLOperation(String urlProcessDoc, String WsdlURL, OntModel m){


		// Query to get the service tuples related with the property grounding:operation
		String queryStringWSDLOp =
				"prefix xsd: <http://www.w3.org/2001/XMLSchema#>"
						+ "prefix service: <http://www.daml.org/services/owl-s/1.1/Service.owl#>"
						+ "prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>"
						+ "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>"
						+ "prefix grounding: <http://www.daml.org/services/owl-s/1.1/Grounding.owl#>" 
						+ "prefix actor: <http://www.daml.org/services/owl-s/1.1/ActorDefault.owl#>"
						+ "prefix mind: <"+urlProcessDoc+"#>" 
						+ "select ?y " 
						+ "where {" 
						+ "?x grounding:operation ?y " 
						+ "}";

		Query queryWSDLOp = QueryFactory.create(queryStringWSDLOp);

		// Execute the query 
		QueryExecution qeWSDLOp = QueryExecutionFactory.create(queryWSDLOp, m);
		ResultSet resultsqeWSDLOp = qeWSDLOp.execSelect();
		WsdlURL=WsdlURL.replace(" ", "");

		// For each tuple, the tuples with the property grounding:operation related 
		// with the service grounding are deleted
		if (resultsqeWSDLOp != null) {

			while (resultsqeWSDLOp.hasNext()) {

				//To take only the url of the property grounding:operation
				String result = resultsqeWSDLOp.next().toString();
				//Delete the tuple with the url that contains the service url 
				if(result.contains((CharSequence)WsdlURL)){
					StringTokenizer Tok = new StringTokenizer(result);
					String wsdlOp = Tok.nextToken("=");
					wsdlOp = Tok.nextToken();
					wsdlOp = wsdlOp.replace(")", "");
					System.out.println("wsdlOP : "+wsdlOp);

					//Delete the tuple with the url that contains the service url 
					String updateWSDLOp= 
							"prefix xsd: <http://www.w3.org/2001/XMLSchema#>" +
									"prefix service: <http://www.daml.org/services/owl-s/1.1/Service.owl#>" +
									"prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>" +  
									"prefix grounding: <http://www.daml.org/services/owl-s/1.1/Grounding.owl#>" +
									"prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>" +
									"prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
									"prefix mind: <"+urlProcessDoc+"#>" +
									"delete {?x ?y ?z}" +
									"where" +
									"{?x ?y "+wsdlOp +
									" filter ( ?y = grounding:operation " +
									")" +
									"?x ?y ?z}";

					// Execute the query 
					QuerySolution querysol=new QuerySolutionMap();
					UpdateAction.parseExecute(updateWSDLOp, m, querysol);

				}// end if 
			}//end for 
		}// end if 
	}// DeleteWSDLOperation


	/**
	 * DeleteProcessInputs
	 * @param urlProcessDoc
	 * @param processname
	 * @param WsdlURL
	 * @param m
	 * @return
	 */
	private void DeleteProcessInputs(String urlProcessDoc, String processname, String WsdlURL, OntModel m) {

		// Query to get the service inputs tuples related with the service process
		String queryStringServiceInputs =
				"prefix xsd: <http://www.w3.org/2001/XMLSchema#>"
						+ "prefix service: <http://www.daml.org/services/owl-s/1.1/Service.owl#>"
						+ "prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>"
						+ "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>"
						+ "prefix actor: <http://www.daml.org/services/owl-s/1.1/ActorDefault.owl#>"
						+ "prefix mind: <"+urlProcessDoc+"#>" 
						+ "select ?x " 
						+ "where {" 
						+ "mind:"+processname+" process:hasInput ?x " 
						+ "}";

		Query queryServiceInputs = QueryFactory.create(queryStringServiceInputs);

		// Execute the query and obtain the service process inputs
		QueryExecution qeServiceInputs = QueryExecutionFactory.create(queryServiceInputs, m);
		ResultSet resultsServiceInputs = qeServiceInputs.execSelect();
		// For each input, all the tuples related with the process are deleted
		if (resultsServiceInputs != null) {
			while (resultsServiceInputs.hasNext()) {
				//To take only the name of the input
				//tring result = resultsServiceInputs.next().toString();
				String result = resultsServiceInputs.next().getResource("x").toString();
				String processInput=result;
				StringTokenizer Tok = new StringTokenizer(result);
				//String processInputResult = Tok.nextToken("#");
				Tok.nextToken("#");
				String processInputName = Tok.nextToken();
				processInputName = processInputName.replace(">", "");
				processInputName = processInputName.replace(")", "");
				if (DEBUG) {
					System.out.println("Process Input: "+processInput);
					System.out.println("Process Input Name: " + processInputName);
				}

				// Delete input tuples related with the service process
				String updateInput= 
						"prefix xsd: <http://www.w3.org/2001/XMLSchema#>" +
								"prefix service: <http://www.daml.org/services/owl-s/1.1/Service.owl#>" +
								"prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>" +  
								"prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>" +
								"prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
								"prefix mind: <"+urlProcessDoc+"#>" +
								"delete {?x ?y ?z}" +
								"where" +
								"{mind:" +processInputName+" ?y ?z" +
								" filter ( ?y = rdfs:label " +
								"|| ?y = process:parameterType " +
								"|| ?z = process:Input " +
								")" +
								"?x ?y ?z}";


				//Delete the output tuple related with the service grounding
				String updateGroundInput= 
						"prefix xsd: <http://www.w3.org/2001/XMLSchema#>" +
								"prefix service: <http://www.daml.org/services/owl-s/1.1/Service.owl#>" +
								"prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>" +  
								"prefix grounding: <http://www.daml.org/services/owl-s/1.1/Grounding.owl#>" +
								"prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>" +
								"prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
								"prefix mind: <"+urlProcessDoc+"#>" +
								"delete {?x ?y ?z}" +
								"where" +
								"{?x ?y mind:"+processInputName +
								" filter ( ?y = grounding:owlsParameter " +
								")" +
								"?x ?y ?z}";
				
				

				// Execute the query 
				QuerySolution querysol=new QuerySolutionMap();
				UpdateAction.parseExecute(updateInput, m, querysol);
				UpdateAction.parseExecute(updateGroundInput, m, querysol);

			}//end for 
		}//end if	

	}// end Delete inputs


	/**
	 * DeleteProcessOutputs
	 * @param urlProcessDoc
	 * @param processname
	 * @param WsdlURL
	 * @param m
	 * @return
	 */
	private void DeleteProcessOutputs(String urlProcessDoc, String processname, String WsdlURL, OntModel m) {

		// Query to get the service outputs related with the process
		String queryStringServiceOutputs =
				"prefix xsd: <http://www.w3.org/2001/XMLSchema#>"
						+ "prefix service: <http://www.daml.org/services/owl-s/1.1/Service.owl#>"
						+ "prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>"
						+ "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>"
						+ "prefix actor: <http://www.daml.org/services/owl-s/1.1/ActorDefault.owl#>"
						+ "prefix mind: <"+urlProcessDoc+"#>" 
						+ "select ?x " 
						+ "where {" 
						+ "mind:"+processname+" process:hasOutput ?x " 
						+ "}";

		Query queryServiceOutputs = QueryFactory.create(queryStringServiceOutputs);

		// Execute the query 
		QueryExecution qeServiceOutputs = QueryExecutionFactory.create(queryServiceOutputs, m);
		ResultSet resultsServiceOutputs = qeServiceOutputs.execSelect();
		// For each service output, all the tuples related with the process are deleted 
		if (resultsServiceOutputs != null) {
			while (resultsServiceOutputs.hasNext()) {
				// To take the name of the output
				String result = resultsServiceOutputs.next().toString();
				StringTokenizer Tok = new StringTokenizer(result);
				//String processOutputResult = Tok.nextToken("#");
				Tok.nextToken("#");
				String processOutputName = Tok.nextToken();
				processOutputName = processOutputName.replace(">", "");
				processOutputName = processOutputName.replace(")", "");

				if (DEBUG) {
					System.out.println("Process Output: " + processOutputName);
				}

				// Delete the output tuple related with the service process
				String updateOutput= 
						"prefix xsd: <http://www.w3.org/2001/XMLSchema#>" +
								"prefix service: <http://www.daml.org/services/owl-s/1.1/Service.owl#>" +
								"prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>" +  
								"prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>" +
								"prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
								"prefix mind: <"+urlProcessDoc+"#>" +
								"delete {?x ?y ?z}" +
								"where" +
								"{mind:" +processOutputName+" ?y ?z" +
								" filter ( ?y = rdfs:label " +
								"|| ?y = process:parameterType " +
								"|| ?z = process:Output " +
								")" +
								"?x ?y ?z}";


				//Delete the output tuple related with the service grounding
				String updateGroundOutput= 
						"prefix xsd: <http://www.w3.org/2001/XMLSchema#>" +
								"prefix service: <http://www.daml.org/services/owl-s/1.1/Service.owl#>" +
								"prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>" +  
								"prefix grounding: <http://www.daml.org/services/owl-s/1.1/Grounding.owl#>" +
								"prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>" +
								"prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
								"prefix mind: <"+urlProcessDoc+"#>" +
								"delete {?x ?y ?z}" +
								"where" +
								"{?x ?y mind:"+processOutputName +
								" filter ( ?y = grounding:owlsParameter " +
								")" +
								"?x ?y ?z}";


				// Execute the query 
				QuerySolution querysol=new QuerySolutionMap();
				UpdateAction.parseExecute(updateOutput, m, querysol);
				UpdateAction.parseExecute(updateGroundOutput, m, querysol);


			}//end for 
		}//end if	
	}//end DeleteProcessOutputs


	/**
	 * DeleteProcessGrounding
	 * @param String urlProcessDoc
	 * @param String processname
	 * @param String processGround
	 * @param String processGroundWSDL
	 * @param OntModel m
	 */ 
	private void DeleteProcessGrounding(String urlProcessDoc, String processname, String processGround, String processGroundWSDL, OntModel m){

		//Deletes Query Strings
		String updateGround= 
				"prefix xsd: <http://www.w3.org/2001/XMLSchema#>" +
						"prefix service: <http://www.daml.org/services/owl-s/1.1/Service.owl#>" +
						"prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>" +  
						"prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>" +
						"prefix grounding: <http://www.daml.org/services/owl-s/1.1/Grounding.owl#>" +
						"prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
						"prefix mind: <"+urlProcessDoc+"#>" +
						"delete {?x ?y ?z .}" +
						"where" +
						"{"+processGround+" ?y ?z ." +
						" filter ( ?y = grounding:owlsProcess" + 
						"|| ?y = grounding:hasAtomicProcessGrounding " +
						"|| ?y = service:supportedBy " +
						"|| ?z = grounding:WsdlGrounding " +
						")" +
						"?x ?y ?z .}";


		String updateGroundWSDL= 
				"prefix xsd: <http://www.w3.org/2001/XMLSchema#>" +
						"prefix service: <http://www.daml.org/services/owl-s/1.1/Service.owl#>" +
						"prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>" +  
						"prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>" +
						"prefix grounding: <http://www.daml.org/services/owl-s/1.1/Grounding.owl#>" +
						"prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
						"prefix mind: <"+urlProcessDoc+"#>" +
						"delete {?x ?y ?z .}" +
						"where" +
						"{"+processGroundWSDL+" ?y ?z ." +
						" filter ( ?y = grounding:owlsProcess" + 
						"|| ?y = grounding:wsdlDocument " +
						"|| ?y = grounding:wsdlInput " +
						"|| ?y = grounding:wsdlInputMessage " +
						"|| ?y = grounding:wsdlOperation " +
						"|| ?y = grounding:wsdlOutput " + 
						"|| ?y = grounding:wsdlOutputMessage "+
						"|| ?y = grounding:xsltTransformationString " +
						"|| ?z = grounding:WsdlInputMessageMap " +
						"|| ?z = grounding:WsdlOutputMessageMap " +
						"|| ?z = grounding:WsdlAtomicProcessGrounding "+
						")" +
						"?x ?y ?z .} ";


		String updateGroundSupportsProperty= 
				"prefix xsd: <http://www.w3.org/2001/XMLSchema#>" +
						"prefix service: <http://www.daml.org/services/owl-s/1.1/Service.owl#>" +
						"prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>" +  
						"prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>" +
						"prefix grounding: <http://www.daml.org/services/owl-s/1.1/Grounding.owl#>" +
						"prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
						"prefix mind: <"+urlProcessDoc+"#>" +
						"delete {?x ?y ?z}" +
						"where" +
						"{?x ?y " + processGround +
						" filter ( ?y = service:supports" +
						")" +
						"?x ?y ?z}";

		// Execute the deletes
		QuerySolution querysol=new QuerySolutionMap();
		UpdateAction.parseExecute(updateGroundWSDL, m, querysol);
		UpdateAction.parseExecute(updateGround, m, querysol);
		UpdateAction.parseExecute(updateGroundSupportsProperty, m, querysol);


	}//end DeleteProcessGrounding



	/**
	 * DeleteProcess
	 * @param String urlProcessDoc
	 * @param String processname
	 * @param OntModel m
	 */ 
	private void DeleteProcess(String urlProcessDoc, String processname, OntModel m) {

		//Delete the process general description
		String updateProcess= 
				"prefix xsd: <http://www.w3.org/2001/XMLSchema#>" +
						"prefix service: <http://www.daml.org/services/owl-s/1.1/Service.owl#>" +
						"prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>" +  
						"prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>" +
						"prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
						"prefix mind: <"+urlProcessDoc+"#>" +
						"delete {?x ?y ?z}" +
						"where" +
						"{mind:" +processname+" ?y ?z" +
						" filter ( ?y = process:hasOutput " +
						"|| ?y = process:hasInput " +
						"|| ?y = service:describes " +
						"|| ?y = process:hasPrecondition " +
						"|| ?z = process:AtomicProcess " +
						")" +
						"?x ?y ?z}";

		//Delete the profile property where the process appears
		String updateDescribedByProperty= 
				"prefix xsd: <http://www.w3.org/2001/XMLSchema#>" +
						"prefix service: <http://www.daml.org/services/owl-s/1.1/Service.owl#>" +
						"prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>" +  
						"prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>" +
						"prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
						"prefix mind: <"+urlProcessDoc+"#>" +
						"delete {?x ?y ?z}" +
						"where" +
						"{?x ?y mind:" +processname +
						" filter ( ?y = service:describedBy " +
						")" +
						"?x ?y ?z}";

		// Execute the query 
		QuerySolution querysol=new QuerySolutionMap();
		UpdateAction.parseExecute(updateProcess, m, querysol);
		UpdateAction.parseExecute(updateDescribedByProperty, m, querysol);

	}// DeleteProcess 

	/**
	 * Opens a Jena DB connection taking the configuration parameters from THOMASDemoConfiguration.xml file
	 * @return {@link IDBConnection} to Jena DB
	 */
	private IDBConnection JenaDBConnection(){
		IDBConnection conn = null;

		String s_dbURL="";
		String s_dbUser="";
		String s_dbPw="";
		String s_dbType="";
		String s_dbDriver="";

		Properties properties = new Properties();

		try {

			FileInputStream fis = new FileInputStream("THOMASDemoConfiguration.xml");
			//properties.loadFromXML(ProvaServeis1.class.getResourceAsStream("THOMASDemoConfiguration.xml"));
			properties.loadFromXML(fis);
			for (Enumeration<Object>  e = properties.keys(); e.hasMoreElements() ; ) {
				// Obtain the object
				Object obj = e.nextElement();
				if (obj.toString().equalsIgnoreCase("DB_URL"))
				{
					s_dbURL= properties.getProperty(obj.toString());	
				}
				else if (obj.toString().equalsIgnoreCase("DB_USER"))
				{
					s_dbUser = properties.getProperty(obj.toString());
				}
				else    if (obj.toString().equalsIgnoreCase("DB_PASSWD"))
				{
					s_dbPw = properties.getProperty(obj.toString());
				}
				else    if (obj.toString().equalsIgnoreCase("DB"))
				{
					s_dbType = properties.getProperty(obj.toString());
				}
				else    if (obj.toString().equalsIgnoreCase("DB_DRIVER"))
				{
					s_dbDriver = properties.getProperty(obj.toString());
				}
			}

		} catch (IOException e) {
			System.out.print(e);
		}

		// ensure the JDBC driver class is loaded
		try {
			s_dbDriver= "com.mysql.jdbc.Driver";
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
			conn = null;
		}
		return conn;
	}

	/**
	 * Makes a clean to the connection of Jena DB. This deletes all the information in the Jena DB.
	 */
	public void clean(){
		try {
			conn.cleanDB();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Returns an {@link OntModelSpec} of Jena DB.
	 * @param maker {@link ModelMaker} to create an {@link OntModelSpec}
	 * @return an {@link OntModelSpec} of Jena DB
	 */
	private OntModelSpec getModelSpec(ModelMaker maker) {
		// create a spec for the new ont model that will use no inference over
		// models made by the given maker (which is where we get the persistent models from)
		OntModelSpec spec = new OntModelSpec(OntModelSpec.OWL_MEM);
		spec.setImportModelMaker(maker);

		return spec;
	}

	/**
	 * Writes to the standard output the Jena DB Model
	 */
	public void writeModel(){
		ModelMaker maker = ModelFactory.createModelRDBMaker(conn);
		Model base = maker.createModel("http://example.org/ontologias");
		OntModel m = ModelFactory.createOntologyModel(getModelSpec(maker), base);

		m.write(System.out, "N3");
	}

}