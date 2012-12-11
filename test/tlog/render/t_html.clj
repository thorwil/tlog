(ns tlog.render.t-html
  (:require [midje.sweet :refer [fact]]
            [tlog.render.html :as r]))

(r/defopt opt-1 (str "s")) ;; Wrap in str, as plain string would be mistaken for a docstring.
(r/defopt opt-2 (str "s") :alt)
(r/defopt-fn opt-fn-1 [x] x)
(r/defopt-fn opt-fn-2 [x] x :alt)

(fact "Macros for defining optional HTML fragments work."
  opt-1 => {:opt-1 "s"}
  opt-2 => {:alt "s"}
  (opt-fn-1 "s") => {:opt-fn-1 "s"}
  (opt-fn-2 "s" ) => {:alt "s"})