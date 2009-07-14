
    /**
     * RegisterUnitSkeleton.java
     *
     * This file was auto-generated from WSDL
     * by the Apache Axis2 version: 1.2 Apr 27, 2007 (04:35:37 IST)
     */
    package wtp;

import persistence.DataBaseInterface;
    /**
     *  RegisterUnitSkeleton java skeleton for the axisService
     */
    public class RegisterUnitSkeleton{
        
         
        /**
         * Auto generated method signature
         
         
                                     * @param registerUnit
         */
        

                 public wtp.RegisterUnitResponse RegisterUnit
                  (
                  wtp.RegisterUnit registerUnit
                  )
            {
                	 wtp.RegisterUnitResponse res=new RegisterUnitResponse();
                     res.setErrorValue("");
                     res.setStatus("Ok");
                   
                     if(registerUnit.getUnitID()=="" || registerUnit.getGoal()==""){
                     	res.setErrorValue("Invalid");
                         res.setStatus("Error");
                         return res;
                     }
                     if(registerUnit.getType()=="") registerUnit.setType("FLAT");
                     if(registerUnit.getParentUnitID()=="") registerUnit.setParentUnitID("VIRTUAL");
                     if(registerUnit.getType().equalsIgnoreCase("FLAT") && registerUnit.getType().equalsIgnoreCase("HIERARCHY")
                    	 && registerUnit.getType().equalsIgnoreCase("TEAM")){
                     	res.setErrorValue("Invalid");
                         res.setStatus("Error");      
                         return res;
                     }
                     persistence.DataBaseInterface thomasBD=new DataBaseInterface();
                     if(thomasBD.CheckExistsUnit(registerUnit.getUnitID())){
                     	res.setErrorValue("Duplicate");
                         res.setStatus("Error"); 
                         return res;                	
                     }
                     if(!thomasBD.AddNewUnit(registerUnit.getUnitID(), registerUnit.getType(), registerUnit.getGoal(), registerUnit.getParentUnitID()))
                     {	res.setErrorValue("Invalid");
                     	res.setStatus("Error"); 
                     	return res;
                     	}
                     return res;
            }
     
    }
    