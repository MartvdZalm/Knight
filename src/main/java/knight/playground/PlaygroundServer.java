package knight.playground;

import java.util.HashMap;
import java.util.Map;

import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;

public final class PlaygroundServer
{
	private static final String DEFAULT_FRAME_ANCESTORS = "https://martvanderzalm.com";

	private final int port;
	private Javalin app;

	public PlaygroundServer(int port)
	{
		this.port = port;
	}

	public void start()
	{
		app = Javalin.create(config -> {
			config.staticFiles.add("/public");
			config.showJavalinBanner = false;
		});

		app.before(this::applySecurityHeaders);
		app.get("/", ctx -> ctx.redirect("/index.html"));
		app.get("/health", ctx -> ctx.status(HttpStatus.OK).result("OK"));
		app.post("/api/run", this::handleRun);

		app.start(port);
	}

	public void stop()
	{
		if (app != null) {
			app.stop();
		}
	}

	private void applySecurityHeaders(Context ctx)
	{
		String allowedOrigins = System.getenv().getOrDefault("KNIGHT_ALLOWED_FRAME_ANCESTORS", DEFAULT_FRAME_ANCESTORS);
		ctx.header("Content-Security-Policy", "frame-ancestors 'self' " + allowedOrigins);
	}

	private void handleRun(Context ctx)
	{
		RunRequest request = ctx.bodyAsClass(RunRequest.class);
		if (request == null || request.getSource() == null) {
			ctx.status(HttpStatus.BAD_REQUEST).json(Map.of("success", false, "errors",
					java.util.List.of("Request body must include a \"source\" field")));
			return;
		}

		PlaygroundResponse response = PlaygroundService.compileAndRun(request.getSource());
		ctx.json(toJsonMap(response));
	}

	private Map<String, Object> toJsonMap(PlaygroundResponse response)
	{
		Map<String, Object> json = new HashMap<>();
		json.put("success", response.isSuccess());
		json.put("errors", response.getErrors());
		json.put("warnings", response.getWarnings());
		json.put("generatedCpp", response.getGeneratedCpp());
		json.put("programOutput", response.getProgramOutput());
		json.put("programError", response.getProgramError());
		json.put("exitCode", response.getExitCode());
		return json;
	}

	public static class RunRequest
	{
		private String source;

		public String getSource()
		{
			return source;
		}

		public void setSource(String source)
		{
			this.source = source;
		}
	}
}
