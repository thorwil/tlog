(ns tlog.render.html.time
  (:require [hiccup.core :refer [html]]
            [hiccup.def :refer [defhtml]]))

(defn- timestamp-to*
  "Create a function that expects a java.sql.Timestamp, to convert it to a date/time string."
  [format*]
  (let [format (java.text.SimpleDateFormat. format*)]
    (.setTimeZone format (java.util.TimeZone/getTimeZone "GMT"))
    #(.format format %)))

(def ^:private ms-to-day (ms-to* "yyyy-MM-dd"))
(def ^:private ms-to-day-time (ms-to* "yyyy-MM-dd '<span class=\"hour-minute\">'H:mm'</span>'"))
(def ^:private ms-to-datetime (ms-to* "yyyy-MM-dd'T'H:mm:ss'+00:00'"))
(def ^:private ms-to-rfc-3339 (ms-to* "yyyy-MM-dd'T'HH:mm:ssZ")) ;; as required for Atom feeds

(defhtml ^:private time*
  [t attr-map]
  [:time (into {:datetime (ms-to-datetime t)} attr-map) (ms-to-day-time t)])

(defhtml ^:private time-created
  [t]
  [:p (time* t {:pubdate "pubdate" :class "time-created" :id t})])

(defhtml ^:private time-updated
  [t]
  [:p "Updated:" [:br] (time* t {:class "time-updated" :id t})])

(defn derive-from-timestamps
  "Take 2 timestamps for creation and last update. Derive everything that depends on whether there
   has been an update. Return timestamps HTML and a CSS class name of either 'not-updated' or
   'updated'."
  [created_timestamp updated_timestamp]
  (let [[maybe-updated css-class] (if (= created_timestamp
                                         updated_timestamp)
				    [nil "not-updated"]
				    [(time-updated updated_timestamp) "updated"])]
    [(html
      [:div.times
       (time-created created_timestamp)
       maybe-updated])
     css-class]))

;; Timestamps are converted from UTC to local time via time.js, which has to rebuild the HTML