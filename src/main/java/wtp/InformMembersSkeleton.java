
    /**
     * InformMembersSkeleton.java
     *
     * This file was auto-generated from WSDL
     * by the Apache Axis2 version: 1.2 Apr 27, 2007 (04:35:37 IST)
     */
    package wtp;

import java.util.List;

import persistence.DataBaseInterface;
    /**
     *  InformMembersSkeleton java skeleton for the axisService
     */

    public class InformMembersSkeleton{
    	persistence.DataBaseInterface thomasBD=new DataBaseInterface();
        public static final Boolean DEBUG=true; 
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
                     
                	 if (DEBUG) {
                			System.out.println("InformMembers :");
                			System.out.println("***AgentID..."+ informMembers.getAgentID());
                			System.out.println("***UnitID()..."+ informMembers.getUnitID());
                			System.out.println("***RoleID()..."+ informMembers.getRoleID());
                          	  
                	 }
                	 
                	 res.setErrorValue("");
                     res.setStatus("Ok");
                     res.setEntityRoleList("");

                     if(informMembers.getUnitID()==""){
                     	res.setErrorValue("Invalid");
                         res.setStatus("Error");
                         return res;
                     }
                     
                     if(!thomasBD.CheckExistsUnit(informMembers.getUnitID())){
                      	res.setErrorValue("NotFound");
                          res.setStatus("Error"); 
                          return res;                	
                      }
                     //role based control
                     if(!roleBasedControl(informMembers.getAgentID(),informMembers.getUnitID()))	
                     {	res.setErrorValue("Not-Allowed");
                  		res.setStatus("Error"); 
                  		return res;
                  	}
                     res.setEntityRoleList(thomasBD.GetEntityRoleList(informMembers.getUnitID(),informMembers.getRoleID()).toString());
                     return res;
                     } 
                 
         		private boolean roleBasedControl(String agentID, String unitID) {
        			if(unitID.equalsIgnoreCase("virtual")) return true;
        			if(!thomasBD.CheckExistsAgent(agentID)) return false;
        			String unitType=thomasBD.GetUnitType(unitID);
        			if(unitType.equalsIgnoreCase("flat")) return true;
        			if(unitType.equalsIgnoreCase("team")) {
        				if(thomasBD.CheckAgentPlaysRoleInUnit(unitID, agentID)) return true;
        				else return false;
        			}
        			List<String> positions;
        			try {
        				positions = thomasBD.GetAgentPosition(agentID, unitID);
        				for(int i=0;i<positions.size();i++)
        					if(positions.get(i).equalsIgnoreCase("supervisor"))
        						return true;
        			} catch (Exception e) {
        			}
        			return false;
        		}
    }
    