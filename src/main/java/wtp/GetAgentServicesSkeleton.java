
/**
 * GetAgentServicesSkeleton.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.4.1  Built on : Aug 13, 2008 (05:03:35 LKT)
 */
    package wtp;

import persistence.DataBaseInterface;
    /**
     *  GetAgentServicesSkeleton java skeleton for the axisService
     */
    public class GetAgentServicesSkeleton{
        
    	public static final Boolean DEBUG = true;

  


        /**
         * Auto generated method signature
         * 
                                     * @param getAgentServices
         */
        
                 public wtp.GetAgentServicesResponse GetAgentServices(wtp.GetAgentServices getAgentServices)
            {
                	 
                	 GetAgentServicesResponse response = new GetAgentServicesResponse();
                	 if (DEBUG) {
             			System.out.println("GetProcess Service:");
             			System.out.println("***AgentID... " + getAgentServices.getAgentID());
             		}
                	 
                	 persistence.DataBaseInterface thomasBD = new DataBaseInterface();
                	 String list = null;
                	 list = thomasBD.DameServiciosDelAgente(getAgentServices.getAgentID());
                	 response.setServicesList(list);
                	 if(list!= null){
                		 response.set_return(1);
                		 response.setServicesList(list);
                	 }
                	 else{
                		 response.set_return(0);
                		 response.setServicesList("");
                	 }
                	 
                	 return(response);
                	 
            }
     
    }
    