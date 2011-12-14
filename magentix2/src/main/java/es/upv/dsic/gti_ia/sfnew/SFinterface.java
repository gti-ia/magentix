package es.upv.dsic.gti_ia.sfnew;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
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

public class SFinterface {
	
	private static boolean DEBUG = true;
	
	private IDBConnection conn;
	
	public SFinterface(){
		super();
		conn=JenaDBConnection();
	}
	
	
	public void testQuery(String serviceURL){
//		ArrayList<String> inputs=new ArrayList<String>();
//		inputs.add("\"http://127.0.0.1/ontology/geographydataset.owl#Code\"^^xsd:anyURI");
//		inputs.add("\"http://127.0.0.1/ontology/protont.owl#longitude\"^^xsd:anyURI");
//		inputs.add("\"http://127.0.0.1/ontology/protonu.owl#Date\"^^xsd:anyURI");
//		inputs.add("\"http://127.0.0.1/ontology/protont.owl#latitude\"^^xsd:anyURI");
//		ArrayList<String> outputs=new ArrayList<String>();
//		outputs.add("\"http://127.0.0.1/ontology/geographydataset.owl#Sunrise\"^^xsd:anyURI");
//		String reg=searchRegisteredServices(inputs, outputs);
//		System.out.println("Registered: "+reg);
		
		
		
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

		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}

		//Close the model 
		m.close();

		
		
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
					//System.out.println("input param="+param+"\n\t cand="+cand);
					if(!candN.contains(cand))
						candN.add(cand);
					
				}//end for 
			}//end if
			
			// close the query
//			querySearchInputs.close();
			
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
					//System.out.println("output param="+param+"\n\t cand="+cand);
					if(!candN.contains(cand))
						candN.add(cand);
					
				}//end for 
			}//end if
			
			// close the query
