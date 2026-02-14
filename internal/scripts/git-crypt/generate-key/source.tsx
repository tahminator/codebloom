import { ConfirmInput } from "@inkjs/ui";
import { Box, render, Static, Text, useApp } from "ink";
import TextInput from "ink-text-input";
import { useState, useCallback, useEffect } from "react";
import util from "util";
import { exec } from "child_process";

const execAsync = util.promisify(exec);

type Log = {
  value: string;
  success?: boolean;
};

function Main() {
  const [stage, setStage] = useState(1);
  const [username, setUsername] = useState("");
  const [email, setEmail] = useState("");
  const [name, setName] = useState("");

  const stepForward = useCallback(() => {
    setStage((prev) => prev + 1);
  }, []);

  switch (stage) {
    case 1:
    case 2:
    case 3:
      return (
        <Box flexDirection="column" gap={1}>
          <Text>Fill out the following information:</Text>
          <Box flexDirection="column">
            {stage >= 1 && (
              <EnterName stepForward={stepForward} onSubmit={setName} />
            )}
            {stage >= 2 && (
              <EnterGitHubUsername
                stepForward={stepForward}
                onSubmit={setUsername}
              />
            )}
            {stage == 3 && (
              <EnterGitHubEmail stepForward={stepForward} onSubmit={setEmail} />
            )}
          </Box>
        </Box>
      );
    case 4:
      return (
        <Confirmation
          stepForward={stepForward}
          username={username}
          email={email}
          name={name}
        />
      );
    case 5:
      return <RunScript username={username} email={email} name={name} />;
  }
}

function EnterName({
  stepForward,
  onSubmit,
}: {
  stepForward: () => void;
  onSubmit: (name: string) => void;
}) {
  const [name, setName] = useState("");
  const [enabled, setEnabled] = useState(() => true);

  const handleSubmit = () => {
    onSubmit(name);
    stepForward();
    setEnabled(false);
  };

  return (
    <Box flexDirection="row" gap={1}>
      <Text>Enter full name:</Text>
      {enabled ? (
        <TextInput value={name} onChange={setName} onSubmit={handleSubmit} />
      ) : (
        <Text>{name}</Text>
      )}
    </Box>
  );
}

function EnterGitHubUsername({
  stepForward,
  onSubmit,
}: {
  stepForward: () => void;
  onSubmit: (username: string) => void;
}) {
  const [username, setUsername] = useState("");
  const [enabled, setEnabled] = useState(() => true);

  const handleSubmit = () => {
    onSubmit(username);
    stepForward();
    setEnabled(false);
  };

  return (
    <Box flexDirection="row" gap={1}>
      <Text>Enter GitHub username:</Text>
      {enabled ? (
        <TextInput
          value={username}
          onChange={setUsername}
          onSubmit={handleSubmit}
        />
      ) : (
        <Text>{username}</Text>
      )}
    </Box>
  );
}

function EnterGitHubEmail({
  stepForward,
  onSubmit,
}: {
  stepForward: () => void;
  onSubmit: (email: string) => void;
}) {
  const [email, setEmail] = useState("");
  const [enabled, setEnabled] = useState(() => true);

  const handleSubmit = () => {
    onSubmit(email);
    stepForward();
    setEnabled(false);
  };

  return (
    <Box flexDirection="row" gap={1}>
      <Text>Enter GitHub email:</Text>
      {enabled ? (
        <TextInput value={email} onChange={setEmail} onSubmit={handleSubmit} />
      ) : (
        <Text>{email}</Text>
      )}
    </Box>
  );
}

function Confirmation({
  stepForward,
  email,
  username,
  name,
}: {
  stepForward: () => void;
  email: string;
  username: string;
  name: string;
}) {
  const { exit } = useApp();

  return (
    <Box flexDirection="column" gap={1}>
      <Text bold color="red">
        Please review the following information.
      </Text>
      <Box flexDirection="column">
        <Box flexDirection="row" gap={1}>
          <Text>Name:</Text>
          <Text color={"blue"}>{name}</Text>
        </Box>
        <Box flexDirection="row" gap={1}>
          <Text>GitHub Username:</Text>
          <Text color={"blue"}>{username}</Text>
        </Box>
        <Box flexDirection="row" gap={1}>
          <Text>GitHub Email:</Text>
          <Text color={"blue"}>{email}</Text>
        </Box>
      </Box>
      <Box flexDirection="row" gap={1}>
        <Text color={"yellow"}>Is this correct?</Text>
        <ConfirmInput onConfirm={stepForward} onCancel={exit} />
      </Box>
    </Box>
  );
}

