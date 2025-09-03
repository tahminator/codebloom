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
  Image,
} from "@mantine/core";
import { notifications } from "@mantine/notifications";
import { useQueryClient } from "@tanstack/react-query";
import { useState } from "react";
import { Link, useParams } from "react-router-dom";

import ClubSignUpSkeleton from "./ClubSignUpSkeleton";

export default function ClubSignUp() {
  const { clubSlug } = useParams<{ clubSlug: string }>();
  const clubQuery = useClubQuery({ clubSlug });
  const authQuery = useAuthQuery();
  const { mutate } = useVerifyPasswordMutation();

  // State Hooks
  const [password, setPassword] = useState("");
  const [error, setError] = useState<string | null>(null);
  const [submitting, setSubmitting] = useState(false);
  const [imgError, setImgError] = useState(false);

  // Query Client
  const queryClient = useQueryClient();

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
  const clubTag = clubQuery.data.payload.tag!;

  const userId = authQuery.data.user!.id;
  const userTags = authQuery.data.user!.tags;

  // Check if the User already has the desired club tag
  const hasTag = userTags.some(
    ({ tag }) => tag.toString() === clubTag?.toString(),
  );

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

            // Refresh queries to update page after obtaining the tag
            queryClient.invalidateQueries({ queryKey: ["auth"] });
            queryClient.invalidateQueries({ queryKey: ["club", clubSlug] });
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
          {club.splashIconUrl && !imgError && (
            <Image
              src={club.splashIconUrl}
              alt=""
              radius="md"
              w={160}
              maw={200}
              fit="contain"
              mx="auto"
              onError={() => setImgError(true)}
            />
          )}
          {!hasTag ?
            <>
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
            </>
          : <>
              <Title order={2} ta="center">
                You already are verified for {club.name ?? "this club"}!
              </Title>

              <TextInput
                label="Password"
                value="Enter secret password"
                readOnly
                disabled
                radius="md"
              />
              <Button
                component={Link}
                to="/dashboard"
                radius="md"
                fullWidth
                mt="md"
              >
                Go to Dashboard
              </Button>
            </>
          }
        </Stack>
      </Card>
    </Center>
  );
}
