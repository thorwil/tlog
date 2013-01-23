(ns tlog.render.html.parts.t-option-macros
  (:require [midje.sweet :refer [fact]]
            [tlog.render.html.parts.option-macros :as o]))

(o/defopt opt-1 (str "s")) ;; Wrap in str, as plain string would be mistaken for a docstring.
(o/defopt opt-2 (str "s") :alt)
(o/defopt-fn opt-fn-1 [x] x)
(o/defopt-fn opt-fn-2 [x] x :alt)

(fact "Macros for defining optional HTML fragments work."
  opt-1 => {:opt-1 "s"}
  opt-2 => {:alt "s"}
  (opt-fn-1 "s") => {:opt-fn-1 "s"}
  (opt-fn-2 "s" ) => {:alt "s"})