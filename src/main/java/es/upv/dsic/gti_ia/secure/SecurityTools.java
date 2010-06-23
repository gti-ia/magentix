package es.upv.dsic.gti_ia.secure;

import java.util.Date;
import java.util.Enumeration;
import java.util.Properties;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.Signature;
import java.security.SignatureException;
import java.security.UnrecoverableKeyException;
import java.security.KeyStore.PrivateKeyEntry;
import java.security.cert.*;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.util.ByteArrayDataSource;
import javax.xml.stream.XMLStreamException;


import es.upv.dsic.gti_ia.architecture.Monitor;
import es.upv.dsic.gti_ia.secure.MMServiceStub.NewCertificate;
import es.upv.dsic.gti_ia.secure.MMServiceStub.NewCertificateResponse;

import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.context.ConfigurationContextFactory;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.apache.neethi.Policy;
import org.apache.neethi.PolicyEngine;
import org.apache.rampart.RampartMessageData;
import org.apache.rampart.policy.model.CryptoConfig;
import org.apache.rampart.policy.model.RampartConfig;
import org.bouncycastle.ocsp.BasicOCSPResp;
import org.bouncycastle.ocsp.CertificateID;
import org.bouncycastle.ocsp.OCSPReq;
import org.bouncycastle.ocsp.OCSPReqGenerator;
import org.bouncycastle.ocsp.OCSPResp;
import org.bouncycastle.ocsp.SingleResp;

import sun.security.x509.AlgorithmId;
import sun.security.x509.BasicConstraintsExtension;
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

public class SecurityTools {

	Monitor m;
	FileWriter fichero = null;
	PrintWriter pw = null;
	private Provider p;
	static Properties propSecurityUser = new Properties();
	private static SecurityTools sec = new SecurityTools();
	static Logger logger = Logger.getLogger(SecurityTools.class);

	private SecurityTools() {
		
		DOMConfigurator.configure("configuration/loggin.xml");
		try {
			propSecurityUser.load(new FileInputStream(
					"./configuration/securityUser.properties"));
		} catch (FileNotFoundException e) {
			logger.error(e);
		} catch (IOException e) {
			logger.error(e);
		}
	}

	public static SecurityTools GetInstance() {
		return sec;
	}

