(ns tlog.route.route
  "Map URL patterns to handlers."
  (:require [tlog.route.handle :as h])
  (:use [net.cgrand.moustache :only [app]]))

(defmacro defroutes
  "def name to a moustache app form."
  [name & more]
  `(def ~name (app ~@more)))

(defroutes get
  [] (h/journal))

(defroutes root
  ["admin" &] {:get "Admin."}
  [&]
  {:get get})

