(ns tlog.render.page
  "Take data from tlog.dispatch.handle and build HTML responses, using tlog.render.html."
  (:require [tlog.render.html :as h]))

(def journal
  (h/skeleton {:title "Journal"
               :main "Journal"}))

(def login
  (h/skeleton {:title "Login"
               :main h/login-form}))

(def admin
  (h/skeleton {:title "Admin"
               :main "Admin"}))

(def write
  (h/skeleton (reduce into
                      [{:title "Write"
                        :scripts h/aloha-admin
                        :main h/article-form}
                       h/option-noscript-warning
                       (h/option-admin-bar :write)])))

(def not-found
  (h/skeleton {:title "Write"
               :main "404: There's nothing associated with this URL."}))
