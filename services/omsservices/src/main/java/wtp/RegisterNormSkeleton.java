
/**
 * RegisterNormSkeleton.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.4  Built on : Apr 26, 2008 (06:24:30 EDT)
 */
package wtp;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.organization.OMSInterface;
/**
 *  RegisterNormSkeleton java skeleton for the axisService
 */
public class RegisterNormSkeleton{
	private Agent myAgent = null;
	private OMSInterface omsInterface = null;

	/**
	 * Auto generated method signature
	 * 
	 * @param registerNorm
	 */

	public wtp.RegisterNormResponse RegisterNorm
	(
			wtp.RegisterNorm registerNorm
	)
	{
		try {
			myAgent = new Agent(new AgentID("myAgent"));
			myAgent.start();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		omsInterface = new OMSInterface(myAgent);
		
		wtp.RegisterNormResponse res = new RegisterNormResponse();
		String result = "";

		DOMConfigurator.configure(RegisterRoleSkeleton.class.getResource("/"+"loggin.xml"));
		Logger logger = Logger.getLogger(RegisterRoleSkeleton.class);


		logger.info("RegisterRole :");
		logger.info("***AgentID..." + registerNorm.getAgentID());
		logger.info("***UnitID..." + registerNorm.getUnitID());
		logger.info("***NormContent..." + registerNorm.getNormContent());


		result = omsInterface.registerNorm(registerNorm.getUnitID(), registerNorm.getNormContent(), registerNorm.getAgentID());
		res.setResult(result);
		return res;
	}

}
