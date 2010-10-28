package es.upv.dsic.gti_ia.secure;


import java.util.Date;
import java.util.Properties;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
//import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.KeyStore.PrivateKeyEntry;
import java.security.cert.*;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.util.ByteArrayDataSource;
import javax.xml.stream.XMLStreamException;


import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.context.ConfigurationContextFactory;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.apache.neethi.Policy;
import org.apache.neethi.PolicyEngine;
import org.apache.rampart.policy.model.CryptoConfig;
import org.apache.rampart.policy.model.RampartConfig;


/** This class gives us the support to work in secure mode **/
public class SecurityTools {

	// Para acceder al fichero acl.
	FileWriter fichero = null;
	PrintWriter pw = null;
	// Fichero de propiedades del usuario
	//static Properties propSecurityUser = new Properties();
	private static SecurityTools sec = new SecurityTools();
	static Logger logger = Logger.getLogger(SecurityTools.class);
	//	private FileInputStream propFile = null;

	// La clase es privada ya que utilizamos el patrón de diseño singleton.
	private SecurityTools() {

		DOMConfigurator.configure("configuration/loggin.xml");

	}

	/**
	 * Returns an instance of the SecurityTools class.
	 * 
	 * @return SecurityTools
	 */
	public static SecurityTools GetInstance() {
		return sec;
	}

