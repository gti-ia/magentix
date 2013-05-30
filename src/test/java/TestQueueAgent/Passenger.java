package TestQueueAgent;






import es.upv.dsic.gti_ia.architecture.Monitor;
import es.upv.dsic.gti_ia.architecture.FIPANames;
import es.upv.dsic.gti_ia.architecture.FIPAQueryInitiator;
import es.upv.dsic.gti_ia.architecture.QueueAgent;
import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;


/**
 * Passenger class defines the structure of a Initiator role in the FIPA Query Protocol
 * 
 * @author David Fernández - dfernandez@dsic.upv.es
 */



public class Passenger extends QueueAgent {
	
	//Storing the query result
	public String queryResult;
	public String informResult;
	
	private ComprobarInitiator ini;
	

	private Monitor adv= new Monitor();
    public Passenger(AgentID aid) throws Exception
    {
    	super(aid);
        
    	queryResult="";
    	informResult="";
    
    }
    protected void execute() {
    	 
        //Create query message
 
        ACLMessage mensaje = new ACLMessage(ACLMessage.QUERY_IF);
        mensaje.setProtocol(FIPANames.InteractionProtocol.FIPA_QUERY);
        mensaje.setContent("" +"I have a reservation?");
 
        mensaje.setSender(getAid());
        mensaje.setReceiver(new AgentID("ManisesAirPort","qpid","localhost",""));
        
        //Add query behaviour
        ini = new ComprobarInitiator(this, mensaje);
        this.addTask(ini); 
        
        while(!ini.finished()){
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}        
    }
    
    public boolean finished(){
    	return ini.finished();
    }
 
    class ComprobarInitiator extends FIPAQueryInitiator {
        public ComprobarInitiator(QueueAgent agente, ACLMessage mensaje) {
            super(agente, mensaje);
        }
 
        protected void handleAgree(ACLMessage agree) {
        	queryResult="Wait a moment please, we are looking for in the Database"+agree.getSender().getLocalName();
        	System.out.println(queryResult);
        }
 
        protected void handleRefuse(ACLMessage refuse) {
        	queryResult=getName()+": At the moment all operators are busy. We can not assist"+refuse.getSender().getLocalName();
            System.out.println(queryResult);
        }
 
        protected void handleNotUnderstood(ACLMessage notUnderstood) {
            queryResult=getName()+": The operator does not understand the message"+notUnderstood.getSender().getLocalName();
            System.out.println(queryResult);
        }
 
        protected void handleInform(ACLMessage inform) {
        	informResult="The operator reports:"+inform.getContent();
        }
 
        protected void handleFailure(ACLMessage fallo) {
        	queryResult=getName() + ": There has been a failure.";
            System.out.println(queryResult);
        }
    }
}
