(ns tlog.data.comment
  "Storing and retrieving comments."
  (:refer-clojure :exclude [comment])
  (:require [korma.core :as k]
            [tlog.data.access :refer [db]]
            [tlog.data.time :refer [now]]))

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
