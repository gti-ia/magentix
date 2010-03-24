package es.upv.dsic.gti_ia.cAgents;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import es.upv.dsic.gti_ia.core.ACLMessage;

;

public class MessageTemplate extends ACLMessage {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final Pattern pattern = Pattern
			.compile("((\\s*\\(*)*[a-zA-Z0-9]+((\\s*\\)\\s*)+|\\s+)(AND|OR))+\\s+[a-zA-Z0-9]+(\\s*\\)*)*");	
	
	Logger logger = Logger.getLogger(CProcessor.class);
	
	public MessageTemplate(){
		super();
	}
	
	public MessageTemplate(int performative, String expr){
		super(performative);	
		if(correctExpression(expr)){
			
		}
	}
	
	private boolean correctExpression(String expr){
		Matcher matcher = pattern.matcher(expr);
				
		if (matcher.find()) {
			System.out.println(matcher.group());
			if (matcher.end() - matcher.start() == expr.length()){
				int i = 0;
				int parentesis = 0;
				while(i < expr.length()){
					if(expr.charAt(i) == '(') parentesis++;
					if(expr.charAt(i) == ')') parentesis--;
					i++;
				}
				if(parentesis != 0){
					this.logger.error("[Wrong parenthisation in expression: " + expr + "]");
					return false;
				}
				else
					return true;
			}				
		}
		this.logger.error("[Wrong expression: " + expr + "]");
		return false;
	}
}
