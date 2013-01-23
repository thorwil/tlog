(ns tlog.data.feed
  "Retrieving feeds, storing and retrieving feed-article relations."
  (:require [korma.core :as k]
            [tlog.data.access :refer [db]]))

db

(k/defentity feed
  (k/pk :slug))

(defn- convert-feeds
  "Take a seq of maps of feeds as delivered from the database. Sort by :position (0...n), extract
   :slug and :preset values, return as array-map."
  [fs]
  (apply array-map (mapcat #(map % [:slug :preset]) (sort-by :position fs))))

(defn feed-defaults
  "Query db for a map of feeds with default checked states."
  []
  (convert-feeds (k/select feed)))

(k/defentity article_feed_rel
  ;; (k/pk :article_slug :feed_slug) Doesn't work: Korma does not support composite keys explicitly.
  )

(defn create-article-feed-rels!
  "Take an article slug and a vector of feed slugs. Add rows to article_feed_rel table. Assume that
   there are no rows for the article-slug already, as this is meant to be used on article creation."
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

(defn feed-pairs-for-article
  "Take an article slug. Return a list of vectors, each consisting of a feed name and either the
   feed name again (as true value), or nil. Here nil stands for the article not being a member of
   the feed."
  [slug]
  (let [memberships (feed-slugs-for-article slug)]
    (for [[feed checked] (feed-defaults)] [feed (some #{feed} memberships)])))

(defn rel-does-not-exist?
  "Return boolean that is true if there's a row for the given article slug and feed name in the
   article_feed_rel table."
  [article-slug feed-slug]
  (empty? (k/select article_feed_rel (k/where {:article_slug article-slug
                                               :feed_slug feed-slug}))))

(defn set-article-feed-rel!
  "Take an article slug, a feed name and a boolean that is true if the article is associated with
   the feed. Create or delete a row in the article_feed_rel table accordingly. Return 2 booleans,
   the first being true if the relation exists in the end, the second being true if the existance
   of the relation has been changed."
  [article-slug feed-slug member]
  (if member
    ;; Store relation, if it doesn't exist already (avoid PSQLException ERROR: duplicate key value
    ;; violates unique constraint "article_feed_rel_pkey"):
    (if (rel-does-not-exist? article-slug feed-slug)
      (do (k/insert article_feed_rel (k/values {:article_slug article-slug
                                                :feed_slug feed-slug}))
          [true true])
      [true false])
    ;; Erase relation (an attempt to delete what's not there returns nil):
    (if (k/delete article_feed_rel (k/where {:article_slug article-slug
                                             :feed_slug feed-slug}))
        [false true]
        [false false])))