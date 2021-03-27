docker build . -t knicholas/opca
docker run -p 8091:8091 --name opinionrestca --network app-tier -m 160m -e database-url=mysql://172.31.224.1:3306/ -d -d knicholas/opca