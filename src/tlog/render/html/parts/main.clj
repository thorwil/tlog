(ns tlog.render.html.parts.main
  "HTML parts to be handed to skeleton as value of :main."
  (:require [hiccup.core :refer [html]]
            [hiccup.def :refer [defhtml]]
            [tlog.data.feed :as feed]
            [tlog.render.html.parts.article :as a]
            [tlog.render.html.parts.navigation :as navigation]
            [tlog.render.html.parts.pagination :as p]))


(def login-form
  "Form for submitting username and password."
  (html
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

(def article-form
  "Form for adding articles."
  (html
   [:noscript
    [:div.noscript-warning "Writing articles won't work with JavaScript disabled."]]
   [:table.form
    [:tr
     [:td [:label "Title"]]
     [:td [:input#article_title_input {:type "text" :name "title" :autofocus "autofocus"
                                       :required "required"}]]]
    [:tr
     [:td [:label "Slug"]]
     [:td [:input#article_slug_input {:type "text" :name "slug" :required "required"
                                      :pattern "[a-zäöüß0-9_-]*"}]]]]
   (a/feed-selector (feed/feed-defaults))
   [:div#article_text_area {:class "article-body hyphenate admin-editable start-blank"} ""]
   [:input#article_submit {:type "submit" :value "Add new article" :disabled "disabled"}]))

(def article-with-comments
  "Specialize a/article-with-comments-generic for visitors."
  (partial a/article-with-comments-generic a/article-solo))

(def article-with-comments-admin
  "Specialize a/article-with-comments-generic for admin."
  (partial a/article-with-comments-generic a/article-solo-admin))

(defhtml journal
  "Take a seq of article maps, index numbers of articles appearing on the page due to pagination,
   the maximum number of articles that appear on a page and the total number of articles stored.
   Return inner HTML for a journal page."
  [articles from-to per-page total]
  (navigation/main-pages :journal)
  [:ul#journal (map #(conj [:li] (a/article-in-journal %)) articles)]
  (p/when-page-navigation from-to per-page total))
