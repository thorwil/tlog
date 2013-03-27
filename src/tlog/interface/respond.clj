(ns tlog.interface.respond
  "Take requests from routing. Build responses, usually by calling tlog.render.html.assemble
   functions with database query results obtained via tlog.data.*."
  (:require [ring.util.response :refer [response redirect]]
            [net.cgrand.moustache :refer [alter-response]]
            [tlog.interface.configuration :refer [articles-per-journal-page]]
            [tlog.render.html.assemble :as a]
            [tlog.data.article :as article]
            [tlog.data.resource :as resource]
            [tlog.data.comment :as comment]
            [tlog.data.feed :as feed]
            [tlog.render.html.parts.time]
            [tlog.render.html.parts.comment :as render-comment]))


;; Utility

(defn- remove-empty-style
  "Remove empty style attributes from a string."
  [s]
  (clojure.string/replace s " style=\"\"" ""))

(defn- remove-empty-<p>
  "Remove <p> elements that are empty, except for a <br>, from a string."
  [s]
  (clojure.string/replace s "<p><br></p>" ""))

(defn- remove-<br>-cleanme
  "Remove <p> elements that are empty, except for a <br>, from a string."
  [s]
  (clojure.string/replace s "<br class=\"aloha-cleanme\">" ""))

(defn- cleanup-html-string
  [s]
  (-> s remove-empty-<p> remove-empty-style remove-<br>-cleanme))

(defn- roles
  "Extract the value of :roles from a request map (passed through Friend)."
  [r]
  (-> r :session :cemerick.friend/identity :authentications vals first :roles))

(defn article-range
  "Take index numbers for a first and last article. Return seq of maps of all articles within that
   range. There are no fixed article index numbers. The association happens only for being able to
   have URI-fragments like 32-28 as a short way to specify a range (continuous selection) of
   articles. The oldest article has index 1, so that adding articles will not alter the mapping. As
   long as no deletions happen, a given range will always contain the same articles."
  [from to]
  (let [offset (- (article/a-count) from)
        ;; from must be greater than to, as long as no logic for reverse sorting is implemented:
        limit (inc (- from to))]
    (article/range offset limit)))


;; Handlers

(defn journal-default
  "Take a request. Return a response with a page with the default-n last articles."
  [r]
  (let [from (article/a-count) ;; from is also total
        to (- from articles-per-journal-page)]
    (response (a/journal (article-range from to)
                         [from to]
                         articles-per-journal-page
                         from))))

(defn journal
  "Take a vector of 2 index numbers. Return a function that will take a request and return a
   response with a page of articles matching the range specified by the 2 index numbers."
  [[from to]]
  (-> (a/journal (article-range from to)
                 [from to]
                 articles-per-journal-page
                 (article/a-count))
      response
      constantly))

(defn login
  [r]
  (-> a/login
      response))

(defn logout
  [r]
  (redirect "/"))

(defn admin
  [r]
  (-> a/admin
      response))

(defn write
  [r]
  (-> a/write
      response))

(defn put-article
  "Take a request map, expecting :uri and :body with an article's title, content and feeds. Store a
   new article in the database. Return a
   status 201: Created, if article/create! returns."
  [{:keys [uri body]}]
  (article/create! (.substring uri 1) ;; Drop leading "/", to extract the slug.
                   (:title body)
                   (-> body :content cleanup-html-string)
                   (-> body :feeds (clojure.string/split ,,, #" ")))
  {:status 201 ;; = Created
   :headers {"Content-Type" "text/plain"}
   :body "Article created."})

(defn- update-article-title+content
  "Update an article's title and content in the database. Return a status 200: OK (if
   article/update! returns). Include HTML for the updated timestamp as body."
  [slug title content]
  (let [updated-timestamp (article/update! slug
                                           title
                                           (cleanup-html-string content))]
    ;; updated-timestamp will be nil, if there was no resource for the given slug.
    (if updated-timestamp
        {:status 200 ;; = OK
         :headers {"Content-Type" "text/plain"}
         :body (tlog.render.html.parts.time/time-updated slug updated-timestamp)}
        {:status 400 ;; = Bad request
         :headers {"Content-Type" "text/plain"}
         :body (format "There doesn't seem to be an article that could be updated, with the slug %s"
                       slug)})))

(defn- update-article-feed-rel
  "Take an article slug, a feed slug and a boolean that is true if the article belongs to the feed.
   Store or erase an article and feed relation."
  [slug feed checked]
  (let [[checked-final changed] (feed/set-article-feed-rel! slug feed checked)]
    {:status (if changed
               200  ;; = OK
               409) ;; = Conflict
     :headers {"Content-Type" "text/plain"}
     :body (str checked-final)}))

(defn update-article*
  "Take a request map, expecting :uri and :body with either an article's title and content, or a
   feed name and its new state regarding having the article as member. Call
   update-article-title+content or update-article-feed-rel accordingly."
  [{:keys [uri body]}]
  (let [slug (.substring uri 1) ;; Drop leading "/", to extract the slug.
        {:keys [title content feed checked]} body]
    (if title
      (update-article-title+content slug title content)
      (update-article-feed-rel slug feed checked))))

(defn article
  "Called from resource, if :table_reference is 'article'."
  [roles article-map]
  (a/article roles article-map (comment/nested-comments (:slug article-map))))

(defn resource
  "Take a resource map, consisting of the merged query results from the resource table and the
   referenced table (for now always the article table). Return a rendition of the map."
  [resource-map]
  (let [handler (-> resource-map :table_reference {"article" article})]
    (fn [request] (response (handler (roles request) resource-map)))))

(defn put-comment
  "Take a a slug and a request map, containing a :body with a comment's content, name and link.
   Store a new comment in the database. Return a status 201: Created, if comment/create! returns."
  [{:keys [body]}]
  (if-let [c (comment/create! (:parent body)
                            (:author body)
                            (:email body)
                            (:link body)
                            (-> body :content cleanup-html-string))]
    {:status 201 ;; = Created
     :headers {"Content-Type" "text/plain"}
     :body (render-comment/new-thread-async c)}
    {:status 451 ;; = Parameter not understood
     :headers {"Content-Type" "text/plain"}
     :body "Failed to add comment!"}))

(def not-found
  (-> a/not-found
      response
      constantly
      (alter-response #(assoc % :status 404))))
