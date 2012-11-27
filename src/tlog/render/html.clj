(ns tlog.render.html
  "Build HTML in a modular way."
  (:require [ring.util.response :refer [response]]
            [hiccup.core :refer [html]]
            ;; [hiccup.def :refer [defhtml]]
            [hiccup.page :refer [html5]]))

(defn skeleton
  "Outer page skeleton."
  [{:keys [title
	   buildup]}]
  (html5
   [:html {:lang "en"}
    [:head
     [:meta {:charset "utf-8"}]
     [:body
      [:div#main
       [:div#content buildup]]]]]))

(def login
  (html
   [:h2 "Login"]
   [:form {:action "/login" :method "POST"}
    [:table
     [:tr
      [:td
       [:label "Username"]]
      [:td
       [:input {:type "text" :name "username" :value ""}]]]
     [:tr
      [:td
       [:label "Password"]]
      [:td
       [:input {:type "password" :name "password" :value ""}]]]]
    [:input {:type "submit" :value "submit"}]]))