import net.minecrell.pluginyml.bukkit.BukkitPluginDescription

plugins {
    `java-library`
    id("net.minecrell.plugin-yml.bukkit") version "0.5.2" // Generates plugin.yml
}

dependencies {
    implementation(project(":core"))

    compileOnly("io.papermc.paper:paper-api:1.19.2-R0.1-SNAPSHOT")

    compileOnly("fr.efreicraft:ECATUP:latest.integration")
}

bukkit {
    load = BukkitPluginDescription.PluginLoadOrder.POSTWORLD
    name = "LudosDAC"
    main = "fr.efreicraft.ludos.games.dac.Main"
    apiVersion = "1.19"
    authors = listOf("Nat.io")
    prefix = "DAC"
}