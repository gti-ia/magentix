package EjemploRequest;

import org.apache.qpid.transport.Connection;



import org.apache.qpid.transport.Connection;



import es.upv.dsic.gti_ia.fipa.ACLMessage;
import es.upv.dsic.gti_ia.fipa.AgentID;
import es.upv.dsic.gti_ia.proto.FIPANames.InteractionProtocol;
import es.upv.dsic.gti_ia.proto.FIPARequestInitiator;
import es.upv.dsic.gti_ia.magentix2.QueueAgent;



	
	public class Testigo extends QueueAgent {


        public Principal_Grafico frame;
        private boolean condicion = true;
        private Llamadas llamada;
        int con=0;
        //private  ManejadorInitiator iniciador = null;

	    public Testigo(AgentID aid, Connection connection, Principal_Grafico _frame, Llamadas _llamada)
	    {
	    	
	    	super(aid, connection);

            this.frame = _frame;
            this.llamada = _llamada;
	    
	    
	    }
	    
	    public Testigo(AgentID aid, Connection connection)
	    {
	    	
	    	super(aid, connection);

          
	    
	    
	    }
	    
	    
	    public void enviarMensaje(int i)
	    {
	        //Object[] args = getArguments();
	        //if (args != null && args.length > 0) {
	    	
	    	
	    	frame.getTextArea(2).append("Número accidente: "+i+" He visto un accidente! Solicitando ayuda a varios hospitales...\n");
            System.out.println("He visto un accidente! Solicitando ayuda a varios hospitales...");
            
           	ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
	           // for (int i = 0; i < args.length; ++i)
	            msg.setReceiver(new AgentID("OMS","qpid","localhost",""));
	            msg.setProtocol(InteractionProtocol.FIPA_REQUEST);
	            msg.setContent("accidente a "+frame.getTextField().getText()+" kms");
	            msg.setSender(this.getAid());
         
	            this.setTarea( new ManejadorInitiator(this,msg));
	
	    }
	    
	    public void detenerHilo()
	    {
	    	this.condicion = false;
	    }
	    
	    protected  void execute()
	    {
	    			do{
	    				
	               this.enviarMensaje(1);
	    			
	                     	
	               llamada.esperar();
	            
	        		}while(condicion);
	    			
	    }
	 

	    
	    class ManejadorInitiator extends FIPARequestInitiator
	    {
	        public ManejadorInitiator(QueueAgent a,ACLMessage msg) {
	            super(a,msg);
	        }
	 
	        protected void handleAgree(ACLMessage agree)
	        {
	        	
                frame.getTextArea(2).append("Hospital " + agree.getSender().getLocalName()
	                    + " informa que han salido a atender el accidente.\n");
	            System.out.println("!!!!Hospital " + agree.getSender().getLocalName()
	                    + " informa que han salido a atender el accidente.");
	        }
	 
	        protected void handleRefuse(ACLMessage refuse)
	        {
                                frame.getTextArea(2).append("Hospital " + refuse.getSender().getLocalName()
	                    + " responde que el accidente esta fuera de su radio de accion. No llegaremos a tiempo!!!\n");

	            System.out.println("!!!!Hospital " + refuse.getSender().getLocalName()
	                    + " responde que el accidente esta fuera de su radio de accion. No llegaremos a tiempo!!!");
	        }
	 
	        protected void handleNotUnderstood(ACLMessage notUnderstood)
	        {
                                frame.getTextArea(2).append("Hospital " + notUnderstood.getSender().getLocalName()
	                    + " no puede entender el mensaje.\n");

	            System.out.println("!!!!Hospital " + notUnderstood.getSender().getLocalName()
	                    + " no puede entender el mensaje.");
	        }
	 
	        protected void handleInform(ACLMessage inform)
	        {
                                frame.getTextArea(2).append("Hospital " + inform.getSender().getLocalName()
	                    + " informa que han atendido el accidente.\n");

	            System.out.println("!!!!!!Hospital " + inform.getSender().getLocalName()
	                    + " informa que han atendido el accidente.");
	        }
	 
	        protected void handleFailure(ACLMessage fallo)
	        {
	            if (fallo.getSender().name.equals(myAgent.getName())) {
                                    frame.getTextArea(2).append("Alguna de los hospitales no existe\n");
	                System.out.println("Alguna de los hospitales no existe");
	            }
	            else
	            {
                                    frame.getTextArea(2).append("Fallo en el hospital " + fallo.getSender().getLocalName()
	                        + ": " + fallo.getContent().substring(0, fallo.getContent().length())+".\n");
	                System.out.println("Fallo en el hospital " + fallo.getSender().getLocalName()
	                        + ": " + fallo.getContent().substring(0, fallo.getContent().length()));
	            }
	        }
	    }
}
