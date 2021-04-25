docker build . -t knicholas/srca
docker run -p 8090:8090 --name statutesrestca --network app-tier -m 160m -d -v "//c/users/karln/opcastorage:/opcastorage" -e PORT=8090 -d knicholas/srca