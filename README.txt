NOTE: To run database need to create a new docker with following statement:

docker run --name pars-pg-13.3 -p 5432:5432 -e POSTGRES_USER=user -e POSTGRES_PASSWORD=pswd -e POSTGRES_DB=links -d postgres:13.3