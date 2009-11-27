package benchmarks.bench4;

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
public class ClonadorBenchmark4 extends SingleAgent {

	int nagents;
	String param[] = new String[10];
	String classe1;
	int nagent;
	int inici;
	int nagentstot;

	public ClonadorBenchmark4(AgentID aid,
			String classe, int nagents, int nagentslocals, int nmsg,
			int nmsgtot, int tmsg) throws Exception {
		super(aid);
		this.nagents = nagents;
		classe1 = classe;
		nagent = Integer.parseInt(this.getAid().name.substring(8));
		// int nagents = nagentslocals;

		inici = nagents * (nagent - 1);

		nagentstot = nagentslocals * nagents;

		param[0] = "" + nagentstot;// nombre total d'agents
		param[1] = (Integer.valueOf(nmsg)).toString();// nombre de missatges
														// significatius
		param[2] = (Integer.valueOf(nmsgtot)).toString();// nombre de
															// missatges total
		param[3] = (Integer.valueOf(tmsg)).toString();// tamany missatges

	}

	public void execute() {

		/*
		 * if(args.length != 1) { System.out.println("Error, Debe invocar la
		 * clase así: clonador\"(\" nreceptores \")\""); System.exit(1); }
		 */
	//	String classe = "receptor";
		// int nagents = Integer.parseInt(args[0].toString());

		for (int i = 1; i <= nagents; i++) {
			try {
				ReceptorBenchmark4 agenteReceptor = new ReceptorBenchmark4(
						new AgentID(classe1 + (inici + i),
								this.getAid().protocol, this.getAid().host,
								this.getAid().port + 1));

				agenteReceptor.start();

				wait(100);

			} catch (Exception e) {
				System.out
						.println("Error en ClonadorBenchmarks4, clonación de agentes receivers");
			}

		}
	}

}
