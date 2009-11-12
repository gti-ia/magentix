package ContractNet_Example;




import es.upv.dsic.gti_ia.architecture.FIPAContractNetInitiator;
import es.upv.dsic.gti_ia.architecture.FIPANames;
import es.upv.dsic.gti_ia.architecture.Monitor;
import es.upv.dsic.gti_ia.architecture.QueueAgent;
import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;



import java.util.Date;
import java.util.ArrayList;

public class Client extends QueueAgent {


	private int precionMaximo;
	private int numeroDeOfertas=0;
    public Client(AgentID aid) throws Exception
    {

    	super(aid);
        
    
    }
    
    protected void execute() {
        //El precio máximo se recibirá como argumento de entrada.
        
 
        
            this.precionMaximo = 20000000;
 
            //Búsqueda del servicio de venta de coches en las páginas amarillas.
              
                                  //Creamos el mensaje CFP(Call For Proposal) cumplimentando sus parámetros
                    ACLMessage mensajeCFP = new ACLMessage(ACLMessage.CFP);
                    
                    for(int i=0;i<200;i++)
                    {
                    mensajeCFP.addReceiver(new AgentID("Concesionario"+i));
                    }
                    
                    
                    //Protocolo que vamos a utilizar
                    mensajeCFP.setProtocol(FIPANames.InteractionProtocol.FIPA_CONTRACT_NET);
                    mensajeCFP.setContent("I look for car, do you propose to prices?");
                    //mensajeCFP.setSender(getAid());
                    //Indicamos el tiempo que esperaremos por las ofertas.
                    mensajeCFP.setReplyByDate(new Date(System.currentTimeMillis() + 1500000));
 
                    //Se añade el comportamiento que manejará las ofertas.
                    
                    this.addTask(new ManejoOpciones(this, mensajeCFP));
                 
                    Monitor adv = new Monitor();
                    adv.waiting();
 
             

 
    } // Fin del setup
 
    private class ManejoOpciones extends FIPAContractNetInitiator {
 
        public ManejoOpciones(QueueAgent agente, ACLMessage plantilla) {
            super(agente, plantilla);
        }
 
        //Manejador de proposiciones.
        protected void handlePropose(ACLMessage propuesta, ArrayList<ACLMessage> aceptadas) {
            System.out.printf("%s: Received offer of cars %s. A car offers for %s Euros.\n",
                this.myAgent.getName(), propuesta.getSender().getLocalName(), propuesta.getContent());
        }
 
        //Manejador de rechazos de proposiciones.
        protected void handleRefuse(ACLMessage rechazo) {
            System.out.printf("%s: Cars %s does not have cars that to offer.\n",
                this.myAgent.getName(), rechazo.getSender().getLocalName());
        }
 
        //Manejador de respuestas de fallo.
        protected void handleFailure(ACLMessage fallo) {
          //  if (fallo.getSender().equals(myAgent.getAMS())) {
 
        //Esta notificacion viene del entorno de ejecución JADE (no existe el receptor)
                System.out.println("AMS: This sale of cars does not exist or is accessible");
            //} else {
                System.out.printf("%s: Cars %s has been a failure.\n",
                    this.myAgent.getName(), fallo.getSender().getLocalName());
            //}
            //Falló, por lo tanto, no recibiremos respuesta desde ese agente
            Client.this.numeroDeOfertas--;
        }
 
        //Método colectivo llamado tras finalizar el tiempo de espera o recibir todas las propuestas.
        protected void handleAllResponses(ArrayList<ACLMessage>  respuestas, ArrayList<ACLMessage>  aceptados) {
 
        //Se comprueba si una venta de autos se pasó del plazo de envío de ofertas.
            if (respuestas.size() < numeroDeOfertas) {
                System.out.printf("%s: %d Car sales are late.\n",
                    this.myAgent.getName(), Client.this.numeroDeOfertas - respuestas.size());
            }
 
            //Escogemos la mejor oferta
            int mejorOferta = Integer.MAX_VALUE;
            AgentID mejorAutos = null;
            ACLMessage aceptado = null;
            for (Object resp:respuestas) {
                ACLMessage mensaje = (ACLMessage) resp;
                if (mensaje.getPerformativeInt() == ACLMessage.PROPOSE) {
                    ACLMessage respuesta = mensaje.createReply();
                    respuesta.setPerformative(ACLMessage.REJECT_PROPOSAL);
                    aceptados.add(respuesta);
 
                    //Si la oferta es la mejor (inferior a todas las otras)
                    //Se almacena su precio y el AID de la venta de autos que la hizo.
                    int oferta = Integer.parseInt(mensaje.getContent());
                    if (oferta <= precionMaximo && oferta <= mejorOferta) {
                        mejorOferta = oferta;
                        mejorAutos = mensaje.getSender();
                        aceptado = respuesta;
                    }
                }
            }
 
            //Si hay una oferta aceptada se modifica su performativa.
            if (aceptado != null) {
                System.out.printf("%s: Determined! Sell Car of the %s\n",
                    this.myAgent.getName(), mejorAutos.getLocalName());
                aceptado.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
            }
        }
 
        //Manejador de los mensajes inform.
        protected void handleInform(ACLMessage inform) {
            System.out.printf("%s: %s has sent the contract.\n",
                this.myAgent.getName(), inform.getSender().getLocalName());
        }
    }
}