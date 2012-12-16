(ns tlog.data.access
  "Database access (PostgreSQL)."
  (:require [korma.db :refer [defdb postgres]]))
 
(defdb db (postgres {:db "tlog"
                     :user "postgres"
                     :password "database"
                     :host "localhost"
                     :port "5432"
                     :delimiters ""}))