(ns tlog.data.comment
  "Storing and retrieving comments."
  (:refer-clojure :exclude [comment])
  (:require [korma.core :as k]
            [tlog.data.access :refer [db]]
            [tlog.data.time :refer [now]]
            [tlog.interface.validate :refer [->int]]))

db

(k/defentity comment
  (k/pk :number))

(defn- comments-for-parent
  "Take an article slug or comment number as string. Return a sequence of associated comments."
  [parent]
  (k/select comment
            (k/where {:parent parent})))

(defn- comments-with-level-for-parent
  "Take an article slug or comment number as string and a nesting level. Return a sequence of
   associated comments, with the level included in each comment map."
  [parent level]
  (map (partial into {:level level}) (comments-for-parent parent)))

(defn nested-comments
  "Retrieve all comments for the parent (article slug or comment number as string or integer), then
   recurse to retrieve comments for the parents of the just retrieved comments. Return a list of
   lists per thread, containing comment maps. Where each comment map includes the nesting level,
   counted from zero for comments referring directly to an article.

   Example with only the :number, :parent and :level keys, of 2 comments to an article 'first', with
   one sub-comment to the first comment:
   (({:number 1, :parent 'first' :level 0}
     ({:number 2, :parent '1' :level 1}))
    ({:number 3, :parent 'first' :level 1}))"
  ([parent level]
     ;; Map over the current level of comments, with a function that cons the current level to the
     ;; nested comments for the next level.
     ;; Primary keys for articles are strings, but for comments they are integers. comment's :parent
     ;; is a string to be able to reference articles. Using str converts integers and acts as
     ;; identity for strings.
     (map #(cons %1 (nested-comments (-> %1 :number str) (inc %2)))
          (comments-with-level-for-parent parent level)
          (repeat level)))
  ([parent]
     (nested-comments parent 0)))

(defn- comment-with-number
  "Take a comment number. Return the comment map, or nil if there is no comment with that
   number."
  [number]
  (not-empty (first (k/select comment
                              (k/where {:number number})))))

(defn comment-with-number-str
  "Take a comment number as string. Return the comment map, or nil, if the string can't be converted
   to an integer or if there is no comment with that number."
  [number-str]
  (if-let [n (->int number-str)]
    (comment-with-number n)))

(defn- level
  "Take a comment number as string. Return the count of members of the chain of parent
   comments that have parent comments. Thus the level for comments refering directly to an article
   is 0. Return nil, if there is no comment for the given number."
  [number-str]
  (loop [n number-str
         l -1]
    (if-let [c (comment-with-number-str n)]
      ;; There's a comment with that number, so recur with its parent:
      (recur (:parent c) (inc l))
      ;; The number-str either contains a number not associated with a comment, or it's an article
      ;; slug. Stop here and return the level reached. If it's still -1, the number-str did not
      ;; match a comment at all; return nil:
      (if (> l -1) l))))

(defn create!
  "Take strings for the comment data. Store a comment in the database. Return the comment map."
  [parent author email link content]
  (let [c (k/insert comment
                    (k/values {;; primary key 'number' is a serial, filled in by a sequence starting
                               ;; from 1 
                               :parent parent
                               :created_timestamp (now)
                               :updated_timestamp (now)
                               :author author
                               :email email
                               :link link
                               :content content}))]
    ;; Add :level to the comment map to be returned:
    (into c {:level (-> c :number str level)})))
