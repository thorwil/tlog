(ns tlog.model.model
  (:require [clojure.java.jdbc :as d]))
 
(let [db-host "localhost"
      db-port 5432
      db-name "tlog"]
 
  (def db {:classname "org.postgresql.Driver" ; must be in classpath
           :subprotocol "postgresql"
           :subname (str "//" db-host ":" db-port "/" db-name)
           ; Any additional keys are passed to the driver
           ; as driver-specific properties.
           :user "postgres"
           :password "database"}))

(defn test-query
  []
  (d/with-connection db
    (d/with-query-results r ["select hash from account where nick = 'admin';"]
      (-> r first :hash))))