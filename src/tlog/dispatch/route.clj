(ns tlog.dispatch.route
  "Route requests to handlers, based on URL patterns, methods and authorization."
  (:require [net.cgrand.moustache :refer [app]]
            [cemerick.friend :as friend]
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
  ["logout"] logout)

;; This would be nicer written inline as [(friend/wrap-authorize [:tlog.data.account/admin])
;; h/admin], but there needs to be a var for the test to redef it:
(defn admin
  [r]
  ((friend/wrap-authorize h/admin [:tlog.data.account/admin]) r))

(defroutes root-routes
  ["admin" &] {:get admin}
                  
  [&]
  {:get get-routes})