package testSFServices;

import persistence.SFinterface;

public class RegisterUTest {

	SFinterface sf=new SFinterface();
	
	String incorrectParamTest1(){
		
		return sf.registerService("dsic-upv-es"); 
	}
	
	String incorrectParamTest2(){
		
		return sf.registerService("http://gti-ia.dsic.upv.es");
	}
	
	String incorrectParamTest3(){
		
		return sf.registerService("http://127.0.0.1/services/1.1/calculateSunriseTime.owls");
	}
	
	String appropiateParamsTest1(){
		
		return sf.registerService("http://127.0.0.1/services/Test/SumArray.owls");
	}
	
	String appropiateParamsTest2(){
		
		return sf.registerService("http://127.0.0.1/services/Test/Product.owls");
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		

	}

}
