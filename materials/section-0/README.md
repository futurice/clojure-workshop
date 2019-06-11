

# Introduction

What is Clojure?

>Clojure was forged in a mythic volcano by Rich Hickey. Using an alloy of Lisp, functional programming, and a lock of his own epic hair, he crafted a language that’s delightful yet powerful. Its Lisp heritage gives you the power to write code more expressively than is possible in most non-Lisp languages, and its distinct take on functional programming will sharpen your thinking as a programmer.

\- CLOJURE for the BRAVE and TRUE

That is a nice and short summary of Clojure. Although, some key things are missing from the description. As the description points out, Clojure is a Lisp, it's also dynamically typed, and it runs on the JVM. In the next section, we'll go through the fundamentals of the language.

#### Why Clojure?

Good question! As mentioned above, Clojure is very feature rich. Clojure was built from the ground up to solve concurrency issues and it has a strong connection to Java. Clojure also includes a very powerful REPL which makes testing code quite easy.
A unique feature of Clojure is it's macro system, with which you can extend the capabilities of the language. If you are writing a piece of software that performs a lot of concurrent operations and needs to easily manipulate data structures and it should also be able to run on any platform, then Clojure might be the right choice for you.

If you're still unsure about Clojure maybe the creator, Rich Hickey, can convince you:
https://www.youtube.com/watch?v=34_L7t7fD_U&t=745s

## Table of contents

