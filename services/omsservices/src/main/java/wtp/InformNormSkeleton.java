
/**
 * InformNormSkeleton.java
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
     *  InformNormSkeleton java skeleton for the axisService
     */
    public class InformNormSkeleton{
        
    	private Agent myAgent = null;
    	private OMSInterface omsInterface = null;
        /**
         * Auto generated method signature
         * 
                                     * @param informNorm
         */
        
                 public wtp.InformNormResponse InformNorm
                  (
                  wtp.InformNorm informNorm
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
             		
                		wtp.InformNormResponse res = new InformNormResponse();
                		String result = "";

                		DOMConfigurator.configure(InformMembersSkeleton.class.getResource("/"+"loggin.xml"));
                		Logger logger = Logger.getLogger(InformMembersSkeleton.class);

                		logger.info("InformMembers :");
                		logger.info("***AgentID..." + informNorm.getAgentID());
                		logger.info("***UnitID()..." + informNorm.getUnitID());
                		logger.info("***NormID()..." + informNorm.getNormID());
                		
                		
                		
                		result =omsInterface.informNorm(informNorm.getNormID(), informNorm.getUnitID(), informNorm.getAgentID());
                		res.setResult(result);
                		return res;
        }
     
    }
    