(ns tlog.data.access
  "Database access (PostgreSQL), used via tlog.dispatch.handle."
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