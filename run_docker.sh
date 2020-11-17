docker rm fi_ncp_server
docker volume create --name=fi_np_server_volume
docker build --tag=fi_ncp_server:latest --rm=true .
docker run \
  --name=fi_ncp_server \
  --publish=9001:9001 \
  --volume=fi_np_server_volume:/var/lib/fi_ncp_server \
  fi_ncp_server:latest
