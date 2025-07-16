# Used Car Notifier

Watches [usedcars.toyota.sk](https://usedcars.toyota.sk) for new listings (e.g. Corolla, RAV4) and sends Telegram alerts
with photos and prices.

Built with **Spring Boot 3** + **Java 21**

---

## Features

- Scrapes listings with `Jsoup`
- Local persistence via `users.json` and `cars.json`
- Sends Telegram photo + caption on new/updated listings
- Configurable via `application.yml`
- CLI-free control via Telegram commands and Swagger-UI

---

## Quickstart

###  Requirements

- Java 17+
- Maven
- Telegram bot token
- Telegram bot name
- Admin Telegram ID (Your regular chat id)

### How to run

```bash
mkdir car-notifier
```

```bash
cd car-notifier
```

```bash
git clone https://github.com/yourname/toyota-car-notifier.git
```

```bash
mvn clean install
```

```bash
java -DTELEGRAM_BOT_USERNAME={your_borName} -DTELEGRAM_BOT_TOKEN={your_bot_token} -DTELEGRAM_ADMIN_CHAT_ID={admin_chat_id} -jar target/car-notify-0.0.1.jar
```

Server will start on port 8080, Swagger-UI is accessible from [here](http://localhost:8080/api/car-notify/swagger-ui/index.html)

<details>
  <summary>Instructions for Windows</summary>
    If you use Windows, you are dead to me, these steps are universal and you should have known
</details>

### Local development

#### create .env file in the directory root with values

TELEGRAM_BOT_USERNAME="Your bot"

TELEGRAM_BOT_TOKEN="Your bot token"

TELEGRAM_CLIENT_IDS="Target approved client ids"
