package NormativeManagement;

import java.io.InputStream;
import java.io.StringReader;

import normativeLanguageParser.NormativeLanguageParser;
import normativeLanguageParser.SimpleNode;
 


public class NormativeChecker {
	private InputStream normStream=null;
	private SimpleNode parserResult=null;
	/*	public NormativeChecker(){
	}
	public NormativeChecker(String norm){
		  try{
			  normStream = new java.io.ByteArrayInputStream(norm.getBytes("UTF-8"));
		  }catch(Exception ex){
		  }
	}
	public void parseNorm(String norm) throws Exception{
	    try{
	    	normStream = new java.io.ByteArrayInputStream(norm.getBytes("UTF-8"));
	    	NormativeLanguageParser interpreter = new NormativeLanguageParser(normStream);
	    	parserResult=interpreter.Norm();
			  
		    }
		    catch(Exception e){
		    	System.out.println("Sintaxis Incorrecta");
		    	throw e;
			  }	    
	}*/
	public String[] analyzeNorm(String norm) throws Exception{
		try{
	    	NormativeLanguageParser interpreter = new NormativeLanguageParser(new StringReader( norm ));
	    	parserResult= interpreter.Norm();
			 return AnalyzeNormTree(parserResult);
		}
	    catch(Exception e){
	    	System.out.println("Sintaxis Incorrecta");
	    	throw e;
		  }	 
	}

	  public static void main(String args[])  {
		// String xml = "forbidden roleuno request acquirerole MESSAGE(content (unit ?u, role 'roledos')) IF RESULT(InformQuantity(?unit, ?role))>5";
		//  String xml = "forbidden roleuno request acquirerole MESSAGE(content (unit ?u, role 'roledos')) IF Quantity(role 'roledos')>'5' ";
		  String xml = "permitted roleuno request serviceID";
		  InputStream in=null;
		  try{
			  in = new java.io.ByteArrayInputStream(xml.getBytes("UTF-8"));
		  }catch(Exception ex){
		  }
		  NormativeLanguageParser interpreter = new NormativeLanguageParser(in);
	    try{
	    SimpleNode result=interpreter.Norm();
	    interpreter.PrintParserResult(" ", result);
	    String []res=null;
	    AnalyzeNormTree(result);
		  
	    }
	    catch(Exception e){
	    	System.out.println("Sintaxis Incorrecta");
	    	e.printStackTrace();
		  }	    
}
	  public static String[] AnalyzeNormTree(SimpleNode n){
		  SimpleNode deonticNode=(SimpleNode)n.jjtGetChild(0);
		  SimpleNode entityNode=(SimpleNode)n.jjtGetChild(1);
		  SimpleNode actionNode=(SimpleNode)n.jjtGetChild(2);
		  SimpleNode ifNode=null,temporalSituationNode=null,sanctionNode=null,rewardNode=null;
		  for(int i=3;i<n.jjtGetNumChildren();i++){
				if(n.jjtGetChild(i).toString().equalsIgnoreCase("if"))
					ifNode=(SimpleNode)n.jjtGetChild(i);
				if(n.jjtGetChild(i).toString().equalsIgnoreCase("TemporalSituation"))
					temporalSituationNode=(SimpleNode)n.jjtGetChild(i);
				if(n.jjtGetChild(i).toString().equalsIgnoreCase("Sanction"))
					sanctionNode=(SimpleNode)n.jjtGetChild(i);
				if(n.jjtGetChild(i).toString().equalsIgnoreCase("Reward"))
					rewardNode=(SimpleNode)n.jjtGetChild(i);
		  }
		 try{
			 String []res=new String[3];
			 res=checkIncompatiblityNorm(deonticNode,entityNode,actionNode,ifNode,temporalSituationNode,sanctionNode,rewardNode);
			 return res;
			  }
		 catch(Exception e){ 
		 }
		 try{
			 String []res=new String[4];
			 res=checkMaxCardinalityNorm(deonticNode,entityNode,actionNode,ifNode,temporalSituationNode,sanctionNode,rewardNode);
			 return res;
		 }
		 catch(Exception e){ }
		 /*
		 try{
			 String []res=new String[4];
			 res=checkSimpleRequestNorm(deonticNode,entityNode,actionNode,ifNode,temporalSituationNode,sanctionNode,rewardNode);
			 return res;
		 }
		 catch(Exception e){ 
		 }*/
		 String []res=new String[1];
		 res[0]="Unknown";
		 return res;
			  
	  }
	private static String[] checkSimpleRequestNorm(SimpleNode deonticNode,
			SimpleNode entityNode, SimpleNode actionNode, SimpleNode ifNode,
			SimpleNode temporalSituationNode, SimpleNode sanctionNode,
			SimpleNode rewardNode) throws Exception {
		String []simpleRequest={"","","",""};
		simpleRequest[0]="SimpleRequestNorm";
		Exception e = null;
		if(ifNode!=null||temporalSituationNode!=null||sanctionNode!=null||rewardNode!=null)
			throw e;
		if(!deonticNode.jjtGetValue().toString().equalsIgnoreCase("forbidden")&&!deonticNode.jjtGetValue().toString().equalsIgnoreCase("permitted"))
			throw e;
		simpleRequest[1]=deonticNode.jjtGetValue().toString();
		if(!entityNode.jjtGetChild(0).toString().equalsIgnoreCase("ID"))
			throw e;
		simpleRequest[2]=((SimpleNode)entityNode.jjtGetChild(0)).jjtGetValue().toString();
		if(!actionNode.jjtGetChild(0).toString().equalsIgnoreCase("request"))
			throw e;
		SimpleNode requestNode=(SimpleNode)actionNode.jjtGetChild(0);
		SimpleNode serviceNode=(SimpleNode)requestNode.jjtGetChild(0);
		if(requestNode.jjtGetNumChildren()>1)
			throw e;
		simpleRequest[3]=serviceNode.jjtGetValue().toString();
		
		return simpleRequest;
	}
	
