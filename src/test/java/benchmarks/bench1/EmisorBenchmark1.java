package benchmarks.bench1;

import org.apache.qpid.transport.Connection;
import es.upv.dsic.gti_ia.fipa.ACLMessage;
import es.upv.dsic.gti_ia.fipa.AgentID;
import es.upv.dsic.gti_ia.magentix2.SingleAgent;

public class EmisorBenchmark1 extends SingleAgent {
	int nmsgtot;	//nombre total de missatges a enviar
	int completat=0;
	int tmsg;		//tamany del missatge
    int ntotal=0;	//nombre total d'agents
    int nemisor;	//nombre del agent
    int nreceptor;	//nombre del primer destinatari
    
	public EmisorBenchmark1(AgentID aid, Connection connection) {
		super(aid, connection);
	}
	
	public void execute(){
		//Enviem missatge de Ready al agent controlador
		AgentID controlador = new AgentID("controlador", "http", "localhost", "8080");
		ACLMessage msgcont = new ACLMessage(ACLMessage.REQUEST);
		msgcont.setContent("Ready");
		msgcont.setReceiver(controlador);
		msgcont.setSender(this.getAid());
		
		
		receiveACLMessage();			//esperem missatge Start des d'el controlador
		
		//creem missatge i contingut
		String cadena = "";
		ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
		for(int i=0;i<tmsg;i++)
			cadena = cadena + "a";
		msg.setContent(cadena);
		//destinatari
		AgentID receiver = new AgentID();
		receiver.protocol = "http";
		receiver.port = "8080";
		nreceptor = (nemisor % ntotal) + 1;
		receiver.name = "receptor"+nreceptor;
		receiver.host = "host"+nreceptor;
		while(completat < nmsgtot){
			send(msg);					//enviem missatge
			receiveACLMessage();		//esperem a la resposta del receptor
			completat++;
			nreceptor = (nreceptor % ntotal) + 1;
			if(nreceptor == nemisor) nreceptor = (nreceptor % ntotal) + 1;
			receiver.name = "receptor"+nreceptor;
			receiver.host = "host"+nreceptor;
		}		
		send(msgcont);					//quan acaba la prova enviem missatge al controlador
	}
}
