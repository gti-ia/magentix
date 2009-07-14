
    /**
     * InformRoleNormsSkeleton.java
     *
     * This file was auto-generated from WSDL
     * by the Apache Axis2 version: 1.2 Apr 27, 2007 (04:35:37 IST)
     */
    package wtp;

import persistence.DataBaseInterface;
    /**
     *  InformRoleNormsSkeleton java skeleton for the axisService
     */
    public class InformRoleNormsSkeleton{
        
         
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
                     res.setNormList(thomasBD.GetRoleNormsList(informRoleNorms.getRoleID()).toString());
                     return res;
                     } 
     
    }
    