	public boolean generateAllProcessCertificate(String name) {

		String path = propSecurityUser.getProperty("KeyStorePath");
		String pass = propSecurityUser.getProperty("KeyStorePassword");
		String alias = propSecurityUser.getProperty("alias");
		String key = propSecurityUser.getProperty("key");
		String type = propSecurityUser.getProperty("type");
		try {
		// String de conexión con el servicio del MMS
		String target = propSecurityUser.getProperty("protocol") + "://"
				+ propSecurityUser.getProperty("host") + ":"
				+ propSecurityUser.getProperty("port")
				+ propSecurityUser.getProperty("path");
		

			// Cargamos el keystore
			KeyStore keystoreUser = this.getKeyStore(path,pass);

			
			if (keystoreUser == null)
				keystoreUser = this.createKeyStore(path,pass);
			// Miramos si tiene el certificado y ya tiene importado el
			// certificado del MMS
			if (!keystoreUser.containsAlias("MMS") || !keystoreUser.containsAlias("MMService")) {
				// Introducimos el certificado del CA
				this.importCACertificateInToCertStore(keystoreUser,
						propSecurityUser, path, pass);
			}

			// Vemos si tiene ya el certificado del agente y es valido

			if (!keystoreUser.containsAlias(name)) {

				// sino lo tiene o no es valido, creamos un certificado nuevo,
				// lo enviamos para que sea firmando por el MMS

				// llamamos al MMS

		
				// create the client stub
				System.out.println("Connecting to " + target);

				// To be able to load the client configuration from axis2.xml
				ConfigurationContext ctx = ConfigurationContextFactory
						.createConfigurationContextFromFileSystem("./Certificates/client-repo", null);

				MMServiceStub stub = new MMServiceStub(ctx, target);// "https://localhost:8334/axis2/services/MMService");
				ServiceClient sc = stub._getServiceClient();

				sc.engageModule("rampart");


				//sc.addHeadersToEnvelope(arg0);
			
				Policy rampartConfig = getRampartConfig(alias, key, type);
				Options options = sc.getOptions();
				options.setProperty(RampartMessageData.KEY_RAMPART_POLICY, rampartConfig);
				
				sc.getAxisService().getPolicySubject().attachPolicy(rampartConfig);

				

				KeyPair kp = generateKeyPair("RSA", 1024);
			
				
				NewCertificate nc = new NewCertificate();
				ByteArrayOutputStream outStream = new ByteArrayOutputStream();
				ObjectOutputStream os = new ObjectOutputStream(outStream);
				
			
				
				
				PublicKey pbk = kp.getPublic();
			
				Object p = pbk;
				os.writeObject(p);
				os.close();
				
				DataSource dataSource = new ByteArrayDataSource(outStream.toByteArray(), "application/octet-stream");
				DataHandler dataHandler = new DataHandler(dataSource);



				nc.setPk(dataHandler);
				nc.setAgentName(name);
				
				System.out.println("Clave publica antes de enviar: "+ pbk);
				NewCertificateResponse re = stub.newCertificate(nc);
				
				
				DataHandler result = re.get_return();
				
				InputStream inputDataHandler = result.getInputStream();
				byte[] arrayByte = IOUtils.toByteArray(inputDataHandler);
				 ByteArrayInputStream bis = new ByteArrayInputStream(arrayByte);
				 ObjectInputStream ois = new ObjectInputStream(bis);
				 Certificate[] certificates = (Certificate[])ois.readObject();
				 ois.close();
				 /*
				 Properties properties = new Properties();

					try {
						properties.load(new FileInputStream(
								"securityUser.properties"));
					} catch (FileNotFoundException e) {
						System.err.println(e);
					} catch (IOException e) {
						System.err.println(e);
					}*/
				 setKeyEntry(name,propSecurityUser, kp,  certificates);

				 System.out.println("Clave publica antes despues: "+ certificates[0].getPublicKey());

				
				 if (certificates != null)
				System.out.println("Llega el mensaje a nombre de: "+ ((X509Certificate)certificates[0]).getSubjectDN().getName());
				
				// si el MMS nos devuelve el certificado correctamente (no hay
				// problemas con el MMS)

				// lo introducimos en el keystore que nos ha facilitado
				
			}
			 return true;
		} catch (Exception e) {
			logger.error(e);
			return false;
		}
	}