	private static String[] checkIncompatiblityNorm(SimpleNode deonticNode,
			SimpleNode entityNode, SimpleNode actionNode, SimpleNode ifNode,
			SimpleNode temporalSituationNode, SimpleNode sanctionNode,
			SimpleNode rewardNode) throws Exception {
		String []incompatibleRoles={"","",""};
		incompatibleRoles[0]="IncompatiblityNorm";
		Exception e = null;
		if(ifNode!=null||temporalSituationNode!=null||sanctionNode!=null||rewardNode!=null)
			throw e;
		if(!deonticNode.jjtGetValue().toString().equalsIgnoreCase("forbidden"))
			throw e;
		if(!entityNode.jjtGetChild(0).toString().equalsIgnoreCase("ID"))
			throw e;
		incompatibleRoles[1]=((SimpleNode)entityNode.jjtGetChild(0)).jjtGetValue().toString();
		String roleID=((SimpleNode)entityNode.jjtGetChild(0)).jjtGetValue().toString();
		if(!actionNode.jjtGetChild(0).toString().equalsIgnoreCase("request"))
			throw e;
		SimpleNode requestNode=(SimpleNode)actionNode.jjtGetChild(0);
		SimpleNode serviceNode=(SimpleNode)requestNode.jjtGetChild(0);
		SimpleNode messageNode=null;
			if(requestNode.jjtGetNumChildren()>1)
				messageNode=(SimpleNode)requestNode.jjtGetChild(1);
		if(!serviceNode.jjtGetValue().toString().equalsIgnoreCase("acquireRole"))
			throw e;
		if(messageNode==null || !messageNode.toString().equalsIgnoreCase("message"))
			throw e;
		SimpleNode messageInfo=(SimpleNode)messageNode.jjtGetChild(0);
		SimpleNode content=(SimpleNode)messageInfo.jjtGetChild(0);
		if(!content.toString().equalsIgnoreCase("content"))
			throw e;
		SimpleNode args=(SimpleNode)content.jjtGetChild(0);
		if(!args.toString().equalsIgnoreCase("args"))
			throw e;
		for(int i=0;i<args.jjtGetNumChildren();i++){
			SimpleNode variant=(SimpleNode)args.jjtGetChild(i);
			SimpleNode atomic=(SimpleNode)variant.jjtGetChild(0);
			if(!atomic.toString().equalsIgnoreCase("atomic"))
				throw e;
			SimpleNode param=(SimpleNode)atomic.jjtGetChild(0);
			if(!param.toString().equalsIgnoreCase("idVariable") && !param.toString().equalsIgnoreCase("idValue"))
				throw e;
			SimpleNode paramid=(SimpleNode)param.jjtGetChild(0);
			SimpleNode paramval=(SimpleNode)param.jjtGetChild(1);
			if(paramid.jjtGetValue().toString().equalsIgnoreCase("role") && paramval.toString().equalsIgnoreCase("variable"))
				throw e;
			if(paramid.jjtGetValue().toString().equalsIgnoreCase("role")||paramid.jjtGetValue().toString().equalsIgnoreCase("roleID"))
				incompatibleRoles[2]=paramval.jjtGetValue().toString().replace("\'", "");

		}
		return incompatibleRoles;
	}
	private static String[] checkMaxCardinalityNorm(SimpleNode deonticNode,
			SimpleNode entityNode, SimpleNode actionNode, SimpleNode ifNode,
			SimpleNode temporalSituationNode, SimpleNode sanctionNode,
			SimpleNode rewardNode) throws Exception {
		String []maxCardinalityRole={"","","",""};
		maxCardinalityRole[0]="MaxCardinalityNorm";
		Exception e = null;
		if(temporalSituationNode!=null||sanctionNode!=null||rewardNode!=null)
			throw e;
		if(!deonticNode.jjtGetValue().toString().equalsIgnoreCase("forbidden"))
			throw e;
		if(!entityNode.jjtGetChild(0).toString().equalsIgnoreCase("ID"))
			throw e;
		//Solo normas de cardinalidad generales (member) 
		if(!((SimpleNode)entityNode.jjtGetChild(0)).jjtGetValue().toString().equalsIgnoreCase("member"))
			throw e;
		maxCardinalityRole[1]=((SimpleNode)entityNode.jjtGetChild(0)).jjtGetValue().toString();
		String roleID=((SimpleNode)entityNode.jjtGetChild(0)).jjtGetValue().toString();
		if(!actionNode.jjtGetChild(0).toString().equalsIgnoreCase("request"))
			throw e;
		SimpleNode requestNode=(SimpleNode)actionNode.jjtGetChild(0);
		SimpleNode serviceNode=(SimpleNode)requestNode.jjtGetChild(0);
		SimpleNode messageNode=null;
			if(requestNode.jjtGetNumChildren()>1)
				messageNode=(SimpleNode)requestNode.jjtGetChild(1);
		if(!serviceNode.jjtGetValue().toString().equalsIgnoreCase("acquireRole"))
			throw e;
		if(messageNode==null || !messageNode.toString().equalsIgnoreCase("message"))
			throw e;
		SimpleNode messageInfo=(SimpleNode)messageNode.jjtGetChild(0);
		SimpleNode content=(SimpleNode)messageInfo.jjtGetChild(0);
		if(!content.toString().equalsIgnoreCase("content"))
			throw e;
		SimpleNode args=(SimpleNode)content.jjtGetChild(0);
		if(!args.toString().equalsIgnoreCase("args"))
			throw e;
		for(int i=0;i<args.jjtGetNumChildren();i++){
			SimpleNode variant=(SimpleNode)args.jjtGetChild(i);
			SimpleNode atomic=(SimpleNode)variant.jjtGetChild(0);
			if(!atomic.toString().equalsIgnoreCase("atomic"))
				throw e;
			SimpleNode param=(SimpleNode)atomic.jjtGetChild(0);
			if(!param.toString().equalsIgnoreCase("idVariable") && !param.toString().equalsIgnoreCase("idValue"))
				throw e;
			SimpleNode paramid=(SimpleNode)param.jjtGetChild(0);
			SimpleNode paramval=(SimpleNode)param.jjtGetChild(1);
			if(paramid.jjtGetValue().toString().equalsIgnoreCase("role") && paramval.toString().equalsIgnoreCase("variable"))
				throw e;
			if(paramid.jjtGetValue().toString().equalsIgnoreCase("role")){
				String aux=(paramval.jjtGetValue().toString().replace("\'", "").replace("\"", ""));
				if(aux.equalsIgnoreCase(maxCardinalityRole[0]))
					throw e;
				else maxCardinalityRole[2]=aux;
		}
		}
		SimpleNode conditionExpression=(SimpleNode)ifNode.jjtGetChild(0);
		if(!conditionExpression.jjtGetChild(0).toString().equalsIgnoreCase("Condition"))
				throw e;
		SimpleNode condition=(SimpleNode)conditionExpression.jjtGetChild(0);
		boolean suma=false;
		if(!condition.jjtGetValue().toString().contains("="))
			suma=true;
		String condSign=condition.jjtGetValue().toString();
		SimpleNode variantNode1=(SimpleNode)condition.jjtGetChild(0);
		SimpleNode variantNode2=(SimpleNode)condition.jjtGetChild(1);
		SimpleNode formulaNode=(SimpleNode)variantNode1.jjtGetChild(0);
		SimpleNode valueNode=(SimpleNode)variantNode2.jjtGetChild(0);
		if(!formulaNode.toString().equalsIgnoreCase("formula")){
			SimpleNode aux=formulaNode; formulaNode=valueNode;valueNode=aux;
			condSign=condSign.replace('<', 'a');
			condSign=condSign.replace('>', '<');
			condSign=condSign.replace('a', '>');}
		if(!condSign.contains(">"))
			throw e;
		if(!formulaNode.toString().equalsIgnoreCase("formula"))
			throw e;
		if(!formulaNode.jjtGetValue().toString().equalsIgnoreCase("quantity"))
			throw e;
		if(!valueNode.toString().equalsIgnoreCase("value"))
			throw e;
		int value=Integer.parseInt(valueNode.jjtGetValue().toString().replace("\'", "").replace("\"", ""));
		if(suma) value=value+1;
		maxCardinalityRole[3]=String.valueOf(value);
		
		args=(SimpleNode)formulaNode.jjtGetChild(0);
		if(!args.toString().equalsIgnoreCase("args"))
			throw e;
		for(int i = 0;i<args.jjtGetNumChildren();i++){
			SimpleNode variant = (SimpleNode)args.jjtGetChild(i);
			SimpleNode atomic=(SimpleNode)variant.jjtGetChild(0);
			if(!atomic.toString().equalsIgnoreCase("atomic"))
				throw e;
			SimpleNode param=(SimpleNode)atomic.jjtGetChild(0);
			if(!param.toString().equalsIgnoreCase("idVariable") && !param.toString().equalsIgnoreCase("idValue"))
				throw e;
			SimpleNode paramid=(SimpleNode)param.jjtGetChild(0);
			SimpleNode paramval=(SimpleNode)param.jjtGetChild(1);
			if(paramid.jjtGetValue().toString().equalsIgnoreCase("role") && paramval.toString().equalsIgnoreCase("variable"))
				throw e;
			if(paramid.jjtGetValue().toString().equalsIgnoreCase("role")){
				String aux=(paramval.jjtGetValue().toString().replace("\'", "").replace("\"", ""));
				if(!aux.equalsIgnoreCase(maxCardinalityRole[2]))
					throw e;
		}
	}
		return maxCardinalityRole;
	
		}
}
