import { ConfirmInput, MultiSelect } from "@inkjs/ui";
import { Box, render, Static, Text, useApp } from "ink";
import TextInput from "ink-text-input";
import util from "util";
import { exec } from "child_process";
import { useCallback, useEffect, useState } from "react";

const execAsync = util.promisify(exec);

type Level = "1" | "2" | "3";
type Log = {
  value: string;
  success?: boolean;
};

function Main() {
  const [stage, setStage] = useState(1);
  const [levels, setLevels] = useState<Level[]>([]);
  const [username, setUsername] = useState("");
  const [email, setEmail] = useState("");
  const [name, setName] = useState("");

  const stepForward = useCallback(() => {
    setStage((prev) => prev + 1);
  }, []);

  switch (stage) {
    case 1:
      return (
        <SelectSecurityLevels stepForward={stepForward} onSubmit={setLevels} />
      );
    case 2:
    case 3:
    case 4:
      return (
        <Box flexDirection="column" gap={1}>
          <Text>Fill out the following information:</Text>
          <Box flexDirection="column">
            {stage >= 2 && (
              <EnterName stepForward={stepForward} onSubmit={setName} />
            )}
            {stage >= 3 && (
              <EnterGitHubUsername
                stepForward={stepForward}
                onSubmit={setUsername}
              />
            )}
            {stage == 4 && (
              <EnterGitHubEmail stepForward={stepForward} onSubmit={setEmail} />
            )}
          </Box>
        </Box>
      );
    case 5:
      return (
        <Confirmation
          stepForward={stepForward}
          levels={levels}
          username={username}
          email={email}
          name={name}
        />
      );
    case 6:
      return (
        <RunScript
          levels={levels}
          username={username}
          email={email}
          name={name}
        />
      );
  }
}

function SelectSecurityLevels({
  stepForward,
  onSubmit,
}: {
  stepForward: () => void;
  onSubmit: (levels: Level[]) => void;
}) {
  const handleSubmit = (itms: string[]) => {
    onSubmit(itms as Level[]);
    stepForward();
  };

  const items: {
    label: string;
    value: Level;
  }[] = [
    {
      label: "Security Level 1 (.env.shared)",
      value: "1",
    },
    {
      label: "Security Level 2 (.env.ci | .env.ci-app | .env.staging)",
      value: "2",
    },
    {
      label: "Security Level 3 (.env.production)",
      value: "3",
    },
  ];

  return (
    <Box flexDirection="column" gap={1}>
      <Text bold color="magenta">
        Add a user to git-crypt
      </Text>
      <Text color="blue">
        Select the security levels you would like to give the user.
      </Text>
      <Text color="red">
        NOTE: You must have access to the security levels you are trying to give
        access to.
      </Text>
      <MultiSelect options={items} onSubmit={handleSubmit} />
      <Text>Space to select, Enter to confirm</Text>
    </Box>
  );
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
  levels,
  email,
  username,
  name,
}: {
  stepForward: () => void;
  levels: Level[];
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
        <Box flexDirection="row" gap={1}>
          <Text>Security levels:</Text>
          <Text color={"blue"}>{levels.map((l) => `L${l}`).join(",")}</Text>
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
  levels,
  username,
  name,
  email,
}: {
  levels: Level[];
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
        appendLog(`Fetching GPG key for ${name}...`);
        const res = await fetch(`https://github.com/${username}.gpg`);
        if (!res.ok) throw new Error(`Failed to fetch key: ${res.status}`);
        const key = await res.text();
        appendSuccessfulLog(
          `Fetched GPG key successfully (${key.length} bytes)`,
        );

        appendLog(`Importing key into GPG keyring...`);
        try {
          const { stdout } = await execAsync(`echo "${key}" | gpg --import`);
          appendSuccessfulLog(`GPG import successful: ${stdout.trim()}`);
        } catch (err: any) {
          appendErrorLog(`Failed to import key: ${err.message}`);
          return;
        }

        try {
          await execAsync(`gpg --list-keys --with-colons ${email}`);
          appendSuccessfulLog("Found GPG key import");
        } catch {
          appendErrorLog(`Could not extract email.`);
        }

        appendLog(`Marking key as ultimately trusted...`);
        try {
          const { stdout: fprOut } = await execAsync(
            `gpg --with-colons --fingerprint ${email} | awk -F: '/^fpr:/ {print $10; exit}'`,
          );
          const fingerprint = fprOut.trim();
          if (!fingerprint) throw new Error("Could not find key fingerprint");

          await execAsync(`echo "${fingerprint}:6:" | gpg --import-ownertrust`);
          appendSuccessfulLog(`Marked ${email} as ultimately trusted.`);
        } catch (err: any) {
          appendErrorLog(`Failed to mark trust: ${err.message}`);
        }

        for (const level of levels) {
          appendLog(`Adding key to git-crypt L${level}...`);
          try {
            await execAsync(`git-crypt add-gpg-user -k l${level} ${email}`);
            appendSuccessfulLog(`Added ${name} to git-crypt L${level}`);
          } catch (err: any) {
            appendErrorLog(
              `Failed to add ${name}'s key to L${level}: ${err.message}`,
            );
            continue;
          }

          const { stdout: status } = await execAsync(
            `git status --porcelain .git-crypt/keys/ || true`,
          );
          if (!status.trim()) {
            appendLog(
              `No changes from git-crypt for L${level}; skipping commit amend.`,
            );
            continue;
          }

          try {
            const msg = `Adding ${name} <${email}> to git-crypt L${level}`;
            await execAsync(`git commit --amend -m "${msg}" --no-edit`);
            appendSuccessfulLog(`Amended commit message for L${level}`);
          } catch (err: any) {
            appendErrorLog(
              `Failed to amend commit for L${level}: ${err.message}`,
            );
          }
        }

        appendSuccessfulLog(`All operations completed.`);
        appendLog("");
        appendLog("Please create a pull request with the new commit(s).");
      } catch (err: any) {
        appendErrorLog(`Fatal error: ${err.message}`);
      }
    })();
  }, [username, levels, appendLog, appendErrorLog, appendSuccessfulLog, exit]);

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
