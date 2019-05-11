# Backend Programming

In this section we will delve into backend programming with Ring and Compojure. Compojure is a lightweight routing library built on top of Ring which is an http library for Clojure backend development.

Breakdown:

- Ring -> Http server
- Compojure -> Routing lib

To scaffold a Compojure project, run `lein new compojure <name>` (replace name with a the name of your project). This command will create a Compojure project with the following folder structure:

```
<name>/
├── README.md
├── project.clj
├── resources
│   └── public
├── src
│   └── <name>
│       └── handler.clj
└── test
    └── <name>
        └── handler_test.clj
```

The most interesting files are without a doubt, `project.clj` and `src/<name>/handler.clj`. The `project.clj` file is the file that describes the project to Leiningen. The project dependencies, name, description etc. are describes inside the `project.clj` file. Incase of a Compojure project, it also defines the entry point for the web-server: `:ring {:handler <name>.handler/app}`. This tells Ring where to look for the entrypoint so it can bootstrap the server. The second interesting file, is the `handler.clj` file. This is where the application is bootstrapped. It will look something like this:

```clojure
(ns <name>.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]))

(defroutes app-routes
  (GET "/" [] "Hello World")
  (route/not-found "Not Found"))

(def app
  (wrap-defaults app-routes site-defaults))
```

Small breakdown:

```clojure
(ns <name>.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]))
```

Namespace is defined and the core of Compojure is included along with the route namespace. It also requires the middleware namespace from Ring. Middlewares are just an easy way to handle requests before they reach your route handler. Ring has a few default ones which Compojure uses when it bootstraps the project.

```clojure
(defroutes app-routes
  (GET "/" [] "Hello World")
  (route/not-found "Not Found"))
```

`defroutes` is a macro provided by Compojure. It takes two parameters. A name and route handlers. A route handler is nothing more than a basic function. Compojure provides a handful of route handlers. `GET`, `POST`, `PATCH`, `PUT`, `HEAD`, `DELETE`, `OPTIONS` and `ANY`.
All of these route handlers work in the same way. Each of them take a path as the first argument, a vector with bound route parameters, and a route body as the last argument.

```clojure
(GET "/home" [person] (str "Welcome Home " person " !"))
  ^     ^       ^                      ^
method route  params                  body
```

The last part:

```clojure
(def app
  (wrap-defaults app-routes site-defaults))
```

This is the bootstrapped application. Ring will look for this _var_ when starting the app. The app _var_ can be wrapped in middleware layers. Compojure wraps our routes with some default middlewares provided by Ring.

**These middlewares should be deleted!**

```clojure
(def app app-routes)
```

This is how it should look like now that you've deleted the middlewares.

Since we are building a basic REST API, we will need to be able to provide json responses. This is done by adding the
`ring.middleware.json` dependency to the project.

Adding new dependencies to your Clojure project is quite simple. Simply open your `project.clj` file and add the dependency to the list of dependencies. In this case we want to add `[ring/ring-json "0.4.0"]`. `ring.middleware.json` exports one function that we want to use, `wrap-json-response`. This function will transform a Clojure map into a json object and it will also add `Content-Type: application/json; charset=utf-8` header to the response.

Now we can wrap our app with the json middleware:

```clojure
(def app (wrap-json-response app-routes))
```

An other middleware that needs to be added before we can begin is `ring.middleware.params`. This middleware exposes a middleware function, `wrap-params`, that parses url-encoded parameters from the query string and request body, ring does not do this by default. This middleware is already included in Ring, so it _does not have to be added to the_ `project.clj` _dependencies list!_

```clojure
(def app (-> app-routes
             wrap-params
             wrap-json-response))
```

Now everything is set up and we can begin building our REST API.
In the next section you will finally get to do some coding yourself, i.e. the tasks are next! Good luck!

---

## Task

#### TODO REST API

The task for this section is to create REST API for a TODO application.
_The TODOs should be stored inside an_ `atom`. The REST API should also provide four endpoints.

1. `GET` Should list all available TODOs.
2. `POST` Should add a single TODO.
3. `DELETE` Should delete a TODO, and finally one endpoint.
4. `PATCH` Should update a single TODO - mark as _done_.

A TODO could look something like this:

```clojure
(def todo
  "A basic TODO"
  {:id 1
   :name "Wash the dishes"
   :done false})
```

Extra credit:

1. Make the `GET` endpoint accept a query parameter, _sortby_, which sorts the TODOs either in ascending or descending order, or maybe even in some other way..?

2. Make the delete endpoint return `400 Bad Request` if the specified TODO doesn't exist in the TODO list.

3. Make a new endpoint that empties the TODO list.

4. Make an new endpoint which only deletes TODOs, which id's are powers of 2.
