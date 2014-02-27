package qpidManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class UnixQpidManager {

	public static Process startQpid(Runtime runtime, Process qpid_broker) {
		try {

			Process qpid_killer = Runtime.getRuntime().exec("bash -c 'kill -9 $(ps ax |grep QPID|grep -v grep| awk '{print $1}')'");
			qpid_killer.waitFor();
			
			qpid_broker = Runtime
					.getRuntime()
					.exec("./installer/magentix2/bin/qpid-broker-0.20/bin/qpid-server");
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					qpid_broker.getInputStream()));

			String line = reader.readLine();

			while (!line.contains("Qpid Broker Ready")) {
				line = reader.readLine();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
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
