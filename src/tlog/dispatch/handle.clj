(ns tlog.dispatch.handle
  "Take requests from routing, build responses (by calling views with query results from the model)."
  (:require [ring.util.response :refer [response redirect]]
            [tlog.data.data :as m]
            [tlog.render.render :as v]))

(defn journal
  [r]
  {:body (m/test-query)})

(defn login
  [r]
  v/login)

(defn logout [r] (redirect "/"))

(defn admin
  [r]
  v/admin)