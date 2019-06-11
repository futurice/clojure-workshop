# Programming & REPL

In this section we'll finally get our hands dirty with some coding.
We'll be going through the core functions and at the same time we'll do some challenges, koans. The koans are located in the `src` folder.

This section is a good part to familiarize yourself with the Clojure REPL (Read - Evaluate - Print - Loop).
To start the REPL, run the following command in the terminal: `lein repl`.
Nightcode has an inbuilt REPL, just press the *run with REPL* button and you are all set.

A good way to try and solve the following koans would be to first try to find a viable solution with the help of the REPL and then in the source file.

Some of the koans can be somewhat challenging. https://clojuredocs.org/ and the [Clojure Cheat Sheet](https://clojure.org/api/cheatsheet) are good sources to find the most relevant and helpful functions to solve the koans.

If you're wondering how Clojure's STL functions work, the `doc` and `source` functions are a good way to peek at them

```clojure
(doc +)
-------------------------
clojure.core/+
([] [x] [x y] [x y & more])
  Returns the sum of nums. (+) returns 0. Does not auto-promote
  longs, will throw on overflow. See also: +'

(source +)
-------------------------
(defn +
  "Returns the sum of nums. (+) returns 0. Does not auto-promote
  longs, will throw on overflow. See also: +'"
  {:inline (nary-inline 'add 'unchecked_add)
   :inline-arities >1?
   :added "1.2"}
  ([] 0)
  ([x] (cast Number x))
  ([x y] (. clojure.lang.Numbers (add x y)))
  ([x y & more]
     (reduce1 + (+ x y) more)))
```

### Nightcode

Nightcode is a lightweight Clojure editor designed primarily for teaching. Nightcode is optional for this workshop. But it already comes equipped with [parinfer](https://github.com/shaunlebron/parinfer) which is a handy plugin that makes writing Clojure a cinch.

Example of parinfer:

![Example](https://camo.githubusercontent.com/142ec87e9c4ab1863c3dc4c26807f6bca4ef3f5b/687474703a2f2f7a697070792e6766796361742e636f6d2f57656972644f6464426c756566696e74756e612e676966)

Parinfer will automatically add parenthesis and brackets for you. All you have to do is worry about indentation.

### Running the Koans

To run the koans, simply run:

`lein koan run` with leiningen

`script/run` on Mac/\*nix (Optional)

`script\run` on Windows (Optional)

As you save your files with the correct answers, section-1 will automatically advance you to the next koan or file.

When you execute `run` you'll see something like this:

    Problem in  /home/paduan/code/section-1/src/koans/equalities.clj
    ---------------------
    Assertion failed!
    We shall contemplate truth by testing reality, via equality.
    (= __ true)

The output is telling you that you have a failing test in `src/koans/equalities.clj`. Open that file up and make it pass!  In general, you just fill in the blanks to make tests pass.  Sometimes there are several (or even an infinite number) of correct answers: any of them will work in these cases.

The koans differ from normal TDD in that the tests are already written for you, so you'll have to pay close attention to the failure messages, because up until the very end, making a test pass just means that the next failure message comes up.

You might notice that some of the koans are commented out - this means that you're welcome to skip these in order to keep to our time schedule. But, if you have some time to spare before we start the next section, I'd highly recommend going back and trying to tackle some.
