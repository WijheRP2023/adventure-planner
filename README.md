# Adventure Planner

Adventure Planner is a RuneLite plugin that turns account progression into short, varied tasks. It combines daily and weekly checklists, bank-aware skill planning, recurring run timers and local shooting-star tracking in one panel.

De plannerinterface en actieve taken zijn in het Nederlands en Engels beschikbaar. Kies de taal via **Taal / Language** in de configuratie.

## Features

- Generates concrete 30, 60 and 90 minute tasks for combat and non-combat skills.
- Uses the player's levels, diary direction and recent tasks to keep the rotation varied.
- Scans the bank locally and uses available supplies before suggesting purchases.
- Builds supply chains when materials are missing, such as gathering logs before Fletching.
- Combines bank and Seed Vault contents for Farming tasks and recognises the Seed box.
- Includes specific methods and targets instead of generic train a skill instructions.
- Supports activities such as Tempoross, Wintertodt, Guardians of the Rift, Giants' Foundry, Mahogany Homes, Hunter Rumours, Farming Contracts and Shooting Stars.
- Keeps daily and weekly tasks together, with per-task configuration and automatic completion for supported in-game events.
- Tracks herb, tree, hardwood, spirit tree, seaweed and Birdhouse locations separately.
- Shows per-location run timers, readiness bars, route hints, map markers and optional sounds.
- Lets players skip, replace, finish or manually stop automatic task generation.
- Stores progress separately for each RuneLite profile.

## Privacy and fair play

Adventure Planner has no network calls, telemetry or crowdsourcing. Bank item names and quantities, timers, notes and task progress are stored only in the local RuneLite configuration for the active profile.

The plugin does not click, move the player, interact with the game or automate gameplay. It only presents information derived from data already available to the RuneLite client.

## Important limitations

- Open the bank to create or refresh the local bank snapshot.
- Open the Seed Vault to include its contents in Farming decisions; a bank scan alone is enough to start normal task generation.
- Automatic checklist completion is event-based and is only available for activities the plugin can identify reliably.
- Shooting Stars are detected only in the currently loaded scene and world. The plugin does not provide a global star-location service.
- Run timers begin after a supported interaction is observed and remain estimates based on the selected activity.

## Installation

After acceptance into the RuneLite Plugin Hub:

1. Open RuneLite.
2. Open the Plugin Hub.
3. Search for **Adventure Planner**.
4. Install and enable the plugin.

## Building from source

Requirements: JDK 17 or newer. The plugin source itself targets Java 11 for RuneLite compatibility.

```powershell
.\gradlew.bat clean test jar
```

The built plugin JAR is written to `build/libs/`. For development, import the repository as a Gradle project and run the `run` task.

Task definitions live in `TaskCatalog` and diary goals in `DiaryGoalCatalog`.

## Contributing

Bug reports and focused pull requests are welcome. Please include the RuneLite version, the selected language and clear reproduction steps. Never include account credentials or private account data.

## License

Adventure Planner is available under the [BSD 2-Clause License](LICENSE).

Adventure Planner is an independent community plugin and is not endorsed by Jagex or the RuneLite project.
