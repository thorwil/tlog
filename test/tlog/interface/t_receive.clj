(ns tlog.interface.t-receive
  (:require [midje.sweet :refer [fact tabular]]
            [tlog.interface.receive :refer [routes]]
            [tlog.interface.respond :as r]
            [cemerick.friend :as friend]))

(defn with-redef-handler
  "Temporarily redef the given handler to replacement. Get around authorization by temporarily
   redefing friend/wrap-authorize to a pass-through. Call routes with the given uri."
  [replacement handler uri]
  (with-redefs-fn {#'friend/wrap-authorize (fn [handler roles] handler)
                   handler replacement}
    (fn [] (routes {:request-method :get :uri uri}))))

(def with-redef-handler-1
  "Specialize with-redef-handler for handler functions that take the request immediately."
  (partial with-redef-handler identity))

(def with-redef-handler-2
  "Specialize with-redef-handler for functions that return a request handler."
  (partial with-redef-handler (fn [arg] (fn [request] (into request {:arg arg})))))

(tabular "Routing calls the right handlers for all static GET cases."
  (fact 
     (with-redef-handler-1 ?handler ?uri) => ?method-and-uri)
     ?handler             ?uri           ?method-and-uri
     #'r/journal-default  "/"            {:request-method :get
                                          :uri "/"}
     #'r/login            "/login"       {:request-method :get
                                          :uri "/login"}
     #'r/logout           "/logout"      {:session nil
                                          :request-method :get
                                          :uri "/logout"}
     #'r/admin            "/admin"       {:path-info "/"
                                          :request-method :get
                                          :uri "/admin"}
     #'r/write            "/admin/write" {:path-info "/write"
                                          :request-method :get
                                          :uri "/admin/write"}
     #'r/not-found        "/admin/xrtgy" {:path-info "/xrtgy"
                                          :request-method :get
                                          :uri "/admin/xrtgy"}
     #'r/not-found        "/xrtgy"       {:path-info "/xrtgy"
                                          :request-method :get
                                          :uri "/xrtgy"})

(tabular "Routing calls the right handlers with the right arguments derived from URI segments."
  (fact
    (with-redef-handler-2 ?handler ?uri) => ?method-uri-arg)
    ?handler    ?uri   ?method-uri-arg
    #'r/journal "/1-1" {:request-method :get, :uri "/1-1", :arg [1 1]})
