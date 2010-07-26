/**
 * InformNormContentSkeleton.java This file was auto-generated from WSDL by the Apache Axis2
 * version: 1.4 Built on : Apr 26, 2008 (06:24:30 EDT)
 */
package wtp;

import persistence.DataBaseInterface;

/**
 * InformNormContentSkeleton java skeleton for the axisService
 */
public class InformNormContentSkeleton
{
	persistence.DataBaseInterface	thomasBD	= new DataBaseInterface();
	/**
	 * Auto generated method signature
	 * @param informNormContent
	 */
	
	public wtp.InformNormContentResponse InformNormContent(
			wtp.InformNormContent informNormContent)
	{
		wtp.InformNormContentResponse res = new InformNormContentResponse();
		res.setErrorValue("");
		res.setStatus("Ok");
		res.setDescription("");
		
		if (informNormContent.getAgentID() == null
				|| informNormContent.getAgentID() == ""
				|| thomasBD.CheckExistsAgent(informNormContent.getAgentID()))
		{
			res.setErrorValue("Invalid Agent ID");
			res.setStatus("Error");
			return res;
		}
		if (thomasBD.CheckExistsNorm(informNormContent.getNormID()) == false)
		{
			res.setErrorValue("Norm does not exist");
			res.setStatus("Error");
			return res;
		}
		res.setDescription(thomasBD.GetNormContent(informNormContent
				.getNormID()));
		return res;
	}
	
}
