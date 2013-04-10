package TestTemplate;

import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentsConnection;
import es.upv.dsic.gti_ia.core.MessageFilter;

import junit.framework.TestCase;

public class TestTemplate extends TestCase {

	MessageFilter template = null;
	ACLMessage msg = null;
	
	protected void setUp() throws Exception{
		
		template = new MessageFilter("performative = UNKNOWN AND purpose= vender");
				
		msg = new ACLMessage(ACLMessage.UNKNOWN);
		msg.setHeader("purpose", "vender");
		msg.setHeader("object", "tv");
		msg.setHeader("buyer", "ramon");
		msg.setHeader("seller", "juan");
		msg.setHeader("place", "mercado");
		
	}
	public void testTemplate() {

		if(!template.compareHeaders(msg))
			fail("The template did not passed");
}

}