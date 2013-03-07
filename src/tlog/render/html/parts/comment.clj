(ns tlog.render.html.parts.comment
  "HTML parts for navigation. To be handed to skeleton as values of keys other than :main."
  (:require [hiccup.core :refer [html]]
            [hiccup.def :refer [defhtml]]
            [tlog.render.html.parts.time :as t]))


(defhtml comment-field
  "Takes a parent comment ID and the number of comments that follow on the same level. Renders
   Aloha editable to be placed at every end in the comment tree. Wrapped in another div that will
   collect the fields and button that may be inserted below the initial field."
  [parent]
  [:div.comment-form
   [:div {:class "hyphenate editable start-blank"
	  :onmouseover (str "configureField('" parent "', this);")}
    [:span.internal-label "Reply"]]])

(defn linked-or-plain
  [link text]
  (if (empty? link)
    text
    [:a {:href link} text]))

(defn- comment
  [{:keys [author content created_timestamp updated_timestamp delete-queued link
           number option-comments-admin-editable] :as foo}
   ;;switch-comment-deleter
   ]
  [:div {:class (str "thread" (when delete-queued " delete-queued"))}
   (let [[timestamps css-class] (t/derive-from-timestamps number
                                                          created_timestamp
                                                          updated_timestamp)]
     [:div {:id number, :class (str "comment " css-class)}
      timestamps
      [:p.meta
       [:a.comment-anchor {:id (str "comment-anchor-for_" number)
                           :name number
                           :href (str "#" number)} (str "#" number " ")]
       [:span {:id (str "comment-author-for_" number)
               :class (str "author " option-comments-admin-editable)}
        (linked-or-plain link author) ": "]
       ;;(switch-comment-deleter number delete-queued)
       ]
      [:div {:id (str "comment-content_" number)
             :class (str "content " option-comments-admin-editable)} content]])])

(defhtml thread
  [comment-or-thread]
  (if (map? comment-or-thread)
    (comment comment-or-thread)
    [:div.thread
     (mapcat thread comment-or-thread)
     (let [c (first comment-or-thread)]
       (comment-field (or ;; Comment field for the article, the slug is the value for :slug in the
                          ;; first map in the first list:
                          (-> comment-or-thread first first :parent)
                          ;; Comment field to add a sub-comment:
                          (-> comment-or-thread first :number))))]))

(defhtml section
  [comments-map]
  [:div#comments
   [:h3 "Comments"]
   [:noscript [:p "Without JavaScript, you cannot add comments, here!"]]
   [:div {:class (when (empty? comments-map) "empty")}
    (thread comments-map)]])
