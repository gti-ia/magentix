package es.upv.dsic.gti_ia.magentix2;
import org.apache.qpid.transport.Connection;

public class Prueba {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Connection con = new Connection();
        con.connect("localhost", 5672, "test", "guest", "guest",false);
		AgenteSerializador agente = new AgenteSerializador("agenteSerializador", con);
		//AgenteHola agente3 = new AgenteHola("agenteHola2",con);
		AgenteConsumidor agente2 = new AgenteConsumidor("AgenteConsumidor", con);
		agente2.start();
		agente.start();
	}
}
