
    /**
     * InformMembersSkeleton.java
     *
     * This file was auto-generated from WSDL
     * by the Apache Axis2 version: 1.2 Apr 27, 2007 (04:35:37 IST)
     */
    package wtp;

import persistence.DataBaseInterface;
    /**
     *  InformMembersSkeleton java skeleton for the axisService
     */
    public class InformMembersSkeleton{
        
         
        /**
         * Auto generated method signature
         
         
                                     * @param informMembers
         */
        

                 public wtp.InformMembersResponse InformMembers
                  (
                  wtp.InformMembers informMembers
                  )
            {
                	 wtp.InformMembersResponse res=new InformMembersResponse();
                     res.setErrorValue("");
                     res.setStatus("Ok");
                     res.setEntityRoleList("");

                     if(informMembers.getUnitID()==""){
                     	res.setErrorValue("Invalid");
                         res.setStatus("Error");
                         return res;
                     }
                     persistence.DataBaseInterface thomasBD=new DataBaseInterface();
                     if(!thomasBD.CheckExistsUnit(informMembers.getUnitID())){
                      	res.setErrorValue("NotFound");
                          res.setStatus("Error"); 
                          return res;                	
                      }
                     res.setEntityRoleList(thomasBD.GetEntityRoleList(informMembers.getUnitID(),informMembers.getRoleID()).toString());
                     return res;
                     } 
     
    }
    