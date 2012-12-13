(ns tlog.render.configuration)

;; Used in html.clj:

(def title-seperator " <- ")

(def meta-description "Interaction and visual design by Thorsten Wilms.")
(def font-link "http://fonts.googleapis.com/css?family=Lato:light,regular,regularitalic,bold,900")


;; Used in html.clj, to be used in atom-feed.clj:

(def title-main "Thorsten Wilms Design Solutions")
(def author "Thorsten Wilms")


;; To be used in html.clj and atom-feed.clj:

(def copyright "Copyright 2011 Thorsten Wilms, unless otherwise noted.")
(def feed-url "http://www.thorstenwilms.com/atom/journal")


;; To be used in atom-feed.clj:

(def articles-per-feed-page 10)
(def domain "http://www.thorstenwilms.com/")
(def author-email "self@thorstenwilms.com")
