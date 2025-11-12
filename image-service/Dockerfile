# Använd en officiell Node.js runtime som bas
FROM node:18-alpine

# Skapa app-katalog
WORKDIR /usr/src/app

# Kopiera package.json och package-lock.json
COPY package*.json ./

# Installera appens beroenden
RUN npm install

# Kopiera resten av appens källkod
COPY . .

# Exponera porten som appen körs på
EXPOSE 3000

# Definiera kommandot för att köra appen
CMD [ "npm", "start" ]