
    /**
     * DeregisterUnitSkeleton.java
     *
     * This file was auto-generated from WSDL
     * by the Apache Axis2 version: 1.2 Apr 27, 2007 (04:35:37 IST)
     */
    package wtp;

import java.util.List;

import persistence.DataBaseInterface;
    /**
     *  DeregisterUnitSkeleton java skeleton for the axisService
     */
    public class DeregisterUnitSkeleton{
        
         
        /**
         * Auto generated method signature
         
         
                                     * @param deregisterUnit
         */
        

                 public wtp.DeregisterUnitResponse DeregisterUnit
                  (
                  wtp.DeregisterUnit deregisterUnit
                  )
            {
                	 wtp.DeregisterUnitResponse res=new DeregisterUnitResponse();
                     res.setErrorValue("");
                     res.setStatus("Ok");

                     if(deregisterUnit.getUnitID()=="" || deregisterUnit.getUnitID().equalsIgnoreCase("virtual")){
                     	res.setErrorValue("Invalid");
                         res.setStatus("Error");
                         return res;
                     }
                     persistence.DataBaseInterface thomasBD=new DataBaseInterface();
                     if(!thomasBD.CheckExistsUnit(deregisterUnit.getUnitID())){
                      	res.setErrorValue("NotFound");
                          res.setStatus("Error"); 
                          return res;                	
                      }
                     if(thomasBD.CheckUnitHasRole(deregisterUnit.getUnitID())){
                     	res.setErrorValue("Invalid");
                         res.setStatus("Error"); 
                         return res;                	
                     }
                     if(thomasBD.CheckUnitHasUnit(deregisterUnit.getUnitID())){
                      	res.setErrorValue("Invalid");
                          res.setStatus("Error"); 
                          return res;                	
                      }
                     if(thomasBD.CheckUnitHasMember(deregisterUnit.getUnitID())){
                       	res.setErrorValue("Invalid");
                           res.setStatus("Error"); 
                           return res;                	
                       }
                     //control acceso basado en roles
     
                     if(!thomasBD.DeleteUnit(deregisterUnit.getUnitID())){
                     	res.setErrorValue("Invalid");
                         res.setStatus("Error"); 
                         return res; 
                     }
                     return res;
                 }
     
    }
    