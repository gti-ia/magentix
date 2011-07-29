package es.upv.dsic.gti_ia.MMService;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;

import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.Security;
import java.security.Signature;
import java.security.SignatureException;
import java.security.UnrecoverableKeyException;
import java.security.KeyStore.PrivateKeyEntry;
import java.security.cert.Certificate;
import java.security.cert.CertificateException; //import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;
import java.util.Vector;

import sun.security.x509.AlgorithmId;
import sun.security.x509.CertificateAlgorithmId;
import sun.security.x509.CertificateExtensions;
import sun.security.x509.CertificateIssuerName;
import sun.security.x509.CertificateSerialNumber;
import sun.security.x509.CertificateSubjectName;
import sun.security.x509.CertificateValidity;
import sun.security.x509.CertificateVersion;
import sun.security.x509.CertificateX509Key;
import sun.security.x509.KeyIdentifier;
import sun.security.x509.SubjectKeyIdentifierExtension;
import sun.security.x509.X500Name;
import sun.security.x509.X500Signer;
import sun.security.x509.X509CertImpl;
import sun.security.x509.X509CertInfo;

import org.apache.axis2.context.MessageContext;
import org.apache.log4j.Logger;
import org.apache.qpid.console.Broker;
import org.apache.qpid.console.QMFObject;
import org.apache.qpid.console.Session;
import org.apache.ws.security.WSSecurityEngineResult;
import org.apache.ws.security.handler.WSHandlerConstants;
import org.apache.ws.security.handler.WSHandlerResult;
import org.opensaml.SAMLAssertion;





/** This class is a core for MMS service, It is responsible for issuing certificates using Magentix2 CA*/
@SuppressWarnings("restriction")
public class AgentCertificate {

	private FileWriter file = null;
	private PrintWriter pw = null;
	private ArrayList<QMFObject> qmf = new ArrayList<QMFObject>();
	private HashMap<String, Object> hash = new HashMap<String, Object>();
	private Session sess = new Session();
	private Properties properties = new Properties();
	private ArrayList<String> reservedNames = new ArrayList<String>();
	private ByteArrayOutputStream outStream = null;
	private Calendar calendar;
	private String id="";
	private boolean existAgent = false;
	private String connectionBroker = "";
	Broker broker = null;
	/** 
	 * 
	 * The connection to the database 
	 * */
	public Connection connection = null;


	static Logger logger = Logger.getLogger(AgentCertificate.class);

