(ns tlog.data.article
  "Storing and retrieving articles."
  (:require [clojure.java.jdbc :as d]
            [tlog.data.access :refer [db]]
            [tlog.data.feed :as feed]
            [tlog.data.resource :as resource]))

(defn create-row!
  [& data]
  (d/insert-rows "article" data))

(defn create!
  "Take strings for a new article's slug, title, body and a vector of strings of feed-slugs for the
   feeds the article is associated with. Create rows in the resource, article and article_feed_rels
   tables."
  [slug title body feed-slugs]
  (d/with-connection db
    (resource/create! slug "article")
    (create-row! slug title body)
    (feed/create-article-feed-rels! slug feed-slugs)))