(ns tlog.data.resource
  "Storing and retrieving resources."
  (:require [clojure.java.jdbc :as d]
            [tlog.data.access :refer [db]]))

(def now (java.sql.Timestamp. (.getTime (java.util.Date.))))

(defn create!
  "Take a slug and a table-reference (the entity type). Add a row to the resource table. Has to
   happen within a d/with-connection."
  [slug table-reference]
  (let [now now]
    (d/insert-rows "resource" [slug now now table-reference])))