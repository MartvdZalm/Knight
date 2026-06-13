const editor = CodeMirror.fromTextArea(document.getElementById("source-editor"), {
	lineNumbers: true,
	theme: "material",
	indentUnit: 4,
	tabSize: 4,
	indentWithTabs: false,
});

const runButton = document.getElementById("run-button");
const programOutput = document.getElementById("program-output");
const diagnosticsOutput = document.getElementById("diagnostics-output");
const cppOutput = document.getElementById("cpp-output");
const statusBar = document.getElementById("status-bar");
const tabs = document.querySelectorAll(".tab");
const tabContents = document.querySelectorAll(".tab-content");

function setStatus(message, type) {
	statusBar.textContent = message;
	statusBar.className = type || "";
}

function activateTab(tabName) {
	tabs.forEach((tab) => {
		tab.classList.toggle("active", tab.dataset.tab === tabName);
	});
	tabContents.forEach((content) => {
		content.classList.toggle("active", content.id === `tab-${tabName}`);
	});
}

tabs.forEach((tab) => {
	tab.addEventListener("click", () => activateTab(tab.dataset.tab));
});

function formatDiagnostics(result) {
	const lines = [];

	if (result.errors && result.errors.length > 0) {
		lines.push("Errors:");
		result.errors.forEach((error) => lines.push(`  ${error}`));
	}

	if (result.warnings && result.warnings.length > 0) {
		if (lines.length > 0) {
			lines.push("");
		}
		lines.push("Warnings:");
		result.warnings.forEach((warning) => lines.push(`  ${warning}`));
	}

	if (result.programError) {
		if (lines.length > 0) {
			lines.push("");
		}
		lines.push("Runtime stderr:");
		lines.push(result.programError);
	}

	if (lines.length === 0) {
		return "No diagnostics.";
	}

	return lines.join("\n");
}

async function runProgram() {
	runButton.disabled = true;
	setStatus("Compiling and running...", "running");
	activateTab("output");
	programOutput.textContent = "Running...";

	try {
		const response = await fetch("/api/run", {
			method: "POST",
			headers: {
				"Content-Type": "application/json",
			},
			body: JSON.stringify({
				source: editor.getValue(),
			}),
		});

		const result = await response.json();
		diagnosticsOutput.textContent = formatDiagnostics(result);
		cppOutput.textContent = result.generatedCpp || "No generated C++ available.";

		if (result.success) {
			programOutput.textContent = result.programOutput || "(no output)";
			setStatus(`Finished with exit code ${result.exitCode}`, "success");
		} else {
			programOutput.textContent = result.programOutput || "(no output)";
			setStatus("Run failed", "error");
			if (result.errors && result.errors.length > 0) {
				activateTab("diagnostics");
			}
		}
	} catch (error) {
		programOutput.textContent = "(no output)";
		diagnosticsOutput.textContent = `Request failed: ${error.message}`;
		setStatus("Request failed", "error");
		activateTab("diagnostics");
	} finally {
		runButton.disabled = false;
	}
}

runButton.addEventListener("click", runProgram);

document.addEventListener("keydown", (event) => {
	if ((event.ctrlKey || event.metaKey) && event.key === "Enter") {
		event.preventDefault();
		runProgram();
	}
});

setStatus("Ready");
