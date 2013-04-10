
/**
 * JoinUnitSkeleton.java
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
     *  JoinUnitSkeleton java skeleton for the axisService
     */
    public class JoinUnitSkeleton{
    	
    	private Agent myAgent = null;
    	private OMSInterface omsInterface = null;
        
         
        /**
         * Auto generated method signature
         * 
                                     * @param joinUnit
         */
        
                 public wtp.JoinUnitResponse JoinUnit
                  (
                  wtp.JoinUnit joinUnit
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
             		
                		wtp.JoinUnitResponse res = new JoinUnitResponse();
                		String result = "";
                		DOMConfigurator.configure(JoinUnitSkeleton.class.getResource("/"+"loggin.xml"));
                		Logger logger = Logger.getLogger(JoinUnitSkeleton.class);
                		logger.info("JoinUnit :");
                		logger.info("***AgentID..." + joinUnit.getAgentID());
                		logger.info("***UnitID..." + joinUnit.getUnitID());
                		logger.info("***ParentUnitID..."
                				+ joinUnit.getParentUnitID());




                		if (joinUnit.getUnitID().equals("null"))
                			result =omsInterface.joinUnit(null, joinUnit.getParentUnitID(), joinUnit.getAgentID());
                		else if (joinUnit.getParentUnitID().equals("null"))
                			result =omsInterface.joinUnit(joinUnit.getUnitID(), null, joinUnit.getAgentID());
                		else
                			result =omsInterface.joinUnit(joinUnit.getUnitID(), joinUnit.getParentUnitID(), joinUnit.getAgentID());
                		res.setResult(result);
                		return res;
        }
     
    }
    