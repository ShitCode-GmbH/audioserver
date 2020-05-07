package de.cokejoke.cokeradio.audio;

import java.io.File;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class Song {
	private final File file;
	private final String title;
	private final long length;
	private final int bitrate;
}