(ns tlog.render.html.parts.article
  "HTML parts to be handed to skeleton as value of :main."
  (:require [hiccup.core :refer [html]]
            [hiccup.def :refer [defhtml]]
            [tlog.data.feed :as feed]
            [tlog.data.article]
            [tlog.render.html.parts.comment :as comment]
            [tlog.render.html.parts.navigation :as navigation]
            [tlog.render.html.parts.time :as time]))


(defhtml ^:privte feed-selector-part
  "One checkbox and label pair, to be used per feed via feed-selector."
  [[label checked]]
  [:input.feed-checkbox (merge {:type "checkbox" :name label :disabled "disabled"}
                               (when checked {:checked "checked"}))]
  [:label label])

(defhtml feed-selector
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

(defhtml article-with-comments-generic
  "Take a function that renders an article, an article map and a nested map of comments. Render one
   article, along with comments."
  [article-fn article-map comments-map]
  (navigation/main-pages)
  (article-fn article-map)
  (comment/section comments-map))

;; article-form is in tlog.render.html.parts.main, as it is complete to be handed to skeleton as
;; value of :main
