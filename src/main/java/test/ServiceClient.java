package test;




import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


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

    public ArrayList<String> invoke(String URLProcess, List<String> params) {

		// Lo sacaremos del parser (sin el wsdl)
		String URLString = "";
	
		try {
		   
		    oracle = new Oracle();
	
		    //Necesitamos algunos valores definidos en el process como la ruta del wsdl.
		    oracle.setURLProcess(URLProcess);
	
		    
		    URLString = oracle.getWSDL();
	
	
		    qnameService = oracle.getWSDLNameService();
	
		    qnamePort = oracle.getNamePort();
	
		    operationName = oracle.getOperation();
		    
		    
	
		    ServiceFactory factory = ServiceFactory.newInstance();
	
		    Service service = factory.createService(new QName(qnameService));
	
		    QName port = new QName(qnamePort);
	
		    Call call = service.createCall(port);
		    call.setTargetEndpointAddress(URLString);
	
		    
		    call.setProperty(Call.SOAPACTION_USE_PROPERTY, new Boolean(true));
		    call.setProperty(Call.SOAPACTION_URI_PROPERTY, "");
		    call.setProperty(ENCODING_STYLE_PROPERTY, URI_ENCODING);
	
		
	
		    call.setReturnType(new QName(NS_XSD, oracle.getOutputsTypes().get(oracle.getOutputsTypes().size()-1)));
	
		    call.setOperationName(new QName(BODY_NAMESPACE_VALUE, operationName));
	
		    // Debemos montar con el getInputTyps y los argumentos de entrada
		    // del agente
		    Iterator<Map.Entry<String,String>> itr = oracle.getElements().entrySet().iterator();
	
		    
		    System.out.println("Input: "+ oracle.getProcessInputs());
		    
		    
		    int j = 0;
		    while (itr.hasNext()) {
			
				Map.Entry<String,String> e = (Map.Entry<String,String>) itr.next();
				
				
				call.addParameter(e.getKey().toString(),
					new QName(NS_XSD, e.getValue().toString()), ParameterMode.IN);
				j++;
		    }
		    
		    String result = (String) call.invoke(params.toArray());
	
		    
		    ArrayList<String> results=new ArrayList<String>();
		    //Montamos el resultado
		    
		    results.add(oracle.getProcessOutputs().get(0)+ "="+ result);
		    for (int i=1; i< oracle.getProcessOutputs().size();i++){
				results.add(oracle.getProcessOutputs().get(i)+ "="+call.getOutputValues().get(i-1));
		    }
		    if (call.getOutputValues().size() != 0)
		    	return results;
		    else
		    	return results;
		} catch (Exception ex) {
		    ex.printStackTrace();
		    return null;
		}
    }

}
