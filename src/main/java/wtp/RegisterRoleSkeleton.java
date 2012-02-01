
/**
 * RegisterRoleSkeleton.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.4  Built on : Apr 26, 2008 (06:24:30 EDT)
 */
    package wtp;

import persistence.OMSInterface;

    /**
     *  RegisterRoleSkeleton java skeleton for the axisService
     */
    public class RegisterRoleSkeleton{
        
    	public static final Boolean		DEBUG		= true;
    	private static OMSInterface omsInterface = new OMSInterface();
        /**
         * Auto generated method signature
         * 
                                     * @param registerRole
         */
        
                 public wtp.RegisterRoleResponse RegisterRole
                  (
                  wtp.RegisterRole registerRole
                  )
            {
                		// Todo fill this with the necessary business logic
             		wtp.RegisterRoleResponse res = new RegisterRoleResponse();
             		String result = "";
             		if (DEBUG)
             		{
             			System.out.println("RegisterRole :");
             			System.out.println("***AgentID..." + registerRole.getAgentID());
             			System.out.println("***UnitID..." + registerRole.getUnitID());
             			System.out.println("***RoleID..." + registerRole.getRoleID());
             			System.out.println("***Accessibility..."
             					+ registerRole.getAccessibility());
             			System.out.println("***Position..." + registerRole.getPosition());
             			System.out.println("***Visibility..."
             					+ registerRole.getVisibility());
             				
             		}
             	
             			if (registerRole.getRoleID().equals("null"))
             				result =omsInterface.registerRole(null, registerRole.getUnitID(), registerRole.getAccessibility(), registerRole.getVisibility(), registerRole.getPosition(), registerRole.getAgentID());
             			else if (registerRole.getUnitID().equals("null"))
             				result =omsInterface.registerRole(registerRole.getRoleID(), null, registerRole.getAccessibility(), registerRole.getVisibility(), registerRole.getPosition(), registerRole.getAgentID());
             			else if (registerRole.getAccessibility().equals("null"))
             				result =omsInterface.registerRole(registerRole.getRoleID(), registerRole.getUnitID(), null, registerRole.getVisibility(), registerRole.getPosition(), registerRole.getAgentID());
             			else if (registerRole.getVisibility().equals("null"))
             				result =omsInterface.registerRole(registerRole.getRoleID(), registerRole.getUnitID(), registerRole.getAccessibility(), null, registerRole.getPosition(), registerRole.getAgentID());
             			else if (registerRole.getPosition().equals("null"))
             				result =omsInterface.registerRole(registerRole.getRoleID(), registerRole.getUnitID(), registerRole.getAccessibility(), registerRole.getVisibility(), null, registerRole.getAgentID());
             			else
             				result =omsInterface.registerRole(registerRole.getRoleID(), registerRole.getUnitID(), registerRole.getAccessibility(), registerRole.getVisibility(), registerRole.getPosition(), registerRole.getAgentID());
             			res.setResult(result);
             			return res;
             		
        }
     
    }
    