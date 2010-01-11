package benchmarks.bench3;

import org.apache.qpid.transport.Connection;

import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.SingleAgent;


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

	public ClonadorBenchmark3(AgentID aid, Connection connection, int nagents) throws Exception {
		super(aid);
		this.nagents = nagents;
	}

	public void execute() {

		/*
		 * if(args.length != 1) { System.out.println("Error, Debe invocar la
		 * clase as�: clonador\"(\" nreceptores \")\""); System.exit(1); }
		 */
		String classe = "receptor";
		// int nagents = Integer.parseInt(args[0].toString());

		for (int i = 1; i <= nagents; i++) {
			try {
				ReceptorBenchmark3 agenteReceptor = new ReceptorBenchmark3(
						new AgentID(classe + i, this.getAid().protocol, this
								.getAid().host, this.getAid().port + 1));

				agenteReceptor.start();

			} catch (Exception e) {
				System.out
						.println("Error ClonadorBenchmarks3, en la clonaci�n de agentes receivers");
			}

		}
	}

}
