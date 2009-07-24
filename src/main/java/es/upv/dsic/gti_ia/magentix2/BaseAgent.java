package es.upv.dsic.gti_ia.magentix2;

import org.apache.qpid.transport.Connection;
import org.apache.qpid.transport.DeliveryProperties;
import org.apache.qpid.transport.Header;
import org.apache.qpid.transport.MessageAcceptMode;
import org.apache.qpid.transport.MessageAcquireMode;
import org.apache.qpid.transport.MessageCreditUnit;
import org.apache.qpid.transport.MessageTransfer;
import org.apache.qpid.transport.Option;
import org.apache.qpid.transport.Session;
import org.apache.qpid.transport.SessionException;
import org.apache.qpid.transport.SessionListener;

public class BaseAgent implements Runnable{

	/*Atributos*/
	private String name;
	private Connection connection;
	private Session session;
	private Thread myThread;
	
	private class Listener implements SessionListener{
		public void opened(Session ssn) {}
	
	    public void resumed(Session ssn) {}
	
	    public void message(Session ssn, MessageTransfer xfr)
	    {
	    	//ejecutamos codigo creado por el usuario
	    	onMessage(ssn, xfr);
	    }
	
	    public void exception(Session ssn, SessionException exc)
	    {
	        exc.printStackTrace();
	    }
	
	    public void closed(Session ssn) {}	
	}	
	private Listener listener;
	
	/*Metodos*/
	public BaseAgent(String name, Connection connection){
		this.name = name;
		this.connection = connection;
		this.session = createSession();
		this.listener = new Listener();
		myThread = new Thread(this);
		createExchange();
		createQueue();
		createBind();
		createSubscription();
	}
		
	//Creamos la sesion para el agente
	private Session createSession(){
		 Session session = this.connection.createSession(0);
		 return session;
	}
	
	//Creamos intercambiador para el agente
	private void createExchange(){
		this.session.exchangeDeclare(this.name, "fanout", null, null, Option.DURABLE);
	}
	
	//Creamos cola para el agente
	private void createQueue(){
		this.session.queueDeclare(this.name, null, null);
	}
	
	//Vinculamos cola e intercambiador del agente
	private void createBind(){
		this.session.exchangeBind(this.name, this.name, null, null);
	}
	
	//Creamos la subscripcion del listener del agente
	private void createSubscription(){
		this.session.setSessionListener(this.listener);

        // create a subscription
		this.session.messageSubscribe(this.name,
                                 "listener_destination",
                                 MessageAcceptMode.NONE,
                                 MessageAcquireMode.PRE_ACQUIRED,
                                 null, 0, null);
		
		//Declaramos un credito ilimitado tanto para el numero de bytes que recibe la sesion como para el numero de mensajes
		this.session.messageFlow("listener_destination", MessageCreditUnit.BYTE, Session.UNLIMITED_CREDIT);
        this.session.messageFlow("listener_destination", MessageCreditUnit.MESSAGE, Session.UNLIMITED_CREDIT);
	}
	
	//Enviamos el mensaje msg al agente destination
	protected void send(Message msg){
		//Recuperamos el destinatario de las cabeceras del mensaje, si no está especificado devolvemos error
		if(msg.getHeader("destination") == null){
			System.out.println("Error, no ha especificado destinatario. Mensaje: "+msg.toString());
			return;
		}
		
		if(msg.getHeader("type") == null){
			System.out.println("Error, no ha especificado tipo de mensaje. Mensaje: "+msg.toString());
			return;
		}
					
		//No es necesario especificar una routing_key ya que el exchange es del tipo fanout, lo pongo por completitud
		DeliveryProperties deliveryProps = new DeliveryProperties();
        deliveryProps.setRoutingKey("routing_key");
        
        MessageTransfer xfr = new MessageTransfer();
        xfr.destination(msg.getHeader("destination"));
        xfr.acceptMode(MessageAcceptMode.EXPLICIT);
        xfr.acquireMode(MessageAcquireMode.PRE_ACQUIRED);
        xfr.header(new Header(deliveryProps));
        if(msg.getHeader("type") == "String"){
        	xfr.setBody(msg.body);
        	session.messageTransfer(xfr.getDestination(), xfr.getAcceptMode(), xfr.getAcquireMode(), xfr.getHeader(), xfr.getBodyString());
        }
        else{
        	xfr.setBody(msg.buffer);
        	session.messageTransfer(xfr.getDestination(), xfr.getAcceptMode(), xfr.getAcquireMode(), xfr.getHeader(), xfr.getBody());
        }
         
	}
			
	public String getName(){
		return this.name;
	}
	
	//codigo que ejecutará el agente, a rellenar por el usuario
	protected void execute(){
		
	}
	
	//codigo que ejecutará el agente cuando reciba un mensaje, a rellenar por el usuario
	protected void onMessage(Session ssn, MessageTransfer xfr){
			
	}
	
	//codigo de terminacion del agente
	protected void terminate(){
		session.exchangeDelete(name);
		session.queueDelete(name);
		session.close();
	}
	
	//hilo del agente
	public void run(){
		execute();
		terminate();
	}
	
	public void start(){
		myThread.start();
	}
}
