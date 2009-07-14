
    /**
     * InformUnitSkeleton.java
     *
     * This file was auto-generated from WSDL
     * by the Apache Axis2 version: 1.2 Apr 27, 2007 (04:35:37 IST)
     */
    package wtp;

import persistence.DataBaseInterface;
    /**
     *  InformUnitSkeleton java skeleton for the axisService
     */
    public class InformUnitSkeleton{
        
         
        /**
         * Auto generated method signature
         
         
                                     * @param informUnit
         */
        

                 public wtp.InformUnitResponse InformUnit
                  (
                  wtp.InformUnit informUnit
                  )
            {
                	 wtp.InformUnitResponse res=new InformUnitResponse();
                     res.setErrorValue("");
                     res.setStatus("Ok");
                     res.setParentID("");
                     res.setUnitGoal("");
                     res.setUnitType("");
                     if(informUnit.getUnitID()==""){
                     	res.setErrorValue("Invalid");
                         res.setStatus("Error");
                         return res;
                     }
                     persistence.DataBaseInterface thomasBD=new DataBaseInterface();
                     if(!thomasBD.CheckExistsUnit(informUnit.getUnitID())){
                      	res.setErrorValue("NotFound");
                          res.setStatus("Error"); 
                          return res;                	
                      }
                     res.setParentID(thomasBD.GetParentUnitID(informUnit.getUnitID()));
                     res.setUnitType(thomasBD.GetUnitType(informUnit.getUnitID()));
                     res.setUnitGoal(thomasBD.GetUnitGoal(informUnit.getUnitID()));
                     return res;
                     } 
     
    }
    