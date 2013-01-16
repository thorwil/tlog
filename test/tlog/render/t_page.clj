(ns tlog.render.t-page
  (:require [midje.sweet :refer [fact tabular]]
            [tlog.render.page :as p]))

(tabular
 "concat-per-key"
 (fact (#'p/concat-per-key ?m ?ms) => ?concatenated)
 ?m                     ?ms                           ?concatenated
 {}                     []                            {}
 {:a "a"}               []                            {}
 {}                     [{:a "a"}]                    {:a "a"}
 {:a "a"}               [{:a "2"} {:b "1"}]           {:a "a2" :b "1"}
 {:a "a" :b "b" :c "c"} [{:a "2"} {:a "3"} {:b "2"}]  {:a "a23" :b "b2"})

(tabular
 "filter-for-role-then-split"
 (fact (#'p/filter-for-role-then-split ?roles ?role-map-pairs) => ?merged+to-append)
 ?roles        ?role-map-pairs                                                            ?merged+to-append
 [:r1]         '((:r2 {:a "a" :b "b"}))                                                   [{} []]
 [:r1]         '((:r1 {:a "a" :b "b"}) (:r2 {:append {:b "2"}}))                          [{:a "a" :b "b"} []]
 [:r1 :r2]     '((:r1 {:a "a" :b "b"}) (:r2 {:append {:b "2"}}))                          [{:a "a" :b "b"} [{:b "2"}]]
 [:r1 :r2 :r3] '((:r1 {:a "a" :b "b"}) (:r2 {:append {:b "2"}}) (:r3 {:append {:b "3"}})) [{:a "a" :b "b"} [{:b "2"} {:b "3"}]])

(fact "select-by-role-merge with no role and no map for :everyone results in empty map."
  (#'p/select-by-role-merge #{} :r1 {:a "a"}) => {})

(fact "select-by-role-merge given nil still looks out for :everyone."
  (#'p/select-by-role-merge nil :everyone {:a "a"}) => {:a "a"})

(fact "select-by-role-merge replaces values for the same keys in earlier maps, but appends what's in
       :append."
  (#'p/select-by-role-merge #{:r1 :r2} :r1 {:a "a" :b "b"} :r2 {:a "a2" :append {:b "2"}})
   => {:a "a2" :b "b2"})