(ns tlog.dispatch.handle
  "Take requests from routing. Build responses, usually by calling views with query results from the
   model."
  (:require [ring.util.response :refer [response redirect]]
            [net.cgrand.moustache :refer [alter-response]]
            [clojure.data.json :as json]
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
  "Take a JSON string as :body."
  [{:keys [body]}]
  (let [body* (try (-> body slurp (json/read-str :key-fn keyword))
                   (catch Exception e nil))]
    (if body*
      (do (println body*)
          {:status 201 ;; Status 201: Created
           :headers {"Content-Type" "text/plain"}
           :body "Success"})
      {:status 400 ;; Status 400: Bad Request
       :headers {"Content-Type" "text/plain"}
       :body "Failure"})))

(def not-found
  (-> p/not-found
      response
      constantly
      (alter-response #(assoc % :status 404))))