package es.upv.dsic.gti_ia.organization;

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
import java.util.HashMap;
import java.util.InvalidPropertiesFormatException;
import java.util.Iterator;
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
import es.upv.dsic.gti_ia.organization.exception.DBConnectionException;
import es.upv.dsic.gti_ia.organization.exception.InvalidDataTypeException;
import es.upv.dsic.gti_ia.organization.exception.InvalidServiceURLException;
import es.upv.dsic.gti_ia.organization.exception.MySQLException;
import es.upv.dsic.gti_ia.organization.exception.ServiceProfileNotFoundException;
import es.upv.dsic.gti_ia.organization.exception.ServiceURINotFoundException;
import es.upv.dsic.gti_ia.organization.exception.THOMASException;
import es.upv.dsic.gti_ia.organization.exception.THOMASMessages;
import es.upv.dsic.gti_ia.organization.exception.THOMASMessages.MessageID;

/**
 * This class implements the Service Facilitator (SF) services.
 * 
 * @author Jaume Jordan
 * 
 */
public class SFInterface {

    private static boolean DEBUG = true;

    /**
     * Used for retrieve local messages.
     */
    private THOMASMessages l10n;

    /**
     * Constructor of the SFInterface
     */
    public SFInterface() {
        super();
        l10n = new THOMASMessages();
    }

    /**
     * Opens a Jena DB connection taking the configuration parameters from
     * THOMASDemoConfiguration.xml file
     * 
     * @return {@link IDBConnection} to Jena DB
     * @throws IOException
     * @throws InvalidPropertiesFormatException
     * @throws ClassNotFoundException
     */
    private IDBConnection JenaDBConnection() throws DBConnectionException {
        IDBConnection conn = null;
        Configuration configuration = Configuration.getConfiguration();

        String s_dbURL = "";
        String s_dbUser = "";
        String s_dbPw = "";
        String s_dbType = "";
        String s_dbDriver = "";

        s_dbURL = configuration.getjenadbURL();

        s_dbUser = configuration.getdatabaseUser();

        s_dbPw = configuration.getdatabasePassword();

        s_dbType = configuration.getjenadbType();

        s_dbDriver = configuration.getjenadbDriver();

        // ensure the JDBC driver class is loaded
        try {

            Class.forName(s_dbDriver);
        } catch (ClassNotFoundException e) {
            String message = l10n.getMessage(MessageID.DB_CONNECTION);
            throw new DBConnectionException(message);

        }

        // Create database connection
        try {
            conn = new DBConnection(s_dbURL, s_dbUser, s_dbPw, s_dbType);
        } catch (Exception e) {
            String message = l10n.getMessage(MessageID.DB_CONNECTION);
            throw new DBConnectionException(message);
        }
        return conn;
    }