	/**
	 * This method returns a new signed certificate 
	 * @param agentName Common Name=Name of agent
	 * @param pk This is a user public key.
	 * @return
	 */
	@SuppressWarnings({ "unchecked" })
	public byte[] newCertificate(String agentName, byte[] pk) {

		reservedNames.add("mms");
		reservedNames.add("tm");
		try {
			properties.load(PWCBHandler.class.getResourceAsStream("/"+"securityAdmin.properties"));
		} catch (FileNotFoundException e) {
			System.err.println(e);
		} catch (IOException e) {
			System.err.println(e);
		}
		try {





			MessageContext ctx = MessageContext.getCurrentMessageContext();
			Vector<WSHandlerResult> results = (Vector<WSHandlerResult>) ctx
			.getProperty(WSHandlerConstants.RECV_RESULTS);
			if (results != null) {
				//En aço podem comprovar que hi ha privacitat, ja que username sera null.
				WSHandlerResult result = (WSHandlerResult) results.get(0);

				WSSecurityEngineResult wsSecurityEngineResult = (WSSecurityEngineResult) result
				.getResults().get(1);

				SAMLAssertion assertion = ((SAMLAssertion) wsSecurityEngineResult

						.get(WSSecurityEngineResult.TAG_SAML_ASSERTION));

				id = "Id: " + assertion.getIssuer() +" Issuer: " +assertion.getId();



			}



			//El nombre del agente esta reservado o ese agente puede ser dado de alta por ese usuario?
			if (!reservedNames.contains(agentName) && !existAgent(agentName, id))
			{


				logger.info("Creating a new Agent certificate with Common Name = "
						+ agentName);





				// pasar de byte a x500Name
				ByteArrayInputStream bis = new ByteArrayInputStream(pk);

				ObjectInputStream ois = new ObjectInputStream(bis);

				Object okey = ois.readObject();

				PublicKey pkey = (PublicKey) okey;
				ois.close();

				bis.close();

				String sigAlg = properties.getProperty("sigAlg");
				double validity =  Double.valueOf(properties.getProperty("Validity")).doubleValue();

				PrivateKeyEntry pke = this.generateMMSPrivateKeyEntry(properties);


				// Que información contendrá el certificados que emita.
				String commonName = agentName;
				String organizationalUnit = properties
				.getProperty("organizationalUnit");
				String organization = properties.getProperty("organization");
				String city = properties.getProperty("city");
				String state = properties.getProperty("state");
				String country = properties.getProperty("country");

				X500Name x500Name;
				x500Name = new X500Name(commonName, organizationalUnit,
						organization, city, state, country);

				Certificate[] certs = this.generateAndSignedCertificate(x500Name,
						sigAlg, validity, pke, pkey);

				outStream = new ByteArrayOutputStream();
				ObjectOutputStream os = new ObjectOutputStream(outStream);

				os.writeObject(certs);
				os.close();


				// Especificar la ubicación del archivo del almacén de certificados
				// en los que se confía.
				System.setProperty("javax.net.ssl.trustStore", properties
						.getProperty("TrustStorePath"));
				System.setProperty("javax.net.ssl.trustStorePassword", properties
						.getProperty("TrustStorePassword"));

				// Especificar la ubicación del almacén de certificados keyStore,
				// tendremos que tener un certificado en cual confie el broker.
				System.setProperty("javax.net.ssl.keyStore", properties
						.getProperty("KeyStorePath"));
				System.setProperty("javax.net.ssl.keyStorePassword", properties
						.getProperty("KeyStorePassword"));

				//Solamente cuando se cree el agente por primera vez. Si no existe el agente.
				if (!existAgent)
				{

				
					//Conectamos con el broker. Podemos provar 2 veces

					this.connectToBroker();

					// Escribimos por cada agente las siguientes lineas con los permisos necesarios.


					String command = String.format("\n" 
							+ "acl allow "+agentName+"@QPID all queue name=" + agentName + "\n"
							+ "acl allow "+agentName+"@QPID all exchange name=amq.direct routingkey="+ agentName +"\n"
							+ "acl allow "+agentName+"@QPID all queue name="+agentName+".trace" +"\n"
							+ "acl allow "+agentName+"@QPID bind exchange name=amq.match routingkey="+agentName+".system.all" +"\n"
							+ "acl allow "+agentName+"@QPID bind exchange name=amq.match routingkey="+agentName+".system.direct"+"\n"
							+ "acl allow "+agentName+"@QPID unbind exchange name=amq.match routingkey="+agentName+".system.all" +"\n"
							+ "acl allow "+agentName+"@QPID unbind exchange name=amq.match routingkey="+agentName+".system.direct"+"\n"
							+ "acl allow "+agentName+"@QPID publish exchange name=amq.match");

					//Escribimos el fichero acl.
					this.writeAclFile(command, properties.getProperty("ACLPath"));


					// Recargamos el fichero.
					this.reloadACLFile();




					this.closeSession();
				}

				existAgent = false;
				this.registerAgentBD(agentName, id);

				this.closeConnection();
				return outStream.toByteArray();
			}
			else//Si el nombre esta reservado, como por ejemplo MMS.
			{
				calendar = Calendar.getInstance();
				//Registramos quin intenta acceder a nombres reservados
				String commandLog = "=====================WARNING========================== \n Session: "+ calendar.getTime().toString() +"\n User name: " + id +"\n Agent Name: "+ agentName +"\n======================================================";

				this.writeAclFile(commandLog, properties.getProperty("Userlog"));			

				outStream = new ByteArrayOutputStream();
				ObjectOutputStream os = new ObjectOutputStream(outStream);

				os.writeObject("Unable to register the agent. Another user has already registered that name.");
				os.close();

				return outStream.toByteArray();
			}




		} catch (Exception e) {
			logger.error("Error caught: " + e);
			return null;
		}

	}

	/**
	 * Inserts or updates a new register in the database with the name of agent, user, date first and date last.
	 * @param _agentName
	 * @param _userName
	 */
	private void registerAgentBD(String _agentName, String _userName)
	{
		this.openConnection();

		ResultSet rs = null;
		calendar = Calendar.getInstance();
		try {
			// Creates a Statement, to query
			Statement s = connection.createStatement();
			String sql = "SELECT agent,user,dateFirst,dateLast FROM registers WHERE agent = '"+_agentName+"'";
			sql.toLowerCase();
			rs = s.executeQuery(sql);

			if (rs.next())
			{

				sql = "UPDATE registers SET dateLast = '"+calendar.getTime()+"' WHERE agent = '"+_agentName+"'";
				sql.toLowerCase();
				int result = s.executeUpdate(sql);
				if(result != 0){
					logger.info("Agent updated correctly.");

				}
				else{
					logger.info("Agent not updated. ");	

				}

			}
			else
			{
				sql = "INSERT INTO registers (agent,user,dateFirst,dateLast) values  ('"+_agentName+"','"+_userName+"','"+calendar.getTime().toString()+"','"+calendar.getTime().toString()+"')";
				sql.toLowerCase();
				boolean result = s.execute(sql);
				if(!result){
					logger.info("Agent registered correctly.");

				}
				else{
					logger.info("Agent not registered. ");	

				}
			}
			// Creates a query.The results are stored in ResultSet rs
		} catch (Exception e) {
			e.printStackTrace();
		}


	}

