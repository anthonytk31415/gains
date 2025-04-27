run_server:
	python3 backend/manage.py runserver 0.0.0.0:8000


include .env
export

# Default psql command
psql_connect:
	PGPASSWORD=$(DB_PASSWORD) psql -h $(DB_HOST) -U $(DB_USER) -d $(DB_NAME) -p $(DB_PORT)

# Run a specific SQL command (pass SQL="YOUR_SQL_HERE")
psql_exec:
	PGPASSWORD=$(DB_PASSWORD) psql -h $(DB_HOST) -U $(DB_USER) -d $(DB_NAME) -p $(DB_PORT) -c "$(SQL)"