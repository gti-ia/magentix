package newSFTests;

import java.util.ArrayList;

import es.upv.dsic.gti_ia.architecture.QueueAgent;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.organization.SFProxy;

public class TesterAgentSearch extends QueueAgent{
	
	SFProxy sfProxy=new SFProxy(this);
	
	public TesterAgentSearch(AgentID aid) throws Exception {
		super(aid);
		
	}
	
	
	protected void execute() {
		
//		String res1=incorrectParamTest1();
//		System.out.println("incorrectParamTest1:\n"+res1);
		
//		String res2=incorrectParamTest2();
//		System.out.println("incorrectParamTest2:\n"+res2);
//		
		String resApp1=appropiateParamsTest1();
		System.out.println("appropiateParamsTest1:\n"+resApp1);
		
		String resApp2=appropiateParamsTest2();
		System.out.println("appropiateParamsTest2:\n"+resApp2);
		
		String resApp3=appropiateParamsTest3();
		System.out.println("appropiateParamsTest3:\n"+resApp3);
		
		String resApp4=appropiateParamsTest4();
		System.out.println("appropiateParamsTest4:\n"+resApp4);
		
		String resApp5=appropiateParamsTest5();
		System.out.println("appropiateParamsTest5:\n"+resApp5);
		
		String resApp6=appropiateParamsTest6();
		System.out.println("appropiateParamsTest6:\n"+resApp6);
		
		String resApp7=appropiateParamsTest7();
		System.out.println("appropiateParamsTest7:\n"+resApp7);
		
		String resApp8=appropiateParamsTest8();
		System.out.println("appropiateParamsTest8:\n"+resApp8);
		
		String resApp9=appropiateParamsTest9();
		System.out.println("appropiateParamsTest9:\n"+resApp9);
		
		String resApp10=appropiateParamsTest10();
		System.out.println("appropiateParamsTest10:\n"+resApp10);
		
		String resApp11=appropiateParamsTest11();
		System.out.println("appropiateParamsTest11:\n"+resApp11);
		
		String resApp12=appropiateParamsTest12();
		System.out.println("appropiateParamsTest12:\n"+resApp12);
		
		String resApp13=appropiateParamsTest13();
		System.out.println("appropiateParamsTest13:\n"+resApp13);
		
	}
	
	/**
	 * Incorrect inputs, empty outputs and keywords. The searchService method is called with a 
	 * string which not represents an input data type.
	 * @return
	 */
	String incorrectParamTest1(){
		ArrayList<String> inputs = new ArrayList<String>();
		ArrayList<String> outputs = new ArrayList<String>();
		ArrayList<String> keywords = new ArrayList<String>();
		inputs.add("Notype");
		return sfProxy.searchService(inputs,outputs,keywords);
	}

	/**
	 * Incorrect outputs, empty inputs and keywords. The getService method is called with a string
	 * which not represents an output data type.
	 * @return
	 */
	String incorrectParamTest2(){
		ArrayList <String> inputs = new ArrayList <String>();
		ArrayList <String> outputs = new ArrayList <String>();
		ArrayList <String> keywords = new ArrayList <String>();
		outputs.add("Notype");
		return sfProxy.searchService(inputs,outputs,keywords);
	}
	
	/**
	 * Search for a service providing only one input parameter of type double.
	 * @return
	 */
	String appropiateParamsTest1(){
		ArrayList <String> inputs = new ArrayList <String>();
		ArrayList <String> outputs = new ArrayList <String>();
		ArrayList <String> keywords = new ArrayList <String>();
		inputs.add("\"http://www.w3.org/2001/XMLSchema#double\"^^xsd:anyURI");
		return sfProxy.searchService(inputs,outputs,keywords);
	}
	
	/**
	 *  Search for a service providing two input parameters of type double.
	 * @return
	 */
	String appropiateParamsTest2(){
		ArrayList <String> inputs = new ArrayList <String>();
		ArrayList <String> outputs = new ArrayList <String>();
		ArrayList <String> keywords = new ArrayList <String>();
		inputs.add("\"http://www.w3.org/2001/XMLSchema#double\"^^xsd:anyURI");
		inputs.add("\"http://www.w3.org/2001/XMLSchema#double\"^^xsd:anyURI");
		return sfProxy.searchService(inputs,outputs,keywords);
	}
	
	/**
	 * Search for a service providing three input parameters of type double.
	 * @return
	 */
	String appropiateParamsTest3(){
		ArrayList <String> inputs = new ArrayList <String>();
		ArrayList <String> outputs = new ArrayList <String>();
		ArrayList <String> keywords = new ArrayList <String>();
		inputs.add("\"http://www.w3.org/2001/XMLSchema#double\"^^xsd:anyURI");
		inputs.add("\"http://www.w3.org/2001/XMLSchema#double\"^^xsd:anyURI");
		inputs.add("\"http://www.w3.org/2001/XMLSchema#double\"^^xsd:anyURI");
		return sfProxy.searchService(inputs,outputs,keywords);
	}
	
