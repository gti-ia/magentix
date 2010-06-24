package es.upv.dsic.gti_ia.MMService;

/*
 import java.io.BufferedInputStream;
 import java.io.BufferedOutputStream;
 import java.io.DataOutputStream;
 import java.io.File;
 import java.io.InputStream;
 import java.io.OutputStream;
 import java.net.HttpURLConnection;
 import java.net.URL;
 */

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
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
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;
import java.util.Vector; /*
 import org.bouncycastle.ocsp.BasicOCSPResp;
 import org.bouncycastle.ocsp.CertificateID;
 import org.bouncycastle.ocsp.OCSPReq;
 import org.bouncycastle.ocsp.OCSPReqGenerator;
 import org.bouncycastle.ocsp.OCSPResp;
 import org.bouncycastle.ocsp.SingleResp;
 */
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

public class AgentCertificate {

	FileWriter fichero = null;
	PrintWriter pw = null;
	private ArrayList<QMFObject> qmf = new ArrayList<QMFObject>();
	private HashMap<String, Object> hash = new HashMap<String, Object>();
	private Session sess = new Session();
	Properties properties = new Properties();

	/**
	 * This method return a new signed certificate, the common name is a name of agent. 
	 * @param agentName (Common Name)
	 * @param pk This is a user public key.
	 * @return
	 */
	public byte[] newCertificate(String agentName, byte[] pk) {

		// this.load();

		try {
			properties.load(new FileInputStream(
					"./configuration/securityAdmin.properties"));
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

				String username = ((Principal) wsSecurityEngineResult
						.get(WSSecurityEngineResult.TAG_PRINCIPAL)).getName();

				// X509Certificate cert = ((X509Certificate)
				// wsSecurityEngineResult
				// .get(WSSecurityEngineResult.TAG_X509_CERTIFICATE));

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
			int validity = Integer.parseInt(properties.getProperty("Validity"));
			PrivateKeyEntry pke = this.generateMMSPrivateKeyEntry();

			// Por defecto como quiere que este formado nombre de los
			// certificados que emita.
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

			ByteArrayOutputStream outStream = new ByteArrayOutputStream();
			ObjectOutputStream os = new ObjectOutputStream(outStream);

			os.writeObject(certs);
			os.close();

			// Especificar la ubicación del archivo del almacén de certificados
			// en los que se confía, el mismo que el de broker.
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

			// Conectamos con el broker
			this.conectToBroker();

			// Escribimos por cada agente las siguientes lineas.

			String command = String.format("\n" + "acl allow " + agentName
					+ "@QPID create queue name=" + agentName + "\n"
					+ "acl allow " + agentName
					+ "@QPID bind exchange name=amq.direct routingkey="
					+ agentName);

			this.writeAclFile(command);

			// Recargamos el fichero.
			this.reloadACLFile();

			return outStream.toByteArray();

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

	private PrivateKeyEntry generateMMSPrivateKeyEntry()
			throws NoSuchAlgorithmException, InvalidKeyException,
			CertificateException, SignatureException, NoSuchProviderException,
			IOException, KeyStoreException {

		Properties prop = new Properties();
		PrivateKeyEntry entry = null;
		PrivateKey rootkey = null;
		Provider p;
		
		prop.load(new FileInputStream(
		"./configuration/securityAdmin.properties"));

		String path = prop.getProperty("pathnsscfg");
		String alias = prop.getProperty("aliasMMS");
		String password = prop.getProperty("passowrd");
		String type = prop.getProperty("type");

		
		Certificate rootCertificate = null;
		Certificate[] certs;
		// X509Certificate rootX509certificate = null;
		
		
		// tenemos que extraer el certificado de la keystore

		
		
		

		if (Security.getProvider("SunPKCS11-NSSkeystore") == null) {
			p = new sun.security.pkcs11.SunPKCS11(path);
			Security.addProvider(p);
		}

		char[] pin = password.toCharArray();
		KeyStore ks = KeyStore.getInstance(type);
		ks.load(null, pin);
		rootCertificate = ks.getCertificate(alias);
		rootCertificate.getPublicKey();
		// rootX509certificate = (X509Certificate) rootCertificate;

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

	private Certificate[] generateAndSignedCertificate(X500Name myname,
			String sigAlg, long validity, PrivateKeyEntry issuerEntry,
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
		Date lastDate = new Date(firstDate.getTime() + (long) validity * 24
				* 60 * 60 * 1000);

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
	private void writeAclFile(String command) {
		PrintWriter aclFile = ACLFile();
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
	private PrintWriter ACLFile() {

		try {
			fichero = new FileWriter(properties.getProperty("ACLPath"), true);
		} catch (IOException e) {
			e.printStackTrace();
		}
		pw = new PrintWriter(fichero);

		return pw;
	}

	//Método para conectar con el broker.
	private void conectToBroker() {

		String connectionBroker = "amqp://" + properties.getProperty("user")
				+ ":" + properties.getProperty("pass") + "@/"
				+ properties.getProperty("vhost") + "?brokerlist='tcp://"
				+ properties.getProperty("host") + ":"
				+ properties.getProperty("port") + "?ssl='"
				+ properties.getProperty("ssl") + "',sasl_mechs='"
				+ properties.getProperty("saslMechs") + "''";
		System.out.println("Connecting to: " + connectionBroker);
		sess = new Session();
		sess.addBroker(connectionBroker);

	}

	//Método que llama a la función de recarga, a continuación el broker recargará el fichero.
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
}
