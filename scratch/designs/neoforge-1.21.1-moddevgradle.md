# NeoForge 1.21.1 ModDevGradle Upgrade

## Overview and Scope

Replace the obsolete NeoGradle UserDev build integration with the current
ModDevGradle toolchain while keeping the project on Minecraft 1.21.1. Upgrade
NeoForge to 21.1.249 and use the official 1.21.1 ModDevGradle 2.0.144 template
as the build-DSL compatibility baseline. Preserve the exporter’s Java 21,
Prometheus shadow JAR, Tinactory development dependency, resource expansion,
and server/client/data run behavior.

## Target Design

The build will apply `net.neoforged.moddev` instead of
`net.neoforged.gradle.userdev`, configure `neoForge.version` from the updated
NeoForge property, and declare the main source set under `neoForge.mods`.
Run definitions will be translated from NeoGradle’s `runs` DSL to ModDevGradle
run types and properties. Runtime development dependencies will use the normal
runtime classpath (`localRuntime`) so Prometheus and Tinactory remain available
without being bundled unintentionally.

The Gradle wrapper and supporting Shadow plugin will use versions compatible
with the official 1.21.1 ModDevGradle template. The custom shadow configuration
will attach to Gradle’s supported `implementation` configuration rather than
the removed legacy `compile` configuration. Resource processing stays in place
for `neoforge.mods.toml` and `pack.mcmeta`; it need not adopt the template’s
separate metadata-generation layout.

## Pending Changes

None.
