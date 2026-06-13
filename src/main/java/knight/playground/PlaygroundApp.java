package knight.playground;

public class PlaygroundApp
{
	public static void main(String[] args)
	{
		int port = 8080;
		String portEnv = System.getenv("PORT");
		if (portEnv != null && !portEnv.isBlank()) {
			port = Integer.parseInt(portEnv.trim());
		}

		PlaygroundServer server = new PlaygroundServer(port);
		server.start();

		Runtime.getRuntime().addShutdownHook(new Thread(server::stop));
	}
}
