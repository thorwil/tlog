(ns tlog.data.resource
  "Storing and retrieving resources."
  (:require [korma.core :as k]
            [tlog.data.access :refer [db]]))

(def now (java.sql.Timestamp. (.getTime (java.util.Date.))))

db

(k/defentity resource
  (k/pk :slug))

(defn create!
  "Take a slug and a table-reference (the entity type). Add a row to the resource table."
  [slug table-reference]
  (let [now now]
    (k/insert resource (k/values {:slug slug
                                  :created_timestamp now
                                  :updated_timestamp now
                                  :table_reference table-reference}))))