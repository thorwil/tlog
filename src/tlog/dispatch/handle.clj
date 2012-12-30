(ns tlog.dispatch.handle
  "Take requests from routing. Build responses, usually by calling tlog.render.page functions
   with database query results obtained via tlog.data.*."
  (:require [ring.util.response :refer [response redirect]]
            [net.cgrand.moustache :refer [alter-response]]
            [tlog.render.page :as p]
            [tlog.data.article :as article]))

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
  "Take article parameters from a request :body. Store a new article in the database. Return a
   status 201: Created, if article/create! returns. "
  [{:keys [uri body]}]
  (article/create! (.substring uri 1) ;; Drop leading "/", to extract the slug.
                   (:title body)
                   (-> body :content cleanup-html-string)
                   (-> body :feeds (clojure.string/split ,,, #" ")))
  {:status 201 ;; = Created
   :headers {"Content-Type" "text/plain"}
   :body "Success"})

(defn resource
  [rsc]
  (-> (str {:body rsc})
      response
      constantly))

(def not-found
  (-> p/not-found
      response
      constantly
      (alter-response #(assoc % :status 404))))