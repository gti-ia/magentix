package sfTest;

import java.util.ArrayList;
import java.util.Iterator;

import es.upv.dsic.gti_ia.architecture.QueueAgent;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.organization.SFProxy;
import es.upv.dsic.gti_ia.organization.THOMASException;

public class TesterAgentSearch extends QueueAgent{
	
	SFProxy sfProxy=new SFProxy(this);
	
	public TesterAgentSearch(AgentID aid) throws Exception {
		super(aid);
		
	}
	
	
	protected void execute() {
		
		ArrayList<ArrayList<String>> res1=incorrectParamTest1();
		System.out.println("incorrectParamTest1:\n"+res1);
		
		ArrayList<ArrayList<String>> res2=incorrectParamTest2();
		System.out.println("incorrectParamTest2:\n"+res2);
		
		
		
		ArrayList<ArrayList<String>> resApp=appropiateParamsTest1();
		Iterator<ArrayList<String>> iterItems=resApp.iterator();
		String resStr="";
		while(iterItems.hasNext()){
			ArrayList<String> item=iterItems.next();
			Iterator<String> iterStr=item.iterator();
			while(iterStr.hasNext()){
				resStr+=iterStr.next()+" ";
			}
			resStr+="\n";
		}
		System.out.println("appropiateParamsTest1:\n"+resStr);
		
		resApp=appropiateParamsTest2();
		iterItems=resApp.iterator();
		resStr="";
		while(iterItems.hasNext()){
			ArrayList<String> item=iterItems.next();
			Iterator<String> iterStr=item.iterator();
			while(iterStr.hasNext()){
				resStr+=iterStr.next()+" ";
			}
			resStr+="\n";
		}
		System.out.println("appropiateParamsTest2:\n"+resStr);
		
		resApp=appropiateParamsTest3();
		iterItems=resApp.iterator();
		resStr="";
		while(iterItems.hasNext()){
			ArrayList<String> item=iterItems.next();
			Iterator<String> iterStr=item.iterator();
			while(iterStr.hasNext()){
				resStr+=iterStr.next()+" ";
			}
			resStr+="\n";
		}
		System.out.println("appropiateParamsTest3:\n"+resStr);
		
		resApp=appropiateParamsTest4();
		iterItems=resApp.iterator();
		resStr="";
		while(iterItems.hasNext()){
			ArrayList<String> item=iterItems.next();
			Iterator<String> iterStr=item.iterator();
			while(iterStr.hasNext()){
				resStr+=iterStr.next()+" ";
			}
			resStr+="\n";
		}
		System.out.println("appropiateParamsTest4:\n"+resStr);
		
		resApp=appropiateParamsTest5();
		iterItems=resApp.iterator();
		resStr="";
		while(iterItems.hasNext()){
			ArrayList<String> item=iterItems.next();
			Iterator<String> iterStr=item.iterator();
			while(iterStr.hasNext()){
				resStr+=iterStr.next()+" ";
			}
			resStr+="\n";
		}
		System.out.println("appropiateParamsTest5:\n"+resStr);
		
		resApp=appropiateParamsTest6();
		iterItems=resApp.iterator();
		resStr="";
		while(iterItems.hasNext()){
			ArrayList<String> item=iterItems.next();
			Iterator<String> iterStr=item.iterator();
			while(iterStr.hasNext()){
				resStr+=iterStr.next()+" ";
			}
			resStr+="\n";
		}
		System.out.println("appropiateParamsTest6:\n"+resStr);
		
		resApp=appropiateParamsTest7();
		iterItems=resApp.iterator();
		resStr="";
		while(iterItems.hasNext()){
			ArrayList<String> item=iterItems.next();
			Iterator<String> iterStr=item.iterator();
			while(iterStr.hasNext()){
				resStr+=iterStr.next()+" ";
			}
			resStr+="\n";
		}
		System.out.println("appropiateParamsTest7:\n"+resStr);
		
		resApp=appropiateParamsTest8();
		iterItems=resApp.iterator();
		resStr="";
		while(iterItems.hasNext()){
			ArrayList<String> item=iterItems.next();
			Iterator<String> iterStr=item.iterator();
			while(iterStr.hasNext()){
				resStr+=iterStr.next()+" ";
			}
			resStr+="\n";
		}
		System.out.println("appropiateParamsTest8:\n"+resStr);
		
		resApp=appropiateParamsTest9();
		iterItems=resApp.iterator();
		resStr="";
		while(iterItems.hasNext()){
			ArrayList<String> item=iterItems.next();
			Iterator<String> iterStr=item.iterator();
			while(iterStr.hasNext()){
				resStr+=iterStr.next()+" ";
			}
			resStr+="\n";
		}
		System.out.println("appropiateParamsTest9:\n"+resStr);
		
		resApp=appropiateParamsTest10();
		iterItems=resApp.iterator();
		resStr="";
		while(iterItems.hasNext()){
			ArrayList<String> item=iterItems.next();
			Iterator<String> iterStr=item.iterator();
			while(iterStr.hasNext()){
				resStr+=iterStr.next()+" ";
			}
			resStr+="\n";
		}
		System.out.println("appropiateParamsTest10:\n"+resStr);
		
		resApp=appropiateParamsTest11();
		iterItems=resApp.iterator();
		resStr="";
		while(iterItems.hasNext()){
			ArrayList<String> item=iterItems.next();
			Iterator<String> iterStr=item.iterator();
			while(iterStr.hasNext()){
				resStr+=iterStr.next()+" ";
			}
			resStr+="\n";
		}
		System.out.println("appropiateParamsTest11:\n"+resStr);
		
		resApp=appropiateParamsTest12();
		iterItems=resApp.iterator();
		resStr="";
		while(iterItems.hasNext()){
			ArrayList<String> item=iterItems.next();
			Iterator<String> iterStr=item.iterator();
			while(iterStr.hasNext()){
				resStr+=iterStr.next()+" ";
			}
			resStr+="\n";
		}
		System.out.println("appropiateParamsTest12:\n"+resStr);
		
		resApp=appropiateParamsTest13();
		iterItems=resApp.iterator();
		resStr="";
		while(iterItems.hasNext()){
			ArrayList<String> item=iterItems.next();
			Iterator<String> iterStr=item.iterator();
			while(iterStr.hasNext()){
				resStr+=iterStr.next()+" ";
			}
			resStr+="\n";
		}
		System.out.println("appropiateParamsTest13:\n"+resStr);
	
		
	}
	