    /**
     * Makes a clean to the connection of Jena DB. This deletes all the
     * information in the Jena DB.
     */
    public void clean() {

        try {
            IDBConnection conn = JenaDBConnection();
            conn.cleanDB();
            conn.close();

        } catch (Exception e) {
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
        try {
            IDBConnection conn = JenaDBConnection();
            ModelMaker maker = ModelFactory.createModelRDBMaker(conn);
            Model base = maker.createModel("http://example.org/ontologias");
            OntModel m = ModelFactory.createOntologyModel(getModelSpec(maker), base);

            m.write(System.out, "N3");

            closeModels(maker, base, m, conn);

            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Closes the modelmaker, model, ontology model and database connection of
     * Jena
     * 
     * @param maker
     *            of the model
     * @param base
     *            moded
     * @param m
     *            ontology model
     * @param conn
     *            Jena IDB connection
     * @throws MySQLException
     *             MySQLException
     */
    private void closeModels(ModelMaker maker, Model base, OntModel m, IDBConnection conn) throws MySQLException {
        m.close();
        base.close();
        maker.close();
        try {
            conn.close();
        } catch (SQLException e) {
            String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
            throw new MySQLException(message);
        }
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
     * @throws MySQLException
     * 
     */
    public String registerService(String serviceURL) throws MySQLException {
        String resultXML = "<response>\n<serviceName>RegisterService</serviceName>\n";
        String owlsService = "";
        String description = "";
        int nGrounds = 0, nProviders = 0;
        boolean fullRegister = false;

        IDBConnection conn;
        try {
            conn = JenaDBConnection();
        } catch (DBConnectionException e) {
            resultXML += "<status>Error</status>\n";
            resultXML += "<result>\n<description>" + e.getContent() + "</description>\n</result>\n";
            resultXML += "</response>";

            return resultXML;
        }
        ModelMaker maker = ModelFactory.createModelRDBMaker(conn);
        Model base = maker.createModel("http://example.org/ontologias");
        OntModel m = ModelFactory.createOntologyModel(getModelSpec(maker), base);

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
                resultXML += "<status>Error</status>\n";
                resultXML += "<result>\n<description>" + l10n.getMessage(MessageID.INVALID_SERVICE_URL, serviceURL) + "</description>\n</result>\n";

                resultXML += "</response>";

                return resultXML;
            }
        } catch (Exception e) {

            resultXML += "<status>Error</status>\n";
            resultXML += "<result>\n<description>" + l10n.getMessage(MessageID.INVALID_SERVICE_URL, serviceURL) + "</description>\n</result>\n";

            resultXML += "</response>";

            return resultXML;
        }

        try {

            String serviceName = getProfileServiceName(null, serviceURL, m);
            String textDescription = getProfileTextDescription(null, serviceURL, m);

            ArrayList<String> inputs = getInputs(null, serviceURL, m);
            ArrayList<String> outputs = getOutputs(null, serviceURL, m);

            ArrayList<String> inputsParams = getInputParameterTypes(inputs, serviceURL, m);
            ArrayList<String> outputParams = getOutputParameterTypes(outputs, serviceURL, m);

            String regServiceProfile = searchRegisteredServices(serviceName, textDescription, inputsParams, outputParams, m);
            if (!regServiceProfile.equalsIgnoreCase("")) {
                // System.out.println("Service already registered: " +
                // regServiceProfile);

                ArrayList<String> newProviders = getProviders(null, serviceURL, m);

                // try to register the providers if they are not registered
                ArrayList<String> registeredProviders = getProviders(regServiceProfile, null, m);
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
                    writeProvidersOWLSFile(serviceURL, regServiceProfile, fileName, m);

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

                String serviceURI = getServiceURI(regServiceProfile, null, m);
                ArrayList<String> groundings = getGroundings(serviceURI, null, m);

                Iterator<String> iterWsdlsToRegister = wsdlsToRegister.iterator();
                while (iterWsdlsToRegister.hasNext()) {
                    String wsdlToRegister = iterWsdlsToRegister.next();

                    Iterator<String> iterGrounds = groundings.iterator();
                    String groundingURI = "";
                    boolean found = false;
                    while (iterGrounds.hasNext()) {
                        groundingURI = iterGrounds.next();
                        String atomicProcessGrounding = getAtomicProcessGrounding(groundingURI, null, m);
                        String WSDLDocandDatatype = getWSDLDocument(atomicProcessGrounding, null, m);

                        if (WSDLDocandDatatype.equalsIgnoreCase(wsdlToRegister)) {
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        // System.out.println("Register new grounding");
                        nGrounds++;

                        StringTokenizer token = new StringTokenizer(wsdlToRegister, "^^");
                        String wsdlDoc = token.nextToken();

                        wsdlToRegister = "\"" + wsdlDoc + "\"" + "^^xsd:anyURI";

                        String fileName = "tmp.owls";
                        writeGroundingOWLSFile(serviceURL, regServiceProfile, wsdlToRegister, fileName, m);

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
                    description = l10n.getMessage(MessageID.ALREADY_REGISTERED, regServiceProfile);
                else
                    description = nGrounds + " groundings and " + nProviders + " providers registered to service profile: " + regServiceProfile;
                // System.out.println(nGrounds + " groundings and " + nProviders
                // + " providers registered to service profile: " +
                // regServiceProfile);

            } else {

                // load the service profile in the database
                m.read(serviceURL);
                m.commit();

                regServiceProfile = getProfileURIfromURL(serviceURL);

                description = "Service registered: " + regServiceProfile;
                // System.out.println("Service registered: " +
                // regServiceProfile);
                fullRegister = true;
            }

            owlsService = getServiceOWLS(regServiceProfile, m);

        } catch (Exception e) {
            e.printStackTrace();
            String msg;
            if (e instanceof THOMASException)
                msg = ((THOMASException) e).getContent();
            else
                msg = l10n.getMessage(MessageID.INVALID_SERVICE_URL, serviceURL);

            resultXML += "<status>Error</status>\n";
            resultXML += "<result>\n<description>" + msg + "</description>\n</result>\n";

            resultXML += "</response>";

            return resultXML;
        } finally {
            closeModels(maker, base, m, conn);
        }

        if (nGrounds == 0 && nProviders == 0 && !fullRegister)
            resultXML += "<status>Error</status>\n";
        else
            resultXML += "<status>Ok</status>\n";

        resultXML += "<result>\n<description>" + description + "</description>\n" + "<specification>\n<!-- " + owlsService + " -->\n</specification>\n</result>\n";

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
     * @throws MySQLException
     */
    public String deregisterService(String serviceProfile) throws MySQLException {
        String resultXML = "<response>\n<serviceName>DeregisterService</serviceName>\n";

        IDBConnection conn;
        try {
            conn = JenaDBConnection();
        } catch (DBConnectionException e1) {
            resultXML += "<status>Error</status>\n";
            resultXML += "<result>\n<description>" + e1.getContent() + "</description>\n</result>\n";
            resultXML += "</response>";

            return resultXML;
        }

        ModelMaker maker = ModelFactory.createModelRDBMaker(conn);
        Model base = maker.createModel("http://example.org/ontologias");
        OntModel m = ModelFactory.createOntologyModel(getModelSpec(maker), base);

        try {

            String profileServName = getProfileServiceName(serviceProfile, null, m);

            if (profileServName == null || profileServName == "") {
                // service does not exist
                resultXML += "<status>Error</status>\n";
                resultXML += "<result>\n<description>" + l10n.getMessage(MessageID.SERVICE_PROFILE_NOT_FOUND, serviceProfile) + "</description>\n</result>\n";
                resultXML += "</response>";

                return resultXML;

            } else {

                String serviceURI = getServiceURI(serviceProfile, null, m);
                String serviceProcess = getServiceProcess(serviceURI, null, m);

                ArrayList<String> providers = getProviders(serviceProfile, null, m);
                Iterator<String> iterProvs = providers.iterator();
                while (iterProvs.hasNext()) {
                    String provider = iterProvs.next();
                    StringTokenizer tokProv = new StringTokenizer(provider, "#");
                    tokProv.nextToken();
                    String providerName = tokProv.nextToken();
                    removeProvider(serviceProfile, providerName, m);
                }

                removeProcess(serviceProcess, serviceProfile, m);
                removeProfile(serviceProfile, serviceURI, m);

            }

        } catch (Exception e) {
            resultXML += "<status>Error</status>\n";
            resultXML += "<result>\n<description>" + l10n.getMessage(MessageID.SERVICE_PROFILE_NOT_FOUND, serviceProfile) + "</description>\n</result>\n";
            resultXML += "</response>";

            return resultXML;

        } finally {
            closeModels(maker, base, m, conn);
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
     * @throws MySQLException
     */
    public String getService(String serviceProfile) throws MySQLException {
        String resultXML = "<response>\n<serviceName>GetService</serviceName>\n";
        String owlsService = "";

        IDBConnection conn;
        try {
            conn = JenaDBConnection();
        } catch (DBConnectionException e) {
            resultXML += "<status>Error</status>\n";
            resultXML += "<result>\n<description>" + e.getContent() + "</description>\n</result>\n";
            resultXML += "</response>";

            return resultXML;
        }

        ModelMaker maker = ModelFactory.createModelRDBMaker(conn);
        Model base = maker.createModel("http://example.org/ontologias");
        OntModel m = ModelFactory.createOntologyModel(getModelSpec(maker), base);

        try {

            owlsService = getServiceOWLS(serviceProfile, m);

        } catch (ServiceProfileNotFoundException e) {
            resultXML += "<status>Error</status>\n";
            resultXML += "<result>\n<description>" + e.getContent() + "</description>\n</result>\n";

            resultXML += "</response>";

            return resultXML;

        } finally {
            closeModels(maker, base, m, conn);
        }

        if (owlsService == null || owlsService == "") {
            resultXML += "<status>Error</status>\n";
            resultXML += "<result>\n<description>" + l10n.getMessage(MessageID.SERVICE_PROFILE_NOT_FOUND, serviceProfile) + "</description>\n</result>\n";
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
     * @throws MySQLException
     */
    public String searchService(ArrayList<String> inputs, ArrayList<String> outputs, ArrayList<String> keywords) throws MySQLException {
        String resultXML = "<response>\n<serviceName>SearchService</serviceName>\n";
        String itemsList = "";
        // store a list of profiles with their similarity weights to the service
        ArrayList<Profile> profiles = new ArrayList<Profile>();

        IDBConnection conn;
        try {
            conn = JenaDBConnection();
        } catch (DBConnectionException e) {
            resultXML += "<status>Error</status>\n";
            resultXML += "<result>\n<description>" + e.getContent() + "</description>\n</result>\n";
            resultXML += "</response>";

            return resultXML;
        }

        ModelMaker maker = ModelFactory.createModelRDBMaker(conn);
        Model base = maker.createModel("http://example.org/ontologias");
        OntModel m = ModelFactory.createOntologyModel(getModelSpec(maker), base);

        try {

            ArrayList<String> candidatesInputs = new ArrayList<String>();

            if (inputs != null && !inputs.isEmpty()) {
                // the service searches each input and add to a list each
                // service that has an equal input as a candidate
                Iterator<String> iterInputs = inputs.iterator();
                while (iterInputs.hasNext()) {

                    String in = iterInputs.next();

                    String queryStr = "prefix xsd: <http://www.w3.org/2001/XMLSchema#>" + "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>" + "prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>" + "select ?x where { ?x a process:Input ; process:parameterType " + in + " . }";

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

                    String queryStr = "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>" + "prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>" + "select ?x where { ?x profile:hasInput " + "<" + cand + ">" + " }";

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

                    String queryStr = "prefix xsd: <http://www.w3.org/2001/XMLSchema#>" + "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>" + "prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>" + "select ?x where { ?x a process:Output ; process:parameterType " + in + " . }";

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

                    String queryStr = "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>" + "prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>" + "select ?x where { ?x profile:hasOutput " + "<" + cand + ">" + " }";

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
                HashMap<String, String> textDescriptions = getProfilesTextDescriptions(m);

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
                String queryStr = "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>" + "prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>" + "select ?x where { " + "<" + profile.getUrl() + ">" + " profile:hasInput ?x }";

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
                            String queryStrType = "prefix xsd: <http://www.w3.org/2001/XMLSchema#>" + "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>" + "prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>" + "select ?x where { <" + input + "> a process:Input ; process:parameterType ?x . }";

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
                String queryStrOutputs = "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>" + "prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>" + "select ?x where { " + "<" + profile.getUrl() + ">" + " profile:hasOutput ?x }";

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
                            String queryStrType = "prefix xsd: <http://www.w3.org/2001/XMLSchema#>" + "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>" + "prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>" + "select ?x where { <" + output + "> a process:Output ; process:parameterType ?x . }";

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
                    String textDescription = getProfileTextDescription(profile.getUrl(), null, m).toLowerCase().trim();

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
                float similarity = (1.0f / similaritiesUsed) * inputsSimilarity + (1.0f / similaritiesUsed) * outputsSimilarity + (1.0f / similaritiesUsed) * similarityToKeywords;
                profile.setSuitability(similarity);

            }// end iterator profiles

            // sort the found candidate profiles by their similarity
            Collections.sort(profiles);

            iterProfiles = profiles.iterator();
            while (iterProfiles.hasNext()) {
                Profile profile = iterProfiles.next();
                itemsList += "\t<item>\n\t\t<profile>" + profile.getUrl() + "</profile>\n\t\t<quantity>" + profile.getSuitability() + "</quantity>\n\t</item>\n";
            }

        } catch (Exception e) {
            e.printStackTrace();
            resultXML += "<status>Error</status>\n";
            resultXML += "<result>\n<description>" + l10n.getMessage(MessageID.INVALID_DATA_TYPE) + "</description>\n</result>\n";

            resultXML += "</response>";

            return resultXML;
        } finally {
            closeModels(maker, base, m, conn);
        }

        if (profiles.size() == 0) {
            resultXML += "<status>Error</status>\n";
            resultXML += "<result>\n<description>" + l10n.getMessage(MessageID.SERVICES_NOT_FOUND) + "</description>\n</result>\n";

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
     * @throws MySQLException
     */
    public String removeProvider(String serviceProfile, String providerName) throws MySQLException {
        String resultXML = "<response>\n<serviceName>RemoveProvider</serviceName>\n";

        IDBConnection conn;
        try {
            conn = JenaDBConnection();
        } catch (DBConnectionException e) {
            resultXML += "<status>Error</status>\n";
            resultXML += "<result>\n<description>" + e.getContent() + "</description>\n</result>\n";
            resultXML += "</response>";

            return resultXML;
        }

        ModelMaker maker = ModelFactory.createModelRDBMaker(conn);
        Model base = maker.createModel("http://example.org/ontologias");
        OntModel m = ModelFactory.createOntologyModel(getModelSpec(maker), base);

        try {

            removeProvider(serviceProfile, providerName, m);

        } catch (ServiceProfileNotFoundException e) {

            resultXML += "<status>Error</status>\n";
            resultXML += "<result>\n<description>" + e.getContent() + "</description>\n</result>\n";

            resultXML += "</response>";

            return resultXML;
        } finally {
            closeModels(maker, base, m, conn);
        }

        resultXML += "<status>Ok</status>\n";
        resultXML += "<result>\n<description>" + "Provider or grounding " + providerName + " removed" + "</description>\n</result>\n";
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
     * 
     * @param m
     *            Ontology model of Jena DB to query
     * 
     * @return a registered service that fits the given parameters, or empty
     *         {@link String} if not
     * @throws ServiceProfileNotFoundException
     * @throws InvalidDataTypeException
     * 
     */
    private String searchRegisteredServices(String serviceName, String textDescription, ArrayList<String> inputs, ArrayList<String> outputs, OntModel m) throws ServiceProfileNotFoundException, InvalidDataTypeException {

        // search the inputs

        ArrayList<String> candidates = new ArrayList<String>();
        boolean firstInput = true;
        Iterator<String> iterInputs = inputs.iterator();
        while (iterInputs.hasNext()) {

            String in = iterInputs.next();

            ArrayList<String> candN = new ArrayList<String>();

            String queryStr = "prefix xsd: <http://www.w3.org/2001/XMLSchema#>" + "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>" + "prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>" + "select ?x where { ?x a process:Input ; process:parameterType " + in + " . }";

            Query query = QueryFactory.create(queryStr);

            // Execute the query and obtain results
            QueryExecution querySearchInputs = QueryExecutionFactory.create(query, m);
            ResultSet resultsSearchInputs = querySearchInputs.execSelect();

            if (resultsSearchInputs != null) {

                while (resultsSearchInputs.hasNext()) {
                    QuerySolution sol = resultsSearchInputs.next();

                    Resource resource = sol.getResource("x");
                    String param = resource.getURI();
                    String cand = getProfileURI(param, true, m);

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

            String queryStr = "prefix xsd: <http://www.w3.org/2001/XMLSchema#>" + "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>" + "prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>" + "select ?x where { ?x a process:Output ; process:parameterType " + out + " . }";

            Query query = QueryFactory.create(queryStr);

            // Execute the query and obtain results
            QueryExecution querySearchOutputs = QueryExecutionFactory.create(query, m);
            ResultSet resultsSearchOutputs = querySearchOutputs.execSelect();

            if (resultsSearchOutputs != null) {

                while (resultsSearchOutputs.hasNext()) {
                    QuerySolution sol = resultsSearchOutputs.next();

                    Resource resource = sol.getResource("x");
                    String param = resource.getURI();
                    String cand = getProfileURI(param, false, m);

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
                String candServiceName = getProfileServiceName(candidate, null, m);
                String candTextDescription = getProfileTextDescription(candidate, null, m);

                // check that the service name and text description are
                // exactly the same
                if (!candServiceName.equalsIgnoreCase(serviceName) || !candTextDescription.equalsIgnoreCase(textDescription)) {
                    candidates.remove(i);
                    i--;
                    continue;
                } else { // check if the inputs and outputs are the same
                    // exactly
                    // (could arrive here having a service with more
                    // inputs
                    // than the specified as parameter and will be
                    // different services)
                    ArrayList<String> inputsCandidate = getInputs(candidate, null, m);
                    ArrayList<String> outputsCandidate = getOutputs(candidate, null, m);

                    ArrayList<String> inputParamTypeCand = getInputParameterTypes(inputsCandidate, null, m);
                    ArrayList<String> outputParamTypeCand = getOutputParameterTypes(outputsCandidate, null, m);

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

    }

    /**
     * Returns the complete service OWL-S specification of the given service
     * profile
     * 
     * @param serviceProfile
     * 
     * @param m
     *            Ontology model of Jena DB to query
     * 
     * @return the complete service OWL-S specification of the given service
     *         profile
     * @throws ServiceProfileNotFoundException
     */
    private String getServiceOWLS(String serviceProfile, OntModel m) throws ServiceProfileNotFoundException {
        String owlsService = "";
        String profileServiceName = getProfileServiceName(serviceProfile, null, m);

        if (profileServiceName != null && profileServiceName != "") {
            // headers
            owlsService += "<?xml version=\"1.0\" encoding=\"WINDOWS-1252\"?>" + "\n" + "<rdf:RDF  xmlns:owl       = \"http://www.w3.org/2002/07/owl#\"" + "\n" + "xmlns:rdfs      = \"http://www.w3.org/2000/01/rdf-schema#\"" + "\n" + "xmlns:rdf       = \"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"" + "\n" + "xmlns:xsd       = \"http://www.w3.org/2001/XMLSchema#\"" + "\n" + "xmlns:service   = \"http://www.daml.org/services/owl-s/1.1/Service.owl#\"" + "\n"
                    + "xmlns:process   = \"http://www.daml.org/services/owl-s/1.1/Process.owl#\"" + "\n" + "xmlns:profile    = \"http://www.daml.org/services/owl-s/1.1/Profile.owl#\"" + "\n" + "xmlns:grounding = \"http://www.daml.org/services/owl-s/1.1/Grounding.owl#\"" + "\n" + "xmlns:provider = \"http://localhost:8080/ontologies/provider.owl#\">" + "\n";

            // profile descriptions...
            // profile:serviceName
            // profile:textDescription
            owlsService += "<profile:Profile rdf:ID=\"" + serviceProfile + "\">\n" + "\t<profile:serviceName xml:lang=\"en\">" + profileServiceName + "</profile:serviceName>" + "\n";

            owlsService += "\t<profile:textDescription xml:lang=\"en\">" + getProfileTextDescription(serviceProfile, null, m) + "</profile:textDescription>" + "\n";

            // providers
            // profile:contactInformation
            // a provider:Provider
            owlsService += getProvidersOWLS(serviceProfile, null, m);

            // inputs and outputs of a process with their types
            String processInputs = "", processOutputs = "";

            ArrayList<String> inputs = getInputs(serviceProfile, null, m);
            Iterator<String> iterInputs = inputs.iterator();
            while (iterInputs.hasNext()) {
                // profile:hasInput
                String in = iterInputs.next();
                owlsService += "\t<profile:hasInput rdf:resource=\"" + in + "\"/>" + "\n";
                processInputs += getInputOWLS(in, m);

            }

            ArrayList<String> outputs = getOutputs(serviceProfile, null, m);
            Iterator<String> iterOutputs = outputs.iterator();
            while (iterOutputs.hasNext()) {
                // profile:hasOutput
                String out = iterOutputs.next();
                owlsService += "\t<profile:hasOutput rdf:resource=\"" + out + "\"/>" + "\n";
                processOutputs += getOutputOWLS(out, m);
            }
            owlsService += "</profile:Profile>\n";

            // process:parameterType
            // process:parameterType

            owlsService += processInputs + processOutputs + "\n";

            // all the groundings
            owlsService += getGroundingsOWLS(serviceProfile, m);

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
     * 
     * @param m
     *            Ontology model of Jena DB to query
     * 
     * @return the profile URI of the given parameter name.
     */
    private String getProfileURI(String paramName, boolean input, OntModel m) {
        String res = "";

        String queryStringSearchName;
        if (input)
            queryStringSearchName = "prefix xsd: <http://www.w3.org/2001/XMLSchema#>" + "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>" + "prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>" + "select ?x where { ?x profile:hasInput <" + paramName + "> }";
        else
            queryStringSearchName = "prefix xsd: <http://www.w3.org/2001/XMLSchema#>" + "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>" + "prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>" + "select ?x where { ?x profile:hasOutput <" + paramName + "> }";

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
     * 
     * @param m
     *            Ontology model of Jena DB to query
     * 
     * @return the inputs of the given service profile
     * @throws THOMASException
     */
    private ArrayList<String> getInputs(String serviceProfile, String serviceURL, OntModel m) throws ServiceProfileNotFoundException {
        ArrayList<String> inputs = new ArrayList<String>();

        try {

            String queryStringSearchName;

            if (serviceURL == null) {

                queryStringSearchName = "prefix xsd: <http://www.w3.org/2001/XMLSchema#>" + "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>" + "prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>" + "select ?x where { <" + serviceProfile + "> profile:hasInput ?x }";
            } else {
                ModelMaker maker = ModelFactory.createMemModelMaker();
                Model base = maker.createModel("http://example.org/ontologias");
                m = ModelFactory.createOntologyModel(getModelSpec(maker), base);

                m.read(serviceURL);

                queryStringSearchName = "prefix xsd: <http://www.w3.org/2001/XMLSchema#>" + "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>" + "prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>" + "select ?x where { ?y profile:hasInput ?x }";
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

        } catch (Exception e) {
            throw new ServiceProfileNotFoundException(l10n.getMessage(MessageID.SERVICE_PROFILE_NOT_FOUND, serviceProfile));
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
     * 
     * @param m
     *            Ontology model of Jena DB to query
     * 
     * @return the inputs parameter type of the given inputs
     * @throws InvalidDataTypeException
     */
    private ArrayList<String> getInputParameterTypes(ArrayList<String> inputs, String serviceURL, OntModel m) throws InvalidDataTypeException {
        ArrayList<String> inputParamsRegistered = new ArrayList<String>();

        try {

            if (serviceURL == null) {

            } else {
                ModelMaker maker = ModelFactory.createMemModelMaker();
                Model base = maker.createModel("http://example.org/ontologias");
                m = ModelFactory.createOntologyModel(getModelSpec(maker), base);
                m.read(serviceURL);
            }

            Iterator<String> iterInputs = inputs.iterator();
            while (iterInputs.hasNext()) {

                String in = iterInputs.next();
                String queryStr = "prefix xsd: <http://www.w3.org/2001/XMLSchema#>" + "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>" + "prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>" + "select ?x where { <" + in + "> a process:Input ; process:parameterType ?x . }";

                Query query = QueryFactory.create(queryStr);

                // Execute the query and obtain results
                QueryExecution querySearchInputs = QueryExecutionFactory.create(query, m);
                ResultSet resultsSearchInputs = querySearchInputs.execSelect();

                if (resultsSearchInputs != null) {

                    while (resultsSearchInputs.hasNext()) {
                        QuerySolution sol = resultsSearchInputs.next();

                        String param = "\"" + sol.getLiteral("x").getString() + "\"^^" + sol.getLiteral("x").getDatatypeURI().replace("http://www.w3.org/2001/XMLSchema#", "xsd:");

                        inputParamsRegistered.add(param);

                    }// end for
                }// end if

                // close the query
                querySearchInputs.close();
            }

        } catch (Exception e) {
            throw new InvalidDataTypeException(l10n.getMessage(MessageID.INVALID_DATA_TYPE));
        }
        return inputParamsRegistered;
    }

    /**
     * Returns the specification in OWL-S of the given input as a process part
     * 
     * @param input
     *            URI to extract data from
     * 
     * @param m
     *            Ontology model of Jena DB to query
     * 
     * @return the specification in OWL-S of the given input as a process part
     */
    private String getInputOWLS(String input, OntModel m) {
        String inputOWLS = "";
        String param = "";

        String queryStringSearchName = "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>" + "prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>" + "select ?x where { <" + input + "> a process:Input ; process:parameterType ?x . }";

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

        inputOWLS = "\t<process:Input rdf:ID=\"" + input + "\">" + "\n" + "\t\t<process:parameterType rdf:datatype=\"" + paramDataType + "\">" + paramType + "</process:parameterType>" + "\n" + "\t</process:Input>\n";

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
     * 
     * @param m
     *            Ontology model of Jena DB to query
     * 
     * @return the outputs of the given service profile
     * @throws ServiceProfileNotFoundException
     */
    private ArrayList<String> getOutputs(String serviceProfile, String serviceURL, OntModel m) throws ServiceProfileNotFoundException {
        ArrayList<String> outputs = new ArrayList<String>();

        try {

            String queryStringSearchName;
            if (serviceURL == null) {

                queryStringSearchName = "prefix xsd: <http://www.w3.org/2001/XMLSchema#>" + "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>" + "prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>" + "select ?x where { <" + serviceProfile + "> profile:hasOutput ?x }";
            } else {
                ModelMaker maker = ModelFactory.createMemModelMaker();
                Model base = maker.createModel("http://example.org/ontologias");
                m = ModelFactory.createOntologyModel(getModelSpec(maker), base);
                m.read(serviceURL);
                queryStringSearchName = "prefix xsd: <http://www.w3.org/2001/XMLSchema#>" + "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>" + "prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>" + "select ?x where { ?y profile:hasOutput ?x }";
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

        } catch (Exception e) {
            throw new ServiceProfileNotFoundException(l10n.getMessage(MessageID.SERVICE_PROFILE_NOT_FOUND, serviceProfile));
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
     * @param m
     *            Ontology model of Jena DB to query
     * @return the outputs parameter type of the given outputs
     * @throws InvalidDataTypeException
     */
    private ArrayList<String> getOutputParameterTypes(ArrayList<String> outputs, String serviceURL, OntModel m) throws InvalidDataTypeException {
        ArrayList<String> outputParamsRegistered = new ArrayList<String>();

        try {

            if (serviceURL == null) {

            } else {
                ModelMaker maker = ModelFactory.createMemModelMaker();
                Model base = maker.createModel("http://example.org/ontologias");
                m = ModelFactory.createOntologyModel(getModelSpec(maker), base);
                m.read(serviceURL);
            }

            Iterator<String> iterOutputs = outputs.iterator();
            while (iterOutputs.hasNext()) {

                String out = iterOutputs.next();
                String queryStr = "prefix xsd: <http://www.w3.org/2001/XMLSchema#>" + "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>" + "prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>" + "select ?x where { <" + out + "> a process:Output ; process:parameterType ?x . }";

                Query query = QueryFactory.create(queryStr);

                // Execute the query and obtain results
                QueryExecution querySearchInputs = QueryExecutionFactory.create(query, m);
                ResultSet resultsSearchInputs = querySearchInputs.execSelect();

                if (resultsSearchInputs != null) {

                    while (resultsSearchInputs.hasNext()) {
                        QuerySolution sol = resultsSearchInputs.next();

                        String param = "\"" + sol.getLiteral("x").getString() + "\"^^" + sol.getLiteral("x").getDatatypeURI().replace("http://www.w3.org/2001/XMLSchema#", "xsd:");
                        outputParamsRegistered.add(param);

                    }// end for
                }// end if

                // close the query
                querySearchInputs.close();
            }

        } catch (Exception e) {
            throw new InvalidDataTypeException(l10n.getMessage(MessageID.INVALID_DATA_TYPE));
        }

        return outputParamsRegistered;
    }

    /**
     * Returns the specification in OWL-S of the given output
     * 
     * @param output
     *            URI to extract data from
     * @param m
     *            Ontology model of Jena DB to query
     * @return the specification in OWL-S of the given output
     */
    private String getOutputOWLS(String output, OntModel m) {
        String outputOWLS = "";
        String param = "";

        String queryStringSearchName = "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>" + "prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>" + "select ?x where { <" + output + "> a process:Output ; process:parameterType ?x . }";

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

        outputOWLS = "\t<process:Output rdf:ID=\"" + output + "\">" + "\n" + "\t\t<process:parameterType rdf:datatype=\"" + paramDataType + "\">" + paramType + "</process:parameterType>" + "\n" + "\t</process:Output>";

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
     * @param m
     *            Ontology model of Jena DB to query
     * @throws ServiceProfileNotFoundException
     * @throws IOException
     * @throws InvalidServiceURLException
     */
    private void writeGroundingOWLSFile(String serviceURL, String registeredProfile, String wsdlToRegister, String fileName, OntModel m) throws ServiceProfileNotFoundException, IOException, InvalidServiceURLException {

        StringTokenizer tokenProfile = new StringTokenizer(registeredProfile, "#");
        String urlBase = tokenProfile.nextToken();
        String regProfileName = tokenProfile.nextToken();

        FileWriter fstream = new FileWriter(fileName);
        BufferedWriter out = new BufferedWriter(fstream);

        out.write("<?xml version=\"1.0\" encoding=\"WINDOWS-1252\"?>" + "\n" + "<rdf:RDF  xmlns:owl       = \"http://www.w3.org/2002/07/owl#\"" + "\n" + "xmlns:rdfs      = \"http://www.w3.org/2000/01/rdf-schema#\"" + "\n" + "xmlns:rdf       = \"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"" + "\n" + "xmlns:xsd       = \"http://www.w3.org/2001/XMLSchema#\"" + "\n" + "xmlns:service   = \"http://www.daml.org/services/owl-s/1.1/Service.owl#\"" + "\n"
                + "xmlns:process   = \"http://www.daml.org/services/owl-s/1.1/Process.owl#\"" + "\n" + "xmlns:profile    = \"http://www.daml.org/services/owl-s/1.1/Profile.owl#\"" + "\n" + "xmlns:grounding = \"http://www.daml.org/services/owl-s/1.1/Grounding.owl#\"" + "\n" + "xmlns:provider = \"http://localhost:8080/ontologies/provider.owl#\"" + "\n" + "xml:base        = \"" + urlBase + "\">" + "\n");

        out.write("<profile:Profile rdf:ID=\"" + regProfileName + "\">\n");

        StringTokenizer tokenRegServ = new StringTokenizer(registeredProfile, "#");
        String baseURI = tokenRegServ.nextToken();

        String registeredServiceURI = getServiceURI(registeredProfile, null, m);
        String profile = getProfileURIfromURL(serviceURL);

        out.write("</profile:Profile>\n");

        String groundOWLS = getGroundingOWLSfromFile(profile, serviceURL, registeredServiceURI, wsdlToRegister, baseURI, m);

        System.out.println("groundOWLS:\n" + groundOWLS);

        out.write(groundOWLS);
        out.write("</rdf:RDF>\n");

        out.close();

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
     * @param m
     *            Ontology model of Jena DB to query
     * @throws IOException
     * @throws InvalidServiceURLException
     */
    private void writeProvidersOWLSFile(String serviceURL, String registeredProfile, String fileName, OntModel m) throws IOException, InvalidServiceURLException {

        StringTokenizer tokenProfile = new StringTokenizer(registeredProfile, "#");
        String urlBase = tokenProfile.nextToken();
        String regProfileName = tokenProfile.nextToken();

        FileWriter fstream = new FileWriter(fileName);
        BufferedWriter out = new BufferedWriter(fstream);

        out.write("<?xml version=\"1.0\" encoding=\"WINDOWS-1252\"?>" + "\n" + "<rdf:RDF  xmlns:owl       = \"http://www.w3.org/2002/07/owl#\"" + "\n" + "xmlns:rdfs      = \"http://www.w3.org/2000/01/rdf-schema#\"" + "\n" + "xmlns:rdf       = \"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"" + "\n" + "xmlns:xsd       = \"http://www.w3.org/2001/XMLSchema#\"" + "\n" + "xmlns:service   = \"http://www.daml.org/services/owl-s/1.1/Service.owl#\"" + "\n"
                + "xmlns:process   = \"http://www.daml.org/services/owl-s/1.1/Process.owl#\"" + "\n" + "xmlns:profile    = \"http://www.daml.org/services/owl-s/1.1/Profile.owl#\"" + "\n" + "xmlns:grounding = \"http://www.daml.org/services/owl-s/1.1/Grounding.owl#\"" + "\n" + "xmlns:provider = \"http://localhost:8080/ontologies/provider.owl#\"" + "\n" + "xml:base        = \"" + urlBase + "\">" + "\n");

        out.write("<profile:Profile rdf:ID=\"" + regProfileName + "\">\n");

        String profile = getProfileURIfromURL(serviceURL);

        // it is not necessary to check if the providers are already
        // registered, Jena does not write them two times
        String providersOWLS = getProvidersOWLS(profile, serviceURL, m);

        System.out.println("profile=" + profile + "\nProvidersOWLS:\n" + providersOWLS);

        out.write(providersOWLS);
        out.write("</profile:Profile>\n");

        out.write("</rdf:RDF>\n");

        out.close();

    }

    /**
     * Returns the profile URI of the service specification in the given service
     * URL
     * 
     * @param serviceURL
     *            to extract data from
     * @return the profile URI of the service specification in the given service
     *         URL
     * @throws InvalidServiceURLException
     */
    private String getProfileURIfromURL(String serviceURL) throws InvalidServiceURLException {

        try {
            ModelMaker maker = ModelFactory.createMemModelMaker();
            Model base = maker.createModel("http://example.org/ontologias");
            OntModel m = ModelFactory.createOntologyModel(getModelSpec(maker), base);

            m.read(serviceURL);

            String queryStringSearchName = "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>" + "prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>" + "select ?x where { ?x a profile:Profile }";

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

        } catch (Exception e) {
            throw new InvalidServiceURLException(l10n.getMessage(MessageID.INVALID_SERVICE_URL, serviceURL));
        }

    }

    /**
     * Returns the profile service name of the given service profile
     * 
     * @param serviceProfile
     *            URI to extract data from
     * @param serviceURL
     *            it specifies the service URL if the query is not to the Jena
     *            DB. <code>null</code> to query the Jena DB model.
     * @param m
     *            Ontology model of Jena DB to query
     * @return the profile service name of the given service profile
     * @throws ServiceProfileNotFoundException
     */
    private String getProfileServiceName(String serviceProfile, String serviceURL, OntModel m) throws ServiceProfileNotFoundException {
        String serviceName = null;

        try {

            String queryStringSearchName;
            if (serviceURL == null) {

                queryStringSearchName = "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>" + "prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>" + "select ?x where { " + "<" + serviceProfile + ">" + " profile:serviceName ?x }";
            } else {
                ModelMaker maker = ModelFactory.createMemModelMaker();
                Model base = maker.createModel("http://example.org/ontologias");
                m = ModelFactory.createOntologyModel(getModelSpec(maker), base);
                m.read(serviceURL);

                queryStringSearchName = "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>" + "prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>" + "select ?x where { ?y profile:serviceName ?x }";
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
                // System.out.println("resultsSearchName is null");
            }
            if(serviceName==null)
            	throw new Exception();

        } catch (Exception e) {
            throw new ServiceProfileNotFoundException(l10n.getMessage(MessageID.SERVICE_PROFILE_NOT_FOUND, serviceProfile));
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
     * @param m
     *            Ontology model of Jena DB to query
     * @return the profile text description of the given service profile
     * @throws ServiceProfileNotFoundException
     */
    private String getProfileTextDescription(String serviceProfile, String serviceURL, OntModel m) throws ServiceProfileNotFoundException {
        String textDescription = null;
        try {
            String queryStringSearchName;

            if (serviceURL == null) {
                queryStringSearchName = "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>" + "prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>" + "select ?x where { " + "<" + serviceProfile + ">" + " profile:textDescription ?x }";
            } else {
                ModelMaker maker = ModelFactory.createMemModelMaker();
                Model base = maker.createModel("http://example.org/ontologias");
                m = ModelFactory.createOntologyModel(getModelSpec(maker), base);
                m.read(serviceURL);
                queryStringSearchName = "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>" + "prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>" + "select ?x where { ?y profile:textDescription ?x }";
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

            if(textDescription==null)
            	throw new Exception();
            
        } catch (Exception e) {
            throw new ServiceProfileNotFoundException(l10n.getMessage(MessageID.SERVICE_PROFILE_NOT_FOUND, serviceProfile));
        }

        return textDescription;

    }

    /**
     * Returns the profiles text descriptions of all registered services in Jena
     * DB
     * 
     * @param m
     *            Ontology model of Jena DB to query
     * @return the profiles text descriptions of all registered services in Jena
     *         DB
     */
    private HashMap<String, String> getProfilesTextDescriptions(OntModel m) {
        HashMap<String, String> textDescriptions = new HashMap<String, String>();

        String queryStringSearchName = "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>" + "prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>" + "select * where { ?y profile:textDescription ?x }";

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
     * @param m
     *            Ontology model of Jena DB to query
     * @return the service URI
     * @throws ServiceProfileNotFoundException
     */
    private String getServiceURI(String serviceProfile, String serviceURL, OntModel m) throws ServiceProfileNotFoundException {

        String serviceURI = null;
        try {
            if (serviceURL == null) {

            } else {
                ModelMaker maker = ModelFactory.createMemModelMaker();
                Model base = maker.createModel("http://example.org/ontologias");
                m = ModelFactory.createOntologyModel(getModelSpec(maker), base);
                m.read(serviceURL);
            }

            String queryStringSearchName = "prefix service: <http://www.daml.org/services/owl-s/1.1/Service.owl#>" + "select ?x where { ?x service:presents <" + serviceProfile + "> }";

            Query querySearchName = QueryFactory.create(queryStringSearchName);

            QueryExecution qeSearchName = QueryExecutionFactory.create(querySearchName, m);
            ResultSet resultsSearchName = qeSearchName.execSelect();

            if (resultsSearchName != null) {

                if (resultsSearchName.hasNext()) {
                    serviceURI = resultsSearchName.next().getResource("x").toString();
                }

            }// end if
            else {
                // System.out.println("resultsSearchName is null");
            }

            // qeSearchName.close();
            
            if(serviceURI==null)
            	throw new Exception();
            
        } catch (Exception e) {
            throw new ServiceProfileNotFoundException(l10n.getMessage(MessageID.SERVICE_PROFILE_NOT_FOUND, serviceProfile));
        }

        return serviceURI;
    }

    /**
     * Returns the service process URI of the given service profile
     * 
     * @param serviceURI
     *            URI to extract data from
     * @param serviceURL
     *            it specifies the service URL if the query is not to the Jena
     *            DB. <code>null</code> to query the Jena DB model
     * @param m
     *            Ontology model of Jena DB to query
     * @return the service process URI of the given service profile
     * @throws ServiceURINotFoundException 
     */
    private String getServiceProcess(String serviceURI, String serviceURL, OntModel m) throws ServiceURINotFoundException {
        String serviceProcess = null;
        try {
            if (serviceURL == null) {

            } else {
                ModelMaker maker = ModelFactory.createMemModelMaker();
                Model base = maker.createModel("http://example.org/ontologias");
                m = ModelFactory.createOntologyModel(getModelSpec(maker), base);
                m.read(serviceURL);
            }

            String queryStringSearchName = "prefix service: <http://www.daml.org/services/owl-s/1.1/Service.owl#>" + 
            "prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>" + 
            		"select ?x where { <"+serviceURI+"> service:describedBy ?x }";

            Query querySearchName = QueryFactory.create(queryStringSearchName);

            QueryExecution qeSearchName = QueryExecutionFactory.create(querySearchName, m);
            ResultSet resultsSearchName = qeSearchName.execSelect();

            if (resultsSearchName != null) {

                if (resultsSearchName.hasNext()) {
                    serviceProcess = resultsSearchName.next().getResource("x").toString();
                }

            }// end if
            else {
                // System.out.println("resultsSearchName is null");
            }

            qeSearchName.close();

            if(serviceProcess==null)
            	throw new Exception();
            
        } catch (Exception e) {
            throw new ServiceURINotFoundException(l10n.getMessage(MessageID.SERVICE_URI_NOT_FOUND, serviceURI));
        }

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
     * @param m
     *            Ontology model of Jena DB to query
     * @return an {@link ArrayList} with the grounding URIs of the given service
     *         URI
     */
    private ArrayList<String> getGroundings(String serviceURI, String serviceURL, OntModel m) {
        ArrayList<String> groundings = new ArrayList<String>();

        if (serviceURL == null) {

        } else {
            ModelMaker maker = ModelFactory.createMemModelMaker();
            Model base = maker.createModel("http://example.org/ontologias");
            m = ModelFactory.createOntologyModel(getModelSpec(maker), base);
            m.read(serviceURL);
        }

        String queryStringSearchName = "prefix service: <http://www.daml.org/services/owl-s/1.1/Service.owl#>" + "select ?x where { ?x service:supportedBy <" + serviceURI + "> }";

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
     * @param m
     *            Ontology model of Jena DB to query
     * @return the atomic process grounding URI of the given grounding URI
     */
    private String getAtomicProcessGrounding(String groundingURI, String serviceURL, OntModel m) {
        String atomicProcessGrounding = "";

        if (serviceURL == null) {

        } else {
            ModelMaker maker = ModelFactory.createMemModelMaker();
            Model base = maker.createModel("http://example.org/ontologias");
            m = ModelFactory.createOntologyModel(getModelSpec(maker), base);
            m.read(serviceURL);
        }

        String queryStringSearchName = "prefix grounding: <http://www.daml.org/services/owl-s/1.1/Grounding.owl#>" + "select ?x where { <" + groundingURI + "> grounding:hasAtomicProcessGrounding ?x }";

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
     * @param m
     *            Ontology model of Jena DB to query
     * @return the WSDL document URI of the given atomic process grounding URI
     */
    private String getWSDLDocument(String atomicProcessGrounding, String serviceURL, OntModel m) {
        String WSDLDoc = "";

        if (serviceURL == null) {

        } else {
            ModelMaker maker = ModelFactory.createMemModelMaker();
            Model base = maker.createModel("http://example.org/ontologias");
            m = ModelFactory.createOntologyModel(getModelSpec(maker), base);
            m.read(serviceURL);
        }

        String queryStringSearchName = "prefix grounding: <http://www.daml.org/services/owl-s/1.1/Grounding.owl#>" + "select ?x where { <" + atomicProcessGrounding + "> grounding:wsdlDocument ?x }";

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

        return WSDLDoc;
    }

    /**
     * Returns the WSDL document URI of the given service URL
     * 
     * @param serviceURL
     *            to extract data from
     * 
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

        String queryStringSearchName = "prefix grounding: <http://www.daml.org/services/owl-s/1.1/Grounding.owl#>" + "select ?x where { ?y grounding:wsdlDocument ?x }";

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

        String queryStringSearchName = "prefix grounding: <http://www.daml.org/services/owl-s/1.1/Grounding.owl#>" + "prefix xsd: <http://www.w3.org/2001/XMLSchema#>" + "select ?x where { ?x grounding:wsdlDocument " + WSDLDoc + " }";

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

        String queryStringSearchName = "prefix grounding: <http://www.daml.org/services/owl-s/1.1/Grounding.owl#>" + "prefix xsd: <http://www.w3.org/2001/XMLSchema#>" + "select ?x where { ?x grounding:hasAtomicProcessGrounding <" + atomicGroundingURI + "> }";

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

        return groundingURI;
    }

    /**
     * Returns a String containing the specification in OWL-S of all groundings
     * of the given service profile
     * 
     * @param serviceProfile
     *            URI to extract data from
     * @param m
     *            Ontology model of Jena DB to query
     * @return a String containing the specification in OWL-S of all groundings
     *         of the given service profile
     * @throws ServiceProfileNotFoundException
     */
    private String getGroundingsOWLS(String serviceProfile, OntModel m) throws ServiceProfileNotFoundException {
        String groundsOWLS = "";
        String serviceURI = getServiceURI(serviceProfile, null, m);
        ArrayList<String> groundings = getGroundings(serviceURI, null, m);
        Iterator<String> iterGrounds = groundings.iterator();
        while (iterGrounds.hasNext()) {
            String groundingURI = iterGrounds.next();
            String atomicProcessGrounding = getAtomicProcessGrounding(groundingURI, null, m);
            String WSDLDocandDatatype = getWSDLDocument(atomicProcessGrounding, null, m);
            StringTokenizer token = new StringTokenizer(WSDLDocandDatatype, "^^");
            String wsdlDoc = token.nextToken();
            String wsdlDatatype = token.nextToken();
            StringTokenizer tokenGroundURI = new StringTokenizer(groundingURI, "#");
            tokenGroundURI.nextToken();
            String groundingURIName = tokenGroundURI.nextToken();

            StringTokenizer tokenAtomicProcess = new StringTokenizer(atomicProcessGrounding, "#");
            tokenAtomicProcess.nextToken();
            String atomicProcessGroundingName = tokenAtomicProcess.nextToken();// baseURI+"#"+tokenAtomicProcess.nextToken();

            groundsOWLS += "<grounding:WsdlGrounding rdf:ID=\"" + groundingURIName + "\">\n" + // "<grounding:WsdlGrounding rdf:ID=\""+baseURI+"#"+groundingURIName+"\">\n"+
                    "\t<service:supportedBy rdf:resource=\"" + "#" + serviceURI + "\"/> \n" + "\t<grounding:hasAtomicProcessGrounding>\n" + "\t<grounding:WsdlAtomicProcessGrounding rdf:ID=\"" + atomicProcessGroundingName + "\"/>" + "\t</grounding:hasAtomicProcessGrounding>\n" + "</grounding:WsdlGrounding>\n";

            groundsOWLS += "<grounding:WsdlAtomicProcessGrounding rdf:about=\"" + "#" + atomicProcessGroundingName + "\">\n" + "\t<grounding:wsdlDocument rdf:datatype=\"" + wsdlDatatype + "\"\n>" + // the
                    // symbol
                    // "\n",
                    // if not, when Jena adds the info to the DB puts some ""
                    // and spaces that causes problems to queries
                    wsdlDoc + "</grounding:wsdlDocument>\n";
            String owlsProcess = getGroundOwlsProcess(atomicProcessGrounding, null, m);
            groundsOWLS += "\t<grounding:owlsProcess rdf:resource=\"" + owlsProcess + "\"/>\n";

            String groundInputMessage = getGroundingInputMessage(atomicProcessGrounding, null, m);
            StringTokenizer inputMessageTok = new StringTokenizer(groundInputMessage, "^^");
            String inputMessage = inputMessageTok.nextToken();
            String inputMessageDatatype = inputMessageTok.nextToken();
            groundsOWLS += "\t<grounding:wsdlInputMessage rdf:datatype=\"" + inputMessageDatatype + "\">" + inputMessage + "</grounding:wsdlInputMessage>\n";
            StringTokenizer outputMessageTok = new StringTokenizer(getGroundingOutputMessage(atomicProcessGrounding, null, m), "^^");
            String outputMessage = outputMessageTok.nextToken();
            String outputMessageDatatype = outputMessageTok.nextToken();
            groundsOWLS += "\t<grounding:wsdlOutputMessage rdf:datatype=\"" + outputMessageDatatype + "\">" + outputMessage + "</grounding:wsdlOutputMessage>\n";

            ArrayList<GroundingInOutput> groundInputs = getGroundingInOutputs(atomicProcessGrounding, null, true, m);
            Iterator<GroundingInOutput> iterIns = groundInputs.iterator();
            while (iterIns.hasNext()) {
                GroundingInOutput in = iterIns.next();

                groundsOWLS += "\t<grounding:wsdlInput>\n\t\t<grounding:WsdlInputMessageMap>\n";
                groundsOWLS += "\t\t<grounding:owlsParameter rdf:resource=\"" + in.getOwlsParameter() + "\"/>\n";

                StringTokenizer messagePartTok = new StringTokenizer(in.getWsdlMessagePart(), "^^");
                String messagePart = messagePartTok.nextToken();
                String messagePartDatatype = messagePartTok.nextToken();
                groundsOWLS += "\t\t<grounding:wsdlMessagePart rdf:datatype=\"" + messagePartDatatype + "\">" + messagePart + "</grounding:wsdlMessagePart>\n";

                groundsOWLS += "\t\t<grounding:xsltTransformationString>" + in.getXsltTransformationString() + "</grounding:xsltTransformationString>\n";
                groundsOWLS += "\t\t</grounding:WsdlInputMessageMap>\n\t</grounding:wsdlInput>\n";
            }

            ArrayList<GroundingInOutput> groundOutputs = getGroundingInOutputs(atomicProcessGrounding, null, false, m);
            Iterator<GroundingInOutput> iterOuts = groundOutputs.iterator();
            while (iterOuts.hasNext()) {
                GroundingInOutput out = iterOuts.next();

                groundsOWLS += "\t<grounding:wsdlOutput>\n\t\t<grounding:WsdlOutputMessageMap>\n";
                groundsOWLS += "\t\t<grounding:owlsParameter rdf:resource=\"" + out.getOwlsParameter() + "\"/>\n";

                StringTokenizer messagePartTok = new StringTokenizer(out.getWsdlMessagePart(), "^^");
                String messagePart = messagePartTok.nextToken();
                String messagePartDatatype = messagePartTok.nextToken();
                groundsOWLS += "\t\t<grounding:wsdlMessagePart rdf:datatype=\"" + messagePartDatatype + "\">" + messagePart + "</grounding:wsdlMessagePart>\n";

                groundsOWLS += "\t\t<grounding:xsltTransformationString>" + out.getXsltTransformationString() + "</grounding:xsltTransformationString>\n";
                groundsOWLS += "\t\t</grounding:WsdlOutputMessageMap>\n\t</grounding:wsdlOutput>\n";
            }

            String groundOperation = getGroundOperation(atomicProcessGrounding, null, m);
            StringTokenizer operationTok = new StringTokenizer(groundOperation, "^^");
            String operation = operationTok.nextToken();
            String operationDatatype = operationTok.nextToken();
            groundsOWLS += "\t<grounding:wsdlOperation>\n\t<grounding:WsdlOperationRef>\n";
            groundsOWLS += "\t<grounding:operation rdf:datatype=\"" + operationDatatype + "\">" + operation + "</grounding:operation>\n";

            String groundPortType = getGroundPortType(atomicProcessGrounding, null, m);
            StringTokenizer portTypeTok = new StringTokenizer(groundPortType, "^^");
            String portType = portTypeTok.nextToken();
            String portTypeDatatype = portTypeTok.nextToken();
            groundsOWLS += "\t<grounding:portType rdf:datatype=\"" + portTypeDatatype + "\">" + portType + "</grounding:portType>\n";
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
     * @param m
     *            Ontology model of Jena DB to query
     * @return a {@link String} containing the specification in OWL-S of all
     *         groundings of the given service URL
     */
    private String getGroundingOWLSfromFile(String serviceProfile, String serviceURL, String registeredServiceURI, String wsdlToRegister, String baseURI, OntModel m) {
        String groundsOWLS = "";

        String atomicProcessGrounding = getAtomicGroundingURIFromWSDLDocumentFromServiceURL(wsdlToRegister, serviceURL);
        String groundingURI = getGroundingURIFromAtomicGrounding(atomicProcessGrounding, serviceURL);

        String WSDLDocandDatatype = getWSDLDocument(atomicProcessGrounding, serviceURL, m);
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

        groundsOWLS += "<grounding:WsdlGrounding rdf:ID=\"" + groundingURIName + "\">\n" + // "<grounding:WsdlGrounding rdf:ID=\""+baseURI+"#"+groundingURIName+"\">\n"+
                "<service:supportedBy rdf:resource=\"" + "#" + registeredServiceURIName + "\"/> \n" + "<grounding:hasAtomicProcessGrounding>\n" + "<grounding:WsdlAtomicProcessGrounding rdf:ID=\"" + atomicProcessGroundingName + "\"/>" + "</grounding:hasAtomicProcessGrounding>\n" + "</grounding:WsdlGrounding>\n";

        groundsOWLS += "<grounding:WsdlAtomicProcessGrounding rdf:about=\"" + "#" + atomicProcessGroundingName + "\">\n" + "<grounding:wsdlDocument rdf:datatype=\"" + wsdlDatatype + "\"\n>" + // the
                // symbol
                // if not, when Jena adds the info to the DB puts some "" and
                // spaces that causes problems to queries
                wsdlDoc + "</grounding:wsdlDocument>\n";
        String owlsProcess = getGroundOwlsProcess(atomicProcessGrounding, serviceURL, m);
        groundsOWLS += "<grounding:owlsProcess rdf:resource=\"" + owlsProcess + "\"/>\n";

        String groundInputMessage = getGroundingInputMessage(atomicProcessGrounding, serviceURL, m);
        StringTokenizer inputMessageTok = new StringTokenizer(groundInputMessage, "^^");
        String inputMessage = inputMessageTok.nextToken();
        String inputMessageDatatype = inputMessageTok.nextToken();
        groundsOWLS += "<grounding:wsdlInputMessage rdf:datatype=\"" + inputMessageDatatype + "\">" + inputMessage + "</grounding:wsdlInputMessage>\n";
        StringTokenizer outputMessageTok = new StringTokenizer(getGroundingOutputMessage(atomicProcessGrounding, serviceURL, m), "^^");
        String outputMessage = outputMessageTok.nextToken();
        String outputMessageDatatype = outputMessageTok.nextToken();
        groundsOWLS += "<grounding:wsdlOutputMessage rdf:datatype=\"" + outputMessageDatatype + "\">" + outputMessage + "</grounding:wsdlOutputMessage>\n";

        ArrayList<GroundingInOutput> groundInputs = getGroundingInOutputs(atomicProcessGrounding, serviceURL, true, m);
        Iterator<GroundingInOutput> iterIns = groundInputs.iterator();
        while (iterIns.hasNext()) {
            GroundingInOutput in = iterIns.next();

            groundsOWLS += "<grounding:wsdlInput>\n<grounding:WsdlInputMessageMap>\n";
            groundsOWLS += "<grounding:owlsParameter rdf:resource=\"" + in.getOwlsParameter() + "\"/>\n";

            StringTokenizer messagePartTok = new StringTokenizer(in.getWsdlMessagePart(), "^^");
            String messagePart = messagePartTok.nextToken();
            String messagePartDatatype = messagePartTok.nextToken();
            groundsOWLS += "<grounding:wsdlMessagePart rdf:datatype=\"" + messagePartDatatype + "\">" + messagePart + "</grounding:wsdlMessagePart>\n";

            groundsOWLS += "<grounding:xsltTransformationString>" + in.getXsltTransformationString() + "</grounding:xsltTransformationString>\n";
            groundsOWLS += "</grounding:WsdlInputMessageMap>\n</grounding:wsdlInput>\n";
        }

        ArrayList<GroundingInOutput> groundOutputs = getGroundingInOutputs(atomicProcessGrounding, serviceURL, false, m);
        Iterator<GroundingInOutput> iterOuts = groundOutputs.iterator();
        while (iterOuts.hasNext()) {
            GroundingInOutput out = iterOuts.next();

            groundsOWLS += "<grounding:wsdlOutput>\n<grounding:WsdlOutputMessageMap>\n";
            groundsOWLS += "<grounding:owlsParameter rdf:resource=\"" + out.getOwlsParameter() + "\"/>\n";

            StringTokenizer messagePartTok = new StringTokenizer(out.getWsdlMessagePart(), "^^");
            String messagePart = messagePartTok.nextToken();
            String messagePartDatatype = messagePartTok.nextToken();
            groundsOWLS += "<grounding:wsdlMessagePart rdf:datatype=\"" + messagePartDatatype + "\">" + messagePart + "</grounding:wsdlMessagePart>\n";

            groundsOWLS += "<grounding:xsltTransformationString>" + out.getXsltTransformationString() + "</grounding:xsltTransformationString>\n";
            groundsOWLS += "</grounding:WsdlOutputMessageMap>\n</grounding:wsdlOutput>\n";
        }

        String groundOperation = getGroundOperation(atomicProcessGrounding, serviceURL, m);
        StringTokenizer operationTok = new StringTokenizer(groundOperation, "^^");
        String operation = operationTok.nextToken();
        String operationDatatype = operationTok.nextToken();
        groundsOWLS += "<grounding:wsdlOperation>\n<grounding:WsdlOperationRef>\n";
        groundsOWLS += "<grounding:operation rdf:datatype=\"" + operationDatatype + "\">" + operation + "</grounding:operation>\n";

        String groundPortType = getGroundPortType(atomicProcessGrounding, serviceURL, m);
        StringTokenizer portTypeTok = new StringTokenizer(groundPortType, "^^");
        String portType = portTypeTok.nextToken();
        String portTypeDatatype = portTypeTok.nextToken();
        groundsOWLS += "<grounding:portType rdf:datatype=\"" + portTypeDatatype + "\">" + portType + "</grounding:portType>\n";
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
     * @param m
     *            Ontology model of Jena DB to query
     * @return the grounding input message of the given atomic process grounding
     *         URI
     */
    private String getGroundingInputMessage(String atomicProcessGrounding, String serviceURL, OntModel m) {
        String inputMessage = "";

        if (serviceURL == null) {

        } else {
            ModelMaker maker = ModelFactory.createMemModelMaker();
            Model base = maker.createModel("http://example.org/ontologias");
            m = ModelFactory.createOntologyModel(getModelSpec(maker), base);
            m.read(serviceURL);
        }

        String queryStringSearchName = "prefix grounding: <http://www.daml.org/services/owl-s/1.1/Grounding.owl#>" + "select ?x where { <" + atomicProcessGrounding + "> grounding:wsdlInputMessage ?x }";

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
     * @param m
     *            Ontology model of Jena DB to query
     * @return the grounding output message of the given atomic process
     *         grounding URI
     */
    private String getGroundingOutputMessage(String atomicProcessGrounding, String serviceURL, OntModel m) {
        String outputMessage = "";

        if (serviceURL == null) {

        } else {
            ModelMaker maker = ModelFactory.createMemModelMaker();
            Model base = maker.createModel("http://example.org/ontologias");
            m = ModelFactory.createOntologyModel(getModelSpec(maker), base);
            m.read(serviceURL);
        }

        String queryStringSearchName = "prefix grounding: <http://www.daml.org/services/owl-s/1.1/Grounding.owl#>" + "select ?x where { <" + atomicProcessGrounding + "> grounding:wsdlOutputMessage ?x }";

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
     * @param m
     *            Ontology model of Jena DB to query
     * @return the grounding owls process of the given atomic process grounding
     */
    private String getGroundOwlsProcess(String atomicProcessGrounding, String serviceURL, OntModel m) {

        String owlsProcess = "";

        if (serviceURL == null) {

        } else {
            ModelMaker maker = ModelFactory.createMemModelMaker();
            Model base = maker.createModel("http://example.org/ontologias");
            m = ModelFactory.createOntologyModel(getModelSpec(maker), base);
            m.read(serviceURL);
        }

        String queryStringSearchName = "prefix grounding: <http://www.daml.org/services/owl-s/1.1/Grounding.owl#>" + "select ?x where { <" + atomicProcessGrounding + "> grounding:owlsProcess ?x }";

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
     * @param m
     *            Ontology model of Jena DB to query
     * @return an {@link ArrayList} of {@link GroundingInOutput} with the data
     *         of the grounding inputs or outputs of the given atomic process
     *         grounding
     */
    private ArrayList<GroundingInOutput> getGroundingInOutputs(String atomicProcessGrounding, String serviceURL, boolean input, OntModel m) {

        ArrayList<GroundingInOutput> groundInputs = new ArrayList<GroundingInOutput>();

        if (serviceURL == null) {

        } else {
            ModelMaker maker = ModelFactory.createMemModelMaker();
            Model base = maker.createModel("http://example.org/ontologias");
            m = ModelFactory.createOntologyModel(getModelSpec(maker), base);
            m.read(serviceURL);
        }

        String wsdlInputOutput = "";
        if (input) {
            wsdlInputOutput = "grounding:wsdlInput";
        } else {
            wsdlInputOutput = "grounding:wsdlOutput";
        }

        String queryStringSearchName = "prefix grounding: <http://www.daml.org/services/owl-s/1.1/Grounding.owl#>" + "select ?x ?y ?z where { <" + atomicProcessGrounding + "> " + wsdlInputOutput + "[  grounding:owlsParameter ?x ; grounding:wsdlMessagePart ?y ; grounding:xsltTransformationString ?z ] }";

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
                GroundingInOutput groundIn = new GroundingInOutput(owlsParameter, wsdlMessagePart, xsltTransformationString);
                groundInputs.add(groundIn);
            }

        }// end if
        else {
            System.out.println("resultsSearchName is null");
        }

        qeSearchName.close();

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
     * @param m
     *            Ontology model of Jena DB to query
     * @return the grounding operation of the given atomic process grounding
     */
    private String getGroundOperation(String atomicProcessGrounding, String serviceURL, OntModel m) {

        String operation = "";

        if (serviceURL == null) {

        } else {
            ModelMaker maker = ModelFactory.createMemModelMaker();
            Model base = maker.createModel("http://example.org/ontologias");
            m = ModelFactory.createOntologyModel(getModelSpec(maker), base);
            m.read(serviceURL);
        }

        String queryStringSearchName = "prefix grounding: <http://www.daml.org/services/owl-s/1.1/Grounding.owl#>" + "select ?x where { <" + atomicProcessGrounding + "> grounding:wsdlOperation" + "[  grounding:operation ?x ; ] }";

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
     * @param m
     *            Ontology model of Jena DB to query
     * @return the grounding port type of the given atomic process grounding
     */
    private String getGroundPortType(String atomicProcessGrounding, String serviceURL, OntModel m) {

        String portType = "";

        if (serviceURL == null) {

        } else {
            ModelMaker maker = ModelFactory.createMemModelMaker();
            Model base = maker.createModel("http://example.org/ontologias");
            m = ModelFactory.createOntologyModel(getModelSpec(maker), base);
            m.read(serviceURL);
        }

        String queryStringSearchName = "prefix grounding: <http://www.daml.org/services/owl-s/1.1/Grounding.owl#>" + "select ?x where { <" + atomicProcessGrounding + "> grounding:wsdlOperation" + "[  grounding:portType ?x ; ] }";

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
     * @param m
     *            Ontology model of Jena DB to query
     * @return an {@link ArrayList} with the provider URIs of the given service
     *         profile URI
     * @throws ServiceProfileNotFoundException
     */
    private ArrayList<String> getProviders(String serviceProfile, String serviceURL, OntModel m) throws ServiceProfileNotFoundException {
        ArrayList<String> providers = new ArrayList<String>();

        try {
            if (serviceURL == null) {

            } else {
                ModelMaker maker = ModelFactory.createMemModelMaker();
                Model base = maker.createModel("http://example.org/ontologias");
                m = ModelFactory.createOntologyModel(getModelSpec(maker), base);
                m.read(serviceURL);
                serviceProfile = getProfileURIfromURL(serviceURL);
            }

            String queryStringSearchName = "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>" + "prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>" + "select ?x where { " + "<" + serviceProfile + ">" + " profile:contactInformation ?x }";

            Query querySearchName = QueryFactory.create(queryStringSearchName);

            QueryExecution qeSearchName = QueryExecutionFactory.create(querySearchName, m);
            ResultSet resultsSearchName = qeSearchName.execSelect();

            if (resultsSearchName != null) {

                while (resultsSearchName.hasNext()) {
                    String providerURI = resultsSearchName.next().getResource("x").toString();
                    providers.add(providerURI);
                }
            }
        } catch (Exception e) {
            throw new ServiceProfileNotFoundException(l10n.getMessage(MessageID.SERVICE_PROFILE_NOT_FOUND, serviceProfile));
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
     * @param m
     *            Ontology model of Jena DB to query
     * @return the OWL-S specification of all providers and their information of
     *         the given service profile URI
     */
    private String getProvidersOWLS(String serviceProfile, String serviceURL, OntModel m) {

        String providersOWLS = "";

        if (serviceURL == null) {

        } else {
            ModelMaker maker = ModelFactory.createMemModelMaker();
            Model base = maker.createModel("http://example.org/ontologias");
            m = ModelFactory.createOntologyModel(getModelSpec(maker), base);
            m.read(serviceURL);
        }

        String queryStringSearchName = "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>" + "prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>" + "select ?x where { " + "<" + serviceProfile + ">" + " profile:contactInformation ?x }";

        Query querySearchName = QueryFactory.create(queryStringSearchName);

        QueryExecution qeSearchName = QueryExecutionFactory.create(querySearchName, m);
        ResultSet resultsSearchName = qeSearchName.execSelect();

        String providerURI = "", entityID = "", entityType = "", language = "", performative = "";

        if (resultsSearchName != null) {

            while (resultsSearchName.hasNext()) {
                providerURI = resultsSearchName.next().getResource("x").toString();

                entityID = getProviderParameter(providerURI, "entityID", serviceURL, m);
                entityType = getProviderParameter(providerURI, "entityType", serviceURL, m);
                language = getProviderParameter(providerURI, "language", serviceURL, m);
                performative = getProviderParameter(providerURI, "performative", serviceURL, m);

                StringTokenizer tokenProvURI = new StringTokenizer(providerURI, "#");
                tokenProvURI.nextToken();
                String providerName = tokenProvURI.nextToken();

                providersOWLS += "<profile:contactInformation>" + "\n" + "\t<provider:Provider rdf:ID=\"" + providerName + "\">" + "\n" + "\t\t<provider:entityID rdf:datatype=\"^^xsd;string\">" + entityID + "</provider:entityID>" + "\n" + "\t\t<provider:entityType rdf:datatype=\"^^xsd;string\">" + entityType + "</provider:entityType>" + "\n" + "\t\t<provider:language rdf:datatype=\"^^xsd;string\">" + language + "</provider:language>" + "\n"
                        + "\t\t<provider:performative rdf:datatype=\"^^xsd;string\">" + performative + "</provider:performative>" + "\n" + "\t</provider:Provider>" + "\n" + "</profile:contactInformation>" + "\n";

            }

        }// end if
        else {
            System.out.println("resultsSearchName is null");
        }

        // close the query
        qeSearchName.close();

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
     * @param m
     *            Ontology model of Jena DB to query
     * @return the value of the given provider parameter
     */
    private String getProviderParameter(String providerURI, String parameter, String serviceURL, OntModel m) {

        if (serviceURL == null) {

        } else {
            ModelMaker maker = ModelFactory.createMemModelMaker();
            Model base = maker.createModel("http://example.org/ontologias");
            m = ModelFactory.createOntologyModel(getModelSpec(maker), base);
            m.read(serviceURL);
        }

        String queryStringSearchName2 = "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>" + "prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>" + "prefix provider: <http://localhost:8080/ontologies/provider.owl#>" + "select ?x where { <" + providerURI + "> provider:" + parameter + " ?x }";

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
     * @param m
     *            Ontology model of Jena DB to query
     */
    private void removeGrounding(String grounding, OntModel m) {

        String atomicProcessGrounding = getAtomicProcessGrounding(grounding, null, m);
        String baseWSDLURL = getGroundingWSDLBaseURI(atomicProcessGrounding, m);

        deleteWSDLMessagePart(baseWSDLURL, m);
        deleteWSDLPortType(baseWSDLURL, m);
        deleteWSDLOperation(baseWSDLURL, m);

        deleteProcessGrounding(grounding, atomicProcessGrounding, m);

    }

    /**
     * Removes the provider or grounding of the given service profile
     * 
     * @param serviceProfile
     *            of the provider
     * @param providerName
     * @param m
     *            Ontology model of Jena DB to query
     * @throws ServiceProfileNotFoundException
     */
    private void removeProvider(String serviceProfile, String providerName, OntModel m) throws ServiceProfileNotFoundException {

    	try{
        StringTokenizer tokenServiceProf = new StringTokenizer(serviceProfile, "#");
        String baseURI = tokenServiceProf.nextToken();
        String profileName = tokenServiceProf.nextToken();
        String serviceURI = getServiceURI(serviceProfile, null, m);

        // search if the given provider name is specified as a provider of
        // the given service profile
        boolean found = false;
        ArrayList<String> providers = getProviders(serviceProfile, null, m);
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

            ArrayList<String> grounds = getGroundings(serviceURI, null, m);
            Iterator<String> iterGrounds = grounds.iterator();
            while (iterGrounds.hasNext()) {
                String ground = iterGrounds.next();
                String groundN = ground.split("#")[1];
                if (groundName.equals(groundN))
                    foundGround = true;
            }
            if (!foundGround) {

                return;
            } else {
                removeGrounding(providerName, m);
            }
        } else {

            // Delete provider
            String delete = "prefix xsd: <http://www.w3.org/2001/XMLSchema#>" + "prefix service: <http://www.daml.org/services/owl-s/1.1/Service.owl#>" + "prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>" + "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>" + "prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>" + "prefix mind: <" + baseURI + "#>" + "delete {?x ?y ?z} " + "where" + "{mind:" + profileName + " ?y ?z"
                    + " filter ( ?y = profile:contactInformation " + "&& ?z = <" + baseURI + "#" + providerName + ">" + ")" + "?x ?y ?z}";

            String delete2 = "prefix xsd: <http://www.w3.org/2001/XMLSchema#>" + "prefix service: <http://www.daml.org/services/owl-s/1.1/Service.owl#>" + "prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>" + "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>" + "prefix provider: <http://localhost:8080/ontologies/provider.owl#>" + "prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>" + "prefix mind: <" + baseURI + "#>" + "delete {?x ?y ?z} " + "where"
                    + "{mind:" + providerName + " ?y ?z" + " filter ( ( ?y = provider:entityID " + "|| ?y = provider:entityType " + "|| ?y = provider:language " + "|| ?y = provider:performative " + "|| ?z = provider:Provider )" + "&& ?x = <" + baseURI + "#" + providerName + ">" + ")" + "?x ?y ?z}";

            QuerySolution querysol = new QuerySolutionMap();
            UpdateAction.parseExecute(delete, m, querysol);
            UpdateAction.parseExecute(delete2, m, querysol);

        }
    	}catch(Exception e){
    		throw new ServiceProfileNotFoundException(l10n.getMessage(MessageID.SERVICE_PROFILE_NOT_FOUND, serviceProfile));
    	}

    }

    /**
     * Removes the given process from the Jena DB
     * 
     * @param serviceProcess
     *            URI to remove
     * @param serviceProfile
     *            URI that it is attached the process to remove
     * @param m
     *            Ontology model of Jena DB to query
     * @throws ServiceProfileNotFoundException
     */
    private void removeProcess(String serviceProcess, String serviceProfile, OntModel m) throws ServiceProfileNotFoundException {

        if (DEBUG) {
            System.out.println("Removing groundings and process ... ");
        }

        String serviceURI = getServiceURI(serviceProfile, null, m);
        ArrayList<String> groundings = getGroundings(serviceURI, null, m);
        Iterator<String> iterGrounds = groundings.iterator();
        while (iterGrounds.hasNext()) {
            String groundingURI = iterGrounds.next();
            removeGrounding(groundingURI, m);
        }

        deleteProcessInputs(serviceProcess, m);
        deleteProcessOutputs(serviceProcess, m);

        deleteProcess(serviceProcess, m);

    }

    /**
     * Removes a service profile from the Jena DB.
     * 
     * @param serviceProfile
     *            to remove
     * @param serviceURI
     *            of the service profile to remove
     * @param m
     *            Ontology model of Jena DB to query
     */
    private void removeProfile(String serviceProfile, String serviceURI, OntModel m) {

        StringTokenizer servProfTok = new StringTokenizer(serviceProfile, "#");
        String baseURL = servProfTok.nextToken();

        // Delete profile tuples where the property is profile
        String update = "prefix xsd: <http://www.w3.org/2001/XMLSchema#>" + "prefix service: <http://www.daml.org/services/owl-s/1.1/Service.owl#>" + "prefix proc: <http://www.daml.org/services/owl-s/1.1/Process.owl#>" + "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>" + " delete {?x ?y ?z}" + " where { <" + serviceProfile + "> ?y ?z" + " filter ( ?y = profile:hasInput " + "|| ?y = profile:hasOutput " + "|| ?y = profile:serviceName " + "|| ?y = profile:textDescription "
                + "|| ?y = profile:has_process " + "|| ?y = service:isPresentedBy " + "|| ?y = service:presents " + "|| ?y = profile:contactInformation " + "|| ?z = profile:Profile " + ")" + "?x ?y ?z}";

        String update2 = "prefix xsd: <http://www.w3.org/2001/XMLSchema#>" + "prefix service: <http://www.daml.org/services/owl-s/1.1/Service.owl#>" + "prefix proc: <http://www.daml.org/services/owl-s/1.1/Process.owl#>" + "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>" + "delete {?x ?y ?z}" + " where { <" + serviceURI + "> ?y ?z" + " filter ( ?z = service:Service " + "|| ?y = service:presents " + ")" + "?x ?y ?z}";

        String update3 = "prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>" + "prefix owl: <http://www.w3.org/2002/07/owl#>" + "delete {?x ?y ?z} " + "where" + "{ <" + baseURL + "> ?y ?z" + " filter ( ( ?y = owl:imports" + "|| ?z = owl:Ontology )" + "&& ?x = <" + baseURL + ">" + ")" + "?x ?y ?z}";

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
     * @param m
     *            Ontology model of Jena DB to query
     * @return the WSDL base URI of the given atomic process grounding
     */
    private String getGroundingWSDLBaseURI(String atomicProcessGrounding, OntModel m) {

        String WSDLBaseURI = "";

        String queryStringDocWSDL = "prefix xsd: <http://www.w3.org/2001/XMLSchema#>" + "prefix service: <http://www.daml.org/services/owl-s/1.1/Service.owl#>" + "prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>" + "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>" + "prefix grounding: <http://www.daml.org/services/owl-s/1.1/Grounding.owl#>" + " select ?x " + "where {" + "<" + atomicProcessGrounding + ">" + " grounding:wsdlInputMessage ?x" + "}";

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

        return WSDLBaseURI;

    }

    /**
     * Deletes the WSLDMessagePart of the grounding with the given base WSDL URI
     * 
     * @param baseWSDLURL
     * @param m
     *            Ontology model of Jena DB to query
     */
    private void deleteWSDLMessagePart(String baseWSDLURL, OntModel m) {

        String queryStr = "prefix xsd: <http://www.w3.org/2001/XMLSchema#>" + "prefix service: <http://www.daml.org/services/owl-s/1.1/Service.owl#>" + "prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>" + "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>" + "prefix grounding: <http://www.daml.org/services/owl-s/1.1/Grounding.owl#>" + "select ?y " + "where {" + "?x grounding:wsdlMessagePart ?y " + "}";

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
                    String updateWSDLMessageMap = "prefix xsd: <http://www.w3.org/2001/XMLSchema#>" + "prefix service: <http://www.daml.org/services/owl-s/1.1/Service.owl#>" + "prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>" + "prefix grounding: <http://www.daml.org/services/owl-s/1.1/Grounding.owl#>" + "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>" + "prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>" + "delete {?x ?y ?z}" + "where"
                            + "{?x ?y " + wsdlInOutput + " filter ( ?y = grounding:wsdlMessagePart " + ")" + "?x ?y ?z}";

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

    }

    /**
     * Deletes the WSDLPortType of the grounding with the given base WSDL URI
     * 
     * @param baseWSDLURL
     * @param m
     *            Ontology model of Jena DB to query
     */
    private void deleteWSDLPortType(String baseWSDLURL, OntModel m) {

        String queryStr = "prefix xsd: <http://www.w3.org/2001/XMLSchema#>" + "prefix service: <http://www.daml.org/services/owl-s/1.1/Service.owl#>" + "prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>" + "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>" + "prefix grounding: <http://www.daml.org/services/owl-s/1.1/Grounding.owl#>" + "select ?y " + "where {" + "?x grounding:portType ?y " + "}";

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
                    String updateWSDLMessageMap = "prefix xsd: <http://www.w3.org/2001/XMLSchema#>" + "prefix service: <http://www.daml.org/services/owl-s/1.1/Service.owl#>" + "prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>" + "prefix grounding: <http://www.daml.org/services/owl-s/1.1/Grounding.owl#>" + "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>" + "prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>" + "delete {?x ?y ?z}" + "where"
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

    }

    /**
     * Deletes the WSDLOperation of the grounding with the given base WSDL URI
     * 
     * @param baseWSDLURL
     * @param m
     *            Ontology model of Jena DB to query
     */
    private void deleteWSDLOperation(String baseWSDLURL, OntModel m) {

        String queryStr = "prefix xsd: <http://www.w3.org/2001/XMLSchema#>" + "prefix service: <http://www.daml.org/services/owl-s/1.1/Service.owl#>" + "prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>" + "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>" + "prefix grounding: <http://www.daml.org/services/owl-s/1.1/Grounding.owl#>" + "select ?y " + "where {" + "?x grounding:operation ?y " + "}";

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
                    String updateWSDLMessageMap = "prefix xsd: <http://www.w3.org/2001/XMLSchema#>" + "prefix service: <http://www.daml.org/services/owl-s/1.1/Service.owl#>" + "prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>" + "prefix grounding: <http://www.daml.org/services/owl-s/1.1/Grounding.owl#>" + "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>" + "prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>" + "delete {?x ?y ?z}" + "where"
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

    }

    /**
     * Deletes the process inputs of the given service process
     * 
     * @param serviceProcess
     * @param m
     *            Ontology model of Jena DB to query
     */
    private void deleteProcessInputs(String serviceProcess, OntModel m) {

        // Query to get the service inputs tuples related with the service
        // process

        String queryStr = "prefix xsd: <http://www.w3.org/2001/XMLSchema#>" + "prefix service: <http://www.daml.org/services/owl-s/1.1/Service.owl#>" + "prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>" + "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>" + "prefix grounding: <http://www.daml.org/services/owl-s/1.1/Grounding.owl#>" + " select ?x " + "where { <" + serviceProcess + "> process:hasInput ?x }";

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
                String updateInput = "prefix xsd: <http://www.w3.org/2001/XMLSchema#>" + "prefix service: <http://www.daml.org/services/owl-s/1.1/Service.owl#>" + "prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>" + "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>" + "prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>" + "delete {?x ?y ?z}" + "where" + "{ <" + processInput + "> ?y ?z" + " filter ( ?y = rdfs:label " + "|| ?y = process:parameterType "
                        + "|| ?z = process:Input " + ")" + "?x ?y ?z}";

                // Delete the output tuple related with the service grounding
                String updateGroundInput = "prefix xsd: <http://www.w3.org/2001/XMLSchema#>" + "prefix service: <http://www.daml.org/services/owl-s/1.1/Service.owl#>" + "prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>" + "prefix grounding: <http://www.daml.org/services/owl-s/1.1/Grounding.owl#>" + "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>" + "prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>" + "delete {?x ?y ?z}" + "where" + "{?x ?y <"
                        + processInput + ">" + " filter ( ?y = grounding:owlsParameter " + ")" + "?x ?y ?z}";

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
     * @param m
     *            Ontology model of Jena DB to query
     */
    private void deleteProcessOutputs(String serviceProcess, OntModel m) {

        String queryStr = "prefix xsd: <http://www.w3.org/2001/XMLSchema#>" + "prefix service: <http://www.daml.org/services/owl-s/1.1/Service.owl#>" + "prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>" + "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>" + "prefix grounding: <http://www.daml.org/services/owl-s/1.1/Grounding.owl#>" + " select ?x " + "where { <" + serviceProcess + "> process:hasOutput ?x }";

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
                String updateOutput = "prefix xsd: <http://www.w3.org/2001/XMLSchema#>" + "prefix service: <http://www.daml.org/services/owl-s/1.1/Service.owl#>" + "prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>" + "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>" + "prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>" + "delete {?x ?y ?z}" + "where" + "{ <" + processOutput + "> ?y ?z" + " filter ( ?y = rdfs:label "
                        + "|| ?y = process:parameterType " + "|| ?z = process:Output " + ")" + "?x ?y ?z}";

                // Delete the output tuple related with the service grounding
                String updateGroundOutput = "prefix xsd: <http://www.w3.org/2001/XMLSchema#>" + "prefix service: <http://www.daml.org/services/owl-s/1.1/Service.owl#>" + "prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>" + "prefix grounding: <http://www.daml.org/services/owl-s/1.1/Grounding.owl#>" + "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>" + "prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>" + "delete {?x ?y ?z}" + "where" + "{?x ?y <"
                        + processOutput + ">" + " filter ( ?y = grounding:owlsParameter " + ")" + "?x ?y ?z}";

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
     * @param m
     *            Ontology model of Jena DB to query
     */
    private void deleteProcessGrounding(String grounding, String atomicProcessGrounding, OntModel m) {

        // Deletes Query Strings
        String updateGround = "prefix xsd: <http://www.w3.org/2001/XMLSchema#>" + "prefix service: <http://www.daml.org/services/owl-s/1.1/Service.owl#>" + "prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>" + "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>" + "prefix grounding: <http://www.daml.org/services/owl-s/1.1/Grounding.owl#>" + "prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>" + "delete {?x ?y ?z .}" + "where" + "{ <" + grounding
                + "> ?y ?z ." + " filter ( ?y = grounding:owlsProcess" + "|| ?y = grounding:hasAtomicProcessGrounding " + "|| ?y = service:supportedBy " + "|| ?z = grounding:WsdlGrounding " + ")" + "?x ?y ?z .}";

        String updateGroundWSDL = "prefix xsd: <http://www.w3.org/2001/XMLSchema#>" + "prefix service: <http://www.daml.org/services/owl-s/1.1/Service.owl#>" + "prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>" + "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>" + "prefix grounding: <http://www.daml.org/services/owl-s/1.1/Grounding.owl#>" + "prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>" + "delete {?x ?y ?z .}" + "where" + "{ <"
                + atomicProcessGrounding + "> ?y ?z ." + " filter ( ?y = grounding:owlsProcess" + "|| ?y = grounding:wsdlDocument " + "|| ?y = grounding:wsdlInput " + "|| ?y = grounding:wsdlInputMessage " + "|| ?y = grounding:wsdlOperation " + "|| ?y = grounding:wsdlOutput " + "|| ?y = grounding:wsdlOutputMessage " + "|| ?y = grounding:xsltTransformationString " + "|| ?z = grounding:WsdlInputMessageMap " + "|| ?z = grounding:WsdlOutputMessageMap "
                + "|| ?z = grounding:WsdlAtomicProcessGrounding " + ")" + "?x ?y ?z .} ";

        String updateGroundSupportsProperty = "prefix xsd: <http://www.w3.org/2001/XMLSchema#>" + "prefix service: <http://www.daml.org/services/owl-s/1.1/Service.owl#>" + "prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>" + "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>" + "prefix grounding: <http://www.daml.org/services/owl-s/1.1/Grounding.owl#>" + "prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>" + "delete {?x ?y ?z}" + "where" + "{?x ?y <"
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
     * @param m
     *            Ontology model of Jena DB to query
     */
    private void deleteProcess(String serviceProcess, OntModel m) {

        // Delete the process general description
        String updateProcess = "prefix xsd: <http://www.w3.org/2001/XMLSchema#>" + "prefix service: <http://www.daml.org/services/owl-s/1.1/Service.owl#>" + "prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>" + "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>" + "prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>" + "delete {?x ?y ?z}" + "where" + "{ <" + serviceProcess + "> ?y ?z" + " filter ( ?y = process:hasOutput " + "|| ?y = process:hasInput "
                + "|| ?y = service:describes " + "|| ?y = process:hasPrecondition " + "|| ?z = process:AtomicProcess " + ")" + "?x ?y ?z}";

        // Delete the profile property where the process appears
        String updateDescribedByProperty = "prefix xsd: <http://www.w3.org/2001/XMLSchema#>" + "prefix service: <http://www.daml.org/services/owl-s/1.1/Service.owl#>" + "prefix process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>" + "prefix profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>" + "prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>" + "delete {?x ?y ?z}" + "where" + "{?x ?y <" + serviceProcess + ">" + " filter ( ?y = service:describedBy " + ")" + "?x ?y ?z}";

        // Execute the query
        QuerySolution querysol = new QuerySolutionMap();
        UpdateAction.parseExecute(updateProcess, m, querysol);
        UpdateAction.parseExecute(updateDescribedByProperty, m, querysol);

    }// DeleteProcess

}
