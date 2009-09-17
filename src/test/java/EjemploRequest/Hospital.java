package EjemploRequest;


import org.apache.qpid.transport.Connection;


import es.upv.dsic.gti_ia.fipa.AgentID;
import es.upv.dsic.gti_ia.magentix2.*;

import es.upv.dsic.gti_ia.fipa.ACLMessage;

import es.upv.dsic.gti_ia.proto.FIPARequestResponder;
import es.upv.dsic.gti_ia.magentix2.QueueAgent;
import es.upv.dsic.gti_ia.proto.FIPANames.InteractionProtocol;
import es.upv.dsic.gti_ia.proto.MessageTemplate;
import es.upv.dsic.gti_ia.proto.*;
import java.util.StringTokenizer;

//import Pruebas.AgenteOMS.OMSResponder;

public class Hospital extends QueueAgent {
	
	
    public double DISTANCIA_MAX;
    private Principal_Grafico frame;
    
    public Hospital(AgentID aid, Connection connection, Principal_Grafico frame)
    {

    	super(aid, connection);
        this.frame = frame;
    
    }
    
    public Hospital(AgentID aid, Connection connection)
    {

    	super(aid, connection);
        
    
    }
    
    
    protected void execute()
    {
        DISTANCIA_MAX=(Math.random()*10);
        
        
        
        MessageTemplate plantilla = new MessageTemplate(InteractionProtocol.FIPA_REQUEST);
 
        
        
    	ManejadorResponder responder = new ManejadorResponder(this, plantilla);
    	
    	frame.getTextArea(1).append("Hospital "+this.getName()+": Esperando avisos...\n");

		System.out.println("Hospital "+this.getName()+": Esperando avisos...");

    	do{
            		responder.action();
    		
    	}while(true);
    }
 
    class ManejadorResponder extends FIPARequestResponder
    {
        public ManejadorResponder(QueueAgent a,MessageTemplate mt) {
            super(a,mt);
        }
 
        protected ACLMessage prepareResponse(ACLMessage request)throws NotUnderstoodException, RefuseException
        {
            frame.getTextArea(1).append("Hospital "+getName()+": Hemos recibido una llamada de " + request.getSender().name + " diciendo que ha visto un accidente.\n");
            System.out.println("Hospital "+getName()+": Hemos recibido una llamada de " + request.getSender().name + " diciendo que ha visto un accidente.");
            StringTokenizer st=new StringTokenizer(request.getContent());
            String contenido=st.nextToken();
            if(contenido.equalsIgnoreCase("accidente"))
            {
                st.nextToken();
                int distancia=Integer.parseInt(st.nextToken());
                if (distancia<DISTANCIA_MAX)
                {
                    frame.getTextArea(1).append("Hospital "+getName()+": Vamos echando chispas!!!\n");
                    System.out.println("Hospital "+getName()+": Vamos echando chispas!!!");
                    ACLMessage agree = request.createReply();
                    agree.setPerformative(ACLMessage.AGREE);
                    return agree;
                }
                else
                {
                    frame.getTextArea(1).append("Hospital "+getName()+": Accidente fuera de nuestro radio de accion. No llegaremos a tiempo!!!\n");
                    System.out.println("Hospital "+getName()+": Accidente fuera de nuestro radio de accion. No llegaremos a tiempo!!!");
                    throw new RefuseException("Accidente demasiado lejos");
                }
            }
            else throw new NotUnderstoodException("Hospital manda un mensaje que no puedo entender.");
        }
 
        protected ACLMessage prepareResultNotification(ACLMessage request,ACLMessage response) throws FailureException
        {
            if (Math.random() > 0.2) {
                 frame.getTextArea(1).append("Hospital "+getName()+": Han vuelto de atender el accidente.\n");
                System.out.println("Hospital "+getName()+": Han vuelto de atender el accidente.");
                ACLMessage inform = request.createReply();
                inform.setPerformative(ACLMessage.INFORM);
                return inform;
            }
            else
            {
                 frame.getTextArea(1).append("Hospital "+getName()+": Han hecho todo lo posible, lo sentimos.\n");
                System.out.println("Hospital "+getName()+": Han hecho todo lo posible, lo sentimos.");
                throw new FailureException("Han hecho todo lo posible");
            }
        }
    }
	


}
