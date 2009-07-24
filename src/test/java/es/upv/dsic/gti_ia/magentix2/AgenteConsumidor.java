package es.upv.dsic.gti_ia.magentix2;

import java.util.concurrent.LinkedBlockingQueue;
import org.apache.qpid.transport.Connection;
import org.apache.qpid.transport.MessageTransfer;
import es.upv.dsic.gti_ia.magentix2.Message;

public class AgenteConsumidor extends SingleAgent{
	
	LinkedBlockingQueue<MessageTransfer> internalQueue;
	
	public AgenteConsumidor(String name, Connection connection) {
		super(name, connection);
	}
	
	public void execute(){
		System.out.println("Arranco, soy "+getName());
		Message msg = new Message();
		msg = receiveMessage();
		Cosa cosa = new Cosa();
		cosa = (Cosa)msg.getByteBuffer();
		System.out.println(cosa.cadena);
	}
}
