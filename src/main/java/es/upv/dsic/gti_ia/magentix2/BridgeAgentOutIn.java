package es.upv.dsic.gti_ia.magentix2;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import org.apache.qpid.transport.Connection;
import es.upv.dsic.gti_ia.fipa.ACLMessage;
import es.upv.dsic.gti_ia.fipa.AgentID;

/**
 * This agent routes messages from inside the platform to outside the platform.
 * It converts ACLMessages into ACLJadeMessages
 * @author  Ricard Lopez Fogues
 */

public class BridgeAgentOutIn extends SingleAgent{
	
	private DatagramSocket socket;

	/**
	 * Creates a new BrideAgentOutIn
	 * @param aid
	 * @param connection
	 * @throws Exception
	 */
	public BridgeAgentOutIn(AgentID aid, Connection connection) throws Exception {
		super(aid, connection);
		
		  // crear objeto DatagramSocket para enviar y recibir paquetes
	       try {
	          socket = new DatagramSocket( 5000 );
	       }
	 
	       // procesar los problemas que pueden ocurrir al crear el objeto DatagramSocket
	       catch( SocketException excepcionSocket ) {
	          excepcionSocket.printStackTrace();
	          System.exit( 1 );
	       }
		
	}
	
	public void execute(){
		while(true){
			//Escuchar por protocolo http y enviar a quien corresponda
			 // recibir paquete, mostrar su contenido
	          try {
	 
	             // establecer el paquete
	             byte datos[] = new byte[ 2000 ];
	             DatagramPacket recibirPaquete = 
	                new DatagramPacket( datos, datos.length );
	 
	             socket.receive( recibirPaquete ); // esperar el paquete
	             
	             logger.info("Received on BridgeAgentOutIn: "+new String( recibirPaquete.getData()));
	 
	             // mostrar la información del paquete recibido 
	             logger.info( "\nPackage received:" + 
	                "\nfrom host: " + recibirPaquete.getAddress() + 
	                "\nPort of the host: " + recibirPaquete.getPort() + 
	                "\nLenght: " + recibirPaquete.getLength() + 
	                "\nContent:\n\t" + new String( recibirPaquete.getData(), 
	                   0, recibirPaquete.getLength() ) );
	 
	             //creamos nuevo hilo encargado de enviar el mensaje
	             httpToACL hilo = new httpToACL(stringToInputStream(new String(recibirPaquete.getData())));
	             hilo.run();
	             
	          }
	 
	          // procesar los problemas que pueden ocurrir al manipular el paquete
	          catch( IOException excepcionES ) {
	            logger.error( "Error on BridgeAgentOutIn, "+excepcionES.toString() + "\n" );
	            excepcionES.printStackTrace();
	          }
		}
	}
	
	public class httpToACL extends Thread{
		InputStream httpmessage;
		
		public httpToACL(InputStream message){
			httpmessage = message;
		}
		
