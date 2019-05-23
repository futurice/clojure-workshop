# Backend Programming

In this section we will delve into backend programming with Ring and Compojure. Compojure is a lightweight routing library built on top of Ring which is an http library for Clojure backend development.

Breakdown:

- Ring -> Http server ([docs](https://github.com/ring-clojure/ring/wiki))
- Compojure -> Routing lib ([docs](https://github.com/weavejester/compojure/wiki))

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

The most interesting files are without a doubt, `project.clj` and `src/<name>/handler.clj`. The `project.clj` file is the file that describes the project to Leiningen. The project dependencies, name, description etc. are described inside the `project.clj` file. In the case of a Compojure project, it also defines the entry point for the web-server: `:ring {:handler <name>.handler/app}`. This tells Ring where to look for the entrypoint so it can bootstrap the server. The second interesting file, is the `handler.clj` file. This is where the application is bootstrapped. It will look something like this:

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

Our namespace is defined, and we include the Compojure core, the route namespace along with the middleware namespace from Ring. It also requires the middleware namespace from Ring. Middlewares are just an easy way to handle requests before they reach your route handler. Ring has a few default ones which are used in the Compojure template.

```clojure
(defroutes app-routes
  (GET "/" [] "Hello World")
  (route/not-found "Not Found"))
```

`defroutes` is a macro provided by Compojure. It takes two parameters. A name and route handlers. A route handler is nothing more than a basic function. Compojure provides a handful of route handlers. `GET`, `POST`, `PATCH`, `PUT`, `HEAD`, `DELETE`, `OPTIONS` and `ANY`.
All of these route handlers work in the same way. Each of them take a path as the first argument, a request body, and a route body as the last argument.

```clojure
(GET "/home" request (str "Welcome Home " ((:params request) "person")  "!"))
  ^     ^       ^                      ^
method route  request                body
```

The request body is quite unique. Compojure will compile the request differently depending on what you have given it as the second argument. This way of defining routes can be a bit unclear, as *all request params are always strings and not keywords*, and fetching the params key from the request object can be very tiring and lead to verbose code. The most common practice is to pass it a vector. Compojure will automatically destruct the request object and pass the request parameters present in the vector into your route body.

```clojure
(GET "/home" [person] (str "Welcome Home " person "!"))
  ^     ^       ^                      ^
method route  params                body
```

This is the more common approach when writing routes.

The last part:

```clojure
(def app
  (wrap-defaults app-routes site-defaults))
```

This is the bootstrapped application. Ring will look for this _var_ when starting the app. The app _var_ can be wrapped in middleware layers. Compojure wraps our routes with some default middlewares provided by Ring.

The default middlewares, such as `site-default` don't support json very well. We will be using json specific middlewares in our project, _so you can go ahead and delete the default middlewares._

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

We can now test our setup.

```clojure
(GET "/profile" [person] {:person person})
```

While testing this, you might have noticed that nothing gets returned from our API.
Why is that? Ring expects that all routes, even the ones created by Compojure, should return a Ring response. An example Ring response could look like this:

```clojure
{:status 200
 :headers {"Content-Type" "text/plain"}
 :body "Hello World!"}
```

So, how do we fix this? We need to wrap our response in a map, like this:
```clojure
(GET "/profile" [person] {:status 200
                          :body {:person person}})
```

But this isn't really an elegant solution. Luckly, Ring provides a helper function that creates this response map for us. Inside the `ring.util.response` namespace, there's a function conveniently called `response`. Once you've included the `ring.util.response` namespace, you can refactor your handler to look something like this:

```clojure
(GET "/profile" [person] (response {:person person}))
```

Now everything is set up and we can begin building the rest  our REST API.
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