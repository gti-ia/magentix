package Others_Examples;

import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class httpclient extends JFrame {
	private JTextField campoIntroducir;
	private JTextArea areaPantalla;
	private DatagramSocket socket;

	// configurar GUI y DatagramSocket
	public httpclient() {
		super("Cliente");

		Container contenedor = getContentPane();

		campoIntroducir = new JTextField("Escriba aqui el mensaje");
		campoIntroducir.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evento) {
				// crear y enviar el paquete
				try {
					areaPantalla.append("\nEnviando paquete que contiene: "
							+ evento.getActionCommand() + "\n");

					// obtener mensaje del campo de texto y convertirlo en
					// arreglo byte
					String mensaje = evento.getActionCommand();
					byte datos[] = mensaje.getBytes();

					// crear enviarPaquete
					DatagramPacket enviarPaquete = new DatagramPacket(datos,
							datos.length, InetAddress.getLocalHost(), 5000);

					socket.send(enviarPaquete); // enviar paquete
					areaPantalla.append("Paquete enviado\n");
					areaPantalla.setCaretPosition(areaPantalla.getText()
							.length());
				}

				// procesar los problemas que pueden ocurrir al crear o enviar
				// el paquete
				catch (IOException excepcionES) {
					mostrarMensaje(excepcionES.toString() + "\n");
					excepcionES.printStackTrace();
				}

			} // fin de actionPerformed

		} // fin de la clase interna

				); // fin de la llamada a addActionListener

		contenedor.add(campoIntroducir, BorderLayout.NORTH);

		areaPantalla = new JTextArea();
		contenedor.add(new JScrollPane(areaPantalla), BorderLayout.CENTER);

		setSize(400, 300);
		setVisible(true);

		// crear objeto DatagramSocket para enviar y recibir paquetes
		try {
			socket = new DatagramSocket();
		}

		// atrapar los problemas que pueden ocurrir al crear objeto
		// DatagramSocket
		catch (SocketException excepcionSocket) {
			excepcionSocket.printStackTrace();
			System.exit(1);
		}

	} // fin del constructor de Cliente

	// esperar a que lleguen los paquetes del Servidor, mostrar el contenido de
	// los paquetes
	private void esperarPaquetes() {
		while (true) { // iterar infinitamente

			// recibir el paquete y mostrar su contenido
			try {

				// establecer el paquete
				byte datos[] = new byte[100];
				DatagramPacket recibirPaquete = new DatagramPacket(datos,
						datos.length);

				socket.receive(recibirPaquete); // esperar un paquete

				// mostrar el contenido del paquete
				mostrarMensaje("\nPaquete recibido:"
						+ "\nDel host: "
						+ recibirPaquete.getAddress()
						+ "\nPuerto del host: "
						+ recibirPaquete.getPort()
						+ "\nLongitud: "
						+ recibirPaquete.getLength()
						+ "\nContenido:\n\t"
						+ new String(recibirPaquete.getData(), 0,
								recibirPaquete.getLength()));
			}

			// procesar los problemas que pueden ocurrir al recibir o mostrar el
			// paquete
			catch (IOException excepcion) {
				mostrarMensaje(excepcion.toString() + "\n");
				excepcion.printStackTrace();
			}

		} // fin de instruccion while

	} // fin del metodo esperarPaquetes

	// metodo utilitario que es llamado desde otros subprocesos para manipular a
	// areaPantalla en el subproceso despachador de eventos
	private void mostrarMensaje(final String mensajeAMostrar) {
		// mostrar mensaje del subproceso de ejecucion despachador de eventos
		SwingUtilities.invokeLater(new Runnable() { // clase interna para
													// asegurar que la GUI se
													// actualice apropiadamente

					public void run() // actualiza areaPantalla
					{
						areaPantalla.append(mensajeAMostrar);
						areaPantalla.setCaretPosition(areaPantalla.getText()
								.length());
					}

				} // fin de la clase interna

				); // fin de la llamada a SwingUtilities.invokeLater
	}

	public static void main(String args[]) {
		httpclient aplicacion = new httpclient();
		aplicacion.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		aplicacion.esperarPaquetes();
	}

} // fin de la clase Cliente

