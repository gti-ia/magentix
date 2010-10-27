package es.upv.dsic.gti_ia.MMService;


import org.apache.ws.security.WSPasswordCallback;


import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 * Simple password callback handler. This just checks if the password for the private key
 * is being requested, and if so sets that value.
 */
public class PWCBHandler implements CallbackHandler
{
	
	/**
	 * This method set the password key for the MMS encryption alias. 
	 */
    public void handle(Callback[] callbacks) throws IOException {
    	
    	Properties prop = new Properties();
		try {
			
			prop.load(PWCBHandler.class.getResourceAsStream("/"+"securityAdmin.properties"));
			
			//prop.load(new FileInputStream(
				//	"./securityAdmin.properties"));
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
                 if (prop.getProperty("alias").equals(id)) {
                    pwcb.setPassword(prop.getProperty("key"));
                }
                
  
            }
        }
    }
}