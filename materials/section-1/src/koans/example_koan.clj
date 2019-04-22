

;;;;  Clojure Koans!
;;
;; Remember that defn is a shorthand for a function!
;; To get started write true, or a function that returns true, in the first koan
(meditations
  "I am ready to write some Clojure!"
  (= true true))

;;;; Arthimetic
;;
;; + - adding
;; - - subtracting
;; * - multiplying
;; / - dividing

(meditations
  "Adding two numbers is the start of learning a new programming language, and also quite easy"
  (= 17 (+ __ __))

  "I wasn't going to tell a joke about subtraction but I didn't want you to think less of me"
  (= 5 (- __ __ __))

  "Multiplying can be ridiculously easy"
  (= 25 (* __))

  "Ratios are a precise alternative for doubles"
  (= 1/4 (/ __ __)))


;;;; Equality & Conditionals
;;
;; > < = >= <= == not= not if if-not
;; Something to remember about Clojures's conventionts is the question mark
;; A appended ? means the function is a predicate function and predicate functions always return booleans

(meditations
  "What is greater than x but lesser than y?"
  (= true (> 15 __ 5))

  "Lesser than or equal to, but something is missing"
  (= true (<= 2 2 __ 25))

  "Not cannot be easier"
  (= true (not (= "Yes!" "Yes!"))) ;; This can also be written as (not= x y)

  "Same same but still different"
  (= true (== 1.0 1/1 __))

  "No ! needed, only functions that resemble plain English"
  (= :truth (if-not (zero? __)
                :truth
                :false))

  "What is empty?"
  (= :futu (if (empty? __) ;; You can try multiple values
            :futu
            :rice))

  "There's a nice story behind not-empty, where is the ? btw??"
  (= __ (not-empty [1 2 3]))

  "When is if without else"
  (= __ (when (nil? [])
          :futu))

  "That which is not positive can only be ..."
  (= :columbia-road (when-not (pos? __)
                      :columbia-road)))
