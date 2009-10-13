package es.upv.dsic.gti_ia.magentix2;

import es.upv.dsic.gti_ia.fipa.*;
import es.upv.dsic.gti_ia.proto.Monitor;
import es.upv.dsic.gti_ia.proto.FIPARequestInitiator;
import es.upv.dsic.gti_ia.proto.FIPANames.InteractionProtocol;
public class OMSService {
	

	private String configuration;
	private String conection;
	private Monitor adv = new Monitor();
	private String salida = "";
	
	public OMSService(String OMSServiceDesciptionLocation)
	{
	
		this.configuration = OMSServiceDesciptionLocation;
	}
	
	/**
	 * Asigna el tipo de resultado de la salida, true o false
	 * @param valor
	 */
	public void setValor(String valor)
	{
		this.salida = valor;
	}
	
	
	
	public String LeaveRole(QueueAgent agente, OMSAgentDescription descripcion)
	{
	//	suggestedServiceCalls[2]=configuration+"RegisterNormProcess.owl NormID=norma1 normContent=FORBIDDEN_Member_REQUEST_acquireRole_MESSAGE(CONTENT(ROLE_'Payee'))";
		
		String call =  configuration +"LeaveRoleProcess.owl RoleID="+ descripcion.getRolID() +" UnitID="+descripcion.getUnitID();
		ACLMessage requestMsg = new ACLMessage(ACLMessage.REQUEST);
		requestMsg.setSender(agente.getAid());
		requestMsg.setContent(call);
		requestMsg.setProtocol(InteractionProtocol.FIPA_REQUEST);
		requestMsg.setReceiver(new AgentID("OMS","qpid","localhost",""));
		
		
		
		System.out.println("[QueryAgent]Sms to send: " + requestMsg.toString());
		System.out.println("[QueryAgent]Sending... ");
		//send(requestMsg);
		
		//crear un fil en el fipa request initiator
	     agente.setTask(new TestAgentClient(agente,requestMsg,this));
		
	     
	     this.adv.waiting();
	     
	     return this.salida;

	}
	
	
	public String InformAgentRole(QueueAgent agente)
	{
	//	suggestedServiceCalls[2]=configuration+"RegisterNormProcess.owl NormID=norma1 normContent=FORBIDDEN_Member_REQUEST_acquireRole_MESSAGE(CONTENT(ROLE_'Payee'))";
		
		String call =  configuration +"InformAgentRoleProcess.owl ";
		ACLMessage requestMsg = new ACLMessage(ACLMessage.REQUEST);
		requestMsg.setSender(agente.getAid());
		requestMsg.setContent(call);
		requestMsg.setProtocol(InteractionProtocol.FIPA_REQUEST);
		requestMsg.setReceiver(new AgentID("OMS","qpid","localhost",""));
		
	
		
		System.out.println("[QueryAgent]Sms to send: " + requestMsg.toString());
		System.out.println("[QueryAgent]Sending... ");
		//send(requestMsg);
		
		//crear un fil en el fipa request initiator
	     agente.setTask(new TestAgentClient(agente,requestMsg,this));
		
	     
	     this.adv.waiting();
	     
	     return this.salida;

	}
	
	public String InformMembers(QueueAgent agente,OMSAgentDescription descripcion)
	{
	//	suggestedServiceCalls[2]=configuration+"RegisterNormProcess.owl NormID=norma1 normContent=FORBIDDEN_Member_REQUEST_acquireRole_MESSAGE(CONTENT(ROLE_'Payee'))";
		
		String call =  configuration +"InformMembersProcess.owl RoleID="+ descripcion.getRolID() +" UnitID="+descripcion.getUnitID();
		ACLMessage requestMsg = new ACLMessage(ACLMessage.REQUEST);
		requestMsg.setSender(agente.getAid());
		requestMsg.setContent(call);
		requestMsg.setProtocol(InteractionProtocol.FIPA_REQUEST);
		requestMsg.setReceiver(new AgentID("OMS","qpid","localhost",""));
		
	
		
		System.out.println("[QueryAgent]Sms to send: " + requestMsg.toString());
		System.out.println("[QueryAgent]Sending... ");
		//send(requestMsg);
		
		//crear un fil en el fipa request initiator
	     agente.setTask(new TestAgentClient(agente,requestMsg,this));
		
	     
	     this.adv.waiting();
	     
	     return this.salida;

	}
	
