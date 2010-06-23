package es.upv.dsic.gti_ia.secure;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.apache.ws.security.WSPasswordCallback;


import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;



public class PWCBHandler implements CallbackHandler
{
	
	protected Logger logger = Logger.getLogger(PWCBHandler.class);
	
    public void handle(Callback[] callbacks) throws IOException {
    	DOMConfigurator.configure("configuration/loggin.xml");
    	Properties prop = new Properties();
    	try {
			prop.load(new FileInputStream(
					"./configuration/securityUser.properties"));
		} catch (FileNotFoundException e) {
			logger.error(e);
		} catch (IOException e) {
			logger.error(e);
		}
		String idP = prop.getProperty("alias");
		String key= prop.getProperty("key");
		 
        for (int i = 0; i < callbacks.length; i++) {
            WSPasswordCallback pwcb = (WSPasswordCallback)callbacks[i];
            String id = pwcb.getIdentifer();
            int usage = pwcb.getUsage();
            if (usage == WSPasswordCallback.DECRYPT ||
                usage == WSPasswordCallback.SIGNATURE) {

                // used to retrieve password for private key
                if (idP.equals(id)) {
                    pwcb.setPassword(key);
                }

            }
        }
    }
}