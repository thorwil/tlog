(ns tlog.render.html.parts.pagination
  "HTML parts for navigating numbered pages."
  (:require [hiccup.def :refer [defhtml]]))


;; Utility:

(defn- headwards-tailwards
  "Take current range of indices as vector of 'from' and 'to', number of items per page, total
   number of items. Return vector of 'headwards' and 'tailwards'. 'headwards' will either be a
   vector with index numbers 'from' and 'to', or nil, if there is page in headward direction.
   Likewise for 'tailwards'. Assumes newer-to-older = tail-to-head, higher-to-lower number."
  [[from to] per-page total]
  (let [headwards (if (<= to 1)
		    nil
		    (let [from* (dec to)
			  to* (max (- from* (dec per-page)) 1)]
		      [from* to*]))
	tailwards (if (>= from total)
		    nil
		    (let [to* (inc from)
			  from* (min (+ to* (dec per-page)) total)]
		      [from* to*]))]
    [headwards tailwards]))


;; Parts:

(defn- pair-to-range-str
  [[a b]]
  (str a "-" b))

(defhtml ^:private page-navigation
  "Render page navigation."
  [[headwards tailwards] url-base]
  [:nav#pages
   (if tailwards
     [:a {:href (str url-base (pair-to-range-str tailwards))} "Newer"]
     [:span "Newer"])
   (if headwards
     [:a {:href (str url-base (pair-to-range-str headwards))} "Older"]
     [:span "Older"])])

(defn when-page-navigation
  "Insert page navigation, only if there are other pages."
  [from-to per-page total]
  (let [[headwards tailwards] (headwards-tailwards from-to per-page total)]
    (when (or headwards tailwards)
      (page-navigation [headwards tailwards] "/"))))
