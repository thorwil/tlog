(ns tlog.data.resource
  "Storing and retrieving resources."
  (:require [korma.core :as k]
            [tlog.data.access :refer [db]]
            [tlog.data.time :refer [now]]))


db

(k/defentity resource
  (k/pk :slug))

(defn create!
  "Take strings for slug and table-reference (the entity type). Add a row to the resource table."
  [slug table-reference]
  (let [now (now)]
    (k/insert resource (k/values {:slug slug
                                  :created_timestamp now
                                  :updated_timestamp now
                                  :table_reference table-reference}))))

(defn update-timestamp!
  "Take a slug (string). Set the associated updated_timestamp to current time. Return the new
   updated_timestamp, or nil if there is no resource for the given slug."
  [slug]
  (let [now (now)]
    (when (k/update resource
                    (k/set-fields {:updated_timestamp now})
                    (k/where {:slug slug}))
      now)))

(defn resolve-table-reference
  "Take a resource map. Retrieve the entity associated via :table_reference and :slug. Return a
   merged map."
  [resource-map]
  (into resource-map
        (first (k/select (:table_reference resource-map)
                         (k/where {:slug (:slug resource-map)})))))

(defn- slug->raw-resource-or-nil
  "Take a slug (string). Retrieve the resource for :slug. Return nil if there is no resource for the
   slug."
  [slug]
  ;; k/select returns a vector of matches. Here it can only be empty or contain a single map:
  (first (k/select resource (k/where {:slug slug}))))

(defn slug->resource-or-nil
  "Take a slug (string). Retrieve the resource for :slug and the entity referenced via
   :table_reference and :slug . Return nil if there is no resource for the slug."
  [slug]
  (if-let [r (slug->raw-resource-or-nil slug)]
    (resolve-table-reference r)))

;; Placed here for not having to require slug->raw-resource-or-nil elsewhere:
(defn valid-article-slug
  "Take a slug (string). Return the slug, if it is associated with an article, otherwise false."
  [slug]
  (and (= (-> slug
              slug->raw-resource-or-nil
              :table_reference)
          "article")
       slug))

(defn article-range
  "Take: - offset into the list of article resources sorted by creation time.
         - limit to number of results.
   Return matching vector of article resource maps."
  [offset limit]
  (k/select resource
            (k/where {:table_reference "article"})
            (k/order :created_timestamp :DESC)
            (k/offset offset)
            (k/limit limit)))


;; For testing

(defn set-updated-to-created-timestamp
  "Set updated_timestamp equal to created_timestamp, as if the resource had never been updated."
  [slug]
  (k/update resource
            (k/set-fields {:updated_timestamp (-> (k/select resource (k/where {:slug slug}))
                                                  first
                                                  :created_timestamp)})
            (k/where {:slug slug})))
