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
            [tlog.dispatch.route :refer [root-routes]]
            [tlog.data.account :refer [accounts]]))

(def users
  {"admin" {:username "admin"
            :password (creds/hash-bcrypt "test")
            :roles #{::admin}}
   "jane" {:username "jane"
           :password (creds/hash-bcrypt "plain")
           :roles #{::guest}}})

(def secured-app
  (-> #'root-routes
      (friend/authenticate {:credential-fn (partial creds/bcrypt-credential-fn accounts)
                            :workflows [(workflows/interactive-form)]})
      wrap-keyword-params
      ;; wrap-nested-params
      wrap-params
      (wrap-session {:store (immutant-session/servlet-store)})))

(web/start secured-app :reload true)