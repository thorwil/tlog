(ns tlog.render.html.skeleton
  "HTML page skeleton."
  (:require [hiccup.page :refer [html5]]
            [tlog.render.configuration :as conf]))

(def ^:private title-with
  "Build string for the <head>, <title> tag."
  #(str %
        (when (not-empty %) conf/title-seperator)
        conf/title-main))

(defn skeleton
  "HTML page skeleton."
  [{:keys [title
           scripts
           option-admin-bar
           option-noscript-warning
	   main]}]
  (html5
   [:html {:lang "en"}
    [:head
     [:meta {:charset "utf-8"}]
     [:title (title-with title)]
     scripts
     [:meta {:name "description" :content conf/meta-description}]
     [:meta {:name "author" :content conf/author}]
     [:link {:href conf/font-link
             :rel "stylesheet"
             :type "text/css"}]
     [:link {:rel "stylesheet" :href "/main.css" :type "text/css"}]
     [:body
      option-admin-bar
      option-noscript-warning
      [:div#main
       [:div#content main]]]]]))