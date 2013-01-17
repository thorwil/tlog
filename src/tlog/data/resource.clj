(ns tlog.data.resource
  "Storing and retrieving resources."
  (:require [korma.core :as k]
            [tlog.data.access :refer [db]]))

(defn- now
  "Get the current time as java.sql.Timestamp."
  []
  (java.sql.Timestamp. (.getTime (java.util.Date.))))

db

(k/defentity resource
  (k/pk :slug))

(defn create!
  "Take strings for slug and table-reference (the entity type). Add a row to the resource table."
  [slug table-reference]
  (let [now now]
    (k/insert resource (k/values {:slug slug
                                  :created_timestamp now
                                  :updated_timestamp now
                                  :table_reference table-reference}))))

(defn update-timestamp!
  "Take a slug (string). Set the associated updated_timestamp to current time. Return the new
   updated_timestamp."
  [slug]
  (let [now (now)]
    (k/update resource
              (k/set-fields {:updated_timestamp now})
              (k/where {:slug slug}))
    now))

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


;; For testing

(defn set-updated-to-created-timestamp
  "Set updated_timestamp equal to created_timestamp, as if the resource had never been updated."
  [slug]
  (k/update resource
            (k/set-fields {:updated_timestamp (-> (k/select resource (k/where {:slug slug}))
                                                  first
                                                  :created_timestamp)})
            (k/where {:slug slug})))