(ns tlog.render.html.parts.navigation
  "HTML parts for navigation. To be handed to skeleton as values of keys other than :main."
  (:require [hiccup.core :refer [html]]
            [hiccup.def :refer [defhtml]]))


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

(defn navigation-list-fn
  [link-map container-tag]
  (fn [& [current]]
    (html
     (let [items-default link-map]
       (if (or (nil? current)
               (some #{current} (keys items-default)))
         ;; No or known key given, render admin-bar:
         (let [items (map-map-if current text+href->span text+href->link items-default)]
           (html
            [container-tag
             [:ul
              (for [v (vals items)]
                [:li v])]]))
         ;; Unrecognized key, render error message:
         "Mistyped or wrong key!")))))

;; Wrap navigation-list-fn to get a combination that takes no or any arguments (ignores any after
;; the first) and returns HTML in a map as value to :admin-bar:
(def admin-bar (fn [& [args]]
                 {:admin-bar ((navigation-list-fn (array-map :list ["List" "/admin"]
                                                             :write ["Write" "/admin/write"]
                                                             :file ["File" "/admin/file"]
                                                             :logout ["Log out" "/logout"])
                                                  :nav#admin-bar) args)}))

(def main-pages (navigation-list-fn (array-map :journal ["Journal" "/"]
                                               :portfolio ["Portfolio" "/portfolio"]
                                               :about ["About" "/about"])
                                    :nav#pages))
