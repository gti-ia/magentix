
    /**
     * DeregisterNormSkeleton.java
     *
     * This file was auto-generated from WSDL
     * by the Apache Axis2 version: 1.2 Apr 27, 2007 (04:35:37 IST)
     */
    package wtp;

import java.util.List;

import persistence.DataBaseInterface;
    /**
     *  DeregisterNormSkeleton java skeleton for the axisService
     */
    public class DeregisterNormSkeleton{
    	public static final Boolean DEBUG = true;
    	persistence.DataBaseInterface thomasBD=new DataBaseInterface();
        
        /**
         * Auto generated method signature
         
         
                                     * @param deregisterNorm
         */
        

                 public wtp.DeregisterNormResponse DeregisterNorm
                  (
                  wtp.DeregisterNorm deregisterNorm
                  )
            {
                	 wtp.DeregisterNormResponse res=new DeregisterNormResponse();
                	 if (DEBUG) {
               			System.out.println("DeregisterNorm :");
               			System.out.println("***AgentID..."+ deregisterNorm.getAgentID());
               			System.out.println("*** NormID()..."+ deregisterNorm.getNormID());
               			}
                	 res.setErrorValue("");
                     res.setStatus("Ok");

                     if(deregisterNorm.getNormID()==""){
                     	res.setErrorValue("Invalid");
                         res.setStatus("Error");
                         return res;
                     }
                     
                     if(!thomasBD.CheckExistsNorm(deregisterNorm.getNormID())){
                      	res.setErrorValue("NotFound");
                          res.setStatus("Error"); 
                          return res;                	
                      }
                     if(!roleBasedControl(deregisterNorm.getAgentID()))	
                     {	res.setErrorValue("Not-Allowed");
                  		res.setStatus("Error"); 
                  		return res;
                  	}
                     //Falta comprobar si es el issuer
                     if(!thomasBD.DeleteNorm(deregisterNorm.getNormID())){
                    	 res.setErrorValue("Invalid");
                         res.setStatus("Error"); 
                         return res; 
                     }
                     return res;
            }
           		private boolean roleBasedControl(String agentID) {
        			if(thomasBD.CheckExistsAgent(agentID)) return true;
        			return false;
        		}
     
    }
    