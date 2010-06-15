package Query_Example;






import es.upv.dsic.gti_ia.architecture.FIPANames;
import es.upv.dsic.gti_ia.architecture.FIPAQueryInitiator;
import es.upv.dsic.gti_ia.architecture.Monitor;
import es.upv.dsic.gti_ia.architecture.QueueAgent;
import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;




public class Passenger extends QueueAgent {

	private Monitor adv= new Monitor();
    public Passenger(AgentID aid) throws Exception
    {

    	super(aid);
        
    
    }
    protected void execute() {
    	 
        //Creamos el mensaje de la consulta.
 
        ACLMessage mensaje = new ACLMessage(ACLMessage.QUERY_IF);
        mensaje.setProtocol(FIPANames.InteractionProtocol.FIPA_QUERY);
        mensaje.setContent("" +"I have a reservation?");
 
        mensaje.setSender(getAid());
        mensaje.setReceiver(new AgentID("ManisesAirPort","qpid","localhost",""));
        
        //A�adimos el comportamiento de la consulta.
        this.addTask(new ComprobarInitiator(this, mensaje)); 
        adv.waiting();
        
    }
 
    class ComprobarInitiator extends FIPAQueryInitiator {
        public ComprobarInitiator(QueueAgent agente, ACLMessage mensaje) {
            super(agente, mensaje);
        }
 
        protected void handleAgree(ACLMessage agree) {
            System.out.printf("Wait a moment please, we are looking for in the Database.", agree.getSender().getLocalName());
        }
 
        protected void handleRefuse(ACLMessage refuse) {
            System.out.printf("%s: At the moment all operators are busy. We can not assist.", getName(), refuse.getSender().getLocalName());
        }
 
        protected void handleNotUnderstood(ACLMessage notUnderstood) {
            System.out.printf("%s: The operator does not understand the message.", getName(), notUnderstood.getSender().getLocalName());
        }
 
    protected void handleInform(ACLMessage inform) {
            System.out.printf("The operator reports: %s.", inform.getContent());
        }
 
        protected void handleFailure(ACLMessage fallo) {
            System.out.println(getName() + ": There has been a failure.");
        }
    }
}
