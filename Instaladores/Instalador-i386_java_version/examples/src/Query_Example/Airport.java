package Query_Example;




import es.upv.dsic.gti_ia.architecture.FIPAQueryResponder;
import es.upv.dsic.gti_ia.architecture.FailureException;
import es.upv.dsic.gti_ia.architecture.MessageTemplate;
import es.upv.dsic.gti_ia.architecture.NotUnderstoodException;
import es.upv.dsic.gti_ia.architecture.QueueAgent;
import es.upv.dsic.gti_ia.architecture.RefuseException;
import es.upv.dsic.gti_ia.architecture.FIPANames.InteractionProtocol;
import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;


public class Airport extends QueueAgent {

	
	
    
    public Airport(AgentID aid) throws Exception
    {
    	super(aid);   
    }
    protected void execute() {
        System.out.println(this.getName() + ": Opening unit...");
 
        // Filtrado para recibir s�lo mensajes del protocolo FIPA-Query.
    
        
        
        MessageTemplate plantilla = new MessageTemplate(InteractionProtocol.FIPA_QUERY);
        ComprobarResponder responder = new ComprobarResponder(this, plantilla);
		System.out.println("AirPort "+this.getName()+": Waiting notices...");
    	do{
      		responder.action();
    	}while(true);
    }
 
    class ComprobarResponder extends FIPAQueryResponder {
        public ComprobarResponder(QueueAgent agente, MessageTemplate plantilla) {
            super(agente, plantilla);
        }
 
        protected ACLMessage prepareResponse(ACLMessage request)
                throws NotUnderstoodException, RefuseException {
            System.out.printf("Operator: We received a call from %s requesting information about your reservation.\n", request.getSender().getLocalName());
 
        
            if ((Math.random()) < 0.50){//comprobarSolicitante(request.getSender().getLocalName())) {
                System.out.println("Operator: One moment please...");
                ACLMessage agree = request.createReply();
                agree.setPerformative(ACLMessage.AGREE);
                return agree;
            } else {
                System.out.println(getName() + ": All operators are busy.");
                throw new RefuseException("Please try again later");
            }
        }
 
        protected ACLMessage prepareResultNotification(ACLMessage request, ACLMessage response) throws FailureException {
            ACLMessage inform = request.createReply();
            inform.setPerformative(ACLMessage.INFORM);
            String retorno = "You have no reserves";
 
            if (comprobarSolicitante(request.getSender().getLocalName()))
                retorno = "You has made a reservation";
 
            inform.setContent(retorno);
            return inform;
        }
 
        // M�todo simple de aceptaci�n o rechazo de solicitudes.
        private boolean comprobarSolicitante(String nombre) {
            return (nombre.length() > 5);
        }
    }
	
}
