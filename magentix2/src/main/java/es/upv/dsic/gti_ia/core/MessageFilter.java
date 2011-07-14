package es.upv.dsic.gti_ia.core;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.log4j.Logger;
import es.upv.dsic.gti_ia.cAgents.CProcessor;
import es.upv.dsic.gti_ia.core.ACLMessage;

/**
 * Class used to represent a filter for messages. By using this filter the user will be able to
 * process only those messages they desire. 
 */
public class MessageFilter implements Cloneable{

	/**
	 * Author Ricard López Fogués
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final Pattern pattern = Pattern
			.compile("(\\s*(NOT\\s)*(\\s*\\(*)*[a-zA-Z0-9_-]+\\s*(=|!=)\\s*[a-zA-Z0-9_-]+((\\s*\\)\\s*)+|\\s+)(AND|OR))+\\s+[a-zA-Z0-9_-]+\\s*(=|!=)\\s*[a-zA-Z0-9_-]+(\\s*\\)*)*");
	private final Pattern pattern2 = Pattern.compile("\\s*(NOT\\s)*[a-zA-Z0-9_-]+\\s*(=|!=)\\s*[a-zA-Z0-9_-]+\\s*");

	Logger logger = Logger.getLogger(CProcessor.class);
	private String expr;
	
	/**
	 * Node in the tree of logic expressions (e.g. AND, EQUAL,..)
	 *
	 */
	private class Node {
		public static final int VALUE = 0;
		public static final int EQUAL = 1;
		public static final int NOTEQUAL = 2;
		public static final int OPARENTHESIS = 3;
		public static final int CPARENTHESIS = 4;
		public static final int NOT = 5;
		public static final int AND = 6;
		public static final int OR = 7;
		private int type;
		private Node left;
		private Node right;
		private String headerName;

		/**
		 * Creates a node with the specified type.
		 * @param type Should be one of the static int defined in this class
		 */
		public Node(int type) {
			this.type = type;
		}

		/**
		 * Creates a node with the specified type and the header name.
		 * @param type The type
		 * @param headerName The header name, to be used for filtering
		 * @see Node#Node(int)
		 */
		public Node(int type, String headerName) {
			this.type = type;
			this.headerName = headerName;
		}
	}

	private Node root;

	/**
	 * Creates a filter tree from the given expression.
	 * @param expr A String containing the expression.
	 */
	public MessageFilter(String expr) {
		if (correctExpression(expr)) {
			this.expr = expr;
			root = this.createBinaryTree(this.createNodeList());
		}
	}
	
	public Object clone(){
		Object obj=null;
		try{
			obj=super.clone();
		}catch(CloneNotSupportedException ex){
			logger.error(ex);
		}
		return obj;
	}

	/**
	 * The evaluation method. The filter is compared against the message in this method.
	 * @param msg The message to compare.
	 * @return True if the message satisfies the filter. False otherwise.
	 */
	public boolean compareHeaders(ACLMessage msg) {
		return this.evaluateTree(root, msg);
	}
	
	/**
	 * Gets The expression for which this object can create the filter.
	 * @return The String containing the expression.
	 */
	public String getExpression(){
		return expr;
	}

