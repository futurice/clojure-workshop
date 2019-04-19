

# Introduction

What is Clojure?

>Clojure was forged in a mythic volcano by Rich Hickey. Using an alloy of Lisp, functional programming, and a lock of his own epic hair, he crafted a language that’s delightful yet powerful. Its Lisp heritage gives you the power to write code more expressively than is possible in most non-Lisp languages, and its distinct take on functional programming will sharpen your thinking as a programmer.

\- CLOJURE for the BRAVE and TRUE

That is a nice and short summary of Clojure. Although, some key things are missing from the description. As the description points out, Clojure is a Lisp, it's also dynamically typed, and it runs on the JVM. In the next section, we'll go through the fundamentals of the language.

## Table of contents

1. [Syntax](#syntax)
2. [Data Types](#data-types)
3. [Special Forms](#special-forms)
4. [References](#references)

## Syntax

The syntax, or structure of the language is quite different from what you might be used too. Clojure is written as lists, or *forms*. The form is written as a pair of parenthesis, `()` which is also how you represent a list in Clojure. When you right a basic expression in Clojure, the first thing you will write is a pair of parenthesis, the first thing you write inside the parenthesis will *always be interpreted as a function to call*. The first position inside the parenthesis is called the function position. Anything after that, is interpreted as an argument(s) for the function you are calling. Any Clojure type that implements the `IFn` interface can be placed in the function position. This includes: `keyword`, `vector`, `map`, `set` and of course *functions* themselves, and last but not least, *macros* can also be placed in the function position.

![Structure](structure.png)

image from clojure.org

Above is an example on how to language is structured and evaluated. One key aspect of the language is revealed here, Clojure only has expression, and *all expressions return some value*. Don't worry if this is confusing, we will now go through the basics of the language in detail.


## Data Types
The first fundamental aspect of Clojure, is that all the underlying *types*
are actually plain old Java classes. All of the following types, except for *atoms*, are intrinsically immutable.

#### Numberic

```clojure
(class 1) ;; => java.lang.Long
(class 1.0) ;; => java.lang.Double
(class 1N) ;; => clojure.lang.BigInt
(class 5/2) ;; => clojure.lang.Ratio
(class (short 1)) ;; => java.lang.Short
(class (int 1)) ;; java.lang.Integer
(class (byte 1)) ;; java.lang.Byte
(class (float 1.2)) ;; java.lang.Float
```

Clojure represents all whole numbers as `long`. If you want to use something else, you have to explicitly construct it or cast it. As you can see, numbers are normal Java objects. Clojure does however add two *new* number types, `clojure.lang.Ratio` and `clojure.lang.BigInt`. The more interesting of the two, is the ratio type.
`clojure.lang.Ratio` is any number that can't be represented as an accurate number.
```clojure
(/ 5 2) ;; => 5/2
(/ 4 2) ;; => 2
```

Interestingly enough, any arithmetic operation that includes a ratio type will always return either a `clojure.lang.Ratio` or `clojure.lang.BigInt`

```clojure
(* 1 1/2) ;; => 1/2
(* 2 1/2) ;; => 1N
```

#### Strings

 ```clojure
(class "Skadam!") ;; => java.lang.String
(class \v) ;; => java.lang.Character
 ```

Just like numbers, string and character are both also plain Java objects.

#### Booleans

```clojure
(class true) ;; => java.lang.Boolean
(class false) ;; => java.lang.Boolean
(class nil) ;; => nil
```

As with numbers and strings, booleans are also plain old Java objects.
`nil` however is not a Java object. `nil` is kinda special in Clojure, but let's just pretend that it's the same as Java `null`. If you are interested in learning more about `nil` this article explains it in great detail: https://lispcast.com/nil-punning/

#### Collections

```clojure
(class '(1 2 3)) ;; => clojure.lang.PersistentList
(class [1 2 3]) ;; => clojure.lang.PersistentVector
(class #{1 2 3}) ;; => clojure.lang.PersistentHashSet
(class {:1 2 :3 4}) ;; => clojure.lang.PersistentArrayMap
```

Clojure has four fundamental *collection* types, `list`, `vector`, `set` and `map`. Again, these are all plain old Java objects, just like everything else so far. Difference between `list` and `vector` is that `list` is a Java `LinkedList` while `vector` is a Java `ArrayList`. `Set` is a list of unique values. And `map` is a basic *key value pair* data structure.
All of these types implement the `ISeq` interface. This enables you to use all of the Clojure STL sequence functions (map, reduce, filter, etc) on these types. Something you might have noticed is that quotation mark in front of the `list`. You might also remember that the first thing inside parenthesis is always interpreted as a function to call, while still true the case for `list` is unique. The quotation mark is what is known as a *special form*. It yields the evaluation of the expression and returns a `list` of unevaluated values instead of interpreting it as a function and arguments.

#### Keywords

```clojure
(class (keyword "4")) ;; => clojure.lang.keyword
(class :1) ;; => clojure.lang.keyword
```

Keywords are a special type in Clojure. They are used as identifiers, but they also work as functions. Keywords are always prepended with a colon. Keywords are commonly used as keys in collections. Keywords can also be used as functions and  will evaluate to their own value inside a `map` or `set` when used as a function: `(:a {:a 4}) ;; => 4`

#### Atoms

```clojure
(class (atom 5)) ;; => clojure.lang.Atom
```

Atoms are how Clojure represents mutable data. Atoms are a special kind of reference type. Atoms can be created using the `atom` function. To read an atoms state, you will need to use the `deref` function.
 ```clojure
(deref (atom 5)) ;; => 5
@(atom 5) ;; => 5
```
The `@` is just a macro for deref, will get into macros later on. Clojure provides a handful of functions to change an atoms state, most commonly used are: `swap!` and `reset!`.

```clojure
(def number-atom (atom 5))
(println @number-atom) ;; => 5
(swap! number-atom inc)
(println @number-atom) ;; => 6

(def text-atom (atom "Hello"))
(println @text-atom) ;; => "Hello"
(reset! text-atom "World")
(println @text-atom) ;; => "World"
```

Don't worry about `def` just yet, we'll get into that one later on. As you can see in the examples above, `reset!` works a little bit differently than `swap!`. `swap!` will take an atom as the first argument and a function that will apply a change on the atom. `swap!` also takes an optional third argument, this argument would be an argument that would be passed to the applying function: `(swap! a-list conj :a-value) ;; => '(:a-value)` *note* `swap!` returns nil, the arrow, in this example, indicates the new mutated version. `reset!` only takes two arguments, an atom and a new value. An interesting thing you might have noticed is the use of the exclamation mark. This is a convention in lisp languages. The exclamation mark indicates that their is a side-effect made by the function. When you see the exclamation mark, most likely, an atom has been changed.


## Special Forms

Next we will briefly look at special forms. These are forms that are evaluated differently (*special*) than normal types.

#### def

The `def` form creates a global *var* with a name and a value inside the current namespace. `def` only requires one argument, a name for the current var, the rest are optional. `def` can also be given a metadata map. This metadata map can contain a handful of useful meta information, such as:

1. `:private`
a boolean indicating the access control for the var. If this key is not present, the default access is public (e.g. as if :private false).

2. `:doc`
a string containing short (1-3 line) documentation for the var contents

3. `:test`
a fn of no args that uses assert to check various operations. The var itself will be accessible during evaluation of a literal fn in the metadata map.

4. `:tag`
a symbol naming a class or a Class object that indicates the Java type of the object in the var, or its return value if the object is a fn.

```clojure
(ns user) ; Current namespace

(def some-empty-var) ;; => #'user/some-empty-var
(def some-var "Hello") ;; => #'user/some-var
(def
  ^{:doc "Description of var"
    :private true}
  metadata-var
  "World") ;; => #'user/metadata-var

(some-empty-var) ;; => "Unbound: #'user/some-empty-var"
(some-var) ;; => "Hello"
(metadata-var) ;; => "World"
```

#### fn

The `fn` form lets you define functions. Functions don't require a name, as they can be anonymous. `fn` form takes three arguments, an optional name, optional vector of arguments for the function body, and the function body itself.

```clojure
(fn some-fn [] (println "Hello")) ;; => nothing
(fn [] (println "World")) ;; => nothing
 ```

 In the examples above, nothing gets printed out, the functions above never get called. To call a function, you need to add a pair of parenthesis.

```clojure
((fn some-fn [] (println "Hello"))) ;; => "Hello"
((fn [] (println "World"))) ;; => "World"
 ```

Functions can also be stored inside a `def`

```clojure
(def some-fn (fn [] (println "Functions!"))) ;; => #'user/some-fn
(some-fn) ;; => "Functions!"
```

There is also a shorthand for this.

```clojure
(defn some-fn [value] (println value)) ;; => #'user/some-fn
(some-fn "Functions!") ;; => "Functions!"
```

#### let

The `let` form lets you define local bindings between symbols and values. The bindings are always immutable and can *never be changed*. The bindings will only exist inside the body of the `let`.

```clojure
(let [x 1
      y 2
      z y]
  (println x y z)) ;; => 1 2 2
```

A `let` can easily be used in conjunction with `defn`

```clojure
(defn fn-with-locals [some-arg]
  (let [x 1
        y (+ x 1)
        z (+ y some-arg)]
    (println x y z))) ;; => #'user/fn-with-locals

(fn-with-locals 5) ;; => 1 2 7
```

And that's it! We covered the bare basics of the language! Hopefully you understand how Clojure works and how to write basic expressions in Clojure. Hopefully you also have a grasp on the different *types* in Clojure, and how the language is structured.


## References

1. [Clojure for the Brave and TRUE](https://www.braveclojure.com/getting-started/)
2. [Clojure Special Forms](https://clojure.org/reference/special_forms)
3. [Clojure Syntax](https://clojure.org/guides/learn/syntax)
4. [Clojure Types](https://aphyr.com/posts/302-clojure-from-the-ground-up-basic-types)
