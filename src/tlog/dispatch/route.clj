(ns tlog.dispatch.route
  "Route requests to handlers (wrapped in middleware), based on URL patterns, methods and
   authorization."
  (:require [net.cgrand.moustache :refer [app]]
            [cemerick.friend :as friend]
            [ring.middleware.resource :refer [wrap-resource]]
            [ring.middleware.file-info :refer [wrap-file-info]]
            [ring.middleware.params :refer [wrap-params]]
            [clojure.data.json :as json]
            [tlog.dispatch.handle :as h]))

(defn- decode-json-body
  "Take a ring request. Replace a JSON string in the :body of the request with a deserialization
   (or nil if it's not an input stream and/or not JSON)"
  [r]
  (update-in r [:body] #(try (-> % slurp (json/read-str :key-fn keyword))
                             (catch Exception e nil))))

(defn- wrap-json
  "Take a handler and wrap it in a fn that decodes JSON and applies the handler, or returns a Bad
   Request status, if decoding fails"
  [handler]
  (fn [request]
    (let [r (decode-json-body request)]
      (if (:body r)
        (handler r)
        {:status 400 ;; Status 400: Bad Request
         :headers {"Content-Type" "text/plain"}
         :body "Failure"}))))

(defmacro ^:private defroutes
  "def name to a moustache app form."
  [name & more]
  `(def ~name (app ~@more)))

;; This would be nicer written inline as [friend/logout h/logout], but there needs to be a var for
;; the test to redef it:
(defn logout
  [r]
  ((friend/logout h/logout) r))

(defroutes get-routes
  [] h/journal
  ["login"] h/login
  ["logout"] logout
  [&] h/not-found)

(defn static
  [r]
  ((-> get-routes
       (wrap-resource "/")
       wrap-file-info)
   r))

(defroutes admin-post-routes
  [] h/admin
  ["write"] h/write
  [&] h/not-found)

;; See comment for logout
(defn admin-protected
  [r]
  ((friend/wrap-authorize admin-post-routes [:tlog.data.account/admin]) r))

;; See comment for logout
(defn put-article
  [r]
  ((-> h/put-article
       wrap-json
       (friend/wrap-authorize [:tlog.data.account/admin])
       )
   r))

(defroutes root-routes
  ["admin" &] {:get admin-protected}
  [&] {:get static
       :put put-article})

(def static-slugs
  "Vector of static slugs. Used in tlog.render.html, to rule out attempts of creating new articles
   with any slug in use."
  ["logout" "login" "journal" "admin"])