	/**
	 * Checks whether the expression is correct or not.
	 * @param expr String with the filtering expression
	 * @return True if OK, False otherwise.
	 */
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
				} else
					return true;
			}
		} else {
			matcher = pattern2.matcher(expr);
			if (matcher.find()) {
				if (matcher.end() - matcher.start() == expr.length()) {
					return true;
				}
			}
		}
		this.logger.error("[Wrong expression: " + expr + "]");
		return false;
	}

	/**
	 * Method acting like a lexer ("scanner") for the creation of the expression tree.
	 * @return The lexemes ("tokens") in order of appearance.
	 */
	private List<Node> createNodeList() {
		List<Node> nodos = new ArrayList<Node>();
		int i = 0;
		int index;
		int indexPar;
		int indexEsp;
		String headerName;
		while (i < expr.length()) {
			if (expr.charAt(i) == '(') {
				nodos.add(new Node(Node.OPARENTHESIS));
				i++;
			} else if (expr.charAt(i) == ')') {
				nodos.add(new Node(Node.CPARENTHESIS));
				i++;
			} else if (expr.charAt(i) == '!') {
				nodos.add(new Node(Node.NOTEQUAL));
				i = i + 2;
			} else if (expr.charAt(i) == '=') {
				nodos.add(new Node(Node.EQUAL));
				i++;
			} else if (expr.charAt(i) == 'A') { // pot ser un AND o altra cosa
				if ((expr.substring(i, i + 3).equals("AND"))
						&& (expr.charAt(i + 3) == ' ' || expr.charAt(i + 3) == '(')) { // es
																						// un
																						// AND
					nodos.add(new Node(Node.AND));
					i = i + 3;
				} else { // es qualsevol cadena
					indexEsp = expr.indexOf(' ', i);
					indexPar = expr.indexOf(')', i);
					if (indexEsp <= -1 && indexPar <= -1)// final de frase
						index = expr.length();
					else if (indexEsp <= -1)
						index = indexPar;
					else if (indexPar <= -1)
						index = indexEsp;
					else if (indexEsp <= indexPar)// hi ha abans un espai que un
													// parentesi
						index = indexEsp;
					else if (indexPar <= indexEsp)// hi ha abans un ) que un
													// espai
						index = indexPar;
					else
						index = indexEsp;
					headerName = expr.substring(i, index);
					nodos.add(new Node(Node.VALUE, headerName));
					i = index;
				}
			} else if (expr.charAt(i) == 'O') { // pot ser un OR o altra cosa
				if ((expr.substring(i, i + 2).equals("OR"))
						&& (expr.charAt(i + 2) == ' ' || expr.charAt(i + 2) == '(')) { // es
																						// un
																						// OR
					nodos.add(new Node(Node.OR));
					i = i + 2;
				} else { // es qualsevol cadena
					indexEsp = expr.indexOf(' ', i);
					indexPar = expr.indexOf(')', i);
					if (indexEsp <= -1 && indexPar <= -1)// final de frase
						index = expr.length();
					else if (indexEsp <= -1)
						index = indexPar;
					else if (indexPar <= -1)
						index = indexEsp;
					else if (indexEsp <= indexPar)// hi ha abans un espai que un
													// parentesi
						index = indexEsp;
					else if (indexPar <= indexEsp)// hi ha abans un ) que un
													// espai
						index = indexPar;
					else
						index = indexEsp;
					headerName = expr.substring(i, index);
					nodos.add(new Node(Node.VALUE, headerName));
					i = index;
				}
			} else if (expr.charAt(i) == 'N') { // pot ser un NOT o altra cosa
				if ((expr.substring(i, i + 3).equals("NOT"))
						&& (expr.charAt(i + 3) == ' ' || expr.charAt(i + 3) == '(')) { // es
																						// un
																						// NOT
					nodos.add(new Node(Node.NOT));
					i = i + 3;
				} else { // es qualsevol cadena
					indexEsp = expr.indexOf(' ', i);
					indexPar = expr.indexOf(')', i);
					if (indexEsp <= -1 && indexPar <= -1)// final de frase
						index = expr.length();
					else if (indexEsp <= -1)
						index = indexPar;
					else if (indexPar <= -1)
						index = indexEsp;
					else if (indexEsp <= indexPar)// hi ha abans un espai que un
													// parentesi
						index = indexEsp;
					else if (indexPar <= indexEsp)// hi ha abans un ) que un
													// espai
						index = indexPar;
					else
						index = indexEsp;
					headerName = expr.substring(i, index);
					nodos.add(new Node(Node.VALUE, headerName));
					i = index;
				}
			} else if (expr.charAt(i) != ' ') { // qualsevol paraula
				indexEsp = expr.indexOf(' ', i);
				indexPar = expr.indexOf(')', i);
				if (indexEsp <= -1 && indexPar <= -1)// final de frase
					index = expr.length();
				else if (indexEsp <= -1)
					index = indexPar;
				else if (indexPar <= -1)
					index = indexEsp;
				else if (indexEsp <= indexPar)// hi ha abans un espai que un
												// parentesi
					index = indexEsp;
				else if (indexPar <= indexEsp)// hi ha abans un ) que un espai
					index = indexPar;
				else
					index = indexEsp;
				headerName = expr.substring(i, index);
				nodos.add(new Node(Node.VALUE, headerName));
				i = index;
			} else
				i++;
		}
		return nodos;
	}

	/**
	 * The parser converting the lexemes into an expression tree.
	 * @param nodos The lexemes (obtained by a call to {@link #createNodeList()}
	 * @return The expression tree
	 */
	private Node createBinaryTree(List<Node> nodos) {
		int minPrior = -1;
		int index = -1;
		int cont = 0;
		while (cont == 0) {
			for (int i = 0; i < nodos.size(); i++) {
				if (nodos.get(i).type == Node.OPARENTHESIS) { // no tenim en
																// conter el que
																// hi ha dins
																// del
																// parentesis
					int parentesis = 1;
					int j = i + 1;
					while (parentesis > 0) {
						if (nodos.get(j).type == Node.OPARENTHESIS)
							parentesis++;
						if (nodos.get(j).type == Node.CPARENTHESIS)
							parentesis--;
						j++;
					}
					i = j;
				}
				if (i < nodos.size()) {
					if (nodos.get(i).type > minPrior) {
						minPrior = nodos.get(i).type;
						index = i;
						cont++;
					}
				}
			}
			if (cont == 0) { // la expresió era del tipus (...)
				nodos = nodos.subList(1, nodos.size() - 1);
			}
		}
		Node root = new Node(nodos.get(index).type);
		if (root.type != Node.VALUE) {
			if (root.type == Node.NOT)
				root.left = createBinaryTree(nodos
						.subList(index + 1, nodos.size()));
			else{
				root.left = createBinaryTree(nodos.subList(0, index));
				root.right = createBinaryTree(nodos
						.subList(index + 1, nodos.size()));
			}
		} else
			root.headerName = nodos.get(index).headerName;
		return root;
	}

	/**
	 * Walks the expression tree filter and compares it with the given message.
	 * @param root The tree filter root node.
	 * @param msg The message.
	 * @return True if the message satisfies the filter. False otherwise.
	 */
	private boolean evaluateTree(Node root, ACLMessage msg) {
		boolean equalValue;
		if (root.type == Node.AND) {
			return evaluateTree(root.left, msg)
					&& evaluateTree(root.right, msg);
		} else if (root.type == Node.OR) {
			return evaluateTree(root.left, msg)
					|| evaluateTree(root.right, msg);
		} else if (root.type == Node.NOT) {
			return !evaluateTree(root.left, msg);
		} else if (root.type == Node.EQUAL || root.type == Node.NOTEQUAL) {
			if(root.left.headerName.toLowerCase().equals("performative"))
				equalValue = msg.getPerformative().toLowerCase().equals(root.right.headerName.toLowerCase());
			else if(root.left.headerName.toLowerCase().equals("protocol"))
				equalValue = msg.getProtocol().toLowerCase().equals(root.right.headerName.toLowerCase());
			else if(root.left.headerName.toLowerCase().equals("sender"))
				equalValue = msg.getSender().toString().toLowerCase().equals(root.right.headerName.toLowerCase());
			else if(root.left.headerName.toLowerCase().equals("receiver"))
				equalValue = msg.getReceiver().toString().toLowerCase().equals(root.right.headerName.toLowerCase());
			else if(root.left.headerName.toLowerCase().equals("reply-to"))
				equalValue = msg.getReplyTo().toString().toLowerCase().equals(root.right.headerName.toLowerCase());
			else if(root.left.headerName.toLowerCase().equals("content"))
				equalValue = msg.getContent().toLowerCase().equals(root.right.headerName.toLowerCase());
			else if(root.left.headerName.toLowerCase().equals("language"))
				equalValue = msg.getLanguage().toLowerCase().equals(root.right.headerName.toLowerCase());
			else if(root.left.headerName.toLowerCase().equals("encoding"))
				equalValue = msg.getEncoding().toLowerCase().equals(root.right.headerName.toLowerCase());
			else if(root.left.headerName.toLowerCase().equals("ontology"))
				equalValue = msg.getOntology().toLowerCase().equals(root.right.headerName.toLowerCase());
			else if(root.left.headerName.toLowerCase().equals("conversation-id"))
				equalValue = msg.getConversationId().toLowerCase().equals(root.right.headerName.toLowerCase());
			else if(root.left.headerName.toLowerCase().equals("reply-with"))
				equalValue = msg.getReplyWith().toLowerCase().equals(root.right.headerName.toLowerCase());
			else if(root.left.headerName.toLowerCase().equals("in-reply-to"))
				equalValue = msg.getInReplyTo().toLowerCase().equals(root.right.headerName.toLowerCase());
			else if(root.left.headerName.toLowerCase().equals("reply-by"))
				equalValue = msg.getReplyBy().toLowerCase().equals(root.right.headerName.toLowerCase());
			else equalValue = msg.getHeaderValue(root.left.headerName).equals(root.right.headerName);
			
			if(root.type == Node.EQUAL)
				return equalValue;
			else
				return !equalValue;
		} else
			return false;
	}
}
