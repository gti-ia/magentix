package es.upv.dsic.gti_ia.MMService;


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
    public void handle(Callback[] callbacks) throws IOException {
    	
    	Properties prop = new Properties();
		try {
			prop.load(new FileInputStream(
					"./configuration/securityAdmin.properties"));
		} catch (FileNotFoundException e) {
			System.err.println(e);
		} catch (IOException e) {
			System.err.println(e);
		}
    	
        for (int i = 0; i < callbacks.length; i++) {
            WSPasswordCallback pwcb = (WSPasswordCallback)callbacks[i];
            String id = pwcb.getIdentifier();
            int usage = pwcb.getUsage();
            if (usage == WSPasswordCallback.DECRYPT || usage == WSPasswordCallback.SIGNATURE) {
                
                //Used to retrieve password for private key
            	//serverkey es el alias, por tanto sera el password del keystore
                if (prop.getProperty("alias").equals(id)) {
                    pwcb.setPassword(prop.getProperty("key"));
                }
  
            }
        }
    }
}