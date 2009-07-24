package es.upv.dsic.gti_ia.magentix2;

import org.apache.qpid.transport.Connection;
import org.apache.qpid.transport.MessageTransfer;
import org.apache.qpid.transport.Session;

import es.upv.dsic.gti_ia.magentix2.BaseAgent;
import es.upv.dsic.gti_ia.magentix2.Message;

public class AgenteHola extends BaseAgent {
	
	public AgenteHola(String name, Connection connection) {
		super(name, connection);
	}
	
	public void execute(){
		System.out.println("Arranco, soy "+getName());
		Message msg = new Message();
		msg.setHeader("destination", "AgenteConsumidor");
		msg.setHeader("type","String");
		for(int i = 0; i< 10;i++){
			msg.body = "Hola soy agente "+ getName()+" mensaje numero "+i;
			send(msg);
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void onMessage(Session ssn, MessageTransfer xfr){
		System.out.println("Mensaje: " + xfr);	
	}
}