	private  Policy getRampartConfig(String alias, String key, String typeUserCertificate) {
		int t= 0;
		
		String pass = propSecurityUser.getProperty("KeyStorePassword");
		String path = propSecurityUser.getProperty("KeyStorePath");
		String type = propSecurityUser.getProperty("KeyStoreCertType");
		String aliasMMS = propSecurityUser.getProperty("aliasMMS");
		try {

			RampartConfig rampartConfig = new RampartConfig();
			rampartConfig.setUser(alias); // CertFirmaDigital
			// CertAutenticacion
			// client

			rampartConfig.setPwCbClass("es.upv.dsic.gti_ia.secure.PWCBHandler");
			rampartConfig.setEncryptionUser(aliasMMS);

			CryptoConfig encrCrypto = new CryptoConfig();
			CryptoConfig sigCrypto = new CryptoConfig();

			sigCrypto
					.setProvider("org.apache.ws.security.components.crypto.Merlin");
			encrCrypto
					.setProvider("org.apache.ws.security.components.crypto.Merlin");

			Properties props = new Properties();
			Properties propsEncryption = new Properties();

			// dnie
			if (typeUserCertificate.equals("dnie"))
				t=0;
			else if (typeUserCertificate.equals("own"))
				t=1;
			else//others
				t=2;
				
				
			switch (t)
			{
			case 0: 
				String pkcs11ConfigFile = "./configuration/dnie_linux.cfg";
				Provider pkcs11Provider = new sun.security.pkcs11.SunPKCS11(pkcs11ConfigFile);
				Security.addProvider(pkcs11Provider);
				props.setProperty("org.apache.ws.security.crypto.merlin.keystore.type",type);
				props.setProperty("org.apache.ws.security.crypto.merlin.file",path);
				props.setProperty("org.apache.ws.security.crypto.merlin.keystore.password",	pass);
				break;
			case 1 : 
				props.setProperty("org.apache.ws.security.crypto.merlin.keystore.type",type);
				props.setProperty("org.apache.ws.security.crypto.merlin.file",path);
				props.setProperty("org.apache.ws.security.crypto.merlin.keystore.password",pass);
				break;
			case 2 : break;
			default: logger.error("What will be the certifying authority?");
			}
		
			propsEncryption.setProperty("org.apache.ws.security.crypto.merlin.keystore.type",type);
			propsEncryption.setProperty("org.apache.ws.security.crypto.merlin.file",path);
			propsEncryption.setProperty("org.apache.ws.security.crypto.merlin.keystore.password",pass);

			// para guardar el certificado del MMS para conectar con el tomcat y
			// autenticarse contra el MMS

			System.setProperty("javax.net.ssl.type", propSecurityUser
					.getProperty("TrustStoreCertType"));
			System.setProperty("javax.net.ssl.trustStore", propSecurityUser
					.getProperty("TrustStorePath"));
			System.setProperty("javax.net.ssl.trustStorePassword",
					propSecurityUser.getProperty("TrustStorePassword"));
			

			sigCrypto.setProp(props);
			encrCrypto.setProp(propsEncryption);

			rampartConfig.setSigCryptoConfig(sigCrypto);
			rampartConfig.setEncrCryptoConfig(encrCrypto);

			Policy policy = new Policy();

			try {
				policy = loadPolicy("policy.xml");
			} catch (XMLStreamException e) {
				logger.error(e);
			}

			policy.addAssertion(rampartConfig);
			return policy;
		} catch (Exception e) {
			e.getMessage();
			return null;
		}

	}

	/**
	 * Load policy file from classpath.
	 */
	private  Policy loadPolicy(String name) throws XMLStreamException {
		ClassLoader loader = SecurityTools.class.getClassLoader();
		InputStream resource = loader.getResourceAsStream(name);
		StAXOMBuilder builder = new StAXOMBuilder(resource);
		return PolicyEngine.getPolicy(builder.getDocumentElement());
	}

	private KeyStore importCACertificateInToCertStore(KeyStore key, Properties propSecurityUser, String path, String pass)
			throws CertificateException, IOException, KeyStoreException, NoSuchAlgorithmException {
		// Aqui deberiamos importar el certificado del CA en el la keystore que
		// ha creado el MMS para el usuario.

		String pathMMS = propSecurityUser.getProperty("CACertificatePath");
		String pathTomcat = propSecurityUser.getProperty("tomcatCertificate");
		Certificate cert = this.getCertificate(pathMMS);
		Certificate certTomcat = this.getCertificate(pathTomcat);
		if (!key.containsAlias("MMS"))
			key.setCertificateEntry("MMS", certTomcat);
		if (!key.containsAlias("MMService"))
			key.setCertificateEntry("MMService", cert);
		key.store(new FileOutputStream(path), pass.toCharArray());
		return key;

	}

