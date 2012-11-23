(ns tlog.data.account
  "For now just retrieving user credentials from the database."
  (:require [clojure.java.jdbc :as d]
            [tlog.data.access :refer [db]]))

(def ^:private accounts-raw
  "Query db for a map of accounts."
  (d/with-connection db
    (d/with-query-results as ["SELECT * FROM account;"]
      (into [] as))))

(defn- add-role
  "Take a single account map. Return the map with roles added: 'admin' if the username is 'admin', 'guest' otherwise.
   Motivation: keep roles out of the database, as long as 'admin' is the only account of interest."
  [a]
  (into a {:roles (if (= (:username a) "admin")
                    #{::admin}
                    #{::guest})}))

(defn- convert-accounts
  "Take a map of accounts as delivered from the database. Return a Friend-friendly map of accounts."
  [as]
  (apply hash-map (interleave (map :username as)
                              (map add-role as))))

(def accounts
  "Return a Friend-friendly map of accounts."
  (convert-accounts accounts-raw))

;; Example Friend accounts map:
;; {"admin" {:username "admin"
;;           :password (cemerick.friend.credentials/hash-bcrypt "password")
;;           :roles #{::admin}}
;;  "jane" {:username "jane"
;;          :password (cemerick.friend.credentials/hash-bcrypt "plain")
;;          :roles #{::guest}}}
