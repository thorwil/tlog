(ns tlog.render.html.parts.main
  "HTML parts to be handed to skeleton as value of :main."
  (:require [hiccup.core :refer [html]]
            [hiccup.def :refer [defhtml]]
            [tlog.data.feed :as feed]
            [tlog.data.article]
            [tlog.render.html.parts.time :as time]
            [tlog.render.html.parts.pagination :as p]))


;; Sub-parts

(defhtml ^:privte feed-selector-part
  "One checkbox and label pair, to be used per feed via feed-selector."
  [[label checked]]
  [:input.feed-checkbox (merge {:type "checkbox" :name label :disabled "disabled"}
                               (when checked {:checked "checked"}))]
  [:label label])

(defhtml ^:private feed-selector
  "Area for selecting the feeds an article should appear in (checkboxes)."
  [feed-pairs]
  [:fieldset.feed-selectors
   [:noscript
    [:div.noscript-warning "Changing feed memberships won't work with JavaScript disabled."]]
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
  (html [:span {:id (str "title_" slug), :class "admin-editable title"} title]))


;; Main parts

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
   [:h1 "Write Article"]
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
   (feed-selector (feed/feed-defaults))
   [:div#article_text_area {:class "article-body hyphenate admin-editable start-blank"} ""]
   [:input#article_submit {:type "submit" :value "Add new article" :disabled "disabled"}]))

(defhtml ^:private article-generic
  "Render article content to be used once on single pages and several times in the journal.
   feed-selector is used as optional component, thus may be nil."
  [title-linked-or-plain
   feed-selector
   noscript-for-admin
   {:keys [slug title created_timestamp updated_timestamp content]}]
  (let [[timestamps css-class-updated?] (time/derive-from-timestamps slug
                                                                     created_timestamp
                                                                     updated_timestamp)]
    [:article {:data-slug slug}
     [:header
      [:h2 (title-linked-or-plain slug title)]
      timestamps
      feed-selector]
     noscript-for-admin
     [:div {:id (str "content_" slug)
            :class (str "article-body hyphenate admin-editable " css-class-updated?)} content]]))

(def article-solo
  "Render article content. Use article-generic specialized for one article on its own page"
  (partial article-generic title-plain nil nil))

(defn article-solo-admin
  "Render article content. Use article-generic specialized for one article on its own page, with
   feed-selector for admin."
  [article-map]
  (article-generic title-plain
                   (feed-selector (feed/feed-pairs-for-article (:slug article-map)))
                   (html [:noscript [:div.noscript-warning
                                     "Editing articles won't work with JavaScript disabled."]])
                   article-map))

(def article-in-journal
  "Render article content. Use article-generic specialized for an article appearing among other in
   the journal."
  (partial article-generic title-linked nil nil))

(defhtml journal
  "Take a seq of article maps, index numbers of articles appearing on the page due to pagination,
   the maximum number of articles that appear on a page and the total number of articles stored.
   Return inner HTML for a journal page."
  [articles from-to per-page total]
  [:ul#journal (map #(conj [:li] (article-in-journal %)) articles)]
  (p/when-page-navigation from-to per-page total))
