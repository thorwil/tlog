(ns tlog.route.handle
  (:require [tlog.model.model :as m]))


(defn journal
  []
  (fn [r] {:body (m/test-query)}))