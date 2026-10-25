# 🎓 CampusConnect — L'Assistant Universitaire Intelligent & Gestionnaire d'Études

<p align="center">
  <img src="https://img.shields.io/badge/Platform-Android-3DDC84?style=for-the-badge&logo=android&logoColor=white" alt="Platform" />
  <img src="https://img.shields.io/badge/Language-Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white" alt="Language" />
  <img src="https://img.shields.io/badge/UI-Jetpack%20Compose%20(Material%203)-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white" alt="Compose" />
  <img src="https://img.shields.io/badge/Architecture-MVVM%20%2B%20Clean-FF6F00?style=for-the-badge" alt="Architecture" />
  <img src="https://img.shields.io/badge/AI-OpenRouter%20%2F%20GPT-00A67E?style=for-the-badge&logo=openai&logoColor=white" alt="AI" />
</p>

---

## 📌 Présentation

**CampusConnect** est une application Android moderne conçue pour accompagner les étudiants dans leur quotidien académique. Développée entièrement avec **Jetpack Compose** et **Material 3**, elle combine la gestion d'emploi du temps, le suivi des devoirs et devoirs prioritaires, un agenda d'événements de campus, ainsi qu'un **Tuteur Académique propulsé par l'Intelligence Artificielle (CampusBot)**.

L'application mise sur une interface épurée, réactive et intuitive, disponible en **mode clair et mode sombre profond (OLED)**.

---

## ✨ Fonctionnalités Principales

### 🏠 1. Tableau de Bord Dynamique (Home)
- **Salutation Contextuelle** personnalisée selon le moment de la journée (Bonjour, Bon après-midi, Bonsoir).
- **Statistiques en Temps Réel** : vue rapide sur les cours programmés aujourd'hui, les devoirs en attente et les événements à venir.
- **Tâches Prioritaires** : aperçu direct des échéances urgentes avec possibilité de les marquer comme terminées en un clic.
- **Raccourcis d'Actions Rapides** pour ajouter instantanément un cours, un devoir ou un événement.

### 🤖 2. CampusBot — Tuteur & Assistant Académique IA
Un assistant conversationnel intelligent propulsé par l'API **OpenRouter** (modèle `openai/gpt-3.5-turbo-16k` ou personnalisable) :
- 💡 **Explication Simple de Concepts** : vulgarise les théories et notions complexes grâce à des analogies accessibles (technique de Feynman).
- 📝 **Générateur de Quiz & Flashcards** : crée des fiches de révision instantanées et des questionnaires à choix multiples pour tester ses connaissances.
- 🎯 **Méthodologie & Organisation** : conseils personnalisés sur la gestion du temps (Pomodoro, priorisation Eisenhower, préparation aux examens).
- 🚀 **Bouton Flottant & BottomSheet** : accessible depuis n'importe quel écran de l'application sans interrompre sa navigation.
- 🔒 **Mode Résilient & Hors-Ligne** : suggestions et réponses intégrées même en l'absence de réseau ou sans clé API configurée.

### 📅 3. Emploi du Temps & Calendrier (Timetable)
- **Double Mode d'Affichage** :
  - **Vue Agenda Journalière** : timeline heure par heure avec indicateurs visuels des créneaux en cours et à venir.
  - **Vue Grille Hebdomadaire** : vision d'ensemble du lundi au dimanche.
- **Personnalisation des Matières** : sélecteur de couleurs par discipline, indication des salles de cours et des professeurs.

### ✅ 4. Gestionnaire de Tâches & Devoirs (Tasks)
- **Niveaux de Priorité** : Basse, Moyenne, Haute avec codes couleurs distinctifs.
- **Recherche & Filtrage Avancés** : filtre par statut (*Toutes, En attente, Terminées*) ou par urgence (*Priorité Haute*).
- **Barre de Progression Interactive** visualisant le pourcentage de devoirs accomplis.
- **Validation Interactive** avec micro-animations lors du cochage d'une tâche.

### 🎪 5. Événements Universitaires (Events)
- Calendrier des conférences, séminaires, clubs étudiants et activités sportives.
- Vignettes de dates avec badges stylisés et localisation du lieu.
- Recherche instantanée par mots-clés.

---

## 🛠️ Architecture & Technologies

L'application respecte les recommandations officielles d'Android en matière d'architecture moderne (**Modern Android Development - MAD**) :