1. [Syntax](#syntax)
2. [Types](#types)
3. [Special Forms](#special-forms)
4. [Examples](#examples)
5. [Reader](#reader)
6. [Macros](#macros)
7. [References](#references)

## Syntax

The syntax, or structure of the language is quite different from what you might be used to. Clojure is written as lists, or *forms*. The form is written as a pair of parenthesis, `()` which is also how you represent a list in Clojure. When you write a basic expression in Clojure, the first thing you will write is a pair of parenthesis, the first thing you write inside the parenthesis will *always be interpreted as a function to call*. The first position inside the parenthesis is called the function position. Anything after that, is interpreted as an argument(s) for the function you are calling. Any Clojure type that implements the `IFn` interface can be placed in the function position. This includes: `keyword`, `vector`, `map`, `set` and of course *functions* and *macros* can also be placed in the function position, and last but not least *special forms* can also be placed in the function position.

![Structure](structure.png)

image from clojure.org

Above is an example on how to language is structured and evaluated. One key aspect of the language is revealed here, Clojure only has expression, and *all expressions return some value*. Don't worry if this is confusing, we will now go through the basics of the language in detail.

## Types
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

One thing to note is that any ratio number that can be represented as a whole number *it will always be represented as a whole number (long)*.

```clojure
2/1 ;; => 2
(class 2/1) ;; => java.lang.Long
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
All of these types are sequential, meaning that they can all be cast into sequences. List is the only type that is naturally a sequence as it implements the Java `ISeq` interface. Sequences allows you to use all of the Clojure STL sequence functions (map, reduce, filter, etc) on these types. Something you might have noticed is that quotation mark in front of the `list`. You might also remember that the first thing inside parenthesis is always interpreted as a function to call, while still true the case for `list` is unique. The quotation mark is what is known as a *special form*. It yields the evaluation of the expression and returns a `list` of unevaluated values instead of interpreting it as a function and arguments.

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

Next we will briefly look at special forms. These are forms that are evaluated differently (*special*) than normal expressions. Special forms are not macros or functions, although they behave very similarly. Special forms are directly built in to the Clojure compiler. They are a fundamental building block of Clojure.
Clojure has a dozen special forms, but generally you will only be using the following:

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

;; Vars don't need parenthesis. They always point to their respective value
some-empty-var ;; => "Unbound: #'user/some-empty-var"
some-var ;; => "Hello"
metadata-var ;; => "World"
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

As we learned earlier, everything after the function position is interpreted as arguments for the function.

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

As mentioned above, these are the most common *special forms*. There are of course a lot more. There is a link to all special forms in the reference section of this page. Up next is examples.

## Examples

Next we will be looking at very common functions, and we'll also be breaking them down to understand thoroughly how they work.

### Plus

```clojure
(+ 1 2) ;; => 3
(+ 1) ;; => 1
(+) ;; => 0
(+ 2 2 2 2 2) ;; => 10
```

The plus `+` function takes 0 to infinite amount of numbers as arguments and sums them.

All arithmetic functions (- * / +) work the same way. They all take between 0 or 1 to infinite, and performs their respective arithmetic operation on the given arguments.

### Keyword

```clojure
(:name {:name "Sam"}) ;; => "Sam"
(:name {:id 5 :balance 500}) ;; => nil
(:name {:id 10 :balance 300} "Simon") ;; => "Simon"
(:name #{:name :age :balance}) ;; => :name
```

Keywords work as functions also. Keywords as functions work by finding themselves from a given collection. If the collection does not contain the given keyword, the function will return nil. Keywords can be given an extra *default* value argument. In case the keyword could not be found from the given collection the *default* value would be returned instead.

### Equality & Comparison & Predicate

```clojure
(= 1 1) ;; => true
(= "1" 1) ;; => false
(= [1 2] [1 2]) ;; => true
(= '("Milo" "Tonny" "Franke") '("Peter" "Glenn" "Joe")) ;; => false
(= nil false) ;; => false
(not (= 1 2)) ;; => true
(not= 1 2) ;; => true
(= 1 1 1 1 1) ;; => true
(= 2 2 2 2 1) ;; => false
(= {:user {:name "Peter"}} {:user {:name "Peter"}}) ;; => true
```

The equals function takes 1 to an infinite amount of arguments and compares them all. If all arguments are *exactly* the same, the function will return true, otherwise false. The equals function will always return a boolean. The not function will return the exact opposite of what equals will return.

```clojure
(> 1 2) ;; => false
(< 1 2) ;; => true
(>= 10 10 9) ;; => true
(<= 5 6 7) ;; => true
```

The comparison functions all work in the same way. Each of them take 1 to an infinite amount of arguments. Comparison functions always checks numbers in pairs. If a comparison function is given e.g the arguments `1 2 3` the first comparison would be between `1 2` and the second one between `2 3`. The will continue until there are no more arguments to compare. If all comparisons returned true then the function itself will return true, otherwise false.

 ```clojure
(even? 2) ;; => true
(odd? 2) ;; => false
(number? "1")  ;; => false
(vector? '()) ;; => false
(seq? {}) ;; => false
(empty? []) ;; => true
(some? nil) ;; => false
(nil? nil) ;; => true
```

Clojure has a handful of predicate functions. Predicate functions are functions that will check if the given argument matches the predicate functions comparison clause. If the argument is a match, the functions will return true, otherwise false. One thing to keep in mind is that predicate functions always return a boolean as a result. Predicate functions can easily be identifiable by the question mark notation. Question marks at the end of functions should, in general, be considered predicate functions.


### Sequence & Collection functions

```clojure
(map inc [1 2 3]) ;; => '(2 3 4)
(filter odd? '(2 2 2 5 9 1 2 2) ;; => '(5 9 1)
(reduce + [10 10 10 10 10]) ;; => 50
(map :name [{:name "Sam" :age 22} {:name "Simon" :age 55}]) ;; => '("Sam" "Simon")
(map + [12 34 5] '(2 3 4) #{1 2 11}) ;; => '(15 39 20)
(map first {:name "Homer J, Simpson" :age 39 :title "Junior Vice President"}) ;; => '(:name :age :title)
(assoc {} :name "Peggy Hill") ;; => {:name "Peggy Hill"}
(dissoc {:name "Bill Dauterive" :occupation nil} :occupation) ;; => {:name "Bill Dauterive"}
(count "Hank Hill") ;; => 9
```

Sequence functions are functions that take a *sequential type* (*map vector list string set*) and a function as arguments, and perform some sort of operation on the sequence and *return a new sequence*. E.g. `map` function will always return a sequence (list), even if you pass it a vector.

Collection functions are functions that take any type that implements the `IPersistentCollection` interface as an argument and performs some sort of operation on the argument. `count` is unique in this case since it can also be given a string even though a string is not a collection.

That's it for most common functions. More examples can be found from the [Clojure Cheatsheet](https://clojure.org/api/cheatsheet).

## Reader

The reader is a unique feature of Clojure. You could call it an extra build step. This step is taken before the source code gets compiled.
Clojure has two steps of compilation in a sense, first the reader will turn your Clojure code into Clojure data structures, and then the compiler will turn the data structures into Java byte code. You can actually test the reader with Clojure's `read-string` function.

```clojure
(read-string "(+ 1 2 3)") ;; => (+ 1 2 3)
```

Why is this useful? Well, in Clojure the compilation and reading (parsing) of the code are completely separate. This is an essential part in Clojure. The reader allows us to change how the code behaves, or even what the code looks like. This is done with the help of *macros*. In the above example we see a straightforward relationship between the reader and the data structure it produces. However, as mentioned, we can manipulate the data structure with the help of *reader macros*.

```clojure
(read-string "'(a b c)") ;; => (quote (a b c))
```
Above we see an example of a *reader macro*. `'` is the *quote* macro. The *quote* macro yields unevaluated Clojure expression.
Clojure offers a handful of *reader macros*. To get a full understanding on how the reader works and how to reader macros work, you can read everything about the reader here: https://clojure.org/reference/reader

## Macros

Macros are one of the features that make Clojure amazing! Macros are quite special. Unlike normal functions, marcos are placed between the reader and compiler in the Clojure lifecycle, and unlike functions, macros are created with the `defmacro` special form. What this means, is that with macros we can manipulate unevaluated (yielded) data structures. We can essentially create new code and change existing code.

Practical example:

```clojure
(defmacro macro-fn [form]
  (drop-last form))

(macro-fn (+ 2 nil)) ;; => 2

(defn normal-fn [form]
  (drop-last form))

(normal-fn (+ 2 nil)) ;; => java.lang.NullPointerException
```

Why does this happen? Clojure syntax is evaluated from the inside out, except in macros and special forms. Macros get passed unevaluated expressions, that's why we can manipulate the unevaluated expression however we wish. In the case of a function, the inner most expression will always get evaluated first, and the result of that expression will get passed to the function body. In the example above the inner most expression resulted in a `NullPointerException`.

And that's it! We covered the bare basics of the language! Hopefully you understand how Clojure works and how to write basic expressions in Clojure. Hopefully you also have a grasp on the different *types* in Clojure, and how the language is structured. Next up we'll get to tackle some real coding challenges in the *koans* section.

## References

1. [Clojure for the Brave and TRUE](https://www.braveclojure.com/getting-started/)
2. [Clojure Special Forms](https://clojure.org/reference/special_forms)
3. [Clojure Syntax](https://clojure.org/guides/learn/syntax)
4. [Clojure Types](https://aphyr.com/posts/302-clojure-from-the-ground-up-basic-types)
