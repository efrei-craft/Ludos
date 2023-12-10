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
        "games:sumo",
        "games:dac"
)

if (System.getenv("NEXUS_REPOSITORY") == null) {
    includeBuild("../ECATUP") {
        name = "ECATUP"
    }

    includeBuild("../AnimusClient-Paper") {
        name = "AnimusClient-Paper"
    }
}