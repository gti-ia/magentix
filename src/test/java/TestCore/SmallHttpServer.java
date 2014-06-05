package TestCore;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.CountDownLatch;

public class SmallHttpServer extends Thread {

	private ServerSocket ss;
	private Socket s;
	private String msg;
	private boolean finalized;
	CountDownLatch cdl;

	public SmallHttpServer(CountDownLatch cdl) {

		try {
			finalized = false;
			ss = new ServerSocket(7778);
			this.cdl = cdl;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public String getContentMsg() {
		int begin = msg.indexOf(":content \"") + 10;
		String content = msg.substring(begin, msg.indexOf("\" :", begin));
		return content;
	}

	public void run() {
		while (!finalized) {
			try {
				InputStream is;
				s = ss.accept();
				if (s != null) {
					is = s.getInputStream();

					msg = getStringFromInputStream(is);
					System.out.println(getContentMsg());
					cdl.countDown();
				}

			} catch(SocketException se) {
				finalized = true;
			} 
			catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	public void finalize() {
		this.finalized = true;
		try {

			if (s != null)
				s.close();

			ss.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.print(e.getMessage());
			e.printStackTrace();
		}
		System.out.println("Small Http leaves the system.");
	}

	private static String getStringFromInputStream(InputStream is) {
		InputStreamReader isr = null;
		BufferedReader br = null;
		StringBuilder sb = new StringBuilder();

		String line;
		try {
			isr = new InputStreamReader(is);
			br = new BufferedReader(isr);
			System.out.println("Leaves BufferedReader");
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
			System.out.println("Leaves While");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return sb.toString();

	}
}
