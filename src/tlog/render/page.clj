(ns tlog.render.page
  "Take data from tlog.dispatch.handle and build HTML responses, using tlog.render.html."
  (:require [ring.util.response :refer [response]]
            [tlog.render.html :as h]))

(def journal
  (-> {:title "Login" :buildup "Journal"} h/skeleton response))

(def login
  (-> {:title "Login" :buildup h/login-form} h/skeleton response))

(def admin
  (-> {:title "Admin" :buildup "Admin"} h/skeleton response))

(def write
  (-> {:title "Write" :buildup h/article-form} h/skeleton response))