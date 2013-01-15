(ns tlog.dispatch.t-route
  (:require [midje.sweet :refer [fact tabular]]
            [tlog.dispatch.route :as r]
            [tlog.dispatch.handle :as h]
            [cemerick.friend :as friend]))

(defn with-redef-handler
  "Temporarily redef the given handler to identity. Get around authorization by temporarily redefing
   friend/wrap-authorize to a pass-through. Call routes with the given uri."
  [handler uri]
  (with-redefs-fn {#'friend/wrap-authorize (fn [handler roles] handler)
                   handler identity}
    (fn [] (r/routes {:request-method :get :uri uri}))))

(tabular "Routing calls the right handlers for all static GET cases."
  (fact 
     (with-redef-handler ?handler ?uri) => ?handler-and-params)
     ?handler      ?uri           ?handler-and-params
     #'h/journal   "/"            {:request-method :get
                                   :uri "/"}
     #'h/login     "/login"       {:request-method :get
                                   :uri "/login"}
     #'h/logout    "/logout"      {:session nil
                                   :request-method :get
                                   :uri "/logout"}
     #'h/admin     "/admin"       {:path-info "/"
                                   :request-method :get
                                   :uri "/admin"}
     #'h/write     "/admin/write" {:path-info "/write"
                                   :request-method :get
                                   :uri "/admin/write"}
     #'h/not-found "/admin/xrtgy" {:path-info "/xrtgy"
                                    :request-method :get
                                    :uri "/admin/xrtgy"}
     #'h/not-found "/xrtgy"       {:path-info "/xrtgy"
                                   :request-method :get
                                   :uri "/xrtgy"})