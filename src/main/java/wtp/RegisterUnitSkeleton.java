
    /**
     * RegisterUnitSkeleton.java
     *
     * This file was auto-generated from WSDL
     * by the Apache Axis2 version: 1.2 Apr 27, 2007 (04:35:37 IST)
     */
    package wtp;

import java.util.List;

import persistence.DataBaseInterface;
    /**
     *  RegisterUnitSkeleton java skeleton for the axisService
     */
    public class RegisterUnitSkeleton{
        
         
        /**
         * Auto generated method signature
         
         
                                     * @param registerUnit
         */
    	persistence.DataBaseInterface thomasBD=new DataBaseInterface();
    	public static final Boolean DEBUG=true;
                 public wtp.RegisterUnitResponse RegisterUnit
                  (
                  wtp.RegisterUnit registerUnit
                  )
            {
                	 wtp.RegisterUnitResponse res=new RegisterUnitResponse();
                	 if (DEBUG) {
               			System.out.println("RegisterUnit :");
               			System.out.println("***AgentID..."+ registerUnit.getAgentID());
               			System.out.println("***UnitID..."+ registerUnit.getUnitID());
               			System.out.println("***ParentUnitID..."+ registerUnit.getParentUnitID());
               			System.out.println("***Goal..."+ registerUnit.getGoal());
               			System.out.println("***Type..."+ registerUnit.getType());
                         	  
                  	 }
                	 
                	 
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
                     if(thomasBD.CheckExistsUnit(registerUnit.getUnitID())){
                     	res.setErrorValue("Duplicate");
                         res.setStatus("Error"); 
                         return res;                	
                     }
                     //role based control
                     if(!roleBasedControl(registerUnit.getAgentID(),registerUnit.getParentUnitID()))	
                     {	res.setErrorValue("Not-Allowed");
                  		res.setStatus("Error"); 
                  		return res;
                  	}
                     if(!thomasBD.AddNewUnit(registerUnit.getUnitID(), registerUnit.getType(), registerUnit.getGoal(), registerUnit.getParentUnitID(),registerUnit.getAgentID()))
                     {	res.setErrorValue("Invalid");
                     	res.setStatus("Error"); 
                     	return res;
                     	}
                     return res;
            }

		private boolean roleBasedControl(String agentID, String parentUnitID) {
			if(!thomasBD.CheckExistsAgent(agentID)) return false;
			if(parentUnitID.equalsIgnoreCase("virtual")) return true;
			String parentUnitType=thomasBD.GetUnitType(parentUnitID);
			if(parentUnitType.equalsIgnoreCase("flat")) return true;
			if(parentUnitType.equalsIgnoreCase("team")) {
				if(thomasBD.CheckAgentPlaysRoleInUnit(parentUnitType, agentID)) return true;
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
    