# APPONE - BOOKSWAP

![BOOKSWAP LOGO](https://www.spanishwithsarah.com/wp-content/uploads/2016/11/book_swap.jpg)

## About Me

At the time of building this, I am a Junior Scala Developer. About
half a year ago I started a new job where the company programmes in Scala
with a functional tech stack. 

The idea of this app is simple and follow CRUD format:

1. POST a book
2. GET a book
3. PUT (update) a book
4. DELETE a book

The core purpose of app one is for me to understand how to deploy a
very simple app. 

For this app I deployed using Heroku, I chose to use this platform as I thought it would be 
an easy place to start. In the future, I will try to deploy to AWS in dockerised containers.

## Learning outcomes

- [x] Build a simple backend Scala app
- [x] connect a database
- [x] write a few tests
- [x] deploy to Herko
- [x] Add CI/CD

# Tech Stack
- Scala 
- Cats
- Cats Effect
- Doobie
- Docker-compose
- Circe
- Heroku

# What I learnt

I wasn't too familar on how to properly use 
environment variables and application.conf files. 

My original plan was to have an application.conf file and have a meta directory. The meta directory would 
have two files: `dev.env` and `prd.env`. I would have something which ran `source meta/prd.env` when in production (heroku). 

Unfortunately this cannot be achieved as I could not store variables statically in that location/file as Heroku updates its variables every so often, 
meaning the variables within `prd.env` would become out of date.

Addtionally my original plan would be against the https://www.12factor.net/ coding principles.

```scala
 def prodConfig(): Config = {
    val dbUri = new URI(System.getenv("DATABASE_URL"))
    val username = dbUri.getUserInfo.split(":")(0)
    val password = dbUri.getUserInfo.split(":")(1)
    val dbUrl = "jdbc:postgresql://" + dbUri.getHost + dbUri.getPath

    Config(ServerConfig(5432, dbUri.getHost), DbConfig(dbUrl, username, password, 10))
  }
```
Instead of using LoadConfig I used the above function to capture and chop the `DATABASE_URL` which is injected by Heroku, I then pass that
to the various parts of config that need it.
