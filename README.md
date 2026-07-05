# ThornedShields (Paper port of "ales' Thorned Shields")

A from-scratch Paper plugin reproducing the uploaded Fabric mod's behavior
("Allows Thorns to work properly on Shields"). It's a rewrite, not a
repackage — Fabric mods and Paper plugins use completely different APIs.

## What it does

- Lets you combine a Thorns book onto a Shield in an anvil (vanilla
  normally refuses this).
- When a player blocks an attack with a Thorns-enchanted shield, there's a
  chance to reflect damage back onto the attacker, and (optionally) consume
  some shield durability.
- All numbers are configurable in `config.yml`. Defaults match the original
  mod exactly (pulled from its compiled config class):
  - Thorns I: 50% chance, 2.0 damage reflected
  - Thorns II: 75% chance, 4.0 damage reflected
  - Thorns III: 100% chance, 6.0 damage reflected
  - 2 durability consumed per proc (toggle-able)

## Verified against your exact server build

I checked this against the real Paper 26.2 javadocs (jd.papermc.io/paper/26.2)
and PaperMC's official docs/GitHub, not from memory:

- **JDK 25 is required** to compile against `paper-api` 26.2 — the Gradle
  toolchain is set to 25.
- **Artifact coordinate**: `io.papermc.paper:paper-api:26.2.build.+` — this
  matches your server's own startup line exactly (`Implementing API version
  26.2.build.24-alpha`), so Gradle pulls the matching build.
- **`plugin.yml`**: `api-version` needs to be `'26.2'` on this version line
  — a bare `'26'` or an old `'1.21'`-style value fails to load on 26.x.
- Every Bukkit/Paper class/method the plugin uses — `LivingEntity`,
  `Player#isBlocking()`, `PrepareAnvilEvent`, `EntityDamageByEntityEvent`,
  `Registry.ENCHANTMENT`, `ItemStack#getEnchantmentLevel`,
  `org.bukkit.inventory.meta.Damageable` (item durability), and
  `Damageable#damage(double, Entity)` (entity damage) — confirmed still
  present, unchanged, in the 26.2 javadocs.

So this should build clean as-is. I can't run the actual PaperMC Gradle
build from this sandbox (no network route to `repo.papermc.io` here), but
the source compiles with no syntax/type errors against stub signatures
matching the verified API.

## Building it

1. Make sure you have **JDK 25** installed and on your PATH.
2. From the project folder, with internet access:
   ```
   gradle build
   ```
   (or open in IntelliJ/VS Code with the Gradle extension and build from
   there). No Gradle installed? Grab it from gradle.org.
3. Output jar: `build/libs/thorned-shields-1.0.0.jar` (shadow/fat jar, no
   other dependencies needed).
4. Drop it in your server's `plugins/` folder and restart.

If the build still fails on some method, it's likely a build-24-alpha-specific
tweak that landed after the docs snapshot I checked — paste me the compiler
error and I'll fix the source immediately.

## Files

- `src/main/java/com/thornedshields/paper/ThornedShieldsPlugin.java` — plugin entrypoint
- `src/main/java/com/thornedshields/paper/ThornedShieldsListener.java` — the actual logic
- `src/main/java/com/thornedshields/paper/ThornedShieldsConfig.java` — config loader
- `src/main/resources/plugin.yml`, `config.yml`
