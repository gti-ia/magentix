package BaseAgent_Example;

import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.BaseAgent;
import es.upv.dsic.gti_ia.core.SingleAgent;

/**
 * EmisorAgent class define the structure of a sender BaseAgent
 * 
 * @author Sergio Pajares - spajares@dsic.upv.es
 * @author Joan Bellver - jbellver@dsic.upv.es
 */
public class SenderAgent extends SingleAgent {

	long t1,t2;
	
	long results[];
	int cambios[];
	
	int ntotal=0,completat=0,nresults=0;
	
	public SenderAgent(AgentID aid) throws Exception {
		super(aid);
		results = new long[100];                        // result array
		cambios = new int[100];
	}

	public void execute() {
		logger.info("Executing, I'm " + getName());
		AgentID receiver = new AgentID("receptor0");

		/**
		 * Building a ACLMessage
		 */
		ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
		msg.setReceiver(receiver);

		msg.setLanguage("ACL");
		msg.setContent("Hello, I'm " + getName());

		/**
		 * Sending a ACLMessage
		 */

		int cambio=0;
		/**
		 * Sending a ACLMessage
		 */
		
		System.out.println("Empiezo a contar");
		for (int i = 0; i < 100;i++)
		{
			cambio=0;
			t1 = System.currentTimeMillis();
			for (int k=0;k<i;k++)
			{
		
				msg.setSender(this.getAid());
			
				send(msg);
				try{
					try {
						ACLMessage msg2 = this.receiveACLMessage();
				
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				
					this.changeIdentity(new AgentID("agent"+i+":"+k));
					System.out.println("Tiempo: "+ (System.currentTimeMillis() - t1 ) / 1000);
					cambio++;

				}catch(Exception e)
				{
					System.err.println(e.getMessage());
				}

			}
	
			for (int j=i;j<100;j++)
			{
				
				msg.setSender(this.getAid());
				
				send(msg);
			
				try {
					ACLMessage msg2 = this.receiveACLMessage();
				
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		
			t2 = System.currentTimeMillis();
			
			/* Statistics */

		//	if(completat>nmsgpad && completat<=nmsgpad+nmsg) 
			//{
	
				results[nresults] = t2 - t1;
				cambios[nresults] = cambio;
	
				nresults++;
				
				
		//	}

			completat++;

		}
		
		System.out.println("Termino de contar");

		/* writing RTT times collected in milliseconds */
		for(int i=0;i<100;i++)
		{
			
			System.out.println(cambios[i]+" "+results[i]);
		}


	}



	

}
