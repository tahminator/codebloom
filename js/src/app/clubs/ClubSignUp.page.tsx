import Toast from "@/components/ui/toast/Toast";
import ToastWithRedirect from "@/components/ui/toast/ToastWithRedirect";
import { useAuthQuery } from "@/lib/api/queries/auth";
import {
  useClubQuery,
  useVerifyPasswordMutation,
} from "@/lib/api/queries/club";
import {
  Button,
  Center,
  Stack,
  Text,
  Card,
  Title,
  TextInput,
} from "@mantine/core";
import { notifications } from "@mantine/notifications";
import { useState } from "react";
import { useParams } from "react-router-dom";

import ClubSignUpSkeleton from "./ClubSignUpSkeleton.page";

export default function ClubSignUp() {
  const { clubSlug } = useParams<{ clubSlug: string }>();
  const clubQuery = useClubQuery({ clubSlug });
  const authQuery = useAuthQuery();
  const { mutate } = useVerifyPasswordMutation();
  const [password, setPassword] = useState("");
  const [error, setError] = useState<string | null>(null);
  const [submitting, setSubmitting] = useState(false);

  if (clubQuery.status === "pending" || authQuery.status === "pending") {
    return <ClubSignUpSkeleton />;
  }

  if (clubQuery.status === "error" || authQuery.status === "error") {
    return (
      <Toast message="Sorry, something went wrong. Please try again later." />
    );
  }

  if (!clubQuery.data.success) {
    return <Toast message={clubQuery.data.message} />;
  }

  // If user isnt logged in or theres an error, toast and redirect to login page
  const authenticated = !!authQuery.data.user && !!authQuery.data.session;
  if (!authenticated) {
    return (
      <ToastWithRedirect to="/login" message="You are not authenticated!" />
    );
  }

  const club = clubQuery.data.payload;
  const userId = authQuery.data.user!.id;

  const handleSubmit = (userId: string, password: string, clubSlug: string) => {
    if (!password.trim()) {
      setError("Please enter a password.");
      return;
    }
    setError(null);
    setSubmitting(true);
    try {
      mutate(
        {
          userId,
          password,
          clubSlug,
        },
        {
          onSuccess: (data) => {
            if (!data.success) {
              return notifications.show({
                color: "red",
                message: data.message,
              });
            }

            notifications.show({
              color: undefined,
              message: data.message,
            });
          },
        },
      );
    } catch {
      setError("Something went wrong. Try again.");
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <Center style={{ minHeight: "70vh", padding: 16 }}>
      <Card
        shadow="sm"
        radius="lg"
        p="xl"
        withBorder
        miw={360}
        maw={480}
        w="100%"
      >
        <Stack gap="md">
          <Title order={2} ta="center">
            Register for {club.name ?? "this club"}
          </Title>
          <Text c="dimmed" ta="center">
            Enter the club password to continue.
          </Text>

          <TextInput
            label="Password"
            placeholder="Enter secret password"
            value={password}
            onChange={(e) => setPassword(e.currentTarget.value)}
            error={error || undefined}
            onKeyDown={(e) =>
              e.key === "Enter" && handleSubmit(userId, password, clubSlug!)
            }
            autoComplete="new-password"
            radius="md"
          />

          <Button
            onClick={() => handleSubmit(userId, password, clubSlug!)}
            loading={submitting}
            radius="md"
            fullWidth
          >
            Register
          </Button>
        </Stack>
      </Card>
    </Center>
  );
}
