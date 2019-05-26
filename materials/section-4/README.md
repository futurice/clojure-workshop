# Database (Extra Credit)

In this extra credit section we will add a database and integrate it with our app. Storing state in a atom is perfectly fine if you don't want to have persistent data, otherwise you probably need a datastore or database. We will be using _postgres_ as our database. We won't be talking about specific databases in this section, only how you integrate with them (in this case postgres).

### Prerequisite

For this section, you will need docker installed on your machine. Follow [this](https://runnable.com/docker/getting-started/) guide to get started with docker. We won't be discussing docker in any great detail in this section, as this section only explains how to integrate with a database.

Once you have docker installed create a `Dockerfile` inside the `section-2` folder. The `Dockerfile` should look like this:

```
FROM postgres:10
ENV POSTGRES_DB clojure_workshop_db
```

Next, we will build and run our database
run `docker build -t clojure_workshop_db .` to build the docker image of our database. Next, run `docker run -p 5432:5432 clojure_workshop_db`.
Now you should have a database up and running!

### Setup

Before we begin refactoring our app, we need to setup our database tables. In this case we only need one. Add the following table to your database:

```
CREATE TABLE todo(
  id serial PRIMARY KEY NOT NULL,
  name varchar(50) NOT NULL,
  done BOOL DEFAULT FALSE
);
```
There are a number of ways you can add the database. The easiest way is probably to download `psql` [download](https://www.postgresql.org/download/) and run the following command to connect to the database `psql -h localhost -p 5432 -u clojure_workshop_db`. After you've connected to the database, you can copy & paste the create table command above and run it.nd presto! You have created your database table

Now that we've created a database and set up a table for our todos, we are finally ready to begin writing Clojure database code!

### HugSQL & Postgres

We will be using *postgres* and *hugsql* for our database integration. We will add two dependencies `[com.layerware/hugsql "0.4.9"]` and `[org.postgresql/postgresql "42.2.2"]`. The first one is *hugsql*. *Hugsql* is a library for writing SQL in Clojure.

>SQL is the right tool for the job when working with a relational database!
HugSQL uses simple conventions in your SQL files to define (at compile time) database functions in your Clojure namespace, creating a clean separation of Clojure and SQL code.
HugSQL supports runtime replacement of SQL Value Parameters (e.g., where id = :id), SQL Identifiers (i.e. table/column names), and SQL Keywords. You can also implement your own parameter types.
HugSQL features Clojure Expressions and Snippets providing the full expressiveness of Clojure and the composability of partial SQL statements when constructing complex SQL queries.
HugSQL has protocol-based adapters supporting multiple database libraries and ships with adapters for clojure.java.jdbc (default) and clojure.jdbc

- HugSQL

The second library we add is the jdbc driver for postgres. HugSQL uses jdbc and requires a jdbc driver for a database to work. Jdbc (Java database connectivity) is a Java API that let's you communicate with databases.

### Using HugSQL

To get started using Hugsql, you will actually need to write a sql file. Hugsql transforms basic sql queries into Clojure functions. It does this with the `def-db-fns` macro. `def-db-fns` will take a sql file location as a parameter.

```clojure
(def-db-fns "<location-to-sql-file>")
```
The sql file could look something like this:

```sql
-- :name fetch-todos :? :*
SELECT * FROM todo;
```

The comment above the sql query might not look familiar. This is Hugsql specific. Let's dissect the comment to see what is actually happening. This comment is called the command sequence.

1. `:name fetch-todos` => The Clojure function name given to this query. Note! The functions will only be available inside the current namespace where the `def-db-fns` function is called in. The named function should *always be passed the database connection url* as the first argument.
Usage:
```clojure
  (ns myproject.namespace
    (:require [hugsql.core :refer [def-db-fns]]))

  (def db-url "postgresql://postgres@localhost:5432/clojure_workshop_db")

  ;; Replace <project-name> with actual name of your project
  (def-db-fns "<project-name>/queries.sql")

  (defn todos []
    (fetch-todos db-url))
```

2. `:?` => indicates that we are about to run query with an expected result set. This is the default behaviour of Hugsql.

3. `:*` => tells Hugsql that we are expecting many rows to be returned by this query and they should be returned as a vector. If no rows are returned, the vector will be empty.

Hugsql is a huge library, and to explain it in greater detail would be a disservice to it's great documentation. Checkout the [documentation](https://www.hugsql.org/#detail) for more information on how to use Hugsql.


### Tasks

There will only be one task in this section: Remove the todo atom and store all the todos in the database instead.
