# Personal Budget - React Frontend

Nowoczesny panel do zarządzania budżetem stworzony w React.js z Tailwind CSS.

## Funkcjonalności

✨ **Pulpit Analizy** - Przegląd sald kont i wydatków wg kategorii
💳 **Zarządzanie Kontami** - Twórz i usuwaj konta, przeglądaj salda
💰 **Transakcje** - Dodawaj przychody/wydatki, filtruj po kategorii i dacie
📊 **Budżety** - Ustalaj limity budżetowe dla kategorii wydatków
📈 **Analityka** - Szczegółowe podsumowania dochodów i wydatków

## Wymagania

- Node.js 16+ 
- npm lub yarn

## Instalacja

```bash
npm install
```

## Uruchomienie

```bash
# Development server (port 3000)
npm run dev

# Build do produkcji
npm run build

# Podgląd build'u
npm run preview
```

## Konfiguracja API

Upewnij się, że backend Spring Boot działa na `http://localhost:8080`

Vite proxy automatycznie przekierowuje żądania `/api/*` do backendu.

## Struktura projektu

```
src/
├── main.jsx           # Entry point
├── App.jsx            # Main component z nawigacją
├── api.js             # API service (axios)
├── index.css          # Tailwind CSS
└── components/
    ├── Dashboard.jsx  # Pulpit analizy
    ├── Accounts.jsx   # Zarządzanie kontami
    ├── Transactions.jsx # Transakcje
    └── Budgets.jsx    # Budżety
```

## Stack technologiczny

- **React 18** - Interfejs użytkownika
- **Vite** - Build tool
- **Tailwind CSS** - Stylowanie
- **Axios** - HTTP client
- **Lucide React** - Ikony
- **date-fns** - Formatowanie dat

## Cechy UI

🎨 Nowoczesny design z gradient'ami
⚡ Responsywny (mobile, tablet, desktop)
🖱️ Minimum wpisywania - klikalne selekty dla wszystkiego
📱 Intuicyjna nawigacja z ikonami
✅ Walidacja formularzy i komunikaty sukcesu/błędu
📊 Красивe karty dla danych

## Autor

Personal Budget Frontend v1.0
