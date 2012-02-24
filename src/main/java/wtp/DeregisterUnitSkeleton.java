
/**
 * DeregisterUnitSkeleton.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.4  Built on : Apr 26, 2008 (06:24:30 EDT)
 */
package wtp;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import persistence.OMSInterface;

/**
 *  DeregisterUnitSkeleton java skeleton for the axisService
 */
public class DeregisterUnitSkeleton{


	
	private static OMSInterface omsInterface = new OMSInterface();

	/**
	 * Auto generated method signature
	 * 
	 * @param deregisterUnit
	 */

	public wtp.DeregisterUnitResponse DeregisterUnit
	(
			wtp.DeregisterUnit deregisterUnit
	)
	{
		wtp.DeregisterUnitResponse res = new DeregisterUnitResponse();
		String result = "";

		DOMConfigurator.configure(DeregisterUnitSkeleton.class.getResource("/"+"loggin.xml"));
		Logger logger = Logger.getLogger(DeregisterUnitSkeleton.class);
	
		logger.info("DeregisterUnit :");
		logger.info("***AgentID..." + deregisterUnit.getAgentID());
		logger.info("*** UnitID()..." + deregisterUnit.getUnitID());
	


		if (deregisterUnit.getUnitID().equals("null"))
			result =omsInterface.deregisterUnit(null, deregisterUnit.getAgentID());
		else
			result =omsInterface.deregisterUnit(deregisterUnit.getUnitID(), deregisterUnit.getAgentID());
		res.setResult(result);
		return res;

	}

}
