plugins {
    id("dev.isxander.modstitch.base") version "0.5.12"
}

fun prop(name: String, consumer: (prop: String) -> Unit) {
    (findProperty(name) as? String?)
        ?.let(consumer)
}

val minecraft = property("deps.minecraft") as String

modstitch {
    minecraftVersion = minecraft

    javaTarget = if (stonecutter.eval(stonecutter.current.version, ">=1.20.5")) 21 else 17

    parchment {
        prop("deps.parchment") { mappingsVersion = it }
    }

    metadata {
        modId = "doppler"
        modName = "Doppler"
        modVersion = "1.2.0"
        modGroup = "im.aether"
        modAuthor = "imAETHER"
        modDescription = "Adds the Doppler effect into Minecraft's sound engine."
        modLicense = "MIT"

        fun <K, V> MapProperty<K, V>.populate(block: MapProperty<K, V>.() -> Unit) {
            block()
        }

        replacementProperties.populate {
            put("mod_issue_tracker", "https://github.com/imAETHER/Doppler/issues")
            put("mod_sources", "https://github.com/imAETHER/Doppler")
            put("pack_format", when (property("deps.minecraft")) {
                    "1.18.2" -> 9
                    "1.19.2" -> 10
                    "1.20.1" -> 15
                    "1.21.1" -> 48
                    "1.21.5" -> 71
                    "1.21.10" -> 88.0
                    else -> throw IllegalArgumentException("Invalid pack format! Add it from here -> https://minecraft.wiki/w/Pack_format")
                }.toString()
            )
        }
    }

    // Fabric
    loom {
        fabricLoaderVersion = "0.18.2"
        configureLoom {}
    }

    // ModDevGradle (NeoForge, Forge, Forgelike)
    moddevgradle {
        enable {
            prop("deps.forge") { forgeVersion = it }
            prop("deps.neoform") { neoFormVersion = it }
            prop("deps.neoforge") { neoForgeVersion = it }
            prop("deps.mcp") { mcpVersion = it }
        }

        defaultRuns()

        configureNeoforge {
            runs.all {
                disableIdeRun()
            }
        }
    }

    mixin {
        addMixinsToModManifest = true
        configs.register("doppler")
    }
}

var modLoader: String = name.split("-")[1]
stonecutter {
    consts(
        "fabric" to (modLoader == "fabric"),
        "neoforge" to (modLoader == "neoforge"),
        "forge" to (modLoader == "forge"),
        "vanilla" to (modLoader == "vanilla")
    )
    swaps["mod_version"] = "\"${modstitch.metadata.modVersion.get()}\","
}

tasks.register<Copy>("buildAndCollect") {
    group = "build"
    from(modstitch.finalJarTask.map { it.archiveFile })
    into(rootProject.layout.buildDirectory.file("libs/$version/$minecraft"))
    rename { _ -> // goober
        "${modstitch.metadata.modName.get()}-${modstitch.metadata.modVersion.get()}+${minecraft}_${modLoader}.jar"
    }
    dependsOn("build")
}

dependencies {
    modstitch.loom {
        modstitchModCompileOnly("com.terraformersmc:modmenu:${property("deps.modmenu_version")}")
        modstitchCompileOnly("de.maxhenkel.voicechat:voicechat-api:2.6.0")
        modstitchCompileOnly("su.plo.voice.api:client:2.1.7")
    }

    modstitch.moddevgradle {
        modstitchCompileOnly("de.maxhenkel.voicechat:voicechat-api:2.6.0")

        modstitchCompileOnly("su.plo.voice.api:client:2.1.7") {
            exclude(group = "com.google.guava", module = "guava")
            exclude(group = "com.google.guava", module = "failureaccess")
            exclude(group = "it.unimi.dsi", module = "fastutil")
        }
    }
}