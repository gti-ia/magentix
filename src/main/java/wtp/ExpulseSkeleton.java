
    /**
     * ExpulseSkeleton.java
     *
     * This file was auto-generated from WSDL
     * by the Apache Axis2 version: 1.2 Apr 27, 2007 (04:35:37 IST)
     */
    package wtp;

import persistence.DataBaseInterface;
    /**
     *  ExpulseSkeleton java skeleton for the axisService
     */
    public class ExpulseSkeleton{
        
         
        /**
         * Auto generated method signature
         
         
                                     * @param expulse
         */
        

                 public wtp.ExpulseResponse Expulse
                  (
                  wtp.Expulse expulse
                  )
            {
                     {
                    	 wtp.ExpulseResponse res=new ExpulseResponse();
                    	 res.setStatus("Ok");
                    	 res.setErrorValue("");
                    	 if(expulse.getAgentID()=="" || expulse.getRoleID()=="" || expulse.getUnitID()==""){
                          	res.setErrorValue("Invalid");
                            res.setStatus("Error");
                              return res;
                          }
                          persistence.DataBaseInterface thomasBD=new DataBaseInterface();
                          if(!thomasBD.CheckExistsAgent(expulse.getAgentID())){
                           	res.setErrorValue("NotFound");
                               res.setStatus("Error"); 
                               return res;                	
                           }
                          if(!thomasBD.CheckExistsUnit(expulse.getUnitID())){
                             	res.setErrorValue("NotFound");
                                 res.setStatus("Error"); 
                                 return res;                	
                             }
                          if(!thomasBD.CheckExistsRole(expulse.getRoleID())){
                           		res.setErrorValue("NotFound");
                           		res.setStatus("Error"); 
                                return res;                	
                           }
                          if(!thomasBD.CheckAgentPlaysRole(expulse.getRoleID(),expulse.getUnitID(),expulse.getAgentID())){
                         		res.setErrorValue("NotFound");
                           		res.setStatus("Error"); 
                                return res;                      
                          }
                          if(!thomasBD.DeleteAgentPlaysRole(expulse.getRoleID(),expulse.getUnitID(),expulse.getAgentID())){
                       		res.setErrorValue("Invalid");
                         		res.setStatus("Error"); 
                              return res;                      
                          }
                          return res;
                          }}
     
    }
    