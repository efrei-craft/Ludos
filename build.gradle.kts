plugins {
    `java-library`
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

subprojects {

    group = "fr.efreicraft.ludos"
    version = "1.0-SNAPSHOT"

    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
        javaCompiler.set(project.javaToolchains.compilerFor {
            languageVersion.set(JavaLanguageVersion.of(17))
        })
    }

    // move jars to a folder
    tasks.withType<Jar> {
        if (project.name == "core") {
            destinationDirectory.set(file("${project.rootDir}/run/plugins"))
        } else {
            destinationDirectory.set(file("${project.rootDir}/run/plugins/LudosCore/games"))
        }
        archiveFileName.set("Ludos${project.name.substring(0, 1).toUpperCase()}${project.name.substring(1)}.jar")
    }

    repositories {
        mavenCentral()
        maven("https://repo.papermc.io/repository/maven-public/")
        maven("https://oss.sonatype.org/content/groups/public/")
        maven("https://maven.enginehub.org/repo/")
        maven("https://repo.dmulloy2.net/repository/public/")
    }

}

tasks.register<Exec>("devBuildDockerImage") {
    dependsOn("build")

    workingDir = File("../")
    commandLine("docker", "build", "-t", "dev.efrei-craft/acp/templates/mini", "-f", "Ludos/dev/Dockerfile", ".")
}
