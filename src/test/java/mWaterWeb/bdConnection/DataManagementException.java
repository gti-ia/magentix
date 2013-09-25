package mWaterWeb.bdConnection;

/**
 * @author agarridot
 *
 */
public class DataManagementException extends Exception 
{
	public DataManagementException(String father_msg) 
	{
		super("Exception when managing data. "+father_msg);
	}

}
