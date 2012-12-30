(ns tlog.render.html.fragment-main
  "HTML fragments for :main of skeleton."
  (:require [hiccup.core :refer [html]]
            [tlog.data.feed]
            [tlog.data.article]))

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
       [:input {:type "text" :name "username" :value "" :autofocus "autofocus"}]]]
     [:tr
      [:td
       [:label "Password"]]
      [:td
       [:input {:type "password" :name "password" :value ""}]]]]
    [:input {:type "submit" :value "submit"}]]))

(def ^:private feed-selector
  "Area for selecting the feeds an article should appear in (checkboxes)."
  (html
   [:fieldset#feed-selectors
    [:legend "Include in the following feeds:"]
    (for [[label checked] tlog.data.feed/feeds]
      [:input.feed (into {:type "checkbox" :name label}
                         (when checked {:checked "checked"}))
       [:label label]])]))

(def article-form
  "Form for adding articles."
  (html
   [:h2 "Write Article"]
   [:table.form
    [:tr
     [:td [:label "Title"]]
     [:td [:input#article_title_input {:type "text" :name "title" :autofocus "autofocus"
                               :required "required"}]]]
    [:tr
     [:td [:label "Slug"]]
     [:td [:input#article_slug_input {:type "text" :name "slug" :required "required"
                              :pattern "[a-zäöüß0-9_-]*"}]]]]
   feed-selector
   [:div#article_text_area {:class "article-body hyphenate admin-editable start-blank"} ""]
   [:input#article_submit {:type "submit" :value "Add new article" :disabled "disabled"}]))