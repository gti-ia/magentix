
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
    	persistence.DataBaseInterface thomasBD=new DataBaseInterface();
        public static final Boolean DEBUG=true;
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
                     
                	 if (DEBUG) {
             			System.out.println("DeregisterUnit :");
             			System.out.println("***AgentID..."+ deregisterUnit.getAgentID());
             			System.out.println("*** UnitID()..."+ deregisterUnit.getUnitID());
                	 }
                	 
                	 res.setErrorValue("");
                     res.setStatus("Ok");

                     if(deregisterUnit.getUnitID()=="" || deregisterUnit.getUnitID().equalsIgnoreCase("virtual")){
                     	res.setErrorValue("Invalid");
                         res.setStatus("Error");
                         return res;
                     }
                     
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
                     //role based control
                     if(!roleBasedControl(deregisterUnit.getAgentID(),deregisterUnit.getUnitID()))	
                     {	res.setErrorValue("Not-Allowed");
                  		res.setStatus("Error"); 
                  		return res;
                  	}
                     if(!thomasBD.DeleteUnit(deregisterUnit.getUnitID())){
                     	res.setErrorValue("Invalid");
                         res.setStatus("Error"); 
                         return res; 
                     }
                     return res;
                }
                 
          		private boolean roleBasedControl(String agentID, String unitID) {
        			if(unitID.equalsIgnoreCase("virtual")) return false;
        			if(!thomasBD.CheckExistsAgent(agentID)) return false;
        			String parentUnitID=thomasBD.GetParentUnitID(unitID);
        			String parentUnitType=thomasBD.GetUnitType(parentUnitID);
        			if(parentUnitType.equalsIgnoreCase("flat")) return true;
        			if(parentUnitType.equalsIgnoreCase("team")) {
        				if(thomasBD.CheckAgentPlaysRoleInUnit(unitID, agentID)) return true;
        				else return false;
        			}
        			List<String> positions;
        			try {
        				positions = thomasBD.GetAgentPosition(agentID, parentUnitID);
        				for(int i=0;i<positions.size();i++)
        					if(positions.get(i).equalsIgnoreCase("supervisor"))
        						return true;
        			} catch (Exception e) {
        			}
        			return false;
        		}     
    }
    