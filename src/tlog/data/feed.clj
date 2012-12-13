(ns tlog.data.feed
  "For now just retrieving user credentials from the database."
  (:require [clojure.java.jdbc :as d]
            [tlog.data.access :refer [db]]))

(def ^:private feeds-raw
  "Query db for a map of accounts."
  (d/with-connection db
    (d/with-query-results as ["SELECT * FROM feed;"]
      (into [] as))))

(defn- convert-feeds
  "Take a seq of maps of feeds as delivered from the database. Sort by :position (0...n), extract
   :slug and :preset values, return as array-map."
  [fs]
  (apply array-map (mapcat #(map % [:slug :preset]) (sort-by :position fs))))

(def feeds
  (convert-feeds feeds-raw))
