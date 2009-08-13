
    /**
     * QuantityMembersSkeleton.java
     *
     * This file was auto-generated from WSDL
     * by the Apache Axis2 version: 1.2 Apr 27, 2007 (04:35:37 IST)
     */
    package wtp;

import java.math.BigInteger;
import java.util.List;

import persistence.DataBaseInterface;
    /**
     *  QuantityMembersSkeleton java skeleton for the axisService
     */
    public class QuantityMembersSkeleton{
        persistence.DataBaseInterface thomasBD=new DataBaseInterface();
         
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
                     if(!thomasBD.CheckExistsUnit(quantityMembers.getUnitID())){
                      	res.setErrorValue("NotFound");
                          res.setStatus("Error"); 
                          return res;                	
                      }
                   //role based control
                     if(!roleBasedControl(quantityMembers.getAgentID(), quantityMembers.getUnitID()))	
                     {	res.setErrorValue("Not-Allowed");
                  		res.setStatus("Error"); 
                  		return res;
                  	}
                     quantity=BigInteger.valueOf(thomasBD.GetQuantityMember(quantityMembers.getUnitID(),quantityMembers.getRoleID()));
                     res.setQuantity(quantity);
                     return res;
                     } 
          		private boolean roleBasedControl(String agentID, String unitID) {
        			if(unitID.equalsIgnoreCase("virtual")) return true;
        			if(!thomasBD.CheckExistsAgent(agentID)) return false;
        			String parentUnitID=thomasBD.GetParentUnitID(unitID);
        			if(thomasBD.CheckAgentPlaysRoleInUnit(parentUnitID, agentID)) return true;
        			else return false;
        		}
     
    }
    