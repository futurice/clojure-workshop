
# Deployment (Heroku)

In this section we will finally get to deploy our application. We will be using Heroku as our infrastructure in this section.
Don't worry if Heroku is not familiar to you, we will setup an account and a basic application together. We will only be going through how to deploy the backend part of our app.
Normally you would have the backend serve and index.html file, but the templates used in the workshop don't allow this by default. The lein `reagent` template creates a fullstack application that also serves all the static assets. This template would be the preferred way to host both a backend api and a frontend static application.

## Heroku setup

Before we begin, we need to create an account on Heroku. Thankfully Heroku is a free service and Heroku allows users to create applications for free. Create an account here. Once you've successfully created your account, you can go ahead and install the `heroku cli` (https://devcenter.heroku.com/articles/heroku-cli#download-and-install).
After installing the `heroku cli` tool, you can now create a Heroku project within your application. Do this by going the your backend app directory, and run `heroku apps:create --region eu`. This will create a Heroku app inside Europe. After the app has been created, a new remote origin called `heroku` should now be available to git.
In section 4 we set up a database for our application. If you didn't setup a database, you can skip this next part. Heroku provides a free postgres database for your applications. To add postgres to our Heroku app, simply run `heroku addons:create heroku-postgresql:hobby-dev --region eu --app <name of your heroku app>`. After our database has been added, be sure to run the *create table* migration we created in section-4 in our newly created database. This can be done with running `heroku pg:psql --app <name of your heroku app>`, this will open a remote shell connection to your database and you can now run postgres queries. *Note* the `heroku pg:psql` command requires that you have the `psql` cli tool installed. Once our database is setup we can create a Heroku configuration file, Procfile.The Profile should be created in the same directory as your `project.clj` file. The Procfile should only contain the following `web: java $JVM_OPTS -jar target/<name of your clojure-app>-0.1.0-SNAPSHOT-standalone.jar`.
 Now our Heroku app is fully setup and configured. Before we deploy our app, we need to make slight modifications to our backend app.

## App configuration

As mentioned before if you didn't setup a database you can skip this section.
In our `handler.clj` file we define a database url var,
```clojure
(def db-url "postgresql://postgres@localhost:5432/clojure_workshop_db")
```
We can now change this to use the Heroku postgres instance instead,
```clojure
(def db-url (or (System/getenv "DATABASE_URL") "postgresql://postgres@localhost:5432/clojure_workshop_db"))
```

## Deploying

Now you have everything ready to go. Only thing left is deploying. Depending on how your app is structured you mmight need to deploy your application in different ways. If you have your `project.clj` file in the root, then run `git push heroku master`. Alternatively, if your `project.clj` file is in a subdirectory, like in this workshop, then you need to run `git subtree push --prefix <location/of/app>`. After the deployment is finished you can check it out with `heroku open`. Your app should now open inside your default browser. Congratulations! Your app is now running in the cloud :)
