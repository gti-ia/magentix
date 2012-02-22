package es.upv.dsic.gti_ia.organization;

import java.util.HashMap;

import java.util.Map.Entry;

public class ServiceTools {
	
	ResponseParser rp = new ResponseParser();
	
	public String buildServiceContent(String serviceName, HashMap<String,String> inputs)
	{
		String resultXML = "";
		
		resultXML += "<serviceInput>\n";
		resultXML += "<serviceName>"+serviceName+"</serviceName>\n";
		resultXML += "<inputs>\n";
		for (Entry<String,String> e: inputs.entrySet()) {
		    resultXML += "<"+e.getKey()+">"+e.getValue()+"</"+e.getKey()+">\n";
		}
		resultXML += "</inputs>\n";
		resultXML += "</serviceInput>\n";
		return resultXML;
		
	}
	
	
	public String extractServiceContent(String xml, HashMap<String, String> outputs)
	{
		
		
		rp.parseResponse(xml);
	
		String serviceName= rp.getServiceName();
		
		HashMap<String,String> outputAux = rp.getKeyAndValueList();
		
		for (Entry<String,String> e: outputAux.entrySet()) {
		    
		    
		    outputs.put(e.getKey(), e.getValue());
		}
		
		return serviceName;
	}

}
