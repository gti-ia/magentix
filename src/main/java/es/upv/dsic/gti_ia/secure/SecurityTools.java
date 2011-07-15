package es.upv.dsic.gti_ia.secure;


import java.util.Date;
import java.util.Properties;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
//import java.io.FileNotFoundException;
import java.io.File;
import java.io.FileNotFoundException;
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


import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.context.ConfigurationContextFactory;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
//import org.apache.log4j.xml.DOMConfigurator;
import org.apache.neethi.Policy;
import org.apache.neethi.PolicyEngine;
import org.apache.rahas.RahasConstants;
import org.apache.rahas.TokenStorage;
import org.apache.rahas.TrustUtil;
import org.apache.rahas.client.STSClient;
import org.apache.rahas.Token;
import org.apache.rampart.RampartMessageData;
import org.apache.rampart.policy.model.CryptoConfig;
import org.apache.rampart.policy.model.RampartConfig;
import org.apache.ws.secpolicy.SP11Constants;


/** This class provides support to work in secure mode **/
public class SecurityTools {

	//To access for a Qpid broker acl file.
	FileWriter fichero = null;
	PrintWriter pw = null;

	KeyStore keystoreUser = null;
	Policy policy, policy_sts, rconfMMS = null;
	ConfigurationContext ctx = null;
	STSClient stsClient = null;
	String action = null;
	Token responseToken, tok = null;
	TokenStorage store = null;
	KeyPair kp  = null;
	ByteArrayOutputStream outStream = new ByteArrayOutputStream();
	ObjectOutputStream os = null;
	Options optionsMMS = null;
	MMSStub stub = null;
	InputStream inputDataHandler = null;
	Certificate[] certificates = null;
	byte[] arrayByte = null;
	ByteArrayInputStream bis = null;
	ObjectInputStream ois = null;
	Object p = null;
	DataSource dataSource = null;
	DataHandler dataHandler = null;

	private static SecurityTools sec = new SecurityTools();
	static Logger logger = Logger.getLogger(SecurityTools.class);




