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


/** This class provides support to work in secure mode **/
public class SecurityTools {

	//To access for a Qpid broker acl file.
	FileWriter fichero = null;
	PrintWriter pw = null;
	
	private static SecurityTools sec = new SecurityTools();
	static Logger logger = Logger.getLogger(SecurityTools.class);


	//This method is a private because SecurityTools is a class that uses an singleton design pattern.
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
	 * @param propSecurityUser
	 * @return True (no problems with the creation of the certificate) or False
	 */
	
	// All process of agent certificate request is created in this method.
	// The steps are:
	// 1. Check that it is the user keystore.
	// 2. Check that the agent not have the certificate or it's not valid.
	// 3. MMS service request.
	// 4. The new certificate returned by the MMS is add to the keystore with agent name alias.
	public boolean generateAllProcessCertificate(String Agentname, Properties propSecurityUser) {

		String name = Agentname;
		String path = propSecurityUser.getProperty("KeyStorePath");
		String pass = propSecurityUser.getProperty("KeyStorePassword");
		String alias = propSecurityUser.getProperty("alias");
		//String key = propSecurityUser.getProperty("key");
		String type = propSecurityUser.getProperty("type");
		try {
			// String connection with the MMS service.
			
			String target = propSecurityUser.getProperty("protocol") + "://"
			+ propSecurityUser.getProperty("host") + ":"
			+ propSecurityUser.getProperty("port")
			+ propSecurityUser.getProperty("path");

			//Loaded the user keystore.
			KeyStore keystoreUser = this.getKeyStore(path, pass);

			//If not exist, will be create a new Keystore.
			if (keystoreUser == null)
				keystoreUser = this.createKeyStore(path, pass);


			//Check if agent has a valid certificate.
			if (getExistAliasAndIsValidPeriod(keystoreUser, name)) {

				//If agent not has or is not valid, a new certificate is created. 
				//This is sends to MMS service. The MMS services is responsible to be signs the certificate. 


				// create the client stub
				System.out.println("Connecting to " + target);

				// To be able to load the client configuration from axis2.xml
				ConfigurationContext ctx = ConfigurationContextFactory
				.createConfigurationContextFromFileSystem(
						"./configuration/client-repo", null);

				MMSStub stub = new MMSStub(ctx, target);
				stub._getServiceClient().engageModule("rampart");


				
				// We configure the security Rampart module.
				Policy rampartConfig = getRampartConfig(alias,type, propSecurityUser);

				stub._getServiceClient().getAxisService().getPolicySubject().attachPolicy(rampartConfig);


				//We make the private/public key pair, the MMS only has that sign the certificates. 
				KeyPair kp = generateKeyPair("RSA", 1024);

		
				ByteArrayOutputStream outStream = new ByteArrayOutputStream();
				ObjectOutputStream os = new ObjectOutputStream(outStream);

				//Send the public key.
				PublicKey pbk = kp.getPublic();

				Object p = pbk;
				os.writeObject(p);
				os.close();

				DataSource dataSource = new ByteArrayDataSource(outStream
						.toByteArray(), "application/octet-stream");
				DataHandler dataHandler = new DataHandler(dataSource);


				//Call service is created.
				DataHandler re = stub.mMS(name, dataHandler);

				//re is a result of service request.
				if (re != null)
				{
					InputStream inputDataHandler = re.getInputStream();
					byte[] arrayByte = IOUtils.toByteArray(inputDataHandler);
					ByteArrayInputStream bis = new ByteArrayInputStream(arrayByte);
					ObjectInputStream ois = new ObjectInputStream(bis);
					Certificate[] certificates = (Certificate[]) ois.readObject();
					ois.close();

					
					//The signed certificate is added in keystore.
					setKeyEntry(name, propSecurityUser, kp, certificates);

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






	/**
	 * This method configures a rampart module and return a Policy. 
	 * 
	 * @param agent alias name
	 * @param typeUserCertificate if is own or others certificate type
	 * @param propSecurityUser
	 */
	private Policy getRampartConfig(String alias,
			String typeUserCertificate, Properties propSecurityUser) {
		int t = 0;

		String pass = propSecurityUser.getProperty("KeyStorePassword");
		String path = propSecurityUser.getProperty("KeyStorePath");
		String aliasMMS = "mms";


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

		
			if (typeUserCertificate.equals("others"))
				t = 0;
			else if (typeUserCertificate.equals("own"))
				t = 1;

			switch (t) {
			case 0:
				

			//	String pkcs11ConfigFile = "./configuration/dnie_linux.cfg";
				//Provider pkcs11Provider = new sun.security.pkcs11.SunPKCS11(pkcs11ConfigFile);

				Provider providerBouncyCastle = new org.bouncycastle.jce.provider.BouncyCastleProvider();
				Security.addProvider(providerBouncyCastle);

				//Security.addProvider(pkcs11Provider);

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

			
			//The values indicate the encrypt keystore path and password.
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
	 * 
	 * @param name The resource name
	 */
	private Policy loadPolicy(String name) throws XMLStreamException {
		
		ClassLoader loader = SecurityTools.class.getClassLoader();
		InputStream resource = loader.getResourceAsStream(name);
		StAXOMBuilder builder = new StAXOMBuilder(resource);
		
		return PolicyEngine.getPolicy(builder.getDocumentElement());
	}


	/**
	 * This method checks if the keystore contains a certificate with alias.
	 * @param keyStore
	 * @param alias agent alias name.
	 * @return true or false
	 */
	private boolean getExistAliasAndIsValidPeriod(KeyStore keyStore,String alias) {

		try {
			boolean value = true;

			if (keyStore.containsAlias(alias)) {

				value = false;
				X509Certificate cert = (X509Certificate) keyStore.getCertificate(alias);
				Date d = new Date();

				//If the shelf life is older than current date.
				//If exists, but not has a valid period will be removed.
				if (d.compareTo(cert.getNotAfter()) < 0) {
					value = false;
				} else {
					// Is invalid
					// Remove certificate.
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


	/**
	 * This method return a object with keystore type. This keystore is opened from path.
	 *  
	 * @param path the path to generate the FileOutpStream
	 * @param pass the password to generate the keystore integrity check	
	 */
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

	
	
	/**
	 * This method creates a new keystore.
	 * 
	 * @param path the path to generate the FileOutpStream
	 * @param pass the password to generate the keystore integrity check
	 */
	private KeyStore createKeyStore(String path, String pass) {
		try {

			
			//This kesytore is created for add all agent new certificates
			KeyStore keystore = KeyStore.getInstance("JKS");// PKCS11
			FileOutputStream keyStoreFile = new FileOutputStream(path);

			keystore.load(null);
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

	
	
	/**
	 * This method generates a new public/private key pair.
	 * 
	 * @param keyType algorithm the standard string name of the algorithm. See Appendix A in the  Java Cryptography Architecture API Specification & Reference  for information about standard algorithm names.
	 * @param keyBits This is an algorithm-specific metric, such as modulus length, specified in number of bits.
	 */
	private static KeyPair generateKeyPair(String keyType, int keyBits)
	throws NoSuchAlgorithmException {

		KeyPairGenerator keyGen = KeyPairGenerator.getInstance(keyType);
		SecureRandom prng = new SecureRandom();
		 
		keyGen.initialize(keyBits, prng);
		KeyPair pair = keyGen.generateKeyPair();
		return pair;
	}


	/**
	 * This method adds a new key entry with a new agent certificate. This agent certificate 
	 * was returned for the MMS service. 
	 * 
	 * @param alias the alias name
	 * @param prop the properties
	 * @param keyPair for extract the private key
	 * @param certs an array of Certificates representing the certificate chain. The chain must be ordered and contain a Certificate at index 0 corresponding to the private key.
	 */
	private void setKeyEntry(String alias, Properties prop, KeyPair keyPair,
			Certificate[] certs) throws KeyStoreException,
			NoSuchAlgorithmException, CertificateException, IOException {



		char[] pass = prop.getProperty("KeyStorePassword").toCharArray();
		String path = prop.getProperty("KeyStorePath");

		
		//We extract the certificate for the keystore. 
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
		
		//If not exists then a new is creates, but if exist the certificate is opened. 
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


		
		//The keys and certificate are added into keystore.
		truststore.setKeyEntry(alias, userEntry.getPrivateKey(), pass, certs);

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
