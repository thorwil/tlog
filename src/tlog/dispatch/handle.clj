(ns tlog.dispatch.handle
  "Take requests from routing. Build responses, usually by calling views with query results from the
   model."
  (:require [ring.util.response :refer [response redirect]]
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