(ns tlog.dispatch.handle
  "Take requests from routing. Build responses, usually by calling tlog.render.page functions
   with database query results obtained via tlog.data.*."
  (:require [ring.util.response :refer [response redirect]]
            [net.cgrand.moustache :refer [alter-response]]
            [tlog.render.page :as p]
            [tlog.data.article :as article]
            [tlog.data.resource :as resource]))

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
  {"article" p/article})

(defn- roles
  "Extract the value of :roles from a request map (passed through Friend)."
  [r]
  (-> r :session :cemerick.friend/identity :authentications vals first :roles))


;; Handlers

(defn journal
  [r]
  (-> p/journal
      response))

(defn login
  [r]
  (-> p/login
      response))

(defn logout
  [r]
  (redirect "/"))

(defn admin
  [r]
  (-> p/admin
      response))

(defn write
  [r]
  (-> p/write
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

(defn update-article
  "Take a request map, expecting :uri and :body with an article's title and content. Update the
   article in the database. Return a status 201: Created, if article/update! returns."
  [{:keys [uri body]}]
  (article/update! (.substring uri 1) ;; Drop leading "/", to extract the slug.
                   (:title body)
                   (-> body :content cleanup-html-string))
  {:status 200 ;; = OK
   :headers {"Content-Type" "text/plain"}
   :body "Article updated."}) ;; Why doesn't this appear as status text in the alert?

(defn resource
  "Take a resource map. Return a rendition of the combined resource and referenced table (for now
   only article) map."
  [rsc]
  (let [item (resource/resolve rsc)
        page (-> rsc :table_reference table-ref->page)]
    (fn [request] (response (page (roles request) item)))))

(def not-found
  (-> p/not-found
      response
      constantly
      (alter-response #(assoc % :status 404))))