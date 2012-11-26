(ns tlog.data.t-account
  (:require [midje.sweet :refer [fact tabular]]
            [tlog.data.account]))

;; Use @#'tlog.data.account/accounts-raw to get at the value of the private var.
(fact "accounts-raw database query result includes 'admin' key"
  (some #(= "admin" %) (map :username @#'tlog.data.account/accounts-raw)) => true)


(defn some-equal
  "True if 'i' is in sequence 'items', else false."
  [i items]
  (some #(= % i) items))

(defn remove-elements
  "Remove items in the seq 'remove-these' from items in seq 'items'."
  [remove-these items]
  (reduce #(if (some-equal %2 remove-these) %1 (cons %2 %1)) '() items))

(fact "accounts-raw database query result contains only :username and :password keys"
  (remove-elements [:username :password] (mapcat keys @#'tlog.data.account/accounts-raw)) => empty?)


(def accounts-1-raw
  [{:password "$2a$10$Cp0GK58KbTE3SYx8wmgqLO2oF2JHLp/a72D6UGevvKCwfzdPHqBHu"
    :username "admin"}])

(def accounts-1-converted
  {"admin" {:password "$2a$10$Cp0GK58KbTE3SYx8wmgqLO2oF2JHLp/a72D6UGevvKCwfzdPHqBHu"
            :username "admin"
            :roles #{:tlog.data.account/admin}}})

(def accounts-2-raw
  [{:password "$2a$10$Cp0GK58KbTE3SYx8wmgqLO2oF2JHLp/a72D6UGevvKCwfzdPHqBHu"
    :username "admin"}
   {:password "$2a$10$Cp0GK58KbTE3SYx8wmgqLO2oF2JHLp/a72D6UGevvKCwfzdPHqBHu"
    :username "guest"}])

(def accounts-2-converted
  {"admin" {:password "$2a$10$Cp0GK58KbTE3SYx8wmgqLO2oF2JHLp/a72D6UGevvKCwfzdPHqBHu"
            :username "admin"
            :roles #{:tlog.data.account/admin}}
   "guest" {:password "$2a$10$Cp0GK58KbTE3SYx8wmgqLO2oF2JHLp/a72D6UGevvKCwfzdPHqBHu"
            :username "guest"
            :roles #{:tlog.data.account/guest}}})

;; Using #'tlog.data.account/ to access private fn:
(tabular "Account conversion from database query results to Friend format works."
  (fact 
     (#'tlog.data.account/convert-accounts ?a) => ?b)
     ?a             ?b
     accounts-1-raw accounts-1-converted
     accounts-2-raw accounts-2-converted)