	/**
	 * This class is responsible for request the agent certificate if this not exists or is not valid.
	 * 
	 * @param Agentname This is a name of agent
	 * @return True (no problems with the creation of the certificate)
	 */
	// ESta clase es la encargada de seguir todo el procedimiento de solicitud
	// de certificados para el agente.
	// Comprobar que exista la keystore del usuario
	// Comprobar que no tenga el certificado del agente que va a lanzar o que no
	// este válido.
	// Contactar con el MMS
	// Añadir el nuevo certificado devuelto por el MMS a la keystore con el alias del agente.
	public boolean generateAllProcessCertificate(String Agentname, Properties propSecurityUser) {

		String name = Agentname;
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

			// Cargamos el keystore del usuario.
			KeyStore keystoreUser = this.getKeyStore(path, pass);

			// Si no tuviera creamos uno nuevo.
			if (keystoreUser == null)
				keystoreUser = this.createKeyStore(path, pass);


			// Vemos si tiene ya el certificado del agente y es valido
			if (getExistAliasAndIsValidPeriod(keystoreUser, name)) {

				// sino lo tiene o no es valido, creamos un certificado nuevo,
				// lo enviamos para que sea firmando por el MMS

				// create the client stub
				System.out.println("Connecting to " + target);

				// To be able to load the client configuration from axis2.xml
				ConfigurationContext ctx = ConfigurationContextFactory
				.createConfigurationContextFromFileSystem(
						"./configuration/client-repo", null);

				MMSStub stub = new MMSStub(ctx, target);// "https://localhost:8334/axis2/services/MMService");
				stub._getServiceClient().engageModule("rampart");


				// Configuramos el módulo de seguridad Rampart.

				Policy rampartConfig = getRampartConfig(alias, key, type, propSecurityUser);


				//stub._getServiceClient().getOptions().setProperty(WSSHandlerConstants.OUTFLOW_SECURITY, rampartConfig);
				//stub._getServiceClient().getOptions().setProperty(WSSHandlerConstants.INFLOW_SECURITY, rampartConfig);


				stub._getServiceClient().getAxisService().getPolicySubject().attachPolicy(
						rampartConfig);



				//stub._getServiceClient().getAxisService().getPolicySubject().attachPolicyComponent(WSSHandlerConstants.OUTFLOW_SECURITY, rampartConfig);
				//stub._getServiceClient().getAxisService().getPolicySubject().attachPolicyComponent(WSSHandlerConstants.INFLOW_SECURITY, rampartConfigInflow);



				//.attachPolicyComponent(PolicyInclude.INPUT_POLICY, rampartConfigInflow);



				// Creamos nosotros el par clave privada/pública, el MMS
				// solamente tendrá
				// que firmar los certificados.
				KeyPair kp = generateKeyPair("RSA", 1024);

				//NewCertificate nc = new NewCertificate();
				ByteArrayOutputStream outStream = new ByteArrayOutputStream();
				ObjectOutputStream os = new ObjectOutputStream(outStream);

				// Enviaremos la clave pública
				PublicKey pbk = kp.getPublic();

				Object p = pbk;
				os.writeObject(p);
				os.close();

				DataSource dataSource = new ByteArrayDataSource(outStream
						.toByteArray(), "application/octet-stream");
				DataHandler dataHandler = new DataHandler(dataSource);

	
				//Hacemos la llamada al servicio.
				DataHandler re = stub.mMS(name, dataHandler);

				//Resultado de la llamada.
				//	DataHandler result = re;


				if (re != null)
				{
					InputStream inputDataHandler = re.getInputStream();
					byte[] arrayByte = IOUtils.toByteArray(inputDataHandler);
					ByteArrayInputStream bis = new ByteArrayInputStream(arrayByte);
					ObjectInputStream ois = new ObjectInputStream(bis);
					Certificate[] certificates = (Certificate[]) ois.readObject();
					ois.close();

					//Introducimos el certificado firmado en la keystore.
					setKeyEntry(name, propSecurityUser, kp, certificates);

					//propFile.close();
				}
				else
				{
					throw new Exception("MMS is not available now or the agent certificate is unauthorized");
				}


			}
			return true;
		} catch (Exception e) {

			logger.error(e);
			return false;
		}
	}





	//Método para la configuración del módulo Rampart
	private Policy getRampartConfig(String alias, String key,
			String typeUserCertificate, Properties propSecurityUser) {
		int t = 0;

		String pass = propSecurityUser.getProperty("KeyStorePassword");
		String path = propSecurityUser.getProperty("KeyStorePath");
		String aliasMMS = "mms";//propSecurityUser.getProperty("aliasMMS");







		try {

			RampartConfig rampartConfig = new RampartConfig();


			rampartConfig.setUser(alias);
			rampartConfig.setEncryptionUser(aliasMMS);


			rampartConfig.setPwCbClass("es.upv.dsic.gti_ia.secure.PWCBHandler");


			CryptoConfig encCrypto = new CryptoConfig();
			CryptoConfig sigCrypto = new CryptoConfig();
			CryptoConfig decCrypto = new CryptoConfig();

			sigCrypto.setProvider("org.apache.ws.security.components.crypto.Merlin");
			encCrypto.setProvider("org.apache.ws.security.components.crypto.Merlin");
			decCrypto.setProvider("org.apache.ws.security.components.crypto.Merlin");

			Properties sigProps = new Properties();
			Properties encProps = new Properties();
			Properties decProps = new Properties();

			// dnie
			if (typeUserCertificate.equals("others"))
				t = 0;
			//Certificados propios.
			else if (typeUserCertificate.equals("own"))
				t = 1;

			switch (t) {
			case 0:
				String pkcs11ConfigFile = "./configuration/dnie_linux.cfg";
				Provider pkcs11Provider = new sun.security.pkcs11.SunPKCS11(pkcs11ConfigFile);

				Provider providerBouncyCastle = new org.bouncycastle.jce.provider.BouncyCastleProvider();
				Security.addProvider(providerBouncyCastle);

				Security.addProvider(pkcs11Provider);

				sigProps.setProperty(
						"org.apache.ws.security.crypto.merlin.keystore.type",

						propSecurityUser.getProperty("othersType"));
				sigProps.setProperty(
						"org.apache.ws.security.crypto.merlin.file",
						propSecurityUser.getProperty("othersPath"));
				sigProps.setProperty(
						"org.apache.ws.security.crypto.merlin.keystore.password",
						propSecurityUser.getProperty("othersPin"));


				decProps.setProperty(
						"org.apache.ws.security.crypto.merlin.keystore.type",
						propSecurityUser.getProperty("othersType"));
				decProps.setProperty(
						"org.apache.ws.security.crypto.merlin.file",
						propSecurityUser.getProperty("othersPath"));
				decProps.setProperty(
						"org.apache.ws.security.crypto.merlin.keystore.password",
						propSecurityUser.getProperty("othersPin"));

				break;
			case 1:
				sigProps.setProperty(
						"org.apache.ws.security.crypto.merlin.file",
						path);
				sigProps.setProperty(
						"org.apache.ws.security.crypto.merlin.keystore.password",
						pass);
				break;

			default:
				logger.error("What will be the certifying authority?");
			}

			//Donde se encuentra el keystore para encriptar
			encProps.setProperty(
					"org.apache.ws.security.crypto.merlin.file",
					path);
			encProps.setProperty(
					"org.apache.ws.security.crypto.merlin.keystore.password",
					pass);






			sigCrypto.setProp(sigProps);

			encCrypto.setProp(encProps);

			decCrypto.setProp(decProps);


			rampartConfig.setSigCryptoConfig(sigCrypto);

			rampartConfig.setDecCryptoConfig(decCrypto);

			rampartConfig.setEncrCryptoConfig(encCrypto);

			Policy policy = new Policy();

			try {
				policy = loadPolicy("policy.xml");
			} catch (XMLStreamException e) {
				logger.error(e);
			}

			policy.addAssertion(rampartConfig);

			return policy;
		} catch (Exception e) {
			logger.error(e);
			return null;
		}

	}

	/**
	 * Load policy file from classpath.
	 */
	private Policy loadPolicy(String name) throws XMLStreamException {
		ClassLoader loader = SecurityTools.class.getClassLoader();
		InputStream resource = loader.getResourceAsStream(name);
		StAXOMBuilder builder = new StAXOMBuilder(resource);
		return PolicyEngine.getPolicy(builder.getDocumentElement());
	}


	private boolean getExistAliasAndIsValidPeriod(KeyStore keyStore,
			String alias) {

		try {
			boolean value = true;

			if (keyStore.containsAlias(alias)) {

				value = false;
				X509Certificate cert = (X509Certificate) keyStore
				.getCertificate(alias);
				Date d = new Date();

				// Si el periodo de caducidad es mayor que la fecha actual
				// Si existe, pero no tiene un periodo válido habra que
				// borrarlo.

				if (d.compareTo(cert.getNotAfter()) < 0) {
					value = false;
				} else {
					// es invalida
					// Quitar certificado
					keyStore.deleteEntry(alias);
					value = true;

				}
			}

			return value;
		} catch (Exception e) {
			logger.error(e);
			return false;
		}
	}

	//Devuelve un objeto de tipo keystore.
	private KeyStore getKeyStore(String path, String pass) {
		try {

			KeyStore keystore = KeyStore.getInstance("JKS");

			FileInputStream keyStoreFile = new FileInputStream(path);

			keystore.load(keyStoreFile, pass.toCharArray());
			keyStoreFile.close();

			return keystore;
		} catch (Exception e) {
			logger.error(e);
			return null;
		}

	}

	//Creamos una nueva keystore
	private KeyStore createKeyStore(String path, String pass) {
		try {

			// Creamos una nueva keystore para ir guardando todos los
			// certificados de los agentes.
			KeyStore keystore = KeyStore.getInstance("JKS");// PKCS11
			FileOutputStream keyStoreFile = new FileOutputStream(path);

			keystore.load(null);
			/*
			 * FileInputStream fis = new FileInputStream( pathCA);
			 * BufferedInputStream bis = new BufferedInputStream(fis);
			 * 
			 * // crear certificado de la utoridad certificadora.
			 * CertificateFactory certFact = CertificateFactory
			 * .getInstance("X.509");
			 * 
			 * Certificate cert = null; while (bis.available() > 0) { cert =
			 * certFact.generateCertificate(bis); }
			 */

			// keystore.setCertificateEntry("MMS", cert);

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

	// Generamos la clave pública y privada
	private static KeyPair generateKeyPair(String keyType, int keyBits)
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
	private void setKeyEntry(String alias, Properties prop, KeyPair keyPair,
			Certificate[] certs) throws KeyStoreException,
			NoSuchAlgorithmException, CertificateException, IOException {

		// lo dividiremos en dos partes.
		// La primera será la encargada de crear el privateKeyEntry dado un
		// certificado y un keypair.

		char[] pass = prop.getProperty("KeyStorePassword").toCharArray();
		String path = prop.getProperty("KeyStorePath");

		// tenemos que extraer el certificado de la keystore

		PrivateKey privateKey = keyPair.getPrivate();
		PrivateKeyEntry userEntry = null;

		userEntry = new KeyStore.PrivateKeyEntry(privateKey, certs);

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

		if (nExist) {
			truststore.load(null);
		} else {
			try {
				truststore.load(ksfis, pass);
			} finally {
				if (ksfis != null) {
					ksfis.close();
				}
			}
		}

		// Añadir las claves y el certificado al keystore
		truststore.setKeyEntry(alias, userEntry.getPrivateKey(), pass, certs);

		// Añadimos el certificado para el certStore
		// truststore = importCACertificateInToCertStore(truststore,
		// prop.getProperty("CACertificatePath"));

		// store away the keystore
		java.io.FileOutputStream fos = null;
		try {
			fos = new java.io.FileOutputStream(path);
			truststore.store(fos, pass);
		} finally {
			if (fos != null) {
				fos.close();
			}
		}
	}
}
