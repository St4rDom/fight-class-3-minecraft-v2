# Fight Class 3 Mod — Forge 1.20.1

## Required Dependencies
- **Forge 1.20.1-47.2.0** — https://files.minecraftforge.net/
- **Player Animator** by KosmX — download from CurseForge, 1.20.1 Forge version

---

## How to build and upload to GitHub

### Step 1: Create a GitHub Actions workflow
Since `.github` is a hidden folder that Mac won't show in Finder, create the workflow DIRECTLY on GitHub:

1. Create a new repo on GitHub
2. Click **Add file → Create new file**
3. Type the filename: `.github/workflows/build.yml`
4. Paste this content:

```yaml
name: Build Fight Class 3 Mod

on:
  push:
    branches: [ main, master ]
  workflow_dispatch:

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4.2.2
      - uses: actions/setup-java@v4.7.0
        with:
          distribution: temurin
          java-version: 17
      - uses: actions/cache@v4.2.3
        with:
          path: |
            ~/.gradle/caches
            ~/.gradle/wrapper
          key: ${{ runner.os }}-gradle-${{ hashFiles('**/*.gradle*') }}
      - run: chmod +x gradlew
      - run: ./gradlew build --no-daemon --stacktrace
        env:
          GRADLE_OPTS: "-Xmx3g"
      - uses: actions/upload-artifact@v4.6.2
        with:
          name: fightclass3-jar
          path: build/libs/fightclass3-*.jar
          retention-days: 30
```

5. Click **Commit new file**

### Step 2: Upload mod files
- Cmd+Shift+. to show hidden files in Finder
- Unzip, open the `fight-class-3` folder
- Select all files and drag to GitHub upload
- Commit — Actions will build automatically

---

## Features

### Stats (press ; to open)
| Stat | Max | Effect |
|------|-----|--------|
| Strength | 100 | +0.1 attack damage per point |
| Vitality | 500 | +1 heart per 10 points (max +50 hearts) |
| Agility | 100 | +0.002 movement speed per point |

### Specialities
**Insanity** (from Recollection item):
- Day: Darkness effect + all mobs within 50 blocks glow red
- Night: Night Vision, darkness removed
- Always: Strength III, Regen III, Absorption II, Health Boost V, Haste III

**Pain Tolerance** (from Willpower item):
- Always: Resistance V, Fire Res III, Regen III, Absorption II, Health Boost V

### Jiu Ji-Tae (rare overworld spawn)
- 40 hearts (80 HP), superhuman speed, attacks EVERYTHING within 35 blocks
- Kill → **I'M FREE** achievement + **Psychopath** title + 20% chance: Recollection drops
- Kill with ≤4 hearts → **Castle of Fortitude** achievement + Willpower drops (guaranteed)

### Commands (`/fctmod` — requires OP)
```
/fctmod SetStrength 0-100
/fctmod SetVitality 0-500
/fctmod SetAgility 0-100
/fctmod GetAllAch
/fctmod GetSpeciality insanity
/fctmod GetSpeciality paintolerance
```

### Punch item
- Always in hotbar slot 1 (restored on respawn)
- Invisible in hand — looks like bare-fist punching
- Player Animator punch animation plays on attack
- Damage: base wooden sword + Strength stat bonus

### ; key conflict with Companion Mod
If you have Companion Mod installed too, both use `;`. Go to **Options → Controls** and rebind one of them.
