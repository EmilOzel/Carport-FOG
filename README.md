# Carport FOG

Java 21, Javalin, Thymeleaf and PostgreSQL.

## Deployment

Production: https://carport.antwi.dk on 164.92.170.39, directory `/opt/carport`.
The existing Caddy container `cit-caddy-1` proxies `carport-app:7070`
over the external Docker network `cit_default`.

Build locally with Java 21 or later and Maven:

```sh
mvn package
```

Copy `target/Carport-FOG.jar`, `Dockerfile`, `.dockerignore`, `compose.yaml`
and `src/main/resources/sql/carport.sql` to the deployment directory,
preserving their relative paths. Create `.env` from `.env.example` with
a randomly generated password; do not commit the real `.env`.

```sh
cd /opt/carport
docker compose up -d --build
docker compose ps
docker compose logs --tail=100 app
```

The SQL initialization runs only on the first start with an empty database
volume. It creates the schema and postal codes, without users or orders.
Database data persists in the `carport_db_data` volume. Do not run
`docker compose down -v` unless you intend to delete that data.

Caddy configuration in `/opt/cit/Caddyfile`:

```caddyfile
api.antwi.dk {
    reverse_proxy api:7070
}

carport.antwi.dk {
    reverse_proxy carport-app:7070
}
```

Validate and reload Caddy after configuration changes:

```sh
docker exec cit-caddy-1 caddy validate --config /etc/caddy/Caddyfile
docker exec cit-caddy-1 caddy reload --config /etc/caddy/Caddyfile
```

Back up the database on the server (copy backups off the server as well):

```sh
cd /opt/carport
umask 077
docker compose exec -T db pg_dump -U carport carport > "carport-$(date +%F-%H%M%S).sql"
```
