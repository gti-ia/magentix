package _Query_Example;






import es.upv.dsic.gti_ia.architecture.Monitor;
import es.upv.dsic.gti_ia.architecture.FIPANames;
import es.upv.dsic.gti_ia.architecture.FIPAQueryInitiator;
import es.upv.dsic.gti_ia.architecture.QueueAgent;
import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;




public class Viajante extends QueueAgent {

	private Monitor adv= new Monitor();
    public Viajante(AgentID aid) throws Exception
    {

    	super(aid);
        
    
    }
    protected void execute() {
    	 
        //Creamos el mensaje de la consulta.
 
        ACLMessage mensaje = new ACLMessage(ACLMessage.QUERY_IF);
        mensaje.setProtocol(FIPANames.InteractionProtocol.FIPA_QUERY);
        mensaje.setContent("" +"¿Tengo la reserva?");
 
        mensaje.setSender(getAid());
        mensaje.setReceiver(new AgentID("aeropuerto1","qpid","localhost",""));
        
        //Añadimos el comportamiento de la consulta.
        this.addTask(new ComprobarInitiator(this, mensaje)); 
        adv.waiting();
        
    }
 
    class ComprobarInitiator extends FIPAQueryInitiator {
        public ComprobarInitiator(QueueAgent agente, ACLMessage mensaje) {
            super(agente, mensaje);
        }
 
        protected void handleAgree(ACLMessage agree) {
            System.out.printf("Espere un momento por favor, estamos buscando en la Base de Datos.", agree.getSender().getLocalName());
        }
 
        protected void handleRefuse(ACLMessage refuse) {
            System.out.printf("%s: En estos momentos todas las operadoras estan ocupadas. No podemos atenderle.", getName(), refuse.getSender().getLocalName());
        }
 
        protected void handleNotUnderstood(ACLMessage notUnderstood) {
            System.out.printf("%s: La operadora no entiende el mensaje.", getName(), notUnderstood.getSender().getLocalName());
        }
 
    protected void handleInform(ACLMessage inform) {
            System.out.printf("La operadora informa: %s.", inform.getContent());
        }
 
        protected void handleFailure(ACLMessage fallo) {
            System.out.println(getName() + ": Se ha producido un fallo.");
        }
    }
}
