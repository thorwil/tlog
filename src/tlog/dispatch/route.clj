(ns tlog.dispatch.route
  "Route requests to handlers, based on URL patterns, methods and authorization."
  (:require [net.cgrand.moustache :refer [app]]
            [cemerick.friend :as friend]
            [ring.middleware.resource :refer [wrap-resource]]
            [ring.middleware.file-info :refer [wrap-file-info]]
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

(defn static
  [r]
  ((-> h/not-found
       (wrap-resource "/")
       wrap-file-info)
   r))

(defroutes get-routes
  [] h/journal
  ["static" &] static
  ["login"] h/login
  ["logout"] logout
  [&] h/not-found)

(defroutes admin-post-routes
  [] h/admin
  ["write"] h/write
  [&] h/not-found)

;; This would be nicer written inline as [(friend/wrap-authorize [:tlog.data.account/admin])
;; admin-post-routes], but there needs to be a var for the test to redef it:
(defn admin-protected
  [r]
  ((friend/wrap-authorize admin-post-routes [:tlog.data.account/admin]) r))

(defroutes root-routes
  ["admin" &] {:get admin-protected}
                  
  [&]
  {:get get-routes})