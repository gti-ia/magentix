/**
 * GetProfileSkeleton.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.2 Apr 27, 2007 (04:35:37 IST)
 */
package wtp;

import java.util.*;
import java.io.*;

import persistence.DataBaseInterface;

/**
 * GetProcessSkeleton java skeleton for the axisService
 */
public class GetProcessSkeleton {
	

	public static final Boolean DEBUG = true;



	/**
	 * GetProfile
	 *
	 * @param getProcess contains the service ID
	 * @return response
	 * 	provider list: [service implementation id urlprocess, service implementation id urlproces, ... ]
	 * 	flag: 1: ok 0 :otherwise
	 * @throws
	 */
                 public wtp.GetProcessResponse GetProcess   (
                  wtp.GetProcess getProcess
                  )
            {
                	 GetProcessResponse response = new GetProcessResponse();
                	 if (DEBUG) {
             			System.out.println("GetProcess Service:");
             			System.out.println("***ServiceID... " + getProcess.getServiceID());
             			System.out.println("***AgentID... " + getProcess.getAgentID());
             		}
             		
             		persistence.DataBaseInterface thomasBD = new DataBaseInterface();
             		String processlist = thomasBD.GetServiceProcessFromProfile(getProcess.getServiceID());
             		if(!processlist.equals("")){
             			response.setProcessList(processlist);
             			response.set_return(1);
             		}
             		else{
             			response.setProcessList("[Error] The service id does not exist");
             			response.set_return(1);
             		}
             		return (response);
        }
     
    }
    