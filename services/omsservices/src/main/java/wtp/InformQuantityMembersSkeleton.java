
/**
 * InformQuantityMembersSkeleton.java
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
     *  InformQuantityMembersSkeleton java skeleton for the axisService
     */
    public class InformQuantityMembersSkeleton{
        
    	private Agent myAgent = null;
    	private OMSInterface omsInterface = null;
        /**
         * Auto generated method signature
         * 
                                     * @param informQuantityMembers
         */
        
                 public wtp.InformQuantityMembersResponse InformQuantityMembers
                  (
                  wtp.InformQuantityMembers informQuantityMembers
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
             		
                	 wtp.InformQuantityMembersResponse res = new InformQuantityMembersResponse();
             		String result = "";

             		DOMConfigurator.configure(InformQuantityMembersSkeleton.class.getResource("/"+"loggin.xml"));
             		Logger logger = Logger.getLogger(InformQuantityMembersSkeleton.class);

             		logger.info("QuantityMembers :");
             		logger.info("***AgentID..." + informQuantityMembers.getAgentID());
             		logger.info("***UnitID..." + informQuantityMembers.getUnitID());
             		logger.info("***RoleID..." + informQuantityMembers.getRoleID());
             		logger.info("***PositionID..." + informQuantityMembers.getPositionID());



             		if (informQuantityMembers.getUnitID().equals("null"))
             			result = omsInterface.informQuantityMembers(null,informQuantityMembers.getRoleID(), informQuantityMembers.getPositionID(), informQuantityMembers.getAgentID());
             		else
             			result = omsInterface.informQuantityMembers(informQuantityMembers.getUnitID(),informQuantityMembers.getRoleID(), informQuantityMembers.getPositionID(), informQuantityMembers.getAgentID());
             		res.setResult(result);
             		return res;
        }
     
    }
    