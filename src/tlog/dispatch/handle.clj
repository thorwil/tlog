(ns tlog.dispatch.handle
  "Take requests from routing. Build responses, usually by calling views with query results from the
   model."
  (:require [ring.util.response :refer [response redirect]]
            [net.cgrand.moustache :refer [alter-response]]
            [tlog.render.render :as v]))

(defn journal
  [r]
  {:body "Journal"})

(defn login
  [r]
  v/login)

(defn logout [r] (redirect "/"))

(defn admin
  [r]
  v/admin)

(defn article-form
  [r]
  {:body (str r)})

;; Example request map for /admin/wrote:
;; {:ssl-client-cert nil
;;  :remote-addr "127.0.0.1"
;;  :scheme :http
;;  :query-params {}
;;  :session {:cemerick.friend/identity {:current "admin"
;;                                       :authentications {"admin" {:identity "admin"
;;                                                                  :username "admin"
;;                                                                  :roles #{:tlog.data.account/admin}}}}}
;;  :cemerick.friend/auth-config {:allow-anon? true
;;                                :default-landing-uri "/"
;;                                :login-uri "/login"
;;                                :credential-fn #<core$partial$fn__4070 clojure.core$partial$fn__4070@215f0267>
;;                                :workflows [#<workflows$interactive_form$fn__1463 cemerick.friend.workflows$interactive_form$fn__1463@677a5379>]
;;                                :unauthorized-handler #'cemerick.friend/default-unauthorized-handler}
;;  :context ""
;;  :form-params {}
;;  :request-method :get
;;  :query-string nil
;;  :content-type nil
;;  :cookies {"ring-session" {:value "VAl3Vg4lWvvGoVp23Laofg4q"}
;;            "JSESSIONID" {:value "VAl3Vg4lWvvGoVp23Laofg4q"}}
;;  :path-info "/write"
;;  :uri "/admin/write"
;;  :server-name "localhost"
;;  :params {}
;;  :headers {"cache-control" "max-age=0"
;;            "cookie" "JSESSIONID=VAl3Vg4lWvvGoVp23Laofg4q; ring-session=VAl3Vg4lWvvGoVp23Laofg4q"
;;            "connection" "keep-alive"
;;            "dnt" "1"
;;            "accept-encoding" "gzip, deflate"
;;            "accept-language" "en-US,en;q=0.5"
;;            "accept" "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8"
;;            "user-agent" "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:17.0) Gecko/17.0 Firefox/17.0"
;;            "host" "localhost:8080"}
;;  :content-length nil
;;  :server-port 8080
;;  :character-encoding nil
;;  :body #<CoyoteInputStream org.apache.catalina.connector.CoyoteInputStream@ffe6202>}

(defn not-found
  []
  (-> "404: There's nothing associated with this URL."
      response
      constantly
      (alter-response #(assoc % :status 404))))