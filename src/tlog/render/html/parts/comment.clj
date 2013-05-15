(ns tlog.render.html.parts.comment
  "HTML parts for navigation. To be handed to skeleton as values of keys other than :main."
  (:require [hiccup.core :refer [html]]
            [hiccup.def :refer [defhtml]]
            [tlog.render.html.parts.time :as t]
            [tlog.data.comment :refer [level]]
            [tlog.interface.configuration :refer [max-comment-level]]))


(defhtml ^:private comment-field
  "Take a parent comment number (as ID). Render an Aloha editable to be placed at every end in the
   comment tree. Optionally take a second argument with the nesting level, to return nil to have
   nothing rendered, once a maximum level is reached. Wrapped in another div that will collect the
   fields and button that may be inserted below the initial field."
  ([parent]
     [:div.comment-form
      [:div {:class "hyphenate editable start-blank"
             :onmouseover (str "configureField('" parent "', this);")}
       [:span.internal-label "Reply"]]])
  ([parent level]
     (if (> max-comment-level level)
       (comment-field parent))))

;; The expanded comment forms are created client-side, via comment.js

(defn- linked-or-plain
  [link text]
  (if (empty? link)
    text
    [:a {:href link} text]))

(defn- comment
  [{:keys [author content created_timestamp updated_timestamp link
           number option-comments-admin-editable]}]
  (let [[timestamps css-class] (t/derive-from-timestamps number
                                                         created_timestamp
                                                         updated_timestamp)]
    [:div {:data-id number, :class (str "comment " css-class)}
     timestamps
     [:p.meta
      [:a.comment-anchor {:id (str "comment-anchor-for_" number)
                          :name number
                          :href (str "#" number)} (str "#" number " ")]
      [:span {:id (str "comment-author-for_" number)
              :class (str "author " option-comments-admin-editable)}
       (linked-or-plain link author) ": "]]
     [:div {:id (str "comment-content_" number)
            :class (str "content " option-comments-admin-editable)} content]]))

(defhtml ^:private thread-
  [thread-or-comment]
  (if (map? thread-or-comment)
    (comment thread-or-comment)
    [:div.thread
     (mapcat thread thread-or-comment)
     (let [c (first thread-or-comment)]
       (apply comment-field (or
                             ;; Comment field for the article, the slug is the value for :slug in
                             ;; the first map in the first list. Since it's already clear this is
                             ;; the first level, no second argument is given to comment-field:
                             (if-let [s (-> c first :parent)] ;; the result in [] for apply to work,
                               [s])                           ;; or a naked nil for the or
                             ;; Comment field to add a sub-comment. A second argument with the
                             ;; nesting level will be used, to leave out comment-fields once a
                             ;; maximum level is reached:
                             [(:number c) (-> c :number level)])))]))

(defhtml ^:private thread
  [thread-or-comment]
  (if (map? thread-or-comment)
    (comment thread-or-comment)
    [:div.thread
     (mapcat thread thread-or-comment)
     (let [c (first thread-or-comment)]
       (if-let [s (-> c first :parent)]
         ;; Comment field for the article, the slug is the value for :slug in the first map in the
         ;; first list. Since it's already clear this is the first level, no second argument is
         ;; given to comment-field:
         (comment-field s)
         ;; Comment field to add a sub-comment. A second argument with the nesting level will be
         ;; used, to leave out comment-fields once a maximum level is reached:
         (let [n (:number c)]
           (comment-field n (level n)))))]))

(defn new-thread-async
  "Render a thread with a single comment. Used for sending HTML representing a just added comment to
   the client asynchronously."
  [parameter-map]
  (thread [parameter-map]))

(defhtml section
  [article-slug comments-map]
  [:div#comments
   [:h3 "Comments"]
   [:noscript [:p "Without JavaScript, you cannot add comments, here!"]]
   ;; The outer div.thread must be marked with a css class 'empty', if there are no comments. Since
   ;; thread recurses, the test has to happen here, to be done only once:
   (if (empty? comments-map)
     [:div {:class "thread empty"}
      (comment-field article-slug)]
     (thread comments-map))])
