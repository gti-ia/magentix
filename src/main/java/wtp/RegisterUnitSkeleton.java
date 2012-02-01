
/**
 * RegisterUnitSkeleton.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.4  Built on : Apr 26, 2008 (06:24:30 EDT)
 */
    package wtp;

import persistence.OMSInterface;

    /**
     *  RegisterUnitSkeleton java skeleton for the axisService
     */
    public class RegisterUnitSkeleton{
        
    	public static final Boolean		DEBUG		= true;
    	private static OMSInterface omsInterface = new OMSInterface();
        /**
         * Auto generated method signature
         * 
                                     * @param registerUnit
         */
        
                 public wtp.RegisterUnitResponse RegisterUnit
                  (
                  wtp.RegisterUnit registerUnit
                  )
            {
                		wtp.RegisterUnitResponse res = new RegisterUnitResponse();
                		String result = "";
                		if (DEBUG)
                		{
                			System.out.println("RegisterUnit :");
                			System.out.println("***AgentID..." + registerUnit.getAgentID());
                			System.out.println("***UnitID..." + registerUnit.getUnitID());
                			System.out.println("***ParentUnitID..."
                					+ registerUnit.getParentUnitID());
                			System.out.println("***CreatorName..." + registerUnit.getCreatorName());
                			System.out.println("***Type..." + registerUnit.getType());
                			
                		}
                		
                
                			if (registerUnit.getParentUnitID().equals(""))
                			{
                				
                				result =omsInterface.registerUnit(registerUnit.getUnitID(), registerUnit.getType(), registerUnit.getAgentID(), registerUnit.getCreatorName());
                			}
                			else
                			{
                				
                				result =omsInterface.registerUnit(registerUnit.getUnitID(), registerUnit.getType(), registerUnit.getParentUnitID(), registerUnit.getAgentID(), registerUnit.getCreatorName());
                			}
                	
                			res.setResult(result);
                			return res;
               
        }
     
    }
    