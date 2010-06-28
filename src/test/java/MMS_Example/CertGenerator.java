package MMS_Example;


import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
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
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Date;

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

public class CertGenerator {



	public static void main(String[] args) throws InvalidKeyException,
			NoSuchAlgorithmException, CertificateException, SignatureException,
			NoSuchProviderException, IOException {

		String configName = "./certificates/nss.cfg";
		Provider p = new sun.security.pkcs11.SunPKCS11(configName);
		Security.addProvider(p);



		PrivateKeyEntry groupEntry = null;
		try {
			groupEntry = generateCert("RSA", "SHA1WithRSA", 1024, null,90, null);
		} catch (KeyStoreException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}

		// CreaciÃ³n del certificado de Usuario (Certificado firmado por la
		// clave de Grupo)
		String commonName = "MMS";
		String organizationalUnit = "GTI";
		String organization = "DSIC";
		String city = "VALÉNCIA";
		String state = "COMUNIDAD DE VALENCIA";
		String country = "ES";

		X500Name x500Name = new X500Name(commonName, organizationalUnit,organization, city, state, country);
		PrivateKeyEntry userEntry = null;
		try {
			userEntry = generateCert("RSA", "SHA1WithRSA", 1024, x500Name, 90,
					groupEntry);
		} catch (KeyStoreException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}

		System.out.println("Group Entry");
		System.out.println(groupEntry);

		System.out.println("User Entry");
		System.out.println(userEntry);

		Certificate certUser = userEntry.getCertificate();
		Certificate certGroup = groupEntry.getCertificate();

		X509Certificate[] chain = new X509Certificate[2];

		chain[0] = (X509Certificate) certUser;
		chain[1] = (X509Certificate) certGroup;

		char[] key = "key123".toCharArray();

		KeyStore truststore = null;
		try {
			truststore = KeyStore.getInstance("JKS");
		} catch (KeyStoreException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		boolean existe = true;
		FileInputStream ksfis = null;
		//si no existe lo creamos, sinos lo abrimos
		try
		{
			ksfis = new FileInputStream("./certificates/user_certificates/keystore.jks");
		}
		catch(Exception e)
		{
			existe = false;
			ksfis = null;
		}
		//BufferedInputStream ksbufin = new BufferedInputStream(ksfis);
		//char[] spass = "key123".toCharArray();
		
		if(existe)
		{
			//lo abrimos
			truststore.load(ksfis, "key123".toCharArray());
		}
		else
		{
		//lo creamos
		truststore.load(null);
		}
	
		
		
		
		
		FileOutputStream keyStoreFile = new FileOutputStream("./certificates/user_certificates/keystore.jks");
		
		
		
		

		// Añadir las claves y el certificado al keystore
		try {			
			truststore.setKeyEntry("MMS",userEntry.getPrivateKey(),key,chain);	
		} catch (KeyStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		char[] pass1 = "key123".toCharArray();
		try {
			truststore.store(keyStoreFile, pass1);
		} catch (KeyStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Certificado importado");
		System.out.println("Autofirmado User:" + autofirmado(certUser));
		System.out.println("Autofirmado Group:" + autofirmado(certGroup));
		System.out.println("Firmado User por Group:"+ firmadoPor(certUser, certGroup));

	}

	public static boolean firmadoPor(Certificate certA, Certificate certB) {
		try {
			certA.verify(certB.getPublicKey());
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public static boolean autofirmado(Certificate cert) {
		return firmadoPor(cert, cert);
	}

	public static PrivateKeyEntry generateCert(String keyType, String sigAlg,
			int keyBits, X500Name myname, long validity,
			PrivateKeyEntry issuerEntry) throws NoSuchAlgorithmException,
			InvalidKeyException, CertificateException, SignatureException,
			NoSuchProviderException, IOException, KeyStoreException {

		Certificate rootCertificate = null;
		X509Certificate rootX509certificate = null;
		PrivateKey rootkey = null;
		// tenemos que extraer el certificado de la keystore
		if (issuerEntry == null) {
			
			

			char[] pin = "certificate1".toCharArray();
			KeyStore ks = KeyStore.getInstance("PKCS11");
			ks.load(null, pin);
			
			rootCertificate = ks.getCertificate("CA");
			rootCertificate.getPublicKey();
			rootX509certificate = (X509Certificate) rootCertificate;

			myname = (X500Name) rootX509certificate.getSubjectDN();
		
			try {
				rootkey = (PrivateKey) ks.getKey("CA", pin);
			} catch (UnrecoverableKeyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		KeyPairGenerator keyGen = KeyPairGenerator.getInstance(keyType);
		SecureRandom prng = new SecureRandom();

		keyGen.initialize(keyBits, prng);
		KeyPair pair = keyGen.generateKeyPair();

		PublicKey publicKey = pair.getPublic();
		PrivateKey privateKey = pair.getPrivate();

		Signature signature = Signature.getInstance(sigAlg);
		X500Signer issuer;
		if (issuerEntry == null) {
			signature.initSign(rootkey);
			issuer = new X500Signer(signature, myname);
		} else {

			signature.initSign(issuerEntry.getPrivateKey());
			issuer = new X500Signer(signature, new X500Name(
					((X509Certificate) issuerEntry.getCertificate())
							.getIssuerX500Principal().getEncoded()));
		}

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
					(int) (firstDate.getTime() / 1000)));

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
		if (issuerEntry == null) {
			cert.sign(rootkey, sigAlg);
		} else {
			cert.sign(issuerEntry.getPrivateKey(), sigAlg);
		}

		Certificate[] certs;
		if (issuerEntry == null) {

			certs = new Certificate[] { rootCertificate };
		} else {
			certs = new Certificate[] { cert, issuerEntry.getCertificate() };
		}
		PrivateKeyEntry entry = null;
		if (issuerEntry == null) {
			entry = new KeyStore.PrivateKeyEntry(rootkey, certs);
		} else {
			entry = new KeyStore.PrivateKeyEntry(privateKey, certs);
		}

		return entry;
	}

}
