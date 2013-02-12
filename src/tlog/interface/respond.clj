(ns tlog.interface.respond
  "Take requests from routing. Build responses, usually by calling tlog.render.html.assemble
   functions with database query results obtained via tlog.data.*."
  (:require [ring.util.response :refer [response redirect]]
            [net.cgrand.moustache :refer [alter-response]]
            [tlog.render.html.assemble :as h]
            [tlog.data.article :as article]
            [tlog.data.resource :as resource]
            [tlog.data.feed :as feed]
            [tlog.render.html.parts.time]))


;; Configuration

(def articles-per-journal-page 5)


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

(def ^:private table-ref->page
  "Associate resource table-references with functions for rendering them."
  {"article" h/article})

(defn- roles
  "Extract the value of :roles from a request map (passed through Friend)."
  [r]
  (-> r :session :cemerick.friend/identity :authentications vals first :roles))


;; Handlers

(defn article-range
  "Take index numbers for a first and last article. Return response with HTML representation of all
   articles within that range.

   There are no fixed article index numbers. The association happens only for being able to have
   URI-fragments like 32-28 as a short way to specify a range (continuous selection) of articles.
   The oldest article has index 1, so that adding articles will not alter the mapping. As long as no
   deletions happen, a given range will always contain the same articles."
  [from to]
  (let [offset (- (article/a-count) from)
        ;; from must be greater than to, as long as no logic for reverse sorting is implemented:
        limit (inc (- from to))]
    (article/range offset limit)))

(defn journal-default
  "Take a request. Return a response with a page with the default-n last articles."
  [r]
  (-> (let [a-count (article/a-count)]
        (article-range a-count (- a-count articles-per-journal-page)))
      h/journal
      response))

(defn journal
  "Take a vector of 2 index numbers. Return a function that will take a request and return a
   response with a page of articles matching the range specified by the 2 index numbers."
  [[from to]]
  (-> (article-range from to)
      h/journal
      response
      constantly))

(defn login
  [r]
  (-> h/login
      response))

(defn logout
  [r]
  (redirect "/"))

(defn admin
  [r]
  (-> h/admin
      response))

(defn write
  [r]
  (-> h/write
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

(defn resource
  "Take a resource map. Return a rendition of the combined resource and referenced table (for now
   only article) map."
  [rsc]
  (let [item (resource/resolve rsc)
        page (-> rsc :table_reference table-ref->page)]
    (fn [request] (response (page (roles request) item)))))

(def not-found
  (-> h/not-found
      response
      constantly
      (alter-response #(assoc % :status 404))))
