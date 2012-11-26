(ns tlog.dispatch.handle
  "Take requests from routing. Build responses, usually by calling views with query results from the
   model."
  (:require [ring.util.response :refer [response redirect]]
            [net.cgrand.moustache :refer [alter-response]]
            [tlog.render.render :as v]))

(defn journal
  [r]
  {:body "Journal"})

(defn login
  [r]
  v/login)

(defn logout [r] (redirect "/"))

(defn admin
  [r]
  v/admin)

(defn not-found
  []
  (-> "404: There's nothing associated with this URL."
      response
      constantly
      (alter-response #(assoc % :status 404))))