| Composant | Technologie | Description |
| :--- | :--- | :--- |
| **Langage** | [Kotlin](https://kotlinlang.org/) | 100% Kotlin avec Coroutines & Flow |
| **Interface (UI)** | [Jetpack Compose](https://developer.android.com/jetpack/compose) | UI déclarative moderne avec [Material Design 3](https://m3.material.io/) |
| **Architecture** | MVVM + Repository Pattern | Séparation stricte de la logique métier et de la vue |
| **Persistance Locale** | [Room Database](https://developer.android.com/training/data-storage/room) | Base de données locale SQLite robuste et réactive |
| **Préférences** | [Jetpack DataStore](https://developer.android.com/topic/libraries/architecture/datastore) | Stockage asynchrone sécurisé des paramètres utilisateur |
| **IA / Chatbot** | [OpenRouter API](https://openrouter.ai/) | Client HTTP asynchrone & streaming de complétion IA |
| **Navigation** | Navigation Compose | Gestion typée des routes et de la barre de navigation inférieure |
| **Asynchronicité** | Kotlin Coroutines & StateFlow | Gestion réactive des flux d'états |

---

## 📂 Structure du Projet

```text
com.campusconnect/
├── CampusConnectApp.kt              # Point d'entrée de navigation (Scaffold, NavHost, BottomBar)
├── BottomNavigationBar.kt          # Barre de navigation animée avec badges
├── MainActivity.kt                 # Activité principale Android
│
├── data/
│   ├── local/                      # Base de données Room (AppDatabase, DAOs, Entités)
│   ├── model/                      # Modèles de données (Task, Event, TimetableEntry, ChatMessage)
│   └── repository/                 # Repositories (DataStoreManager, CampusBotRepository)
│
├── ui/
│   ├── components/                 # Composants réutilisables (TaskItem, StatCard, EventItem, FloatingCampusBot...)
│   ├── screens/                    # Écrans principaux (HomeScreen, TasksScreen, EventsScreen, CalendarScreen, CampusBotScreen)
│   └── theme/                      # Système de design (Color, Theme, Type)
│
└── viewmodel/                      # ViewModels (Tasks, Events, Timetable, CampusBot, Main)
```

---

## 🚀 Installation & Lancement

### Prérequis
- **Android Studio** : Ladybug (2024.2.1) ou version ultérieure.
- **JDK** : Version 17 ou 21 (JDK embarqué d'Android Studio recommandé).
- **SDK Android** : Android 7.0 (API 24) minimum, cible Android 15 (API 35).

### 1. Cloner le Dépôt
```bash
git clone https://github.com/AnisAlger/CampusConnect.git
cd CampusConnect
```

### 2. Configuration de la Clé API OpenRouter (Optionnel mais Recommandé)
Pour activer les fonctionnalités d'intelligence artificielle dans l'application sans exposer vos secrets sur Git :

Ajoutez votre clé dans le fichier `local.properties` à la racine du projet (ce fichier est déjà ignoré par Git) :
```properties
openrouter.api.key=sk-or-v1-VOTRE_CLE_ICI
```

> 💡 **Alternative dans l'application** : Vous pouvez également saisir ou modifier votre clé d'API directement depuis l'écran **CampusBot** en cliquant sur l'icône **⚙️ Paramètres** en haut à droite.

### 3. Compilation et Exécution
1. Ouvrez le projet dans **Android Studio**.
2. Attendez la synchronisation Gradle (`Sync Project with Gradle Files`).
3. Sélectionnez un émulateur ou un appareil physique connecté.
4. Cliquez sur **Run 'app'** (`Shift + F10`) ou lancez la commande Gradle :
```bash
./gradlew assembleDebug
```

---

## 🎨 Design & Ergonomie

- **Palette Harmonisée** : Indigo profond (`#3949AB`), Sarcelle moderne (`#00897B`), Ambre doré (`#FFA000`).
- **Support Sombre / Clair** : Basculement instantané avec gestion de la barre d'état et surface adaptative.
- **Accessibilité** : Contrastes soignés, tailles de police conformes aux normes Material Design et animations fluides.

---

## 📄 Licence

Ce projet est sous licence MIT — consultez le fichier [LICENSE](LICENSE) pour plus de détails.
