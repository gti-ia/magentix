
    /**
     * RegisterRoleSkeleton.java
     *
     * This file was auto-generated from WSDL
     * by the Apache Axis2 version: 1.2 Apr 27, 2007 (04:35:37 IST)
     */
    package wtp;

import java.util.List;

import persistence.DataBaseInterface;
    /**
     *  RegisterRoleSkeleton java skeleton for the axisService
     */

    public class RegisterRoleSkeleton{
    	persistence.DataBaseInterface thomasBD=new DataBaseInterface();
         
        /**
         * Auto generated method signature
         
         
                                     * @param registerRole
         */
        

                 public wtp.RegisterRoleResponse RegisterRole
                  (
                  wtp.RegisterRole registerRole
                  )
            {
                //Todo fill this with the necessary business logic
                wtp.RegisterRoleResponse res=new RegisterRoleResponse();
                res.setErrorValue("");
                res.setStatus("Ok");
                if(registerRole.getAccessibility()==""){
                	registerRole.setAccessibility("EXTERNAL");
                }
                if(registerRole.getVisibility()==""){
                	registerRole.setVisibility("PUBLIC");
                }
                if(registerRole.getPosition()==""){
                	registerRole.setPosition("MEMBER");
                }
                if(registerRole.getInheritance()==""){
                	registerRole.setInheritance("MEMBER");
                }
                if(registerRole.getRoleID()=="" || registerRole.getUnitID()==""){
                	res.setErrorValue("Invalid");
                    res.setStatus("Error");
                    return res;
                }
                if(!registerRole.getAccessibility().equalsIgnoreCase("EXTERNAL") && !registerRole.getAccessibility().equalsIgnoreCase("INTERNAL")){
                	res.setErrorValue("Invalid");
                    res.setStatus("Error"); 
                    return res;
                }
                if(!registerRole.getVisibility().equalsIgnoreCase("PUBLIC") && !registerRole.getVisibility().equalsIgnoreCase("PRIVATE")){
                	res.setErrorValue("Invalid");
                    res.setStatus("Error");
                    return res;
                }
                if(!registerRole.getPosition().equalsIgnoreCase("MEMBER") && !registerRole.getPosition().equalsIgnoreCase("SUBORDINATE") 
                		&& !registerRole.getPosition().equalsIgnoreCase("SUPERVISOR")){
                	res.setErrorValue("Invalid");
                    res.setStatus("Error"); 
                    return res;
                }
                
                if(thomasBD.CheckExistsRoleInUnit(registerRole.getRoleID(),registerRole.getUnitID())){
                	res.setErrorValue("Duplicate");
                    res.setStatus("Error"); 
                    return res;                	
                }
                if(!thomasBD.CheckExistsUnit(registerRole.getUnitID())){
                	res.setErrorValue("Invalid");
                    res.setStatus("Error"); 
                    return res;                	
                }
                if(!thomasBD.CheckExistsRole(registerRole.getInheritance())){
                	res.setErrorValue("Invalid");
                    res.setStatus("Error"); 
                    return res;                	
                }
                //role based control
                if(!roleBasedControl(registerRole.getAgentID(),registerRole.getUnitID()))	
                {	res.setErrorValue("Not-Allowed");
             		res.setStatus("Error"); 
             		return res;
             	}
                if(!thomasBD.AddNewRole(registerRole.getRoleID(),registerRole.getUnitID(),registerRole.getVisibility(),registerRole.getAccessibility(),registerRole.getInheritance(),registerRole.getPosition())){
                	res.setErrorValue("Invalid");
                    res.setStatus("Error"); 
                    return res; 
                }
                return res;
        }

		private boolean roleBasedControl(String agentID, String unitID) {
			if(unitID.equalsIgnoreCase("virtual")) return false;
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
    