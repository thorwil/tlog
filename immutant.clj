(ns tlog.init
  "Start Immutant services."
  (:require [immutant.web :as web]
            [immutant.web.session :as immutant-session]
            [cemerick.friend :as friend]
            (cemerick.friend [workflows :as workflows]
                             [credentials :as creds])
            [ring.middleware.session :refer [wrap-session]]
            [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.keyword-params :refer [wrap-keyword-params]]
            ;; [ring.middleware.nested-params :refer [wrap-nested-params]]
            [tlog.route.route :refer [root-routes]]))

; A dummy in-memory user "database"
(def users {"admin" {:username "admin"
                     :password (creds/hash-bcrypt "test")
                     :roles #{::admin}}
            "jane" {:username "jane"
                    :password (creds/hash-bcrypt "plain")
                    :roles #{::user}}})

(def secured-app
  (-> #'root-routes
      (friend/authenticate {:credential-fn (partial creds/bcrypt-credential-fn users)
                            :workflows [(workflows/interactive-form)]})
      wrap-keyword-params
      ;; wrap-nested-params
      wrap-params
      (wrap-session {:store (immutant-session/servlet-store)})))

(web/start secured-app :reload true)