//			querySearchOutputs.close();
			
			if(!candidates.isEmpty()){
				candidates.retainAll(candN);
				
			}
			
		}
		
		//m.close();
	
		if(candidates.isEmpty()){
			return "";
		}
		else{//check if the inputs and outputs are the same exactly 
			//(could arrive here having a service with more inputs 
			//than the specified as parameter and will be different services) 
			
			
			//TODO bad use, create another getInputs getOutputs without using the URI, using the Jena DB
			ArrayList<String> inputsCandidate=getInputs(candidates.get(0));
			ArrayList<String> outputsCandidate=getOutputs(candidates.get(0));
			
			ArrayList<String> inputParamTypeCand=getInputParameterTypes(inputsCandidate);
			ArrayList<String> outputParamTypeCand=getOutputParameterTypes(outputsCandidate);
			
			//comparar completamente (que sean los mismos y el mismo número) los parametros del candidato y los dados
			
			if(inputParamTypeCand.size()!=inputs.size())
				return "";
			if(outputParamTypeCand.size()!=outputs.size())
				return "";
			
			Iterator<String> iterInputCand=inputParamTypeCand.iterator();
			while(iterInputCand.hasNext()){
				String inCand=iterInputCand.next();
				boolean found=false;
				//System.out.println("inCand="+inCand);
				Iterator<String> iterInput=inputs.iterator();
				while(iterInput.hasNext()){
					String in=iterInput.next();
					//System.out.println("\tin="+in);
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
				//System.out.println("outCand="+outCand);
				Iterator<String> iterOutput=outputs.iterator();
				while(iterOutput.hasNext()){
					String out=iterOutput.next();
					//System.out.println("\tout="+out);
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
//				System.out.println(res);
				
			} 
			
		}//end if
		else{
			System.out.println("resultsSearchName is null");
		}
		
		// close the query
		qeSearchName.close();
		//m.close();
		
		return res; 
	}
	
	/**
	 * Returns the inputs of the given service profile
	 * @param serviceProfile
	 * @return the inputs of the given service profile
	 */
	private ArrayList<String> getInputs(String serviceURL){
		ArrayList<String> inputs=new ArrayList<String>();
		
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
				"prefix xsd: <http://www.w3.org/2001/XMLSchema#>"+
				"prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>"+
				"prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>"+
				"select ?x where { ?y profile:hasInput ?x }";
		
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
	
	private ArrayList<String> getInputParameterTypes(ArrayList<String> inputs){
		ArrayList<String> inputParamsRegistered=new ArrayList<String>();
		
		ModelMaker maker = ModelFactory.createModelRDBMaker(conn);
		Model base = maker.createModel("http://example.org/ontologias");
		OntModel m = ModelFactory.createOntologyModel(getModelSpec(maker), base);
		
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
	 * Returns the specification in OWL-S of the given input
	 * @param input
	 * @return the specification in OWL-S of the given input
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
	 * @param serviceProfile
	 * @return the outputs of the given service profile
	 */
	private ArrayList<String> getOutputs(String serviceURL){
		ArrayList<String> outputs=new ArrayList<String>();
		
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
				"prefix xsd: <http://www.w3.org/2001/XMLSchema#>"+
				"prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>"+
				"prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>"+
				"select ?x where { ?y profile:hasOutput ?x }";
		
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
	
	private ArrayList<String> getOutputParameterTypes(ArrayList<String> outputs){
		ArrayList<String> outputParamsRegistered=new ArrayList<String>();
		
		ModelMaker maker = ModelFactory.createModelRDBMaker(conn);
		Model base = maker.createModel("http://example.org/ontologias");
		OntModel m = ModelFactory.createOntologyModel(getModelSpec(maker), base);
		
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
					//System.out.println(param);
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
	 * @param output
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
	
	
	//TODO detectar si entra un process y grounding nuevos para registrarlos igualmente, pero solo esa parte... el profile de nuevo no!
	public void RegisterService(String serviceURL){
		
		 
		
		ArrayList<String> inputs=getInputs(serviceURL);
		ArrayList<String> outputs=getOutputs(serviceURL);
		
		ArrayList<String> inputsParams=getInputParameterTypes(inputs);
		ArrayList<String> outputParams=getOutputParameterTypes(outputs);
		
		String regService=searchRegisteredServices(inputsParams, outputParams);
		if(!regService.equalsIgnoreCase("")){
			System.out.println("Service already registered: "+regService);
			
			
			
			//TODO mirar si se tiene que registrar algun provider
			
			
			//obtain the registered wsdl docs and the one that has to be registered. 
			//If the new wsdl doc is different, it is registered
			
			String wsdlToRegister=getWSDLDocumentFromServiceURL(serviceURL);
			
			String serviceURI=getServiceURI(regService);
			ArrayList<String> groundings=getGroundings(serviceURI);
			Iterator<String> iterGrounds=groundings.iterator();
			boolean found=false;
			while(iterGrounds.hasNext()){
				String groundingURI=iterGrounds.next();
				String atomicProcessGrounding=getAtomicProcessGrounding(groundingURI);
				String WSDLDocandDatatype=getWSDLDocument(atomicProcessGrounding);
				if(WSDLDocandDatatype.equalsIgnoreCase(wsdlToRegister)){
					found=true;
					break;
				}
			}
			if(!found){//register the grounding
				System.out.println("Register the grounding");
				
				
				//1) crear un nuevo owls y meterlo en una url del propio servidor local 
				//funciona, pero ojo con las URLs!!
				
				//2) cargar el owls con Jena, y extraer uno a uno los datos que se quieren,
				//ir añadiendolos al modelo mediante funciones m.add ... (esto se tiene que probar...)
			}
			else{
				System.out.println("Not register any grounding");
			}
			
			
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

			try {
				//conn.close();
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}

			//Close the model 
			m.close();

			
			System.out.println("Service registered: "+serviceURL);
		}
		
		
		
	}
	
	/**
	 * Returns the profile service name 
	 * @param serviceProfile
	 * @return the profile service name 
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
	 * Returns the profile text description
	 * @param serviceProfile
	 * @return the profile text description
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
	 * @param serviceProfile
	 * @return the service URI
	 */
	private String getServiceURI(String serviceProfile){
		String serviceURI="";
		
		ModelMaker maker = ModelFactory.createModelRDBMaker(conn);
		Model base = maker.createModel("http://example.org/ontologias");
		OntModel m = ModelFactory.createOntologyModel(getModelSpec(maker), base);
		
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
	 * Returns an {@link ArrayList} with the grounding URIs of the given service URI 
	 * @param serviceURI
	 * @return an {@link ArrayList} with the grounding URIs of the given service URI
	 */
	private ArrayList<String> getGroundings(String serviceURI){
		ArrayList<String> groundings=new ArrayList<String>();
		
		ModelMaker maker = ModelFactory.createModelRDBMaker(conn);
		Model base = maker.createModel("http://example.org/ontologias");
		OntModel m = ModelFactory.createOntologyModel(getModelSpec(maker), base);
		
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
	 * @param groundingURI
	 * @return the atomic process grounding URI of the given grounding URI
	 */
	private String getAtomicProcessGrounding(String groundingURI){
		String atomicProcessGrounding="";
		
		ModelMaker maker = ModelFactory.createModelRDBMaker(conn);
		Model base = maker.createModel("http://example.org/ontologias");
		OntModel m = ModelFactory.createOntologyModel(getModelSpec(maker), base);
		
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
	 * @param atomicProcessGrounding
	 * @return the WSDL document URI of the given atomic process grounding URI
	 */
	private String getWSDLDocument(String atomicProcessGrounding){
		String WSDLDoc="";
		
		ModelMaker maker = ModelFactory.createModelRDBMaker(conn);
		Model base = maker.createModel("http://example.org/ontologias");
		OntModel m = ModelFactory.createOntologyModel(getModelSpec(maker), base);
		
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
	
	
	private String getWSDLDocumentFromServiceURL(String serviceURL){
		String WSDLDoc="";
		
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
				WSDLDoc=resultsSearchName.next().getLiteral("x").toString();
				//System.out.println("wsdldoc="+WSDLDoc);
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
	 * Returns a String containing the specification in OWL-S of all groundings of the given service profile
	 * @param serviceProfile
	 * @return a String containing the specification in OWL-S of all groundings of the given service profile
	 */
	private String getGroundingsOWLS(String serviceProfile){
		String groundsOWLS="";
		String serviceURI=getServiceURI(serviceProfile);
		ArrayList<String> groundings=getGroundings(serviceURI);
		Iterator<String> iterGrounds=groundings.iterator();
		while(iterGrounds.hasNext()){
			String groundingURI=iterGrounds.next();
			String atomicProcessGrounding=getAtomicProcessGrounding(groundingURI);
			String WSDLDocandDatatype=getWSDLDocument(atomicProcessGrounding);
			StringTokenizer token=new StringTokenizer(WSDLDocandDatatype,"^^");
			String wsdlDoc=token.nextToken();
			String wsdlDatatype=token.nextToken();
			groundsOWLS+="<grounding:WsdlGrounding rdf:ID=\""+groundingURI+"\">\n"+
			"<service:supportedBy rdf:resource=\""+serviceURI+"\"/> \n"+
			"<grounding:hasAtomicProcessGrounding>\n"+
			"<grounding:WsdlAtomicProcessGrounding rdf:ID=\""+atomicProcessGrounding+"\"/>"+
			"</grounding:hasAtomicProcessGrounding>\n"+
			"</grounding:WsdlGrounding>\n";
			
			groundsOWLS+="<grounding:WsdlAtomicProcessGrounding rdf:about=\""+atomicProcessGrounding+"\">\n"+
			"<grounding:wsdlDocument rdf:datatype=\""+wsdlDatatype+"\">\n"+
			wsdlDoc+"</grounding:wsdlDocument>\n"+
			"</grounding:WsdlAtomicProcessGrounding>\n";
		}
		
		return groundsOWLS;
	}
	
	/**
	 * Implementation of the SF service Get Service. It returns an OWL-S specification with the information of the given service.
	 * @param serviceProfile
	 * @return an OWL-S specification with the information of the given service.
	 */
	public String getService(String serviceProfile){
		String owlsService="";
		
		//headers
		owlsService+="<?xml version=\"1.0\" encoding=\"WINDOWS-1252\"?>"+"\n"+
		"<rdf:RDF  xmlns:owl       = \"http://www.w3.org/2002/07/owl#\""+"\n"+
		"xmlns:rdfs      = \"http://www.w3.org/2000/01/rdf-schema#\""+"\n"+
		"xmlns:rdf       = \"http://www.w3.org/1999/02/22-rdf-syntax-ns#\""+"\n"+
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
		//+
		//<http://127.0.0.1/services/1.1/calculateSunriseTime.owls#Provider2>
        //a       provider:Provider
		owlsService+=getProvidersOWLS(serviceProfile);
		
		//inputs and outputs of a process with their types
		String processInputs="", processOutputs="";
		//TODO bad use of getInputs...
		ArrayList<String> inputs=getInputs(serviceProfile);
		Iterator<String> iterInputs=inputs.iterator();
		while(iterInputs.hasNext()){
			//profile:hasInput
			String in=iterInputs.next();
			owlsService+="<profile:hasInput rdf:resource=\""+in+ "\"/>"+"\n";
			processInputs+=getInputOWLS(in);
			
			
		}
		//TODO bad use of getOutputs...
		ArrayList<String> outputs=getOutputs(serviceProfile);
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
	
	//TODO
	public void deregisterService(String profileURL, String profileName, String serviceName){
		
		ModelMaker maker = ModelFactory.createModelRDBMaker(conn);
		Model base = maker.createModel("http://example.org/ontologias");
		OntModel m = ModelFactory.createOntologyModel(getModelSpec(maker), base);
		
		profileURL="http://127.0.0.1/services/1.1/calculateSunriseTime.owls";
		profileName="CALCULATE_SUNRISE_PROFILE";
		serviceName="CALCULATE_SUNRISE_SERVICE";
		
		String serviceprocess="http://127.0.0.1/services/1.1/calculateSunriseTime.owls#CALCULATE_SUNRISE_PROCESS";
		String urlProfileService="http://127.0.0.1/services/1.1/calculateSunriseTime.owls#CALCULATE_SUNRISE_PROFILE";
		
		StringTokenizer Tok = new StringTokenizer(serviceprocess);
		// Get the owl process document
		String urlProcessDoc = Tok.nextToken("#");
		// Get the name of the process
		String processName = Tok.nextToken();

		System.out.println("urlProcessDoc " + urlProcessDoc);
		System.out.println("processname " + processName);

		// Get the url profile # service name
//		m.write(System.out, "N3");
		String urlProcessService = GetServiceProcess(urlProcessDoc,processName, m);
		if(urlProcessService==null){
			System.err.println("urlProcessService is null, cannot deregister service "+profileName);
			return;
		}
			
		if (DEBUG) {
			System.out.println("URL process: " + urlProcessDoc);
			System.out.println("Process name: " + processName);
			System.out.println("URL profile#service: " + urlProfileService);
			System.out.println("URL process#service: " + urlProcessService);
			
			// System.out.println("Provider ID: "+ providerName);
		}
		
		// Query to get the service profile name
		String queryStringServiceProfileName = "prefix xsd: <http://www.w3.org/2001/XMLSchema#>"
				+ "prefix service: <http://www.daml.org/services/owl-s/1.1/Service.owl#>"
				+ "prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>"
				+ "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>"
				+ "select ?x "
				+ "where {"
				+ urlProcessService
				+ " service:presents ?x" + "}";

		Query queryServiceProfileName = QueryFactory.create(queryStringServiceProfileName);

		// Execute the query
		QueryExecution qeServiceProfileName = QueryExecutionFactory.create(	queryServiceProfileName, m);
		ResultSet resultsServiceProfileName = qeServiceProfileName.execSelect();

		// To get the url profile # profile name
		String result = resultsServiceProfileName.next().toString();
		Tok = new StringTokenizer(result);
		String ServiceProfileResult = Tok.nextToken("=");
		String Profile = Tok.nextToken();
		Profile = Profile.replace(")", "");

		// RemoveProvider(urlProcessDoc, providerName, processName, m);
		RemoveProcess(urlProcessDoc, processName, urlProfileService,urlProcessService,Profile, m);

		m.commit();
		
		
		
		
		
		// Delete profile tuples where the property is profile
		String update =
				"prefix xsd: <http://www.w3.org/2001/XMLSchema#>"
						+ "prefix service: <http://www.daml.org/services/owl-s/1.1/Service.owl#>"
						+ "prefix proc: <http://www.daml.org/services/owl-s/1.1/Process.owl#>"
						+ "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>"
						+ "prefix mind: <" + profileURL + "#>" +

						"delete {?x ?y ?z}" + "where" + "{" + "mind:"
						+ profileName + " ?y ?z"
						+ " filter ( ?y = profile:hasInput "
						+ "|| ?y = profile:hasOutput "
						+ "|| ?y = profile:serviceName "
						+ "|| ?y = profile:textDescription "
						+ "|| ?y = profile:has_process "
						+ "|| ?y = service:isPresentedBy "
						+ "|| ?y = service:presents "
						+ "|| ?z = profile:Profile " + ")" + "?x ?y ?z}";
		
		String update2 =
			"prefix xsd: <http://www.w3.org/2001/XMLSchema#>"
					+ "prefix service: <http://www.daml.org/services/owl-s/1.1/Service.owl#>"
					+ "prefix proc: <http://www.daml.org/services/owl-s/1.1/Process.owl#>"
					+ "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>"
					+ "prefix mind: <" + profileURL + "#>" +

					"delete {?x ?y ?z}" + "where" + "{" + "mind:"
					+ serviceName + " ?y ?z"
					+ " filter ( ?z = service:Service "
					+ "|| ?y = service:presents " + ")"
					+ "?x ?y ?z}";
		
		// Execute the query and obtain results
		QuerySolution querysol = new QuerySolutionMap();
		UpdateAction.parseExecute(update, m, querysol);
		UpdateAction.parseExecute(update2, m, querysol);
		
		
		
	}
	
	
	private String getProvidersOWLS(String serviceProfile){
		
		String providersOWLS="";
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
		
		String providerURI="", entityID="", entityType="", language="", performative="";
		
		if (resultsSearchName != null) {
			
			while(resultsSearchName.hasNext()) {
				providerURI = resultsSearchName.next().getResource("x").toString();
				
				
				entityID=getProviderParameter(providerURI, "entityID");
				entityType=getProviderParameter(providerURI, "entityType");
				language=getProviderParameter(providerURI, "language");
				performative=getProviderParameter(providerURI, "performative");
				
				providersOWLS+="<profile:contactInformation>"+"\n"+
				"<provider:Provider rdf:ID=\""+providerURI+"\">"+"\n"+
				"<provider:entityID rdf:datatype=\"^^xsd;string\">"+entityID+"</provider:entityID>"+"\n"+
				"<provider:entityType rdf:datatype=\"^^xsd;string\">"+entityType+"</provider:entityType>"+"\n"+
				"<provider:language rdf:datatype=\"^^xsd;string\">"+language+"</provider:language>"+"\n"+
				"<provider:performative rdf:datatype=\"^^xsd;string\">"+performative+"</provider:performative>"+"\n"+
				"</provider:Provider>"+"\n"+
				"</profile:contactInformation>"+"\n";
				
				//System.out.println(providerURI+"\n"+entityID+" "+entityType+" "+language+" "+performative);
				
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
	
	
	private String getProviderParameter(String providerURI, String parameter){
		
		ModelMaker maker = ModelFactory.createModelRDBMaker(conn);
		Model base = maker.createModel("http://example.org/ontologias");
		OntModel m = ModelFactory.createOntologyModel(getModelSpec(maker), base);
		
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
	
	
	
	
	//TODO idea: fer consulta primer per el process:parameterType per veure si coincideix i despres
		//agafar el profile:hasInput
		//seria més elegant poder agafar directament el parameterType sabent que es Input...
		
		
		
		
	//hacer una consulta por cada entrada deseada, guardando los servicios que tengan al menos una entrada (sin repetirlos en la lista)
	//de cada servicio, comprobar exactamente cuantas entradas coinciden, y después las salidas
	//sumar con algún tipo de ponderación
	/**
	 * 
	 * @param conn
	 * @param inputs  Specify the parameter type. Example: \"http://127.0.0.1/ontology/books.owl#Novel\"^^xsd:anyURI
	 * @param outputs Specify the parameter type. Example: \"http://127.0.0.1/ontology/books.owl#Novel\"^^xsd:anyURI
	 */
	public void SearchServices(ArrayList<String> inputs, ArrayList<String> outputs){
		
		
		ModelMaker maker = ModelFactory.createModelRDBMaker(conn);
		Model base = maker.createModel("http://example.org/ontologias");
		OntModel m = ModelFactory.createOntologyModel(getModelSpec(maker), base);
	
		ArrayList<String> candidates=new ArrayList<String>();
		
		Iterator<String> iterInputs=inputs.iterator();
		while(iterInputs.hasNext()){
			
			
			
			String in=iterInputs.next();
			
			String queryStr =
				"prefix xsd: <http://www.w3.org/2001/XMLSchema#>"+
				"prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>"+
				"prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>"+
				"select ?x where { ?x a process:Input ; process:parameterType "+in+" . }";
			
//				String queryStr =
//				"prefix xsd: <http://www.w3.org/2001/XMLSchema#>"+
//				"prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>"+
//				"prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>"+
//				"select ?x where { ?x a process:Input { ?x process:parameterType "+in+" . } }";
			
//				String queryStr =
//					"prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>"+
//					"prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>"+
//					"select ?x where { ?x process:parameterType ?y }";
			
			Query query = QueryFactory.create(queryStr);

			// Execute the query and obtain results
			QueryExecution querySearchInputs = QueryExecutionFactory.create(query, m);
			ResultSet resultsSearchInputs = querySearchInputs.execSelect();
			
			if (resultsSearchInputs != null) {
				int controws=0;
				
				
				while (resultsSearchInputs.hasNext()) {
					controws++;
					
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
			
//				m.close();
			
		}
		
		
//			System.out.println("****FINAL CANDIDATES:****");
//			Iterator<String> iterCandidates=candidates.iterator();
//			while(iterCandidates.hasNext()){
//				String cand=iterCandidates.next();
//				System.out.println(cand);
//			}
		
		
		
		//TODO guardar una lista de profiles con sus ponderaciones asociadas de semejanza al servicio
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
					
					//TODO mirar todas las entradas (consulta del tipo) buscando si esta está
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
					
					//TODO mirar todas las entradas (consulta del tipo) buscando si esta está
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
			
			float adaptToAsked=profile.getSuitability()/(inputs.size()+outputs.size());
			float adaptToFoundService=profile.getSuitability()/(inputsProfile+outputsProfile);
			profile.setSuitability(0.5f*adaptToAsked+0.5f*adaptToFoundService);
			
			System.out.println(profile.getUrl()+" -> "+profile.getSuitability());
			
		}
		
		
		
		
		
		
		
		
		
	  }
	
	  public int RemoveProcess(String urlProcessDoc, String processname, String urlProfileService, String urlProcessService, String Profile, OntModel m){
		   	 
	 	   if (DEBUG) {
				System.out.println("Removing Process ... ");
		       }
	 	 
	 	   
	 	    // Query to know if there are more process with the same profile
				String queryStringServiceProcess =
					"prefix xsd: <http://www.w3.org/2001/XMLSchema#>"
							+ "prefix service: <http://www.daml.org/services/owl-s/1.1/Service.owl#>"
							+ "prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>"
							+ "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>"
							+ "prefix actor: <http://www.daml.org/services/owl-s/1.1/ActorDefault.owl#>"
							+ "select ?x " 
							+ "where {" 
							+ "?x service:presents "+Profile 
							+ "}";

				Query queryServiceProcess = QueryFactory.create(queryStringServiceProcess);

	  		// Execute the query
				QueryExecution qeServiceProcess = QueryExecutionFactory.create(queryServiceProcess, m);
				ResultSet resultsServiceProcess = qeServiceProcess.execSelect();
				
				int numprocess=0;
				Iterator j = resultsServiceProcess;
				while ( resultsServiceProcess.hasNext()) {
					resultsServiceProcess.next();
					numprocess++;
				}
				
				// If there are not more than one process, the profile should be deleted
				/*if (numprocess==1) {  
					if (DEBUG) {
	       			System.out.println("The profile should be deleted... ");
		 	        }
					DeleteProfile(urlProfileService,Profile, m);
				}*/
				
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
	       		
	 		return(1);
	 	 
	  }// end RemoveProcess
	  
	  
	  /**
	   * GetServiceProfile
	   * @param String urlProcessDoc
	   * @param String processname
	   * @param OntModel m
	   * @return 
	   */ 
	  public String GetServiceProcess(String urlProcessDoc, String processname, OntModel m){
	  	//Query to get the service profile
			String queryStringProfile =
				"prefix xsd: <http://www.w3.org/2001/XMLSchema#>"
						+ "prefix service: <http://www.daml.org/services/owl-s/1.1/Service.owl#>"
						+ "prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>"
						+ "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>"
						+ "prefix actor: <http://www.daml.org/services/owl-s/1.1/ActorDefault.owl#>"
						+ "prefix mind: <"+urlProcessDoc+"#>" 
						+ "select ?x " 
						+ "where {" 
						+ "mind:"+processname+" service:describes ?x " 
						+ "}";

			Query queryProfile = QueryFactory.create(queryStringProfile);

			// Execute the query and obtain results
			QueryExecution qeProfile = QueryExecutionFactory.create(queryProfile, m);
			ResultSet resultProfile = qeProfile.execSelect();
			if(resultProfile.hasNext()){
				// To get the name of the profile
				String result = resultProfile.next().toString();
				StringTokenizer Tok = new StringTokenizer(result);
				String processProfileResult = Tok.nextToken("=");
				String processProfile = Tok.nextToken();
				processProfile = processProfile.replace(")", "");
				if (DEBUG) {
					System.out.println("Process Profile: " + processProfile);
				}
		  	
				return(processProfile);
			}
			else{
				if (DEBUG) {
					System.out.println("There is not Process Profile!!");
				}
				return null;
			}
			
	  }//end GetServiceProfile 
	  
	  /**
	   * GetServiceGrounding
	   * @param String urlProcessDoc
	   * @param String processname
	   * @param String processProfile
	   * @param OntModel m
	   */ 
	  public String GetServiceGrounding(String urlProcessDoc, String processname, String processProfile, OntModel m){
	  	
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
						+ "?x service:supportedBy "+processProfile 
						+ "}";

			Query queryProcessGround = QueryFactory.create(queryStringProcessGround);

			// Execute the query and obtain results
			QueryExecution qeProcessGround = QueryExecutionFactory.create(queryProcessGround, m);
			ResultSet resultProcessGround = qeProcessGround.execSelect();
			// To take the grounding
			String result = resultProcessGround.next().toString();
			StringTokenizer Tok = new StringTokenizer(result);
			String processGroundResult = Tok.nextToken("=");
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
	  public String GetServiceWSDLGrounding(String urlProcessDoc, String processGround, OntModel m){
			
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
			String processGroundWSDLResult = Tok.nextToken("=");
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
						+ processGroundWSDL +" grounding:wsdlDocument ?x" 
						+ "}";

			System.out.println("queryStringDocWSDL: "+queryStringDocWSDL);
			Query queryDocWSDL = QueryFactory.create(queryStringDocWSDL);

			// Execute the query and obtain results
			QueryExecution qeDocWSDL = QueryExecutionFactory.create(queryDocWSDL, m);
			ResultSet resultDocWSDL = qeDocWSDL.execSelect();
			
			
			// To take the WSDL Grounding Document
			String result = resultDocWSDL.next().toString();
			StringTokenizer Tok = new StringTokenizer(result);
			String DocWSDLResult = Tok.nextToken("=");
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
			String URL = null;
			URL = DocURL;
			String WsdlURL = "";
			Tok = new StringTokenizer(URL, ":8080/");
			String DocPartURL = Tok.nextToken();
			System.out.println("DocPartURL"+DocPartURL);	
			
			return (DocPartURL);
	  }//end GetServiceWSDLGroundingDoc
	  	
	  
	  
	  /**
	   * DeleteProfile
	   * @param urlProfileService
	   * @param Profile
	   * @param m
	   * @return
	   */
	  private void DeleteProfile(String urlProfileService,String Profile, OntModel m){
	 	 
	 	 if (DEBUG) {
				System.out.println("Delete Profile... "+urlProfileService);
		      }
	 	 
	 	//Delete profile tuples where the property is profile
			String update= 
	      	"prefix xsd: <http://www.w3.org/2001/XMLSchema#>" +
	          "prefix service: <http://www.daml.org/services/owl-s/1.1/Service.owl#>" +
	          "prefix proc: <http://www.daml.org/services/owl-s/1.1/Process.owl#>" +  
	          "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>" + 
	          "delete {?x ?y ?z}" +
	          "where" +
	          "{" +Profile+" ?y ?z" +
	          " filter ( ?y = profile:hasInput " +
	                 "|| ?y = profile:hasOutput " +
	                 "|| ?y = profile:serviceName " +
	                 "|| ?y = service:isPresentedBy " +
	                 "|| ?y = service:presentedBy " +
	                 "|| ?y = profile:textDescription " +
	                 "|| ?y = profile:has_process " +
	                 "|| ?y = service:presents " +
	                 "|| ?z = profile:Profile " +
	                 ")" +
	          "?x ?y ?z}";
			
		         		
			//Delete tuples where the property is service (it is related with the profile)
	      String update2= 
	      	"prefix xsd: <http://www.w3.org/2001/XMLSchema#>" +
	          "prefix service: <http://www.daml.org/services/owl-s/1.1/Service.owl#>" +
	          "prefix proc: <http://www.daml.org/services/owl-s/1.1/Process.owl#>" +  
	          "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>" + 
	          "delete {?x ?y ?z}" +
	          "where" +
	          "{ <"+urlProfileService+"> ?y ?z" +
	          " filter ( ?z = service:Service " +
	                 "|| ?y = service:presents " +
	                 ")" +
	          "?x ?y ?z}";
	      
	     
	 		// Execute the query and obtain results
	 		QuerySolution querysol=new QuerySolutionMap();
	        UpdateAction.parseExecute(update, m, querysol);
	        UpdateAction.parseExecute(update2, m, querysol);
	     
	           
	  }// end DeleteProfile
	  
	  
	  
	  
	  
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

				for (Iterator j = resultsqeWSDLMsgMap; resultsqeWSDLMsgMap.hasNext();) {
				
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

				for (Iterator j = resultsqeWSDLPort; resultsqeWSDLPort.hasNext();) {
				
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

				for (Iterator j = resultsqeWSDLOp; resultsqeWSDLOp.hasNext();) {
				
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
				for (Iterator j = resultsServiceInputs; resultsServiceInputs.hasNext();) {
					//To take only the name of the input
					String result = resultsServiceInputs.next().toString();
					StringTokenizer Tok = new StringTokenizer(result);
					String processInputResult = Tok.nextToken("#");
					String processInputName = Tok.nextToken();
					processInputName = processInputName.replace(">", "");
					processInputName = processInputName.replace(")", "");
					if (DEBUG) {
						System.out.println("Process Input: " + processInputName);
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
				for (Iterator j = resultsServiceOutputs; resultsServiceOutputs.hasNext();) {
					// To take the name of the output
					String result = resultsServiceOutputs.next().toString();
					StringTokenizer Tok = new StringTokenizer(result);
					String processOutputResult = Tok.nextToken("#");
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
	
	public void clean(){
		try {
			conn.cleanDB();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private OntModelSpec getModelSpec(ModelMaker maker) {
		// create a spec for the new ont model that will use no inference over
		// models made by the given maker (which is where we get the persistent models from)
		OntModelSpec spec = new OntModelSpec(OntModelSpec.OWL_MEM);
		spec.setImportModelMaker(maker);

		return spec;
	}
	
	public void writeModel(){
		ModelMaker maker = ModelFactory.createModelRDBMaker(conn);
		Model base = maker.createModel("http://example.org/ontologias");
		OntModel m = ModelFactory.createOntologyModel(getModelSpec(maker), base);
		
		m.write(System.out, "N3");
	}

}
