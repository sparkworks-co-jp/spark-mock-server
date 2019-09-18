package jp.sparkworks.sparkmockserver;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SimpleHttpServer {

	private ExecutorService service = Executors.newCachedThreadPool();

	private String httpStatusCode = null;

	private int port = 0;

	public SimpleHttpServer(int port) {
		this.port = port;
	}

	public void start() {
		try (ServerSocket server = new ServerSocket(port)) {
			while (true) {
				this.serverProcess(server);
			}
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
	}

	private void serverProcess(ServerSocket server) throws IOException {
		Socket socket = server.accept();

		this.service.execute(() -> {
			try (InputStream in = socket.getInputStream(); OutputStream out = socket.getOutputStream();) {

				HttpRequest request = new HttpRequest(in);

				HttpHeader header = request.getHeader();
				httpStatusCode = Status.OK.toString();

				File file = getFile(header.getPath(), header.getMethod());

				if (file != null) {
					this.respondLocalFile(file, out);
				} else {
					this.respondNotFoundError(out);
				}

			} catch (EmptyRequestException e) {
				// ignore
			} catch (IOException e) {
				throw new UncheckedIOException(e);
			} finally {
				try {
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
	}

	private File getFile(String path, HttpMethod httpMethod) {

		System.out.println("REQUEST: " + path + " " + httpMethod);

		StringBuffer sb = new StringBuffer();
		sb.append("response-json");
		sb.append(path.replaceAll("\\?", "/"));
		sb.append("/");
		sb.append(httpMethod);
		sb.append(".json");

		File file = new File(".", sb.toString());

		if (file.exists() && file.isFile()) {
			System.out.println("RESPONSE FILE: " + sb.toString());
			return file;
		} else {
			System.out.println("RESPONSE FILE: " + sb.toString() + " Not Found! ");

			File parent = file.getParentFile();
			if (parent.exists() && parent.isDirectory()) {
				File[] files = parent.listFiles();
				if (files != null && files.length > 0) {
					for (File mfile : files) {
						if (mfile.getName().startsWith(httpMethod.name()) && mfile.getName().endsWith(".json")) {

							httpStatusCode = mfile.getName().substring(mfile.getName().indexOf("_") + 1,
									mfile.getName().indexOf("_") + 1 + 3);
							System.out.println("RESPONSE FILE: " + mfile.getName() + " Found!" + httpStatusCode);
							return mfile;
						}
					}
				}
			}

			return null;
		}

	}

	private void respondNotFoundError(OutputStream out) throws IOException {
		HttpResponse response = new HttpResponse(Status.NOT_FOUND);
		response.addHeader("Content-Type", ContentType.TEXT_PLAIN);
		response.setBody("404 Not Found");
		response.writeTo(out);
	}

	private void respondLocalFile(File file, OutputStream out) throws IOException {
		HttpResponse response = new HttpResponse(httpStatusCode);
		response.setBody(file);
		response.writeTo(out);
	}

}
