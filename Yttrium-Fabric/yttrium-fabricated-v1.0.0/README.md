# Yttrium

**GPU-accelerated performance mod for Minecraft 1.21.4 using Fabric.**

Yttrium offloads matrix-heavy entity logic from the CPU to the GPU, delivering smoother gameplay and significant FPS gains in entity-dense environments. Designed for client-side use only, it integrates seamlessly into both singleplayer and multiplayer setups.

## 🚀 Features

- Offloads matrix calculations to GPU for faster entity logic
- Up to **+40% FPS boost** in heavy scenes
- Client-side only — no server installation required
- Clean, lightweight codebase with no bundled dependencies

## 🛠️ Requirements

- Minecraft `1.21.4`
- Fabric Loader `0.15.3`
- Fabric API `0.119.2+1.21.4` (declared as required dependency)

## 🧪 Build Instructions

Clone the repo and run:

```bash
./gradlew build

The compiled .jar will appear in build/libs.

📦 Distribution

Available on CurseForge and Modrinth (once approved).
