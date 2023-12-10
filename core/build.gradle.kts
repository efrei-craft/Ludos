import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import net.minecrell.pluginyml.bukkit.BukkitPluginDescription

buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath("com.github.jengelman.gradle.plugins:shadow:2.0.4")
    }
}

plugins {
    `java-library`
    id("net.minecrell.plugin-yml.bukkit") version "0.5.2" // Generates plugin.yml
    id("com.github.johnrengelman.shadow") version "2.0.4"
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.19.2-R0.1-SNAPSHOT")

    implementation(platform("com.intellectualsites.bom:bom-1.18.x:1.18"))
    compileOnly("com.fastasyncworldedit:FastAsyncWorldEdit-Core")
    compileOnly("com.fastasyncworldedit:FastAsyncWorldEdit-Bukkit") { isTransitive = false }

    compileOnly("com.comphenix.protocol:ProtocolLib:4.7.0")
    implementation("commons-io:commons-io:2.11.0")

    compileOnly("fr.efreicraft:AnimusClient:latest.integration")
    compileOnly("fr.efreicraft:AnimusClient-Paper:latest.integration")
    compileOnly("fr.efreicraft:ECATUP:latest.integration")
}

tasks {
    named<ShadowJar>("shadowJar") {
        dependencies {
            exclude(dependency("io.papermc.paper:paper-api:1.19.2-R0.1-SNAPSHOT"))
            exclude(dependency("com.fastasyncworldedit:FastAsyncWorldEdit-Core"))
            exclude(dependency("com.fastasyncworldedit:FastAsyncWorldEdit-Bukkit"))
            exclude(dependency("com.comphenix.protocol:ProtocolLib:4.7.0"))
            exclude(dependency("commons-io:commons-io:2.11.0"))
        }
    }
}

tasks {
    build {
        dependsOn(shadowJar)
    }
}

bukkit {
    load = BukkitPluginDescription.PluginLoadOrder.POSTWORLD
    name = "LudosCore"
    main = "fr.efreicraft.ludos.core.LudosCore"
    apiVersion = "1.19"
    authors = listOf("Antoine BANHA", "Logan TANN", "Aurelien DASSE", "Idir NAIT MEDDOUR")
    prefix = "MINI"
    depend = listOf("WorldEdit", "ProtocolLib", "ECATUP", "AnimusClient-Paper")
    permissions {
        register("ludos.admin") {
            description = "Gives access to all admin permissions"
            default = BukkitPluginDescription.Permission.Default.OP
            children = listOf(
                "ludos.admin.game",
                "ludos.admin.map",
                "ludos.admin.forcewin",
                "ludos.admin.move"
            )
        }
    }
    commands {
        register("game") {
            description = "Manages the games"
            permission = "ludos.admin.game"
            permissionMessage = "You do not have permission to manage the games"
            aliases = listOf("g")
        }
        register("map") {
            description = "Manages the maps"
            permission = "ludos.admin.map"
            permissionMessage = "You do not have permission to manage the maps"
            aliases = listOf("m")
        }
        register("spectate") {
            description = "Puts players in spectator mode"
            aliases = listOf("spec")
        }
        register("forcewin") {
            description = "Makes a team or player win"
            permission = "ludos.admin.forcewin"
        }
        register("move") {
            description = "Moves a player to a team"
            permission = "ludos.admin.move"
        }
    }
}