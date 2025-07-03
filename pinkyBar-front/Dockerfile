# Builder stage
FROM node:20-alpine AS builder

WORKDIR /app

COPY package*.json ./
RUN npm install

COPY . .

# Build avec la variable d'environnement de prod (par défaut)
ARG VITE_API_URL= /
ENV VITE_API_URL=$VITE_API_URL

RUN npm ci
RUN npm run build

# Serveur nginx
FROM nginx:stable-alpine

COPY --from=builder /app/dist /usr/share/nginx/html
COPY nginx/nginx.conf /etc/nginx/nginx.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]