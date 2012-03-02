package httpInterfaceTest;

import java.io.Writer;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.json.JettisonMappedXmlDriver;
import com.thoughtworks.xstream.io.json.JsonHierarchicalStreamDriver;
import com.thoughtworks.xstream.io.json.JsonWriter;

import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.SingleAgent;

public class MarketAgent extends SingleAgent{

	private class ProductQuery {

		public String type;
		public int max_price;
	}

	private class JSONMessage {

		public String agent_name;
		public String conversation_id;
		public ProductQuery content;
	}

	public MarketAgent(AgentID aid) throws Exception {
		super(aid);
	}

	public void execute() {
		/**
		 * Wait for messages, forever
		 */
		while(true){
			try {
				ACLMessage msg = receiveACLMessage(); // receive the message with the query from the web page
				// Transform the input data into a Java object
				XStream xstream = new XStream(new JettisonMappedXmlDriver());
				xstream.alias("jsonObject", JSONMessage.class);
				JSONMessage query = (JSONMessage)xstream.fromXML(msg.getContent());
				
				// We prepare the response message
				ACLMessage response = new ACLMessage(ACLMessage.INFORM);
				response.setSender(getAid());
				response.setReceiver(msg.getSender());
				Product product = new Product();
				
				// check which product fits the specifications
				if(query.content.type.equals("fruit")){
					if(query.content.max_price <= 10){
						product.name = "Banana";
						product.id = 123;
						product.price = 8;
					}
					else{
						product.name = "Apple";
						product.id = 456;
						product.price = 25;
					}
				}
				else{
					if(query.content.max_price <= 10){
						product.name = "Sausage";
						product.id = 789;
						product.price = 9;
					}
					else{
						product.name = "Hamburger";
						product.id = 159;
						product.price = 29;
					}
				}
				
				// Transform the product Java objet into a JSON object
				XStream xstream2 = new XStream(new JsonHierarchicalStreamDriver() {
					@Override
					public HierarchicalStreamWriter createWriter(Writer writer) {
						return new JsonWriter(writer, JsonWriter.DROP_ROOT_MODE);
					}
				});
				String result = xstream2.toXML(product);
				response.setContent(result);
				
				// send the response message
				this.send(response);
			} catch (Exception e) {
				logger.error(e.getMessage());
				System.out.println(e.getMessage());
				return;
			}
		}
	}
}
