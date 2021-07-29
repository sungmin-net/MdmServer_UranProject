import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;

public class MdmServerMain {

	private static final int PORT = 9999;

	public static void main(String[] args) {

		System.setProperty("javax.net.ssl.keyStore",
				"C:\\eclipse\\workspace\\UranPolicyServer\\UranMdmServer.p12");
		System.setProperty("javax.net.ssl.keyStorePassword", "mmlabmmlab");
		System.setProperty("javax.net.debug", "ssl");
		SSLServerSocket servSock = null;

		try {
			SSLServerSocketFactory factory =
					(SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
			servSock = (SSLServerSocket) factory.createServerSocket(PORT);
			System.out.println(getTime() + "Wait for the client request");

			while (true) {
				SSLSocket sock = (SSLSocket) servSock.accept();
				new Thread(new ClientHandler(sock)).start();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (servSock != null && !servSock.isClosed()) {
				try {
					servSock.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private static class ClientHandler implements Runnable {
		private SSLSocket socket;
		public ClientHandler(SSLSocket socket) {
			this.socket = socket;
		}

		@Override
		public void run() {
			try {
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(socket.getInputStream()));
				PrintWriter writer = new PrintWriter(socket.getOutputStream());
				String clientMsg = reader.readLine();
				log("[" + socket.getInetAddress() + "] RECV: " + clientMsg);
				if (clientMsg.startsWith("Hello!")) {
					String serverMsg = "[ServerEcho] " + clientMsg;
					writer.println(serverMsg);
					writer.flush();
					log("[" + socket.getInetAddress() + "] SENT: " + serverMsg);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static String getTime() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("[yy.MM.dd HH:mm:ss] ");
		return dateFormat.format(new Date());
	}

	public static void log(String msg) {
		System.out.println(getTime() + msg);
	}
}
