package es.upv.dsic.gti_ia.core;


import org.apache.qpid.transport.Connection;
import org.apache.qpid.transport.ConnectionSettings;

import es.upv.dsic.gti_ia.organization.Configuration;

/**
 * This class work to open a Qpid broker connection.
 * @author Sergio Pajares
 */
public class AgentsConnection {
	public static org.apache.qpid.transport.Connection connection;
	private static boolean Secure = false;
	private static Configuration c = null;

	/**
	 * Connects with a Qpid broker taking the input connection parameters from the Settings.xml file.
	 * 
	 */
	public static void connect() {
		c =  Configuration.getConfiguration();
		connection = new Connection();
	
		ConnectionSettings connectSettings = new ConnectionSettings();
		connectSettings.setHost(c.getqpidHost());
		connectSettings.setPort(c.getqpidPort());
		connectSettings.setVhost(c.getqpidVhost());
		connectSettings.setUsername(c.getqpidUser());
		connectSettings.setPassword(c.getqpidPassword());
		connectSettings.setUseSSL(c.getqpidSSL());
		connectSettings.setSaslMechs(c.getqpidsaslMechs());
		connectSettings.setSaslProtocol(c.getqpidsaslProtocol());
		connectSettings.setSaslServerName(c.getqpidServerName());
	

		
		connection.connect(connectSettings);
	
	//	connection.connect(c.getqpidHost(),c.getqpi, arg2, arg3, arg4, arg5, arg6)
	}
	
	public static boolean isSecured()
	{
		return Secure;
	}
	public static void SecureConnect()
	{
		
		Secure = true;
		/*1. Validar certificado del usuario:
		 * Primero ofrecer varios certificados en los que se confia (CA), por ahora en el del dnie (fabrica de moneda i timbre)
		 * De que manera se nos hara llegar ese certificado, mediante ruta (mirar si puede dar problemas en rutas de fuera del host)
		 * o mediante argumentos java.
		 * Llamar a una funcion del agente MMS que se encarge de validar ese certificado
		 * La funcion del MMS encargada de validar el certificador debera:
		 * 		De alguna manera(usar sasl com hace el broker) verificar el certificado.
		 * (o usar librerias de java, deberemos diferenciar entre las diferentes plataformas)
		 * 		Si el resultado ok, 
		 * 			Por cada agente que haya o vaya a crear el usuario:
		 * 				(Modificar codigo del BaseAgent)
		 * 				Un certificado nuevo con el nombre del agente.
		 * 				Una conexion con el certificado anterior
		 * 				Una session por cada conexion
		 * 			
		
		
		Pruebas verificadas:
			Agregar el certificado de moneda y timbre a nuestra base de datos de confianza (parte del broker)
		*/	
		
	}
	

	
	
	/**
	 * Connects to Qpid broker taking into account all the parameters specified as input.
	 * @param qpidHost
	 * @param qpidPort
	 * @param qpidVhost
	 * @param qpdidUser
	 * @param qpidPassword
	 * @param qpidSSL
	 */
	public static void connect(String qpidHost, int qpidPort, String qpidVhost, String qpdidUser,
			String qpidPassword, boolean qpidSSL) {
		connection = new Connection();
		connection.connect(qpidHost, qpidPort, qpidVhost, qpdidUser, qpidPassword, qpidSSL);
	}
	
	public static void connect(String qpidHost, int qpidPort, String qpidVhost, String qpdidUser,
			String qpidPassword, boolean qpidSSL, String sasl_mechs) {
		connection = new Connection();
		connection.connect(qpidHost, qpidPort, qpidVhost, qpdidUser, qpidPassword, qpidSSL,sasl_mechs);
	
	}
	
	
	/**
	 * Connects to Qpid broker taking into account the qpidhost parameter and considering the rest as defaults parameters.
	 * broker installation
	 * @param qpidHost
	 */
	public static void connect(String qpidHost) {
		connection = new Connection();
		connection.connect(qpidHost, 5672,  "test", "guest", "guest", false);
	}
	


}
