package es.upv.dsic.gti_ia.magentix2;

import es.upv.dsic.gti_ia.fipa.*;
import es.upv.dsic.gti_ia.proto.Adviser;
import es.upv.dsic.gti_ia.proto.FIPARequestInitiator;
import es.upv.dsic.gti_ia.proto.FIPANames.InteractionProtocol;
public class OMSService {
	

	private String configuration;
	private String conection;
	private Adviser adv = new Adviser();
	private boolean salida = true;
	
	public OMSService(String OMSServiceDesciptionLocation)
	{
	
		this.configuration = OMSServiceDesciptionLocation;
	}
	
	/**
	 * Asigna el tipo de resultado de la salida, true o false
	 * @param valor
	 */
	public void setValor(boolean valor)
	{
		this.salida = valor;
	}
	
	
	public boolean RegisterNorm(QueueAgent agente, OMSAgentDescription descripcion)
	{
	//	suggestedServiceCalls[2]=configuration+"RegisterNormProcess.owl NormID=norma1 normContent=FORBIDDEN_Member_REQUEST_acquireRole_MESSAGE(CONTENT(ROLE_'Payee'))";
		return salida;
	}
	
	
	
	/**
	 * Registra un agente en la organizacion
	 * @param agente agente a registrar
	 * @param descripcion inidica que rol y en que organizacion entrara el agente
	 * @return
	 */
	public boolean register(QueueAgent agente, OMSAgentDescription descripcion)
	{
		//montar string de conexion 
		//Enviamos el mensaje
	    
		
		
		String call =  configuration +"AcquireRoleProcess.owl RoleID="+ descripcion.getRolID() +" UnitID="+descripcion.getUnitID()+" AgentID="+agente.getName();
		ACLMessage requestMsg = new ACLMessage(ACLMessage.REQUEST);
		requestMsg.setSender(agente.getAid());
		requestMsg.setContent(call);
		requestMsg.setProtocol(InteractionProtocol.FIPA_REQUEST);
		requestMsg.setReceiver(new AgentID("OMS","qpid","localhost",""));
		
		
		System.out.println("Destinatario del mensaje: " + requestMsg.getReceiver().toString());
		
		System.out.println("[QueryAgent]Sms to send: " + requestMsg.toString());
		System.out.println("[QueryAgent]Sending... ");
		//send(requestMsg);
		
		//crear un fil en el fipa request initiator
	     agente.setTarea(new TestAgentClient(agente,requestMsg,this));
		
	     
	     this.adv.esperar();
	     
	     return this.salida;
		//registar el agente en la plataforma
	
	}
	
	/**
	 * TestAgentClient handles the messages received from the SF
	 */
	static class TestAgentClient extends FIPARequestInitiator{
		QueueAgent agent;
		OMSService oms;
    
        protected TestAgentClient(QueueAgent agent,ACLMessage msg, OMSService oms){
        	super(agent,msg);
        		this.agent=agent;
        		this.oms = oms;
                
        }
        
        protected  void   handleAgree(ACLMessage msg) {
                System.out.println(myAgent.getName()  + ": OOH! " + 
                msg.getSender().getLocalName() +
                " Has agreed to excute the service!");
        }
        
        
        protected  void   handleRefuse(ACLMessage msg) {
                System.out.println(myAgent.getName()  + ": Oh no! " + 
                msg.getSender().getLocalName() + 
                " has rejected my proposal.");
        }
        
        protected  void   handleInform(ACLMessage msg) {
                System.out.println(myAgent.getName()  + ":" + 
                msg.getSender().getLocalName() + 
                " has informed me of the status of my request." +
                " They said : " + msg.getContent());
                
                
                String patron = msg.getContent().substring(0,msg.getContent().indexOf("=")); 
                String arg1 = "";
                arg1 = msg.getContent().substring(msg.getContent().indexOf("=") +1 , msg.getContent().length());
                arg1 =   arg1.substring(arg1.indexOf("=")+1,arg1.indexOf(","));
                System.out.println("Llega aqui el valor es:"+ patron);
                
                if (patron.equals("AcquireRoleProcess"))
                {
                	
                //si ha salido bien despierto al agente
                if 	(arg1.equals("Ok"))
                {
                	this.oms.setValor(true);
                }
                else
                	this.oms.setValor(false);
                
                	this.oms.adv.dar();
                }
                	
                
        }
        
        protected  void handleNotUnderstood(ACLMessage msg){
                System.out.println(myAgent.getName()  + ":"
                + msg.getSender().getLocalName() + 
                " has indicated that they didn't understand.");
        }
        
        protected  void  handleOutOfSequence(ACLMessage msg) {
                System.out.println(myAgent.getName()  + ":"
                + msg.getSender().getLocalName() + 
                " has send me a message which i wasn't" + 
                " expecting in this conversation");
        }}

}
