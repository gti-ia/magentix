package Others_Examples;

import java.awt.BorderLayout;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

public class httpserver extends JFrame {
	private JTextArea areaPantalla;
	private DatagramSocket socket;

	// configurar GUI y DatagramSocket
	public httpserver() {
		super("Servidor");

		areaPantalla = new JTextArea();
		getContentPane()
				.add(new JScrollPane(areaPantalla), BorderLayout.CENTER);
		setSize(400, 300);
		setVisible(true);

		// crear objeto DatagramSocket para enviar y recibir paquetes
		try {
			socket = new DatagramSocket(5000);
		}

		// procesar los problemas que pueden ocurrir al crear el objeto
		// DatagramSocket
		catch (SocketException excepcionSocket) {
			excepcionSocket.printStackTrace();
			System.exit(1);
		}

	} // fin del constructor de Servidor

	// esperar a que lleguen los paquetes, mostrar los datos y repetir el
	// paquete al cliente
	private void esperarPaquetes() {
		while (true) { // iterar infinitamente

			// recibir paquete, mostrar su contenido, devolver copia al cliente
			try {

				// establecer el paquete
				byte datos[] = new byte[100];
				DatagramPacket recibirPaquete = new DatagramPacket(datos,
						datos.length);

				socket.receive(recibirPaquete); // esperar el paquete

				// mostrar la informaci�n del paquete recibido
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

				enviarPaqueteACliente(recibirPaquete); // enviar paquete al
														// cliente
			}

			// procesar los problemas que pueden ocurrir al manipular el paquete
			catch (IOException excepcionES) {
				mostrarMensaje(excepcionES.toString() + "\n");
				excepcionES.printStackTrace();
			}

		} // fin de instrucci�n while

	} // fin del m�todo esperarPaquetes

	// repetir el paquete al cliente
	private void enviarPaqueteACliente(DatagramPacket recibirPaquete)
			throws IOException {
		mostrarMensaje("\n\nRepitiendo datos al cliente...");

		// crear paquete a enviar
		DatagramPacket enviarPaquete = new DatagramPacket(recibirPaquete
				.getData(), recibirPaquete.getLength(), recibirPaquete
				.getAddress(), recibirPaquete.getPort());

		socket.send(enviarPaquete); // enviar el paquete
		mostrarMensaje("Paquete enviado\n");
	}

	// m�todo utilitario que es llamado desde otros subprocesos para manipular a
	// areaPantalla en el subproceso despachador de eventos
	private void mostrarMensaje(final String mensajeAMostrar) {
		// mostrar el mensaje del subproceso de ejecuci�n despachador de eventos
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
		httpserver aplicacion = new httpserver();
		aplicacion.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		aplicacion.esperarPaquetes();
	}

} // fin de la clase Servidor