	private boolean getContainsAliasAndValidPeriod(String keyStorePath,
			String pass, String alias) {

		try {
			boolean value = false;

			KeyStore keyStore = KeyStore.getInstance("JKS");
			FileInputStream ksfis = null;

			ksfis = new FileInputStream(keyStorePath);

			keyStore.load(ksfis, pass.toCharArray());

			if (keyStore.containsAlias(alias)) {

				X509Certificate cert = (X509Certificate) keyStore
						.getCertificate(alias);
				Date d = new Date();

				// Si el periodo de caducidad es mayor que la fecha actual

				if (d.compareTo(cert.getNotAfter()) < 0) {
					value = true;
				} else {
					value = false;
				}
			}

			return value;
		} catch (Exception e) {
			System.err.println("Caught exception " + e.toString());
			return false;
		}
	}
/*
	private void createAgentKeyStore(String agent, String keystore)
			throws NoSuchAlgorithmException, CertificateException, IOException,
			InvalidKeyException, SignatureException, NoSuchProviderException,
			KeyStoreException {

		System.out.println("Creating a new Agent certificate...");
		String _agent = agent;
		String _keystore = keystore;

		PrivateKeyEntry groupEntry = null;

		// Lo hace el MMS
		if (Security.getProvider("SunPKCS11-NSSkeystore") == null) {
			String configName = "./certificates/nss.cfg";
			p = new sun.security.pkcs11.SunPKCS11(configName);
			Security.addProvider(p);
		}

		groupEntry = generateCert("RSA", "SHA1WithRSA", 1024, null, 90, null);

		String commonName = _agent;
		String organizationalUnit = "GTI";
		String organization = "DSIC";
		String city = "VALENCIA";
		String state = "COMUNIDAD VALENCIANA";
		String country = "ES";

		X500Name x500Name;

		x500Name = new X500Name(commonName, organizationalUnit, organization,
				city, state, country);

		PrivateKeyEntry userEntry = null;

		userEntry = generateCert("RSA", "SHA1WithRSA", 1024, x500Name, 90,
				groupEntry);

		Certificate certUser = userEntry.getCertificate();
		Certificate certGroup = groupEntry.getCertificate();

		X509Certificate[] chain = new X509Certificate[2];

		chain[0] = (X509Certificate) certUser;
		chain[1] = (X509Certificate) certGroup;

		char[] key = "key123".toCharArray();

		KeyStore truststore = null;

		truststore = KeyStore.getInstance("JKS");

		boolean existe = true;
		FileInputStream ksfis = null;
		// si no existe lo creamos, sinos lo abrimos
		try {
			ksfis = new FileInputStream(_keystore);
		} catch (Exception e) {
			existe = false;
			ksfis = null;
		}

		if (existe) {
			// lo abrimos
			truststore.load(ksfis, "key123".toCharArray());
		} else {

			// lo creamos
			truststore.load(null);

		}

		FileOutputStream keyStoreFile;

		keyStoreFile = new FileOutputStream(_keystore);

		// Añadir las claves y el certificado al keystore
		try {
			truststore.setKeyEntry(_agent, userEntry.getPrivateKey(), key,
					chain);
		} catch (KeyStoreException e) {
			e.printStackTrace();
		}

		char[] pass1 = "key123".toCharArray();

		// Añadimos el certificado para el certStore
		truststore = this.importCACertificateInToCertStore(truststore,
				propSecurityUser);

		truststore.store(keyStoreFile, pass1);

	}*/

