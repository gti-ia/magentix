package persistence;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
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
 * 
 * @author Jaume Jordan
 * 
 */
public class SFinterface {

	private static boolean DEBUG = true;

	private IDBConnection conn = null;

	/**
	 * Constructor of the SFinterface that starts a connection with Jena DB
	 */
	public SFinterface() {
		super();
		conn = JenaDBConnection();
	}

	/**
	 * Opens a Jena DB connection taking the configuration parameters from
	 * THOMASDemoConfiguration.xml file
	 * 
	 * @return {@link IDBConnection} to Jena DB
	 */
	private IDBConnection JenaDBConnection() {
		IDBConnection conn = null;

		String s_dbURL = "";
		String s_dbUser = "";
		String s_dbPw = "";
		String s_dbType = "";
		String s_dbDriver = "";

		Properties properties = new Properties();

		try {

			properties.loadFromXML(SFinterface.class.getResourceAsStream("/" + "THOMASDemoConfiguration.xml"));
			for (Enumeration<Object> e = properties.keys(); e.hasMoreElements();) {
				// Obtain the object
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
			s_dbDriver = "com.mysql.jdbc.Driver";
			Class.forName(s_dbDriver);
		} catch (Exception e) {
			System.err.println("Failed to load the driver for the database: " + e.getMessage());
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
	 * Makes a clean to the connection of Jena DB. This deletes all the
	 * information in the Jena DB.
	 */
	public void clean() {
		try {
			conn.cleanDB();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Returns an {@link OntModelSpec} of Jena DB.
	 * 
	 * @param maker
	 *            {@link ModelMaker} to create an {@link OntModelSpec}
	 * @return an {@link OntModelSpec} of Jena DB
	 */
	private OntModelSpec getModelSpec(ModelMaker maker) {
		// create a spec for the new ont model that will use no inference over
		// models made by the given maker (which is where we get the persistent
		// models from)
		OntModelSpec spec = new OntModelSpec(OntModelSpec.OWL_MEM);
		spec.setImportModelMaker(maker);

		return spec;
	}

	/**
	 * Writes to the standard output the Jena DB Model
	 */
	public void writeModel() {
		ModelMaker maker = ModelFactory.createModelRDBMaker(conn);
		Model base = maker.createModel("http://example.org/ontologias");
		OntModel m = ModelFactory.createOntologyModel(getModelSpec(maker), base);

		m.write(System.out, "N3");
	}

	/**
	 * The RegisterService tries to register the service that is specified as
	 * parameter. If it is already registered, it registers the new providers
	 * and/or groundings.
	 * 
	 * @param serviceURL
	 *            is the original URL of the OWL-S specification of the service
	 * @return a {@link String} with an XML response describing if the service
	 *         has been entirely registered or the number of groundings added to
	 *         an already registered service profile. It also returns the entire
	 *         OWL-S specification of the related service.
	 */
	public String registerService(String serviceURL) {
		String resultXML = "<response>\n<serviceName>RegisterService</serviceName>\n";
		String owlsService = "";
		String description = "";
		int nGrounds = 0, nProviders = 0;
		boolean fullRegister = false;
		try {

			// open the serviceURL as a file
			URL url = new URL(serviceURL);
			BufferedReader inBR = new BufferedReader(new InputStreamReader(url.openStream()));

			// read lines to find if it is an XML document
			String line = inBR.readLine();
			boolean isXML = false;
			while (line != null) {
				if (!line.contains("<?xml version=")) {
					System.out.println(line);
					line = inBR.readLine();
				} else {
					isXML = true;
					break;
				}
			}
			// is not an XML document, return error
			if (!isXML) {
				String msg = serviceURL + " is not a valid OWL-S document";
				resultXML += "<status>Error</status>\n";
				resultXML += "<result>\n<description>ERROR: " + msg + "</description>\n</result>\n";

				resultXML += "</response>";

				return resultXML;
			}
		} catch (Exception e) {
			String msg = serviceURL + " is not a valid OWL-S document";
			resultXML += "<status>Error</status>\n";
			resultXML += "<result>\n<description>ERROR: " + msg + "</description>\n</result>\n";

			resultXML += "</response>";

			return resultXML;
		}

		try {

			String serviceName = getProfileServiceName(null, serviceURL);
			String textDescription = getProfileTextDescription(null, serviceURL);

			ArrayList<String> inputs = getInputs(null, serviceURL);
			ArrayList<String> outputs = getOutputs(null, serviceURL);

			ArrayList<String> inputsParams = getInputParameterTypes(inputs, serviceURL);
			ArrayList<String> outputParams = getOutputParameterTypes(outputs, serviceURL);

			String regServiceProfile = searchRegisteredServices(serviceName, textDescription, inputsParams,
					outputParams);
			if (!regServiceProfile.equalsIgnoreCase("")) {
				System.out.println("Service already registered: " + regServiceProfile);

				ArrayList<String> newProviders = getProviders(null, serviceURL);

				// try to register the providers if they are not registered
				ArrayList<String> registeredProviders = getProviders(regServiceProfile, null);
				ArrayList<String> registeredProvidersNames = new ArrayList<String>();
				Iterator<String> iterRegsProvs = registeredProviders.iterator();
				while (iterRegsProvs.hasNext()) {
					registeredProvidersNames.add(getName(iterRegsProvs.next()));
				}
				ArrayList<String> providersToAdd = new ArrayList<String>();
				Iterator<String> iterNewProviders = newProviders.iterator();
				while (iterNewProviders.hasNext()) {
					String newProv = getName(iterNewProviders.next());
					if (!registeredProvidersNames.contains(newProv))
						providersToAdd.add(newProv);
				}

				nProviders = providersToAdd.size();

				if (!providersToAdd.isEmpty()) {
					String fileName = "tmp.owls";
					writeProvidersOWLSFile(serviceURL, regServiceProfile, fileName);

					ModelMaker maker = ModelFactory.createModelRDBMaker(conn);
					Model base = maker.createModel("http://example.org/ontologias");
					OntModelSpec spec = new OntModelSpec(OntModelSpec.OWL_MEM);
					spec.setImportModelMaker(maker);
					OntModel m = ModelFactory.createOntologyModel(spec, base);

					try {

						File file = new File(fileName);
						InputStream in = new FileInputStream(file);

						m.read(in, "");
						m.commit();

					} catch (FileNotFoundException e) {

						e.printStackTrace();
					}
				}

				// obtain the registered wsdl docs and the one that has to be
				// registered.
				// If the new wsdl doc is different, it is registered

				ArrayList<String> wsdlsToRegister = getWSDLDocumentFromServiceURL(serviceURL);

				String serviceURI = getServiceURI(regServiceProfile, null);
				ArrayList<String> groundings = getGroundings(serviceURI, null);

				Iterator<String> iterWsdlsToRegister = wsdlsToRegister.iterator();
				while (iterWsdlsToRegister.hasNext()) {
					String wsdlToRegister = iterWsdlsToRegister.next();

					Iterator<String> iterGrounds = groundings.iterator();
					String groundingURI = "";
					boolean found = false;
					while (iterGrounds.hasNext()) {
						groundingURI = iterGrounds.next();
						String atomicProcessGrounding = getAtomicProcessGrounding(groundingURI, null);
						String WSDLDocandDatatype = getWSDLDocument(atomicProcessGrounding, null);

						if (WSDLDocandDatatype.equalsIgnoreCase(wsdlToRegister)) {
							found = true;
							break;
						}
					}
					if (!found) {
						System.out.println("Register new grounding");
						nGrounds++;

						StringTokenizer token = new StringTokenizer(wsdlToRegister, "^^");
						String wsdlDoc = token.nextToken();
						// String wsdlDatatype=token.nextToken();
						wsdlToRegister = "\"" + wsdlDoc + "\"" + "^^xsd:anyURI";

						String fileName = "tmp.owls";
						writeGroundingOWLSFile(serviceURL, regServiceProfile, wsdlToRegister, fileName);

						ModelMaker maker = ModelFactory.createModelRDBMaker(conn);
						Model base = maker.createModel("http://example.org/ontologias");
						OntModelSpec spec = new OntModelSpec(OntModelSpec.OWL_MEM);
						spec.setImportModelMaker(maker);
						OntModel m = ModelFactory.createOntologyModel(spec, base);

						try {

							File file = new File(fileName);
							InputStream in = new FileInputStream(file);

							m.read(in, "");
							m.commit();

						} catch (FileNotFoundException e) {

							e.printStackTrace();
						}
					}
				}
				if (nGrounds == 0 && nProviders == 0)
					description = "ERROR: All information already registered in service profile: " + regServiceProfile;
				else
					description = nGrounds + " groundings and " + nProviders
							+ " providers registered to service profile: " + regServiceProfile;
				System.out.println(nGrounds + " groundings and " + nProviders
						+ " providers registered to service profile: " + regServiceProfile);

			} else {
				ModelMaker maker = ModelFactory.createModelRDBMaker(conn);
				Model base = maker.createModel("http://example.org/ontologias");
				// now we plug that base model into an ontology model that also
				// uses
				// the given model maker to create storage for imported models
				OntModelSpec spec = new OntModelSpec(OntModelSpec.OWL_MEM);
				spec.setImportModelMaker(maker);
				OntModel m = ModelFactory.createOntologyModel(spec, base);

				// load the service profile in the database
				m.read(serviceURL);
				m.commit();

				// Close the model
				m.close();

				regServiceProfile = getProfileURIfromURL(serviceURL);

				description = "Service registered: " + regServiceProfile;
				System.out.println("Service registered: " + regServiceProfile);
				fullRegister = true;
			}

			owlsService = getServiceOWLS(regServiceProfile);

		} catch (Exception e) {
			e.printStackTrace();
			String msg;
			if (e instanceof THOMASException)
				msg = ((THOMASException) e).getContent();
			else
				msg = e.toString();
			resultXML += "<status>Error</status>\n";
			resultXML += "<result>\n<description>ERROR: " + msg + "</description>\n</result>\n";

			resultXML += "</response>";

			return resultXML;
		}

		if (nGrounds == 0 && nProviders == 0 && !fullRegister)
			resultXML += "<status>Error</status>\n";
		else
			resultXML += "<status>Ok</status>\n";

		resultXML += "<result>\n<description>" + description + "</description>\n" + "<specification>\n<!-- "
				+ owlsService + " -->\n</specification>\n</result>\n";

		resultXML += "</response>";

		return resultXML;

	}

	/**
	 * Deregister service of the SF. It removes a complete service given its
	 * service profile URI.
	 * 
	 * @param serviceProfile
	 *            URI of the service to deregister
	 * @return a {@link String} with an XML response describing if the service
	 *         has been entirely deregistered or not
	 */
	public String deregisterService(String serviceProfile) {
		String resultXML = "<response>\n<serviceName>DeregisterService</serviceName>\n";
		try {

			String profileServName = getProfileServiceName(serviceProfile, null);

			if (profileServName == null || profileServName == "") {// service
																	// does not
																	// exist
				resultXML += "<status>Error</status>\n";
				resultXML += "<result>\n<description>" + "ERROR: Service " + serviceProfile + " does not exist"
						+ "</description>\n</result>\n";
				resultXML += "</response>";

				return resultXML;

			} else {

				String serviceURI = getServiceURI(serviceProfile, null);
				String serviceProcess = getServiceProcess(serviceProfile, null);

				ArrayList<String> providers = getProviders(serviceProfile, null);
				Iterator<String> iterProvs = providers.iterator();
				while (iterProvs.hasNext()) {
					String provider = iterProvs.next();
					StringTokenizer tokProv = new StringTokenizer(provider, "#");
					tokProv.nextToken();
					String providerName = tokProv.nextToken();
					removeProvider(serviceProfile, providerName);
				}

				removeProcess(serviceProcess, serviceProfile);
				removeProfile(serviceProfile, serviceURI);

			}

		} catch (Exception e) {
			e.printStackTrace();
			resultXML += "<status>Error</status>\n";
			resultXML += "<result>\n<description>ERROR: " + e.getMessage() + "</description>\n</result>\n";

			resultXML += "</response>";

			return resultXML;

		}

		resultXML += "<status>Ok</status>\n";
		resultXML += "<result>\n<description>Service " + serviceProfile + " Deregistered</description>\n</result>\n";
		resultXML += "</response>";

		return resultXML;
	}

	/**
	 * Implementation of the SF service Get Service. It returns an OWL-S
	 * specification with the information of the given service.
	 * 
	 * @param serviceProfile
	 *            URI to extract data from
	 * @return a {@link String} with an XML response with an OWL-S specification
	 *         with the information of the given service, or an specified error
	 */
	public String getService(String serviceProfile) {
		String resultXML = "<response>\n<serviceName>GetService</serviceName>\n";
		String owlsService = "";

		try {

			owlsService = getServiceOWLS(serviceProfile);

		} catch (Exception e) {
			resultXML += "<status>Error</status>\n";
			resultXML += "<result>\n<description>ERROR: " + e.getMessage() + "</description>\n</result>\n";

			resultXML += "</response>";

			return resultXML;
		}

		if (owlsService == null || owlsService == "") {
			resultXML += "<status>Error</status>\n";
			resultXML += "<result>\n<description>ERROR: service profile " + serviceProfile
					+ " not found</description>\n</result>\n";
		} else {
			resultXML += "<status>Ok</status>\n";
			resultXML += "<result>\n<specification><!-- \n" + owlsService + " -->\n</specification>\n</result>\n";
		}
		resultXML += "</response>";

		return resultXML;

	}

	/**
	 * The SearchService receives as parameters three lists with the data types
	 * of the inputs and outputs desired, and another with the keywords desired.
	 * With these parameters, the service searches in the Jena DB and returns
	 * the more similar services, that is, the services that have the same (or
	 * almost the same) data types as inputs and outputs, weighted in function
	 * of the amount of similarity.
	 * 
	 * @param inputs
	 *            list of the desired input parameter type to search a similar
	 *            service. Example:
	 *            \"http://127.0.0.1/ontology/books.owl#Novel\"^^xsd:anyURI
	 * @param outputs
	 *            list of the desired output parameter type to search a similar
	 *            service. Example:
	 *            \"http://127.0.0.1/ontology/books.owl#Novel\"^^xsd:anyURI
	 * @param keywords
	 *            list of the desired keywords that describes a service
	 * @return a {@link String} with an XML response with a list of found
	 *         services or an specified error
	 */
	public String searchService(ArrayList<String> inputs, ArrayList<String> outputs, ArrayList<String> keywords) {
		String resultXML = "<response>\n<serviceName>SearchService</serviceName>\n";
		String itemsList = "";
		// store a list of profiles with their similarity weights to the service
		ArrayList<Profile> profiles = new ArrayList<Profile>();

		try {
			ModelMaker maker = ModelFactory.createModelRDBMaker(conn);
			Model base = maker.createModel("http://example.org/ontologias");
			OntModel m = ModelFactory.createOntologyModel(getModelSpec(maker), base);

			ArrayList<String> candidatesInputs = new ArrayList<String>();

			if (inputs != null && !inputs.isEmpty()) {
				// the service searches each input and add to a list each
				// service that has an equal input as a candidate
				Iterator<String> iterInputs = inputs.iterator();
				while (iterInputs.hasNext()) {

					String in = iterInputs.next();

					String queryStr = "prefix xsd: <http://www.w3.org/2001/XMLSchema#>"
							+ "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>"
							+ "prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>"
							+ "select ?x where { ?x a process:Input ; process:parameterType " + in + " . }";

					Query query = QueryFactory.create(queryStr);

					// Execute the query and obtain results
					QueryExecution querySearchInputs = QueryExecutionFactory.create(query, m);
					ResultSet resultsSearchInputs = querySearchInputs.execSelect();

					if (resultsSearchInputs != null) {

						while (resultsSearchInputs.hasNext()) {

							QuerySolution sol = resultsSearchInputs.next();

							Resource resource = sol.getResource("x");
							String cand = resource.getURI();
							if (!candidatesInputs.contains(cand))
								candidatesInputs.add(cand);

						}// end for
					}// end if

					// close the query
					querySearchInputs.close();

				}

				// obtain the profileURL of each candidate and create their
				// profiles
				Iterator<String> iterCandidates = candidatesInputs.iterator();
				while (iterCandidates.hasNext()) {
					String cand = iterCandidates.next();

					String queryStr = "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>"
							+ "prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>"
							+ "select ?x where { ?x profile:hasInput " + "<" + cand + ">" + " }";

					Query query = QueryFactory.create(queryStr);

					// Execute the query and obtain results
					QueryExecution querySearchInputs = QueryExecutionFactory.create(query, m);
					ResultSet resultsSearchInputs = querySearchInputs.execSelect();

					if (resultsSearchInputs != null && resultsSearchInputs.hasNext()) {
						QuerySolution sol = resultsSearchInputs.next();

						Resource resource = sol.getResource("x");
						String profileURL = resource.getURI();
						Profile profile = new Profile(profileURL, 0f);
						if (!profiles.contains(profile))
							profiles.add(profile);

					}

				}

			}

			ArrayList<String> candidatesOutputs = new ArrayList<String>();

			if (outputs != null && !outputs.isEmpty()) {
				// the service searches each output and add to a list each
				// service that has an equal output as a candidate
				Iterator<String> iterOutputs = outputs.iterator();
				while (iterOutputs.hasNext()) {

					String in = iterOutputs.next();

					String queryStr = "prefix xsd: <http://www.w3.org/2001/XMLSchema#>"
							+ "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>"
							+ "prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>"
							+ "select ?x where { ?x a process:Output ; process:parameterType " + in + " . }";

					Query query = QueryFactory.create(queryStr);

					// Execute the query and obtain results
					QueryExecution querySearchInputs = QueryExecutionFactory.create(query, m);
					ResultSet resultsSearchInputs = querySearchInputs.execSelect();

					if (resultsSearchInputs != null) {

						while (resultsSearchInputs.hasNext()) {

							QuerySolution sol = resultsSearchInputs.next();

							Resource resource = sol.getResource("x");
							String cand = resource.getURI();
							if (!candidatesOutputs.contains(cand))
								candidatesOutputs.add(cand);

						}// end for
					}// end if

					// close the query
					querySearchInputs.close();

				}

				// obtain the profileURL of each candidate and create their
				// profiles
				Iterator<String> iterCandidates = candidatesOutputs.iterator();
				while (iterCandidates.hasNext()) {
					String cand = iterCandidates.next();

					String queryStr = "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>"
							+ "prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>"
							+ "select ?x where { ?x profile:hasOutput " + "<" + cand + ">" + " }";

					Query query = QueryFactory.create(queryStr);

					// Execute the query and obtain results
					QueryExecution querySearchInputs = QueryExecutionFactory.create(query, m);
					ResultSet resultsSearchInputs = querySearchInputs.execSelect();

					if (resultsSearchInputs != null && resultsSearchInputs.hasNext()) {
						QuerySolution sol = resultsSearchInputs.next();

						Resource resource = sol.getResource("x");
						String profileURL = resource.getURI();
						Profile profile = new Profile(profileURL, 0f);
						if (!profiles.contains(profile))
							profiles.add(profile);

					}

				}

			}

			if (keywords != null && !keywords.isEmpty()) {
				HashMap<String, String> textDescriptions = getProfilesTextDescriptions();

				Iterator<String> iterTextDescriptions = textDescriptions.keySet().iterator();
				while (iterTextDescriptions.hasNext()) {
					String profileURI = iterTextDescriptions.next();
					String textDescription = textDescriptions.get(profileURI).toLowerCase().trim();
					Iterator<String> iterKeywords = keywords.iterator();
					while (iterKeywords.hasNext()) {
						String keyword = iterKeywords.next().toLowerCase().trim();
						if (textDescription.contains(keyword)) {
							Profile profile = new Profile(profileURI, 0f);
							if (!profiles.contains(profile))
								profiles.add(profile);

						}
					}
				}
			}

			Iterator<Profile> iterProfiles = profiles.iterator();
			while (iterProfiles.hasNext()) {
				Profile profile = iterProfiles.next();
				int inputsProfile = 0;
				int outputsProfile = 0;
				int sameInputs = 0;
				int sameOutputs = 0;

				// SEARCH INPUTS
				String queryStr = "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>"
						+ "prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>"
						+ "select ?x where { " + "<" + profile.getUrl() + ">" + " profile:hasInput ?x }";

				Query query = QueryFactory.create(queryStr);

				// Execute the query and obtain results
				QueryExecution querySearchInputs = QueryExecutionFactory.create(query, m);
				ResultSet resultsSearchInputs = querySearchInputs.execSelect();

				if (resultsSearchInputs != null) {

					while (resultsSearchInputs.hasNext()) {
						inputsProfile++;
						QuerySolution sol = resultsSearchInputs.next();

						if (inputs != null && !inputs.isEmpty() && sameInputs < inputs.size()) {

							Resource resource = sol.getResource("x");
							String input = resource.getURI();

							// explore all inputs (searching their type) to find
							// out if it is in the service
							String queryStrType = "prefix xsd: <http://www.w3.org/2001/XMLSchema#>"
									+ "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>"
									+ "prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>"
									+ "select ?x where { <" + input
									+ "> a process:Input ; process:parameterType ?x . }";

							Query queryType = QueryFactory.create(queryStrType);

							// Execute the query and obtain results
							QueryExecution querySearchInputType = QueryExecutionFactory.create(queryType, m);
							ResultSet resultsSearchInputType = querySearchInputType.execSelect();

							if (resultsSearchInputType != null && resultsSearchInputType.hasNext()) {
								QuerySolution sol2 = resultsSearchInputType.next();

								Literal literal2 = sol2.getLiteral("x");
								String parameterType = literal2.getString();
								Iterator<String> iterInputsSearch = inputs.iterator();
								while (iterInputsSearch.hasNext()) {
									String inputSearch = iterInputsSearch.next();
									String inputSearchModif = inputSearch.replaceAll("\"", "");
									StringTokenizer stringTokenizer = new StringTokenizer(inputSearchModif, "^^");
									inputSearchModif = stringTokenizer.nextToken();
									if (inputSearchModif.equalsIgnoreCase(parameterType)) {
										sameInputs++;
										break;
									}
								}

							}

						}
					}
				}

				// SEARCH OUTPUTS
				String queryStrOutputs = "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>"
						+ "prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>"
						+ "select ?x where { " + "<" + profile.getUrl() + ">" + " profile:hasOutput ?x }";

				Query queryOutputs = QueryFactory.create(queryStrOutputs);

				// Execute the query and obtain results
				QueryExecution querySearchOutputs = QueryExecutionFactory.create(queryOutputs, m);
				ResultSet resultsSearchOutputs = querySearchOutputs.execSelect();

				if (resultsSearchOutputs != null) {

					while (resultsSearchOutputs.hasNext()) {
						outputsProfile++;
						QuerySolution sol = resultsSearchOutputs.next();

						if (outputs != null && !outputs.isEmpty() && sameOutputs < outputs.size()) {

							Resource resource = sol.getResource("x");
							String output = resource.getURI();

							// explore all outputs (searching their type) to
							// find out if it is in the service
							String queryStrType = "prefix xsd: <http://www.w3.org/2001/XMLSchema#>"
									+ "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>"
									+ "prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>"
									+ "select ?x where { <" + output
									+ "> a process:Output ; process:parameterType ?x . }";

							Query queryType = QueryFactory.create(queryStrType);

							// Execute the query and obtain results
							QueryExecution querySearchOutputType = QueryExecutionFactory.create(queryType, m);
							ResultSet resultsSearchOutputType = querySearchOutputType.execSelect();

							if (resultsSearchOutputType != null && resultsSearchOutputType.hasNext()) {
								QuerySolution sol2 = resultsSearchOutputType.next();

								Literal literal2 = sol2.getLiteral("x");
								String parameterType = literal2.getString();
								Iterator<String> iterOutputsSearch = outputs.iterator();
								while (iterOutputsSearch.hasNext()) {
									String outputSearch = iterOutputsSearch.next();
									String outputSearchModif = outputSearch.replaceAll("\"", "");
									StringTokenizer stringTokenizer = new StringTokenizer(outputSearchModif, "^^");
									outputSearchModif = stringTokenizer.nextToken();
									if (outputSearchModif.equalsIgnoreCase(parameterType)) {
										sameOutputs++;
										break;
									}
								}

							}

						}
					}
				}

				int keywordsFound = 0;
				if (keywords != null && !keywords.isEmpty()) {
					// obtain the similarity of the searched keywords in the
					// profile text description
					String textDescription = getProfileTextDescription(profile.getUrl(), null).toLowerCase().trim();

					Iterator<String> iterKeywords = keywords.iterator();
					while (iterKeywords.hasNext()) {
						String keyword = iterKeywords.next().toLowerCase().trim();
						if (textDescription.contains(keyword))
							keywordsFound++;
					}
				}

				// obtain the final similarity degree of the profile

				float inputsSimilarity = 0, outputsSimilarity = 0, similarityToKeywords = 0, similaritiesUsed = 0;
				if (inputs != null && !inputs.isEmpty()) {
					inputsSimilarity = ((float) sameInputs / inputsProfile) * ((float) sameInputs / inputs.size());
					similaritiesUsed++;
				}
				if (outputs != null && !outputs.isEmpty()) {
					outputsSimilarity = ((float) sameOutputs / outputsProfile) * ((float) sameOutputs / outputs.size());
					similaritiesUsed++;
				}
				if (keywords != null && !keywords.isEmpty()) {
					similarityToKeywords = (float) keywordsFound / keywords.size();
					similaritiesUsed++;
				}
				float similarity = (1.0f / similaritiesUsed) * inputsSimilarity + (1.0f / similaritiesUsed)
						* outputsSimilarity + (1.0f / similaritiesUsed) * similarityToKeywords;
				profile.setSuitability(similarity);

			}// end iterator profiles

			// sort the found candidate profiles by their similarity
			Collections.sort(profiles);

			iterProfiles = profiles.iterator();
			while (iterProfiles.hasNext()) {
				Profile profile = iterProfiles.next();
				itemsList += "\t<item>\n\t\t<profile>" + profile.getUrl() + "</profile>\n\t\t<quantity>"
						+ profile.getSuitability() + "</quantity>\n\t</item>\n";
			}

		} catch (com.hp.hpl.jena.query.QueryParseException e) {
			e.printStackTrace();
			resultXML += "<status>Error</status>\n";
			resultXML += "<result>\n<description>ERROR: incorrect input or output data type</description>\n</result>\n";

			resultXML += "</response>";

			return resultXML;
		} catch (Exception e) {
			e.printStackTrace();
			resultXML += "<status>Error</status>\n";
			resultXML += "<result>\n<description>ERROR: " + e.getMessage() + "</description>\n</result>\n";

			resultXML += "</response>";

			return resultXML;
		}

		if (profiles.size() == 0) {
			resultXML += "<status>Error</status>\n";
			resultXML += "<result>\n<description>ERROR: No services found</description>\n</result>\n";
		} else if (profiles.size() == 1) {
			resultXML += "<status>Ok</status>\n";
			resultXML += "<result>\n" + itemsList + "</result>\n";
		} else {
			resultXML += "<status>Ok</status>\n";
			resultXML += "<result>\n" + itemsList + "</result>\n";
		}
		resultXML += "</response>";

		return resultXML;

	}

	/**
	 * Removes a provider from a registered service in the Jena DB
	 * 
	 * @param serviceProfile
	 *            URI of the service to remove the provider
	 * @param providerName
	 *            of the provider to remove, or the complete grounding URI
	 * @return a {@link String} with an XML response indicating if the provider
	 *         has been removed or not, and why.
	 */
	public String removeProvider(String serviceProfile, String providerName) {
		String resultXML = "<response>\n<serviceName>RemoveProvider</serviceName>\n";
		try {

			StringTokenizer tokenServiceProf = new StringTokenizer(serviceProfile, "#");
			String baseURI = tokenServiceProf.nextToken();
			String profileName = tokenServiceProf.nextToken();
			String serviceURI = getServiceURI(serviceProfile, null);

			ModelMaker maker = ModelFactory.createModelRDBMaker(conn);
			Model base = maker.createModel("http://example.org/ontologias");
			OntModel m = ModelFactory.createOntologyModel(getModelSpec(maker), base);

			// search if the given provider name is specified as a provider of
			// the given service profile
			boolean found = false;
			ArrayList<String> providers = getProviders(serviceProfile, null);
			Iterator<String> iterProviders = providers.iterator();
			while (iterProviders.hasNext()) {
				String prov = iterProviders.next();
				String provName = prov.split("#")[1];
				if (providerName.equals(provName))
					found = true;
			}
			if (!found) {
				// search if the given provider name is a grounding
				// identification

				boolean foundGround = false;
				StringTokenizer tokGround = new StringTokenizer(providerName, "#");
				String groundName = tokGround.nextToken();
				if (tokGround.hasMoreTokens())
					groundName = tokGround.nextToken();

				ArrayList<String> grounds = getGroundings(serviceURI, null);
				Iterator<String> iterGrounds = grounds.iterator();
				while (iterGrounds.hasNext()) {
					String ground = iterGrounds.next();
					String groundN = ground.split("#")[1];
					if (groundName.equals(groundN))
						foundGround = true;
				}
				if (!foundGround) {
					resultXML += "<status>Error</status>\n";
					resultXML += "<result>\n<description>" + "ERROR: Provider or grounding " + providerName
							+ " not found" + "</description>\n</result>\n";

					resultXML += "</response>";

					return resultXML;
				} else {
					removeGrounding(providerName);
				}
			} else {

				// Delete provider
				String delete = "prefix xsd: <http://www.w3.org/2001/XMLSchema#>"
						+ "prefix service: <http://www.daml.org/services/owl-s/1.1/Service.owl#>"
						+ "prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>"
						+ "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>"
						+ "prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>" + "prefix mind: <" + baseURI + "#>"
						+ "delete {?x ?y ?z} " + "where" + "{mind:" + profileName + " ?y ?z"
						+ " filter ( ?y = profile:contactInformation " + "&& ?z = <" + baseURI + "#" + providerName
						+ ">" + ")" + "?x ?y ?z}";

				String delete2 = "prefix xsd: <http://www.w3.org/2001/XMLSchema#>"
						+ "prefix service: <http://www.daml.org/services/owl-s/1.1/Service.owl#>"
						+ "prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>"
						+ "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>"
						+ "prefix provider: <http://127.0.0.1/ontology/provider.owl#>"
						+ "prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>" + "prefix mind: <" + baseURI + "#>"
						+ "delete {?x ?y ?z} " + "where" + "{mind:" + providerName + " ?y ?z"
						+ " filter ( ( ?y = provider:entityID " + "|| ?y = provider:entityType "
						+ "|| ?y = provider:language " + "|| ?y = provider:performative "
						+ "|| ?z = provider:Provider )" + "&& ?x = <" + baseURI + "#" + providerName + ">" + ")"
						+ "?x ?y ?z}";

				QuerySolution querysol = new QuerySolutionMap();
				UpdateAction.parseExecute(delete, m, querysol);
				UpdateAction.parseExecute(delete2, m, querysol);

			}

		} catch (Exception e) {
			String msg;
			if (e.getMessage() == null || e.getMessage() == "")
				msg = e.toString();
			else
				msg = e.getMessage();
			resultXML += "<status>Error</status>\n";
			resultXML += "<result>\n<description>ERROR: " + msg + "</description>\n</result>\n";

			resultXML += "</response>";

			return resultXML;
		}

		resultXML += "<status>Ok</status>\n";
		resultXML += "<result>\n<description>" + "Provider or grounding " + providerName + " removed"
				+ "</description>\n</result>\n";
		resultXML += "</response>";

		return resultXML;

	}

	/**
	 * Search in the Jena DB if it exists a registered service with the same
	 * given service name, text description, inputs and outputs
	 * 
	 * @param serviceName
	 *            to search
	 * @param textDescription
	 *            to search
	 * @param inputs
	 *            of the service to search specified as parameter type
	 * @param outputs
	 *            of the service to search specified as parameter type
	 * @return the profile URI if it exists an equal registered service, or an
	 *         empty String if not
	 * @throws THOMASException
	 */
	private String searchRegisteredServices(String serviceName, String textDescription, ArrayList<String> inputs,
			ArrayList<String> outputs) throws THOMASException {

		try {

			// search the inputs

			ModelMaker maker = ModelFactory.createModelRDBMaker(conn);
			Model base = maker.createModel("http://example.org/ontologias");
			OntModel m = ModelFactory.createOntologyModel(getModelSpec(maker), base);

			ArrayList<String> candidates = new ArrayList<String>();
			boolean firstInput = true;
			Iterator<String> iterInputs = inputs.iterator();
			while (iterInputs.hasNext()) {

				String in = iterInputs.next();

				ArrayList<String> candN = new ArrayList<String>();

				String queryStr = "prefix xsd: <http://www.w3.org/2001/XMLSchema#>"
						+ "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>"
						+ "prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>"
						+ "select ?x where { ?x a process:Input ; process:parameterType " + in + " . }";

				Query query = QueryFactory.create(queryStr);

				// Execute the query and obtain results
				QueryExecution querySearchInputs = QueryExecutionFactory.create(query, m);
				ResultSet resultsSearchInputs = querySearchInputs.execSelect();

				if (resultsSearchInputs != null) {

					while (resultsSearchInputs.hasNext()) {
						QuerySolution sol = resultsSearchInputs.next();

						Resource resource = sol.getResource("x");
						String param = resource.getURI();
						String cand = getProfileURI(param, true);

						if (!candN.contains(cand))
							candN.add(cand);

					}// end for
				}// end if

				if (!candidates.isEmpty()) {
					candidates.retainAll(candN);
				}
				if (firstInput) {
					candidates.addAll(candN);
					firstInput = false;
				}

			}

			// search the outputs
			Iterator<String> iterOutputs = outputs.iterator();
			while (iterOutputs.hasNext()) {

				String out = iterOutputs.next();

				ArrayList<String> candN = new ArrayList<String>();

				String queryStr = "prefix xsd: <http://www.w3.org/2001/XMLSchema#>"
						+ "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>"
						+ "prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>"
						+ "select ?x where { ?x a process:Output ; process:parameterType " + out + " . }";

				Query query = QueryFactory.create(queryStr);

				// Execute the query and obtain results
				QueryExecution querySearchOutputs = QueryExecutionFactory.create(query, m);
				ResultSet resultsSearchOutputs = querySearchOutputs.execSelect();

				if (resultsSearchOutputs != null) {

					while (resultsSearchOutputs.hasNext()) {
						QuerySolution sol = resultsSearchOutputs.next();

						Resource resource = sol.getResource("x");
						String param = resource.getURI();
						String cand = getProfileURI(param, false);

						if (!candN.contains(cand))
							candN.add(cand);

					}// end for
				}// end if

				if (!candidates.isEmpty()) {
					candidates.retainAll(candN);

				}

			}

			if (candidates.isEmpty()) {
				return "";
			} else {

				for (int i = 0; i < candidates.size(); i++) {
					String candidate = candidates.get(i);
					String candServiceName = getProfileServiceName(candidate, null);
					String candTextDescription = getProfileTextDescription(candidate, null);

					// check that the service name and text description are
					// exactly the same
					if (!candServiceName.equalsIgnoreCase(serviceName)
							|| !candTextDescription.equalsIgnoreCase(textDescription)) {
						candidates.remove(i);
						i--;
						continue;
					} else { // check if the inputs and outputs are the same
								// exactly
								// (could arrive here having a service with more
								// inputs
								// than the specified as parameter and will be
								// different services)
						ArrayList<String> inputsCandidate = getInputs(candidate, null);
						ArrayList<String> outputsCandidate = getOutputs(candidate, null);

						ArrayList<String> inputParamTypeCand = getInputParameterTypes(inputsCandidate, null);
						ArrayList<String> outputParamTypeCand = getOutputParameterTypes(outputsCandidate, null);

						if (inputParamTypeCand.size() != inputs.size())
							return "";
						if (outputParamTypeCand.size() != outputs.size())
							return "";

						Iterator<String> iterInputCand = inputParamTypeCand.iterator();
						boolean found = false;
						while (iterInputCand.hasNext()) {
							String inCand = iterInputCand.next();

							Iterator<String> iterInput = inputs.iterator();
							while (iterInput.hasNext()) {
								String in = iterInput.next();

								if (inCand.equalsIgnoreCase(in)) {
									found = true;
									break;
								}
							}
							if (!found) {
								candidates.remove(i);
								i--;
								break;
							}

						}
						if (!found)
							continue;

						Iterator<String> iterOutputCand = outputParamTypeCand.iterator();
						found = false;
						while (iterOutputCand.hasNext()) {
							String outCand = iterOutputCand.next();

							Iterator<String> iterOutput = outputs.iterator();
							while (iterOutput.hasNext()) {
								String out = iterOutput.next();

								if (outCand.equalsIgnoreCase(out)) {
									found = true;
									break;
								}
							}
							if (!found) {
								candidates.remove(i);
								i--;
								break;
							}

						}
						if (!found)
							continue;
					}

				}

				if (!candidates.isEmpty())
					return candidates.get(0);
				else
					return "";
			}

		} catch (Exception e) {
			throw new THOMASException(e.getMessage());
		}
	}

	/**
	 * Returns the complete service OWL-S specification of the given service
	 * profile
	 * 
	 * @param serviceProfile
	 * @return the complete service OWL-S specification of the given service
	 *         profile
	 * @throws THOMASException
	 */
	private String getServiceOWLS(String serviceProfile) throws THOMASException {
		String owlsService = "";
		String profileServiceName = getProfileServiceName(serviceProfile, null);

		if (profileServiceName != null && profileServiceName != "") {
			// headers
			owlsService += "<?xml version=\"1.0\" encoding=\"WINDOWS-1252\"?>" + "\n"
					+ "<rdf:RDF  xmlns:owl       = \"http://www.w3.org/2002/07/owl#\"" + "\n"
					+ "xmlns:rdfs      = \"http://www.w3.org/2000/01/rdf-schema#\"" + "\n"
					+ "xmlns:rdf       = \"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"" + "\n"
					+ "xmlns:xsd       = \"http://www.w3.org/2001/XMLSchema#\"" + "\n"
					+ "xmlns:service   = \"http://www.daml.org/services/owl-s/1.1/Service.owl#\"" + "\n"
					+ "xmlns:process   = \"http://www.daml.org/services/owl-s/1.1/Process.owl#\"" + "\n"
					+ "xmlns:profile    = \"http://www.daml.org/services/owl-s/1.1/Profile.owl#\"" + "\n"
					+ "xmlns:grounding = \"http://www.daml.org/services/owl-s/1.1/Grounding.owl#\"" + "\n"
					+ "xmlns:provider = \"http://127.0.0.1/ontology/provider.owl#\">" + "\n";

			// profile descriptions...
			// profile:serviceName
			// profile:textDescription
			owlsService += "<profile:Profile rdf:ID=\"" + serviceProfile + "\">\n"
					+ "\t<profile:serviceName xml:lang=\"en\">" + profileServiceName + "</profile:serviceName>" + "\n";

			owlsService += "\t<profile:textDescription xml:lang=\"en\">"
					+ getProfileTextDescription(serviceProfile, null) + "</profile:textDescription>" + "\n";

			// providers
			// profile:contactInformation
			// a provider:Provider
			owlsService += getProvidersOWLS(serviceProfile, null);

			// inputs and outputs of a process with their types
			String processInputs = "", processOutputs = "";

			ArrayList<String> inputs = getInputs(serviceProfile, null);
			Iterator<String> iterInputs = inputs.iterator();
			while (iterInputs.hasNext()) {
				// profile:hasInput
				String in = iterInputs.next();
				owlsService += "\t<profile:hasInput rdf:resource=\"" + in + "\"/>" + "\n";
				processInputs += getInputOWLS(in);

			}

			ArrayList<String> outputs = getOutputs(serviceProfile, null);
			Iterator<String> iterOutputs = outputs.iterator();
			while (iterOutputs.hasNext()) {
				// profile:hasOutput
				String out = iterOutputs.next();
				owlsService += "\t<profile:hasOutput rdf:resource=\"" + out + "\"/>" + "\n";
				processOutputs += getOutputOWLS(out);
			}
			owlsService += "</profile:Profile>\n";

			// process:parameterType
			// process:parameterType

			owlsService += processInputs + processOutputs + "\n";

			// all the groundings
			owlsService += getGroundingsOWLS(serviceProfile);

			owlsService += "</rdf:RDF>";
		}

		return owlsService;
	}

	/**
	 * Returns the profile URI of the given parameter name.
	 * 
	 * @param paramName
	 *            parameter name of a service profile
	 * @param input
	 *            indicate if the param is an input (true) or output (false)
	 * @return the profile URI of the given parameter name.
	 */
	private String getProfileURI(String paramName, boolean input) {
		String res = "";

		ModelMaker maker = ModelFactory.createModelRDBMaker(conn);
		Model base = maker.createModel("http://example.org/ontologias");
		OntModel m = ModelFactory.createOntologyModel(getModelSpec(maker), base);

		String queryStringSearchName;
		if (input)
			queryStringSearchName = "prefix xsd: <http://www.w3.org/2001/XMLSchema#>"
					+ "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>"
					+ "prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>"
					+ "select ?x where { ?x profile:hasInput <" + paramName + "> }";
		else
			queryStringSearchName = "prefix xsd: <http://www.w3.org/2001/XMLSchema#>"
					+ "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>"
					+ "prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>"
					+ "select ?x where { ?x profile:hasOutput <" + paramName + "> }";

		Query querySearchName = QueryFactory.create(queryStringSearchName);

		QueryExecution qeSearchName = QueryExecutionFactory.create(querySearchName, m);
		ResultSet resultsSearchName = qeSearchName.execSelect();

		if (resultsSearchName != null) {

			while (resultsSearchName.hasNext()) {
				res = resultsSearchName.next().getResource("x").toString();

			}

		}// end if
		else {
			System.out.println("resultsSearchName is null");
		}

		// close the query
		qeSearchName.close();

		return res;
	}

	/**
	 * Returns the inputs of the given service profile
	 * 
	 * @param serviceProfile
	 *            URI to extract data from
	 * @param serviceURL
	 *            it specifies the service URL if the query is not to the Jena
	 *            DB. <code>null</code> to query the Jena DB model.
	 * @return the inputs of the given service profile
	 * @throws THOMASException
	 */
	private ArrayList<String> getInputs(String serviceProfile, String serviceURL) throws THOMASException {
		ArrayList<String> inputs = new ArrayList<String>();

		try {
			ModelMaker maker;
			Model base;
			OntModel m;
			String queryStringSearchName;

			if (serviceURL == null) {
				maker = ModelFactory.createModelRDBMaker(conn);
				base = maker.createModel("http://example.org/ontologias");
				m = ModelFactory.createOntologyModel(getModelSpec(maker), base);
				queryStringSearchName = "prefix xsd: <http://www.w3.org/2001/XMLSchema#>"
						+ "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>"
						+ "prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>"
						+ "select ?x where { <" + serviceProfile + "> profile:hasInput ?x }";
			} else {
				maker = ModelFactory.createMemModelMaker();
				base = maker.createModel("http://example.org/ontologias");
				m = ModelFactory.createOntologyModel(getModelSpec(maker), base);

				m.read(serviceURL);

				queryStringSearchName = "prefix xsd: <http://www.w3.org/2001/XMLSchema#>"
						+ "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>"
						+ "prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>"
						+ "select ?x where { ?y profile:hasInput ?x }";
			}

			Query querySearchName = QueryFactory.create(queryStringSearchName);

			QueryExecution qeSearchName = QueryExecutionFactory.create(querySearchName, m);
			ResultSet resultsSearchName = qeSearchName.execSelect();

			if (resultsSearchName != null) {

				while (resultsSearchName.hasNext()) {
					String res = resultsSearchName.next().getResource("x").toString();
					inputs.add(res);
				}

			}// end if
			else {
				System.out.println("resultsSearchName is null");
			}

			qeSearchName.close();
			m.close();

		} catch (Exception e) {
			throw new THOMASException(e.getMessage());
		}

		return inputs;
	}

	/**
	 * Returns the inputs parameter type of the given inputs.
	 * 
	 * @param inputs
	 *            names to obtain their parameter type
	 * @param serviceURL
	 *            it specifies the service URL if the query is not to the Jena
	 *            DB. <code>null</code> to query the Jena DB model.
	 * @return the inputs parameter type of the given inputs
	 * @throws THOMASException
	 */
	private ArrayList<String> getInputParameterTypes(ArrayList<String> inputs, String serviceURL)
			throws THOMASException {
		ArrayList<String> inputParamsRegistered = new ArrayList<String>();

		try {

			ModelMaker maker;
			Model base;
			OntModel m;
			if (serviceURL == null) {
				maker = ModelFactory.createModelRDBMaker(conn);
				base = maker.createModel("http://example.org/ontologias");
				m = ModelFactory.createOntologyModel(getModelSpec(maker), base);
			} else {
				maker = ModelFactory.createMemModelMaker();
				base = maker.createModel("http://example.org/ontologias");
				m = ModelFactory.createOntologyModel(getModelSpec(maker), base);
				m.read(serviceURL);
			}

			Iterator<String> iterInputs = inputs.iterator();
			while (iterInputs.hasNext()) {

				String in = iterInputs.next();
				String queryStr = "prefix xsd: <http://www.w3.org/2001/XMLSchema#>"
						+ "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>"
						+ "prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>"
						+ "select ?x where { <" + in + "> a process:Input ; process:parameterType ?x . }";

				Query query = QueryFactory.create(queryStr);

				// Execute the query and obtain results
				QueryExecution querySearchInputs = QueryExecutionFactory.create(query, m);
				ResultSet resultsSearchInputs = querySearchInputs.execSelect();

				if (resultsSearchInputs != null) {

					while (resultsSearchInputs.hasNext()) {
						QuerySolution sol = resultsSearchInputs.next();

						String param = "\""
								+ sol.getLiteral("x").getString()
								+ "\"^^"
								+ sol.getLiteral("x").getDatatypeURI()
										.replace("http://www.w3.org/2001/XMLSchema#", "xsd:");
						// System.out.println(param);
						inputParamsRegistered.add(param);

					}// end for
				}// end if

				// close the query
				querySearchInputs.close();
			}

		} catch (Exception e) {
			throw new THOMASException(e.getMessage());
		}
		return inputParamsRegistered;
	}

	/**
	 * Returns the specification in OWL-S of the given input as a process part
	 * 
	 * @param input
	 *            URI to extract data from
	 * @return the specification in OWL-S of the given input as a process part
	 */
	private String getInputOWLS(String input) {
		String inputOWLS = "";
		String param = "";

		ModelMaker maker = ModelFactory.createModelRDBMaker(conn);
		Model base = maker.createModel("http://example.org/ontologias");
		OntModel m = ModelFactory.createOntologyModel(getModelSpec(maker), base);

		String queryStringSearchName = "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>"
				+ "prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>" + "select ?x where { <"
				+ input + "> a process:Input ; process:parameterType ?x . }";

		Query querySearchName = QueryFactory.create(queryStringSearchName);

		QueryExecution qeSearchName = QueryExecutionFactory.create(querySearchName, m);
		ResultSet resultsSearchName = qeSearchName.execSelect();

		if (resultsSearchName != null) {

			if (resultsSearchName.hasNext()) {
				param = resultsSearchName.next().getLiteral("x").toString();
			}

		}// end if
		else {
			System.out.println("resultsSearchName is null");
		}
		StringTokenizer strToken = new StringTokenizer(param, "^^");
		String paramType = strToken.nextToken();
		String paramDataType = strToken.nextToken();

		inputOWLS = "\t<process:Input rdf:ID=\"" + input + "\">" + "\n" + "\t\t<process:parameterType rdf:datatype=\""
				+ paramDataType + "\">" + paramType + "</process:parameterType>" + "\n" + "\t</process:Input>\n";

		return inputOWLS;

	}

	/**
	 * Returns the outputs of the given service profile
	 * 
	 * @param serviceProfile
	 *            URI to extract data from
	 * @param serviceURL
	 *            it specifies the service URL if the query is not to the Jena
	 *            DB. <code>null</code> to query the Jena DB model.
	 * @return the outputs of the given service profile
	 * @throws THOMASException
	 */
	private ArrayList<String> getOutputs(String serviceProfile, String serviceURL) throws THOMASException {
		ArrayList<String> outputs = new ArrayList<String>();

		try {
			ModelMaker maker;
			Model base;
			OntModel m;
			String queryStringSearchName;
			if (serviceURL == null) {
				maker = ModelFactory.createModelRDBMaker(conn);
				base = maker.createModel("http://example.org/ontologias");
				m = ModelFactory.createOntologyModel(getModelSpec(maker), base);
				queryStringSearchName = "prefix xsd: <http://www.w3.org/2001/XMLSchema#>"
						+ "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>"
						+ "prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>"
						+ "select ?x where { <" + serviceProfile + "> profile:hasOutput ?x }";
			} else {
				maker = ModelFactory.createMemModelMaker();
				base = maker.createModel("http://example.org/ontologias");
				m = ModelFactory.createOntologyModel(getModelSpec(maker), base);
				m.read(serviceURL);
				queryStringSearchName = "prefix xsd: <http://www.w3.org/2001/XMLSchema#>"
						+ "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>"
						+ "prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>"
						+ "select ?x where { ?y profile:hasOutput ?x }";
			}

			Query querySearchName = QueryFactory.create(queryStringSearchName);

			QueryExecution qeSearchName = QueryExecutionFactory.create(querySearchName, m);
			ResultSet resultsSearchName = qeSearchName.execSelect();

			if (resultsSearchName != null) {

				while (resultsSearchName.hasNext()) {
					String res = resultsSearchName.next().getResource("x").toString();
					outputs.add(res);
				}

			}// end if
			else {
				System.out.println("resultsSearchName is null");
			}

			qeSearchName.close();
			m.close();

		} catch (Exception e) {
			throw new THOMASException(e.getMessage());
		}

		return outputs;
	}

	/**
	 * Returns the outputs parameter type of the given outputs.
	 * 
	 * @param outputs
	 *            names to obtain their parameter type
	 * @param serviceURL
	 *            it specifies the service URL if the query is not to the Jena
	 *            DB. <code>null</code> to query the Jena DB model.
	 * @return the outputs parameter type of the given outputs
	 * @throws THOMASException
	 */
	private ArrayList<String> getOutputParameterTypes(ArrayList<String> outputs, String serviceURL)
			throws THOMASException {
		ArrayList<String> outputParamsRegistered = new ArrayList<String>();

		try {

			ModelMaker maker;
			Model base;
			OntModel m;
			if (serviceURL == null) {
				maker = ModelFactory.createModelRDBMaker(conn);
				base = maker.createModel("http://example.org/ontologias");
				m = ModelFactory.createOntologyModel(getModelSpec(maker), base);
			} else {
				maker = ModelFactory.createMemModelMaker();
				base = maker.createModel("http://example.org/ontologias");
				m = ModelFactory.createOntologyModel(getModelSpec(maker), base);
				m.read(serviceURL);
			}

			Iterator<String> iterOutputs = outputs.iterator();
			while (iterOutputs.hasNext()) {

				String out = iterOutputs.next();
				String queryStr = "prefix xsd: <http://www.w3.org/2001/XMLSchema#>"
						+ "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>"
						+ "prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>"
						+ "select ?x where { <" + out + "> a process:Output ; process:parameterType ?x . }";

				Query query = QueryFactory.create(queryStr);

				// Execute the query and obtain results
				QueryExecution querySearchInputs = QueryExecutionFactory.create(query, m);
				ResultSet resultsSearchInputs = querySearchInputs.execSelect();

				if (resultsSearchInputs != null) {

					while (resultsSearchInputs.hasNext()) {
						QuerySolution sol = resultsSearchInputs.next();

						String param = "\""
								+ sol.getLiteral("x").getString()
								+ "\"^^"
								+ sol.getLiteral("x").getDatatypeURI()
										.replace("http://www.w3.org/2001/XMLSchema#", "xsd:");
						outputParamsRegistered.add(param);

					}// end for
				}// end if

				// close the query
				querySearchInputs.close();
			}

		} catch (Exception e) {
			throw new THOMASException(e.getMessage());
		}

		return outputParamsRegistered;
	}

	/**
	 * Returns the specification in OWL-S of the given output
	 * 
	 * @param output
	 *            URI to extract data from
	 * @return the specification in OWL-S of the given output
	 */
	private String getOutputOWLS(String output) {
		String outputOWLS = "";
		String param = "";

		ModelMaker maker = ModelFactory.createModelRDBMaker(conn);
		Model base = maker.createModel("http://example.org/ontologias");
		OntModel m = ModelFactory.createOntologyModel(getModelSpec(maker), base);

		String queryStringSearchName = "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>"
				+ "prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>" + "select ?x where { <"
				+ output + "> a process:Output ; process:parameterType ?x . }";

		Query querySearchName = QueryFactory.create(queryStringSearchName);

		QueryExecution qeSearchName = QueryExecutionFactory.create(querySearchName, m);
		ResultSet resultsSearchName = qeSearchName.execSelect();

		if (resultsSearchName != null) {

			if (resultsSearchName.hasNext()) {
				param = resultsSearchName.next().getLiteral("x").toString();
			}

		}// end if
		else {
			System.out.println("resultsSearchName is null");
		}
		StringTokenizer strToken = new StringTokenizer(param, "^^");
		String paramType = strToken.nextToken();
		String paramDataType = strToken.nextToken();

		outputOWLS = "\t<process:Output rdf:ID=\"" + output + "\">" + "\n"
				+ "\t\t<process:parameterType rdf:datatype=\"" + paramDataType + "\">" + paramType
				+ "</process:parameterType>" + "\n" + "\t</process:Output>";

		return outputOWLS;

	}

	/**
	 * Writes the OWL-S specification of the groundings of the given service URL
	 * in a file given as parameter. This specification is attached (by the
	 * profile specification) to the given registered profile of the Jena DB.
	 * 
	 * @param serviceURL
	 *            URL of the service to extract the groundings
	 * @param registeredProfile
	 *            in the Jena DB to attach the new groundings
	 * @param wsdlToRegister
	 *            wsdl document to register
	 * @param fileName
	 *            to store the OWL-S specification created with groundings
	 */
	private void writeGroundingOWLSFile(String serviceURL, String registeredProfile, String wsdlToRegister,
			String fileName) {

		StringTokenizer tokenProfile = new StringTokenizer(registeredProfile, "#");
		String urlBase = tokenProfile.nextToken();
		String regProfileName = tokenProfile.nextToken();

		try {

			FileWriter fstream = new FileWriter(fileName);
			BufferedWriter out = new BufferedWriter(fstream);

			out.write("<?xml version=\"1.0\" encoding=\"WINDOWS-1252\"?>" + "\n"
					+ "<rdf:RDF  xmlns:owl       = \"http://www.w3.org/2002/07/owl#\"" + "\n"
					+ "xmlns:rdfs      = \"http://www.w3.org/2000/01/rdf-schema#\"" + "\n"
					+ "xmlns:rdf       = \"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"" + "\n"
					+ "xmlns:xsd       = \"http://www.w3.org/2001/XMLSchema#\"" + "\n"
					+ "xmlns:service   = \"http://www.daml.org/services/owl-s/1.1/Service.owl#\"" + "\n"
					+ "xmlns:process   = \"http://www.daml.org/services/owl-s/1.1/Process.owl#\"" + "\n"
					+ "xmlns:profile    = \"http://www.daml.org/services/owl-s/1.1/Profile.owl#\"" + "\n"
					+ "xmlns:grounding = \"http://www.daml.org/services/owl-s/1.1/Grounding.owl#\"" + "\n"
					+ "xmlns:provider = \"http://127.0.0.1/ontology/provider.owl#\"" + "\n" + "xml:base        = \""
					+ urlBase + "\">" + "\n");

			out.write("<profile:Profile rdf:ID=\"" + regProfileName + "\">\n");

			StringTokenizer tokenRegServ = new StringTokenizer(registeredProfile, "#");
			String baseURI = tokenRegServ.nextToken();

			String registeredServiceURI = getServiceURI(registeredProfile, null);
			String profile = getProfileURIfromURL(serviceURL);

			out.write("</profile:Profile>\n");

			String groundOWLS = getGroundingOWLSfromFile(profile, serviceURL, registeredServiceURI, wsdlToRegister,
					baseURI);

			System.out.println("groundOWLS:\n" + groundOWLS);

			out.write(groundOWLS);
			out.write("</rdf:RDF>\n");

			out.close();

		} catch (IOException e) {

			e.printStackTrace();
		}

	}

	/**
	 * Writes the OWL-S specification of the providers of the given service URL
	 * in a file given as parameter. This specification is attached (by the
	 * profile specification) to the given registered profile of the Jena DB.
	 * 
	 * @param serviceURL
	 *            URL of the service to extract the providers
	 * @param registeredProfile
	 *            in the Jena DB to attach the new providers
	 * @param fileName
	 *            to store the OWL-S specification created with the providers
	 */
	private void writeProvidersOWLSFile(String serviceURL, String registeredProfile, String fileName) {

		StringTokenizer tokenProfile = new StringTokenizer(registeredProfile, "#");
		String urlBase = tokenProfile.nextToken();
		String regProfileName = tokenProfile.nextToken();

		try {

			FileWriter fstream = new FileWriter(fileName);
			BufferedWriter out = new BufferedWriter(fstream);

			out.write("<?xml version=\"1.0\" encoding=\"WINDOWS-1252\"?>" + "\n"
					+ "<rdf:RDF  xmlns:owl       = \"http://www.w3.org/2002/07/owl#\"" + "\n"
					+ "xmlns:rdfs      = \"http://www.w3.org/2000/01/rdf-schema#\"" + "\n"
					+ "xmlns:rdf       = \"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"" + "\n"
					+ "xmlns:xsd       = \"http://www.w3.org/2001/XMLSchema#\"" + "\n"
					+ "xmlns:service   = \"http://www.daml.org/services/owl-s/1.1/Service.owl#\"" + "\n"
					+ "xmlns:process   = \"http://www.daml.org/services/owl-s/1.1/Process.owl#\"" + "\n"
					+ "xmlns:profile    = \"http://www.daml.org/services/owl-s/1.1/Profile.owl#\"" + "\n"
					+ "xmlns:grounding = \"http://www.daml.org/services/owl-s/1.1/Grounding.owl#\"" + "\n"
					+ "xmlns:provider = \"http://127.0.0.1/ontology/provider.owl#\"" + "\n" + "xml:base        = \""
					+ urlBase + "\">" + "\n");

			out.write("<profile:Profile rdf:ID=\"" + regProfileName + "\">\n");

			String profile = getProfileURIfromURL(serviceURL);

			// it is not necessary to check if the providers are already
			// registered, Jena does not write them two times
			String providersOWLS = getProvidersOWLS(profile, serviceURL);

			System.out.println("profile=" + profile + "\nProvidersOWLS:\n" + providersOWLS);

			out.write(providersOWLS);
			out.write("</profile:Profile>\n");

			out.write("</rdf:RDF>\n");

			out.close();

		} catch (IOException e) {

			e.printStackTrace();
		}

	}

	/**
	 * Returns the profile URI of the service specification in the given service
	 * URL
	 * 
	 * @param serviceURL
	 *            to extract data from
	 * @return the profile URI of the service specification in the given service
	 *         URL
	 */
	private String getProfileURIfromURL(String serviceURL) {

		ModelMaker maker = ModelFactory.createMemModelMaker();
		Model base = maker.createModel("http://example.org/ontologias");
		OntModel m = ModelFactory.createOntologyModel(getModelSpec(maker), base);

		m.read(serviceURL);

		String queryStringSearchName = "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>"
				+ "prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>"
				+ "select ?x where { ?x a profile:Profile }";

		Query querySearchName = QueryFactory.create(queryStringSearchName);

		QueryExecution qeSearchName = QueryExecutionFactory.create(querySearchName, m);
		ResultSet resultsSearchName = qeSearchName.execSelect();

		String profile = "";

		if (resultsSearchName != null) {

			if (resultsSearchName.hasNext()) {
				profile = resultsSearchName.next().getResource("x").toString();

			}

		}// end if
		else {
			System.out.println("resultsSearchName is null");
		}

		return profile;

	}

	/**
	 * Returns the profile service name of the given service profile
	 * 
	 * @param serviceProfile
	 *            URI to extract data from
	 * @param serviceURL
	 *            it specifies the service URL if the query is not to the Jena
	 *            DB. <code>null</code> to query the Jena DB model.
	 * @return the profile service name of the given service profile
	 * @throws THOMASException
	 */
	private String getProfileServiceName(String serviceProfile, String serviceURL) throws THOMASException {
		String serviceName = "";

		try {

			ModelMaker maker;
			Model base;
			OntModel m;
			String queryStringSearchName;
			if (serviceURL == null) {
				maker = ModelFactory.createModelRDBMaker(conn);
				base = maker.createModel("http://example.org/ontologias");
				m = ModelFactory.createOntologyModel(getModelSpec(maker), base);

				queryStringSearchName = "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>"
						+ "prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>"
						+ "select ?x where { " + "<" + serviceProfile + ">" + " profile:serviceName ?x }";
			} else {
				maker = ModelFactory.createMemModelMaker();
				base = maker.createModel("http://example.org/ontologias");
				m = ModelFactory.createOntologyModel(getModelSpec(maker), base);
				m.read(serviceURL);

				queryStringSearchName = "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>"
						+ "prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>"
						+ "select ?x where { ?y profile:serviceName ?x }";
			}

			Query querySearchName = QueryFactory.create(queryStringSearchName);

			QueryExecution qeSearchName = QueryExecutionFactory.create(querySearchName, m);
			ResultSet resultsSearchName = qeSearchName.execSelect();

			if (resultsSearchName != null) {

				while (resultsSearchName.hasNext()) {
					serviceName = resultsSearchName.next().getLiteral("x").toString();

				}

			}// end if
			else {
				System.out.println("resultsSearchName is null");
			}

		} catch (Exception e) {

			throw new THOMASException(e.toString());
		}
		return serviceName;

	}

	/**
	 * Returns the profile text description of the given service profile
	 * 
	 * @param serviceProfile
	 *            URI to extract data from
	 * @param serviceURL
	 *            it specifies the service URL if the query is not to the Jena
	 *            DB. <code>null</code> to query the Jena DB model.
	 * @return the profile text description of the given service profile
	 */
	private String getProfileTextDescription(String serviceProfile, String serviceURL) {
		String textDescription = "";

		ModelMaker maker;
		Model base;
		OntModel m;

		String queryStringSearchName;

		if (serviceURL == null) {
			maker = ModelFactory.createModelRDBMaker(conn);
			base = maker.createModel("http://example.org/ontologias");
			m = ModelFactory.createOntologyModel(getModelSpec(maker), base);
			queryStringSearchName = "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>"
					+ "prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>" + "select ?x where { "
					+ "<" + serviceProfile + ">" + " profile:textDescription ?x }";
		} else {
			maker = ModelFactory.createMemModelMaker();
			base = maker.createModel("http://example.org/ontologias");
			m = ModelFactory.createOntologyModel(getModelSpec(maker), base);
			m.read(serviceURL);
			queryStringSearchName = "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>"
					+ "prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>"
					+ "select ?x where { ?y profile:textDescription ?x }";
		}

		Query querySearchName = QueryFactory.create(queryStringSearchName);

		QueryExecution qeSearchName = QueryExecutionFactory.create(querySearchName, m);
		ResultSet resultsSearchName = qeSearchName.execSelect();

		if (resultsSearchName != null) {

			while (resultsSearchName.hasNext()) {
				textDescription = resultsSearchName.next().getLiteral("x").toString();

			}

		}// end if
		else {
			System.out.println("resultsSearchName is null");
		}

		return textDescription;

	}

	/**
	 * Returns the profiles text descriptions of all registered services in Jena
	 * DB
	 * 
	 * @return the profiles text descriptions of all registered services in Jena
	 *         DB
	 */
	public HashMap<String, String> getProfilesTextDescriptions() {
		HashMap<String, String> textDescriptions = new HashMap<String, String>();

		ModelMaker maker = ModelFactory.createModelRDBMaker(conn);
		Model base = maker.createModel("http://example.org/ontologias");
		OntModel m = ModelFactory.createOntologyModel(getModelSpec(maker), base);
		String queryStringSearchName = "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>"
				+ "prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>"
				+ "select * where { ?y profile:textDescription ?x }";

		Query querySearchName = QueryFactory.create(queryStringSearchName);

		QueryExecution qeSearchName = QueryExecutionFactory.create(querySearchName, m);
		ResultSet resultsSearchName = qeSearchName.execSelect();

		if (resultsSearchName != null) {

			while (resultsSearchName.hasNext()) {
				QuerySolution qS = resultsSearchName.next();
				String textDescription = qS.getLiteral("x").toString();
				String profile = qS.getResource("y").toString();
				textDescriptions.put(profile, textDescription);

			}

		}// end if
		else {
			System.out.println("resultsSearchName is null");
		}

		return textDescriptions;

	}

	/**
	 * Returns the service URI
	 * 
	 * @param serviceProfile
	 *            URI to extract data from
	 * @param serviceURL
	 *            it specifies the service URL if the query is not to the Jena
	 *            DB. <code>null</code> to query the Jena DB model.
	 * @return the service URI
	 */
	private String getServiceURI(String serviceProfile, String serviceURL) {
		String serviceURI = "";

		ModelMaker maker;
		Model base;
		OntModel m;
		if (serviceURL == null) {
			maker = ModelFactory.createModelRDBMaker(conn);
			base = maker.createModel("http://example.org/ontologias");
			m = ModelFactory.createOntologyModel(getModelSpec(maker), base);
		} else {
			maker = ModelFactory.createMemModelMaker();
			base = maker.createModel("http://example.org/ontologias");
			m = ModelFactory.createOntologyModel(getModelSpec(maker), base);
			m.read(serviceURL);
		}

		String queryStringSearchName = "prefix service: <http://www.daml.org/services/owl-s/1.1/Service.owl#>"
				+ "select ?x where { ?x service:presents <" + serviceProfile + "> }";

		Query querySearchName = QueryFactory.create(queryStringSearchName);

		QueryExecution qeSearchName = QueryExecutionFactory.create(querySearchName, m);
		ResultSet resultsSearchName = qeSearchName.execSelect();

		if (resultsSearchName != null) {

			if (resultsSearchName.hasNext()) {
				serviceURI = resultsSearchName.next().getResource("x").toString();
			}

		}// end if
		else {
			System.out.println("resultsSearchName is null");
		}

		qeSearchName.close();
		m.close();

		return serviceURI;
	}

	/**
	 * Returns the service process URI of the given service profile
	 * 
	 * @param serviceProfile
	 *            URI to extract data from
	 * @param serviceURL
	 *            it specifies the service URL if the query is not to the Jena
	 *            DB. <code>null</code> to query the Jena DB model
	 * @return the service process URI of the given service profile
	 */
	private String getServiceProcess(String serviceProfile, String serviceURL) {
		String serviceProcess = "";

		ModelMaker maker;
		Model base;
		OntModel m;
		if (serviceURL == null) {
			maker = ModelFactory.createModelRDBMaker(conn);
			base = maker.createModel("http://example.org/ontologias");
			m = ModelFactory.createOntologyModel(getModelSpec(maker), base);
		} else {
			maker = ModelFactory.createMemModelMaker();
			base = maker.createModel("http://example.org/ontologias");
			m = ModelFactory.createOntologyModel(getModelSpec(maker), base);
			m.read(serviceURL);
		}

		String queryStringSearchName = "prefix service: <http://www.daml.org/services/owl-s/1.1/Service.owl#>"
				+ "prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>"
				+ "select ?x where { ?x  a process:AtomicProcess }";

		Query querySearchName = QueryFactory.create(queryStringSearchName);

		QueryExecution qeSearchName = QueryExecutionFactory.create(querySearchName, m);
		ResultSet resultsSearchName = qeSearchName.execSelect();

		if (resultsSearchName != null) {

			if (resultsSearchName.hasNext()) {
				serviceProcess = resultsSearchName.next().getResource("x").toString();
			}

		}// end if
		else {
			System.out.println("resultsSearchName is null");
		}

		qeSearchName.close();
		m.close();

		return serviceProcess;
	}

	/**
	 * Returns an {@link ArrayList} with the grounding URIs of the given service
	 * URI
	 * 
	 * @param serviceURI
	 *            to extract data from
	 * @param serviceURL
	 *            it specifies the service URL if the query is not to the Jena
	 *            DB. <code>null</code> to query the Jena DB model
	 * @return an {@link ArrayList} with the grounding URIs of the given service
	 *         URI
	 */
	private ArrayList<String> getGroundings(String serviceURI, String serviceURL) {
		ArrayList<String> groundings = new ArrayList<String>();

		ModelMaker maker;
		Model base;
		OntModel m;
		if (serviceURL == null) {
			maker = ModelFactory.createModelRDBMaker(conn);
			base = maker.createModel("http://example.org/ontologias");
			m = ModelFactory.createOntologyModel(getModelSpec(maker), base);
		} else {
			maker = ModelFactory.createMemModelMaker();
			base = maker.createModel("http://example.org/ontologias");
			m = ModelFactory.createOntologyModel(getModelSpec(maker), base);
			m.read(serviceURL);
		}

		String queryStringSearchName = "prefix service: <http://www.daml.org/services/owl-s/1.1/Service.owl#>"
				+ "select ?x where { ?x service:supportedBy <" + serviceURI + "> }";

		Query querySearchName = QueryFactory.create(queryStringSearchName);

		QueryExecution qeSearchName = QueryExecutionFactory.create(querySearchName, m);
		ResultSet resultsSearchName = qeSearchName.execSelect();

		if (resultsSearchName != null) {

			while (resultsSearchName.hasNext()) {
				String ground = resultsSearchName.next().getResource("x").toString();
				groundings.add(ground);
			}

		}// end if
		else {
			System.out.println("resultsSearchName is null");
		}

		qeSearchName.close();
		m.close();

		return groundings;
	}

	/**
	 * Returns the atomic process grounding URI of the given grounding URI
	 * 
	 * @param groundingURI
	 *            to extract data from
	 * @param serviceURL
	 *            it specifies the service URL if the query is not to the Jena
	 *            DB. <code>null</code> to query the Jena DB model
	 * @return the atomic process grounding URI of the given grounding URI
	 */
	private String getAtomicProcessGrounding(String groundingURI, String serviceURL) {
		String atomicProcessGrounding = "";

		ModelMaker maker;
		Model base;
		OntModel m;
		if (serviceURL == null) {
			maker = ModelFactory.createModelRDBMaker(conn);
			base = maker.createModel("http://example.org/ontologias");
			m = ModelFactory.createOntologyModel(getModelSpec(maker), base);
		} else {
			maker = ModelFactory.createMemModelMaker();
			base = maker.createModel("http://example.org/ontologias");
			m = ModelFactory.createOntologyModel(getModelSpec(maker), base);
			m.read(serviceURL);
		}

		String queryStringSearchName = "prefix grounding: <http://www.daml.org/services/owl-s/1.1/Grounding.owl#>"
				+ "select ?x where { <" + groundingURI + "> grounding:hasAtomicProcessGrounding ?x }";

		Query querySearchName = QueryFactory.create(queryStringSearchName);

		QueryExecution qeSearchName = QueryExecutionFactory.create(querySearchName, m);
		ResultSet resultsSearchName = qeSearchName.execSelect();

		if (resultsSearchName != null) {

			if (resultsSearchName.hasNext()) {
				atomicProcessGrounding = resultsSearchName.next().getResource("x").toString();
			}

		}// end if
		else {
			System.out.println("resultsSearchName is null");
		}

		qeSearchName.close();
		m.close();

		return atomicProcessGrounding;
	}

	/**
	 * Returns the WSDL document URI of the given atomic process grounding URI
	 * 
	 * @param atomicProcessGrounding
	 *            URI to extract data from
	 * @param serviceURL
	 *            it specifies the service URL if the query is not to the Jena
	 *            DB. <code>null</code> to query the Jena DB model
	 * @return the WSDL document URI of the given atomic process grounding URI
	 */
	private String getWSDLDocument(String atomicProcessGrounding, String serviceURL) {
		String WSDLDoc = "";

		ModelMaker maker;
		Model base;
		OntModel m;
		if (serviceURL == null) {
			maker = ModelFactory.createModelRDBMaker(conn);
			base = maker.createModel("http://example.org/ontologias");
			m = ModelFactory.createOntologyModel(getModelSpec(maker), base);
		} else {
			maker = ModelFactory.createMemModelMaker();
			base = maker.createModel("http://example.org/ontologias");
			m = ModelFactory.createOntologyModel(getModelSpec(maker), base);
			m.read(serviceURL);
		}

		String queryStringSearchName = "prefix grounding: <http://www.daml.org/services/owl-s/1.1/Grounding.owl#>"
				+ "select ?x where { <" + atomicProcessGrounding + "> grounding:wsdlDocument ?x }";

		Query querySearchName = QueryFactory.create(queryStringSearchName);

		QueryExecution qeSearchName = QueryExecutionFactory.create(querySearchName, m);
		ResultSet resultsSearchName = qeSearchName.execSelect();

		if (resultsSearchName != null) {

			if (resultsSearchName.hasNext()) {
				WSDLDoc = resultsSearchName.next().getLiteral("x").toString();
			}

		}// end if
		else {
			System.out.println("resultsSearchName is null");
		}

		qeSearchName.close();
		m.close();

		return WSDLDoc;
	}

	/**
	 * Returns the WSDL document URI of the given service URL
	 * 
	 * @param serviceURL
	 *            to extract data from
	 * @return the WSDL document URI of the given service URL
	 */
	private ArrayList<String> getWSDLDocumentFromServiceURL(String serviceURL) {
		ArrayList<String> WSDLDocs = new ArrayList<String>();

		ModelMaker maker = ModelFactory.createMemModelMaker();
		Model base = maker.createModel("http://example.org/ontologias");
		// now we plug that base model into an ontology model that also uses
		// the given model maker to create storage for imported models
		OntModelSpec spec = new OntModelSpec(OntModelSpec.OWL_MEM);
		spec.setImportModelMaker(maker);
		OntModel m = ModelFactory.createOntologyModel(spec, base);
		// load the service profile in the database
		m.read(serviceURL);

		String queryStringSearchName = "prefix grounding: <http://www.daml.org/services/owl-s/1.1/Grounding.owl#>"
				+ "select ?x where { ?y grounding:wsdlDocument ?x }";

		Query querySearchName = QueryFactory.create(queryStringSearchName);

		QueryExecution qeSearchName = QueryExecutionFactory.create(querySearchName, m);
		ResultSet resultsSearchName = qeSearchName.execSelect();

		if (resultsSearchName != null) {

			while (resultsSearchName.hasNext()) {
				String wsdldoc = resultsSearchName.next().getLiteral("x").toString();
				WSDLDocs.add(wsdldoc);

			}

		}// end if
		else {
			System.out.println("resultsSearchName is null");
		}

		qeSearchName.close();
		m.close();

		return WSDLDocs;
	}

	/**
	 * Returns the atomic grounding URI URI of the given service URL and WSDL
	 * document
	 * 
	 * @param WSDLDoc
	 *            that identifies the grounding
	 * @param serviceURL
	 *            to extract data from
	 * @return the atomic grounding URI URI of the given service URL and WSDL
	 *         document
	 */
	private String getAtomicGroundingURIFromWSDLDocumentFromServiceURL(String WSDLDoc, String serviceURL) {
		String atomicGroundingURI = "";

		ModelMaker maker = ModelFactory.createMemModelMaker();
		Model base = maker.createModel("http://example.org/ontologias");
		// now we plug that base model into an ontology model that also uses
		// the given model maker to create storage for imported models
		OntModelSpec spec = new OntModelSpec(OntModelSpec.OWL_MEM);
		spec.setImportModelMaker(maker);
		OntModel m = ModelFactory.createOntologyModel(spec, base);
		// load the service profile in the database
		m.read(serviceURL);

		String queryStringSearchName = "prefix grounding: <http://www.daml.org/services/owl-s/1.1/Grounding.owl#>"
				+ "prefix xsd: <http://www.w3.org/2001/XMLSchema#>" + "select ?x where { ?x grounding:wsdlDocument "
				+ WSDLDoc + " }";

		Query querySearchName = QueryFactory.create(queryStringSearchName);

		QueryExecution qeSearchName = QueryExecutionFactory.create(querySearchName, m);
		ResultSet resultsSearchName = qeSearchName.execSelect();

		if (resultsSearchName != null) {

			if (resultsSearchName.hasNext()) {
				String wsdldoc = resultsSearchName.next().getResource("x").toString();
				atomicGroundingURI = wsdldoc;

			}

		}// end if
		else {
			System.out.println("resultsSearchName is null");
		}

		qeSearchName.close();
		m.close();

		return atomicGroundingURI;
	}

	/**
	 * Returns the grounding URI URI of the given service URL and atomic
	 * grounding
	 * 
	 * @param atomicGroundingURI
	 * @param serviceURL
	 *            to extract data from
	 * @return the atomic grounding URI URI of the given service URL and WSDL
	 *         document
	 */
	private String getGroundingURIFromAtomicGrounding(String atomicGroundingURI, String serviceURL) {
		String groundingURI = "";

		ModelMaker maker = ModelFactory.createMemModelMaker();
		Model base = maker.createModel("http://example.org/ontologias");
		// now we plug that base model into an ontology model that also uses
		// the given model maker to create storage for imported models
		OntModelSpec spec = new OntModelSpec(OntModelSpec.OWL_MEM);
		spec.setImportModelMaker(maker);
		OntModel m = ModelFactory.createOntologyModel(spec, base);
		// load the service profile in the database
		m.read(serviceURL);

		String queryStringSearchName = "prefix grounding: <http://www.daml.org/services/owl-s/1.1/Grounding.owl#>"
				+ "prefix xsd: <http://www.w3.org/2001/XMLSchema#>"
				+ "select ?x where { ?x grounding:hasAtomicProcessGrounding <" + atomicGroundingURI + "> }";

		Query querySearchName = QueryFactory.create(queryStringSearchName);

		QueryExecution qeSearchName = QueryExecutionFactory.create(querySearchName, m);
		ResultSet resultsSearchName = qeSearchName.execSelect();

		if (resultsSearchName != null) {

			if (resultsSearchName.hasNext()) {
				groundingURI = resultsSearchName.next().getResource("x").toString();
			}

		}// end if
		else {
			System.out.println("resultsSearchName is null");
		}

		qeSearchName.close();
		m.close();

		return groundingURI;
	}

	/**
	 * Returns a String containing the specification in OWL-S of all groundings
	 * of the given service profile
	 * 
	 * @param serviceProfile
	 *            URI to extract data from
	 * @return a String containing the specification in OWL-S of all groundings
	 *         of the given service profile
	 */
	private String getGroundingsOWLS(String serviceProfile) {
		String groundsOWLS = "";
		String serviceURI = getServiceURI(serviceProfile, null);
		ArrayList<String> groundings = getGroundings(serviceURI, null);
		Iterator<String> iterGrounds = groundings.iterator();
		while (iterGrounds.hasNext()) {
			String groundingURI = iterGrounds.next();
			String atomicProcessGrounding = getAtomicProcessGrounding(groundingURI, null);
			String WSDLDocandDatatype = getWSDLDocument(atomicProcessGrounding, null);
			StringTokenizer token = new StringTokenizer(WSDLDocandDatatype, "^^");
			String wsdlDoc = token.nextToken();
			String wsdlDatatype = token.nextToken();
			StringTokenizer tokenGroundURI = new StringTokenizer(groundingURI, "#");
			tokenGroundURI.nextToken();
			String groundingURIName = tokenGroundURI.nextToken();

			StringTokenizer tokenAtomicProcess = new StringTokenizer(atomicProcessGrounding, "#");
			tokenAtomicProcess.nextToken();
			String atomicProcessGroundingName = tokenAtomicProcess.nextToken();// baseURI+"#"+tokenAtomicProcess.nextToken();

			groundsOWLS += "<grounding:WsdlGrounding rdf:ID=\""
					+ groundingURIName
					+ "\">\n"
					+ // "<grounding:WsdlGrounding rdf:ID=\""+baseURI+"#"+groundingURIName+"\">\n"+
					"\t<service:supportedBy rdf:resource=\"" + "#" + serviceURI + "\"/> \n"
					+ "\t<grounding:hasAtomicProcessGrounding>\n" + "\t<grounding:WsdlAtomicProcessGrounding rdf:ID=\""
					+ atomicProcessGroundingName + "\"/>" + "\t</grounding:hasAtomicProcessGrounding>\n"
					+ "</grounding:WsdlGrounding>\n";

			groundsOWLS += "<grounding:WsdlAtomicProcessGrounding rdf:about=\"" + "#" + atomicProcessGroundingName
					+ "\">\n" + "\t<grounding:wsdlDocument rdf:datatype=\"" + wsdlDatatype + "\"\n>" + // the
																										// symbol
																										// ">"
																										// must
																										// go
																										// after
																										// the
																										// "\n",
					// if not, when Jena adds the info to the DB puts some ""
					// and spaces that causes problems to queries
					wsdlDoc + "</grounding:wsdlDocument>\n";
			String owlsProcess = getGroundOwlsProcess(atomicProcessGrounding, null);
			groundsOWLS += "\t<grounding:owlsProcess rdf:resource=\"" + owlsProcess + "\"/>\n";

			String groundInputMessage = getGroundingInputMessage(atomicProcessGrounding, null);
			StringTokenizer inputMessageTok = new StringTokenizer(groundInputMessage, "^^");
			String inputMessage = inputMessageTok.nextToken();
			String inputMessageDatatype = inputMessageTok.nextToken();
			groundsOWLS += "\t<grounding:wsdlInputMessage rdf:datatype=\"" + inputMessageDatatype + "\">"
					+ inputMessage + "</grounding:wsdlInputMessage>\n";
			StringTokenizer outputMessageTok = new StringTokenizer(getGroundingOutputMessage(atomicProcessGrounding,
					null), "^^");
			String outputMessage = outputMessageTok.nextToken();
			String outputMessageDatatype = outputMessageTok.nextToken();
			groundsOWLS += "\t<grounding:wsdlOutputMessage rdf:datatype=\"" + outputMessageDatatype + "\">"
					+ outputMessage + "</grounding:wsdlOutputMessage>\n";

			ArrayList<GroundingInOutput> groundInputs = getGroundingInOutputs(atomicProcessGrounding, null, true);
			Iterator<GroundingInOutput> iterIns = groundInputs.iterator();
			while (iterIns.hasNext()) {
				GroundingInOutput in = iterIns.next();

				groundsOWLS += "\t<grounding:wsdlInput>\n\t\t<grounding:WsdlInputMessageMap>\n";
				groundsOWLS += "\t\t<grounding:owlsParameter rdf:resource=\"" + in.getOwlsParameter() + "\"/>\n";

				StringTokenizer messagePartTok = new StringTokenizer(in.getWsdlMessagePart(), "^^");
				String messagePart = messagePartTok.nextToken();
				String messagePartDatatype = messagePartTok.nextToken();
				groundsOWLS += "\t\t<grounding:wsdlMessagePart rdf:datatype=\"" + messagePartDatatype + "\">"
						+ messagePart + "</grounding:wsdlMessagePart>\n";

				groundsOWLS += "\t\t<grounding:xsltTransformationString>" + in.getXsltTransformationString()
						+ "</grounding:xsltTransformationString>\n";
				groundsOWLS += "\t\t</grounding:WsdlInputMessageMap>\n\t</grounding:wsdlInput>\n";
			}

			ArrayList<GroundingInOutput> groundOutputs = getGroundingInOutputs(atomicProcessGrounding, null, false);
			Iterator<GroundingInOutput> iterOuts = groundOutputs.iterator();
			while (iterOuts.hasNext()) {
				GroundingInOutput out = iterOuts.next();

				groundsOWLS += "\t<grounding:wsdlOutput>\n\t\t<grounding:WsdlOutputMessageMap>\n";
				groundsOWLS += "\t\t<grounding:owlsParameter rdf:resource=\"" + out.getOwlsParameter() + "\"/>\n";

				StringTokenizer messagePartTok = new StringTokenizer(out.getWsdlMessagePart(), "^^");
				String messagePart = messagePartTok.nextToken();
				String messagePartDatatype = messagePartTok.nextToken();
				groundsOWLS += "\t\t<grounding:wsdlMessagePart rdf:datatype=\"" + messagePartDatatype + "\">"
						+ messagePart + "</grounding:wsdlMessagePart>\n";

				groundsOWLS += "\t\t<grounding:xsltTransformationString>" + out.getXsltTransformationString()
						+ "</grounding:xsltTransformationString>\n";
				groundsOWLS += "\t\t</grounding:WsdlOutputMessageMap>\n\t</grounding:wsdlOutput>\n";
			}

			String groundOperation = getGroundOperation(atomicProcessGrounding, null);
			StringTokenizer operationTok = new StringTokenizer(groundOperation, "^^");
			String operation = operationTok.nextToken();
			String operationDatatype = operationTok.nextToken();
			groundsOWLS += "\t<grounding:wsdlOperation>\n\t<grounding:WsdlOperationRef>\n";
			groundsOWLS += "\t<grounding:operation rdf:datatype=\"" + operationDatatype + "\">" + operation
					+ "</grounding:operation>\n";

			String groundPortType = getGroundPortType(atomicProcessGrounding, null);
			StringTokenizer portTypeTok = new StringTokenizer(groundPortType, "^^");
			String portType = portTypeTok.nextToken();
			String portTypeDatatype = portTypeTok.nextToken();
			groundsOWLS += "\t<grounding:portType rdf:datatype=\"" + portTypeDatatype + "\">" + portType
					+ "</grounding:portType>\n";
			groundsOWLS += "\t</grounding:WsdlOperationRef>\n\t</grounding:wsdlOperation>\n";

			groundsOWLS += "</grounding:WsdlAtomicProcessGrounding>\n";

		}

		return groundsOWLS;
	}

	/**
	 * Returns a {@link String} containing the specification in OWL-S of all
	 * groundings of the given service URL
	 * 
	 * @param serviceProfile
	 *            URI to extract data from
	 * @param serviceURL
	 *            where the orginal OWL-S specification of the service is
	 * @param registeredServiceURI
	 *            the service URI of the corresponding registered service in the
	 *            Jena DB
	 * @param wsdlToRegister
	 *            wsdl that represents the grounding
	 * @param baseURI
	 *            of the registered service in the Jena DB
	 * @return a {@link String} containing the specification in OWL-S of all
	 *         groundings of the given service URL
	 */
	private String getGroundingOWLSfromFile(String serviceProfile, String serviceURL, String registeredServiceURI,
			String wsdlToRegister, String baseURI) {
		String groundsOWLS = "";

		String atomicProcessGrounding = getAtomicGroundingURIFromWSDLDocumentFromServiceURL(wsdlToRegister, serviceURL);
		String groundingURI = getGroundingURIFromAtomicGrounding(atomicProcessGrounding, serviceURL);

		String WSDLDocandDatatype = getWSDLDocument(atomicProcessGrounding, serviceURL);
		StringTokenizer token = new StringTokenizer(WSDLDocandDatatype, "^^");
		String wsdlDoc = token.nextToken();
		String wsdlDatatype = token.nextToken();
		StringTokenizer tokenGroundURI = new StringTokenizer(groundingURI, "#");
		tokenGroundURI.nextToken();
		String groundingURIName = tokenGroundURI.nextToken();

		StringTokenizer tokenRegServURI = new StringTokenizer(registeredServiceURI, "#");
		tokenRegServURI.nextToken();
		String registeredServiceURIName = tokenRegServURI.nextToken();

		StringTokenizer tokenAtomicProcess = new StringTokenizer(atomicProcessGrounding, "#");
		tokenAtomicProcess.nextToken();
		String atomicProcessGroundingName = tokenAtomicProcess.nextToken();// baseURI+"#"+tokenAtomicProcess.nextToken();

		groundsOWLS += "<grounding:WsdlGrounding rdf:ID=\""
				+ groundingURIName
				+ "\">\n"
				+ // "<grounding:WsdlGrounding rdf:ID=\""+baseURI+"#"+groundingURIName+"\">\n"+
				"<service:supportedBy rdf:resource=\"" + "#" + registeredServiceURIName + "\"/> \n"
				+ "<grounding:hasAtomicProcessGrounding>\n" + "<grounding:WsdlAtomicProcessGrounding rdf:ID=\""
				+ atomicProcessGroundingName + "\"/>" + "</grounding:hasAtomicProcessGrounding>\n"
				+ "</grounding:WsdlGrounding>\n";

		groundsOWLS += "<grounding:WsdlAtomicProcessGrounding rdf:about=\"" + "#" + atomicProcessGroundingName
				+ "\">\n" + "<grounding:wsdlDocument rdf:datatype=\"" + wsdlDatatype + "\"\n>" + // the
																									// symbol
																									// ">"
																									// must
																									// go
																									// after
																									// the
																									// "\n",
				// if not, when Jena adds the info to the DB puts some "" and
				// spaces that causes problems to queries
				wsdlDoc + "</grounding:wsdlDocument>\n";
		String owlsProcess = getGroundOwlsProcess(atomicProcessGrounding, serviceURL);
		groundsOWLS += "<grounding:owlsProcess rdf:resource=\"" + owlsProcess + "\"/>\n";

		String groundInputMessage = getGroundingInputMessage(atomicProcessGrounding, serviceURL);
		StringTokenizer inputMessageTok = new StringTokenizer(groundInputMessage, "^^");
		String inputMessage = inputMessageTok.nextToken();
		String inputMessageDatatype = inputMessageTok.nextToken();
		groundsOWLS += "<grounding:wsdlInputMessage rdf:datatype=\"" + inputMessageDatatype + "\">" + inputMessage
				+ "</grounding:wsdlInputMessage>\n";
		StringTokenizer outputMessageTok = new StringTokenizer(getGroundingOutputMessage(atomicProcessGrounding,
				serviceURL), "^^");
		String outputMessage = outputMessageTok.nextToken();
		String outputMessageDatatype = outputMessageTok.nextToken();
		groundsOWLS += "<grounding:wsdlOutputMessage rdf:datatype=\"" + outputMessageDatatype + "\">" + outputMessage
				+ "</grounding:wsdlOutputMessage>\n";

		ArrayList<GroundingInOutput> groundInputs = getGroundingInOutputs(atomicProcessGrounding, serviceURL, true);
		Iterator<GroundingInOutput> iterIns = groundInputs.iterator();
		while (iterIns.hasNext()) {
			GroundingInOutput in = iterIns.next();

			groundsOWLS += "<grounding:wsdlInput>\n<grounding:WsdlInputMessageMap>\n";
			groundsOWLS += "<grounding:owlsParameter rdf:resource=\"" + in.getOwlsParameter() + "\"/>\n";

			StringTokenizer messagePartTok = new StringTokenizer(in.getWsdlMessagePart(), "^^");
			String messagePart = messagePartTok.nextToken();
			String messagePartDatatype = messagePartTok.nextToken();
			groundsOWLS += "<grounding:wsdlMessagePart rdf:datatype=\"" + messagePartDatatype + "\">" + messagePart
					+ "</grounding:wsdlMessagePart>\n";

			groundsOWLS += "<grounding:xsltTransformationString>" + in.getXsltTransformationString()
					+ "</grounding:xsltTransformationString>\n";
			groundsOWLS += "</grounding:WsdlInputMessageMap>\n</grounding:wsdlInput>\n";
		}

		ArrayList<GroundingInOutput> groundOutputs = getGroundingInOutputs(atomicProcessGrounding, serviceURL, false);
		Iterator<GroundingInOutput> iterOuts = groundOutputs.iterator();
		while (iterOuts.hasNext()) {
			GroundingInOutput out = iterOuts.next();

			groundsOWLS += "<grounding:wsdlOutput>\n<grounding:WsdlOutputMessageMap>\n";
			groundsOWLS += "<grounding:owlsParameter rdf:resource=\"" + out.getOwlsParameter() + "\"/>\n";

			StringTokenizer messagePartTok = new StringTokenizer(out.getWsdlMessagePart(), "^^");
			String messagePart = messagePartTok.nextToken();
			String messagePartDatatype = messagePartTok.nextToken();
			groundsOWLS += "<grounding:wsdlMessagePart rdf:datatype=\"" + messagePartDatatype + "\">" + messagePart
					+ "</grounding:wsdlMessagePart>\n";

			groundsOWLS += "<grounding:xsltTransformationString>" + out.getXsltTransformationString()
					+ "</grounding:xsltTransformationString>\n";
			groundsOWLS += "</grounding:WsdlOutputMessageMap>\n</grounding:wsdlOutput>\n";
		}

		String groundOperation = getGroundOperation(atomicProcessGrounding, serviceURL);
		StringTokenizer operationTok = new StringTokenizer(groundOperation, "^^");
		String operation = operationTok.nextToken();
		String operationDatatype = operationTok.nextToken();
		groundsOWLS += "<grounding:wsdlOperation>\n<grounding:WsdlOperationRef>\n";
		groundsOWLS += "<grounding:operation rdf:datatype=\"" + operationDatatype + "\">" + operation
				+ "</grounding:operation>\n";

		String groundPortType = getGroundPortType(atomicProcessGrounding, serviceURL);
		StringTokenizer portTypeTok = new StringTokenizer(groundPortType, "^^");
		String portType = portTypeTok.nextToken();
		String portTypeDatatype = portTypeTok.nextToken();
		groundsOWLS += "<grounding:portType rdf:datatype=\"" + portTypeDatatype + "\">" + portType
				+ "</grounding:portType>\n";
		groundsOWLS += "</grounding:WsdlOperationRef>\n</grounding:wsdlOperation>\n";

		groundsOWLS += "</grounding:WsdlAtomicProcessGrounding>\n";

		return groundsOWLS;
	}

	/**
	 * Returns the grounding input message of the given atomic process grounding
	 * URI
	 * 
	 * @param atomicProcessGrounding
	 *            URI to extract data from
	 * @param serviceURL
	 *            it specifies the service URL if the query is not to the Jena
	 *            DB. <code>null</code> to query the Jena DB model
	 * @return the grounding input message of the given atomic process grounding
	 *         URI
	 */
	private String getGroundingInputMessage(String atomicProcessGrounding, String serviceURL) {
		String inputMessage = "";
		ModelMaker maker;
		Model base;
		OntModel m;
		if (serviceURL == null) {
			maker = ModelFactory.createModelRDBMaker(conn);
			base = maker.createModel("http://example.org/ontologias");
			m = ModelFactory.createOntologyModel(getModelSpec(maker), base);
		} else {
			maker = ModelFactory.createMemModelMaker();
			base = maker.createModel("http://example.org/ontologias");
			m = ModelFactory.createOntologyModel(getModelSpec(maker), base);
			m.read(serviceURL);
		}

		String queryStringSearchName = "prefix grounding: <http://www.daml.org/services/owl-s/1.1/Grounding.owl#>"
				+ "select ?x where { <" + atomicProcessGrounding + "> grounding:wsdlInputMessage ?x }";

		Query querySearchName = QueryFactory.create(queryStringSearchName);

		QueryExecution qeSearchName = QueryExecutionFactory.create(querySearchName, m);
		ResultSet resultsSearchName = qeSearchName.execSelect();

		if (resultsSearchName != null) {

			if (resultsSearchName.hasNext()) {
				inputMessage = resultsSearchName.next().getLiteral("x").toString();
			}

		}// end if
		else {
			System.out.println("resultsSearchName is null");
		}

		qeSearchName.close();
		m.close();

		return inputMessage;
	}

	/**
	 * Returns the grounding output message of the given atomic process
	 * grounding URI
	 * 
	 * @param atomicProcessGrounding
	 *            URI to extract data from
	 * @param serviceURL
	 *            it specifies the service URL if the query is not to the Jena
	 *            DB. <code>null</code> to query the Jena DB model
	 * @return the grounding output message of the given atomic process
	 *         grounding URI
	 */
	private String getGroundingOutputMessage(String atomicProcessGrounding, String serviceURL) {
		String outputMessage = "";
		ModelMaker maker;
		Model base;
		OntModel m;
		if (serviceURL == null) {
			maker = ModelFactory.createModelRDBMaker(conn);
			base = maker.createModel("http://example.org/ontologias");
			m = ModelFactory.createOntologyModel(getModelSpec(maker), base);
		} else {
			maker = ModelFactory.createMemModelMaker();
			base = maker.createModel("http://example.org/ontologias");
			m = ModelFactory.createOntologyModel(getModelSpec(maker), base);
			m.read(serviceURL);
		}

		String queryStringSearchName = "prefix grounding: <http://www.daml.org/services/owl-s/1.1/Grounding.owl#>"
				+ "select ?x where { <" + atomicProcessGrounding + "> grounding:wsdlOutputMessage ?x }";

		Query querySearchName = QueryFactory.create(queryStringSearchName);

		QueryExecution qeSearchName = QueryExecutionFactory.create(querySearchName, m);
		ResultSet resultsSearchName = qeSearchName.execSelect();

		if (resultsSearchName != null) {

			if (resultsSearchName.hasNext()) {
				outputMessage = resultsSearchName.next().getLiteral("x").toString();
			}

		}// end if
		else {
			System.out.println("resultsSearchName is null");
		}

		qeSearchName.close();
		m.close();

		return outputMessage;
	}

	/**
	 * Returns the grounding owls process of the given atomic process grounding
	 * 
	 * @param atomicProcessGrounding
	 *            URI to extract data from
	 * @param serviceURL
	 *            it specifies the service URL if the query is not to the Jena
	 *            DB. <code>null</code> to query the Jena DB model
	 * @return the grounding owls process of the given atomic process grounding
	 */
	private String getGroundOwlsProcess(String atomicProcessGrounding, String serviceURL) {

		String owlsProcess = "";
		ModelMaker maker;
		Model base;
		OntModel m;
		if (serviceURL == null) {
			maker = ModelFactory.createModelRDBMaker(conn);
			base = maker.createModel("http://example.org/ontologias");
			m = ModelFactory.createOntologyModel(getModelSpec(maker), base);
		} else {
			maker = ModelFactory.createMemModelMaker();
			base = maker.createModel("http://example.org/ontologias");
			m = ModelFactory.createOntologyModel(getModelSpec(maker), base);
			m.read(serviceURL);
		}

		String queryStringSearchName = "prefix grounding: <http://www.daml.org/services/owl-s/1.1/Grounding.owl#>"
				+ "select ?x where { <" + atomicProcessGrounding + "> grounding:owlsProcess ?x }";

		Query querySearchName = QueryFactory.create(queryStringSearchName);

		QueryExecution qeSearchName = QueryExecutionFactory.create(querySearchName, m);
		ResultSet resultsSearchName = qeSearchName.execSelect();

		if (resultsSearchName != null) {

			if (resultsSearchName.hasNext()) {
				owlsProcess = resultsSearchName.next().getResource("x").toString();
			}

		}// end if
		else {
			System.out.println("resultsSearchName is null");
		}

		qeSearchName.close();
		m.close();

		return owlsProcess;

	}

	/**
	 * Returns an {@link ArrayList} of {@link GroundingInOutput} with the data
	 * of the grounding inputs or outputs of the given atomic process grounding
	 * 
	 * @param atomicProcessGrounding
	 *            URI to extract data from
	 * @param serviceURL
	 *            it specifies the service URL if the query is not to the Jena
	 *            DB. <code>null</code> to query the Jena DB model
	 * @param input
	 *            <code>true</code> to extract the data of the inputs,
	 *            <code>false</code> to extract the data of the outputs
	 * @return an {@link ArrayList} of {@link GroundingInOutput} with the data
	 *         of the grounding inputs or outputs of the given atomic process
	 *         grounding
	 */
	private ArrayList<GroundingInOutput> getGroundingInOutputs(String atomicProcessGrounding, String serviceURL,
			boolean input) {

		ArrayList<GroundingInOutput> groundInputs = new ArrayList<GroundingInOutput>();

		ModelMaker maker;
		Model base;
		OntModel m;
		if (serviceURL == null) {
			maker = ModelFactory.createModelRDBMaker(conn);
			base = maker.createModel("http://example.org/ontologias");
			m = ModelFactory.createOntologyModel(getModelSpec(maker), base);
		} else {
			maker = ModelFactory.createMemModelMaker();
			base = maker.createModel("http://example.org/ontologias");
			m = ModelFactory.createOntologyModel(getModelSpec(maker), base);
			m.read(serviceURL);
		}

		String wsdlInputOutput = "";
		if (input) {
			wsdlInputOutput = "grounding:wsdlInput";
		} else {
			wsdlInputOutput = "grounding:wsdlOutput";
		}

		String queryStringSearchName = "prefix grounding: <http://www.daml.org/services/owl-s/1.1/Grounding.owl#>"
				+ "select ?x ?y ?z where { <"
				+ atomicProcessGrounding
				+ "> "
				+ wsdlInputOutput
				+ "[  grounding:owlsParameter ?x ; grounding:wsdlMessagePart ?y ; grounding:xsltTransformationString ?z ] }";

		Query querySearchName = QueryFactory.create(queryStringSearchName);

		QueryExecution qeSearchName = QueryExecutionFactory.create(querySearchName, m);
		ResultSet resultsSearchName = qeSearchName.execSelect();

		String owlsParameter = "", wsdlMessagePart = "", xsltTransformationString = "";
		if (resultsSearchName != null) {

			while (resultsSearchName.hasNext()) {
				QuerySolution qSol = resultsSearchName.next();
				owlsParameter = qSol.getResource("x").toString();
				wsdlMessagePart = qSol.getLiteral("y").toString();
				xsltTransformationString = qSol.getLiteral("z").toString();
				GroundingInOutput groundIn = new GroundingInOutput(owlsParameter, wsdlMessagePart,
						xsltTransformationString);
				groundInputs.add(groundIn);
			}

		}// end if
		else {
			System.out.println("resultsSearchName is null");
		}

		qeSearchName.close();
		m.close();

		return groundInputs;
	}

	/**
	 * Returns the grounding operation of the given atomic process grounding
	 * 
	 * @param atomicProcessGrounding
	 *            URI to extract data from
	 * @param serviceURL
	 *            it specifies the service URL if the query is not to the Jena
	 *            DB. <code>null</code> to query the Jena DB model
	 * @return the grounding operation of the given atomic process grounding
	 */
	private String getGroundOperation(String atomicProcessGrounding, String serviceURL) {

		String operation = "";
		ModelMaker maker;
		Model base;
		OntModel m;
		if (serviceURL == null) {
			maker = ModelFactory.createModelRDBMaker(conn);
			base = maker.createModel("http://example.org/ontologias");
			m = ModelFactory.createOntologyModel(getModelSpec(maker), base);
		} else {
			maker = ModelFactory.createMemModelMaker();
			base = maker.createModel("http://example.org/ontologias");
			m = ModelFactory.createOntologyModel(getModelSpec(maker), base);
			m.read(serviceURL);
		}

		String queryStringSearchName = "prefix grounding: <http://www.daml.org/services/owl-s/1.1/Grounding.owl#>"
				+ "select ?x where { <" + atomicProcessGrounding + "> grounding:wsdlOperation"
				+ "[  grounding:operation ?x ; ] }";

		Query querySearchName = QueryFactory.create(queryStringSearchName);

		QueryExecution qeSearchName = QueryExecutionFactory.create(querySearchName, m);
		ResultSet resultsSearchName = qeSearchName.execSelect();

		if (resultsSearchName != null) {

			if (resultsSearchName.hasNext()) {
				operation = resultsSearchName.next().getLiteral("x").toString();
			}

		}// end if
		else {
			System.out.println("resultsSearchName is null");
		}

		qeSearchName.close();
		m.close();

		return operation;
	}

	/**
	 * Returns the grounding port type of the given atomic process grounding
	 * 
	 * @param atomicProcessGrounding
	 *            URI to extract data from
	 * @param serviceURL
	 *            it specifies the service URL if the query is not to the Jena
	 *            DB. <code>null</code> to query the Jena DB model
	 * @return the grounding port type of the given atomic process grounding
	 */
	private String getGroundPortType(String atomicProcessGrounding, String serviceURL) {

		String portType = "";
		ModelMaker maker;
		Model base;
		OntModel m;
		if (serviceURL == null) {
			maker = ModelFactory.createModelRDBMaker(conn);
			base = maker.createModel("http://example.org/ontologias");
			m = ModelFactory.createOntologyModel(getModelSpec(maker), base);
		} else {
			maker = ModelFactory.createMemModelMaker();
			base = maker.createModel("http://example.org/ontologias");
			m = ModelFactory.createOntologyModel(getModelSpec(maker), base);
			m.read(serviceURL);
		}

		String queryStringSearchName = "prefix grounding: <http://www.daml.org/services/owl-s/1.1/Grounding.owl#>"
				+ "select ?x where { <" + atomicProcessGrounding + "> grounding:wsdlOperation"
				+ "[  grounding:portType ?x ; ] }";

		Query querySearchName = QueryFactory.create(queryStringSearchName);

		QueryExecution qeSearchName = QueryExecutionFactory.create(querySearchName, m);
		ResultSet resultsSearchName = qeSearchName.execSelect();

		if (resultsSearchName != null) {

			if (resultsSearchName.hasNext()) {
				portType = resultsSearchName.next().getLiteral("x").toString();
			}

		}// end if
		else {
			System.out.println("resultsSearchName is null");
		}

		qeSearchName.close();
		m.close();

		return portType;
	}

	/**
	 * Returns an {@link ArrayList} with the provider URIs of the given service
	 * profile URI
	 * 
	 * @param serviceProfile
	 *            URI to extract its providers
	 * @param serviceURL
	 *            it specifies the service URL if the query is not to the Jena
	 *            DB. <code>null</code> to query the Jena DB model.
	 * @return an {@link ArrayList} with the provider URIs of the given service
	 *         profile URI
	 */
	private ArrayList<String> getProviders(String serviceProfile, String serviceURL) {
		ArrayList<String> providers = new ArrayList<String>();

		ModelMaker maker;
		Model base;
		OntModel m;
		if (serviceURL == null) {
			maker = ModelFactory.createModelRDBMaker(conn);
			base = maker.createModel("http://example.org/ontologias");
			m = ModelFactory.createOntologyModel(getModelSpec(maker), base);
		} else {
			maker = ModelFactory.createMemModelMaker();
			base = maker.createModel("http://example.org/ontologias");
			m = ModelFactory.createOntologyModel(getModelSpec(maker), base);
			m.read(serviceURL);
			serviceProfile = getProfileURIfromURL(serviceURL);
		}

		String queryStringSearchName = "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>"
				+ "prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>" + "select ?x where { " + "<"
				+ serviceProfile + ">" + " profile:contactInformation ?x }";

		Query querySearchName = QueryFactory.create(queryStringSearchName);

		QueryExecution qeSearchName = QueryExecutionFactory.create(querySearchName, m);
		ResultSet resultsSearchName = qeSearchName.execSelect();

		if (resultsSearchName != null) {

			while (resultsSearchName.hasNext()) {
				String providerURI = resultsSearchName.next().getResource("x").toString();
				providers.add(providerURI);
			}
		}

		return providers;

	}

	/**
	 * Returns the OWL-S specification of all providers and their information of
	 * the given service profile URI
	 * 
	 * @param serviceProfile
	 *            URI to extract its providers
	 * @param serviceURL
	 *            it specifies the service URL if the query is not to the Jena
	 *            DB. <code>null</code> to query the Jena DB model
	 * @return the OWL-S specification of all providers and their information of
	 *         the given service profile URI
	 */
	private String getProvidersOWLS(String serviceProfile, String serviceURL) {

		String providersOWLS = "";
		ModelMaker maker;
		Model base;
		OntModel m;
		if (serviceURL == null) {
			maker = ModelFactory.createModelRDBMaker(conn);
			base = maker.createModel("http://example.org/ontologias");
			m = ModelFactory.createOntologyModel(getModelSpec(maker), base);
		} else {
			maker = ModelFactory.createMemModelMaker();
			base = maker.createModel("http://example.org/ontologias");
			m = ModelFactory.createOntologyModel(getModelSpec(maker), base);
			m.read(serviceURL);
		}

		String queryStringSearchName = "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>"
				+ "prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>" + "select ?x where { " + "<"
				+ serviceProfile + ">" + " profile:contactInformation ?x }";

		Query querySearchName = QueryFactory.create(queryStringSearchName);

		QueryExecution qeSearchName = QueryExecutionFactory.create(querySearchName, m);
		ResultSet resultsSearchName = qeSearchName.execSelect();

		String providerURI = "", entityID = "", entityType = "", language = "", performative = "";

		if (resultsSearchName != null) {

			while (resultsSearchName.hasNext()) {
				providerURI = resultsSearchName.next().getResource("x").toString();

				entityID = getProviderParameter(providerURI, "entityID", serviceURL);
				entityType = getProviderParameter(providerURI, "entityType", serviceURL);
				language = getProviderParameter(providerURI, "language", serviceURL);
				performative = getProviderParameter(providerURI, "performative", serviceURL);

				StringTokenizer tokenProvURI = new StringTokenizer(providerURI, "#");
				tokenProvURI.nextToken();
				String providerName = tokenProvURI.nextToken();

				providersOWLS += "<profile:contactInformation>" + "\n" + "\t<provider:Provider rdf:ID=\""
						+ providerName + "\">" + "\n" + "\t\t<provider:entityID rdf:datatype=\"^^xsd;string\">"
						+ entityID + "</provider:entityID>" + "\n"
						+ "\t\t<provider:entityType rdf:datatype=\"^^xsd;string\">" + entityType
						+ "</provider:entityType>" + "\n" + "\t\t<provider:language rdf:datatype=\"^^xsd;string\">"
						+ language + "</provider:language>" + "\n"
						+ "\t\t<provider:performative rdf:datatype=\"^^xsd;string\">" + performative
						+ "</provider:performative>" + "\n" + "\t</provider:Provider>" + "\n"
						+ "</profile:contactInformation>" + "\n";

			}

		}// end if
		else {
			System.out.println("resultsSearchName is null");
		}

		// close the query
		qeSearchName.close();
		m.close();

		return providersOWLS;
	}

	/**
	 * Returns the value of the given provider parameter
	 * 
	 * @param providerURI
	 *            to extract its parameter
	 * @param parameter
	 *            of the provider to extract
	 * @param serviceURL
	 *            it specifies the service URL if the query is not to the Jena
	 *            DB. <code>null</code> to query the Jena DB model
	 * @return the value of the given provider parameter
	 */
	private String getProviderParameter(String providerURI, String parameter, String serviceURL) {

		ModelMaker maker;
		Model base;
		OntModel m;
		if (serviceURL == null) {
			maker = ModelFactory.createModelRDBMaker(conn);
			base = maker.createModel("http://example.org/ontologias");
			m = ModelFactory.createOntologyModel(getModelSpec(maker), base);
		} else {
			maker = ModelFactory.createMemModelMaker();
			base = maker.createModel("http://example.org/ontologias");
			m = ModelFactory.createOntologyModel(getModelSpec(maker), base);
			m.read(serviceURL);
		}

		String queryStringSearchName2 = "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>"
				+ "prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>"
				+ "prefix provider: <http://127.0.0.1/ontology/provider.owl#>" + "select ?x where { <" + providerURI
				+ "> provider:" + parameter + " ?x }";

		Query querySearchName2 = QueryFactory.create(queryStringSearchName2);

		QueryExecution qeSearchName2 = QueryExecutionFactory.create(querySearchName2, m);
		ResultSet resultsSearchName2 = qeSearchName2.execSelect();
		String result = "";
		if (resultsSearchName2 != null) {

			if (resultsSearchName2.hasNext()) {
				String str = resultsSearchName2.next().getLiteral("x").toString();
				StringTokenizer strToken = new StringTokenizer(str, "^^");
				result = strToken.nextToken();
			}
		}
		return result;
	}

	/**
	 * Returns the local name of an URI (the String after the #)
	 * 
	 * @param URI
	 * @return the local name of an URI (the String after the #)
	 */
	private String getName(String URI) {
		StringTokenizer strTok = new StringTokenizer(URI, "#");
		strTok.nextToken();
		String name = "";
		if (strTok.hasMoreTokens())
			name = strTok.nextToken();

		return name;
	}

	/**
	 * Removes a specified grounding from the Jena DB
	 * 
	 * @param grounding
	 *            to remove
	 */
	private void removeGrounding(String grounding) {

		String atomicProcessGrounding = getAtomicProcessGrounding(grounding, null);
		String baseWSDLURL = getGroundingWSDLBaseURI(atomicProcessGrounding);

		deleteWSDLMessagePart(baseWSDLURL);
		deleteWSDLPortType(baseWSDLURL);
		deleteWSDLOperation(baseWSDLURL);

		deleteProcessGrounding(grounding, atomicProcessGrounding);

	}

	/**
	 * Removes the given process from the Jena DB
	 * 
	 * @param serviceProcess
	 *            URI to remove
	 * @param serviceProfile
	 *            URI that it is attached the process to remove
	 */
	private void removeProcess(String serviceProcess, String serviceProfile) {

		if (DEBUG) {
			System.out.println("Removing groundings and process ... ");
		}

		String serviceURI = getServiceURI(serviceProfile, null);
		ArrayList<String> groundings = getGroundings(serviceURI, null);
		Iterator<String> iterGrounds = groundings.iterator();
		while (iterGrounds.hasNext()) {
			String groundingURI = iterGrounds.next();
			removeGrounding(groundingURI);
		}

		deleteProcessInputs(serviceProcess);
		deleteProcessOutputs(serviceProcess);

		deleteProcess(serviceProcess);

	}

	/**
	 * Removes a service profile from the Jena DB.
	 * 
	 * @param serviceProfile
	 *            to remove
	 * @param serviceURI
	 *            of the service profile to remove
	 */
	private void removeProfile(String serviceProfile, String serviceURI) {

		StringTokenizer servProfTok = new StringTokenizer(serviceProfile, "#");
		String baseURL = servProfTok.nextToken();

		ModelMaker maker = ModelFactory.createModelRDBMaker(conn);
		Model base = maker.createModel("http://example.org/ontologias");
		OntModel m = ModelFactory.createOntologyModel(getModelSpec(maker), base);

		// Delete profile tuples where the property is profile
		String update = "prefix xsd: <http://www.w3.org/2001/XMLSchema#>"
				+ "prefix service: <http://www.daml.org/services/owl-s/1.1/Service.owl#>"
				+ "prefix proc: <http://www.daml.org/services/owl-s/1.1/Process.owl#>"
				+ "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>" + " delete {?x ?y ?z}"
				+ " where { <" + serviceProfile + "> ?y ?z" + " filter ( ?y = profile:hasInput "
				+ "|| ?y = profile:hasOutput " + "|| ?y = profile:serviceName " + "|| ?y = profile:textDescription "
				+ "|| ?y = profile:has_process " + "|| ?y = service:isPresentedBy " + "|| ?y = service:presents "
				+ "|| ?y = profile:contactInformation " + "|| ?z = profile:Profile " + ")" + "?x ?y ?z}";

		String update2 = "prefix xsd: <http://www.w3.org/2001/XMLSchema#>"
				+ "prefix service: <http://www.daml.org/services/owl-s/1.1/Service.owl#>"
				+ "prefix proc: <http://www.daml.org/services/owl-s/1.1/Process.owl#>"
				+ "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>" + "delete {?x ?y ?z}"
				+ " where { <" + serviceURI + "> ?y ?z" + " filter ( ?z = service:Service "
				+ "|| ?y = service:presents " + ")" + "?x ?y ?z}";

		String update3 = "prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>"
				+ "prefix owl: <http://www.w3.org/2002/07/owl#>" + "delete {?x ?y ?z} " + "where" + "{ <" + baseURL
				+ "> ?y ?z" + " filter ( ( ?y = owl:imports" + "|| ?z = owl:Ontology )" + "&& ?x = <" + baseURL + ">"
				+ ")" + "?x ?y ?z}";

		// Execute the query and obtain results
		QuerySolution querysol = new QuerySolutionMap();
		UpdateAction.parseExecute(update, m, querysol);
		UpdateAction.parseExecute(update2, m, querysol);
		UpdateAction.parseExecute(update3, m, querysol);

		m.commit();
	}

	/**
	 * Gets the WSDL base URI of the given atomic process grounding
	 * 
	 * @param atomicProcessGrounding
	 *            to extract the WSDL base URI
	 * @return the WSDL base URI of the given atomic process grounding
	 */
	private String getGroundingWSDLBaseURI(String atomicProcessGrounding) {

		String WSDLBaseURI = "";
		ModelMaker maker = ModelFactory.createModelRDBMaker(conn);
		Model base = maker.createModel("http://example.org/ontologias");
		OntModel m = ModelFactory.createOntologyModel(getModelSpec(maker), base);

		String queryStringDocWSDL = "prefix xsd: <http://www.w3.org/2001/XMLSchema#>"
				+ "prefix service: <http://www.daml.org/services/owl-s/1.1/Service.owl#>"
				+ "prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>"
				+ "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>"
				+ "prefix grounding: <http://www.daml.org/services/owl-s/1.1/Grounding.owl#>" + " select ?x "
				+ "where {" + "<" + atomicProcessGrounding + ">" + " grounding:wsdlInputMessage ?x" + "}";

		Query querySearchName = QueryFactory.create(queryStringDocWSDL);

		QueryExecution qeSearchName = QueryExecutionFactory.create(querySearchName, m);
		ResultSet resultsSearchName = qeSearchName.execSelect();

		if (resultsSearchName != null) {

			if (resultsSearchName.hasNext()) {
				String wsdlInputMessage = resultsSearchName.next().getLiteral("x").toString();
				WSDLBaseURI = wsdlInputMessage.split("#")[0];
			}

		}// end if
		else {
			System.out.println("resultsSearchName is null");
		}

		qeSearchName.close();
		m.close();

		return WSDLBaseURI;

	}

	/**
	 * Deletes the WSLDMessagePart of the grounding with the given base WSDL URI
	 * 
	 * @param baseWSDLURL
	 */
	private void deleteWSDLMessagePart(String baseWSDLURL) {

		ModelMaker maker = ModelFactory.createModelRDBMaker(conn);
		Model base = maker.createModel("http://example.org/ontologias");
		OntModel m = ModelFactory.createOntologyModel(getModelSpec(maker), base);

		String queryStr = "prefix xsd: <http://www.w3.org/2001/XMLSchema#>"
				+ "prefix service: <http://www.daml.org/services/owl-s/1.1/Service.owl#>"
				+ "prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>"
				+ "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>"
				+ "prefix grounding: <http://www.daml.org/services/owl-s/1.1/Grounding.owl#>" + "select ?y "
				+ "where {" + "?x grounding:wsdlMessagePart ?y " + "}";

		Query query = QueryFactory.create(queryStr);

		QueryExecution queryExecution = QueryExecutionFactory.create(query, m);
		ResultSet resultSet = queryExecution.execSelect();

		if (resultSet != null) {

			while (resultSet.hasNext()) {
				String wsdlInOutput = resultSet.next().getLiteral("y").toString().trim();

				StringTokenizer tokwsdlInOut = new StringTokenizer(wsdlInOutput, "^^");
				String param = tokwsdlInOut.nextToken();
				String dataType = tokwsdlInOut.nextToken();
				dataType = dataType.replaceAll("http://www.w3.org/2001/XMLSchema#", "xsd:");

				wsdlInOutput = "\"" + param + "\"^^" + dataType;

				// If the url related with the WsdlMessageMap property contains
				// the url
				// of the service, the tuple should be deleted
				System.out.println("wsdlInOutput: " + wsdlInOutput);

				if (wsdlInOutput.contains(baseWSDLURL)) {
					// Delete the WsdlMessageMap tuple related with the service
					// grounding
					String updateWSDLMessageMap = "prefix xsd: <http://www.w3.org/2001/XMLSchema#>"
							+ "prefix service: <http://www.daml.org/services/owl-s/1.1/Service.owl#>"
							+ "prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>"
							+ "prefix grounding: <http://www.daml.org/services/owl-s/1.1/Grounding.owl#>"
							+ "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>"
							+ "prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>" + "delete {?x ?y ?z}" + "where"
							+ "{?x ?y " + wsdlInOutput + " filter ( ?y = grounding:wsdlMessagePart " + ")"
							+ "?x ?y ?z}";

					// Execute the query
					QuerySolution querysol = new QuerySolutionMap();
					UpdateAction.parseExecute(updateWSDLMessageMap, m, querysol);

				}
			}

		}// end if
		else {
			System.out.println("resultsSearchName is null");
		}

		queryExecution.close();
		m.close();

	}

	/**
	 * Deletes the WSDLPortType of the grounding with the given base WSDL URI
	 * 
	 * @param baseWSDLURL
	 */
	private void deleteWSDLPortType(String baseWSDLURL) {

		ModelMaker maker = ModelFactory.createModelRDBMaker(conn);
		Model base = maker.createModel("http://example.org/ontologias");
		OntModel m = ModelFactory.createOntologyModel(getModelSpec(maker), base);

		String queryStr = "prefix xsd: <http://www.w3.org/2001/XMLSchema#>"
				+ "prefix service: <http://www.daml.org/services/owl-s/1.1/Service.owl#>"
				+ "prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>"
				+ "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>"
				+ "prefix grounding: <http://www.daml.org/services/owl-s/1.1/Grounding.owl#>" + "select ?y "
				+ "where {" + "?x grounding:portType ?y " + "}";

		Query query = QueryFactory.create(queryStr);

		QueryExecution queryExecution = QueryExecutionFactory.create(query, m);
		ResultSet resultSet = queryExecution.execSelect();

		if (resultSet != null) {

			while (resultSet.hasNext()) {
				String wsdlInOutput = resultSet.next().getLiteral("y").toString().trim();

				StringTokenizer tokwsdlInOut = new StringTokenizer(wsdlInOutput, "^^");
				String param = tokwsdlInOut.nextToken();
				String dataType = tokwsdlInOut.nextToken();
				dataType = dataType.replaceAll("http://www.w3.org/2001/XMLSchema#", "xsd:");

				wsdlInOutput = "\"" + param + "\"^^" + dataType;

				// If the url related with the WsdlMessageMap property contains
				// the url
				// of the service, the tuple should be deleted
				System.out.println("wsdlInOutput: " + wsdlInOutput);

				if (wsdlInOutput.contains(baseWSDLURL)) {
					// Delete the WsdlMessageMap tuple related with the service
					// grounding
					String updateWSDLMessageMap = "prefix xsd: <http://www.w3.org/2001/XMLSchema#>"
							+ "prefix service: <http://www.daml.org/services/owl-s/1.1/Service.owl#>"
							+ "prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>"
							+ "prefix grounding: <http://www.daml.org/services/owl-s/1.1/Grounding.owl#>"
							+ "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>"
							+ "prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>" + "delete {?x ?y ?z}" + "where"
							+ "{?x ?y " + wsdlInOutput + " filter ( ?y = grounding:portType " + ")" + "?x ?y ?z}";

					// Execute the query
					QuerySolution querysol = new QuerySolutionMap();
					UpdateAction.parseExecute(updateWSDLMessageMap, m, querysol);

				}
			}

		}// end if
		else {
			System.out.println("resultsSearchName is null");
		}

		queryExecution.close();
		m.close();

	}

	/**
	 * Deletes the WSDLOperation of the grounding with the given base WSDL URI
	 * 
	 * @param baseWSDLURL
	 */
	private void deleteWSDLOperation(String baseWSDLURL) {

		ModelMaker maker = ModelFactory.createModelRDBMaker(conn);
		Model base = maker.createModel("http://example.org/ontologias");
		OntModel m = ModelFactory.createOntologyModel(getModelSpec(maker), base);

		String queryStr = "prefix xsd: <http://www.w3.org/2001/XMLSchema#>"
				+ "prefix service: <http://www.daml.org/services/owl-s/1.1/Service.owl#>"
				+ "prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>"
				+ "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>"
				+ "prefix grounding: <http://www.daml.org/services/owl-s/1.1/Grounding.owl#>" + "select ?y "
				+ "where {" + "?x grounding:operation ?y " + "}";

		Query query = QueryFactory.create(queryStr);

		QueryExecution queryExecution = QueryExecutionFactory.create(query, m);
		ResultSet resultSet = queryExecution.execSelect();

		if (resultSet != null) {

			while (resultSet.hasNext()) {
				String wsdlInOutput = resultSet.next().getLiteral("y").toString().trim();

				StringTokenizer tokwsdlInOut = new StringTokenizer(wsdlInOutput, "^^");
				String param = tokwsdlInOut.nextToken();
				String dataType = tokwsdlInOut.nextToken();
				dataType = dataType.replaceAll("http://www.w3.org/2001/XMLSchema#", "xsd:");

				wsdlInOutput = "\"" + param + "\"^^" + dataType;

				// If the url related with the WsdlMessageMap property contains
				// the url
				// of the service, the tuple should be deleted
				System.out.println("wsdlInOutput: " + wsdlInOutput);

				if (wsdlInOutput.contains(baseWSDLURL)) {
					// Delete the WsdlMessageMap tuple related with the service
					// grounding
					String updateWSDLMessageMap = "prefix xsd: <http://www.w3.org/2001/XMLSchema#>"
							+ "prefix service: <http://www.daml.org/services/owl-s/1.1/Service.owl#>"
							+ "prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>"
							+ "prefix grounding: <http://www.daml.org/services/owl-s/1.1/Grounding.owl#>"
							+ "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>"
							+ "prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>" + "delete {?x ?y ?z}" + "where"
							+ "{?x ?y " + wsdlInOutput + " filter ( ?y = grounding:operation " + ")" + "?x ?y ?z}";

					// Execute the query
					QuerySolution querysol = new QuerySolutionMap();
					UpdateAction.parseExecute(updateWSDLMessageMap, m, querysol);

				}
			}

		}// end if
		else {
			System.out.println("resultsSearchName is null");
		}

		queryExecution.close();
		m.close();

	}

	/**
	 * Deletes the process inputs of the given service process
	 * 
	 * @param serviceProcess
	 */
	private void deleteProcessInputs(String serviceProcess) {

		// Query to get the service inputs tuples related with the service
		// process

		ModelMaker maker = ModelFactory.createModelRDBMaker(conn);
		Model base = maker.createModel("http://example.org/ontologias");
		OntModel m = ModelFactory.createOntologyModel(getModelSpec(maker), base);

		String queryStr = "prefix xsd: <http://www.w3.org/2001/XMLSchema#>"
				+ "prefix service: <http://www.daml.org/services/owl-s/1.1/Service.owl#>"
				+ "prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>"
				+ "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>"
				+ "prefix grounding: <http://www.daml.org/services/owl-s/1.1/Grounding.owl#>" + " select ?x "
				+ "where { <" + serviceProcess + "> process:hasInput ?x }";

		Query query = QueryFactory.create(queryStr);

		QueryExecution queryExec = QueryExecutionFactory.create(query, m);
		ResultSet resultSet = queryExec.execSelect();

		// Execute the query and obtain the service process inputs

		// For each input, all the tuples related with the process are deleted
		if (resultSet != null) {
			while (resultSet.hasNext()) {
				String processInput = resultSet.next().getResource("x").toString();

				if (DEBUG) {
					System.out.println("Process Input: " + processInput);
				}

				// Delete input tuples related with the service process
				String updateInput = "prefix xsd: <http://www.w3.org/2001/XMLSchema#>"
						+ "prefix service: <http://www.daml.org/services/owl-s/1.1/Service.owl#>"
						+ "prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>"
						+ "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>"
						+ "prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>" + "delete {?x ?y ?z}" + "where"
						+ "{ <" + processInput + "> ?y ?z" + " filter ( ?y = rdfs:label "
						+ "|| ?y = process:parameterType " + "|| ?z = process:Input " + ")" + "?x ?y ?z}";

				// Delete the output tuple related with the service grounding
				String updateGroundInput = "prefix xsd: <http://www.w3.org/2001/XMLSchema#>"
						+ "prefix service: <http://www.daml.org/services/owl-s/1.1/Service.owl#>"
						+ "prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>"
						+ "prefix grounding: <http://www.daml.org/services/owl-s/1.1/Grounding.owl#>"
						+ "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>"
						+ "prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>" + "delete {?x ?y ?z}" + "where"
						+ "{?x ?y <" + processInput + ">" + " filter ( ?y = grounding:owlsParameter " + ")"
						+ "?x ?y ?z}";

				// Execute the query
				QuerySolution querysol = new QuerySolutionMap();
				UpdateAction.parseExecute(updateInput, m, querysol);
				UpdateAction.parseExecute(updateGroundInput, m, querysol);

			}// end for
		}// end if

	}

	/**
	 * Delete the process outputs of the given service process
	 * 
	 * @param serviceProcess
	 */
	private void deleteProcessOutputs(String serviceProcess) {

		ModelMaker maker = ModelFactory.createModelRDBMaker(conn);
		Model base = maker.createModel("http://example.org/ontologias");
		OntModel m = ModelFactory.createOntologyModel(getModelSpec(maker), base);

		String queryStr = "prefix xsd: <http://www.w3.org/2001/XMLSchema#>"
				+ "prefix service: <http://www.daml.org/services/owl-s/1.1/Service.owl#>"
				+ "prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>"
				+ "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>"
				+ "prefix grounding: <http://www.daml.org/services/owl-s/1.1/Grounding.owl#>" + " select ?x "
				+ "where { <" + serviceProcess + "> process:hasOutput ?x }";

		Query query = QueryFactory.create(queryStr);

		QueryExecution queryExec = QueryExecutionFactory.create(query, m);
		ResultSet resultSet = queryExec.execSelect();

		if (resultSet != null) {
			while (resultSet.hasNext()) {

				String processOutput = resultSet.next().getResource("x").toString();

				if (DEBUG) {
					System.out.println("Process Output: " + processOutput);
				}

				// Delete the output tuple related with the service process
				String updateOutput = "prefix xsd: <http://www.w3.org/2001/XMLSchema#>"
						+ "prefix service: <http://www.daml.org/services/owl-s/1.1/Service.owl#>"
						+ "prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>"
						+ "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>"
						+ "prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>" + "delete {?x ?y ?z}" + "where"
						+ "{ <" + processOutput + "> ?y ?z" + " filter ( ?y = rdfs:label "
						+ "|| ?y = process:parameterType " + "|| ?z = process:Output " + ")" + "?x ?y ?z}";

				// Delete the output tuple related with the service grounding
				String updateGroundOutput = "prefix xsd: <http://www.w3.org/2001/XMLSchema#>"
						+ "prefix service: <http://www.daml.org/services/owl-s/1.1/Service.owl#>"
						+ "prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>"
						+ "prefix grounding: <http://www.daml.org/services/owl-s/1.1/Grounding.owl#>"
						+ "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>"
						+ "prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>" + "delete {?x ?y ?z}" + "where"
						+ "{?x ?y <" + processOutput + ">" + " filter ( ?y = grounding:owlsParameter " + ")"
						+ "?x ?y ?z}";

				// Execute the query
				QuerySolution querysol = new QuerySolutionMap();
				UpdateAction.parseExecute(updateOutput, m, querysol);
				UpdateAction.parseExecute(updateGroundOutput, m, querysol);

			}// end for
		}// end if
	}

	/**
	 * Deletes the given process grounding
	 * 
	 * @param grounding
	 *            URI to delete
	 * @param atomicProcessGrounding
	 *            URI to delete
	 */
	private void deleteProcessGrounding(String grounding, String atomicProcessGrounding) {

		ModelMaker maker = ModelFactory.createModelRDBMaker(conn);
		Model base = maker.createModel("http://example.org/ontologias");
		OntModel m = ModelFactory.createOntologyModel(getModelSpec(maker), base);

		// Deletes Query Strings
		String updateGround = "prefix xsd: <http://www.w3.org/2001/XMLSchema#>"
				+ "prefix service: <http://www.daml.org/services/owl-s/1.1/Service.owl#>"
				+ "prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>"
				+ "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>"
				+ "prefix grounding: <http://www.daml.org/services/owl-s/1.1/Grounding.owl#>"
				+ "prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>" + "delete {?x ?y ?z .}" + "where" + "{ <"
				+ grounding + "> ?y ?z ." + " filter ( ?y = grounding:owlsProcess"
				+ "|| ?y = grounding:hasAtomicProcessGrounding " + "|| ?y = service:supportedBy "
				+ "|| ?z = grounding:WsdlGrounding " + ")" + "?x ?y ?z .}";

		String updateGroundWSDL = "prefix xsd: <http://www.w3.org/2001/XMLSchema#>"
				+ "prefix service: <http://www.daml.org/services/owl-s/1.1/Service.owl#>"
				+ "prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>"
				+ "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>"
				+ "prefix grounding: <http://www.daml.org/services/owl-s/1.1/Grounding.owl#>"
				+ "prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>" + "delete {?x ?y ?z .}" + "where" + "{ <"
				+ atomicProcessGrounding + "> ?y ?z ." + " filter ( ?y = grounding:owlsProcess"
				+ "|| ?y = grounding:wsdlDocument " + "|| ?y = grounding:wsdlInput "
				+ "|| ?y = grounding:wsdlInputMessage " + "|| ?y = grounding:wsdlOperation "
				+ "|| ?y = grounding:wsdlOutput " + "|| ?y = grounding:wsdlOutputMessage "
				+ "|| ?y = grounding:xsltTransformationString " + "|| ?z = grounding:WsdlInputMessageMap "
				+ "|| ?z = grounding:WsdlOutputMessageMap " + "|| ?z = grounding:WsdlAtomicProcessGrounding " + ")"
				+ "?x ?y ?z .} ";

		String updateGroundSupportsProperty = "prefix xsd: <http://www.w3.org/2001/XMLSchema#>"
				+ "prefix service: <http://www.daml.org/services/owl-s/1.1/Service.owl#>"
				+ "prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>"
				+ "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>"
				+ "prefix grounding: <http://www.daml.org/services/owl-s/1.1/Grounding.owl#>"
				+ "prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>" + "delete {?x ?y ?z}" + "where" + "{?x ?y <"
				+ grounding + ">" + " filter ( ?y = service:supports" + ")" + "?x ?y ?z}";

		// Execute the deletes
		QuerySolution querysol = new QuerySolutionMap();
		UpdateAction.parseExecute(updateGroundWSDL, m, querysol);
		UpdateAction.parseExecute(updateGround, m, querysol);
		UpdateAction.parseExecute(updateGroundSupportsProperty, m, querysol);

	}

	/**
	 * Deletes the given service process
	 * 
	 * @param serviceProcess
	 */
	private void deleteProcess(String serviceProcess) {

		ModelMaker maker = ModelFactory.createModelRDBMaker(conn);
		Model base = maker.createModel("http://example.org/ontologias");
		OntModel m = ModelFactory.createOntologyModel(getModelSpec(maker), base);

		// Delete the process general description
		String updateProcess = "prefix xsd: <http://www.w3.org/2001/XMLSchema#>"
				+ "prefix service: <http://www.daml.org/services/owl-s/1.1/Service.owl#>"
				+ "prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>"
				+ "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>"
				+ "prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>" + "delete {?x ?y ?z}" + "where" + "{ <"
				+ serviceProcess + "> ?y ?z" + " filter ( ?y = process:hasOutput " + "|| ?y = process:hasInput "
				+ "|| ?y = service:describes " + "|| ?y = process:hasPrecondition " + "|| ?z = process:AtomicProcess "
				+ ")" + "?x ?y ?z}";

		// Delete the profile property where the process appears
		String updateDescribedByProperty = "prefix xsd: <http://www.w3.org/2001/XMLSchema#>"
				+ "prefix service: <http://www.daml.org/services/owl-s/1.1/Service.owl#>"
				+ "prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>"
				+ "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>"
				+ "prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>" + "delete {?x ?y ?z}" + "where" + "{?x ?y <"
				+ serviceProcess + ">" + " filter ( ?y = service:describedBy " + ")" + "?x ?y ?z}";

		// Execute the query
		QuerySolution querysol = new QuerySolutionMap();
		UpdateAction.parseExecute(updateProcess, m, querysol);
		UpdateAction.parseExecute(updateDescribedByProperty, m, querysol);

	}// DeleteProcess

}
