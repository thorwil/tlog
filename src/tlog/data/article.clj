(ns tlog.data.article
  "Storing and retrieving articles."
  (:require [korma.core :as k]
            [korma.db :as d]
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
  "Take a map with strings for a new article's slug, title, body and a vector of strings of
   feed-slugs for the feeds the article shall appear in. Create rows in the resource, article and
   article_feed_rels tables."
  [slug title content feeds]
  (d/transaction
   (println slug title content feeds)
   (resource/create! slug "article")
   (create-row! {:slug slug :title title :content content})
   (feed/create-article-feed-rels! slug feeds)))

(def slugs
  "List of all article slugs"
  (map :slug (k/select article (k/fields :slug))))