function RunScript({
  username,
  name,
  email,
}: {
  username: string;
  name: string;
  email: string;
}) {
  const { exit } = useApp();
  const [logs, setLogs] = useState<Log[]>([]);

  const appendLog = useCallback((log: string) => {
    setLogs((prev) => [
      ...prev,
      {
        value: log,
        success: undefined,
      },
    ]);
  }, []);

  const appendSuccessfulLog = useCallback((log: string) => {
    setLogs((prev) => [
      ...prev,
      {
        value: log,
        success: true,
      },
    ]);
  }, []);

  const appendErrorLog = useCallback((log: string) => {
    setLogs((prev) => [
      ...prev,
      {
        value: log,
        success: false,
      },
    ]);
    setTimeout(() => {
      exit();
    }, 2000);
  }, []);

  useEffect(() => {
    (async () => {
      try {
        appendLog(`Generating new GPG key for ${name} <${email}>...`);
        const batch = `
        %no-protection
        Key-Type: RSA
        Key-Length: 4096
        Subkey-Type: RSA
        Subkey-Length: 4096
        Name-Real: ${name}
        Name-Email: ${email}
        Expire-Date: 0
        %commit
      `;

        // 1. Generate key
        try {
          await execAsync(`echo "${batch}" | gpg --batch --gen-key`);
          appendSuccessfulLog(`Generated new GPG keypair.`);
        } catch (err: any) {
          appendErrorLog(`Failed to generate GPG key: ${err.message}`);
          return;
        }

        // 2. Export public key
        appendLog(`Exporting public key...`);
        let publicKey = "";
        try {
          const { stdout } = await execAsync(`gpg --armor --export ${email}`);
          publicKey = stdout.trim();
          appendSuccessfulLog(
            `Exported public key (${publicKey.length} bytes).`,
          );
        } catch (err: any) {
          appendErrorLog(`Failed to export public key: ${err.message}`);
          return;
        }

        // 4. Copy public key to clipboard
        try {
          await execAsync(`echo "${publicKey}" | pbcopy`);
          appendSuccessfulLog(`Copied public key to clipboard.`);
        } catch (err: any) {
          appendErrorLog(`Failed to copy to clipboard: ${err.message}`);
        }

        // 5. Log GitHub instructions
        appendSuccessfulLog("Generation complete!");
        appendLog("");
        appendLog("🔗 Your GPG key has been copied to your clipboard");
        appendLog("Add the public key to GitHub here:");
        appendLog("https://github.com/settings/gpg/new");
        appendLog("");
        appendLog("After uploading, your public key will be available at:");
        appendLog(`https://github.com/${username}.gpg`);
        appendLog(
          "Once uploaded, MAKE SURE THAT THE LINK SHOWS YOUR PUBLIC KEY.",
        );
        appendLog("");
        appendLog(
          "Once complete, reach out to a Codebloom developer to onboard your key to the repository.",
        );
      } catch (err: any) {
        appendErrorLog(`Fatal error: ${err.message}`);
      }
    })();
  }, [
    username,
    name,
    email,
    appendLog,
    appendErrorLog,
    appendSuccessfulLog,
    exit,
  ]);
  return (
    <Static items={logs}>
      {(log, i) => {
        const color = (() => {
          if (log.success === true) {
            return "green";
          }

          if (log.success === false) {
            return "red";
          }

          return undefined;
        })();

        const icon = (() => {
          if (log.success === true) {
            return "✔";
          }

          if (log.success === false) {
            return "✗";
          }

          return undefined;
        })();
        return (
          <Box key={i}>
            <Text color={color}>
              {icon} {log.value}
            </Text>
          </Box>
        );
      }}
    </Static>
  );
}

render(<Main />);