	//This method is a private because SecurityTools is a class that uses an singleton design pattern.
	private SecurityTools() {

		//DOMConfigurator.configure("configuration/loggin.xml");

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

		logger.setLevel(Level.OFF);
		
		
		//======================================================//
		//    													//
		//				Client Initialization					//
		//														//	
		//======================================================//

		String name = Agentname;
		String alias = propSecurityUser.getProperty("alias");

		if (policy == null)
			policy = getRampartConfig("sts",alias, propSecurityUser,"policy.xml");

		if (policy_sts == null)
			policy_sts = getRampartConfig("sts",alias, propSecurityUser,"sts_policy.xml");

		if (rconfMMS == null)
			rconfMMS = getRampartConfig("mms", alias, propSecurityUser, "policy.xml");
		//String key = propSecurityUser.getProperty("key");



		try {
			// String connection with the MMS service.

			String target = propSecurityUser.getProperty("protocol") + "://"
			+ propSecurityUser.getProperty("host") + ":"
			+ propSecurityUser.getProperty("port")
			+ propSecurityUser.getProperty("path");


			//If not exist, will be create a new Keystore.
			if (keystoreUser == null)
				keystoreUser = getKeyStore(propSecurityUser.getProperty("KeyStorePath"), propSecurityUser.getProperty("KeyStorePassword"));


			//Check if agent has a valid certificate.
			if (getExistAliasAndIsValidPeriod(keystoreUser, name)) {

				// create the client stub
				logger.info("Connecting to " + target);

				if (ctx == null)
				{
					ctx = ConfigurationContextFactory.createConfigurationContextFromFileSystem("./configuration/client-repo", null);
					stub =  new MMSStub(ctx, null);
				}


				
				
				

				//==================================================================================//
				//  STS Section: In this section, the client sends a new RST message				//
				// The RST message contains a security token that holds the client's credentials	//
				//																					//	
				//==================================================================================//

				stsClient = new STSClient(ctx);
				stsClient.setRstTemplate(getRSTTemplate());
				action = TrustUtil.getActionValue(RahasConstants.VERSION_05_02, RahasConstants.RST_ACTION_ISSUE);
				stsClient.setAction(action);



				responseToken = stsClient.requestSecurityToken(policy,propSecurityUser.getProperty("STSpath") ,policy_sts, null);
				store = TrustUtil.getTokenStore(ctx);


				store.add(responseToken);



				//store.getValidTokens()[0].getId();


				tok = store.getToken(responseToken.getId());






				//We make the private/public key pair, the MMS only has that sign the certificates. 
				kp = generateKeyPair("RSA", 1024);
				outStream = new ByteArrayOutputStream();
				os = new ObjectOutputStream(outStream);

				//Send the public key.
				//PublicKey pbk = kp.getPublic();
				p = kp.getPublic();
				os.writeObject(p);

				dataSource = new ByteArrayDataSource(outStream.toByteArray(), "application/octet-stream");
				dataHandler = new DataHandler(dataSource);








				optionsMMS = new Options();
				optionsMMS.setTo(new EndpointReference(target));
				optionsMMS.setProperty(RampartMessageData.KEY_RAMPART_POLICY, rconfMMS);// loadPolicy("sample05/policy.xml"));


				optionsMMS.setTimeOutInMilliSeconds(300 * 1000);
				stub._getServiceClient().engageModule("rampart");
				stub._getServiceClient().engageModule("addressing");

				optionsMMS.setProperty(RampartMessageData.KEY_CUSTOM_ISSUED_TOKEN, tok.getId());
				stub._getServiceClient().setOptions(optionsMMS);


				DataHandler result = null;

				//Call service is created.
				result = stub.mMS(name, dataHandler);


				//result is a result of service request.
				try
				{
					inputDataHandler = result.getInputStream();
					arrayByte = IOUtils.toByteArray(inputDataHandler);
					bis = new ByteArrayInputStream(arrayByte);
					ois = new ObjectInputStream(bis);
					certificates = (Certificate[]) ois.readObject();



					//The signed certificate is added in keystore.
					setKeyEntry(name, propSecurityUser, kp, certificates);


					//Todo ha ido correctamente.
					return true;

				}
				catch(Exception e)
				{
					//El MMS le ha denegado la emision del certificado.
					logger.error("MMS is not available now or the user not has the permissions to acquire the name " + Agentname);
					logger.error("The error is caused by: "+ e.getMessage());
					return false;
				}
				finally
				{
					stub.cleanup();
					bis.close();
					ois.close();
					arrayByte = null;
					kp = null;
					certificates = null;
					inputDataHandler = null;
				}


			}
			else //Tiene un certificado emitido por el MMS valido.
				return true;
		} catch (Exception e) {
			logger.error(e);
			return false;
		}
		finally{
			try {
				if (ctx != null)
				{
					ctx.cleanupContexts();
					ctx.clearPropertyDifferences();
					ctx.flush();
					ctx.terminate();
				}
				responseToken = null;
				optionsMMS = null;
				dataSource = null;
				dataHandler = null;
				outStream.close();
				p = null;
				//os.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}



	private static OMElement getRSTTemplate() throws Exception {
		OMFactory fac = OMAbstractFactory.getOMFactory();
		OMElement elem = fac.createOMElement(SP11Constants.REQUEST_SECURITY_TOKEN_TEMPLATE);
		TrustUtil.createTokenTypeElement(RahasConstants.VERSION_05_02, elem).setText(RahasConstants.TOK_TYPE_SAML_10);
		TrustUtil.createKeyTypeElement(RahasConstants.VERSION_05_02, elem, RahasConstants.KEY_TYPE_SYMM_KEY);
		TrustUtil.createKeySizeElement(RahasConstants.VERSION_05_02, elem, 256);
		return elem;
	} 




	/**
	 * This method configures a rampart module and return a Policy. 
	 * 
	 * @param agent alias name
	 * @param typeUserCertificate if is own or others certificate type
	 * @param propSecurityUser
	 */
	private Policy getRampartConfig(String _aliasService, String alias,
			Properties propSecurityUser, String _policy) {
		int t = 0;

		String aliasService = _aliasService;
		String typeUserCertificate = propSecurityUser.getProperty("type");
		RampartConfig rampartConfig = new RampartConfig();
		CryptoConfig sigCrypto = new CryptoConfig();
		Properties sigProps = new Properties();

		try {


			Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());




			rampartConfig.setUser(alias);
			rampartConfig.setEncryptionUser(aliasService);


			rampartConfig.setPwCbClass("es.upv.dsic.gti_ia.secure.PWCBHandler");




			sigCrypto.setProvider("org.apache.ws.security.components.crypto.Merlin");




			if (typeUserCertificate.equals("others"))
				t = 0;
			else if (typeUserCertificate.equals("own"))
				t = 1;

			switch (t) {
			case 0:

				sigProps.setProperty(
						"org.apache.ws.security.crypto.merlin.keystore.type",
						propSecurityUser.getProperty("othersType"));

				sigProps.setProperty(
						"org.apache.ws.security.crypto.merlin.file",
						propSecurityUser.getProperty("othersPath"));

				sigProps.setProperty(
						"org.apache.ws.security.crypto.merlin.keystore.password",
						propSecurityUser.getProperty("othersPin"));

				break;
			case 1:
				sigProps.setProperty(
						"org.apache.ws.security.crypto.merlin.file",
						propSecurityUser.getProperty("KeyStorePath"));

				sigProps.setProperty(
						"org.apache.ws.security.crypto.merlin.keystore.password",
						propSecurityUser.getProperty("KeyStorePassword"));

				break;

			default:
				logger.error("Please select 0 to own certificates or 1 to others.");
			}

			sigCrypto.setProp(sigProps);

			rampartConfig.setSigCryptoConfig(sigCrypto);



			Policy policy = new Policy();

			try {
				policy = loadPolicy(_policy);
			} catch (XMLStreamException e) {
				logger.error(e);
			}

			policy.addAssertion(rampartConfig);

			return policy;
		} catch (Exception e) {
			logger.error(e);
			return null;
		}
		finally{
			sigCrypto = null;
			sigProps = null;
			rampartConfig = null;
		}

	}

	/**
	 * Load policy file from classpath.
	 * 
	 * @param name The resource name
	 * @throws FileNotFoundException 
	 */
	private Policy loadPolicy(String name) throws XMLStreamException, FileNotFoundException {

		File f = new File("./configuration/"+name);

		FileInputStream fi = new FileInputStream(f);
		StAXOMBuilder builder = new StAXOMBuilder(fi);	
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


	private void closeKeyStore(KeyStore ks)
	{
		ks = null;

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

			keyStoreFile.close();
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
