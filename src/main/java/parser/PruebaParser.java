package parser;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import es.upv.dsic.gti_ia.fipa.ACLMessage;
import es.upv.dsic.gti_ia.fipa.AgentID;

public class PruebaParser {
	public static void main(String[] args) {
		ACLMessage msg = new ACLMessage(-1);
		AgentID sender = new AgentID();
		AgentID receiver = new AgentID();
		//parseamos las cabeceras html, y obtenemos las diferentes partes del mensaje
		File file = new File("/home/ricard/captura_JADE.txt");
	    FileInputStream fis = null;
	    BufferedInputStream bis = null;
	    BufferedReader dis = null;

	    try {
	    	fis = new FileInputStream(file);

	    	// Here BufferedInputStream is added for fast reading.
	    	bis = new BufferedInputStream(fis);
	    	dis = new BufferedReader(new InputStreamReader(bis));

	    	//leemos las cuatro primeras lineas que no nos interesan y nos quedamos con la quinta
	    	String cadena = dis.readLine();
	    	int cont = 0;
	    	while (cadena != null && cont <4){
	    		// this statement reads the line from the file and print it to
	    		// the console.
	    		System.out.println(cadena);
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
	    	System.out.println(cadena);
	    	//parseamos contenido XML
	    	Xml envelope = new Xml(stringToInputStream(cadena),"envelope");			

			Xml params = envelope.child("params");
			System.out.println("index: "+params.integer("index"));
			
			int index = 0;
			//Agente destino
			System.out.println("Datos agente destino");
			Xml to = params.child("to");
			Xml agent_indentifier = to.child("agent-identifier");
			receiver.name = agent_indentifier.child("name").content();
			System.out.println("Nombre agente: "+ agent_indentifier.child("name").content());
			Xml adresses = agent_indentifier.child("addresses");
			for(Xml url:adresses.children("url")){
				index = url.content().indexOf(':');
				receiver.protocol = url.content().substring(0,index);
				receiver.host = url.content().substring(index+3,url.content().indexOf(":", index+1));
				index = url.content().indexOf(":", index+1);
				receiver.port = url.content().substring(index+1);
				System.out.println("Adress: "+receiver.toString());
			}
			
			//Agente remitente
			System.out.println("Datos agente remitente");
			Xml from = params.child("from");
			agent_indentifier = from.child("agent-identifier");
			sender.name = agent_indentifier.child("name").content();
			System.out.println("Nombre agente: "+ sender.name);
			adresses = agent_indentifier.child("addresses");
			
			for(Xml url:adresses.children("url")){
				index = url.content().indexOf(':');
				sender.protocol = url.content().substring(0,index);
				sender.host = url.content().substring(index+3,url.content().indexOf(":", index+1));
				index = url.content().indexOf(":", index+1);
				sender.port = url.content().substring(index+1);
				System.out.println("Sender: "+sender.toString());
			}
			
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
			System.out.println(msg.getPerformative());
			//agente remitente, ya tenemos sus datos, no?
			cadena = dis.readLine();
			/*int indexname = cadena.indexOf(":name");
			int indexaddresses = cadena.indexOf(":addresses");
			String nombreremitente = cadena.substring(indexname+5, indexaddresses);
			nombreremitente = nombreremitente.trim();
			System.out.println(nombreremitente);*/
			//agente receptor, ya tenemos sus datos, no?
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
			content = content.substring(0, content.length()-2);
			msg.setContent(content);
			System.out.println("content "+content);
			
			//language
			cadena = dis.readLine();
			msg.setLanguage(cadena.substring(cadena.indexOf(":language")+9, cadena.indexOf(":ontology") - 1).trim());
			System.out.println(msg.getLanguage());
			
			//ontology
			msg.setOntology(cadena.substring(cadena.indexOf(":ontology")+9, cadena.indexOf(")") - 1).trim());
			System.out.println(msg.getOntology());
				    	
	    } catch (FileNotFoundException e) {
	    	e.printStackTrace();
	    } catch (IOException e) {
	    	e.printStackTrace();
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
