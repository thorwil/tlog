(ns tlog.render.page
  "Take data from tlog.dispatch.handle and build HTML responses, using tlog.render.html."
  (:require [tlog.render.html :as h]))

(defn- var-as-map
  "Take a #'var or a quoted sexp starting with a #'var. Return a map with the var's :name as keyword
   and the var's value as body."
  [v]
  (if (list? v)
    ;; Argument is a list (and should be a quoted sexp starting with a #'var)
    {(-> v first eval meta :name keyword) (eval v)}
    ;; Argument is a not a list (and should be a #'var)
    {(-> v meta :name keyword) (var-get v)}))

(def journal
  (h/skeleton {:title "Journal" :buildup "Journal"}))

(def login
  (h/skeleton {:title "Login" :buildup h/login-form}))

(def admin
  (h/skeleton {:title "Admin" :buildup "Admin"}))

(def write*
  (h/skeleton {:title "Writer"
               :scripts h/aloha-admin
               :buildup h/article-form}))

(def write
  (h/skeleton (into  {:title "Writer"
                      :scripts h/aloha-admin
                      :buildup h/article-form}
                     (var-as-map #'h/option-noscript-warning))))

(def not-found
  (h/skeleton {:title "Write" :buildup "404: There's nothing associated with this URL."}))
