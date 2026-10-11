# cargorent-notification-service

In-app notifications and the reminder scheduler for upcoming pickups and returns.

Part of **CarGoRent** – see [cargorent-infra](../cargorent-infra/README.md) for the architecture, how to run
the whole system, demo accounts and the Postman collection.

| | |
|---|---|
| Port | 8085 |
| Schema | `cargorent_notifications` (table `notifications`) |
| Swagger | http://localhost:8085/swagger-ui.html |
| Calls | booking-service (`GET /bookings/upcoming`) |

## Endpoints

| Method | Path | Who |
|---|---|---|
| GET | `/notifications/my`, `/notifications/my/unread-count` | logged in |
| PATCH | `/notifications/{id}/read`, `/notifications/my/read-all` | logged in |
| POST | `/notifications/reminders/run` | admin – run the reminder job now |
| POST | `/notifications` | internal only (booking-service) |

The reminder job runs every 15 minutes (`cargorent.reminders.cron`) and sends one PICKUP_REMINDER / RETURN_REMINDER per booking due today or tomorrow.

## Run

```bash
# needs MySQL: cd ../cargorent-infra && docker compose -f docker-compose.mysql.yml up -d
mvn spring-boot:run
```

## Package layout

`com.cargorent.<service>` → `controller` · `service` · `repository` · `entity` · `dto` · `client` (calls to other services) · `common` (errors, caller identity) · `config`
