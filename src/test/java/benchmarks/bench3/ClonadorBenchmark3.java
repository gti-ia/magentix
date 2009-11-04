package benchmarks.bench3;

import org.apache.qpid.transport.Connection;

import s.dsic.gti_ia.fipa.AgentID;

import _BaseAgent_Example.BridgeAgentInOut;
import _BaseAgent_Example.SingleAgent;

/**
 * This class is responsible for creating receiver's agents like the first one.
 * 
 * @author Sergio, Ricard
 * 
 * FALTA PERMITIR INVOCAR A NUESTROS AGENTES CON UN ARGUMENTO !!!
 * 
 */
public class ClonadorBenchmark3 extends SingleAgent {

	int nagents;

	public ClonadorBenchmark3(AgentID aid, Connection connection, int nagents) {
		super(aid, connection);
		this.nagents = nagents;
	}

	public void execute() {

		/*
		 * if(args.length != 1) { System.out.println("Error, Debe invocar la
		 * clase así: clonador\"(\" nreceptores \")\""); System.exit(1); }
		 */
		String classe = "receptor";
		// int nagents = Integer.parseInt(args[0].toString());

		for (int i = 1; i <= nagents; i++) {
			try {
				ReceptorBenchmark3 agenteReceptor = new ReceptorBenchmark3(
						new AgentID(classe + i, this.getAid().protocol, this
								.getAid().host, this.getAid().port + 1), this
								.getConnection());

				agenteReceptor.start();

			} catch (Exception e) {
				System.out
						.println("Error ClonadorBenchmarks3, en la clonación de agentes receivers");
			}

		}
	}

}
