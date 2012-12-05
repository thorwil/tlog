(ns tlog.dispatch.handle
  "Take requests from routing. Build responses, usually by calling views with query results from the
   model."
  (:require [ring.util.response :refer [response redirect]]
            [net.cgrand.moustache :refer [alter-response]]
            [tlog.render.page :as p]))

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
  [r]
  (do (println (:uri r))
      (println r)
      {:status 200
       :headers {"Content-Type" "text/plain"}
       :body "example"}))

(def not-found
  (-> p/not-found
      response
      constantly
      (alter-response #(assoc % :status 404))))