	public String InformRoleNorms(QueueAgent agente,OMSAgentDescription descripcion)
	{
	//	suggestedServiceCalls[2]=configuration+"RegisterNormProcess.owl NormID=norma1 normContent=FORBIDDEN_Member_REQUEST_acquireRole_MESSAGE(CONTENT(ROLE_'Payee'))";
		
		String call =  configuration +"InformRoleNormsProcess.owl RoleID="+ descripcion.getRolID();
		ACLMessage requestMsg = new ACLMessage(ACLMessage.REQUEST);
		requestMsg.setSender(agente.getAid());
		requestMsg.setContent(call);
		requestMsg.setProtocol(InteractionProtocol.FIPA_REQUEST);
		requestMsg.setReceiver(new AgentID("OMS","qpid","localhost",""));
		
	
		
		System.out.println("[QueryAgent]Sms to send: " + requestMsg.toString());
		System.out.println("[QueryAgent]Sending... ");
		//send(requestMsg);
		
		//crear un fil en el fipa request initiator
	     agente.setTask(new TestAgentClient(agente,requestMsg,this));
		
	     
	     this.adv.waiting();
	     
	     return this.salida;

	}
	
	public String InformRoleProfiles(QueueAgent agente,OMSAgentDescription descripcion)
	{
	//	suggestedServiceCalls[2]=configuration+"RegisterNormProcess.owl NormID=norma1 normContent=FORBIDDEN_Member_REQUEST_acquireRole_MESSAGE(CONTENT(ROLE_'Payee'))";
		
		String call =  configuration +"InformRoleProfilesProcess.owl UnitID="+ descripcion.getUnitID();
		ACLMessage requestMsg = new ACLMessage(ACLMessage.REQUEST);
		requestMsg.setSender(agente.getAid());
		requestMsg.setContent(call);
		requestMsg.setProtocol(InteractionProtocol.FIPA_REQUEST);
		requestMsg.setReceiver(new AgentID("OMS","qpid","localhost",""));
		
	
		
		System.out.println("[QueryAgent]Sms to send: " + requestMsg.toString());
		System.out.println("[QueryAgent]Sending... ");
		//send(requestMsg);
		
		//crear un fil en el fipa request initiator
	     agente.setTask(new TestAgentClient(agente,requestMsg,this));
		
	     
	     this.adv.waiting();
	     
	     return this.salida;

	}
	
	
	public String InformUnit(QueueAgent agente,OMSAgentDescription descripcion)
	{
	//	suggestedServiceCalls[2]=configuration+"RegisterNormProcess.owl NormID=norma1 normContent=FORBIDDEN_Member_REQUEST_acquireRole_MESSAGE(CONTENT(ROLE_'Payee'))";
		
		String call =  configuration +"InformUnitProcess.owl UnitID="+ descripcion.getUnitID();
		ACLMessage requestMsg = new ACLMessage(ACLMessage.REQUEST);
		requestMsg.setSender(agente.getAid());
		requestMsg.setContent(call);
		requestMsg.setProtocol(InteractionProtocol.FIPA_REQUEST);
		requestMsg.setReceiver(new AgentID("OMS","qpid","localhost",""));
		
	
		
		System.out.println("[QueryAgent]Sms to send: " + requestMsg.toString());
		System.out.println("[QueryAgent]Sending... ");
		//send(requestMsg);
		
		//crear un fil en el fipa request initiator
	     agente.setTask(new TestAgentClient(agente,requestMsg,this));
		
	     
	     this.adv.waiting();
	     
	     return this.salida;

	}
	
