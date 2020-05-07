package de.cokejoke.cokeradio;

import java.io.IOException;

import de.cokejoke.cokeradio.audio.AudioStreamer;
import de.cokejoke.cokeradio.network.AudioServer;
import lombok.Getter;

@Getter
public class CokeRadio {

	private AudioServer server;
	private AudioStreamer stream;

	public CokeRadio() {

	}

	public void start() {
		System.out.println("Starting CokeRadio...");

		this.server = new AudioServer();

		try {
			this.server.start(25565);
		} catch (IOException e) {
			System.out.println("Could not start AudioServer: ");
			e.printStackTrace();
			System.exit(1);
			return;
		}

		AudioStreamer stream = new AudioStreamer(server);
		stream.start();

		// TODO set AudioStreamers song provider

		/*
		 * stream.setSongProvider(() -> { return null; });
		 */

		System.out.println("CokeRadio started successfully!");
	}
}
