(ns tlog.render.page
  "Take data from tlog.dispatch.handle and build HTML responses, using tlog.render.html.*."
  (:require [tlog.render.html.skeleton :refer [skeleton]]
            [tlog.render.html.main :as main]
            [tlog.render.html.admin :as option]
            [tlog.render.html.script :as script]))


;; Utility

(defn- first-or-into
  "Take a condition and 2 expressions. If the condition is met, return the expressions combined via
   into. Otherwise return the first expression."
  [cond a b]
  (if cond
    (into a b)
    a))

(defn- per-role
  "Take roles and a map of maps per role. Add the map of every role present in roles into the
   :everyone map."
  ;; Currently roles can only be one of nil or #{:tlog.data.account/admin}, so the following is good
  ;; enough:
  [roles {:keys [everyone admin] :as all}]
  (let [additions+replacements (first-or-into (= roles #{:tlog.data.account/admin})
                                              everyone
                                              admin)
        scripts-joined {:scripts (apply str (map :scripts (vals all)))}]
    (into additions+replacements scripts-joined)))


;; Handlers

(def journal
  (skeleton {:title "Journal"
             :main "Journal"}))

(def login
  (skeleton {:title "Login"
             :main main/login-form}))

(def admin
  (skeleton {:title "Admin"
             :main "Admin"}))

(def write
  (skeleton (reduce into
                    [{:title "Write"
                      :scripts script/aloha-admin
                      :main main/article-form}
                     option/noscript-warning
                     (option/admin-bar :write)])))

(defn article
  "Take roles (for now, can only be nil or #{:tlog.data.account/admin}) and an article map. Return a
   HTML page representing the article."
  [roles art]
  (skeleton (per-role roles {:everyone {:title (:title art)
                                        :scripts script/client-time-offset
                                        :main (main/article-solo art)}
                             :admin {:scripts script/aloha-admin}})))

(def not-found
  (skeleton {:title "404"
             :main "404: There's nothing associated with this URL."}))