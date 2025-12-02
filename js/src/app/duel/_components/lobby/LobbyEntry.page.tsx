import { useCreateLobbyMutation } from "@/lib/api/queries/duel/lobby";
import {
  Container,
  Paper,
  Title,
  Text,
  Button,
  Stack,
  TextInput,
  Center,
  Flex,
  Box,
} from "@mantine/core";
import { useState } from "react";
import { useNavigate } from "react-router-dom";

export default function LobbyEntryPage() {
  const { mutate } = useCreateLobbyMutation();
  const navigate = useNavigate();

  const [joinCode, setJoinCode] = useState("");

  const handleJoinLobby = () => {
    if (joinCode.trim().length >= 4) {
      navigate(`/lobby/${joinCode.trim().toUpperCase()}`);
    }
  };

  return (
    <Box
      style={{
        paddingTop: "5vh",
        color: "white",
        justifyContent: "center",
      }}
    >
      <Container
        style={{
          width: "100%",
        }}
      >
        <Stack align="center" mb="5vh">
          <Title
            order={2}
            style={{
              color: "white",
              fontSize: "clamp(1.4rem, 3vw, 2.4rem)",
              textAlign: "center",
            }}
          >
            Create or Join a Lobby
          </Title>
          <Text
            c="dimmed"
            ta="center"
            style={{
              fontSize: "clamp(0.8rem, 2.2vw, 1rem)",
              width: "90%",
            }}
          >
            Generate a lobby code to host, or enter one to join a friend.
          </Text>
        </Stack>
        <Paper
          radius="lg"
          style={{
            width: "100%",
            padding: "5%",
            border: "0.2vw solid rgba(0,255,125,0.5)",
          }}
        >
          <Flex
            direction="row"
            wrap="wrap"
            justify="space-between"
            style={{
              width: "100%",
            }}
          >
            <Paper
              withBorder
              radius="md"
              onClick={() =>
                mutate(undefined, {
                  onSuccess: (data) => {
                    if (data?.lobbyCode) {
                      navigate(`/lobby/${data.lobbyCode}`);
                    }
                  },
                })
              }
              style={{
                cursor: "pointer",
                width: "48%",
                minHeight: "20vh",
                borderColor: "rgba(0,255,125,0.4)",
                transition: "0.2s",
              }}
              onMouseEnter={(e) =>
                (e.currentTarget.style.boxShadow =
                  "0 0 1.5vw rgba(0,255,125,0.5)")
              }
              onMouseLeave={(e) => (e.currentTarget.style.boxShadow = "none")}
            >
              <Center style={{ height: "100%" }}>
                <Stack align="center" gap={4}>
                  <Title
                    order={3}
                    style={{
                      color: "white",
                    }}
                  >
                    Create Lobby
                  </Title>
                  <Text size="sm" c="dimmed">
                    Generate a new lobby code
                  </Text>
                </Stack>
              </Center>
            </Paper>
            <Box
              p="2%"
              style={{
                width: "48%",
                minHeight: "20vh",
                borderRadius: "12px",
                border: "1px solid rgba(0,255,125,0.4)",
                cursor: "text",
                transition: "0.2s",
              }}
              onMouseEnter={(e) =>
                (e.currentTarget.style.boxShadow =
                  "0 0 1.5vw rgba(0,255,125,0.5)")
              }
              onMouseLeave={(e) => (e.currentTarget.style.boxShadow = "none")}
            >
              <Center style={{ height: "100%" }}>
                <Stack align="center" gap={12} style={{ width: "100%" }}>
                  <Title
                    order={3}
                    style={{
                      color: "white",
                      fontSize: "clamp(1rem, 2.5vw, 1.6rem)",
                    }}
                  >
                    Join Lobby
                  </Title>
                  <TextInput
                    placeholder="Enter lobby code"
                    value={joinCode}
                    onChange={(e) => setJoinCode(e.target.value)}
                    radius="md"
                    styles={{
                      input: {
                        backgroundColor: "rgba(255,255,255,0.03)",
                        border: "1px solid rgba(0,255,125,0.4)",
                        color: "white",
                        textAlign: "center",
                      },
                    }}
                    style={{ width: "100%" }}
                  />
                  <Button
                    radius="md"
                    color="green"
                    onClick={handleJoinLobby}
                    style={{
                      backgroundColor: "transparent",
                      border: "1px solid rgba(0,255,125,0.4)",
                      width: "100%",
                      color: "rgba(0,255,125,0.85)",
                    }}
                  >
                    Join
                  </Button>
                </Stack>
              </Center>
            </Box>
          </Flex>
        </Paper>
      </Container>
    </Box>
  );
}