	private String revocation_state(X509Certificate cert,
			X509Certificate certAC, String address) {
		try {

			/* Se carga el proveedor necesario para la petición OCSP */
			if (Security.getProvider("BC") == null)
				Security
						.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

			/*
			 * Se genera la petición con el certificado a verificar y su número
			 * de serie
			 */
			CertificateID cID = new CertificateID(CertificateID.HASH_SHA1,
					certAC, cert.getSerialNumber());
			OCSPReqGenerator ocspReqGen = new OCSPReqGenerator();
			ocspReqGen.addRequest(cID);
			OCSPReq ocspReq = ocspReqGen.generate();

			/* Se establece la conexión HTTP con el ocsp del DNIe */
			URL url = new URL(address);// "http://ocsp.dnielectronico.es/");
			HttpURLConnection con = (HttpURLConnection) url.openConnection();

			/* Se configuran las propiedades de la petición HTTP */
			con.setRequestProperty("Content-Type", "application/ocsp-request");
			con.setRequestProperty("Accept", "application/ocsp-response");
			con.setDoOutput(true);
			OutputStream out = con.getOutputStream();
			DataOutputStream dataOut = new DataOutputStream(
					new BufferedOutputStream(out));
			/* Se obtiene la respuesta del servidos OCSP del DNIe */
			dataOut.write(ocspReq.getEncoded());

			dataOut.flush();
			dataOut.close();

			/*
			 * Se parsea la respuesta y se obtiene el estado del certificado
			 * retornado por el OCSP
			 */
			InputStream in = (InputStream) con.getContent();

			BasicOCSPResp basicResp = (BasicOCSPResp) new OCSPResp(in)
					.getResponseObject();
			String estado = "";
			for (SingleResp singResp : basicResp.getResponses()) {
				Object status = singResp.getCertStatus();

				if (status instanceof org.bouncycastle.ocsp.UnknownStatus) {
					estado = "unknown";
				} else if (status instanceof org.bouncycastle.ocsp.RevokedStatus) {
					estado = "revoked";
				} else {
					estado = "valid";
				}
			}

			return estado;
		} catch (Exception e) {
			System.err.println("Caught exception " + e.toString());
			return "exception";
		}
	}

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

	}

	private Certificate getCertificate(String route)
			throws CertificateException, IOException {
		// Cargamos un certificado
		FileInputStream fis = new FileInputStream(route);
		BufferedInputStream bis = new BufferedInputStream(fis);
		CertificateFactory certFact = CertificateFactory.getInstance("X.509");
		Certificate cert = null;
		while (bis.available() > 0) {
			cert = certFact.generateCertificate(bis);
			System.out.println(cert.toString());
		}

		return cert;
	}

	private KeyStore getKeyStore(String path, String pass) {
		try {

			KeyStore keystore = KeyStore.getInstance("JKS");
			
	
			FileInputStream keyStoreFile = new FileInputStream(path);

			keystore.load(keyStoreFile, pass.toCharArray());
			keyStoreFile.close();

			return keystore;
		} catch (Exception e) {
			System.out.println(e);
			return null;
		}

	}

	// creamos una nueva keystore e importamos el certificado de la CA
	private KeyStore createKeyStore(String path, String pass) {
		try {

			// Creamos una nueva keystore para ir guardando todos los
			// certificados de los agentes.
			KeyStore keystore = KeyStore.getInstance("JKS");//PKCS11
			FileOutputStream keyStoreFile = new FileOutputStream(path);

			keystore.load(null);
			/*
			FileInputStream fis = new FileInputStream(
					pathCA);
			BufferedInputStream bis = new BufferedInputStream(fis);

			// crear certificado de la utoridad certificadora.
			CertificateFactory certFact = CertificateFactory
					.getInstance("X.509");
			
			Certificate cert = null;
			while (bis.available() > 0) {
				cert = certFact.generateCertificate(bis);
			}*/

			//keystore.setCertificateEntry("MMS", cert);

			// keytwo.load(null, "key123".toCharArray());
			keystore.store(keyStoreFile, pass.toCharArray());
			
			return keystore;

		} catch (NoSuchAlgorithmException nsaex) {
			nsaex.printStackTrace(System.err);
			return null;
		} catch (KeyStoreException ksex) {
			ksex.printStackTrace(System.err);
			return null;
		} catch (CertificateException cex) {
			cex.printStackTrace(System.err);
			return null;
		} catch (IOException ioex) {
			ioex.printStackTrace(System.err);
			return null;
		}
	}

	private  boolean firmadoPor(Certificate certA, Certificate certB) {
		try {
			certA.verify(certB.getPublicKey());
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private  boolean autofirmado(Certificate cert) {
		return firmadoPor(cert, cert);
	}

	// Para el cliente, generamos la clave pública y privada
	private static  KeyPair generateKeyPair(String keyType, int keyBits)
			throws NoSuchAlgorithmException {

		KeyPairGenerator keyGen = KeyPairGenerator.getInstance(keyType);
		SecureRandom prng = new SecureRandom();

		keyGen.initialize(keyBits, prng);
		KeyPair pair = keyGen.generateKeyPair();
		return pair;
	}
	
	// Una vez el MMS nos envie el certificado generado y firmado por el mismo
	// lo introduciremos en el keystore
	// con nuestro par de claves pública/privada.
	private  void setKeyEntry(String alias,Properties prop, KeyPair keyPair, Certificate[] certs) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {

		// lo dividiremos en dos partes.
		// La primera será la encargada de crear el privateKeyEntry dado un
		// certificado y un keypair.

		
		char[] pass = prop.getProperty("KeyStorePassword").toCharArray();
		String path = prop.getProperty("KeyStorePath");
		
		
		
		// tenemos que extraer el certificado de la keystore


		PrivateKey privateKey = keyPair.getPrivate();
		PrivateKeyEntry userEntry = null;
		
		userEntry = new KeyStore.PrivateKeyEntry(privateKey, certs);

		//TODO aci falta algo en el chain
		Certificate certUser = userEntry.getCertificate();
		X509Certificate[] chain = new X509Certificate[2];
		chain[0] = (X509Certificate) certUser;


		KeyStore truststore = null;
		truststore = KeyStore.getInstance("JKS");

		boolean nExist = false;
		FileInputStream ksfis = null;
		// si no existe lo creamos, sinos lo abrimos
		try {
			ksfis = new FileInputStream(path);
		} catch (Exception e) {
			nExist = true;
		}
	
		
		if (nExist)
		{
			truststore.load(null);	
		}
		else
		{
		try
		{
			truststore.load(ksfis,pass);
		}
		finally
		{
			if (ksfis != null) {
		       ksfis.close();
		    }
		}
		}

	
		// Añadir las claves y el certificado al keystore
		truststore.setKeyEntry(alias, userEntry.getPrivateKey(),pass,certs);
		

		

		// Añadimos el certificado para el certStore
	//	truststore = importCACertificateInToCertStore(truststore,
		//		prop.getProperty("CACertificatePath"));
	
		 // store away the keystore
	    java.io.FileOutputStream fos = null;
	    try {
	        fos = new java.io.FileOutputStream(path);
	        truststore.store(fos,pass);
	    } finally {
	        if (fos != null) {
	            fos.close();
	        }
	    }
	}
	
	private  PrivateKeyEntry generateCert(String keyType, String sigAlg,
			int keyBits, X500Name myname, long validity,
			PrivateKeyEntry issuerEntry) throws NoSuchAlgorithmException,
			InvalidKeyException, CertificateException, SignatureException,
			NoSuchProviderException, IOException, KeyStoreException {

		Certificate rootCertificate = null;
		X509Certificate rootX509certificate = null;
		PrivateKey rootkey = null;
		// tenemos que extraer el certificado de la keystore
		if (issuerEntry == null) {

			char[] pin = "certificate".toCharArray();
			KeyStore ks = KeyStore.getInstance("PKCS11");
			ks.load(null, pin);

			rootCertificate = ks.getCertificate("CA");
			rootCertificate.getPublicKey();
			rootX509certificate = (X509Certificate) rootCertificate;

			myname = (X500Name) rootX509certificate.getSubjectDN();

			try {
				rootkey = (PrivateKey) ks.getKey("CA", pin);
			} catch (UnrecoverableKeyException e) {
				e.printStackTrace();
			}
		}

		// Lo hara el cliente
		KeyPairGenerator keyGen = KeyPairGenerator.getInstance(keyType);
		SecureRandom prng = new SecureRandom();

		keyGen.initialize(keyBits, prng);
		KeyPair pair = keyGen.generateKeyPair();

		PublicKey publicKey = pair.getPublic();
		PrivateKey privateKey = pair.getPrivate();

		Signature signature = Signature.getInstance(sigAlg);
		X500Signer issuer;

		// Se firma el certificado, esto lo hara el MMS, no el agente.
		if (issuerEntry == null) {
			signature.initSign(rootkey);
			issuer = new X500Signer(signature, myname);
		} else {

			signature.initSign(issuerEntry.getPrivateKey());
			issuer = new X500Signer(signature, new X500Name(
					((X509Certificate) issuerEntry.getCertificate())
							.getIssuerX500Principal().getEncoded()));
		}

		// Esto lo debería hacer el cliente
		Date firstDate = new Date();
		Date lastDate = new Date(firstDate.getTime() + (long) validity * 24
				* 60 * 60 * 1000);

		CertificateValidity interval = new CertificateValidity(firstDate,
				lastDate);

		X509CertInfo info = new X509CertInfo();

		if (issuerEntry == null) {
			info.set(X509CertInfo.VERSION, new CertificateVersion(
					CertificateVersion.V3));

			info.set(X509CertInfo.SERIAL_NUMBER, new CertificateSerialNumber(
					rootX509certificate.getSerialNumber()));

			AlgorithmId algID = issuer.getAlgorithmId();
			info.set(X509CertInfo.ALGORITHM_ID, new CertificateAlgorithmId(
					algID));
			info.set(X509CertInfo.SUBJECT, new CertificateSubjectName(myname));

			info.set(X509CertInfo.KEY, new CertificateX509Key(publicKey));
			CertificateValidity intervalRoot = new CertificateValidity(
					rootX509certificate.getNotBefore(), rootX509certificate
							.getNotAfter());
			info.set(X509CertInfo.VALIDITY, intervalRoot);
			info.set(X509CertInfo.ISSUER, new CertificateIssuerName(issuer
					.getSigner()));

			CertificateExtensions certExt = new CertificateExtensions();
			// info.get(X509CertInfo.EXTENSIONS);
			/*
			 * certExt.set(SubjectKeyIdentifierExtension.NAME, new
			 * SubjectKeyIdentifierExtension( new
			 * KeyIdentifier(publicKey).getIdentifier()));
			 */

			certExt.set(BasicConstraintsExtension.NAME,
					new BasicConstraintsExtension(false, true, 2147483647));

			info.set(X509CertInfo.EXTENSIONS, certExt);
		} else {
			info.set(X509CertInfo.VERSION, new CertificateVersion(
					CertificateVersion.V3));

			info.set(X509CertInfo.SERIAL_NUMBER, new CertificateSerialNumber(
					(int) (firstDate.getTime() / 1000 * Math.random())));

			AlgorithmId algID = issuer.getAlgorithmId();
			info.set(X509CertInfo.ALGORITHM_ID, new CertificateAlgorithmId(
					algID));
			info.set(X509CertInfo.SUBJECT, new CertificateSubjectName(myname));
			info.set(X509CertInfo.KEY, new CertificateX509Key(publicKey));
			info.set(X509CertInfo.VALIDITY, interval);
			info.set(X509CertInfo.ISSUER, new CertificateIssuerName(issuer
					.getSigner()));
		}

		if (System.getProperty("sun.security.internal.keytool.skid") != null) {
			CertificateExtensions ext = new CertificateExtensions();
			ext.set(SubjectKeyIdentifierExtension.NAME,
					new SubjectKeyIdentifierExtension(new KeyIdentifier(
							publicKey).getIdentifier()));
			info.set(X509CertInfo.EXTENSIONS, ext);
		}

		X509CertImpl cert = new X509CertImpl(info);

		// Lo tiene que hacer el MMS
		if (issuerEntry == null) {
			cert.sign(rootkey, sigAlg);
		} else {
			cert.sign(issuerEntry.getPrivateKey(), sigAlg);
		}

		Certificate[] certs;

		// Lo tiene que hacer el MMS
		if (issuerEntry == null) {

			certs = new Certificate[] { rootCertificate };

		} else {
			certs = new Certificate[] { cert, issuerEntry.getCertificate() };
		}
		PrivateKeyEntry entry = null;

		// Lo tiene que hacer el Cliente
		if (issuerEntry == null) {
			entry = new KeyStore.PrivateKeyEntry(rootkey, certs);
		} else {
			entry = new KeyStore.PrivateKeyEntry(privateKey, certs);
		}

		return entry;
	}

}
