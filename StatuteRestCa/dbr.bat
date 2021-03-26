docker build . -t knicholas/srca
docker run -p 8090:8090 --name statutesrestca --network app-tier -m 128m -d -v "//c/users/karln/opcastorage/CaliforniaStatutes:/CaliforniaStatutes" -d knicholas/srca