# --- ETAP 1: Budowanie aplikacji (Build Stage) ---
FROM node:20-alpine AS build
WORKDIR /app

# Kopiujemy pliki konfiguracyjne i instalujemy zależności
COPY package*.json ./
RUN npm ci

# Kopiujemy resztę kodu i budujemy wersję produkcyjną
COPY . .
RUN npm run build

# --- ETAP 2: Serwowanie plików (Production Stage) ---
FROM nginx:alpine
container_name: personal_budget_frontend

# Kopiujemy zbudowane pliki statyczne z pierwszego etapu do Nginxa
# (Uwaga: Jeśli używasz starego Create React App, zmień /app/dist na /app/build)
COPY --from=build /app/dist /usr/share/nginx/html

# Kopiujemy naszą własną konfigurację Nginxa (stworzymy ją w Kroku 2)
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]