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
		System.out.println("Inside InformNormContent 0");
		System.out.println("Parameters:");
		System.out.println("AgentID="+informNormContent.getAgentID());
		System.out.println("NormID="+informNormContent.getNormID());
		System.out.println("-----");
		wtp.InformNormContentResponse res = new InformNormContentResponse();
		System.out.println("Inside InformNormContent 1");
		res.setErrorValue("");
		res.setStatus("Ok");
		res.setDescription("");
		System.out.println("Inside InformNormContent 2");
		if (informNormContent.getAgentID() == null
				|| informNormContent.getAgentID() == ""
				|| thomasBD.CheckExistsAgent(informNormContent.getAgentID()))
		{
			System.out.println("Inside InformNormContent 3");
			res.setErrorValue("Invalid Agent ID");
			res.setStatus("Error");
			return res;
		}
		System.out.println("Inside InformNormContent 4");
		if (thomasBD.CheckExistsNorm(informNormContent.getNormID()) == false)
		{
			System.out.println("Inside InformNormContent 5");
			res.setErrorValue("Norm does not exist");
			res.setStatus("Error");
			return res;
		}
		System.out.println("Inside InformNormContent 6");
		res.setDescription(thomasBD.GetNormContent(informNormContent
				.getNormID()));
		System.out.println("About to return from InformNormContent");
		return res;
	}
	
}
