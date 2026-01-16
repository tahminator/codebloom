import { $ } from "bun";
import { cyan } from "./colors";

let be: Bun.Subprocess<"ignore", Bun.BunFile, "inherit"> | undefined;

async function start() {
  try {
    console.log("Starting backend instance...");

    await $`java -version`;
    await $`javac -version`;
    console.log(`JAVA_HOME=${process.env.JAVA_HOME}`);

    const logFile = Bun.file("backend.log");
    be = Bun.spawn(
      ["./mvnw", "-Dspring-boot.run.profiles=ci", "spring-boot:run"],
      {
        stdout: logFile,
      },
    );

    console.log("Waiting for backend to become ready.");

    let ready = false;
    const attempts = 30;

    for (let i = 1; i <= attempts; i++) {
      try {
        const response = await fetch("http://localhost:8080/api");
        const data = (await response.json()) as { success: boolean };

        if (data.success === true) {
          console.log("Backend is up!");
          ready = true;
          break;
        }
      } catch (_) {}

      console.log(`Waiting for backend... (${i}/${attempts})`);
      await Bun.sleep(2000);
    }

    if (!ready) {
      console.error("Backend failed to start in time.");
      end();
    }

    console.log("backend ready");
  } catch (e) {
    console.error(e);
    end();
  }
}

async function end() {
  if (be) {
    if (!be.killed) {
      be.kill();
    }
    console.log(cyan("=== BACKEND LOGS ==="));
    console.log(cyan(await Bun.file("backend.log").text()));
    console.log(cyan("=== BACKEND LOGS ==="));
  }
  process.exit(1);
}

export const backend = {
  start,
  end,
};
