import net.minecrell.pluginyml.bukkit.BukkitPluginDescription

plugins {
    `java-library`
    id("net.minecrell.plugin-yml.bukkit") version "0.5.2" // Generates plugin.yml
}

dependencies {
    implementation(project(":core"))

    compileOnly("io.papermc.paper:paper-api:1.19.2-R0.1-SNAPSHOT")
}

bukkit {
    load = BukkitPluginDescription.PluginLoadOrder.POSTWORLD
    name = "LudosRush"
    main = "fr.efreicraft.ludos.games.rush.Main"
    apiVersion = "1.19"
    authors = listOf("Idir 'Niilyx' NAIT MEDDOUR")
    prefix = "RUSH"
    depend = listOf("LudosCore")
}