	public String InformUnitRoles(QueueAgent agente,OMSAgentDescription descripcion)
	{
	//	suggestedServiceCalls[2]=configuration+"RegisterNormProcess.owl NormID=norma1 normContent=FORBIDDEN_Member_REQUEST_acquireRole_MESSAGE(CONTENT(ROLE_'Payee'))";
		
		String call =  configuration +"InformUnitRolesProcess.owl UnitID="+ descripcion.getUnitID();
		ACLMessage requestMsg = new ACLMessage(ACLMessage.REQUEST);
		requestMsg.setSender(agente.getAid());
		requestMsg.setContent(call);
		requestMsg.setProtocol(InteractionProtocol.FIPA_REQUEST);
		requestMsg.setReceiver(new AgentID("OMS","qpid","localhost",""));
		
	
		
		System.out.println("[QueryAgent]Sms to send: " + requestMsg.toString());
		System.out.println("[QueryAgent]Sending... ");
		//send(requestMsg);
		
		//crear un fil en el fipa request initiator
	     agente.setTask(new TestAgentClient(agente,requestMsg,this));
		
	     
	     this.adv.waiting();
	     
	     return this.salida;

	}
	
	public String QuantityMembers(QueueAgent agente,OMSAgentDescription descripcion)
	{
	//	suggestedServiceCalls[2]=configuration+"RegisterNormProcess.owl NormID=norma1 normContent=FORBIDDEN_Member_REQUEST_acquireRole_MESSAGE(CONTENT(ROLE_'Payee'))";
		
		String call =  configuration +"QuantityMembersProcess.owl RoleID="+ descripcion.getRolID() +" UnitID="+descripcion.getUnitID();
		ACLMessage requestMsg = new ACLMessage(ACLMessage.REQUEST);
		requestMsg.setSender(agente.getAid());
		requestMsg.setContent(call);
		requestMsg.setProtocol(InteractionProtocol.FIPA_REQUEST);
		requestMsg.setReceiver(new AgentID("OMS","qpid","localhost",""));
		
	
		
		System.out.println("[QueryAgent]Sms to send: " + requestMsg.toString());
		System.out.println("[QueryAgent]Sending... ");
		//send(requestMsg);
		
		//crear un fil en el fipa request initiator
	     agente.setTask(new TestAgentClient(agente,requestMsg,this));
		
	     
	     this.adv.waiting();
	     
	     return this.salida;

	}
	
	public String RegisterNorm(QueueAgent agente,String NormID, String NormContent)
	{
	//	suggestedServiceCalls[2]=configuration+"RegisterNormProcess.owl NormID=norma1 normContent=FORBIDDEN_Member_REQUEST_acquireRole_MESSAGE(CONTENT(ROLE_'Payee'))";
		
		String call =  configuration +"RegisterNormProcess.owl NormID="+ NormID +" NormContent="+NormContent;
		ACLMessage requestMsg = new ACLMessage(ACLMessage.REQUEST);
		requestMsg.setSender(agente.getAid());
		requestMsg.setContent(call);
		requestMsg.setProtocol(InteractionProtocol.FIPA_REQUEST);
		requestMsg.setReceiver(new AgentID("OMS","qpid","localhost",""));
		
	
		
		System.out.println("[QueryAgent]Sms to send: " + requestMsg.toString());
		System.out.println("[QueryAgent]Sending... ");
		//send(requestMsg);
		
		//crear un fil en el fipa request initiator
	     agente.setTask(new TestAgentClient(agente,requestMsg,this));
		
	     
	     this.adv.waiting();
	     
	     return this.salida;

	}
	
	public String RegisterRole(QueueAgent agente,String RegisterRoleInputRoleID, String UnitID,String Accessibility,String Position,String Visibility,String Inheritance)
	{
	//	suggestedServiceCalls[2]=configuration+"RegisterNormProcess.owl NormID=norma1 normContent=FORBIDDEN_Member_REQUEST_acquireRole_MESSAGE(CONTENT(ROLE_'Payee'))";
	
	  

		
		String call =  configuration +"RegisterRoleProcess.owl RegisterRoleInputRoleID="+ RegisterRoleInputRoleID +" UnitID="+UnitID+ " Accessibility="
		+Accessibility+ " Position="+Position+" Visibility="+Visibility+" Inheritance="+Inheritance;
		ACLMessage requestMsg = new ACLMessage(ACLMessage.REQUEST);
		requestMsg.setSender(agente.getAid());
		requestMsg.setContent(call);
		requestMsg.setProtocol(InteractionProtocol.FIPA_REQUEST);
		requestMsg.setReceiver(new AgentID("OMS","qpid","localhost",""));
		
	
		
		System.out.println("[QueryAgent]Sms to send: " + requestMsg.toString());
		System.out.println("[QueryAgent]Sending... ");
		//send(requestMsg);
		
		//crear un fil en el fipa request initiator
	     agente.setTask(new TestAgentClient(agente,requestMsg,this));
		
	     
	     this.adv.waiting();
	     
	     return this.salida;

	}
	
