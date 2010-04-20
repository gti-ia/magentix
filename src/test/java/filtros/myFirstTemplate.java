package filtros;

import org.apache.log4j.xml.DOMConfigurator;

import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentsConnection;
import es.upv.dsic.gti_ia.core.MessageFilter;

public class myFirstTemplate {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		DOMConfigurator.configure("configuration/loggin.xml");
		AgentsConnection.connect("localhost");
		
		//MessageFilter template = new MessageFilter("NOT performative AND NOT (seller AND(buyer OR object AND purpose))");
		MessageFilter template = new MessageFilter("(purpose != comprar OR object != tv) AND buyer = ramon ");
				
		ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
		msg.setHeader("purpose", "vender");
		msg.setHeader("object", "tv");
		msg.setHeader("buyer", "ramon");
		msg.setHeader("seller", "juan");
		msg.setHeader("place", "mercado");
		
		if(template.compareHeaders(msg))
			System.out.println("Pasa el filtro");
		else
			System.out.println("No pasa el filtro");
	}

}
