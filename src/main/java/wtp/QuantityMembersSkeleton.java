
    /**
     * QuantityMembersSkeleton.java
     *
     * This file was auto-generated from WSDL
     * by the Apache Axis2 version: 1.2 Apr 27, 2007 (04:35:37 IST)
     */
    package wtp;

import java.math.BigInteger;

import persistence.DataBaseInterface;
    /**
     *  QuantityMembersSkeleton java skeleton for the axisService
     */
    public class QuantityMembersSkeleton{
        
         
        /**
         * Auto generated method signature
         
         
                                     * @param quantityMembers
         */
        

                 public wtp.QuantityMembersResponse QuantityMembers
                  (
                  wtp.QuantityMembers quantityMembers
                  )
            {
                	 wtp.QuantityMembersResponse res=new QuantityMembersResponse();
                     res.setErrorValue("");
                     res.setStatus("Ok");
                     java.math.BigInteger quantity=BigInteger.valueOf(0);
                     res.setQuantity(quantity);

                     if(quantityMembers.getUnitID()==""){
                     	res.setErrorValue("Invalid");
                         res.setStatus("Error");
                         return res;
                     }
                     persistence.DataBaseInterface thomasBD=new DataBaseInterface();
                     if(!thomasBD.CheckExistsUnit(quantityMembers.getUnitID())){
                      	res.setErrorValue("NotFound");
                          res.setStatus("Error"); 
                          return res;                	
                      }
                     quantity=BigInteger.valueOf(thomasBD.GetQuantityMember(quantityMembers.getUnitID(),quantityMembers.getRoleID()));
                     res.setQuantity(quantity);
                     return res;
                     } 
     
    }
    