(ns tlog.render.page
  "Take data from tlog.dispatch.handle and build HTML responses, using tlog.render.html."
  (:require [tlog.render.html.skeleton :refer [skeleton]]
            [tlog.render.html.main :as main]
            [tlog.render.html.admin :as option]
            [tlog.render.html.script :as script]))

(def journal
  (skeleton {:title "Journal"
             :main "Journal"}))

(def login
  (skeleton {:title "Login"
             :main main/login-form}))

(def admin
  (skeleton {:title "Admin"
             :main "Admin"}))

(def write
  (skeleton (reduce into
                    [{:title "Write"
                      :scripts script/aloha-admin
                      :main main/article-form}
                     option/noscript-warning
                     (option/admin-bar :write)])))

(defn article
  [item]
  (skeleton {:title (:title item)
             :scripts script/client-time-offset
             :main (main/article-solo item)}))

(def not-found
  (skeleton {:title "404"
             :main "404: There's nothing associated with this URL."}))
