(ns tlog.render.html.parts.comment
  "HTML parts for navigation. To be handed to skeleton as values of keys other than :main."
  (:require [hiccup.core :refer [html]]
            [hiccup.def :refer [defhtml]]
            [tlog.data.resource :refer [valid-article-slug]]
            [tlog.interface.configuration :refer [max-comment-level]]
            [tlog.render.html.parts.time :as t]))


(defhtml ^:private comment-field
  "Take a parent comment number. Render an Aloha editable to be placed at every end in the comment
   tree. Wrapped in another div that will collect the fields and button that may be inserted below
   the initial field."
  [parent]
  [:div.comment-form
   [:div {:class "hyphenate editable start-blank"
          :onmouseover (str "configureField('" parent "', this);")}
    [:span.internal-label "Reply"]]])

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

(defn- comment-field-if-not-max-level
  "Take a level (integer) and comment number (string). Return a comment-field, if the level does not
   exceed max-comment-level, otherwise nil."
  [level number]
  (if (> max-comment-level (inc level))
           (comment-field number)))

(defhtml ^:private thread
  "Take either a thread list or a single comment-map. Render a comment thread of threads, or a
   single comment thread."
  [thread-or-comment]
  (if (map? thread-or-comment)
    ;; Argument is a single comment map:
    (comment thread-or-comment)
    ;; Argument is a thread (nested sequence):
    [:div.thread
     (mapcat thread thread-or-comment)
     (let [c (first thread-or-comment)]
       (if-let [s (-> c first :parent)]
         ;; Comment field for an article:
         (comment-field s)
         ;; Comment field to add a sub-comment, unless the nesting level reached the maximum:
         (comment-field-if-not-max-level (-> c :level inc)
                                       (:number c))))]))

(defhtml new-thread-async
  "Render a thread with a single comment. Used for sending HTML representing a just added comment to
   the client asynchronously."
  [comment-map]
  ;;(thread [parameter-map])
  [:div.thread
   (comment comment-map)
   ;; Comment field to add a sub-comment, unless the nesting level reached the maximum:
   (comment-field-if-not-max-level (-> comment-map :level Integer. inc)
                                   (:number comment-map))])

(defhtml section
  "Render a comment section."
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
