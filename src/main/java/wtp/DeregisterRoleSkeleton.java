
    /**
     * DeregisterRoleSkeleton.java
     *
     * This file was auto-generated from WSDL
     * by the Apache Axis2 version: 1.2 Apr 27, 2007 (04:35:37 IST)
     */
    package wtp;

import persistence.DataBaseInterface;
    /**
     *  DeregisterRoleSkeleton java skeleton for the axisService
     */
    public class DeregisterRoleSkeleton{
        
         
        /**
         * Auto generated method signature
         
         
                                     * @param deregisterRole
         */
        

                 public wtp.DeregisterRoleResponse DeregisterRole
                  (
                  wtp.DeregisterRole deregisterRole
                  )
            {
                	 wtp.DeregisterRoleResponse res=new DeregisterRoleResponse();
                     res.setErrorValue("");
                     res.setStatus("Ok");

                     if(deregisterRole.getRoleID()=="" || deregisterRole.getRoleID().equalsIgnoreCase("member") || deregisterRole.getUnitID()==""){
                     	res.setErrorValue("Invalid");
                         res.setStatus("Error");
                         return res;
                     }
                     persistence.DataBaseInterface thomasBD=new DataBaseInterface();
                     if(!thomasBD.CheckExistsRoleInUnit(deregisterRole.getRoleID(),deregisterRole.getUnitID())){
                     	res.setErrorValue("NotFound");
                         res.setStatus("Error"); 
                         return res;                	
                     }
              
                     if(thomasBD.CheckRoleHasNorms(deregisterRole.getRoleID())){
                    	 res.setErrorValue("Invalid");
                         res.setStatus("ErrorNorm"); 
                         return res;
                     }
                     if(thomasBD.CheckRoleIsPlayed(deregisterRole.getRoleID())){
                    	 res.setErrorValue("Invalid");
                         res.setStatus("ErrorPlayed"); 
                         return res;
                     }
                     if(!thomasBD.DeleteRole(deregisterRole.getRoleID(),deregisterRole.getUnitID())){
                     	res.setErrorValue("Invalid");
                         res.setStatus("Error"); 
                         return res; 
                     }
                     return res;
             }
     
    }
    