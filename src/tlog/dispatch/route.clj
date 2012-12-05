(ns tlog.dispatch.route
  "Route requests to handlers (wrapped in middleware), based on URL patterns, methods and
   authorization."
  (:require [net.cgrand.moustache :refer [app]]
            [cemerick.friend :as friend]
            [ring.middleware.resource :refer [wrap-resource]]
            [ring.middleware.file-info :refer [wrap-file-info]]
            [ring.middleware.params :refer [wrap-params]]
            [tlog.dispatch.handle :as h]))

(defmacro defroutes
  "def name to a moustache app form."
  [name & more]
  `(def ~name (app ~@more)))

;; This would be nicer written inline as [friend/logout h/logout], but there needs to be a var for
;; the test to redef it:
(defn logout
  [r]
  ((friend/logout h/logout) r))

(defroutes get-routes
  [] h/journal
  ["login"] h/login
  ["logout"] logout
  [&] h/not-found)

(defn static
  [r]
  ((-> get-routes
       (wrap-resource "/")
       wrap-file-info)
   r))

(defroutes admin-post-routes
  [] h/admin
  ["write"] h/write
  [&] h/not-found)

;; See comment for logout
(defn admin-protected
  [r]
  ((friend/wrap-authorize admin-post-routes [:tlog.data.account/admin]) r))

;; See comment for logout
(defn put-article
  [r]
  ((-> h/put-article
       wrap-params)
       ;; (friend/wrap-authorize [:tlog.data.account/admin]))
   r))

(defroutes root-routes
  ["admin" &] {:get admin-protected}
  [&] {:get static
       :put put-article})