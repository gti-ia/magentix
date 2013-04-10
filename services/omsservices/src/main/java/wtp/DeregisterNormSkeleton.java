
/**
 * DeregisterNormSkeleton.java
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
 *  DeregisterNormSkeleton java skeleton for the axisService
 */
public class DeregisterNormSkeleton{

	private Agent myAgent = null;
	private OMSInterface omsInterface = null;

	/**
	 * Auto generated method signature
	 * 
	 * @param deregisterNorm
	 */

	public wtp.DeregisterNormResponse DeregisterNorm
	(
			wtp.DeregisterNorm deregisterNorm
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
		wtp.DeregisterNormResponse res = new DeregisterNormResponse();
		String result = "";

		DOMConfigurator.configure(RegisterRoleSkeleton.class.getResource("/"+"loggin.xml"));
		Logger logger = Logger.getLogger(RegisterRoleSkeleton.class);


		logger.info("RegisterRole :");
		logger.info("***AgentID..." + deregisterNorm.getAgentID());
		logger.info("***UnitID..." + deregisterNorm.getUnitID());
		logger.info("***NormID..." + deregisterNorm.getNormID());


		result = omsInterface.deregisterNorm(deregisterNorm.getNormID(), deregisterNorm.getUnitID(), deregisterNorm.getAgentID());
		res .setResult(result);
		return res;
	}

}
