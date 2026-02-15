# Prerequisites

Please ensure that you install the following (homebrew is a recommendation but not a requirement):

1. Install `gpg`

```bash
brew install gpg
```

1. Install `git-crypt`

```bash
brew install git-crypt
```

# New Codebloom Developer (needs access)

If you don't have access to environment secrets yet, you need to generate a public GPG key and add it to your GitHub profile. If you know what you are doing, you can choose to do so manually. Else, Codebloom has a script you can run to do it for you instead. Please run:

```
just git-crypt-generate-key
```

After getting access,
from your main branch:
run:

```
git pull
```

then run:

```
git-crypt unlock
```

and follow the instructions inside the script.

# Existing Codebloom Developer (has access)

Once the new developer has generated a key, please run:

```
just git-crypt-add-user
```

and follow the instructions inside the script.
