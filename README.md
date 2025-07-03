# 🍸 Bienvenue au PinkyBar ! 🍸

Plongez dans l’univers vibrant de **PinkyBar**, une application *fullstack* conçue pour gérer vos cocktails, ingrédients et commandes avec style !  
Ce projet combine la robustesse de **Spring Boot** pour le backend, la réactivité de **Vue.js** pour le frontend, et la fiabilité de **PostgreSQL** pour la base de données – le tout orchestré avec **Docker** pour un déploiement fluide et portable.

---

## 🚀 Type de Projet

PinkyBar est une application web moderne composée de :

- **Backend** : développé avec `Spring Boot (Java)`.
- **Frontend** : construit avec `Vue.js`, proposant une interface dynamique et intuitive.
- **Base de Données** : propulsée par `PostgreSQL`, pour une gestion fiable des données.
- **Conteneurisation** : orchestrée avec `Docker` et `Docker Compose`, assurant un environnement cohérent et reproductible.

---

## 📊 Modèle Conceptuel de Données (MCD)

![](https://github.com/wefixte/pinky_le_bar/pinkybar.png?raw=true)

---

## ▶️ Lancement de l’Application

### 🔧 Prérequis

Assurez-vous d’avoir :

- [Docker Desktop](https://www.docker.com/products/docker-desktop) installé et en cours d’exécution
- `docker-compose` disponible depuis votre terminal

---

### 📦 Étapes de Lancement

1. Ouvrez un terminal
2. Naviguez jusqu’au dossier racine du projet (là où se trouve `docker-compose.yml`)
3. Exécutez les commandes suivantes :

```bash
docker-compose build --no-cache
docker-compose up
