# Liquid Docker Deploy

Since this incubator project hasn't been published to DockerHub,
you need to build the docker images locally.

After pulling the latest version of this repository, you can run the following
command to manage the docker images or start/stop the service compose.

```shell
cd path/to/liquid/liquid-docker
sh liquid-docker-standalone.sh  # see the prompts printed
```

---

## Standalone Deploy

The following third-party consoles are exposed, you are free to manage them manually.

### Nacos
> service discovery, configuration and management

- web console: access [ip:8001/nacos](http://localhost:8001/nacos) with the default user `liquid`, password `liquid`

### MINIO
> high performance distributed storge

- web console: access [ip:9001](http://localhost:9000) with the default user `liquid`, password `liquid_media_minio`