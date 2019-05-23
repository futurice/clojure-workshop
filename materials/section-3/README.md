# Section 3: Writing a frontend web application with ClojureScript and Reagent

### Contents

1. Introduction to ClojureScript
2. Introduction to Reagent
3. Create a new Reagent project
4. Create the views
5. Introduction to Ajax on ClojureScript
6. Create the backend calls

## 1. Introduction to ClojureScript

### What is ClojureScript?

Taken directly from the ClojureScript [website](https://clojurescript.org):

> ClojureScript is a compiler for Clojure that targets JavaScript. It emits JavaScript code which is compatible with the advanced compilation mode of the Google Closure optimizing compiler.

The Google Closure compiler allows for your ClojureScript code to be compiled into highly optimised JavaScript code. It (in it's own words) "compiles from JavaScript to better JavaScript".


### So, why not just use JavaScript?

I think we can all agree here that JavaScript has _some_ shortcomings. Since JavaScript is quite a feature-rich language, there are many corners in which to hide bugs in your code. It requires extensive discipline from the JavaScript developers to ensure that the software written is bug-free. The amount of effort required to write robust code in JavaScript is vastly higher than to achieve the same feat in ClojureScript.

### What are the differences between Clojure and ClojureScript?

There are a few differences here (but actually surpsingly little on the whole), and if you're really interested I'd recommend you go to the page on the [ClojureScript website](https://clojurescript.org/about/differences) that talks about this in a little bit more detail. But, to summarise the main bits:

- Data Structures
  - `nil` in ClojureScript corresponds to `null` and `undefined` in JavaScript, as opposed to just `null` in Java.
  - Characters do not exist in Java, so a ClojureScript `\a` corresponds to the single letter string `"a"` in JavaScript
- Refs do not exist in ClojureScript (but we can just use Atoms instead, which work exactly the same as they do in Clojure)
- ClojureScript only supports integer and floating point literals that can be mapped to JavaScript primitives.
  - Therefore, Ratio, BigDecimal & BigInteger do not exist in ClojureScript.
  - Equality on numbers works as it does in JavaScript, and not in Clojure.
    - `(= 1.0 1) => true` in ClojureScript, but not in Clojure!


### Interop between ClojureScript <-> JavaScript

ClojureScript defines a namespace called `js` that allows us to access JavaScript objects, functions and primitives defined in the global scope.

```clojure
(def text js/globalName)
```

If we want to invoke JavaScript functions, we can do so by prefixing the function name with a `.`. We can pass function arguments at the end of each form.

```clojure
(.getElementById js/document "my-element")
```
n
If we want to access properties of a JavaScript object, we can do so with the `.-` property access syntax.

```clojure
(.-bar js/foo) ;; JS output: foo.bar;
```

You can access nested properties using the `..` access syntax.

```clojure
(.. js/foo -bar -baz) ;; JS output: foo.bar.baz;
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
