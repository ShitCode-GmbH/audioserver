package de.cokejoke.cokeradio.audio;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class StreamableAudio {

	private File file;
	private FileInputStream streamIn;

	private long dataLength;
	private long dataCounter;
	private int bitrate;

	public StreamableAudio(File file, long length, int bitrate) {
		this.file = file;
		this.dataLength = length;
		this.bitrate = bitrate;
	}

	public void openStream() throws IOException {
		if (this.streamIn != null) {
			this.closeStream();
		}

		this.streamIn = new FileInputStream(file);

		// skip mp3 header
		this.streamIn.skip(128);
	}

	public void closeStream() {
		try {
			this.streamIn.close();
		} catch (Exception ex) {
		}
		this.streamIn = null;
	}

	public int getSegmentsSize(int duration) {
		return ((int) (this.bitrate * (duration / 1000d) * 1024)) / 8;
	}

	public byte[] getNextSegments(int duration) {
		try {
			int size = (int) Math.min(dataLength - dataCounter, getSegmentsSize(duration));
			byte[] buff = new byte[size];
			this.streamIn.read(buff);
			this.dataCounter += size;
			return buff;
		} catch (IOException e) {
			System.err.println("Error streaming " + this.file.getName());
			e.printStackTrace();
			return new byte[] {};
		}
	}
}
