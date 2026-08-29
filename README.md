# NetScope RU

Android-приложение для мониторинга параметров мобильной сети, истории измерений и экспорта сессий в CSV.

## Сборка

Требования: JDK 17 и Android SDK с API 35.

```bash
gradle testDebugUnitTest assembleDebug
```

APK создается в `app/build/outputs/apk/debug/`.

## GitHub Actions

Workflow `.github/workflows/android.yml` запускает unit-тесты, собирает debug APK и сохраняет APK как artifact для каждого push и pull request.
