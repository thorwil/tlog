(ns tlog.data.article
  "Storing and retrieving articles."
  (:require [korma.core :as k]
            [tlog.data.access :refer [db]]
            [tlog.data.feed :as feed]
            [tlog.data.resource :as resource]))

db

(k/defentity article
  (k/pk :slug))

(defn create-row!
  [data]
  (k/insert article (k/values data)))

(defn create!
  "Take strings for a new article's slug, title, body and a vector of strings of feed-slugs for the
   feeds the article shall appear in. Create rows in the resource, article and article_feed_rels
   tables."
  [slug title body feed-slugs]
  (resource/create! slug "article")
  (create-row! {:slug slug :title title :body body})
  (feed/create-article-feed-rels! slug feed-slugs))