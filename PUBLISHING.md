# Publishing Adventure Planner

The source tree is prepared for a RuneLite Plugin Hub submission. Plugin Hub distribution requires this public source repository and a Plugin Hub pull request.

## Before the first push

- Create a public repository with a meaningful name, for example `adventure-planner`.
- Commit all source files, the Gradle wrapper, README, changelog and BSD 2-Clause license.
- Push the commit and copy its full 40-character hash.
- Do not commit `.gradle/`, `build/`, IDE files or local RuneLite data.

## Plugin Hub submission

Create a file named `adventure-planner` in the Plugin Hub repository's `plugins` directory:

```text
repository=https://github.com/WijheRP2023/adventure-planner.git
commit=FULL_40_CHARACTER_COMMIT_HASH
```

Open a pull request against `runelite/plugin-hub` and resolve all automated review results. Every update after acceptance uses a new commit hash in that manifest.

## Release verification

Run before every submission:

```powershell
.\gradlew.bat clean test jar
```

Also verify in RuneLite that the toolbar icon appears, both languages render correctly, the bank scan starts a task, task controls work, and disabled daily/run settings disappear from the panel.
