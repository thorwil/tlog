(ns tlog.route.route
  "Map URL patterns to handlers."
  (:use [net.cgrand.moustache :only [app]]))

(defmacro defroutes
  "def name to a moustache app form."
  [name & more]
  `(def ~name (app ~@more)))

(defroutes root
  ["admin" &] {:get "Admin."}
  [&]
  {:get "Visitor."})