	public String RegisterUnit(QueueAgent agente,String UnitID,String Type,String Goal,String ParentUnitID)
	{
	//	suggestedServiceCalls[2]=configuration+"RegisterNormProcess.owl NormID=norma1 normContent=FORBIDDEN_Member_REQUEST_acquireRole_MESSAGE(CONTENT(ROLE_'Payee'))";
	
	  

		
		String call =  configuration +"RegisterUnitProcess.owl  UnitID="+UnitID+ " Type="
		+Type+ " Goal="+Goal+" ParentUnitID="+ParentUnitID;
		ACLMessage requestMsg = new ACLMessage(ACLMessage.REQUEST);
		requestMsg.setSender(agente.getAid());
		requestMsg.setContent(call);
		requestMsg.setProtocol(InteractionProtocol.FIPA_REQUEST);
		requestMsg.setReceiver(new AgentID("OMS","qpid","localhost",""));
		
	
		
		System.out.println("[QueryAgent]Sms to send: " + requestMsg.toString());
		System.out.println("[QueryAgent]Sending... ");
		//send(requestMsg);
		
		//crear un fil en el fipa request initiator
	     agente.setTask(new TestAgentClient(agente,requestMsg,this));
		
	     
	     this.adv.waiting();
	     
	     return this.salida;

	}
	
