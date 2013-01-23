(ns tlog.render.html.parts.main
  "HTML fragments to be handed to skeleton as value of :main."
  (:require [hiccup.core :refer [html]]
            [hiccup.def :refer [defhtml]]
            [tlog.data.feed :as feed]
            [tlog.data.article]
            [tlog.render.html.parts.time :as time]))


;; Sub-fragments

(defhtml ^:privte feed-selector-part
  "One checkbox and label pair, to be used per feed via feed-selector."
  [[label checked]]
  [:input.feed-checkbox (merge {:type "checkbox" :name label}
                               (when checked {:checked "checked"}))]
  [:label label])

(defhtml ^:private feed-selector
  "Area for selecting the feeds an article should appear in (checkboxes)."
  [feed-pairs]
  [:fieldset.feed-selectors
   [:legend "Include in the following feeds:"]
   (mapcat feed-selector-part feed-pairs)])

(defn- title-linked
  "For articles appearing in a list: Wrap the title in a link to the article's page."
  [slug title]
  (html [:a.article-link {:id (str "title_" slug), :href (str "/" slug)} title]))

(defn title-plain
  "For an article appearing on its own page: Use a plain text title, as a link would lead to where
   we already are."
  [slug title]
  (html [:span {:id (str "title_" slug), :class "admin-editable"} title]))


;; Main fragments

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
   (feed-selector (feed/feed-defaults))
   [:div#article_text_area {:class "article-body hyphenate admin-editable start-blank"} ""]
   [:input#article_submit {:type "submit" :value "Add new article" :disabled "disabled"}]))

(defhtml ^:private article-generic
  "Render article content to be used once on single pages and several times in the journal.
   feed-selector is used as optional component, thus may be nil."
  [title-linked-or-plain
   feed-selector
   {:keys [slug title created_timestamp updated_timestamp content]}]
  (let [[timestamps css-class-updated?] (time/derive-from-timestamps slug
                                                                     created_timestamp
                                                                     updated_timestamp)]
    [:article {:data-slug slug}
     [:header
      [:h2 (title-linked-or-plain slug title)]
      timestamps
      feed-selector]
     [:div {:id (str "content_" slug)
            :class (str "article-body hyphenate admin-editable " css-class-updated?)} content]]))

(def article-solo
  "Render article content. Use article-generic specialized for one article on its own page"
  (partial article-generic title-plain nil))

(defn article-solo-admin
  "Render article content. Use article-generic specialized for one article on its own page, with
   feed-selector for admin."
  [article-map]
  (article-generic title-plain
                   (feed-selector (feed/feed-pairs-for-article (:slug article-map)))
                   article-map))