	/**
	 * Search for a service providing only one output parameter of type double.
	 * @return
	 */
	String appropiateParamsTest4(){
		ArrayList <String> inputs = new ArrayList <String>();
		ArrayList <String> outputs = new ArrayList <String>();
		ArrayList <String> keywords = new ArrayList <String>();
		outputs.add("\"http://www.w3.org/2001/XMLSchema#double\"^^xsd:anyURI");
		return sfProxy.searchService(inputs,outputs,keywords);
	}
	
	/**
	 * Search for a service providing two output parameters of type double.
	 * @return
	 */
	String appropiateParamsTest5(){
		ArrayList <String> inputs = new ArrayList <String>();
		ArrayList <String> outputs = new ArrayList <String>();
		ArrayList <String> keywords = new ArrayList <String>();
		outputs.add("\"http://www.w3.org/2001/XMLSchema#double\"^^xsd:anyURI");
		outputs.add("\"http://www.w3.org/2001/XMLSchema#double\"^^xsd:anyURI");
		return sfProxy.searchService(inputs,outputs,keywords);
	}
	
	/**
	 * Search for a service providing only one output parameter of type boolean.
	 * @return
	 */
	String appropiateParamsTest6(){
		ArrayList <String> inputs = new ArrayList <String>();
		ArrayList <String> outputs = new ArrayList <String>();
		ArrayList <String> keywords = new ArrayList <String>();
		outputs.add("\"http://www.w3.org/2001/XMLSchema#boolean\"^^xsd:anyURI");		
		return sfProxy.searchService(inputs,outputs,keywords);
	}

	/**
	 * Search for a service providing only one output parameter of type string.
	 * @return
	 */
	String appropiateParamsTest7(){
		ArrayList <String> inputs = new ArrayList <String>();
		ArrayList <String> outputs = new ArrayList <String>();
		ArrayList <String> keywords = new ArrayList <String>();
		outputs.add("\"http://www.w3.org/2001/XMLSchema#string\"^^xsd:anyURI");		
		return sfProxy.searchService(inputs,outputs,keywords);
	}
	
	/**
	 * Search for a service providing only one keyword.
	 * @return
	 */
	String appropiateParamsTest8(){
		ArrayList <String> inputs = new ArrayList <String>();
		ArrayList <String> outputs = new ArrayList <String>();
		ArrayList <String> keywords = new ArrayList <String>();
		keywords.add("product");		
		return sfProxy.searchService(inputs,outputs,keywords);
	}
	
	/**
	 * Search for a service providing only one keyword.
	 * @return
	 */
	String appropiateParamsTest9(){
		ArrayList <String> inputs = new ArrayList <String>();
		ArrayList <String> outputs = new ArrayList <String>();
		ArrayList <String> keywords = new ArrayList <String>();
		keywords.add("returns the product");		
		return sfProxy.searchService(inputs,outputs,keywords);
	}
	
	/**
	 * Search for a service providing two words as keywords.
	 * @return
	 */
	String appropiateParamsTest10(){
		ArrayList <String> inputs = new ArrayList <String>();
		ArrayList <String> outputs = new ArrayList <String>();
		ArrayList <String> keywords = new ArrayList <String>();
		keywords.add("product");
		keywords.add("numbers");
		return sfProxy.searchService(inputs,outputs,keywords);
	}
	
	/**
	 * Search for a service providing two words as keywords.
	 * @return
	 */
	String appropiateParamsTest11(){
		ArrayList <String> inputs = new ArrayList <String>();
		ArrayList <String> outputs = new ArrayList <String>();
		ArrayList <String> keywords = new ArrayList <String>();
		inputs.add("\"http://www.w3.org/2001/XMLSchema#double\"^^xsd:anyURI");
		outputs.add("\"http://www.w3.org/2001/XMLSchema#boolean\"^^xsd:anyURI");
		return sfProxy.searchService(inputs,outputs,keywords);
	}
	
	/**
	 * Search for a service providing one input parameters of type double, one output 
	 * parameter of type boolean and one keyword.
	 * @return
	 */
	String appropiateParamsTest12(){
		ArrayList <String> inputs = new ArrayList <String>();
		ArrayList <String> outputs = new ArrayList <String>();
		ArrayList <String> keywords = new ArrayList <String>();
		inputs.add("\"http://www.w3.org/2001/XMLSchema#double\"^^xsd:anyURI");
		outputs.add("\"http://www.w3.org/2001/XMLSchema#boolean\"^^xsd:anyURI");
		keywords.add("positive");
		return sfProxy.searchService(inputs,outputs,keywords);
	}
	
	/**
	 * Search for a service providing two input parameters of type double, one 
	 * output parameter of type double and phrase as keyword.
	 * @return
	 */
	String appropiateParamsTest13(){
		ArrayList <String> inputs = new ArrayList <String>();
		ArrayList <String> outputs = new ArrayList <String>();
		ArrayList <String> keywords = new ArrayList <String>();
		inputs.add("\"http://www.w3.org/2001/XMLSchema#double\"^^xsd:anyURI");
		inputs.add("\"http://www.w3.org/2001/XMLSchema#double\"^^xsd:anyURI");
		outputs.add("\"http://www.w3.org/2001/XMLSchema#double\"^^xsd:anyURI");
		keywords.add("returns the addition");
		return sfProxy.searchService(inputs,outputs,keywords);
	}
	

}
