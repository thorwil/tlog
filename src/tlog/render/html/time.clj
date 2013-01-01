(ns tlog.render.html.time
  (:require [hiccup.core :refer [html]]
            [hiccup.def :refer [defhtml]]))

(defn- timestamp-to*
  "Create a function that expects a java.sql.Timestamp, to convert it to a date/time string."
  [format*]
  (let [format (java.text.SimpleDateFormat. format*)]
    (.setTimeZone format (java.util.TimeZone/getTimeZone "GMT"))
    #(.format format %)))

(def ^:private timestamp-to-day-time (timestamp-to*
                                      "yyyy-MM-dd '<span class=\"hour-minute\">'H:mm'</span>'"))

(def ^:private timestamp-to-datetime (timestamp-to* "yyyy-MM-dd'T'H:mm:ss'+00:00'"))

(def ^:private timestamp-to-rfc-3339 (timestamp-to* "yyyy-MM-dd'T'HH:mm:ssZ")) ;; For Atom feeds

(defn- timestamp-to-ms
  [t]
  (.getTime t))

(defhtml ^:private time*
  [t attr-map]
  [:time (into {:datetime (timestamp-to-datetime t)} attr-map) (timestamp-to-day-time t)])

(defhtml ^:private time-created
  [t]
  [:p (time* t {:pubdate "pubdate" :class "time-created" :id (timestamp-to-ms t)})])

(defhtml ^:private time-updated
  [t]
  [:p "Updated:" [:br] (time* t {:class "time-updated" :id (timestamp-to-ms t)})])

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