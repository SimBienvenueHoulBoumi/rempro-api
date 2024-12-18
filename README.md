# Getting Started

```bash
  # Run springboot projet
  mvn spring-boot:run

  # Install dependenses
  mvn clean install

  # swagger
  ```
  http://localhost:8095/swagger-ui/index.html
  ```

# Models

## Users
- `id` : Identifiant unique de l'utilisateur.
- `username` : Nom d'utilisateur choisi par l'utilisateur.
- `password` : Mot de passe sécurisé, généralement encodé.

{
    "username": "user44@mail.com",
    "password": "user123"
}

## Followed
- `id` : Identifiant unique pour chaque élément suivi (film, série, anime, etc.).
- `name` : Nom du contenu suivi (titre du film, de la série ou de l'anime).
- `type` : Type de contenu suivi (référence à `type_name`).
- `level_type` : Niveau du contenu suivi (référence à `level_name`)
- `level_number` : Indice du niveau suivi

## FollowedTypeAndLevel
- `type_name` : Nom du type de contenu (ex. : `movie`, `anime`, `series`).
- `level_name` : Nom du type de contenu (ex. : `episodes`, `chapter`).

```