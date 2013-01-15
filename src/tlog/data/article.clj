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
  "Take a not yet existing article's slug, title, body and a vector of strings of feed-slugs for the
   feeds the article shall appear in. Create rows in the resource, article and article_feed_rels
   tables."
  [slug title content feeds]
  (d/transaction
   (resource/create! slug "article")
   (create-row! {:slug slug :title title :content content})
   (feed/create-article-feed-rels! slug feeds)))

(defn update!
  "Take an article's slug, title and content. Update the article table. Update the created_timestamp
   in the resource table."
  [slug title content]
  (d/transaction
   (k/update article
             (k/set-fields {:title title
                            :content content})
             (k/where {:slug slug}))
   (resource/update-timestamp! slug)))

(def slugs
  "List of all article slugs"
  (map :slug (k/select article (k/fields :slug))))