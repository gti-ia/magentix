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
import java.security.Principal;
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
import org.apache.qpid.console.QMFObject;
import org.apache.qpid.console.Session;
import org.apache.ws.security.WSSecurityEngineResult;
import org.apache.ws.security.handler.WSHandlerConstants;
import org.apache.ws.security.handler.WSHandlerResult;



/** This class is a core for MMS service, It is responsible for issuing certificates signed by the CA*/
public class AgentCertificate {

	FileWriter fichero = null;
	PrintWriter pw = null;
	private ArrayList<QMFObject> qmf = new ArrayList<QMFObject>();
	private HashMap<String, Object> hash = new HashMap<String, Object>();
	private Session sess = new Session();
	Properties properties = new Properties();
	private ArrayList<String> reservedNames = new ArrayList<String>();
	ByteArrayOutputStream outStream = null;
	String username = "";
	Calendar calendario;




	/**
	 * This method returns a new signed certificate 
	 * @param agentName Common Name=Name of agent
	 * @param pk This is a user public key.
	 * @return
	 */
	@SuppressWarnings("unchecked")
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

				WSHandlerResult result = (WSHandlerResult) results.get(0);

				WSSecurityEngineResult wsSecurityEngineResult = (WSSecurityEngineResult) result
				.getResults().get(0);

				username = ((Principal) wsSecurityEngineResult
						.get(WSSecurityEngineResult.TAG_PRINCIPAL)).getName();

				System.out.println("User name: " + username);


				// Para poder comprobar el estado de revocación del dnie o otro
				// certificado. Falta por hacer con https.

