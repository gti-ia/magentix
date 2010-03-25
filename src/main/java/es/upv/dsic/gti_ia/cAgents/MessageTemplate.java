package es.upv.dsic.gti_ia.cAgents;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import es.upv.dsic.gti_ia.core.ACLMessage;

public class MessageTemplate extends ACLMessage {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final Pattern pattern = Pattern
			.compile("((\\s*\\(*)*[a-zA-Z0-9]+((\\s*\\)\\s*)+|\\s+)(AND|OR))+\\s+[a-zA-Z0-9]+(\\s*\\)*)*");
	private final Pattern pattern2 = Pattern.compile("\\s*[a-zA-Z0-9]+\\s*");

	Logger logger = Logger.getLogger(CProcessor.class);
	private String expr;
	private boolean singleElement = false;

	class Node {
		public static final int VALUE = 0;
		public static final int OPARENTHESIS = 1;
		public static final int CPARENTHESIS = 2;
		public static final int AND = 3;
		public static final int OR = 4;
		int type;
		Node left;
		Node right;
		String headerName;

		public Node(int type) {
			this.type = type;
		}
				
		public Node(int type, String headerName){
			this.type = type;
			this.headerName = headerName;
		}
	}
	
	Node root;

	public MessageTemplate() {
		super();
	}

	public MessageTemplate(int performative, String expr) {
		super(performative);
		if(correctExpression(expr)){
			this.expr = expr;
			root = this.createBinaryTree(this.createNodeList());
		}
	}
	
	public boolean compareHeaders(ACLMessage msg){
		if(!singleElement)
			return this.evaluateTree(root, msg);
		else
			return this.getHeaderValue(expr.trim()) == msg.getHeaderValue(expr.trim());
		
	}

	private boolean correctExpression(String expr) {
		Matcher matcher = pattern.matcher(expr);

		if (matcher.find()) {
			if (matcher.end() - matcher.start() == expr.length()) {
				int i = 0;
				int parentesis = 0;
				while (i < expr.length()) {
					if (expr.charAt(i) == '(')
						parentesis++;
					if (expr.charAt(i) == ')')
						parentesis--;
					i++;
				}
				if (parentesis != 0) {
					this.logger.error("[Wrong parenthisation in expression: "
							+ expr + "]");
					return false;
				}
				else
					return true;
			}
		}
		else{
			matcher = pattern2.matcher(expr);
			if (matcher.find()) {
				if (matcher.end() - matcher.start() == expr.length()){
					this.singleElement = true;
					return true;
				}
			}
		}
		this.logger.error("[Wrong expression: " + expr + "]");
		return false;
	}
	
	private List<Node> createNodeList(){
		List<Node> nodos = new ArrayList<Node>();
		int i =0;
		int index;
		int indexPar;
		int indexEsp;
		String headerName;
		while(i < expr.length()){
			if(expr.charAt(i) == '('){
				nodos.add(new Node(Node.OPARENTHESIS));
				i++;
			}
			else if(expr.charAt(i) == ')'){
				nodos.add(new Node(Node.CPARENTHESIS));
				i++;
			}
			else if(expr.charAt(i) == 'A'){ //pot ser un AND o altra cosa
				if((expr.substring(i, i + 3).equals("AND")) && expr.charAt(i + 3) == ' '){ //es un AND
					nodos.add(new Node(Node.AND));
					i = i+3;
				}
				else{ //es qualsevol cadena
					indexEsp = expr.indexOf(' ',i);
					indexPar = expr.indexOf(')',i);
					if(indexEsp <= -1 && indexPar <= -1)//final de frase
						index = expr.length();
					else if(indexEsp <= -1)
						index = indexPar;
					else if(indexPar <= -1)
						index = indexEsp;
					else if(indexEsp <= indexPar)//hi ha abans un espai que un parentesi
						index = indexEsp;
					else if(indexPar <= indexEsp)//hi ha abans un ) que un espai
						index = indexPar;
					else
						index = indexEsp;
					headerName = expr.substring(i, index);
					nodos.add(new Node(Node.VALUE, headerName));
					i = index;
				}
			}
			else if(expr.charAt(i) == 'O'){ //pot ser un OR o altra cosa
				if((expr.substring(i, i + 2).equals("OR")) && expr.charAt(i + 2) == ' '){ //es un OR
					nodos.add(new Node(Node.OR));
					i = i + 2;
				}
				else{ //es qualsevol cadena
					indexEsp = expr.indexOf(' ',i);
					indexPar = expr.indexOf(')',i);
					if(indexEsp <= -1 && indexPar <= -1)//final de frase
						index = expr.length();
					else if(indexEsp <= -1)
						index = indexPar;
					else if(indexPar <= -1)
						index = indexEsp;
					else if(indexEsp <= indexPar)//hi ha abans un espai que un parentesi
						index = indexEsp;
					else if(indexPar <= indexEsp)//hi ha abans un ) que un espai
						index = indexPar;
					else
						index = indexEsp;
					headerName = expr.substring(i, index);
					nodos.add(new Node(Node.VALUE, headerName));
					i = index;
				}
			}
			else if(expr.charAt(i) != ' '){ //qualsevol paraula
				indexEsp = expr.indexOf(' ',i);
				indexPar = expr.indexOf(')',i);
				if(indexEsp <= -1 && indexPar <= -1)//final de frase
					index = expr.length();
				else if(indexEsp <= -1)
					index = indexPar;
				else if(indexPar <= -1)
					index = indexEsp;
				else if(indexEsp <= indexPar)//hi ha abans un espai que un parentesi
					index = indexEsp;
				else if(indexPar <= indexEsp)//hi ha abans un ) que un espai
					index = indexPar;
				else
					index = indexEsp;
				headerName = expr.substring(i, index);
				nodos.add(new Node(Node.VALUE, headerName));
				i = index;
			}
			else
				i++;
		}
		return nodos;		
	}
	
	private Node createBinaryTree(List<Node> nodos){
		int minPrior = -1;
		int index = -1;
		int cont = 0;
		while(cont == 0){
			for(int i=0; i< nodos.size(); i++){			
				if(nodos.get(i).type == Node.OPARENTHESIS){ //no tenim en conter el que hi ha dins del parentesis
					int parentesis = 1;
					int j = i + 1;
					while(parentesis > 0){
						if(nodos.get(j).type == Node.OPARENTHESIS)
							parentesis++;
						if(nodos.get(j).type == Node.CPARENTHESIS)
							parentesis--;
						j++;
					}
					i = j;
				}
				if(i < nodos.size()){
					if(nodos.get(i).type > minPrior){
						minPrior = nodos.get(i).type;
						index = i;
						cont++;
					}
				}
			}
			if(cont == 0){ //la expresió era del tipus (...)
				nodos = nodos.subList(1, nodos.size()-1);
			}
		}
		Node root = new Node(nodos.get(index).type);
		if(root.type != Node.VALUE){
			root.left = createBinaryTree(nodos.subList(0, index));
			root.right = createBinaryTree(nodos.subList(index+1, nodos.size()));
		}
		else
			root.headerName = nodos.get(index).headerName;
		return root;
	}
	
	private boolean evaluateTree(Node root, ACLMessage msg){
		if(root.type == Node.AND){
			return evaluateTree(root.left, msg) && evaluateTree(root.right, msg);
		}
		else if(root.type == Node.OR){
			return evaluateTree(root.left, msg) || evaluateTree(root.right, msg);
		}
		else if(root.type == Node.VALUE){
			return this.getHeaderValue(root.headerName) == msg.getHeaderValue(root.headerName);
		}
		else return false;
	}
}
