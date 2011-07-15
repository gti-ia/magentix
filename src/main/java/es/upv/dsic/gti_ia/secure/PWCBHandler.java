package es.upv.dsic.gti_ia.secure;


import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
//import org.apache.log4j.xml.DOMConfigurator;
import org.apache.ws.security.WSPasswordCallback;


import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;


/**
 * Simple password callback handler. This just checks if the password for the private key
 * is being requested, and if so sets that value.
 */
public class PWCBHandler implements CallbackHandler
{

	protected Logger logger = Logger.getLogger(PWCBHandler.class);

	Properties prop = null;
	FileInputStream file = null;
	/**
	 * This method set the password for the private key. 
	 */
	public void handle(Callback[] callbacks) throws IOException {
		//DOMConfigurator.configure("configuration/loggin.xml");

		file = new FileInputStream(
		"./configuration/securityUser.properties");

		prop = new Properties();
		try {
			prop.load(file);



			




			//String idP = prop.getProperty("alias");
			String keySignature = prop.getProperty("KeyStorePassword");
			String keyDecrypt = prop.getProperty("KeyStorePassword");

			for (int i = 0; i < callbacks.length; i++) {

				WSPasswordCallback pwcb = (WSPasswordCallback)callbacks[i];

				int usage = pwcb.getUsage();


				if (usage == WSPasswordCallback.SIGNATURE) {
					pwcb.setPassword(keySignature);
				}
				else if (usage == WSPasswordCallback.DECRYPT)
				{
					pwcb.setPassword(keyDecrypt);

				}

			}
		} catch (FileNotFoundException e) {
			logger.error(e);
		} catch (IOException e) {
			logger.error(e);
		}
		finally {
			//IOUtils.closeQuietly(file);
			//file.reset();
	

			//file.getFD().sync();
			file.close();


		}


	}
}