(ns tlog.dispatch.route
  "Route requests to handlers (wrapped in middleware), based on URL patterns, methods and
   authorization."
  (:require [net.cgrand.moustache :refer [app]]
            [cemerick.friend :as friend]
            [ring.middleware.resource :refer [wrap-resource]]
            [ring.middleware.file-info :refer [wrap-file-info]]
            [ring.middleware.params :refer [wrap-params]]
            [clojure.data.json :as json]
            [tlog.data.resource]
            [tlog.dispatch.handle :as h]))


;; Utility

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


;; Routes

;; Remember to update tlog.render.html.script/static-slugs when changing slugs in static
;; routes like "login".

(def routes
  (app :get [(wrap-resource "/")
             wrap-file-info
             [] h/journal
             [[slug tlog.data.resource/slug->resource-or-nil]] (h/resource slug)
             ["login"] h/login
             ["logout"] (friend/logout h/logout)
             ["admin" &] (-> (app [] h/admin
                                  ["write"] h/write
                                  [&] h/not-found)
                             (friend/wrap-authorize [:tlog.data.account/admin]))
             [&] h/not-found]
       :put [[slug] (-> h/put-article
                        wrap-json
                        (friend/wrap-authorize [:tlog.data.account/admin]))]
       :post [[slug] (-> h/update-article
                         wrap-json
                         (friend/wrap-authorize [:tlog.data.account/admin]))]))