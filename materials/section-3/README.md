# Section 3: Writing a frontend web application with ClojureScript and Reagent

### Contents

1. Introduction to ClojureScript
2. Introduction to Reagent
3. Create a new Reagent project
4. Create the views
5. Introduction to Ajax on ClojureScript
6. Create the backend calls

## 1. Introduction to ClojureScript

TBD:
- differences between clj and cljs
- js interop

---

## 2. Introduction to Reagent

TBD
- -> https://reagent-project.github.io/
- until managing state

---

## 3. Create a new Reagent project

Create the projec from template

	$ cd materials/section-3
	$ lein new reagent-frontend todoapp

Start it

	$ lein figwheel

Now you have an independent ClojureScript & Reagent frontend running on a Figwheel development server and a *ClojureScript* REPL.

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

Write a regaent component for rendering a single todo item. When you iterate over the vector, remember the `^{:key ...}` in the for comprehension.

Write the rest of the components: adding a new todo item, toggling the done state, and removing a todo.

After you have tested that the components work, add the functions that take care of the `on-change` and `on-click` interaction.

---

## 5. Introduction to Ajax on ClojureScript

There's two libraries that provide HTTP calls on ClojureScript:
- [cljs-ajax](https://github.com/JulianBirch/cljs-ajax) which offers a callback style interface
- [cljs-http](https://github.com/r0man/cljs-http) which utilizes [core.async](https://github.com/clojure/core.async) channels and macros

We wanted to keep things simple and not go through core.async, so we used cljs-ajax. Let's see how it works: https://github.com/JulianBirch/cljs-ajax

---

## 6. Create the backend calls
