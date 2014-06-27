package TestTemplate;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.MessageFilter;

public class TestTemplate {

	MessageFilter template = null;
	ACLMessage msg = null;
	
	@Before
	public void setUp() throws Exception {

		template = new MessageFilter(
				"performative = UNKNOWN AND purpose= vender");

		msg = new ACLMessage(ACLMessage.UNKNOWN);
		msg.setHeader("purpose", "vender");
		msg.setHeader("object", "tv");
		msg.setHeader("buyer", "ramon");
		msg.setHeader("seller", "juan");
		msg.setHeader("place", "mercado");

	}
	@Test(timeout = 5 * 1000)
	public void testTemplate() {

		if (!template.compareHeaders(msg))
			fail("The template did not passed");
	}

}