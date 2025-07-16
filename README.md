# 🚗 Used Car Notifier

Watches [usedcars.toyota.sk](https://usedcars.toyota.sk) for new listings (e.g. Corolla, RAV4) and sends Telegram alerts with photos and prices.

Built with **Spring Boot 3** + **Java 21**

---

## ✅ Features

- Scrapes listings with `Jsoup`
- Tracks known cars using `cars.json`
- Sends Telegram photo + caption on new/updated listings
- Configurable via `application.yml`
- CLI-free control via Telegram commands

---

## 🚀 Quickstart

```bash
git clone https://github.com/yourname/toyota-car-notifier.git
cd toyota-car-notifier

# create .env file in the directory root with values
TELEGRAM_BOT_USERNAME="Your bot"
TELEGRAM_BOT_TOKEN="Your bot token"
TELEGRAM_CLIENT_IDS="Target approved client ids"
