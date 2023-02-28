pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://repo.papermc.io/repository/maven-public/")
    }
}

rootProject.name = "Ludos"

include (
        "core",
        "games:blockparty",
        "games:arena",
        "games:spleef",
        "games:rush",
        "games:sumo"
)

includeBuild("../AnimusClient-Paper") {
    name = "AnimusClient-Paper"
}