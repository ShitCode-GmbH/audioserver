package de.cokejoke.cokeradio.network;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ConnectionHandler {

	private final AudioServer server;

	/**
	 * Noice hard coded http response header cause we don't care.
	 */
	String HTTP_RESPONSE = "HTTP/1.1 200 OK\r\n" + "Content-Type: audio/mpeg\r\n" + "Connection: Close\r\n" + "\r\n";

	public void handleConnection(Socket socket) throws IOException {
		DataOutputStream out = new DataOutputStream(socket.getOutputStream());
		DataInputStream in = new DataInputStream(socket.getInputStream());

		BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		String line;
		boolean audio = false;
		while ((line = reader.readLine()).length() > 1) {
			if (line.contains("Range:")) {
				audio = true;
			}
		}
		out.write(HTTP_RESPONSE.getBytes());

		if (!audio) {
			socket.close();
			return;
		}

		AudioClient client = new AudioClient(this.server, socket, out, in);
		this.server.connectClient(client);
		client.startWorker();
	}
}
