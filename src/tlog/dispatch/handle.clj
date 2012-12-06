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
  "Take article parameters from a request :body. For now just print it."
  [{:keys [body]}]
  (do (println body)
      {:status 201 ;; Status 201: Created
       :headers {"Content-Type" "text/plain"}
       :body "Success"}))

(def not-found
  (-> p/not-found
      response
      constantly
      (alter-response #(assoc % :status 404))))