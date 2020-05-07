package de.cokejoke.cokeradio;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class LegacyCokeradio {

	public static void main(String[] args) throws Exception {
		server();
	}

	static void server() throws Exception {

		String iloveresponse = "HTTP/1.1 200 OK\r\n" + "Content-Type: audio/mpeg\r\n" + "Date: Thu, 26 Mar 2020 18:03:50 GMT\r\n" + "icy-br:128\r\n" + "icy-name:I Love Mashup by ilovemusic.de\r\n"
				+ "icy-pub:1\r\n" + "icy-url:https://www.ilovemusic.de\r\n" + "Server: Icecast acc-0.9.10.23 2.4.0-kh8-addlog\r\n" + "Cache-Control: no-cache, no-store\r\n"
				+ "Access-Control-Allow-Origin: *\r\n" + "Access-Control-Allow-Headers: Origin, Accept, X-Requested-With, Content-Type\r\n" + "Access-Control-Allow-Methods: GET, OPTIONS, HEAD\r\n"
				+ "Connection: Close\r\n" + "Expires: Mon, 26 Jul 1997 05:00:00 GMT\r\n" + "\r\n";

		System.out.println("start server...");
		ServerSocket server = new ServerSocket(8080);
		while (true) {
			try {
				Socket socket = server.accept();
				System.out.println("client connected");
				
				// read header
				BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				String line;
				boolean stream = false;
				while((line = reader.readLine()).length() > 1) {
					System.out.println(line);
					if(line.contains("Range:")) {
						stream = true;
					}
				}
				System.out.println("====== END HEADER ======");
				
				DataOutputStream out = new DataOutputStream(socket.getOutputStream());

				String header = "HTTP/1.1 200 OK\nContent-Type: audio/mpeg\nicy-br:128\nCache-Control: no-cache, no-store\nConnection: Close\n\n";
				out.write(iloveresponse.getBytes());
				
				if(!stream) {
					socket.close();
					continue;
				}

				/*
				 * byte[] array = Files.readAllBytes(file.toPath()); for(int i = 0; i < 100;
				 * i++) { System.out.println("send ding " + i); out.write(array); }
				 */

				File file = new File("C:\\Users\\TH\\Desktop\\Test.mp3");
				InputStream fileIn = new FileInputStream(file);
				long stamp = System.currentTimeMillis();
				
				// skip mp3 file tags
				fileIn.skip(128);
				
				// presend first 5 seconds
				/*byte[] pre = new byte[(5 * 192 * 1000) / 8];
				fileIn.read(pre);
				out.write(pre);*/
				
				while (true) {
					byte[] buff = new byte[(192 * 1000) / 8];
					fileIn.read(buff);
					out.write(buff);
					
						long diff = Math.min(0, (System.currentTimeMillis() - stamp));
						
						Thread.sleep(1000 - diff);
						diff = (System.currentTimeMillis() - stamp);
						System.out.println("Wait! Diff: " + diff + " ms");
						stamp = System.currentTimeMillis();
				}

				// out.write(array, 100, array.length - 100);
				// out.write(array, 100, array.length - 100);
				// request(socket.getOutputStream());

				// System.out.println("end");
			} catch (Exception ex) {
				ex.printStackTrace();
				System.out.println("exception " + ex.getMessage());
			}
		}

	}

	static void writeFile(File file, OutputStream out) {

	}

	static void donwloadYT() {
		/*
		 * YoutubeDL.setExecutablePath("C:\\Users\\Justin\\youtube-dl.exe");
		 * 
		 * String videoUrl = "https://www.youtube.com/watch?v=q0WBhOEUD3k";
		 * 
		 * String directory = "C:\\Users\\Justin\\Desktop";
		 * 
		 * YoutubeDLRequest request = new YoutubeDLRequest(videoUrl, directory);
		 * request.setOption("ignore-errors"); request.setOption("output",
		 * "%(title)s.%(ext)s"); request.setOption("retries", 10);
		 * request.setOption("extract-audio"); request.setOption("audio-format", "mp3");
		 * 
		 * YoutubeDLResponse response = null; try { response =
		 * YoutubeDL.execute(request); String stdOut = response.getOut(); } catch
		 * (YoutubeDLException e) { e.printStackTrace(); }
		 */
	}

	static void request(OutputStream to) throws IOException {

		String host = "streams.ilovemusic.de";

		Socket socket = new Socket(host, 80);
		DataOutputStream out = new DataOutputStream(socket.getOutputStream());
		InputStream in = socket.getInputStream();
		String request = "GET /iloveradio5.mp3 HTTP/1.1\nHost: " + host
				+ "\nUser-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.149 Safari/537.36\n\n";
		out.write(request.getBytes());

		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		String line;

		while ((line = reader.readLine()) != null) {
			System.out.println(line);
			if (line.length() < 1) {
				System.out.println("end header");
				break;
			}
		}

		for (int i = 0; i < 50000000; i++) {
			int read = in.read();
			to.write(read);
			System.out.println(read + " " + (char) read);
		}

		socket.close();
	}

}
