package de.cokejoke.cokeradio.network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AudioServer implements Runnable {
	private List<AudioClient> clients = new ArrayList<AudioClient>();
	private ConnectionHandler connectionHandler;
	private ServerSocket server;
	private Thread dipatcherThread;
	private boolean running;
	private ExecutorService executor = Executors.newCachedThreadPool();

	public AudioServer() {
		this.connectionHandler = new ConnectionHandler(this);
	}

	private ArrayList<byte[]> lastData = new ArrayList<byte[]>();

	public void connectClient(AudioClient client) {
		synchronized (lastData) {
			// sending last few seconds to speed up buffering
			for (byte[] data : lastData) {
				client.write(data);
			}
		}
		synchronized (client) {
			clients.add(client);
		}
		System.out.println("AudioClient from " + client.getIp() + " connected!");
	}

	public void disconnectClient(AudioClient client) {
		synchronized (client) {
			clients.remove(client);
		}
		System.out.println("AudioClient " + client.getIp() + " disconnected!");
	}

	public void writeToClients(byte[] data) {
		synchronized (clients) {
			Iterator<AudioClient> itr = this.clients.iterator();
			while (itr.hasNext()) {
				AudioClient client = itr.next();
				client.write(data);
			}
			lastData.add(data);
			while (lastData.size() > 3) {
				lastData.remove(0);
			}
		}
	}

	public void start(int port) throws IOException {
		System.out.println("Starting AudioServer on port " + port + "...");
		this.server = new ServerSocket(port);
		this.dipatcherThread = new Thread(this::run);
		this.running = true;
		this.dipatcherThread.start();
	}

	@Override
	public void run() {
		try {
			while (running) {
				Socket socket = this.server.accept();
				this.dispatchConnection(socket);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void dispatchConnection(Socket socket) {
		executor.execute(() -> {
			try {
				this.connectionHandler.handleConnection(socket);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		});
	}
}
