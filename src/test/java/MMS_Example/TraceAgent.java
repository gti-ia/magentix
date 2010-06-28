package MMS_Example;

import org.apache.qpid.transport.MessageAcceptMode;
import org.apache.qpid.transport.MessageAcquireMode;
import org.apache.qpid.transport.MessageCreditUnit;
import org.apache.qpid.transport.Option;
import org.apache.qpid.transport.Session;

import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.BaseAgent;

public class TraceAgent extends BaseAgent {

	public TraceAgent(AgentID aid, String keyStorePath, String key,
			String CertType) throws Exception {
		super(aid);
		
	}
	public void execute()
	{
		
		this.createQueue();
		this.createBind();
		this.unbindExchange();
	}
	

	/**
	 * 
	 * Unbind the exchange and the agent queue
	 */
	private void unbindExchange() {

		this.session.exchangeUnbind("event.guest", "amq.fanout", "event.guest");
		System.out.println("Unbind creada");
	}
	
	public void createQueue()
	{	
		this.session.queueDeclare("event.guest", null, null, Option.AUTO_DELETE);
		System.out.println("Cola creada");
	}
	private void createBind() {
		// this.session.exchangeBind(aid.name, aid.name, null, null);
		this.session.exchangeBind("event.guest", "amq.fanout", "event.guest", null);
		System.out.println("Bind creada");

	}

}
