package _thomas_Example;



import es.upv.dsic.gti_ia.organization.Configuration;


import es.upv.dsic.gti_ia.architecture.*;
import es.upv.dsic.gti_ia.organization.*;
import java.util.Date;
import java.util.ArrayList;

import es.upv.dsic.gti_ia.core.*;
import es.upv.dsic.gti_ia.core.AgentID;

public class Cliente extends QueueAgent {


	private int precionMaximo;
	private int numeroDeOfertas=0;
	ArrayList<AgentID> agentes = new ArrayList<AgentID>();
	
	
    public Cliente(AgentID aid)throws Exception{
    

    	super(aid);
        
    
    }
    
    protected void execute() {
        //El precio máximo se recibirá como argumento de entrada.
        
 
        
            this.precionMaximo = 20000000;
            
            Configuration c= Configuration.getConfiguration();
            
            SFAgentDescription descripcionsf = new SFAgentDescription(c.getTHServiceDesciptionLocation(),c.getTHServiceDesciptionLocation());
            SFProxy serviciosf = new SFProxy();
            
            OMSProxy servicio  = new OMSProxy();
            
            
            
            System.out.println("Voy a registrar el cliente en la organizacion");
            
   
            
            String resultado = servicio.AcquireRole(this,"member","virtual");
            System.out.println("AcquireRole: "+ resultado+ " Agent: "+ this.getName());
                
            
            
            
            for (int i=0;i<900000000;i++)
            	for (int j=0;j<20;j++)

            	{}  
            
           
            System.out.println("Me registro con un rol customer en la organizacion virtualAgeny para acceder al servicio.");
            

           servicio.AcquireRole(this,"member","travelagency");
            

           System.out.println("AcquireRole: "+ resultado+ " Agent: "+ this.getName());
            
          	
           servicio.AcquireRole(this,"customer","travelagency");
            
            descripcionsf.setServiceGoal("Concesionario"); 
            System.out.println("Hago una busqueda de servicio.");
            
     
           // ArrayList<String> ids = serviciosf.searchService(this,"Concesionario");
            
            //this.agentes = serviciosf.getProcess(this, ids.get(0));

            
            
            if (agentes.size() <= 0) {
                System.out.println("No existen ventas de coches.");
            } else {
            
            	ACLMessage mensajeCFP = new ACLMessage(ACLMessage.CFP);
            	
           	
             for (AgentID a : agentes)
             {
            	 System.out.println("Voy a enviar a:"+ a.name_all());
            	 mensajeCFP.addReceiver(a);
             }
            //Búsqueda del servicio de venta de coches en las páginas amarillas.
              
                                  //Creamos el mensaje CFP(Call For Proposal) cumplimentando sus parámetros
                    
                    
                   
                   
                    
            //Protocolo que vamos a utilizar
                    mensajeCFP.setProtocol(FIPANames.InteractionProtocol.FIPA_CONTRACT_NET);
                    mensajeCFP.setContent("Busco coche, ¿proponeis precios?");
                    //mensajeCFP.setSender(getAid());
                    //Indicamos el tiempo que esperaremos por las ofertas.
                    mensajeCFP.setReplyByDate(new Date(System.currentTimeMillis() + 1500000));
 
                    //Se añade el comportamiento que manejará las ofertas.
                    
                    this.addTask(new ManejoOpciones(this, mensajeCFP));
             
            }
           
                    Monitor adv = new Monitor();
                    
                    
                    adv.waiting();
 
             

 
    } // Fin del setup
 
    private class ManejoOpciones extends FIPAContractNetInitiator {
 
        public ManejoOpciones(QueueAgent agente, ACLMessage plantilla) {
            super(agente, plantilla);
        }
 
        //Manejador de proposiciones.
        protected void handlePropose(ACLMessage propuesta, ArrayList aceptadas) {
            System.out.printf("%s: Recibida oferta de autos %s. Ofrece un coche por %s euros.\n",
                this.myAgent.getName(), propuesta.getSender().getLocalName(), propuesta.getContent());
        }
 
        //Manejador de rechazos de proposiciones.
        protected void handleRefuse(ACLMessage rechazo) {
            System.out.printf("%s: Autos %s no tiene coches que ofrecer.\n",
                this.myAgent.getName(), rechazo.getSender().getLocalName());
        }
 
        //Manejador de respuestas de fallo.
        protected void handleFailure(ACLMessage fallo) {
          //  if (fallo.getSender().equals(myAgent.getAMS())) {
 
        //Esta notificacion viene del entorno de ejecución JADE (no existe el receptor)
                System.out.println("AMS: Esta venta de autos no existe o no es accesible");
            //} else {
                System.out.printf("%s: Autos %s ha sufrido un fallo.\n",
                    this.myAgent.getName(), fallo.getSender().getLocalName());
            //}
            //Falló, por lo tanto, no recibiremos respuesta desde ese agente
            Cliente.this.numeroDeOfertas--;
        }
 
        //Método colectivo llamado tras finalizar el tiempo de espera o recibir todas las propuestas.
        protected void handleAllResponses(ArrayList respuestas, ArrayList aceptados) {
 
        //Se comprueba si una venta de autos se pasó del plazo de envío de ofertas.
            if (respuestas.size() < numeroDeOfertas) {
                System.out.printf("%s: %d ventas de autos llegan tarde.\n",
                    this.myAgent.getName(), Cliente.this.numeroDeOfertas - respuestas.size());
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
                System.out.printf("%s: Decidido!!! Compro el coche de Autos %s\n",
                    this.myAgent.getName(), mejorAutos.getLocalName());
                aceptado.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
            }
        }
 
        //Manejador de los mensajes inform.
        protected void handleInform(ACLMessage inform) {
            System.out.printf("%s: Autos %s te ha enviado el contrato.\n",
                this.myAgent.getName(), inform.getSender().getLocalName());
        }
    }
}