
    /**
     * InformUnitRolesSkeleton.java
     *
     * This file was auto-generated from WSDL
     * by the Apache Axis2 version: 1.2 Apr 27, 2007 (04:35:37 IST)
     */
    package wtp;

import persistence.DataBaseInterface;
    /**
     *  InformUnitRolesSkeleton java skeleton for the axisService
     */
    public class InformUnitRolesSkeleton{
        
         
        /**
         * Auto generated method signature
         
         
                                     * @param informUnitRoles
         */
        

                 public wtp.InformUnitRolesResponse InformUnitRoles
                  (
                  wtp.InformUnitRoles informUnitRoles
                  )
            {
                	 wtp.InformUnitRolesResponse res=new InformUnitRolesResponse();
                     res.setErrorValue("");
                     res.setStatus("Ok");
                     res.setRoleList("");
                     if(informUnitRoles.getUnitID()==""){
                     	res.setErrorValue("Invalid");
                         res.setStatus("Error");
                         return res;
                     }
                     persistence.DataBaseInterface thomasBD=new DataBaseInterface();
                     if(!thomasBD.CheckExistsUnit(informUnitRoles.getUnitID())){
                      	res.setErrorValue("NotFound");
                          res.setStatus("Error"); 
                          return res;                	
                      }
                     res.setRoleList(thomasBD.GetRoleList(informUnitRoles.getUnitID()).toString());
                     return res;
                     } 
     
    }
    