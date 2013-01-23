(ns tlog.interface.t-receive
  (:require [midje.sweet :refer [fact tabular]]
            [tlog.interface.receive :refer [routes]]
            [tlog.interface.respond :as r]
            [cemerick.friend :as friend]))

(defn with-redef-handler
  "Temporarily redef the given handler to identity. Get around authorization by temporarily redefing
   friend/wrap-authorize to a pass-through. Call routes with the given uri."
  [handler uri]
  (with-redefs-fn {#'friend/wrap-authorize (fn [handler roles] handler)
                   handler identity}
    (fn [] (routes {:request-method :get :uri uri}))))

(tabular "Routing calls the right handlers for all static GET cases."
  (fact 
     (with-redef-handler ?handler ?uri) => ?handler-and-params)
     ?handler      ?uri           ?handler-and-params
     #'r/journal   "/"            {:request-method :get
                                   :uri "/"}
     #'r/login     "/login"       {:request-method :get
                                   :uri "/login"}
     #'r/logout    "/logout"      {:session nil
                                   :request-method :get
                                   :uri "/logout"}
     #'r/admin     "/admin"       {:path-info "/"
                                   :request-method :get
                                   :uri "/admin"}
     #'r/write     "/admin/write" {:path-info "/write"
                                   :request-method :get
                                   :uri "/admin/write"}
     #'r/not-found "/admin/xrtgy" {:path-info "/xrtgy"
                                    :request-method :get
                                    :uri "/admin/xrtgy"}
     #'r/not-found "/xrtgy"       {:path-info "/xrtgy"
                                   :request-method :get
                                   :uri "/xrtgy"})