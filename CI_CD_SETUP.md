# 🚀 CI/CD Setup Instructions

## ✅ Что уже настроено:

1. ✅ **Detekt** - статический анализатор кода для Kotlin
2. ✅ **GitHub Actions CI** - автоматическая сборка, тесты, lint
3. ✅ **GitHub Actions Release** - ручная сборка релизных APK
4. ✅ **Gradle** - настроен для всех проверок

---

## 📝 Что нужно сделать:

### 1. Обновите badge в README.md

Откройте `README.md` и замените:
```markdown
![Android CI](https://github.com/ЗАМЕНИТЕ_НА_ВАШ_USERNAME/ЗАМЕНИТЕ_НА_НАЗВАНИЕ_РЕПО/workflows/Android%20CI/badge.svg)
```

На:
```markdown
![Android CI](https://github.com/ВАШ_USERNAME/MyTracker/workflows/Android%20CI/badge.svg)
```

Например, если ваш username `firee`:
```markdown
![Android CI](https://github.com/firee/MyTracker/workflows/Android%20CI/badge.svg)
```

---

### 2. Запушьте изменения в GitHub

```bash
git add .
git commit -m "Add CI/CD: GitHub Actions, Detekt, Lint"
git push origin main
```

---

### 3. Проверьте что CI работает

1. Зайдите в ваш репозиторий на GitHub
2. Перейдите во вкладку **"Actions"**
3. Вы должны увидеть запущенный workflow **"Android CI"**
4. Дождитесь завершения (обычно 5-10 минут)
5. ✅ **Зелёная галка** = всё работает!

---

## 🔧 Локальная проверка перед пушем

Перед тем как пушить код, можете запустить проверки локально:

### Запустить все проверки сразу:
```bash
./gradlew build
```

### Запустить только тесты:
```bash
./gradlew test
```

### Запустить Android Lint:
```bash
./gradlew lintDebug
```

### Запустить Detekt:
```bash
./gradlew detekt
```

Отчёты будут в:
- **Lint**: `app/build/reports/lint-results-debug.html`
- **Detekt**: `app/build/reports/detekt/detekt.html`

---

## 🎯 Ручная сборка релиза

### Через GitHub UI:

1. Зайдите в **Actions** → **Build Release APK**
2. Нажмите **"Run workflow"**
3. Введите версию (например, `1.0.0`)
4. Нажмите **"Run workflow"**
5. Дождитесь завершения
6. Скачайте APK из **Artifacts**

### Через командную строку:

```bash
./gradlew assembleRelease
```

APK будет в: `app/build/outputs/apk/release/app-release-unsigned.apk`

---

## 📊 Что проверяет CI:

✅ **Build** - компиляция проекта  
✅ **Unit Tests** - юнит-тесты  
✅ **Android Lint** - проверка кода на Android-специфичные проблемы  
✅ **Detekt** - статический анализ Kotlin кода  

---

## 🔐 (Опционально) Подпись APK

Если хотите подписанные APK для релиза:

1. Создайте keystore:
```bash
keytool -genkey -v -keystore mytracker.keystore -alias mytracker -keyalg RSA -keysize 2048 -validity 10000
```

2. Добавьте в GitHub Secrets:
   - `KEYSTORE_FILE` (base64 keystore)
   - `KEYSTORE_PASSWORD`
   - `KEY_ALIAS`
   - `KEY_PASSWORD`

3. Обновите `release.yml` для использования секретов

---

## ❓ Troubleshooting

### CI падает на сборке:
- Проверьте что локально `./gradlew build` проходит успешно
- Посмотрите логи в GitHub Actions

### Detekt находит проблемы:
- Откройте отчёт: `app/build/reports/detekt/detekt.html`
- Исправьте найденные проблемы
- Или настройте правила в `app/detekt.yml`

### Lint находит проблемы:
- Откройте отчёт: `app/build/reports/lint-results-debug.html`
- Исправьте предупреждения
- Критичные ошибки блокируют сборку

---

## 📚 Полезные команды

```bash
# Очистить проект
./gradlew clean

# Собрать проект
./gradlew build

# Запустить все тесты
./gradlew test

# Собрать debug APK
./gradlew assembleDebug

# Собрать release APK
./gradlew assembleRelease

# Посмотреть все доступные задачи
./gradlew tasks
```

---

## ✅ Чеклист для задания:

- [x] GitHub Workflows настроены
- [x] Lint интегрирован
- [x] Gradle правильно сконфигурирован
- [x] Detekt подключён
- [x] Ручные Github Actions для релиза настроены
- [ ] Первый пуш с зелёной галкой ✅

**После пуша - проверьте GitHub Actions и убедитесь что всё зелёное!** 🎉

