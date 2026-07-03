# ETL Open Food Facts - Spring Boot

## 1. Présentation du projet

Ce projet est un TP d'optimisation Backend Java visant à construire un pipeline ETL (Extract, Transform, Load) avec Spring Boot.

**Objectif :** Importer les données du fichier CSV Open Food Facts, les nettoyer, les stocker en base de données H2, et les exposer via une API REST.

**Source des données :** Open Food Facts (https://world.openfoodfacts.org/) - fichier CSV contenant des produits alimentaires avec leurs informations nutritionnelles, ingrédients, allergènes, additifs, marques et catégories.

## 2. Stack technique

- **Java 21**
- **Spring Boot 3.4.1**
- **Spring Batch** - pour le pipeline ETL
- **Spring Data JPA** - pour la persistance
- **Spring Cache** - pour l'optimisation des résolutions d'entités
- **H2** - base de données en mémoire (développement)
- **Lombok** - réduction de code boilerplate
- **Maven** - gestion des dépendances et build

## 3. Architecture du projet

### Pipeline ETL

Le pipeline suit un flux séquentiel en 5 étapes :

```
Lecture CSV (FlatFileItemReader)
    ↓
Parsing (CsvLineParser)
    ↓
Nettoyage (CleaningService)
    ↓
Résolution/Déduplication (EntityResolverService + Cache)
    ↓
Persistance (ProductWriter -> ProductRepository)
```

**Description des étapes :**

1. **Lecture CSV** : Spring Batch lit le fichier ligne par ligne, sauf l'en-tête
2. **Parsing** : Chaque ligne est découpée en 31 colonnes selon le format Open Food Facts
3. **Nettoyage** : Suppression des caractères parasites, pourcentages, contenu entre parenthèses, normalisation des espaces
4. **Résolution** : Pour chaque valeur unique (marque, catégorie, ingrédient, allergène, additif), vérification en base + cache. Création si inexistante
5. **Persistance** : Sauvegarde du produit avec ses relations ManyToMany

## 4. Arborescence des packages

```
com.etloff/
├── entity/          # Entités JPA (Product, Brand, Category, Ingredient, Allergen, Additive)
├── repository/      # Interfaces Spring Data JPA pour l'accès aux données
├── service/         # Logique métier (CleaningService, EntityResolverService)
├── batch/           # Configuration Spring Batch (reader, processor, writer, job)
├── parser/          # Parsing du CSV Open Food Facts
├── controller/      # REST controllers (ProductController, ReferenceEntityController)
├── dto/             # Data Transfer Objects (ProductDTO, ReferenceEntityDTO)
├── config/          # Configuration Spring (BatchConfig, application.properties)
└── util/            # Classes utilitaires (si nécessaire)
```

## 5. Règles de gestion

### Unicité des entités référentielles

Les entités `Brand`, `Category`, `Ingredient`, `Allergen` et `Additive` possèdent une contrainte d'unicité sur le champ `name` en base de données. Le `EntityResolverService` garantit qu'aucun doublon n'est créé via un mécanisme find-or-create avec cache.

### Nettoyage des ingrédients, allergènes et additifs

Le `CleaningService` applique les règles suivantes :

1. **Caractères parasites** : suppression des caractères non-alphanumériques hors espaces et tirets
2. **Pourcentages** : suppression des patterns `%` et des valeurs numériques associées
3. **Parenthèses** : suppression du contenu entre parenthèses `(...)`
4. **Séparateurs multiples** : gestion des virgules, points-virgules, tirets comme séparateurs de listes
5. **Normalisation** : suppression des espaces multiples, trim

Exemple : `"Sucre (France), sel marin 2%, arôme naturel"` → `"Sucre, sel, arôme naturel"`

### Filtre des noms trop longs

Les noms dépassant 150 caractères sont ignorés (considérés comme non-exploitables, ex : descriptions longues dans la colonne ingrédients).

## 6. Comment lancer le projet

### Prérequis

- Java 21 (JDK)
- Maven 3.8+

### Commandes

**Lancer les tests :**
```bash
mvn test
```

**Lancer l'application :**
```bash
mvn spring-boot:run
```

**Accéder à la console H2 :**
```
http://localhost:8080/h2-console
```
URL JDBC : `jdbc:h2:mem:etloffdb`

### Placement du fichier CSV

Placer le fichier CSV Open Food Facts dans le dossier `data/` à la racine du projet :
```
data/open-food-facts.csv
```

Le chemin est configuré dans `src/main/resources/application.properties` :
```properties
etl.csv.path=data/open-food-facts.csv
```

### Déclencher le job ETL

Le job Spring Batch est désactivé au démarrage (`spring.batch.job.enabled=false`). Pour le lancer :

**Option 1 - Via test d'intégration :**
```bash
mvn test -Dtest=EtlJobIntegrationTest
```

**Option 2 - Via commande Maven avec profil :**
```bash
mvn spring-boot:run -Dspring-boot.run.arguments=--spring.batch.job.enabled=true
```

## 7. Documentation des endpoints REST

### Endpoints Produits

#### GET /products/top-by-brand?brand=X&limit=N

Retourne les N premiers produits pour une marque donnée.

**Exemple de requête :**
```bash
curl "http://localhost:8080/products/top-by-brand?brand=Ferrero&limit=5"
```

**Exemple de réponse :**
```json
[
  {
    "id": 1234,
    "name": "Nutella",
    "nutritionScore": "E",
    "energyPer100g": 539.0,
    "fatPer100g": 30.9,
    "brandName": "Ferrero",
    "categoryName": "Pâtes à tartiner"
  }
]
```

#### GET /products/top-by-category?category=X&limit=N

Retourne les N premiers produits pour une catégorie donnée.

**Exemple de requête :**
```bash
curl "http://localhost:8080/products/top-by-category?category=Chocolat&limit=3"
```

**Exemple de réponse :**
```json
[
  {
    "id": 5678,
    "name": "Chocolat Noir 70%",
    "nutritionScore": "B",
    "energyPer100g": 580.0,
    "fatPer100g": 42.0,
    "brandName": "Côte d'Or",
    "categoryName": "Chocolat"
  }
]
```

#### GET /products/top-by-brand-category?brand=X&category=Y&limit=N

Retourne les N premiers produits pour une marque et une catégorie données.

**Exemple de requête :**
```bash
curl "http://localhost:8080/products/top-by-brand-category?brand=Ferrero&category=Pâtes à tartiner&limit=2"
```

**Exemple de réponse :**
```json
[
  {
    "id": 1234,
    "name": "Nutella",
    "nutritionScore": "E",
    "energyPer100g": 539.0,
    "fatPer100g": 30.9,
    "brandName": "Ferrero",
    "categoryName": "Pâtes à tartiner"
  }
]
```

### Endpoints Entités référentielles

#### GET /ingredients/top?limit=N

Retourne les N ingrédients les plus courants (par nombre de produits associés).

**Exemple de requête :**
```bash
curl "http://localhost:8080/ingredients/top?limit=5"
```

**Exemple de réponse :**
```json
[
  {
    "id": 1,
    "name": "Sucre",
    "productCount": 5432
  },
  {
    "id": 2,
    "name": "Sel",
    "productCount": 4891
  }
]
```

#### GET /allergens/top?limit=N

Retourne les N allergènes les plus courants.

**Exemple de requête :**
```bash
curl "http://localhost:8080/allergens/top?limit=5"
```

**Exemple de réponse :**
```json
[
  {
    "id": 1,
    "name": "Lait",
    "productCount": 2341
  },
  {
    "id": 2,
    "name": "Gluten",
    "productCount": 1876
  }
]
```

#### GET /additives/top?limit=N

Retourne les N additifs les plus courants.

**Exemple de requête :**
```bash
curl "http://localhost:8080/additives/top?limit=5"
```

**Exemple de réponse :**
```json
[
  {
    "id": 1,
    "name": "E322",
    "productCount": 1234
  },
  {
    "id": 2,
    "name": "E330",
    "productCount": 987
  }
]
```

### Gestion des erreurs

Tous les endpoints retournent HTTP 400 si le paramètre `limit` est négatif ou si un paramètre requis est manquant.

```json
{
  "error": "Parameter 'limit' must be positive"
}
```

## 8. Optimisations de performance

### Démarche

L'optimisation a suivi une approche itérative avec mesure à chaque étape :

1. **Baseline** : Architecture séquentielle sans optimisation
2. **Cache Spring** : Ajout d'un cache sur `EntityResolverService` pour éviter les requêtes DB répétées
3. **Tentative Virtual Threads** : Test de parallélisation du step ETL avec Virtual Threads - **abandonné** car le goulot est l'I/O base de données, pas le CPU. Le double scan CSV nécessaire pour pré-résoudre les entités référentielles annulait tous les gains (71,8s vs 7,6s)
4. **Batch inserts Hibernate** : Activation du batching avec `batch_size=50` et changement de stratégie d'ID de `IDENTITY` vers `SEQUENCE` pour `Product`

### Résultats chiffrés

| Configuration | Temps total | Débit | Heap delta | Threads |
|---------------|-------------|-------|------------|---------|
| Baseline | 23,0 s | 584 prod/s | 403 MB | 10 |
| Cache seul | 7,6 s | 1774 prod/s | 290 MB | 10 |
| 2-phase + VT (abandonné) | 71,8 s | 187 prod/s | 232 MB | 17 |
| **Final (cache + batch)** | **8,75 s** | **1534 prod/s** | 232 MB | 10 |

**Configuration finale retenue :**
- Traitement séquentiel avec chunk size de 100
- Cache Spring `ConcurrentMapCacheManager` sur les résolutions d'entités
- Batch inserts Hibernate avec `batch_size=50`
- Séquence Hibernate pour `Product` avec `allocationSize=50`

**Pourquoi cette architecture ?** Le cache apporte le gain principal (-67% de temps). Le batch insert ajoute +15% de débit supplémentaire. La parallélisation n'est pas rentable car le goulot est l'I/O base de données.

## 9. Tests

**Nombre total de tests : 49**

### Répartition par type

**Tests unitaires (37 tests) :**
- `CsvLineParserTest` : 7 tests (parsing du CSV)
- `CleaningServiceTest` : 13 tests (règles de nettoyage)
- `EntityResolverServiceTest` : 17 tests (résolution d'entités, cache, filtre longueur)

**Tests d'intégration (12 tests) :**
- `EtlJobIntegrationTest` : 1 test (job ETL complet sur fichier réel)
- `EtlPerformanceTest` : 1 test (benchmark performance)
- `LongNamesInvestigationTest` : 1 test (investigation noms longs - test de debug conservé pour référence)
- `ProductApiTest` : 9 tests (6 endpoints + 3 cas d'erreur)

**Couverture fonctionnelle :**
- Pipeline ETL complet
- Nettoyage des données
- Résolution et déduplication des entités
- API REST (6 endpoints)
- Gestion des erreurs

## 10. Conception

Le dossier `/conception` contient :

- `Diagramme_de_classe.png` - Diagramme de classes UML du projet
- `Modele_physique_de_donnees.png` - Modèle physique de données (MPD) avec les tables et relations

## 11. Configuration

### application.properties

```properties
# Base de données H2 en mémoire
spring.datasource.url=jdbc:h2:mem:etloffdb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

# JPA / Hibernate
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false

# Batch inserts optimization
spring.jpa.properties.hibernate.jdbc.batch_size=50
spring.jpa.properties.hibernate.order_inserts=true
spring.jpa.properties.hibernate.order_updates=true
spring.jpa.properties.hibernate.jdbc.batch_versioned_data=true

# Console H2
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console

# ETL CSV path
etl.csv.path=data/open-food-facts.csv

# Spring Batch
spring.batch.job.enabled=false
spring.batch.jdbc.initialize-schema=always
```

## 12. Points techniques notables

### Gestion des ManyToMany

Les relations ManyToMany entre `Product` et `Ingredient`/`Allergen`/`Additive` sont gérées via des tables de jointure :
- `product_ingredients`
- `product_allergens`
- `product_additives`

### Stratégie d'ID

- `Product` : `SEQUENCE` avec `allocationSize=50` pour permettre le batch insert
- `Brand`, `Category`, `Ingredient`, `Allergen`, `Additive` : `IDENTITY` (création unique, pas de batching nécessaire)

### Cache Spring

Le cache est configuré sur les méthodes de résolution d'entités (`@Cacheable`). En mode développement avec H2 en mémoire, le cache est perdu à chaque redémarrage de l'application.