# ATTENTION.
# Ce Dockerfile ne doit pas être utilisé en production.
# Il doit également être utilisé à la racine de tous les projets Animus.
# Il est préférable de lancer la tâche gradle "devBuildDockerImage" pour créer l'image.

# L'image de base ci-dessous est locale, vous pouvez donc la modifier à votre guise.
# Toutefois, il est préférable de l'utiliser telle quelle.

FROM registry.efreicraft.fr/templates/mini.dev:latest

# A noter que le dossier /data est applicable pour les serveurs de type Paper.
# Pour les serveurs de type Velocity, le dossier est /server.

COPY --chown=1000:1000 ./AnimusClient-Paper/build/libs /data/plugins
COPY --chown=1000:1000 ./AnimusClient/build/libs/AnimusClient.jar /data/libraries/AnimusClient.jar

COPY --chown=1000:1000 ./ECATUP/build/libs /data/plugins
COPY --chown=1000:1000 ./Ludos/run/plugins /data/plugins

COPY --chown=1000:1000 ./Ludos/dev/paper-global.yml /data/config/paper-global.yml