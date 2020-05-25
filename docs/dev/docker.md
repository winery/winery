# Docker

## Build and Run Winery Container

```
docker build -t winery .
docker run -p 8080:8080 winery
```

Open a browser and navigate to <http://localhost:8080>.


## Build and Run the Winery CLI Container

```
docker build -t winery-cli -f Dockerfile.cli .
docker run -v <path>:/root/winery-repository -it winery-cli winery -v
```
