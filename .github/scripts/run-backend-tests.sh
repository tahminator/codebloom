set -euxo pipefail

java -version
javac -version
echo "JAVA_HOME=$JAVA_HOME"

# build and install
./mvnw -B install -D skipTests --no-transfer-progress -Denv.SKIP=true

# run checkstyle
./mvnw checkstyle:check -Denv.SKIP=true

# install browsers, as well as any required deps.
./mvnw exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="install --with-deps firefox"

# === React-email ===
corepack enable pnpm
cd email
pnpm i --frozen-lockfile
cd ..

./email.sh

./mvnw test -Dspring.profiles.active=ci
