package de.cokejoke.cokeradio.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AudioClient {

	private boolean connected = true;

	private final AudioServer server;
	private final Socket socket;
	private final DataOutputStream out;
	private final DataInputStream in;

	private BlockingQueue<byte[]> dataQueue = new ArrayBlockingQueue<byte[]>(100);

	public void write(byte[] data) {
		if (this.connected) {
			if (dataQueue.remainingCapacity() > 0) {
				dataQueue.add(data);
			} else {
				this.disconnect("buffer overflow");
			}
		}
	}

	public void startWorker() {
		Thread worker = new Thread(this::work);
		worker.start();
	}

	private void work() {
		System.out.println("ready to send data to " + this.getIp() + "...");
		try {
			while (connected) {
				byte[] data = dataQueue.take();
				this.out.write(data);
			}
		} catch (Exception e) {
			e.printStackTrace();
			disconnect(e.getMessage());
		}
	}

	private void disconnect(String reason) {
		synchronized (this) {
			if (!this.connected) {
				return;
			}

			this.connected = false;
		}

		this.dataQueue.clear();
		this.server.disconnectClient(this);

		try {
			this.socket.close();
		} catch (Exception e) {
		}
	}

	public String getIp() {
		return this.socket.getInetAddress().getHostAddress();
	}
}
