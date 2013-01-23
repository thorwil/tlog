(ns tlog.interface.t-respond
  (:require [midje.sweet :refer [fact]]
            [tlog.interface.respond :as r]))

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
  (#'r/roles session-nobody) => nil)

(fact "roles-in-resource works with a request map like we get when admin is logged in."
  (#'r/roles session-admin) => #{:tlog.data.account/admin})