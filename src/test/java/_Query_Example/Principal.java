package _Query_Example;

import org.apache.qpid.transport.Connection;

import s.dsic.gti_ia.fipa.AgentID;

public class Principal {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		// TODO Auto-generated method stub

		Connection con = new Connection();
		con.connect("gtiiaprojects.dsic.upv.es", 5672, "test", "guest",
				"guest", false); // TODO add your handling code here:

		Aeropuerto aeropuerto = new Aeropuerto(new AgentID("aeropuerto1",
				"qpid", "localhost", ""), con);
		aeropuerto.start();

		Viajante viajante = new Viajante(new AgentID(
				"Viajeroooooooooooooooooooooooooooooooooooo", "qpid",
				"localhost", ""), con);
		viajante.start();

	}

}
