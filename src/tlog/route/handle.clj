(ns tlog.route.handle
  "Take requests from routing, call views with query results from the model."
  (:require [ring.util.response :refer [response redirect]]
            [tlog.model.model :as m]
            [tlog.view.view :as v]))

(defn journal
  [r]
  {:body (m/test-query)})

(defn login
  [r]
  v/login)

(defn logout [r] (redirect "/"))

(def admin
  "Admin")