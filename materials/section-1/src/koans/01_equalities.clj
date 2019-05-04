(ns koans.01-equalities
  (:require [koan-engine.core :refer [mediations]]))

"
Equalities:

The equality function in Clojure works similarly to x.equals(y) in Java. That is, it tries to compare numbers and collections in a type-independent manner.

One thing to note - Clojure data structures are compared by value, and not by identity.

If you run into any issues, remember, the REPL is your friend! Copy forms and evaluate them in the REPL for some help. If you want to find out what a function does, you can call (doc <function>) from within the REPL to retrive the docstring for the given function.

clojuredocs.org is also a great resource to use if you want to see some examples of functions being used

I've commented out some koans that I feel aren't critical and will allow us to stay on our timeframe, but you are more than welcome to tackle them if you have some time to spare.
"

(meditations
 ;; "We shall contemplate truth by testing reality, via equality"
 ;; (= __ true)

 "To understand reality, we must compare our expectations against reality"
 (= __ (+ 1 1))

 ;; "You can test equality of many things"
 ;; (= (+ 3 4) 7 (+ 2 __))

 "Some things may appear different, but be the same"
 (= __ (= 2 2/1))

 "You cannot generally float to heavens of integers"
 (= __ (= 2 2.0))

 "But a looser equality is also possible"
 (= __ (== 2.0 2))

 "Something is not equal to nothing"
 (= __ (not (= 1 nil)))

 "Strings, and keywords, and symbols: oh my!"
 (= __ (= "hello" :hello 'hello))

 "Make a keyword with your keyboard"
 (= :hello (keyword __))

 "Symbolism is all around us"
 (= 'hello (symbol __))

 ;; "What could be equivalent to nothing?"
 ;; (= __ nil)

 ;; "When things cannot be equal, they must be different"
 ;; (not= :fill-in-the-blank __)
 )
