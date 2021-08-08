docker build . -f Dockerfile-Openshift -t knicholas/srca
docker run -p 8090:8090 --name statutesrestca --network app-tier -m 160m -d -v "//c/Users/karln/opcastorage:/opcastorage" -e californiastatutesloc=/opcastorage/CaliforniaStatutes -e PORT=8090 -d knicholas/srca
