(ns tlog.data.t-account
  (:require [midje.sweet :refer [fact tabular]]
            [tlog.data.account]))

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