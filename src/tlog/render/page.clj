(ns tlog.render.page
  "Take data from tlog.dispatch.handle and build HTML responses, using tlog.render.html."
  (:require [tlog.render.html :as h]))

(def journal
  (h/skeleton {:title "Login" :buildup "Journal"}))

(def login
  (h/skeleton {:title "Login" :buildup h/login-form}))

(def admin
  (h/skeleton {:title "Admin" :buildup "Admin"}))

(def write
  (h/skeleton {:title "Write" :buildup h/article-form}))