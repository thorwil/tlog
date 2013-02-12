(ns tlog.interface.validate
  "Functions that return their argument unchanged, if it matches one or several predicates, or that
   convert their argument if possible. If a predicate evaluates to false or if a conversion is not
   possible, they return nil. To be used for URL matching."
  (:require [clojure.string :refer [split]]))


;; Template for validators

(defn valid
  "Take a predicate. Return a function that returns its single argument, if the predicate on the
   argument is true, otherwise nil."
  [pred]
  #(when (pred %) %))


;; Validators

(defn ->int
  "Validate/convert argument to integer"
  [s]
  (try (Integer. s)
       (catch Exception e nil)))

(def >zero
  "Validate integer being > 0."
  (valid #(> % 0)))

(defn int-gt-zero
  [s]
  (some-> s ->int >zero))

(defn ->pair
  "Validate string consisting of 2 parts with '-' in the middle."
  [s]
  (when (re-matches #"[^-]+-[^-]+" s)
    (split s #"-")))

(defn int-gt-zero-pair
  "Validate string consisting of 2 parts with '-' in the middle, the 2 parts converting to integers
   > 0."
  [s]
  (some-> s
          ->pair
          ((fn [[a b]] (when-let [a (int-gt-zero a)]
                         [a b])))
          ((fn [[a b]] (when-let [b (int-gt-zero b)]
                         [a b])))))
