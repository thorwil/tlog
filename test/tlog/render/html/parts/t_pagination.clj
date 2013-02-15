(ns tlog.render.html.parts.t-pagination
  (:require [midje.sweet :refer [tabular fact]]
            [tlog.render.html.parts.pagination :as p]))


(tabular "headwards-tailwards works as intended."
 (fact (#'p/headwards-tailwards ?from-to ?per-page ?total) => ?r)
 ?from-to ?per-page ?total ?r
  [1 1]   1          1     [nil nil]
  [4 1]   2          4     [nil nil]
  [4 2]   2          6     [[1 1] [6 5]]
  [12 7]  5         12     [[6 2] nil])
