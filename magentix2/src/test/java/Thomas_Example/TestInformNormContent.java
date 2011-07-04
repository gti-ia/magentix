package Thomas_Example;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import BaseAgent_Example.Run;
import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;
import es.upv.dsic.gti_ia.core.BaseAgent;
import es.upv.dsic.gti_ia.organization.OMS;

public class TestInformNormContent extends BaseAgent
{
	
	public TestInformNormContent(AgentID aid) throws Exception
	{
		super(aid);
	}
	public void execute()
	{
		
		AgentID receiver = new AgentID("OMS");
		
		
		ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
		msg.setSender(this.getAid());
		msg.setReceiver(receiver);
		//msg.setProtocol("REQUEST");
		//msg.setPerformative("REQUEST");
		String OMSLocation = "http://localhost:8080/omsservices/OMSservices/owl/owls/";
		String callString = OMSLocation+"InformNormContentProcess.owl NormID=norma1 AgentID="+this.getAid().name;
		//String callString = OMSLocation+"AcquireRoleProcess.owl RoleID=member UnitID=virtual AgentID=customer@ThomasPlatform";
		msg.setContent(callString);
		send(msg);
		System.out.println("Mensaje enviado.");
		
		while(true)
		{
			try
			{
				Thread.sleep(100);
			}
			catch (InterruptedException e)
			{ }
		}
		
	}
	public void onMessage(ACLMessage msg)
	{
		logger.info("Mensaje received in " + this.getName()
				+ " agent, by onMessage: " + msg.getContent());
	}

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception
	{
		/**
		 * Setting the Logger
		 */
		DOMConfigurator.configure("configuration/loggin.xml");
		Logger logger = Logger.getLogger(Run.class);
		
		
		AgentsConnection.connect();
		
		OMS oms = OMS.getOMS();
		oms.start();
		
		TestInformNormContent agent = new TestInformNormContent(new AgentID(
				"tester"));
		agent.start();
	}
	
}
