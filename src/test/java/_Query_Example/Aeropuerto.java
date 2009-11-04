package _Query_Example;




import es.upv.dsic.gti_ia.architecture.FIPAQueryResponder;
import es.upv.dsic.gti_ia.architecture.FailureException;
import es.upv.dsic.gti_ia.architecture.MessageTemplate;
import es.upv.dsic.gti_ia.architecture.NotUnderstoodException;
import es.upv.dsic.gti_ia.architecture.QueueAgent;
import es.upv.dsic.gti_ia.architecture.RefuseException;
import es.upv.dsic.gti_ia.architecture.FIPANames.InteractionProtocol;
import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;


public class Aeropuerto extends QueueAgent {

	
	
    
    public Aeropuerto(AgentID aid) throws Exception
    {
    	super(aid);   
    }
    protected void execute() {
        System.out.println(this.getName() + ": Abriendo centralita...");
 
        // Filtrado para recibir sólo mensajes del protocolo FIPA-Query.
    
        
        
        MessageTemplate plantilla = new MessageTemplate(InteractionProtocol.FIPA_QUERY);
        ComprobarResponder responder = new ComprobarResponder(this, plantilla);
		System.out.println("Aeropuerto "+this.getName()+": Esperando avisos...");
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
            System.out.printf("Operadora: Hemos recibido una llamada de %s solicitando informacion sobre su reserva.", request.getSender().getLocalName());
 
            // Si el solicitante es válido se acepta su petición.
 
            if (true){//comprobarSolicitante(request.getSender().getLocalName())) {
                System.out.println("Operadora: Espere un momento por favor...");
                ACLMessage agree = request.createReply();
                agree.setPerformative(ACLMessage.AGREE);
                return agree;
            } else {
                System.out.println(getName() + ": Todas las operadoras estan ocupadas.");
                throw new RefuseException("Por favor intentelo de nuevo mas tarde");
            }
        }
 
        protected ACLMessage prepareResultNotification(ACLMessage request, ACLMessage response) throws FailureException {
            ACLMessage inform = request.createReply();
            inform.setPerformative(ACLMessage.INFORM);
            String retorno = "No dispone de ninguna reserva";
 
            if (comprobarSolicitante(request.getSender().getLocalName()))
                retorno = "Si que ha hecho alguna reserva";
 
            inform.setContent(retorno);
            return inform;
        }
 
        // Método simple de aceptación o rechazo de solicitudes.
        private boolean comprobarSolicitante(String nombre) {
            return (nombre.length() > 25);
        }
    }
	
}
