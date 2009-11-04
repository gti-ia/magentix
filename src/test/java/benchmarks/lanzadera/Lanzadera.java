package benchmarks.lanzadera;

import org.apache.qpid.transport.Connection;

import es.upv.dsic.gti_ia.core.AgentID;

import benchmarks.bench1.ControladorBenchmark1;
import benchmarks.bench1.EmisorBenchmark1;
import benchmarks.bench1.ReceptorBenchmark1;
import benchmarks.bench2a.ControladorBenchmark2;
import benchmarks.bench2a.EmisorBenchmark2;
import benchmarks.bench3.ControladorBenchmark3;
import benchmarks.bench3.EmisorBenchmark3;
import benchmarks.bench4.ControladorBenchmark4;
import benchmarks.bench4.EmisorBenchmark4;

public class Lanzadera {
	/*
	 * Lanzadera Emisor "1 o 2 o 3 o 4" (Tipo de Benchamrk), <------ 0 "Emisor" ,
	 * <------ 1 nombre completo 2 nmsgtot, //nombre total de missatges a enviar
	 * <------ 3 tmsg, //tamany del missatge <------ 4 ntotal, //nombre total
	 * d'agents <------ 5 nemisor, //nombre del agent <------ 6
	 * 
	 */

	/*
	 * Lanzadera Receptor "1 o 2 o 3 o 4" (Tipo de Benchamrk), <------ 0
	 * "Receptor" , <------ 1 nombre completo 2
	 * 
	 * 
	 */
	/*
	 * Lanzadera Controlador "1 o 2 o 3 o 4" (Tipo de Benchamrk), <------ 0
	 * "Controlador" , <------ 1 nombre completo 2 ntotal, //nombre total
	 * d'agents <------ 3
	 * 
	 * 
	 */
	public static void main(String args[]) {

		AgentID agent = new AgentID(args[2]);

		Connection con = new Connection();
		con.connect("rilpefo.dsic.upv.es", 5672, "test", "guest", "guest",
				false);

		if (args[1].toString().toLowerCase().equals("emisor")) {
			switch (Integer.valueOf(args[0].toString())) {
			case 1:
				EmisorBenchmark1 a1 = new EmisorBenchmark1(agent, con, Integer
						.valueOf(args[3]), Integer.valueOf(args[4]), Integer
						.valueOf(args[5]), Integer.valueOf(args[6]));
				a1.start();
				break;
			case 2:
				EmisorBenchmark2 a2 = new EmisorBenchmark2(agent, con, Integer
						.valueOf(args[3]), Integer.valueOf(args[4]), Integer
						.valueOf(args[5]), Integer.valueOf(args[6]));
				a2.start();
				break;
			case 3:
				EmisorBenchmark3 a3 = new EmisorBenchmark3(agent, con, Integer
						.valueOf(args[3]), Integer.valueOf(args[4]), Integer
						.valueOf(args[5]), Integer.valueOf(args[6]));
				a3.start();
				break;
			case 4:
				EmisorBenchmark4 a4 = new EmisorBenchmark4(agent, con, Integer
						.valueOf(args[3]), Integer.valueOf(args[4]), Integer
						.valueOf(args[5]), Integer.valueOf(args[6]));
				a4.start();
				break;
			default:
				System.out.println("Error, Seleccione un bechmark entre 1 y 4");
				break;
			}
		} else if (args[1].toString().toLowerCase().equals("receptor")) {
			switch (Integer.valueOf(args[0].toString())) {
			case 1:
				ReceptorBenchmark1 a1 = new ReceptorBenchmark1(agent, con);
				a1.start();
				break;
			case 2:
				ReceptorBenchmark1 a2 = new ReceptorBenchmark1(agent, con);
				a2.start();
				break;
			case 3:
				ReceptorBenchmark1 a3 = new ReceptorBenchmark1(agent, con);
				a3.start();
				break;
			case 4:
				ReceptorBenchmark1 a4 = new ReceptorBenchmark1(agent, con);
				a4.start();
				break;
			default:
				System.out.println("Error, Seleccione un bechmark entre 1 y 4");
				break;
			}
		} else if (args[1].toString().toLowerCase().equals("controlador")) {
			switch (Integer.valueOf(args[0].toString())) {
			case 1:
				ControladorBenchmark1 a1 = new ControladorBenchmark1(agent,
						con, Integer.valueOf(args[3]));
				a1.start();
				break;
			case 2:
				ControladorBenchmark2 a2 = new ControladorBenchmark2(agent,
						con, Integer.valueOf(args[3]));
				a2.start();
				break;
			case 3:
				ControladorBenchmark3 a3 = new ControladorBenchmark3(agent,
						con, Integer.valueOf(args[3]));
				a3.start();
				break;
			case 4:
				ControladorBenchmark4 a4 = new ControladorBenchmark4(agent,
						con, Integer.valueOf(args[3]));
				a4.start();
				break;
			default:
				System.out.println("Error, Seleccione un bechmark entre 1 y 4");
				break;
			}
		} else {
			System.out
					.println("Error, Selecciones Emisor, Receptor o controlador como primer arguemento");
		}
	}

}
