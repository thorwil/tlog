(ns tlog.render.html.admin
  "HTML fragments for admin. To be handed to skeleton as values of keys other than :main."
  (:require [hiccup.core :refer [html]]
            [hiccup.def :refer [defhtml]]
            [tlog.render.html.option-macros :refer [defopt defopt-fn]]))

(defn- map-map-if
  "Take a key, 2 functions and a map. Return a map of the same kind, with one of the functions
   applied to each value. The first function, where the key matches the key argument, the second
   function in all other cases."
  [akey f-true f-false m]
  (into (empty m) (for [[k v] m] [k ((if (= akey k)
                                       f-true
                                       f-false)
                                     v)])))

(defhtml ^:private text+href->link
  [[text href]]
  [:a {:href href} text])

(defhtml ^:private text+href->span
  [[text href]]
  [:span text])

(defopt noscript-warning
  (html [:noscript [:div#noscript-warning "This won't work with JavaScript disabled ;)"]]))

(defopt-fn admin-bar
  "Take no argument or a key referring to the current page. Render area with links for the logged in
   admin, with the link, that is associated with the key (if any), turned into plain text."
  [& [current]]
  (let [items-default (array-map :list ["List" "/admin"]
                                 :write ["Write" "/admin/write"]
                                 :file ["File" "/admin/file"]
                                 :logout ["Log out" "/logout"])]
    (if (or (nil? current)
            (some #{current} (keys items-default)))
      ;; No or known key given, render admin-bar:
      (let [items (map-map-if current text+href->span text+href->link items-default)]
        (html
         [:div#top
          [:nav {:id "admin-bar"}
           [:ul
            (for [v (vals items)]
              [:li v])]]]))
      ;; Unrecognized key, render error message:
      "option-admin-bar: mistyped or wrong key!"))
  :option-admin-bar)