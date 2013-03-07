(ns tlog.data.time
  "Utility functions dealing with time.")

(defn now
  "Get the current time as java.sql.Timestamp."
  []
  (java.sql.Timestamp. (.getTime (java.util.Date.))))
