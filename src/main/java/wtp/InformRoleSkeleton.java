
/**
 * InformRoleSkeleton.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.4  Built on : Apr 26, 2008 (06:24:30 EDT)
 */
    package wtp;

import java.sql.SQLException;

import persistence.OMSInterface;
import persistence.THOMASException;
import wtp.InformRoleResponse;
    /**
     *  InformRoleSkeleton java skeleton for the axisService
     */
    public class InformRoleSkeleton{
        
    	private static OMSInterface omsInterface = new OMSInterface();
        /**
         * Auto generated method signature
         * 
                                     * @param informRole
         */
        
                 public wtp.InformRoleResponse InformRole
                  (
                  wtp.InformRole informRole
                  )
            {
                wtp.InformRoleResponse res = new InformRoleResponse();
        		String result = "";
        		res.setProfileList("");
        		res.setStatus("Ok");
        		res.setErrorValue("");
        		
        		try{
        			if (informRole.getRoleName().equals("null"))
        				result =omsInterface.informRole(null,informRole.getUnitName(),informRole.getAgentID());
        			else if (informRole.getUnitName().equals("null"))
        				result =omsInterface.informRole(informRole.getRoleName(),null,informRole.getAgentID());
        				else
        					result =omsInterface.informRole(informRole.getRoleName(),informRole.getUnitName(),informRole.getAgentID());
        			res.setProfileList(result);
        			res.setStatus("Ok");
        			return res;
        		}catch(THOMASException e)
        		{
        			res.setStatus("Error");
        	
        			res.setErrorValue(e.getContent());
        		
        			return res;
        		}
        		catch(SQLException e)
        		{
        			res.setStatus("Error");
        			res.setErrorValue(e.getMessage());
        			return res;
        		}
        }
     
    }
    