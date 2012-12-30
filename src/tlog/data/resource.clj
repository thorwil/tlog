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

(defn slug->resource-or-nil
  "Take a slug (string). Return a resource for the slug, or nil if there is none."
  [slug]
  (first (k/select resource (k/where {:slug slug}))))

(defn resolve
  "Take a resource map. Retrieve the entity associated via :table_reference and :slug. Return a
   combined map."
  [rsc]
  (into rsc
        (first (k/select (:table_reference rsc)
                         (k/where {:slug (:slug rsc)})))))