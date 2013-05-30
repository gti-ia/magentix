package TestQueueAgent;




import es.upv.dsic.gti_ia.architecture.FIPAQueryResponder;
import es.upv.dsic.gti_ia.architecture.FailureException;
import es.upv.dsic.gti_ia.architecture.MessageTemplate;
import es.upv.dsic.gti_ia.architecture.NotUnderstoodException;
import es.upv.dsic.gti_ia.architecture.QueueAgent;
import es.upv.dsic.gti_ia.architecture.RefuseException;
import es.upv.dsic.gti_ia.architecture.FIPANames.InteractionProtocol;
import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;


/**
 * Airpot class defines the structure of a Responder role in the FIPA Query Protocol
 * 
 * @author David Fernández - dfernandez@dsic.upv.es
 */

public class Airport extends QueueAgent {

	public double ASSIST_PROB;//Probability of assisting the passenger
	
	private ComprobarResponder responder;
	
	private Boolean end;
    
    public Airport(AgentID aid) throws Exception
    {
    	super(aid);
    	
    	ASSIST_PROB=0;
    	end=false;
    }
    protected void execute() {
        System.out.println(this.getName() + ": Opening unit...");
 
        // Filtrado para recibir s�lo mensajes del protocolo FIPA-Query.
           
        
        MessageTemplate plantilla = new MessageTemplate(InteractionProtocol.FIPA_QUERY);
        responder = new ComprobarResponder(this, plantilla);
		System.out.println("AirPort "+this.getName()+": Waiting notices...");
    	while(!finished()){
      		responder.action();
    	}
    	if(end){
    		//In case it has ended because RefuseMessage
    		//Airport needs another action() to send the refuseMessage
    		//to the passenger, if not passenger will wait forever
    		responder.action();
    	}
    }
    
    //Informs when the protocol is one of the final states
    //RESET_STATE is the following state to SEND_RESULT_NOTIFICATION_STATE
    //it is needed to stop Airport after SEND_RESULT_NOTIFICATION_STATE
    //because if not it will not send the notification message
  	public boolean finished(){
  		return (responder.getState()==5 ||  end);
  	}
 
    class ComprobarResponder extends FIPAQueryResponder {
        public ComprobarResponder(QueueAgent agente, MessageTemplate plantilla) {
            super(agente, plantilla);
        }
 
        protected ACLMessage prepareResponse(ACLMessage request)
                throws NotUnderstoodException, RefuseException {
            System.out.printf("Operator: We received a call from %s requesting information about your reservation.\n", request.getSender().getLocalName());
 
        
            if (ASSIST_PROB < 0.50){//comprobarSolicitante(request.getSender().getLocalName())) {
                System.out.println("Operator: One moment please...");
                ACLMessage agree = request.createReply();
                agree.setPerformative(ACLMessage.AGREE);
                return agree;
            } else {
                System.out.println(getName() + ": All operators are busy.");
                end=true;
                throw new RefuseException("Please try again later");
            }
        }
 
        protected ACLMessage prepareResultNotification(ACLMessage request, ACLMessage response) throws FailureException {
            ACLMessage inform = request.createReply();
            inform.setPerformative(ACLMessage.INFORM);
            String retorno = "You have no reserves";
 
            if (comprobarSolicitante(request.getSender().getLocalName()))
                retorno = "You have made a reservation";
 
            inform.setContent(retorno);
            return inform;
        }
 
        // M�todo simple de aceptaci�n o rechazo de solicitudes.
        private boolean comprobarSolicitante(String nombre) {
            return (nombre.length() > 5);
        }
    }
	
}
