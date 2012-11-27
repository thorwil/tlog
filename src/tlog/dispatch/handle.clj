(ns tlog.dispatch.handle
  "Take requests from routing. Build responses, usually by calling views with query results from the
   model."
  (:require [ring.util.response :refer [response redirect]]
            [net.cgrand.moustache :refer [alter-response]]
            [tlog.render.page :as p]))

(defn journal
  [r]
  p/journal)

(defn login
  [r]
  p/login)

(defn logout
  [r]
  (redirect "/"))

(defn admin
  [r]
  p/admin)

(defn write
  [r]
  p/write)

(defn not-found
  []
  (-> "404: There's nothing associated with this URL."
      response
      constantly
      (alter-response #(assoc % :status 404))))