(ns tlog.interface.receive
  "Route requests to handlers (wrapped in middleware), based on URL patterns, methods and
   authorization."
  (:require [net.cgrand.moustache :refer [app]]
            [cemerick.friend :as friend]
            [ring.middleware.resource :refer [wrap-resource]]
            [ring.middleware.file-info :refer [wrap-file-info]]
            [ring.middleware.params :refer [wrap-params]]
            [clojure.data.json :as json]
            [tlog.interface.respond :as r]
            [tlog.interface.validate :as v]
            [tlog.data.resource :as rsc]))


;; Utility

(defn- decode-json-body
  "Take a ring request. Replace a JSON string in the :body of the request with a deserialization
   (or nil if it's not an input stream and/or not JSON)"
  [req]
  (update-in req [:body] #(try (-> % slurp (json/read-str :key-fn keyword))
                             (catch Exception e nil))))

(defn- wrap-json
  "Take a handler and wrap it in a fn that decodes JSON and applies the handler, or returns a Bad
   Request status, if decoding fails"
  [handler]
  (fn [request]
    (let [req (decode-json-body request)]
      (if (:body req)
        (handler req)
        {:status 400 ;; Status 400: Bad Request
         :headers {"Content-Type" "text/plain"}
         :body "Failure"}))))


;; Routes

;; Remember to update tlog.render.html.parts.script/static-slugs when changing slugs in static
;; routes like "login".

(def routes
  (app :get [(wrap-resource "/")
             wrap-file-info
             [] r/journal-default
             [[from-to v/dash-separated-integer-pair]] (r/journal from-to)
             ["login"] r/login
             ["logout"] (friend/logout r/logout)
             ["admin" &] (-> (app [] r/admin
                                  ["write"] r/write
                                  [&] r/not-found)
                             (friend/wrap-authorize [:tlog.data.account/admin]))
             [[slug rsc/slug->resource-or-nil]] (r/resource slug)
             [&] r/not-found]
       :put [[[slug rsc/valid-slug-for-article] "comment"] (wrap-json r/put-comment)
             [slug] (-> r/put-article
                        wrap-json
                        (friend/wrap-authorize [:tlog.data.account/admin]))]
       :post [[slug] (-> r/update-article*
                         wrap-json
                         (friend/wrap-authorize [:tlog.data.account/admin]))]))
