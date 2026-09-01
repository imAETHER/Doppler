plugins {
    id("dev.kikugie.stonecutter")
}
stonecutter active "1.21.10-fabric"

stonecutter registerChiseled tasks.register("chiseledBuild", stonecutter.chiseled) { 
    group = "project"
    ofTask("buildAndCollect")
}

allprojects {
    repositories {
        mavenCentral()
        mavenLocal()
        maven("https://maven.neoforged.net/releases")
        maven("https://maven.fabricmc.net/")

        // modmenu
        maven("https://maven.terraformersmc.com/")

        // voicechats
        maven("https://maven.maxhenkel.de/repository/public")
        maven("https://repo.plasmoverse.com/releases")
    }
}