	public String DeregisterNorm(QueueAgent agente,String NormID)
	{
	//	suggestedServiceCalls[2]=configuration+"RegisterNormProcess.owl NormID=norma1 normContent=FORBIDDEN_Member_REQUEST_acquireRole_MESSAGE(CONTENT(ROLE_'Payee'))";
	
	  

		
		String call =  configuration +"DeregisterNormProcess.owl  NormID="+NormID;
		ACLMessage requestMsg = new ACLMessage(ACLMessage.REQUEST);
		requestMsg.setSender(agente.getAid());
		requestMsg.setContent(call);
		requestMsg.setProtocol(InteractionProtocol.FIPA_REQUEST);
		requestMsg.setReceiver(new AgentID("OMS","qpid","localhost",""));
		
	
		
		System.out.println("[QueryAgent]Sms to send: " + requestMsg.toString());
		System.out.println("[QueryAgent]Sending... ");
		//send(requestMsg);
		
		//crear un fil en el fipa request initiator
	     agente.setTask(new TestAgentClient(agente,requestMsg,this));
		
	     
	     this.adv.waiting();
	     
	     return this.salida;

	}
	public String DeregisterRole(QueueAgent agente,String RoleID, String UnitID)
	{
	//	suggestedServiceCalls[2]=configuration+"RegisterNormProcess.owl NormID=norma1 normContent=FORBIDDEN_Member_REQUEST_acquireRole_MESSAGE(CONTENT(ROLE_'Payee'))";
	
	  

		
		String call =  configuration +"DeregisterRoleProcess.owl  RoleID="+RoleID + " UnitID="+UnitID;
		ACLMessage requestMsg = new ACLMessage(ACLMessage.REQUEST);
		requestMsg.setSender(agente.getAid());
		requestMsg.setContent(call);
		requestMsg.setProtocol(InteractionProtocol.FIPA_REQUEST);
		requestMsg.setReceiver(new AgentID("OMS","qpid","localhost",""));
		
	
		
		System.out.println("[QueryAgent]Sms to send: " + requestMsg.toString());
		System.out.println("[QueryAgent]Sending... ");
		//send(requestMsg);
		
		//crear un fil en el fipa request initiator
	     agente.setTask(new TestAgentClient(agente,requestMsg,this));
		
	     
	     this.adv.waiting();
	     
	     return this.salida;

	}
	public String DeregisterUnit(QueueAgent agente,String UnitID)
	{
	//	suggestedServiceCalls[2]=configuration+"RegisterNormProcess.owl NormID=norma1 normContent=FORBIDDEN_Member_REQUEST_acquireRole_MESSAGE(CONTENT(ROLE_'Payee'))";
	
	  

		
		String call =  configuration +"DeregisterNormProcess.owl  UnitID="+UnitID;
		ACLMessage requestMsg = new ACLMessage(ACLMessage.REQUEST);
		requestMsg.setSender(agente.getAid());
		requestMsg.setContent(call);
		requestMsg.setProtocol(InteractionProtocol.FIPA_REQUEST);
		requestMsg.setReceiver(new AgentID("OMS","qpid","localhost",""));
		
	
		
		System.out.println("[QueryAgent]Sms to send: " + requestMsg.toString());
		System.out.println("[QueryAgent]Sending... ");
		//send(requestMsg);
		
		//crear un fil en el fipa request initiator
	     agente.setTask(new TestAgentClient(agente,requestMsg,this));
		
	     
	     this.adv.waiting();
	     
	     return this.salida;

	}
	public String Expulse(QueueAgent agente, OMSAgentDescription descripcion)
	{
	//	suggestedServiceCalls[2]=configuration+"RegisterNormProcess.owl NormID=norma1 normContent=FORBIDDEN_Member_REQUEST_acquireRole_MESSAGE(CONTENT(ROLE_'Payee'))";
		
		String call =  configuration +"ExpulseProcess.owl RoleID="+ descripcion.getRolID() +" UnitID="+descripcion.getUnitID();
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
	     agente.setTask(new TestAgentClient(agente,requestMsg,this));
		
	     
	     this.adv.waiting();
	     
	     return this.salida;

	}
	
	
	
	
	/**
	 * Registra un agente en la organizacion
	 * @param agente agente a registrar
	 * @param descripcion inidica que rol y en que organizacion entrara el agente
	 * @return
	 */
	public String AcquireRole(QueueAgent agente, OMSAgentDescription descripcion)
	{
		//montar string de conexion 
		//Enviamos el mensaje
	    
		
		
		String call =  configuration +"AcquireRoleProcess.owl RoleID="+ descripcion.getRolID() +" UnitID="+descripcion.getUnitID();
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
	     agente.setTask(new TestAgentClient(agente,requestMsg,this));
		
	     
	     this.adv.waiting();
	     
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
                this.oms.setValor(myAgent.getName()  + ": Oh no! " + 
                        msg.getSender().getLocalName() + 
                        " has rejected my proposal.");
    			this.oms.adv.advise();
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
   
                
         
            	String arg2 = msg.getContent();
    			arg2 = arg2.substring((arg2.lastIndexOf("=")) + 1,
    					arg2.length() - 1);
    			
                //si ha salido bien despierto al agente
                if 	(arg1.equals("Ok"))
                {
                	this.oms.setValor(arg1);
                }
                else
                {
                	//vemos que tipo de error
                	this.oms.setValor(arg1 +" "+ arg2);
                }
                
                this.oms.adv.advise();
                
            
                	
                
        }
        
        protected  void handleNotUnderstood(ACLMessage msg){
                System.out.println(myAgent.getName()  + ":"
                + msg.getSender().getLocalName() + 
                " has indicated that they didn't understand.");
                this.oms.setValor(myAgent.getName()  + ":"
                        + msg.getSender().getLocalName() + 
                        " has indicated that they didn't understand.");
    			this.oms.adv.advise();
        }
        
        protected  void  handleOutOfSequence(ACLMessage msg) {
                System.out.println(myAgent.getName()  + ":"
                + msg.getSender().getLocalName() + 
                " has send me a message which i wasn't" + 
                " expecting in this conversation");
                this.oms.setValor(myAgent.getName()  + ":"
                        + msg.getSender().getLocalName() + 
                        " has send me a message which i wasn't" + 
                        " expecting in this conversation");
    			this.oms.adv.advise();
        }}

}
