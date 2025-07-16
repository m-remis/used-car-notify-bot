# Used Car Notifier

Watches [usedcars.toyota.sk](https://usedcars.toyota.sk) for new listings (e.g. Corolla, RAV4) and sends Telegram alerts
with photos and prices.

Built with **Spring Boot 3** + **Java 21**

---

## Features

- Scrapes listings with `Jsoup`
- Local persistence via `users.json` and `cars.json`
- Approval of new users and user management
- Configurable notifications per user
- Configurable price limit and multiple configurable car models
- Sends Telegram photo + caption on new/updated listings
- Configurable via `application.yml`
- CLI-free control via Telegram commands and Swagger-UI

---

![arch](docs/car_notif_diagram.svg)

### App
#### Simple storage using 2 json files
- on startup, app checks for users.json and cars.json, if it's present, it's content is loaded in memory, each operation that takes place is then performed against records that are in memory and after each action the files are overwritten by the content of the in-memory map, this way you can restart the app all the time but the data stays there
#### Background job
- configurable, responsible for scrapping and sending notifications to the users


    1. it scrapes data of the listings from the website (based on configurable filter)
    2. checks against in-memory records, detects which records were added since the last run
    3. fetches admin-approved users that have notifications enabled
    4. sends notifications to the target telegram chatIds 

## Quickstart

### Requirements

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
git clone https://github.com/m-remis/used-car-notify-bot.git
```

```bash
mvn clean install
```

```bash
java -DTELEGRAM_BOT_USERNAME={your_borName} -DTELEGRAM_BOT_TOKEN={your_bot_token} -DTELEGRAM_ADMIN_CHAT_ID={admin_chat_id} -jar target/car-notify-0.0.1.jar
```

Server will start on port 8080, Swagger-UI is accessible
from [here](http://localhost:8080/api/car-notify/swagger-ui/index.html)

<details>
  <summary>Instructions for Windows</summary>
    If you use Windows, you are dead to me, these steps are universal and you should have known
</details>

![swagger](docs/swagger.png)

<h3 align="center">Demo</h3>

<p align="center">
  <img src="docs/demo.gif" alt="demo">
</p>
