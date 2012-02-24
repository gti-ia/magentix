package es.upv.dsic.gti_ia.organization;




import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


import javax.xml.rpc.Call;
import javax.xml.rpc.Service;
import javax.xml.namespace.QName;
import javax.xml.rpc.ServiceFactory;
import javax.xml.rpc.ParameterMode;



public class ServiceClient {

    // Definido por <wsdl:service name="..."> en el documento wsdl
    private static String qnameService;
    // Definido por <wsdl:portType name="..."> en el documento wsdl
    private static String qnamePort;
    // Definido por <wsdl:operation name="..."> en el documento wsdl
    private static String operationName = "";

    // Se deja por defecto, funciona bién de esta manera.
    private static String BODY_NAMESPACE_VALUE = "urn:Foo";
    
    //Propiedades por defecto.
    private static String ENCODING_STYLE_PROPERTY = "javax.xml.rpc.encodingstyle.namespace.uri";
    private static String NS_XSD = "http://www.w3.org/2001/XMLSchema";
    private static String URI_ENCODING = "http://schemas.xmlsoap.org/soap/encoding/";
    private Oracle oracle;

    public HashMap<String, Object> invoke(String wsdlURL, List<String> params) {

		try {
		   
		    oracle = new Oracle();
	
		    oracle.parseWSDL(wsdlURL);
		    
		    qnameService = oracle.getWSDLNameService();
	
		    qnamePort = oracle.getNamePort();
	
		    operationName = oracle.getOperation();
		    
		    
	
		    ServiceFactory factory = ServiceFactory.newInstance();
	
		    Service service = factory.createService(new QName(qnameService));
	
		    QName port = new QName(qnamePort);
	
		    Call call = service.createCall(port);
		    call.setTargetEndpointAddress(wsdlURL);
	
		    
		    call.setProperty(Call.SOAPACTION_USE_PROPERTY, new Boolean(true));
		    call.setProperty(Call.SOAPACTION_URI_PROPERTY, "");
		    call.setProperty(ENCODING_STYLE_PROPERTY, URI_ENCODING);
	
		
	
		    call.setReturnType(new QName(NS_XSD, oracle.getOutputsTypes().get(oracle.getOutputsTypes().size()-1)));
	
		    call.setOperationName(new QName(BODY_NAMESPACE_VALUE, operationName));
	
		    // Debemos montar con el getInputTyps y los argumentos de entrada
		    // del agente
//		    Iterator<Map.Entry<String,String>> itr = oracle.getElements().entrySet().iterator();
	
		    
		//    System.out.println("Input: "+ oracle.getProcessInputs());
		    
		    
		    
//		    while (itr.hasNext()) {
//			
//				Map.Entry<String,String> e = (Map.Entry<String,String>) itr.next();
//				
//				
//				call.addParameter(e.getKey().toString(),
//					new QName(NS_XSD, e.getValue().toString()), ParameterMode.IN);
//				
//		    }
		    
		    HashMap<String,String> inputsAndTypes=oracle.getWsdlInputParamsAndTypes();
		    for (Entry<String,String> e: inputsAndTypes.entrySet()) {
			    
		    	call.addParameter(e.getKey().toString(),
						new QName(NS_XSD, e.getValue().toString()), ParameterMode.IN);
			}
		    
		    Object firstResult = call.invoke(params.toArray());
	
		  //Build result
		    HashMap<String, Object> results=new HashMap<String, Object>();
		    
		    results.put(oracle.getWSDLOutputs().get(0), firstResult);
		    for (int i=1; i< oracle.getWSDLOutputs().size();i++){
		    	results.put(oracle.getWSDLOutputs().get(i), call.getOutputValues().get(i-1));
			}
		    
		    return results;
		} catch (Exception ex) {
		    ex.printStackTrace();
		    return null;
		}
    }

}

