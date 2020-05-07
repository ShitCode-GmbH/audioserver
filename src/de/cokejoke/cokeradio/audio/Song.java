package de.cokejoke.cokeradio.audio;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class Song {
	static List<String> files = Arrays.asList("Test.mp3", "creeper.mp3", "YAY.mp3", "bibi.mp3", "hotto_dogu.mp3", "soos.mp3");
	static int index = 0;

	static Song next() {
		if (index >= files.size()) {
			index = 0;
		}
		String fileName = files.get(index);
		File file = new File("C:/Users/Th/Desktop/" + fileName);
		index++;
		return new Song(file, file.getName(), file.length(), 192);
	}

	private final File file;
	private final String title;
	private final long length;
	private final int bitrate;
}