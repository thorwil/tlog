(ns tlog.data.feed
  "Retrieving feeds, storing and retrieving feed-article relations."
  (:require [korma.core :as k]
            [tlog.data.access :refer [db]]))

db

(k/defentity feed
  (k/pk :slug))

(def ^:private feeds-raw
  "Query db for a map of feeds."
  (k/select feed))

(defn- convert-feeds
  "Take a seq of maps of feeds as delivered from the database. Sort by :position (0...n), extract
   :slug and :preset values, return as array-map."
  [fs]
  (apply array-map (mapcat #(map % [:slug :preset]) (sort-by :position fs))))

(def feeds
  (convert-feeds feeds-raw))

(k/defentity article_feed_rel
  ;; (k/pk :article_slug :feed_slug) Doesn't work: Korma does not support composite keys explicitly.
  )

(defn create-article-feed-rels!
  "Take an article slug and a vector of feed slugs. Add row to article_feed_rel table. Assume that
   there are no rows for the article-slug already."
  [article-slug feed-slugs]
  (k/insert article_feed_rel (k/values
                              (for [feed-slug feed-slugs] {:article_slug article-slug
                                                           :feed_slug feed-slug}))))

(defn article-slugs-in-feed
  "Take a feed slug. Return a list of slugs for all articles the feed shall contain."
  [feed-slug]
  (map :article_slug
       (k/select article_feed_rel
                 (k/fields :article_slug)
                 (k/where {:feed_slug feed-slug}))))

(defn feed-slugs-for-article
  "Take an article slug. Return a list of the slugs for the feeds the article shall appear in."
  [article-slug]
  (map :feed_slug
       (k/select article_feed_rel
                 (k/fields :feed_slug)
                 (k/where {:article_slug article-slug}))))