	/**
	 * If the agent exists in the database and the user is the user that requires the certificate.
	 * @param _agent
	 * @param _user
	 * @return
	 */
	private boolean existAgent(String _agent, String _user)
	{
		boolean value = false;

		//Accedemos a la base de datos para comprobar si existe un agente ya registrado.

		this.openConnection();

		ResultSet rs = null;
		try {
			// Creates a Statement, to query
			Statement s = connection.createStatement();

			// Creates a query.The results are stored in ResultSet rs
			String sql = "SELECT agent,user,dateFirst,dateLast FROM registers WHERE agent = '"+_agent+"'";
			sql.toLowerCase();
			rs = s.executeQuery(sql);
			if(rs.next()){

				//Si existe debo comprobar el usuario es el mismo

				if (rs.getString("user").equals(_user))
				{
					value = false;
					existAgent = true;
				}
				else
				{
					value = true;
				}
			}
			else{
				value = false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return value;
	}

	/** 
	 * This method sets the connection with the THOMAS database  
	 * */
	private void openConnection() {
		if (connection != null)
			return;

		try {

			try{
				Class.forName("com.mysql.jdbc.Driver");
			}	catch( ClassNotFoundException e ) { e.printStackTrace();  }

			String serverName = "localhost";
			String mydatabase = "security";
			String url = "jdbc:mysql://" + serverName + "/" + mydatabase; // a
			String username = "mms";
			String password = "mms";
			connection = DriverManager.getConnection(url, username, password);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/** This method closes the connection with the database  */
	private void closeConnection() {
		try {
			connection.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Se nos hara llegar la información y crearemos el
	// certificado ya firmado.

	// Primero necesitamos un keyentry de los certificados del MMS para firmar
	// todos los certificados, pero
	// como el servicio no tiene persistencia lo haremos todo en la misma
	// función cada vez.

	/**
	 * This method extracts an MMS private key entry. With this private key, MMS signing the agent certificates
	 */
	private PrivateKeyEntry generateMMSPrivateKeyEntry(Properties prop)
	throws NoSuchAlgorithmException, InvalidKeyException,
	CertificateException, SignatureException, NoSuchProviderException,
	IOException, KeyStoreException, InstantiationException, IllegalAccessException {




		PrivateKeyEntry entry = null;
		PrivateKey rootkey = null;
		Provider p;



		String path = prop.getProperty("pathnsscfg");
		String alias = prop.getProperty("aliasCA");
		String password = prop.getProperty("password");
		String type = prop.getProperty("type");


		Certificate rootCertificate = null;
		Certificate[] certs;

		// tenemos que extraer el certificado de la base de datos nss.

		if (Security.getProvider("SunPKCS11-NSSkeystore") == null) {
			p = new sun.security.pkcs11.SunPKCS11(path);
			Security.addProvider(p);
		}

		char[] pin = password.toCharArray();
		KeyStore ks = KeyStore.getInstance(type);
		ks.load(null, pin);
		rootCertificate = ks.getCertificate(alias);

		try {
			rootkey = (PrivateKey) ks.getKey(alias, pin);
		} catch (UnrecoverableKeyException e) {
			e.printStackTrace();
		}



		certs = new Certificate[] { rootCertificate };
		entry = new KeyStore.PrivateKeyEntry(rootkey, certs);

		return entry;
	}

	// Este método creará un certificado mediante el PrivateKeyEntry (se usara parar firmar el certificado) del MMS y
	// la
	// información del agente (Common Name...).

	/**
	 * This method generates and signs a certificate of agent.
	 */
	private Certificate[] generateAndSignedCertificate(X500Name myname,
			String sigAlg, double validity, PrivateKeyEntry issuerEntry,
			PublicKey publicKey) throws NoSuchAlgorithmException,
			InvalidKeyException, CertificateException, SignatureException,
			NoSuchProviderException, IOException, KeyStoreException {

		Signature signature = Signature.getInstance(sigAlg);
		X500Signer issuer;

		signature.initSign(issuerEntry.getPrivateKey());
		issuer = new X500Signer(signature, new X500Name(
				((X509Certificate) issuerEntry.getCertificate())
				.getIssuerX500Principal().getEncoded()));

		Date firstDate = new Date();
		Date lastDate = new Date(firstDate.getTime() + (long) (validity * 24
				* 60 * 60 * 1000));

		CertificateValidity interval = new CertificateValidity(firstDate,
				lastDate);
		X509CertInfo info = new X509CertInfo();

		info.set(X509CertInfo.VERSION, new CertificateVersion(
				CertificateVersion.V3));

		info.set(X509CertInfo.SERIAL_NUMBER, new CertificateSerialNumber(
				(int) (firstDate.getTime() / 1000 * Math.random())));

		AlgorithmId algID = issuer.getAlgorithmId();
		info.set(X509CertInfo.ALGORITHM_ID, new CertificateAlgorithmId(algID));
		info.set(X509CertInfo.SUBJECT, new CertificateSubjectName(myname));
		info.set(X509CertInfo.KEY, new CertificateX509Key(publicKey));
		info.set(X509CertInfo.VALIDITY, interval);
		info.set(X509CertInfo.ISSUER, new CertificateIssuerName(issuer
				.getSigner()));
		if (System.getProperty("sun.security.internal.keytool.skid") != null) {
			CertificateExtensions ext = new CertificateExtensions();
			ext.set(SubjectKeyIdentifierExtension.NAME,
					new SubjectKeyIdentifierExtension(new KeyIdentifier(
							publicKey).getIdentifier()));
			info.set(X509CertInfo.EXTENSIONS, ext);
		}

		X509CertImpl cert = new X509CertImpl(info);

		// El MMS firmara el certificado.
		cert.sign(issuerEntry.getPrivateKey(), sigAlg);

		Certificate[] certs;
		certs = new Certificate[] { cert, issuerEntry.getCertificate() };
		return certs;
	}

	//Método para escribir en el fichero acl dando permisos o restricciones.

	/**
	 * This file acl contains the agent permissions
	 */
	private void writeAclFile(String command, String path) {
		PrintWriter aclFile = ACLFile(path);
		try {
			aclFile.println(command);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				// Nuevamente aprovechamos el finally para
				// asegurarnos que se cierra el fichero.
				if (null != file)
					file.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}

	}

	/*
	 * private void clearACLFile() { File fichero = new File(
	 * properties.getProperty("ACLPath")); fichero.delete(); try {
	 * fichero.createNewFile(); } catch (IOException ex) { //
	 * Logger.getLogger(ThomasGUI.class.getName()).log(Level.SEVERE, // null,
	 * ex); System.err.println(ex.getMessage());
	 * 
	 * } }
	 */
	//Crear un nuevo tipo PrintWriter en base a una ruta (indica el path donde se encuentra el fichero acl).
	/**
	 * Returns a new PrintWriter.
	 * 
	 * @param path is the path for a new FileWriter
	 */
	private PrintWriter ACLFile(String path) {

		try {
			file = new FileWriter(path, true);
		} catch (IOException e) {
			e.printStackTrace();
		}
		pw = new PrintWriter(file);

		return pw;
	}

	//Método para conectar con el broker.

	/**
	 * This method connects with the broker. 
	 */
	private void connectToBroker() {

		connectionBroker = "amqp://" + properties.getProperty("user")
		+ ":" + properties.getProperty("pass") + "@/"
		+ properties.getProperty("vhost") + "?brokerlist='tcp://"
		+ properties.getProperty("host") + ":"
		+ properties.getProperty("port") + "?ssl='"
		+ properties.getProperty("ssl") + "',sasl_mechs='"
		+ properties.getProperty("saslMechs") + "''";


		sess = new Session();
		
		try{
			broker = new Broker(sess, connectionBroker);
			sess.addBroker(connectionBroker);
		}
		catch(Exception e)
		{
			System.out.println("Error in add broker: "+ e.getMessage());
		}
		
//			try{
//				
//				sess.addBroker(connectionBroker);
//				
//			}catch(Exception e)
//			{
//				System.out.println("Fail to add a broker. Try again.");
//				System.out.println("ERROR message:"+ e.getMessage());
//				System.out.println("ERROR cause:"+ e.getCause().toString());
//			
//				sess.close();
//				sess = new Session();
//				sess.addBroker(connectionBroker);
//			}
		



	}

	//Cerramos la sesion con el broker.
	/**
	 * Close session
	 */
	private void closeSession()
	{
		try
		{
		sess.removeBroker(broker);
		sess.close();
		broker.shutdown();
		}catch(Exception e)
		{
			System.out.println("Error in close session: "+ e.getMessage());
		}

	}

	//Método que llama a la función de recarga, a continuación el broker recargará el fichero.

	/**
	 * Indicated to broker that reload the acl file.
	 * This acl file contains the agent permissions
	 */
	private void reloadACLFile() {
		
		try{
		hash.put("_class", "acl");
		qmf = sess.getObjects(hash);
		qmf.get(0).invokeMethod("reloadACLFile", "");
		}catch(Exception e)
		{
			System.out.println("Error in reload ACL File: "+ e.getMessage());
		}
	}

}
