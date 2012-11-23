(ns tlog.dispatch.route
  "Route requests to handlers, based on URL patterns, methods and authorization."
  (:require [net.cgrand.moustache :refer [app]]
            [cemerick.friend :as friend]
            [tlog.dispatch.handle :as h]))

(defmacro defroutes
  "def name to a moustache app form."
  [name & more]
  `(def ~name (app ~@more)))

(defroutes get-routes
  [] h/journal
  ["login"] h/login
  ["logout"] [friend/logout h/logout])

(defroutes root-routes
  ["admin" &] {:get [(friend/wrap-authorize [:tlog.data.account/admin]) h/admin]}
                  
  [&]
  {:get get-routes})