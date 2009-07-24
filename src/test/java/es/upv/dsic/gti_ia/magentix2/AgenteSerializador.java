package es.upv.dsic.gti_ia.magentix2;

import org.apache.qpid.transport.Connection;
import es.upv.dsic.gti_ia.magentix2.BaseAgent;
import es.upv.dsic.gti_ia.magentix2.Message;

public class AgenteSerializador extends BaseAgent{
	
	public AgenteSerializador(String name, Connection connection) {
		super(name, connection);
	}
	
	public void execute(){
		System.out.println("Arranco, soy "+getName());
		Message msg = new Message();
		msg.setHeader("destination", "AgenteConsumidor");
		msg.setHeader("type","byteBuffer");
		Cosa cosa = new Cosa();
		msg.setByteBuffer(cosa, 1024);
        send(msg);
	}
}
