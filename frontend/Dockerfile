# Build stage
FROM node:20-alpine AS build

WORKDIR /app

COPY ./package*.json .
RUN npm install

COPY . .
RUN npm run build

# Run stage
FROM nginx:latest
COPY --from=build /app/dist /var/www/html/dist
EXPOSE 80

ENTRYPOINT ["nginx", "-g", "daemon off;"]
