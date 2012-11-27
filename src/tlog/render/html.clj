(ns tlog.render.html
  "Build HTML in a modular way."
  (:require [ring.util.response :refer [response]]
            [hiccup.core :refer [html]]
            ;; [hiccup.def :refer [defhtml]]
            [hiccup.page :refer [html5]]
            [tlog.render.configuration :as conf]))

(def title-with
  "Build string for the <head>, <title> tag."
  #(str %
        (when (not-empty %) conf/title-seperator)
        conf/title-main))

(defn skeleton
  "HTML page skeleton."
  [{:keys [title
	   buildup]}]
  (html5
   [:html {:lang "en"}
    [:head
     [:meta {:charset "utf-8"}]
     [:meta {:name "description" :content conf/meta-description}]
     [:meta {:name "author" :content conf/author}]
     [:title (title-with title)]
     [:body
      [:div#main
       [:div#content buildup]]]]]))

(def login-form
  "Form for submitting username and password."
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

(def feed-selector
  "Area for selecting the feeds an article should appear in (checkboxes)."
  (html
   [:fieldset#feed-selectors
    [:legend "Include in the following feeds:"]
    (for [[label checked] conf/feeds]
      [:input#feed (into {:type "checkbox" :name label}
                         (when checked {:checked "checked"}))
       [:label label]])]))

(def article-form
  "Form for adding articles."
  (html
   [:h2 "Write Article"]
   [:table.form
    [:tr
     [:td [:label "Title"]]
     [:td [:input {:type "text" :name "title" :autofocus "autofocus" :required "required"}]]]
    [:tr
     [:td [:label "Slug"]]
     [:td [:input {:type "text" :name "slug" :required "required" :pattern "[a-zäöüß0-9_-]*"}]]]]
   feed-selector
   [:div {:id "slug" :class "article-body hyphenate admin-editable start-blank"} ""]
   [:input {:type "submit" :value "Add new article" :disabled "disabled"}]))