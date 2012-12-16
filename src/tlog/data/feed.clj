(ns tlog.data.feed
  "Retrieving feeds, storing and retrieving feed-article relations."
  (:require [clojure.java.jdbc :as d]
            [tlog.data.access :refer [db]]))

(def ^:private feeds-raw
  "Query db for a map of feeds."
  (d/with-connection db
    (d/with-query-results rs ["SELECT * FROM feed;"]
      (into [] rs))))

(defn- convert-feeds
  "Take a seq of maps of feeds as delivered from the database. Sort by :position (0...n), extract
   :slug and :preset values, return as array-map."
  [fs]
  (apply array-map (mapcat #(map % [:slug :preset]) (sort-by :position fs))))

(def feeds
  (convert-feeds feeds-raw))

(defn create-article-feed-rels!
  "Take an article slug and a vector of feed slugs. Add row to article_feed_rel table. Assume that
   there are no rows for the article-slug already. Has to happen within a d/with-connection."
  [article-slug feed-slugs]
  (apply (partial d/insert-rows "article_feed_rel")
         (for [feed-slug feed-slugs] [article-slug feed-slug])))

(defn set-article-feed-rels!
  "Take an article slug and a vector of feed slugs. Delete previous article and feed relations,
   store new ones. Has to happen within a d/with-connection."
  [article-slug feed-slugs]
  (d/delete-rows "article_feed_rel" (format "article_slug = %s" article-slug))
  (create-article-feed-rels! article-slug feed-slugs))

(defn article-slugs-in-feed
  "Take a feed slug. Return a list of slugs for all articles the feed is associated with."
  [feed-slug]
  (d/with-connection db
    (d/with-query-results rs [(format "SELECT article_slug FROM article_feed_rel
                                                           WHERE feed_slug = '%s';" feed-slug)]
      (map :article_slug (into [] rs)))))

(defn feed-slugs-for-article
  "Take an article slug. Return a list of the slugs for the feeds the article is associated with."
  [article-slug]
  (d/with-connection db
    (d/with-query-results rs [(format "SELECT feed_slug FROM article_feed_rel
                                                        WHERE article_slug = '%s';" article-slug)]
      (map :feed_slug (into [] rs)))))