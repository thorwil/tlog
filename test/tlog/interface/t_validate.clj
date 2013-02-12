(ns tlog.interface.t-validate
  (:require [midje.sweet :refer [fact tabular]]
            [tlog.interface.validate :as v]))

(tabular "->int converts integers as strings into integers, leaves integers unchanged and answers
          nil for everything else."
  (fact (v/->int ?s) => ?r)
                 ?s     ?r
                 "a"   nil
                 true  nil
                 false nil
                 nil   nil
                 "1.1" nil
                 "1"   1
                 "42"  42
                 7     7)

(tabular ">zero only validates numbers > 0."
  (fact (v/>zero ?n) => ?r)
                 ?n     ?r
                 -1     nil
                 -2.4   nil
                 0      nil
                 1      1
                 3.4    3.4)

(tabular "->pair validates strings that can be split into 2 parts before and after '-'."
  (fact (v/->pair ?s) => ?r)
                  ?s     ?r
                  "a"    nil
                  "a-"   nil
                  "a-b"  ["a" "b"]
                  "a-b-" nil
                  "1-2"  ["1" "2"])

(tabular "int-gt-zero-pair validates strings that can be split into 2 parts before and after '-',
          the parts convertible to integers > 0."
  (fact (v/int-gt-zero-pair ?s) => ?r)
                            ?s      ?r
                            "a"     nil
                            "a-"    nil
                            "a-b"   nil
                            "a-b-"  nil
                            "a-1"   nil
                            "1-a"   nil
                            "1-2"   [1 2]
                            "1.1-1" nil
                            "1-1.1" nil)