	/**
	 * Incorrect inputs, empty outputs and keywords. The searchService method is called with a 
	 * string which not represents an input data type.
	 * @return
	 */
	ArrayList<ArrayList<String>> incorrectParamTest1(){
		ArrayList<String> inputs = new ArrayList<String>();
		ArrayList<String> outputs = new ArrayList<String>();
		ArrayList<String> keywords = new ArrayList<String>();
		inputs.add("Notype");
		try {
			return sfProxy.searchService(inputs,outputs,keywords);
		} catch (THOMASException e) {
			
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Incorrect outputs, empty inputs and keywords. The getService method is called with a string
	 * which not represents an output data type.
	 * @return
	 */
	ArrayList<ArrayList<String>> incorrectParamTest2(){
		ArrayList <String> inputs = new ArrayList <String>();
		ArrayList <String> outputs = new ArrayList <String>();
		ArrayList <String> keywords = new ArrayList <String>();
		outputs.add("Notype");
		try {
			return sfProxy.searchService(inputs,outputs,keywords);
		} catch (THOMASException e) {
			
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Search for a service providing only one input parameter of type double.
	 * @return
	 */
	ArrayList<ArrayList<String>> appropiateParamsTest1(){
		ArrayList <String> inputs = new ArrayList <String>();
		ArrayList <String> outputs = new ArrayList <String>();
		ArrayList <String> keywords = new ArrayList <String>();
		inputs.add("\"http://www.w3.org/2001/XMLSchema#double\"^^xsd:anyURI");
		try {
			return sfProxy.searchService(inputs,outputs,keywords);
		} catch (THOMASException e) {
			
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 *  Search for a service providing two input parameters of type double.
	 * @return
	 */
	ArrayList<ArrayList<String>> appropiateParamsTest2(){
		ArrayList <String> inputs = new ArrayList <String>();
		ArrayList <String> outputs = new ArrayList <String>();
		ArrayList <String> keywords = new ArrayList <String>();
		inputs.add("\"http://www.w3.org/2001/XMLSchema#double\"^^xsd:anyURI");
		inputs.add("\"http://www.w3.org/2001/XMLSchema#double\"^^xsd:anyURI");
		try {
			return sfProxy.searchService(inputs,outputs,keywords);
		} catch (THOMASException e) {
			
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Search for a service providing three input parameters of type double.
	 * @return
	 */
	ArrayList<ArrayList<String>> appropiateParamsTest3(){
		ArrayList <String> inputs = new ArrayList <String>();
		ArrayList <String> outputs = new ArrayList <String>();
		ArrayList <String> keywords = new ArrayList <String>();
		inputs.add("\"http://www.w3.org/2001/XMLSchema#double\"^^xsd:anyURI");
		inputs.add("\"http://www.w3.org/2001/XMLSchema#double\"^^xsd:anyURI");
		inputs.add("\"http://www.w3.org/2001/XMLSchema#double\"^^xsd:anyURI");
		try {
			return sfProxy.searchService(inputs,outputs,keywords);
		} catch (THOMASException e) {
			
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Search for a service providing only one output parameter of type double.
	 * @return
	 */
	ArrayList<ArrayList<String>> appropiateParamsTest4(){
		ArrayList <String> inputs = new ArrayList <String>();
		ArrayList <String> outputs = new ArrayList <String>();
		ArrayList <String> keywords = new ArrayList <String>();
		outputs.add("\"http://www.w3.org/2001/XMLSchema#double\"^^xsd:anyURI");
		try {
			return sfProxy.searchService(inputs,outputs,keywords);
		} catch (THOMASException e) {
			
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Search for a service providing two output parameters of type double.
	 * @return
	 */
	ArrayList<ArrayList<String>> appropiateParamsTest5(){
		ArrayList <String> inputs = new ArrayList <String>();
		ArrayList <String> outputs = new ArrayList <String>();
		ArrayList <String> keywords = new ArrayList <String>();
		outputs.add("\"http://www.w3.org/2001/XMLSchema#double\"^^xsd:anyURI");
		outputs.add("\"http://www.w3.org/2001/XMLSchema#double\"^^xsd:anyURI");
		try {
			return sfProxy.searchService(inputs,outputs,keywords);
		} catch (THOMASException e) {
			
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Search for a service providing only one output parameter of type boolean.
	 * @return
	 */
	ArrayList<ArrayList<String>> appropiateParamsTest6(){
		ArrayList <String> inputs = new ArrayList <String>();
		ArrayList <String> outputs = new ArrayList <String>();
		ArrayList <String> keywords = new ArrayList <String>();
		outputs.add("\"http://www.w3.org/2001/XMLSchema#boolean\"^^xsd:anyURI");		
		try {
			return sfProxy.searchService(inputs,outputs,keywords);
		} catch (THOMASException e) {
			
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Search for a service providing only one output parameter of type string.
	 * @return
	 */
	ArrayList<ArrayList<String>> appropiateParamsTest7(){
		ArrayList <String> inputs = new ArrayList <String>();
		ArrayList <String> outputs = new ArrayList <String>();
		ArrayList <String> keywords = new ArrayList <String>();
		outputs.add("\"http://www.w3.org/2001/XMLSchema#string\"^^xsd:anyURI");		
		try {
			return sfProxy.searchService(inputs,outputs,keywords);
		} catch (THOMASException e) {
			
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Search for a service providing only one keyword.
	 * @return
	 */
	ArrayList<ArrayList<String>> appropiateParamsTest8(){
		ArrayList <String> inputs = new ArrayList <String>();
		ArrayList <String> outputs = new ArrayList <String>();
		ArrayList <String> keywords = new ArrayList <String>();
		keywords.add("product");		
		try {
			return sfProxy.searchService(inputs,outputs,keywords);
		} catch (THOMASException e) {
			
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Search for a service providing only one keyword.
	 * @return
	 */
	ArrayList<ArrayList<String>> appropiateParamsTest9(){
		ArrayList <String> inputs = new ArrayList <String>();
		ArrayList <String> outputs = new ArrayList <String>();
		ArrayList <String> keywords = new ArrayList <String>();
		keywords.add("returns the product");		
		try {
			return sfProxy.searchService(inputs,outputs,keywords);
		} catch (THOMASException e) {
			
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Search for a service providing two words as keywords.
	 * @return
	 */
	ArrayList<ArrayList<String>> appropiateParamsTest10(){
		ArrayList <String> inputs = new ArrayList <String>();
		ArrayList <String> outputs = new ArrayList <String>();
		ArrayList <String> keywords = new ArrayList <String>();
		keywords.add("product");
		keywords.add("numbers");
		try {
			return sfProxy.searchService(inputs,outputs,keywords);
		} catch (THOMASException e) {
			
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Search for a service providing two words as keywords.
	 * @return
	 */
	ArrayList<ArrayList<String>> appropiateParamsTest11(){
		ArrayList <String> inputs = new ArrayList <String>();
		ArrayList <String> outputs = new ArrayList <String>();
		ArrayList <String> keywords = new ArrayList <String>();
		inputs.add("\"http://www.w3.org/2001/XMLSchema#double\"^^xsd:anyURI");
		outputs.add("\"http://www.w3.org/2001/XMLSchema#boolean\"^^xsd:anyURI");
		try {
			return sfProxy.searchService(inputs,outputs,keywords);
		} catch (THOMASException e) {
			
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Search for a service providing one input parameters of type double, one output 
	 * parameter of type boolean and one keyword.
	 * @return
	 */
	ArrayList<ArrayList<String>> appropiateParamsTest12(){
		ArrayList <String> inputs = new ArrayList <String>();
		ArrayList <String> outputs = new ArrayList <String>();
		ArrayList <String> keywords = new ArrayList <String>();
		inputs.add("\"http://www.w3.org/2001/XMLSchema#double\"^^xsd:anyURI");
		outputs.add("\"http://www.w3.org/2001/XMLSchema#boolean\"^^xsd:anyURI");
		keywords.add("positive");
		try {
			return sfProxy.searchService(inputs,outputs,keywords);
		} catch (THOMASException e) {
			
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Search for a service providing two input parameters of type double, one 
	 * output parameter of type double and phrase as keyword.
	 * @return
	 */
	ArrayList<ArrayList<String>> appropiateParamsTest13(){
		ArrayList <String> inputs = new ArrayList <String>();
		ArrayList <String> outputs = new ArrayList <String>();
		ArrayList <String> keywords = new ArrayList <String>();
		inputs.add("\"http://www.w3.org/2001/XMLSchema#double\"^^xsd:anyURI");
		inputs.add("\"http://www.w3.org/2001/XMLSchema#double\"^^xsd:anyURI");
		outputs.add("\"http://www.w3.org/2001/XMLSchema#double\"^^xsd:anyURI");
		keywords.add("returns the addition");
		try {
			return sfProxy.searchService(inputs,outputs,keywords);
		} catch (THOMASException e) {
			
			e.printStackTrace();
			return null;
		}
	}
	

}
