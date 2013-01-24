(ns tlog.render.html.parts.time
  "HTML parts for timestamps. Used in tlog.render.html.parts.main."
  (:require [hiccup.core :refer [html]]
            [hiccup.def :refer [defhtml]]))

(defn- timestamp-to*
  "Create a function that expects a java.sql.Timestamp, to convert it to a date/time string."
  [format*]
  (let [format (java.text.SimpleDateFormat. format*)]
    (.setTimeZone format (java.util.TimeZone/getTimeZone "GMT"))
    #(.format format %)))

(def ^:private timestamp-to-day-time
  "Date/time format for HTML output. Timestamps are converted from UTC to local time via time.js,
   which has to match this format."
  (timestamp-to* "yyyy-MM-dd '<span class=\"hour-minute\">'H:mm'</span>'"))

(def ^:private timestamp-to-datetime
  "Date/time format for <time> datetime attributes."
  (timestamp-to* "yyyy-MM-dd'T'H:mm:ss'+00:00'"))

(def ^:private timestamp-to-rfc-3339
  "Date/time format for Atom feeds."
  (timestamp-to* "yyyy-MM-dd'T'HH:mm:ssZ"))

(defn- timestamp-to-ms
  [t]
  (.getTime t))

(defhtml ^:private time*
  [t attr-map]
  [:time (into {:datetime (timestamp-to-datetime t)} attr-map) (timestamp-to-day-time t)])

(defhtml ^:private time-created
  "HTML <p> for the created_timestamp."
  [slug t]
  [:p (time* t {:pubdate "pubdate"
                :id (str "time-created_" slug)
                :class "time-created"
                :data-time-created (timestamp-to-ms t)})])

(defhtml time-updated
  "HTML <p> for the updated_timestamp. Also called when updating an article, to pass replacement
   HTML to the page."
  [slug t]
  [:p "Updated:" [:br] (time* t {:id (str "time-updated_" slug)
                                 :class "time-updated"
                                 :data-time-updated (timestamp-to-ms t)})])

(defn derive-from-timestamps
  "Take 2 timestamps for creation and last update. Derive everything that depends on whether there
   has been an update. Return timestamps HTML and a CSS class name of either 'not-updated' or
   'updated'."
  [slug created_timestamp updated_timestamp]
  (let [[maybe-updated css-class] (if (= created_timestamp
                                         updated_timestamp)
				    [nil "not-updated"]
				    [(time-updated slug updated_timestamp) "updated"])]
    [(html
      [:div.times {:id (str "times_" slug)}
       (time-created slug created_timestamp)
       maybe-updated])
     css-class]))