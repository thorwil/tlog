(ns tlog.core
  (:use [net.cgrand.moustache :only [app]]))

(def root-routes (app [&] "Hello World!"))