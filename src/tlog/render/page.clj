(ns tlog.render.page
  "Take data from tlog.dispatch.handle and build HTML responses, using tlog.render.html."
  (:require [ring.util.response :refer [response]]
            [tlog.render.html :as h]))

(def login
  (-> {:title "Login" :buildup h/login} h/skeleton response))

(def admin
  (-> {:title "Admin" :buildup "Admin"} h/skeleton response))