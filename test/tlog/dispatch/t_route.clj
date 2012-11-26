(ns tlog.dispatch.t-route
  (:require [midje.sweet :refer [fact tabular]]
            [tlog.dispatch.route :as r]
            [tlog.dispatch.handle :as h]))

(defn test-handler
  "Return a pseudo-handler that returns a vector of the replaced handler var as string and the
   request parameters."
  [handler]
  (fn [r]
    [(str handler) r]))

(defn with-redef-handler
  [handler uri]
  ;; Found no way to use with-redefs instead, so the body must be wrapped in a fn:
  (with-redefs-fn {handler (test-handler handler)} ;; (fn [r] r) worked fine
    (fn [] (r/root-routes {:request-method :get :uri uri}))))

(with-redef-handler #'h/journal "/")

(tabular "Routing calls the right handlers with the right parameters."
  (fact 
     (with-redef-handler ?handler ?uri) => ?handler-and-params)
     ?handler   ?uri     ?handler-and-params
     #'h/journal "/"      ["#'tlog.dispatch.handle/journal" {:path-info "/" :request-method :get
                                                            :uri "/"}]
     #'h/login  "/login"  ["#'tlog.dispatch.handle/login" {:path-info "/login" :request-method :get
                                                         :uri "/login"}]
     #'r/logout "/logout" ["#'tlog.dispatch.route/logout" {:path-info "/logout" :request-method :get
                                                           :uri "/logout"}]
     #'r/admin-protected  "/admin"  ["#'tlog.dispatch.route/admin-protected" {:path-info "/"
                                                                              :request-method :get
                                                                              :uri "/admin"}])