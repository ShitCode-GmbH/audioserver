package de.cokejoke.cokeradio.audio;

import java.io.File;
import java.io.IOException;

import de.cokejoke.cokeradio.network.AudioServer;
import lombok.Setter;

public class AudioStreamer implements Runnable {

	private StreamableAudio currentAudio;
	private AudioServer audioServer;
	@Setter
	private SongProvider songProvider;

	public AudioStreamer(AudioServer server) {
		this.audioServer = server;
	}

	public void start() {
		this.nextSong();
		new Thread(this).start();
	}

	/**
	 * We don't care about flow control or anything, the client will do it for us.
	 */

	@Override
	public void run() {
		long stamp = System.currentTimeMillis();

		int tickRate = 1000;

		while (true) {
			byte[] data = this.currentAudio.getNextSegments(tickRate);
			this.audioServer.writeToClients(data);

			if (data.length < this.currentAudio.getSegmentsSize(tickRate)) {
				this.onSongEnd();
			}

			long diff = Math.min(0, (System.currentTimeMillis() - stamp));

			try {
				Thread.sleep(tickRate - diff);
			} catch (InterruptedException e) {
			}
			diff = (System.currentTimeMillis() - stamp);
			stamp = System.currentTimeMillis();
		}
	}

	private void onSongEnd() {
		System.out.println("Song ended!");
		nextSong();
	}

	public void nextSong() {
		if (songProvider == null) {
			System.out.println("No song provider set, defaulting to hardcoded file on desktop :)");
			File file = new File(System.getProperty("user.home") + "/Desktop/Test.mp3");
			if (!file.exists()) {
				// 404 - file not found, i guess
				System.exit(404);
			}
			Song next = new Song(file, file.getName(), file.length(), 192);
			this.play(next);
			return;
		}
		
		Song next = songProvider.getNext();
		this.play(next);
	}

	public void play(Song song) {
		if (currentAudio != null) {
			this.currentAudio.closeStream();
		}

		StreamableAudio nextAudio = new StreamableAudio(song.getFile(), song.getLength(), song.getBitrate());

		try {
			nextAudio.openStream();
			this.currentAudio = nextAudio;
			System.out.println("Now playing " + song.getTitle());
		} catch (IOException e) {
			System.out.println("Could not play song " + song.getTitle());
			this.nextSong();
			e.printStackTrace();
		}
	}
}
