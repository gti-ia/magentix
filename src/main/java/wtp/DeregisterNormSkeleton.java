
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
                     res.setErrorValue("");
                     res.setStatus("Ok");

                     if(deregisterNorm.getNormID()==""){
                     	res.setErrorValue("Invalid");
                         res.setStatus("Error");
                         return res;
                     }
                     persistence.DataBaseInterface thomasBD=new DataBaseInterface();
                     if(!thomasBD.CheckExistsNorm(deregisterNorm.getNormID())){
                      	res.setErrorValue("NotFound");
                          res.setStatus("Error"); 
                          return res;                	
                      }
                     //Control acceso basado en roles


                     //Falta comprobar si es el isssuer
                     if(!thomasBD.DeleteNorm(deregisterNorm.getNormID())){
                    	 res.setErrorValue("Invalid");
                         res.setStatus("Error"); 
                         return res; 
                     }
                     return res;
            }
     
    }
    