docker build . -t knicholas/opca
docker run -p 8091:8091 --name opinionrestca --network app-tier -m 160m -e database-url=mysql://172.26.128.1:3306/ -e PORT=8091 -d knicholas/opca