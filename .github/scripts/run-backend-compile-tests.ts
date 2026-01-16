import { $ } from "bun";

async function main() {
  // fmt
  await $`./mvnw spotless:check`;

  // lint
  await $`./mvnw checkstyle:check`;

  // compile
  await $`./mvnw -B verify -Dmaven.test.skip=true --no-transfer-progress`;
}

main()
  .then(() => {
    process.exit();
  })
  .catch((e) => {
    console.error(e);
    process.exit(1);
  });
