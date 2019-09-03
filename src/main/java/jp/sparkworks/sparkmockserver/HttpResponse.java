package jp.sparkworks.sparkmockserver;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class HttpResponse {

	private Status status;
	private String statusCode;
	private Map<String, String> headers = new HashMap<>();
	private String body;
	private File bodyFile;

	public HttpResponse(Status status) {
		Objects.requireNonNull(status);
		this.status = status;
	}

	public HttpResponse(String statusCode) {
		Objects.requireNonNull(statusCode);
		this.statusCode = statusCode;
	}

	public void addHeader(String string, Object value) {
		this.headers.put(string, value.toString());
	}

	public void setBody(String body) {
		this.body = body;
	}

	public void writeTo(OutputStream out) throws IOException {
		IOUtil.println(out, "HTTP/1.1 " +( statusCode == null ? this.status.toString() : statusCode));

		this.headers.forEach((key, value) -> {
			IOUtil.println(out, key + ": " + value);
		});

		if (this.body != null) {
			IOUtil.println(out, "");
			IOUtil.print(out, this.body);
		} else if (this.bodyFile != null) {
			IOUtil.println(out, "");
			Files.copy(this.bodyFile.toPath(), out);
		}
	}

	public void setBody(File file) {
		Objects.requireNonNull(file);
		this.bodyFile = file;

		String fileName = this.bodyFile.getName();
		String extension = fileName.substring(fileName.lastIndexOf('.') + 1);

		this.addHeader("Content-Type", ContentType.toContentType(extension));
	}
}
