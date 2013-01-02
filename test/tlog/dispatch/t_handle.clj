(ns tlog.dispatch.t-handle
  (:require [midje.sweet :refer [fact]]
            [tlog.dispatch.handle :as h]))

(def session-nobody
  "Relevant part of a request map passed through Friend, if no one is logged in."
  {:session {}})

(def session-admin
  "Relevant part of a request map passed through Friend, if admin is logged in."
  {:session {:cemerick.friend/identity
             {:current "admin,"
              :authentications {"admin" {:identity "admin,"
                                       :username "admin,"
                                       :roles #{:tlog.data.account/admin}}}}}})

(fact "roles-in-resource works with a request map like we get when there no one logged in."
  (#'h/roles session-nobody) => nil)

(fact "roles-in-resource works with a request map like we get when admin is logged in."
  (#'h/roles session-admin) => #{:tlog.data.account/admin})