import Toast from "@/components/ui/toast/Toast";
import {
  clubVerificationForm,
  useClubQuery,
  useVerifyPasswordMutation,
} from "@/lib/api/queries/club";
import { PrivateUser } from "@/lib/api/types/user";
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
import { useForm } from "@mantine/form";
import { notifications } from "@mantine/notifications";
import { useQueryClient } from "@tanstack/react-query";
import { zodResolver } from "mantine-form-zod-resolver";
import { useState } from "react";
import { Link, useParams } from "react-router-dom";
import z from "zod";

import ClubSignUpSkeleton from "./ClubSignUpSkeleton";

export default function ClubSignUp({
  id: userId,
  tags: userTags,
}: PrivateUser) {
  const { clubSlug } = useParams<{ clubSlug: string }>();
  const { data, status } = useClubQuery({
    clubSlug,
  });
  const { mutate } = useVerifyPasswordMutation();

  // State Hooks
//   const [password, setPassword] = useState("");
//   const [error, setError] = useState<string | null>(null);
//   const [submitting, setSubmitting] = useState(false);
  const [imgError, setImgError] = useState(false);

  // Query Client
  const queryClient = useQueryClient();

  const form = useForm({
    validate: zodResolver(clubVerificationForm),
    initialValues: {
      password: "",
    },
  });

  const onSubmit = (values: z.infer<typeof clubVerificationForm>) => {
    const id = notifications.show({
      message: "Verifying email... ",
      color: "blue",
    });
    mutate(
      {
        userId: userId,
        password: values.password,
        clubSlug: clubSlug!,
      },
      //   {
      //     onSuccess: async (data) => {
      //       notifications.update({
      //         id,
      //         message: data.message,
      //         color: data.success ? undefined : "red",
      //       });
      //       if (data.success) {
      //         form.reset();
      //         toggle();
      //       }
      //     },
      //   },
      {
        onSuccess: async (data) => {
          notifications.update({
            id,
            message: data.message,
            color: data.success ? undefined : "red",
          });

          if (data.success) {
            form.reset();
          }

          // Refresh queries to update page after obtaining the tag
          queryClient.invalidateQueries({ queryKey: ["auth"] });
          queryClient.invalidateQueries({ queryKey: ["club", clubSlug] });
        },
      },
    );
  };

  if (status === "pending") {
    return <ClubSignUpSkeleton />;
  }

  if (status === "error") {
    return (
      <Toast message="Sorry, something went wrong. Please try again later." />
    );
  }

  if (!data.success) {
    return <Toast message={data.message} />;
  }

  const club = data.payload;
  const clubTag = data.payload.tag!;

  // Check if the User already has the desired club tag
  const hasTag = userTags.some(
    ({ tag }) => tag.toString() === clubTag?.toString(),
  );

//   const handleSubmit = (userId: string, password: string, clubSlug: string) => {
//     if (!password.trim()) {
//       setError("Please enter a password.");
//       return;
//     }
//     setError(null);
//     setSubmitting(true);
//     try {
//       mutate(
//         {
//           userId,
//           password,
//           clubSlug,
//         },
//         {
//           onSuccess: (data) => {
//             if (!data.success) {
//               return notifications.show({
//                 color: "red",
//                 message: data.message,
//               });
//             }

//             notifications.show({
//               color: undefined,
//               message: data.message,
//             });

//             // Refresh queries to update page after obtaining the tag
//             queryClient.invalidateQueries({ queryKey: ["auth"] });
//             queryClient.invalidateQueries({ queryKey: ["club", clubSlug] });
//           },
//         },
//       );
//     } catch {
//       setError("Something went wrong. Try again.");
//     } finally {
//       setSubmitting(false);
//     }
//   };

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
              <form onSubmit={form.onSubmit(onSubmit)}>
                <TextInput
                  {...form.getInputProps("password")}
                  placeholder="Enter the club password"
                  error={form.errors.email}
                  pb="md"
                />

                <Button type="submit" size="xs" disabled={hasTag}>
                  Submit
                </Button>
              </form>
            </>
          : <>
              <Title order={2} ta="center">
                You already are verified for {club.name ?? "this club"}!
              </Title>

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
