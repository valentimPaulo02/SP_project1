Run the database: 
docker run --name testPostgres -e POSTGRES_PASSWORD=password -e POSTGRES_DB=pds_proj_1 -d -p 5432:5432 postgres

Run the server:
mvn clean compile package
mvn spring-boot:run

Run the setup script:
python populate_tables.py