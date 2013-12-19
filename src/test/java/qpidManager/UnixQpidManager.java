package qpidManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class UnixQpidManager {

	public static Process startQpid(Runtime runtime, Process qpid_broker) {
		try {
			qpid_broker = Runtime
					.getRuntime()
					.exec("./installer/magentix2/bin/qpid-broker-0.20/bin/qpid-server");
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					qpid_broker.getInputStream()));

			String line;
			line = reader.readLine();
			
			while (!line.contains("Qpid Broker Ready")) {
				line = reader.readLine();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return qpid_broker;
	}

	public static void stopQpid(Process container) {
		container.destroy();
		try {
			container.destroy();
			container.waitFor();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
