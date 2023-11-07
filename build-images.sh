mvn clean install

mv target/seasonsforce-ms-invoice-api-1.0-SNAPSHOT.jar api-image/seasonsforce-ms-invoice-api-1.0-SNAPSHOT.jar

cd api-image

docker build -t invoice-api .

cd ../postgres-image

docker build -t invoice-db .

cd ../minio-image

docker build -t invoice-minio .