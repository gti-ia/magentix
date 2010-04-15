package MMS_Example;

import java.util.ArrayList;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.Security;
import java.security.cert.*;

import es.upv.dsic.gti_ia.architecture.Monitor;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.BaseAgent;
import es.upv.dsic.gti_ia.organization.Configuration;


import org.apache.qpid.console.Session;
import org.apache.qpid.console.QMFObject;



public class MMS extends BaseAgent {

	public MMS(AgentID aid) throws Exception {
		super(aid);
		// TODO Auto-generated constructor stub

	}

	private Configuration c;
	Monitor m;
	FileWriter fichero = null;
    PrintWriter pw = null;
    private ArrayList<QMFObject> qmf = new ArrayList<QMFObject>();
    private HashMap<String, Object> hash = new HashMap<String, Object>();
    private Session sess = new Session();
    
    
	public void init() {
		c = Configuration.getConfiguration();
		m = new Monitor();
		//this.createCertificate(null);
	}

	public void execute() {
		//this.conectToBroker();
		//this.writeAclFile();
		this.conectToBroker();
		//this.readACLFile();
		m.waiting();
	}

	
	
	public void ValidateCertificate()
	{
		
		try {
			//per a version windows
			KeyStore keyStore = KeyStore.getInstance("Windows-MY");
			keyStore.load(null,null);
			//per a version  linux/mac
			String pkcs11config = "name = DNIE\nlibrary = opensc-pkcs11.so ";
			InputStream confStream = new ByteArrayInputStream(pkcs11config.getBytes());

			Class sunPkcs11Class = Class.forName("sun.security.pkcs11.SunPKCS11");
			Constructor pkcs11Constr = sunPkcs11Class.getConstructor(InputStream.class);

			Provider pkcs11Provider = (Provider) pkcs11Constr.newInstance( confStream );
			Security.addProvider(pkcs11Provider);

			
			
		} catch (KeyStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CertificateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void writeAclFile(String command) {
		PrintWriter aclFile = ACLFile();
		try
		{
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


	public void clearACLFile()
	{
		File fichero = new File("./certificates/broker.acl");
		fichero.delete();
		try {
			fichero.createNewFile();
		} catch (IOException ex) {
			// Logger.getLogger(ThomasGUI.class.getName()).log(Level.SEVERE,
			// null, ex);
			logger.error(ex.getMessage());
		}	
	}
	private PrintWriter ACLFile() {
		// TODO Auto-generated method stub
		 try {
			//fichero = new FileWriter("./certificates/broker.acl");
			fichero = new FileWriter("./certificates/broker.acl", true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
         pw = new PrintWriter(fichero);

		return pw;
	}

	public void createCertificate(java.security.cert.Certificate[] certs) {

		try {
			FileInputStream fis = new FileInputStream("./certificates/MMS_CA/root.crt");
			BufferedInputStream bis = new BufferedInputStream(fis);

	

		// crear certificado de la utoridad certificadora.
			CertificateFactory certFact = CertificateFactory
					.getInstance("X.509");

			 while (bis.available() > 0) {
				    Certificate cert = certFact.generateCertificate(bis);
				    
				    System.out.println(cert.toString());
				 }
			 

			 Collection c = certFact.generateCertificates(fis);
			 Iterator i = c.iterator();
			 while (i.hasNext()) {
			    Certificate cert = (Certificate)i.next();
			    System.out.println(cert);
			 }


		} catch (java.security.cert.CertificateEncodingException e) {
			logger.error(e.getMessage());
		} catch (CertificateException e) {
			logger.error(e.getMessage());
		} catch (IOException ex) {
			logger.error(ex.getMessage());
	}

	}

	public void conectToBroker() {
		
	
		String connectionBroker = "amqp://" + c.getqpidUser() + ":"
				+ c.getqpidPassword() + "@/" + c.getqpidVhost()
				+ "?brokerlist='tcp://" + c.getqpidHost() + ":"
				+ c.getqpidPort() + "?ssl='" + c.getqpidSSL()
				+ "',sasl_mechs='" + c.getqpidsaslMechs() +"''";
		//,TrustStorePassword="+"dsfds"+"
		
		sess = new Session();
		sess.addBroker(connectionBroker);
		
		
		
	
	}
	public void readACLFile()
	{
		// sess.addBroker("amqp://guest:guest@/?brokerlist='tcp://gtiiaprojects.dsic.upv.es:5671?ssl='true',saslMechs='PLAIN',saslProtocol='qpidd',saslServerName='produccion''");
		
		//ArrayList<ClassKey> array = sess.getClasses("org.apache.qpid.acl");
		hash.put("_class", "acl");
		qmf = sess.getObjects(hash);
		qmf.get(0).invokeMethod("reloadACLFile", "");	
	}

	public void finalize() {
	
		sess.close();
		m.advise();
	}
}
