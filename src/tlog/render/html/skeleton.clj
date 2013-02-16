(ns tlog.render.html.skeleton
  "HTML page skeleton."
  (:require [hiccup.page :refer [html5]]
            [tlog.render.configuration :as conf]))

(def ^:private head-title
  "Build string for the <head>, <title> tag."
  #(str %
        (when (not-empty %) conf/title-seperator)
        conf/title-main))

(defn skeleton
  "HTML page skeleton. The keys admin-bar and noscript-warning are used for optional components."
  [{:keys [title
           scripts
           admin-bar
           noscript-warning
           headline
	   main]}]
  (html5 {:lang "en"}
   [:head
    [:meta {:charset "utf-8"}]
    [:title (head-title title)]
    scripts
    [:meta {:name "description" :content conf/meta-description}]
    [:meta {:name "author" :content conf/author}]
    [:link {:href conf/font-link
            :rel "stylesheet"
            :type "text/css"}]
    [:link {:rel "stylesheet" :href "/main.css" :type "text/css"}]]
   [:body
    admin-bar
    noscript-warning
    [:div#main
     [:div#content
      [:h1 (or headline title)]
      main]]]))