				/*
				 * Certificate certAC = null;
				 * 
				 * certAC = this.getCertificate(
				 * "/home/joabelfa/workspace/java/workarea/magentix2Secure/certificates/ACDNIE_CERTIFICATES/ACDNIE002-SHA2.crt"
				 * ); this.revocation_stateBouncyCastle(cert, (X509Certificate)
				 * certAC, "http://ocsp.dnielectronico.es/");
				 */
			}

			//El nombre del agente esta reservado??
			if (!reservedNames.contains(agentName))
			{


				System.out.println("Creating a new Agent certificate with Common Name= "
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

				//Guardamos el registro del usuario / agente.
				//Estará formado por fecha - usuario - agente 
				calendario = Calendar.getInstance();
				String commandLog = "====================================================== \n Session: "+ calendario.getTime().toString() +"\n User name: " + username +"\n Agent Name: "+ agentName +"\n======================================================";

				this.writeAclFile(commandLog, properties.getProperty("Userlog"));

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



				//Conectamos con el broker.
				this.connectToBroker();

				// Escribimos por cada agente las siguientes lineas con los permisos necesarios.

				String command = String.format("\n" 
						+ "acl allow "+agentName+"@QPID all queue name=" + agentName + "\n"
						+ "acl allow "+agentName+"@QPID all exchange name=amq.direct routingkey="+ agentName +"\n"
						+ "acl allow "+agentName+"@QPID all queue name="+agentName+".trace" +"\n"
						+ "acl allow "+agentName+"@QPID bind exchange name=amq.match routingkey="+agentName+".system.all" +"\n"
						+ "acl allow "+agentName+"@QPID bind exchange name=amq.match routingkey="+agentName+".system.direct"+"\n"
						+ "acl allow "+agentName+"@QPID publish exchange name=amq.match");

				//Escribimos el fichero acl.
				this.writeAclFile(command, properties.getProperty("ACLPath"));

				// Recargamos el fichero.
				this.reloadACLFile();

				this.closeSession();


				return outStream.toByteArray();
			}
			else//Si el nombre esta reservado, como por ejemplo MMS.
			{
				calendario = Calendar.getInstance();
				//Registramos quin intenta acceder a nombres reservados
				String commandLog = "=====================WARNING========================== \n Session: "+ calendario.getTime().toString() +"\n User name: " + username +"\n Agent Name: "+ agentName +"\n======================================================";

				this.writeAclFile(commandLog, properties.getProperty("Userlog"));			

				return null;
			}




		} catch (Exception e) {
			System.err.println("Error caught: " + e);
			return null;
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
				if (null != fichero)
					fichero.close();
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
			fichero = new FileWriter(path, true);
		} catch (IOException e) {
			e.printStackTrace();
		}
		pw = new PrintWriter(fichero);

		return pw;
	}

	//Método para conectar con el broker.
	
	/**
	 * This method connects with the broker. 
	 */
	private void connectToBroker() {

		String connectionBroker = "amqp://" + properties.getProperty("user")
		+ ":" + properties.getProperty("pass") + "@/"
		+ properties.getProperty("vhost") + "?brokerlist='tcp://"
		+ properties.getProperty("host") + ":"
		+ properties.getProperty("port") + "?ssl='"
		+ properties.getProperty("ssl") + "',sasl_mechs='"
		+ properties.getProperty("saslMechs") + "''";
		sess = new Session();
		sess.addBroker(connectionBroker);



	}

	//Cerramos la sesion con el broker.
	/**
	 * Close session
	 */
	private void closeSession()
	{
		sess.close();

	}

	//Método que llama a la función de recarga, a continuación el broker recargará el fichero.
	
	/**
	 * Indicated to broker that reload the acl file.
	 * This acl file contains the agent permissions
	 */
	private void reloadACLFile() {
		hash.put("_class", "acl");
		qmf = sess.getObjects(hash);
		qmf.get(0).invokeMethod("reloadACLFile", "");
	}

	/*
	 * private Certificate getCertificate(String route) throws
	 * CertificateException, IOException { // Cargamos un certificado
	 * FileInputStream fis = new FileInputStream(route); BufferedInputStream bis
	 * = new BufferedInputStream(fis); CertificateFactory certFact =
	 * CertificateFactory.getInstance("X.509"); Certificate cert = null; while
	 * (bis.available() > 0) { cert = certFact.generateCertificate(bis);
	 * System.out.println(cert.toString()); }
	 * 
	 * return cert; }
	 */
	/*
	 * private String revocation_stateBouncyCastle(X509Certificate cert,
	 * X509Certificate certAC, String direccion) { try {
	 * 
	 * // Se carga el proveedor necesario para la petición OCSP if
	 * (Security.getProvider("BC") == null) Security .addProvider(new
	 * org.bouncycastle.jce.provider.BouncyCastleProvider());
	 * 
	 * 
	 * //Se genera la petición con el certificado a verificar y su número // de
	 * serie
	 * 
	 * CertificateID cID = new CertificateID(CertificateID.HASH_SHA1, certAC,
	 * cert.getSerialNumber()); OCSPReqGenerator ocspReqGen = new
	 * OCSPReqGenerator(); ocspReqGen.addRequest(cID); OCSPReq ocspReq =
	 * ocspReqGen.generate();
	 * 
	 * //Se establece la conexión HTTP con el ocsp del DNIe URL url = new
	 * URL(direccion);// "http://ocsp.dnielectronico.es/"); HttpURLConnection
	 * con = (HttpURLConnection) url.openConnection();
	 * 
	 * //Se configuran las propiedades de la petición HTTP
	 * con.setRequestProperty("Content-Type", "application/ocsp-request");
	 * con.setRequestProperty("Accept", "application/ocsp-response");
	 * con.setDoOutput(true); OutputStream out = con.getOutputStream();
	 * DataOutputStream dataOut = new DataOutputStream( new
	 * BufferedOutputStream(out)); //Se obtiene la respuesta del servidos OCSP
	 * del DNIe dataOut.write(ocspReq.getEncoded());
	 * 
	 * dataOut.flush(); dataOut.close();
	 * 
	 * 
	 * //Se parsea la respuesta y se obtiene el estado del certificado
	 * //retornado por el OCSP
	 * 
	 * InputStream in = (InputStream) con.getContent();
	 * 
	 * BasicOCSPResp basicResp = (BasicOCSPResp) new OCSPResp(in)
	 * .getResponseObject(); String estado = ""; for (SingleResp singResp :
	 * basicResp.getResponses()) { Object status = singResp.getCertStatus();
	 * 
	 * if (status instanceof org.bouncycastle.ocsp.UnknownStatus) {
	 * System.out.println("Certificado con numero de serie " +
	 * Integer.toHexString(singResp.getCertID() .getSerialNumber().intValue()) +
	 * " desconocido"); estado = "desconocido"; } else if (status instanceof
	 * org.bouncycastle.ocsp.RevokedStatus) {
	 * System.out.println("Certificado con numero de serie " +
	 * Integer.toHexString(singResp.getCertID() .getSerialNumber().intValue()) +
	 * " revocado"); estado = "revocado"; } else {
	 * System.out.println("Certificado con numero de serie " +
	 * Integer.toHexString(singResp.getCertID() .getSerialNumber().intValue()) +
	 * " valido"); estado = "valido"; } }
	 * 
	 * return estado; } catch (Exception e) {
	 * System.err.println("Caught exception " + e.toString()); return
	 * "exception"; } }
	 */
	/*
	 *Este método tiene algunas funcionalidades para trabajar con dnie. En principio no se utilizará, peró nos puede
	 *servir para futuros trabajos.
	private void DNIeAccesCertificate() {

		try {

			// Primero hago una prueba con el certificado de la generalitat
			// valenciana.
			X509Certificate certACCVOCSP = (X509Certificate) this
					.getCertificate("./certificates/accv_certificates/ocsp-gva_pem.crt");
			X509Certificate certACCVJoan = (X509Certificate) this
					.getCertificate("./certificates/accv_certificates/joabelfa@hotmail.com.crt");

			System.out.println("El estado de revocación es: "
					+ this.revocation_state(certACCVJoan, certACCVOCSP,
							"http://ocsp.pki.gva.es"));

			// sacar el certificado de la ac, es este caso la 002:

			Certificate certAC = null;

			certAC = this
					.getCertificate("./certificates/ACDNIE_CERTIFICATES/ACDNIE002-SHA2.crt");
			// borrar el que accede al la base de datos del certificado del MMS.

			if (p != null)
				Security.removeProvider(p.getName());
			if (Security.getProvider("SunPKCS11-DNIE\nlibrary") == null) {
				String configName = "./certificates/dnie_linux.cfg";
				Provider p = new sun.security.pkcs11.SunPKCS11(configName);
				Security.addProvider(p);
			}

			System.out.println("keyStore accessing...");
			KeyStore store = KeyStore.getInstance("PKCS11");

			char[] pin = "ue3wYatu".toCharArray();
			// store.load(null,"CFqXmkUT".toCharArray());//

			store.load(null, pin);

			Enumeration<?> enumeration = store.aliases();
			Certificate[] certs = null;
			PrivateKey privateKey = null;
			while (enumeration.hasMoreElements()) {

				String alias = enumeration.nextElement().toString();
				// Solo queremos acceder al alias del certificado de
				// autenticacion
				if (alias.equals("CertAutenticacion")) {
					certs = store.getCertificateChain(alias);

					// sacamos la clave privada para firmar el reto
					privateKey = (PrivateKey) store.getKey(alias, pin);

				}

			}

			// se crea un reto para autenticar al usuario (challenge-response)

			Signature dsa = Signature.getInstance("SHA1withRSA");
			dsa.initSign((PrivateKey) privateKey);

			byte[] data = new String(
					"Estos datos sirven para verificar la firma del propietario del DNIe")
					.getBytes();

			dsa.update(data);
			byte[] realSig = dsa.sign();

			Signature sigver = Signature.getInstance("SHA1withRSA");
			sigver.initVerify(store.getCertificate("CertAutenticacion")
					.getPublicKey());
			// ó initVerify(store.getCertificate(alias))
			sigver.update(data);
			boolean verSig = sigver.verify(realSig);

			System.out.println("Dnie verificado: " + verSig);

			for (Certificate c : certs) {
				X509Certificate cx = (X509Certificate) c;
				// System.out.println(cx);
				System.out.println("Subject: " + cx.getSubjectX500Principal());
				// System.out.println("Subject: "+cx.getSubjectDN().getName().substring(cx.getSubjectDN().getName().indexOf("GIVENNAME"),cx.getSubjectDN().getName().indexOf(",",
				// 4)));// .getSubjectDN().getName())
				System.out.println("Validity: " + cx.getNotBefore() + " to "
						+ cx.getNotAfter());

				System.out
						.println("Hola ....vamos a comprobar si su certificado esta vigente....");

				// System.out.println("El estado de revocación es: "+
				// this.revocation_stateBouncyCastle(cx,(X509Certificate)certAC));
				// X509Certificate certOCSP = (X509Certificate)
				// getCertificate("./certificates/OCSP_certificate/AVDNIEFNMTSHA2.crt");
				System.out.println("El estado de revocación es: "
						+ this.revocation_state(cx, (X509Certificate) certAC,
								"http://ocsp.dnielectronico.es/"));

			}
			// deberemos exportar el certificado de la direccion general de
			// trafico a nuestra keystore
			// System.out.println("Su  certificado es valido??: "+
			// firmadoPor(certs[0], certAC));

			// verificar el certificado

		} catch (CertificateException e) {
			System.err.println("Caught exception " + e.toString()
					+ ". Compruebe si ha introducido un pin.");
		} catch (Exception e) {
			System.err.println("Caught exception " + e.toString());
		}

	}*/
}
