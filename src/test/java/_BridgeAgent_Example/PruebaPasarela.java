package _BridgeAgent_Example;
import org.apache.qpid.transport.Connection;




import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.BridgeAgentInOut;



public class PruebaPasarela {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Connection con = new Connection();
        con.connect("rilpefo.dsic.upv.es", 5672, "test", "guest", "guest",false);
        //http por que el agente va a enviar un mensaje hacia el exterior
     try{
    	 BridgeAgentInOut agente2 = new BridgeAgentInOut(new AgentID("agentepasarela", "qpid", "localhost","8080"));
        AgenteSergio agente = new AgenteSergio(new AgentID("agentehola", "qpid", "localhost","8080"));
		
		agente2.start();
		agente.start();
	}catch(Exception e){
		System.out.println("Error");
	}
		
	//	CopyOfAgenteHola agente3 = new CopyOfAgenteHola(new AgentID("agtente3", "http", "localhost","8080"),con);
	//	agente3.start();
	}
}
