
/**
 * InformTargetNormsSkeleton.java
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
 *  InformTargetNormsSkeleton java skeleton for the axisService
 */
public class InformTargetNormsSkeleton{
	
	private Agent myAgent = null;
	private OMSInterface omsInterface = null;

	/**
	 * Auto generated method signature
	 * 
	 * @param informTargetNorms
	 */

	public wtp.InformTargetNormsResponse InformTargetNorms
	(
			wtp.InformTargetNorms informTargetNorms
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
		
		wtp.InformTargetNormsResponse res = new InformTargetNormsResponse();
		String result = "";

		DOMConfigurator.configure(InformMembersSkeleton.class.getResource("/"+"loggin.xml"));
		Logger logger = Logger.getLogger(InformMembersSkeleton.class);

		logger.info("InformTargetNorms :");
		logger.info("***AgentID..." + informTargetNorms.getAgentID());
		logger.info("***UnitID()..." + informTargetNorms.getUnitID());
		logger.info("***TargetTypeName()..." + informTargetNorms.getTargetTypeName());
		logger.info("***TargetValueName()..." + informTargetNorms.getTargetValueName());
		
		
		
		result =omsInterface.informTargetNorms(informTargetNorms.getTargetTypeName(), informTargetNorms.getTargetValueName(), informTargetNorms.getUnitID(), informTargetNorms.getAgentID());
		res.setResult(result);
		
		
		return res;
	}

}
