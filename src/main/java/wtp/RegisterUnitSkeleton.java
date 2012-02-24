
/**
 * RegisterUnitSkeleton.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.4  Built on : Apr 26, 2008 (06:24:30 EDT)
 */
package wtp;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import persistence.OMSInterface;
/**
 *  RegisterUnitSkeleton java skeleton for the axisService
 */
public class RegisterUnitSkeleton{

	private static OMSInterface omsInterface = new OMSInterface();  
	/**
	 * Auto generated method signature
	 * 
	 * @param registerUnit
	 */

	public wtp.RegisterUnitResponse RegisterUnit
	(
			wtp.RegisterUnit registerUnit
	)
	{
		wtp.RegisterUnitResponse res = new RegisterUnitResponse();
		String result = "";

		DOMConfigurator.configure(RegisterUnitSkeleton.class.getResource("/"+"loggin.xml"));
		Logger logger = Logger.getLogger(RegisterUnitSkeleton.class);


		logger.info("RegisterUnit :");
		logger.info("***AgentID..." + registerUnit.getAgentID());
		logger.info("***UnitID..." + registerUnit.getUnitID());
		logger.info("***ParentUnitID..."
				+ registerUnit.getParentUnitID());
		logger.info("***CreatorName..." + registerUnit.getCreatorID());
		logger.info("***Type..." + registerUnit.getTypeID());




		if (registerUnit.getParentUnitID().equals(""))
		{

			result =omsInterface.registerUnit(registerUnit.getUnitID(), registerUnit.getTypeID(), registerUnit.getAgentID(), registerUnit.getCreatorID());
		}
		else
		{

			result =omsInterface.registerUnit(registerUnit.getUnitID(), registerUnit.getTypeID(), registerUnit.getParentUnitID(), registerUnit.getAgentID(), registerUnit.getCreatorID());
		}

		res.setResult(result);
		return res;
	}

}
