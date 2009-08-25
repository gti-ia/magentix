
    /**
     * InformRoleNormsSkeleton.java
     *
     * This file was auto-generated from WSDL
     * by the Apache Axis2 version: 1.2 Apr 27, 2007 (04:35:37 IST)
     */
    package wtp;

import java.util.List;

import persistence.DataBaseInterface;
    /**
     *  InformRoleNormsSkeleton java skeleton for the axisService
     */

    public class InformRoleNormsSkeleton{
    	persistence.DataBaseInterface thomasBD=new DataBaseInterface();
        public static final Boolean DEBUG=true; 
        /**
         * Auto generated method signature
         
         
                                     * @param informRoleNorms
         */
        

                 public wtp.InformRoleNormsResponse InformRoleNorms
                  (
                  wtp.InformRoleNorms informRoleNorms
                  )
            {
                	 wtp.InformRoleNormsResponse res=new InformRoleNormsResponse();
                	 if (DEBUG) {
             			System.out.println("InformRoleNorms :");
             			System.out.println("***AgentID..."+ informRoleNorms.getAgentID());
             			System.out.println("***ROleID()..."+ informRoleNorms.getRoleID());
                       	  
             	 }
                	 
                	 res.setErrorValue("");
                     res.setStatus("Ok");
                     res.setNormList("");
                     if(informRoleNorms.getRoleID()==""){
                     	res.setErrorValue("Invalid");
                         res.setStatus("Error");
                         return res;
                     }
                     persistence.DataBaseInterface thomasBD=new DataBaseInterface();
                     if(!thomasBD.CheckExistsRole(informRoleNorms.getRoleID())){
                      	res.setErrorValue("NotFound");
                          res.setStatus("Error"); 
                          return res;                	
                      }
                     //role based control
                     if(!roleBasedControl(informRoleNorms.getAgentID()))	
                     {	res.setErrorValue("Not-Allowed");
                  		res.setStatus("Error"); 
                  		return res;
                  	}
                     res.setNormList(thomasBD.GetRoleNormsList(informRoleNorms.getRoleID()).toString());
                     return res;
                     } 
            		private boolean roleBasedControl(String agentID) {
            			if(thomasBD.CheckExistsAgent(agentID)) return true;
            			return false;
            		}
    }
    