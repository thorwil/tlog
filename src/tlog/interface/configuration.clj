(ns tlog.interface.configuration)

(def articles-per-journal-page 5)

(def max-comment-level
  "Maximum nesting level for comments. No Reply fields for comments reaching it and no PUTS with
   sub-comments for them accepted."
  8)
