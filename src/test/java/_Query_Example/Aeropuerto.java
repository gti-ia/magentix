package _Query_Example;

import org.apache.qpid.transport.Connection;

import s.dsic.gti_ia.fipa.*;
import s.dsic.gti_ia.proto.*;
import s.dsic.gti_ia.proto.FIPANames.InteractionProtocol;

import _BaseAgent_Example.QueueAgent;
import _Request_Example.Principal_Grafico;


import es.upv.ACLMessage;
import es.upv.FIPAQueryResponder;
import es.upv.FailureException;
import es.upv.NotUnderstoodException;
import es.upv.RefuseException;

public class Aeropuerto extends QueueAgent {

	
	
    
    public Aeropuerto(AgentID aid, Connection connection)
    {
    	super(aid, connection);   
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
