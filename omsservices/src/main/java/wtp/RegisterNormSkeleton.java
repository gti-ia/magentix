
    /**
     * RegisterNormSkeleton.java
     *
     * This file was auto-generated from WSDL
     * by the Apache Axis2 version: 1.2 Apr 27, 2007 (04:35:37 IST)
     */
    package wtp;

import persistence.DataBaseInterface;
import NormativeManagement.NormativeChecker;
import NormativeManagement.NormativeManager;
    /**
     *  RegisterNormSkeleton java skeleton for the axisService
     */

    public class RegisterNormSkeleton{
    	private static NormativeChecker normCheck = new NormativeChecker();
    	private static DataBaseInterface thomasBD = new DataBaseInterface();
    	private static NormativeManager normManager = new NormativeManager();        
    	public static final Boolean DEBUG=true; 
        /**
         * Auto generated method signature
         
         
                                     * @param registerNorm
         */
        

                 public wtp.RegisterNormResponse RegisterNorm
                  (
                  wtp.RegisterNorm registerNorm
                  )
            {		wtp.RegisterNormResponse res = new RegisterNormResponse();
		            if (DEBUG) {
		      			System.out.println("RegisterNorm :");
		      			System.out.println("***AgentID..."+ registerNorm.getAgentID());
		      			System.out.println("***NormID..."+ registerNorm.getNormID());
		      			System.out.println("***NormContent..."+ registerNorm.getNormContent());
		                	  
		         	 }
            
            
            		res.setErrorValue("");
		    		res.setStatus("Ok");
		    		if (registerNorm.getNormID() == "" || registerNorm.getNormContent()=="") {
		    			res.setErrorValue("Invalid");
		    			res.setStatus("Error");
		    			return res;
		    		}
		    		registerNorm.setNormContent(registerNorm.getNormContent().replace('_', ' '));
		    		Integer normID = addNewNorm(registerNorm.getNormID(), registerNorm
		    				.getNormContent());
		    		if (normID == -1) {
		    			res.setErrorValue("Invalid");
		    			res.setStatus("Error");
		    			return res;
		    		}
		
		    		try {
		    			String[] typeNorm = normCheck.analyzeNorm(registerNorm.getNormContent());
		    			if (normManager.ManageNorm(typeNorm, normID))
		    				return res;
		    		} catch (Exception e) {
		    			res.setErrorValue("Invalid");
		    			res.setStatus("Error");
		    			e.printStackTrace();
		    			/* Eliminamos la norma que habiamos insertado */
		    			thomasBD.DeleteNorm(registerNorm.getNormID());
		    			return res;
		    		}
		
		    		/*
		    		 * if(!addNewNorm(registerNorm.getNormID(),registerNorm.getAddressedRoleID(),)){
		    		 * res.setErrorValue("Invalid"); res.setStatus("Error"); return res; }
		    		 */
		    		res.setErrorValue("NotReconigsed");
		    		res.setStatus("Ok");
		    		return res;
    	}

    	private Integer addNewNorm(String normID, String normContent) {

    		// Check norm identifiers
    		if (thomasBD.CheckExistsNorm(normID)) {
    			System.out.println("1");
    			return -1;
    		}/*
    		if ((sanction != "") && !thomasBD.CheckExistsNorm(sanction)) {
    			System.out.println("2: " + sanction);
    			return -1;
    		}
    		if ((reward != "") && !thomasBD.CheckExistsNorm(reward)) {
    			System.out.println("3");
    			return -1;
    		}
    		// Check role identifiers
    		if (!thomasBD.CheckExistsRole(addressedRoleID)) {
    			System.out.println("4");
    			return -1;
    		}
    		if ((issuerRoleID != "") && !thomasBD.CheckExistsRole(issuerRoleID)) {
    			System.out.println("5");
    			return -1;
    		}
    		if ((promoterRoleID != "") && !thomasBD.CheckExistsRole(promoterRoleID)) {
    			System.out.println("6");
    			return -1;
    		}
    		if ((defenderRoleID != "") && !thomasBD.CheckExistsRole(defenderRoleID)) {
    			System.out.println("7");
    			return -1;
    		}*/
    		// Check service Identifier
    		/*
    		 * if(!thomasBD.CheckExistsService(serviceName))
    		 * {System.out.println("8");return -1;} FALTA		 */
    		// Register new norm in data base
    		if (thomasBD.AddNewNorm(normID,normContent))

    			return thomasBD.GetNormID(normID);
    		System.out.println("9");
    		return -1;
    	}
    }