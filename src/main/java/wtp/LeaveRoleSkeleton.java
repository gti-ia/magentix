
/**
 * LeaveRoleSkeleton.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.4  Built on : Apr 26, 2008 (06:24:30 EDT)
 */
    package wtp;

import persistence.OMSInterface;

    /**
     *  LeaveRoleSkeleton java skeleton for the axisService
     */
    public class LeaveRoleSkeleton{
    	public static final Boolean	DEBUG	= true;
    	private static OMSInterface omsInterface = new OMSInterface();
         
        /**
         * Auto generated method signature
         * 
                                     * @param leaveRole
         */
        
                 public wtp.LeaveRoleResponse LeaveRole
                  (
                  wtp.LeaveRole leaveRole
                  )
            {
                		wtp.LeaveRoleResponse res = new LeaveRoleResponse();
                		String result = "";
                		if (DEBUG)
                		{
                			System.out.println("LeaveRole :");
                			System.out.println("***AgentID..." + leaveRole.getAgentID());
                			System.out.println("***UnitID()..." + leaveRole.getUnitID());
                			System.out.println("***RoleID()..." + leaveRole.getRoleID());
                			
                		}
                		
                			if (leaveRole.getRoleID().equals("null"))
                				result =omsInterface.leaveRole(null, leaveRole.getUnitID(),leaveRole.getAgentID());
                			else if (leaveRole.getUnitID().equals("null"))
                				result =omsInterface.leaveRole(leaveRole.getRoleID(), null,leaveRole.getAgentID());
                			else
                				result =omsInterface.leaveRole(leaveRole.getRoleID(), leaveRole.getUnitID(),leaveRole.getAgentID());
                			res.setResult(result);
                			return res;
             
        }
     
    }
    