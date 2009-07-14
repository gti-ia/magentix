
    /**
     * InformAgentRoleSkeleton.java
     *
     * This file was auto-generated from WSDL
     * by the Apache Axis2 version: 1.2 Apr 27, 2007 (04:35:37 IST)
     */
    package wtp;

import persistence.DataBaseInterface;
    /**
     *  InformAgentRoleSkeleton java skeleton for the axisService
     */
    public class InformAgentRoleSkeleton{
        
         
        /**
         * Auto generated method signature
         
         
                                     * @param informAgentRole
         */
        

                 public wtp.InformAgentRoleResponse InformAgentRole
                  (
                  wtp.InformAgentRole informAgentRole
                  )
            {
                	 wtp.InformAgentRoleResponse res=new InformAgentRoleResponse();
                     res.setErrorValue("");
                     res.setStatus("Ok");
                     res.setRoleUnitList("");

                     if(informAgentRole.getAgentID()==""){
                     	res.setErrorValue("Invalid");
                         res.setStatus("Error");
                         return res;
                     }
                     persistence.DataBaseInterface thomasBD=new DataBaseInterface();
                     if(!thomasBD.CheckExistsAgent(informAgentRole.getAgentID())){
                      	res.setErrorValue("NotFound");
                          res.setStatus("Error"); 
                          return res;                	
                      }
                     res.setRoleUnitList(thomasBD.GetRoleUnitList(informAgentRole.getAgentID()).toString());
                     return res;
                     }
     
    }
    