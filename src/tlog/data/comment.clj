(ns tlog.data.comment
  "Storing and retrieving comments."
  (:refer-clojure :exclude [comment])
  (:require [korma.core :as k]
            [tlog.data.access :refer [db]]
            [tlog.data.time :refer [now]]
            [tlog.interface.validate :refer [->int >zero]]))

db

(k/defentity comment
  (k/pk :number))

(defn create!
  [parent author email link content]
  (k/insert comment
            (k/values {;; primary key 'number' is a serial, filled in by a sequence starting from 1 
                       :parent parent
                       :created_timestamp (now)
                       :updated_timestamp (now)
                       :author author
                       :email email
                       :link link
                       :content content})))

(defn- comments-for-parent
  "Take an article slug (string). Return a sequence of comments directly associated via the slug."
  [parent]
  (k/select comment
            (k/where {:parent parent})))

(defn nested-comments
  "Retrieve all comments for the parent, then recurse to retrieve comments for the parents of the
   just retrieved comments. Returns a list of lists per thread. Example with only the :number and
   :parent keys, of 2 comments to an article 'first', with one sub-comment to the first comment:
   (({:number 1, :parent 'first'} ({:number 2, :parent '1'})) ({:number 3, :parent 'first'}))"
  [parent]
  ;; primary keys for articles are strings, but for comments they are integers. comment's :parent is
  ;; a string to be able to reference articles. Using str converts integers and acts as identity for
  ;; strings.
  (map #(cons % (-> % :number str nested-comments)) (comments-for-parent parent)))

(defn- comment-with-number
  "Take a comment number. Return the comment map, or nil if there is no comment with that
   number."
  [number]
  (not-empty (first (k/select comment
                              (k/where {:number number})))))

(defn- comment-with-number-str
  "Take a comment number as string. Return the comment map, or nil, if the string can't be converted
   to an integer or if there is no comment with that number."
  [number-str]
  (if-let [n (->int number-str)]
    (comment-with-number n)))

(defn level
  "Take a comment number as string. Return the count of members of the successive chain of parent
   comments with an article as parent are level 1. Return nil, if there is no comment for the given
   number."
  [number-str]
  (loop [n number-str
         l 0]
    (if-let [c (comment-with-number-str n)]
      ;; There's a comment with that number, so recur with its parent:
      (recur (:parent c) (inc l))
      ;; The number-str either contains a number not associated with a comment, or it's an article
      ;; slug. Stop here and return the level reached. If it's still zero, the number-str did not
      ;; match a comment at all; return nil:
      (>zero l))))
