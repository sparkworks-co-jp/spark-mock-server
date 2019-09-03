package jp.sparkworks.sparkmockserver;

public class Main {

	public static void main(String[] args) throws Exception {

		int port = 8888;// default port

		if (args != null && args.length > 0) {
			try {
				port = Integer.parseInt(args[0]);
			} catch (Exception ex) {
				System.err.println("Port Setting Error!" + args);
			}

		}

		System.out.println("SPARK mock started(PORT:" + port + ") >>>");

		SimpleHttpServer server = new SimpleHttpServer(port);
		server.start();
	}
}