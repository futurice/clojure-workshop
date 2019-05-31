# Section 3: Writing a frontend web application with ClojureScript and Reagent

### Contents

1. [Introduction to ClojureScript](#1-introduction-to-clojurescript)
2. [Introduction to Reagent](#2-introduction-to-reagent)
3. [Create a new Reagent project](#3-create-a-new-reagent-project)
4. [Create the views](#4-create-the-views)
5. [Introduction to Ajax on ClojureScript](#5-introduction-to-ajax-on-clojurescript)
6. [Create the backend calls](#6-create-the-backend-calls)

## 1. Introduction to ClojureScript

### What is ClojureScript?

Taken directly from the ClojureScript [website](https://clojurescript.org):

> ClojureScript is a compiler for Clojure that targets JavaScript. It emits JavaScript code which is compatible with the advanced compilation mode of the Google Closure optimizing compiler.

The Google Closure compiler allows for your ClojureScript code to be compiled into highly optimised JavaScript code. It (in it's own words) "compiles from JavaScript to better JavaScript". It also removes dead code.


### So, why not just use JavaScript?

Unsurprisingly, the vast majority of the benefits of using Clojure also apply to using ClojureScript. So, you get all the parts of using a functional lisp dialect with immutable-by-default data structures. There's also some great tooling specific to ClojureScript that provides additional help to the developer experience. One such tool is [Figwheel](https://github.com/bhauman/lein-figwheel), which handles your build-related tasks such as hot code reloading, dependency management, loval dev environment management, and even provides you with a REPL that hooks directly into your running application (which is _awesome_).

One additional benefit that isn't to be underestimated is using a single language across the full stack. One particularly nice part of this (which sadly we won't be utilising within this workshop) is the concept of [Reader Conditionals](https://clojure.org/reference/reader#_reader_conditionals). This allows you to define `cljc` files in your project that work with both Clojure _and_ ClojureScript. For example, here's a basic project structure of a full-stack Clojure(Script) application:

```
src
├── clj
│   └── <name>
│       ├── core.clj
│       ├── handler.clj
│       ├── profiles.clj
│       └── server.clj
├── cljs
│   └── <name>
│       ├── core.cljs
│       ├── db.cljs
│       ├── events.cljs
│       ├── routes.cljs
│       ├── subs.cljs
│       └── views.cljs
└── cljc
    └── <name>
        ├── utils.cljc
        └── other.cljc
```

Here, everything defined in `utils.cljc` and `other.cljc` can be refereced in our `clj` and `cljs` files. This makes it possible to create libraries that target both Clojure and ClojureScript!

### What are the differences between Clojure and ClojureScript?

There are a few differences here (but actually surpsingly little on the whole), and if you're really interested I'd recommend you go to the page on the [ClojureScript website](https://clojurescript.org/about/differences) that talks about this in a little bit more detail. But, to summarise the main bits:

- Data Structures
  - `nil` in ClojureScript corresponds to `null` and `undefined` in JavaScript, as opposed to just `null` in Java.
  - Characters do not exist in JavaScript, so a ClojureScript `\a` corresponds to the single letter string `"a"` in JavaScript
- Refs do not exist in ClojureScript (but we can just use Atoms instead, which work exactly the same as they do in Clojure)
- ClojureScript only supports integer and floating point literals that can be mapped to JavaScript primitives.
  - Therefore, Ratio, BigDecimal & BigInteger do not exist in ClojureScript.
  - Equality on numbers works as it does in JavaScript, and not in Clojure.
    - `(= 1.0 1) => true` in ClojureScript, but not in Clojure!

For a quick reference of functions available in ClojureScript code, check the [ClojureScript cheat sheet](https://cljs.info/cheatsheet/).

### Interop between ClojureScript and JavaScript

When working in ClojureScript, we are often working with ClojureScript's own data structures alongside native JavaScript data structures. So naturally, we are often going to be performing operations that allow us to convert JavaScript objects and primitives to ClojureScript, and vice-versa. The following functions and macros allow us to do this easily. To help us with this, ClojureScript defines a namespace called `js` that allows us to access JavaScript objects, functions and primitives defined in the global scope.

```clojure
(def text js/globalName)
```

If we want to invoke JavaScript functions, we can do so by prefixing the function name with a `.`. We can pass function arguments at the end of each form.

```clojure
(.getElementById js/document "my-element")
```

ClojureScript data structures (set, vector, list, keyword, symbol and map) need to be converted in order to use them in JavaScript. The same applies also vice versa.

You can also access properties of a JavaScript object directly from ClojureScript with the `.-` property access syntax.

```clojure
(.-bar js/foo) ;; JS output: foo.bar;
```

You can access nested properties using the `..` access syntax.

```clojure
(fn [e]
	(.. e -target -value))
 ;; JS output: function(e) { return e.target.value; }
```

We can create JavaScript objects in ClojureScript using the `js-obj` macro:

```clojure
(js-obj "foo" "bar") ;; creates { foo: "bar"}
```

This only allows you to use basic data literals as values, though. Any ClojureScript data structures (such as vectors, hash sets etc) won't be changed. To recursively change values into JavaScript, we can use the `clj->js` function:

```clojure
(clj->js :a [1 2 3] :b #{"wow" "what" "fun"})
```

This will correspond to

```javascript
{
  "a": [1, 2, 3],
  "b": ["wow", "what", "fun"]
}
```

If we want to do the same, but in reverse (recursively transform JavaScript into ClojureScript maps), we can do this in such a way using the `js->clj` function:

```clojure
(def js-array (js-obj "foo" "bar" "awesome" true))
(js->clj js-array :keywordize-keys true) ;; => {:foo "bar", :awesome true}
```
As you can see above, we can add a flag to automatically keywordise the JavaScript object keys, if we would like.

If you'd like to read a little more in depth, I'd recommend turning to [this blog post](https://www.spacjer.com/blog/2014/09/12/clojurescript-javascript-interop/) which provides a great rundown of additional ways you can interact with JS from ClojureScript, and vice-versa.

---

## 2. Introduction to Reagent

Reagent is a minimalistic interface between ClojureScript and React, and we will be using this today as the base of our frontend. It plays into ClojureScripts strengths by using just ClojureScript data and functions to define React components (rather than JSX sticking out like a sore thumb in the middle of some JavaScript code).

The best place to learn the basics of Reagent is by going to the [official documentation](https://reagent-project.github.io/) and having a read through. You only need to read up until the end of the "Managing state in Reagent" section for the scope of this workshop, but you're more than welcome to continue reading if you have the time.

Once you've read through and understood the basics of Reagent, let's continue by making our frontend project.

---

## 3. Create a new Reagent project

Create the project using the [reagent-frontend](https://github.com/reagent-project/reagent-frontend-template) template

	$ cd materials/section-3
	$ lein new reagent-frontend todoapp

Start it

	$ lein figwheel

Now you have an independent ClojureScript & Reagent frontend running on a Figwheel development server and a *ClojureScript* REPL.

The REPL is connected to your browser and you can directly require namespaces, call functions and even change atom values in the REPL.

### Application structure

```
.
├── LICENSE
├── README.md
├── env
│   ├── dev
│   │   ├── clj
│   │   │   └── user.clj
│   │   └── cljs
│   │       └── todoapp
│   │           └── dev.cljs
│   └── prod
│       └── cljs
│           └── todoapp
│               └── prod.cljs
├── project.clj
├── public
│   ├── css
│   │   └── site.css
│   ├── index.html
│   └── js
└── src
    └── todoapp
        └── core.cljs
```

- `env` contains bootstrap code for development and production
- `project.clj` dependencies
- `public` static resources, ClojureScript code will be compiled in `public/js`
- `src` the ClojureScript sources

### Full stack Clojure project

Often, you would start by creating a full stack Clojure/ClojureScript/Reagent project with `lein new reagent foobar` command.

The full stack project has one `project.clj` that defines dependencies and build for both backend and frontend and it starts only one http development server that serves both backend and static frontend files.

The [full stack Reagent template](https://github.com/reagent-project/reagent-template) contains also flags to enable e.g. unit tests and less/sass compilation.

---

## 4. Create the views

Create the application global state in atoms, e.g. a vector of maps containing todo data:

```
(def todos
  (r/atom
   [{:id 1 :name "Write frontend" :done false}
    {:id 2 :name "Test it" :done false}]))
```

Write a Reagent component for rendering a single todo item. When you iterate over the vector, remember the `^{:key ...}` in the for comprehension.

Write the rest of the components: adding a new todo item, toggling the done state, and removing a todo.

After you have tested that the components work, add the functions that take care of the `on-change` and `on-click` interaction.

---

## 5. Introduction to Ajax on ClojureScript

There's two libraries that provide HTTP calls on ClojureScript:
- [cljs-ajax](https://github.com/JulianBirch/cljs-ajax) which offers a callback style interface
- [cljs-http](https://github.com/r0man/cljs-http) which utilizes [core.async](https://github.com/clojure/core.async) channels and macros

We wanted to keep things simple and not go through core.async, so we used `cljs-ajax`. Let's see how it works: https://github.com/JulianBirch/cljs-ajax

The `cljs-ajax` library takes care of `json` parsing. To handle the response formatting, use the following options:

```clojure
{:response-format :json
 :keywords? true}
```

To handle the POST parameter formatting, give the parameters as a Clojure map and use the following option:

```clojure
{:format :json}
```

### CORS headers

Before creating the frontend, add the CORS header middleware in the backend project:

1. In `project.clj`, add the following dependency: `[ring-cors "0.1.13"]`
2. In `handler.clj`, require: `[ring.middleware.cors :refer [wrap-cors]]`
3. In `handler.clj`, use the following middleware (inside `def app`):

```clojure
(wrap-cors :access-control-allow-origin [#"http://localhost:3449"]
           :access-control-allow-methods [:get :patch :post :delete])
```

---

## 6. Create the backend calls

Use `cljs-ajax` to create the backend calls. The way to refactor your implementation would be:
1. Instead of mutating the `todos` atom locally, make an Ajax call. Update the `todos` state atom in the handler function.
2. When the page is loaded, fetch the current state. This could be called in the `core/mount-root` function.
