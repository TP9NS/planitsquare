# React 앱 빌드
FROM node:20-alpine AS build

WORKDIR /app

COPY package*.json ./
RUN npm install --legacy-peer-deps

COPY . .
RUN npm run build

# nginx로 정적 파일 서빙
FROM nginx:alpine

# nginx 설정
COPY nginx/nginx.conf /etc/nginx/conf.d/default.conf


COPY --from=build /app/dist /usr/share/nginx/html

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
