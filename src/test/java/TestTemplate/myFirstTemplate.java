package TestTemplate;

import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentsConnection;
import es.upv.dsic.gti_ia.core.MessageFilter;

import junit.framework.TestCase;

public class TestTemplate extends TestCase {

	protected void setUp() throws Exception{
		AgentsConnection.connect("localhost");
		
		MessageFilter template = new MessageFilter("performative = UNKNOWN AND purpose= vender");
				
		ACLMessage msg = new ACLMessage(ACLMessage.UNKNOWN);
		msg.setHeader("purpose", "vender");
		msg.setHeader("object", "tv");
		msg.setHeader("buyer", "ramon");
		msg.setHeader("seller", "juan");
		msg.setHeader("place", "mercado");
		
	}
	public void testTemplate() {

		if(template.compareHeaders(msg))
			return true;
		else
			fail("The filter is not passed");
	}

}
