(ns tlog.render.html.assemble
  "Take data from tlog.interface.receive and build HTML responses, using tlog.render.html.*."
  (:require [tlog.render.configuration :refer [title-main]]
            [tlog.render.html.skeleton :refer [skeleton]]
            [tlog.render.html.parts.main :as main]
            [tlog.render.html.parts.navigation :as navigation]
            [tlog.render.html.parts.script :as script]))


;; Utility

(defn- first-or-into
  "Take a condition and 2 expressions. If the condition is met, return the expressions combined via
   into. Otherwise return the first expression."
  [cond a b]
  (if cond
    (into a b)
    a))

(defn- concat-per-key
  "Take a map and a sequence of maps. Return a map with keys from the maps in the sequence, each
   with a value that is all values associated to the key in the input maps concatenated.

   Reduce over the sequence of maps, using a selection from the single map with only the keys
   present in the sequence as seed. Within, reduce over the keys in use, using the selection as
   seed."
  [m ms]
  (let [keys-used (set (mapcat keys ms))
        overlap (select-keys m keys-used)]
    (reduce (fn [m from-ms] (reduce (fn [m k] (assoc m k (str (k m) (k from-ms))))
                                    m
                                    keys-used))
            overlap
            ms)))

(defn- filter-for-role-then-split
  "Take a set of roles and a vector of vectors, each consisting of a role key and a map. Return a
   vector with 2 items: All maps belonging to keys found in roles merged. The values associated to
   :append of said maps in a vector."
  [roles role-map-pairs]
  (let [merged+to-append (reduce (fn [[merged to-append] [k v]]
                                   (if (some #{k} roles)
                                     [(merge merged v) (if-let [a (:append v)]
                                                         (conj to-append a)
                                                         to-append)]
                                     [merged to-append]))
                                 [{} []]
                                 role-map-pairs)
        [merged to-append] merged+to-append]
    [(dissoc merged :append) to-append]))

(defn- select-by-role-merge
  "Take a set of roles and any pairs of role-key and per-role-map. Merge the map of every role
   present in roles in given order: Replace values for identical keys, but concatenate when given
   per-role-maps with maps in :append.

   Currently roles will only be one of nil or #{:tlog.data.account/admin}."
  [roles & per-role]
  (let [roles+everyone (into [:everyone] roles)
        role-map-pairs (partition 2 per-role)
        [merged to-append] (filter-for-role-then-split roles+everyone role-map-pairs)]
    (merge merged (concat-per-key merged to-append))))


;; Pages

(defn journal
  "Take a seq of article maps, index numbers of articles appearing on the page due to pagination,
   the maximum number of articles that appear on a page and the total number of articles stored.
   Return complete HTML for a journal page."
  [articles from-to per-page total]
  (skeleton {:title "Journal"
             :headline title-main
             :main (main/journal articles from-to per-page total)}))

(def login
  (skeleton {:title "Login"
             :main main/login-form}))

(def admin
  (skeleton (merge {:title "Admin"
                    :main "Admin"}
                   (navigation/admin-bar :list))))

(def write
  (skeleton (merge {:title "Write"
                    :scripts script/aloha-admin-create
                    :main main/article-form}
                   (navigation/admin-bar :write))))

(defn article
  "Take roles (for now, can only be nil or #{:tlog.data.account/admin}) and an article map. Return a
   HTML page representing the article."
  [roles article-map]
  (skeleton (select-by-role-merge roles
                                  :everyone {:title (:title article-map)
                                             :headline title-main
                                             :scripts script/client-time-offset
                                             :main (main/article-solo article-map)}
                                  :tlog.data.account/admin
                                  (merge {:main (main/article-solo-admin article-map)
                                          :append {:scripts (str script/aloha-admin-edit
                                                                 script/feed-selection)}}))))

(def not-found
  (skeleton {:title "404"
             :main "404: There's nothing associated with this URL."}))