		public void run(){
			ACLMessage msg = new ACLMessage(-1);
			AgentID sender = new AgentID();
			AgentID receiver = new AgentID();
			//parseamos las cabeceras html, y obtenemos las diferentes partes del mensaje
			BufferedInputStream bis = null;
		    BufferedReader dis = null;
	
		    try {
		    	// Here BufferedInputStream is added for fast reading.
		    	bis = new BufferedInputStream(httpmessage);
		    	dis = new BufferedReader(new InputStreamReader(bis));		    	
	
		    	//leemos las cuatro primeras lineas que no nos interesan y nos quedamos con la quinta
		    	String cadena = dis.readLine();		    	
		    
		    	int cont = 0;
		    	while (cadena != null && cont <4){
		    		// this statement reads the line from the file and print it to
		    		// the console.
		    		logger.info(cadena);
		    		cadena = dis.readLine();
		    		cont++;
		    	}
		    	//buscamos el boundary
		    	int indexboundary = cadena.indexOf("boundary=\"");
		    	String boundary = cadena.substring(indexboundary+10, cadena.indexOf("\"", indexboundary + 10));
		    	//leemos hasta encontrar el boundary, donde empieza el envelope
		    	do
		    		cadena = dis.readLine();
		    	while (cadena != null && cadena.indexOf(boundary) == -1);
		    	//la primera linea nos indica el content type
		    	cadena = dis.readLine();
		    	//linea en blanco
		    	cadena = dis.readLine();
		    	//version xml
		    	cadena = dis.readLine();
		    	//envelope
		    	cadena = dis.readLine();
		    	logger.info(cadena);
		    	//parseamos contenido XML
		    	Xml envelope = new Xml(stringToInputStream(cadena),"envelope");	
	
				Xml params = envelope.child("params");
				logger.debug("index: "+params.integer("index"));
				
				int index = 0;
				//Agente destino
				logger.debug("Agent Details destination");
				Xml to = params.child("to");
				Xml agent_indentifier = to.child("agent-identifier");
				
				int index2 = agent_indentifier.child("name").content().indexOf('@');
				
				
				receiver.name = agent_indentifier.child("name").content().substring(0, index2);
				logger.debug("Agent Name: "+ receiver.name);
				Xml adresses = agent_indentifier.child("addresses");
				for(Xml url:adresses.children("url")){
					index = url.content().indexOf(':');
					receiver.protocol = url.content().substring(0,index);
					receiver.host = url.content().substring(index+3,url.content().indexOf(":", index+1));
					index = url.content().indexOf(":", index+1);
					receiver.port = url.content().substring(index+1);
					logger.debug("Adress: "+receiver.toString());
				}
				
				msg.setReceiver(receiver);
				
				//Agente remitente
				logger.debug("Details sender agent");
				Xml from = params.child("from");
				agent_indentifier = from.child("agent-identifier");
				
				index2 = agent_indentifier.child("name").content().indexOf('@');
				sender.name = agent_indentifier.child("name").content().substring(0, index2);
				logger.debug("Agent name: "+ sender.name);
				adresses = agent_indentifier.child("addresses");
				
				for(Xml url:adresses.children("url")){
					index = url.content().indexOf(':');
					sender.protocol = url.content().substring(0,index);
					sender.host = url.content().substring(index+3,url.content().indexOf(":", index+1));
					index = url.content().indexOf(":", index+1);
					sender.port = url.content().substring(index+1);
					logger.debug("Sender: "+sender.toString());
				}
				msg.setSender(sender);
				
				//volvemos a buscar el boundary
				do
					cadena = dis.readLine();
				while(cadena != null && cadena.indexOf(boundary) == -1);
				//tipo contenido
				cadena = dis.readLine();
				//linea en blanco
				cadena = dis.readLine();
				//performativa
				cadena = dis.readLine();
				String performative = cadena.substring(1).trim();
				msg.setPerformative(performative);
				logger.debug("Performative: "+msg.getPerformative());
				//agente remitente, ya tenemos sus datos
				cadena = dis.readLine();
				//agente receptor, ya tenemos sus datos
				cadena = dis.readLine();
				//content
				cadena = dis.readLine();
				int indexcontent = cadena.indexOf('"');
				String content = "";
				boolean seguir = true;
				int pos;
				content = content + cadena.substring(indexcontent + 1);
				pos = cadena.length();
				while(cadena.charAt(pos-1) == ' ')
					pos--;
				if(cadena.charAt(pos-1) == '"' && cadena.charAt(pos-2) != '\\')
					seguir = false;
				while(seguir){				
					cadena = dis.readLine();
					content = content + cadena;
					pos = cadena.length();
					while(cadena.charAt(pos-1) == ' ')
						pos--;
					if(cadena.charAt(pos-1) == '"' && cadena.charAt(pos-2) != '\\')
						seguir = false;
				}
				content = content.substring(0, content.length()-1);
				msg.setContent(content);
				logger.debug("content "+content);
				
				//language
				String lang = "";
				cadena = dis.readLine();
				while(cadena.indexOf(":language")+9 < 0 && cadena != null)
					cadena = dis.readLine();
				
				cadena = cadena.substring(cadena.indexOf(":language")+9);
				
				int k = 0;
				while(cadena.charAt(k) == ' ')
					k++;
				
				while(cadena.charAt(k) != ' '){
					lang = lang + cadena.charAt(k);
					k++;
				}
				msg.setLanguage(lang);
							
				//ontology
				String ontology = "";
				while(cadena.indexOf(":ontology")+9 < 0 && cadena != null)
					cadena = dis.readLine();
				cadena = cadena.substring(cadena.indexOf(":ontology")+9);
				
				k = 0;
				while(cadena.charAt(k) == ' ')
					k++;
				
				while(cadena.charAt(k) != ' '){
					ontology = ontology + cadena.charAt(k);
					k++;
				}			
				msg.setOntology(ontology);   
		    } catch (IOException e) {
		    	e.printStackTrace();
		    }
		    
		    msg.getReceiver().protocol="qpid";
		    send(msg);
		}
	}
	
	public static InputStream stringToInputStream(String cadena){
		InputStream is = null;
		try {
			is = new ByteArrayInputStream(cadena.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return is;
	}
}
