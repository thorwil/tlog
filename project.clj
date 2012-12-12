(defproject tlog "0.1.0-SNAPSHOT"
  :description "A blog."
  :url "https://github.com/thorwil/tlog"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [org.immutant/immutant "0.7.0"] ;; Not needed when using "lein immutant run",
                                                 ;; but for "lein midje".
                 [ring/ring-core "1.1.6"]
		 [net.cgrand/moustache "1.1.0" :exclusions [org.clojure/clojure ring/ring-core]]
                 [org.clojure/java.jdbc "0.2.3"]
                 [postgresql/postgresql "9.1-901-1.jdbc4"]
                 [com.cemerick/friend "0.1.2"]
                 [hiccup "1.0.1"]
                 [org.clojure/data.json "0.2.0"]
                 [org.clojure/tools.nrepl "0.2.0-beta10"]]
  :profiles {:dev {:dependencies [[midje "1.4.0" :exclusions [org.clojure/core.incubator]]]
                   :plugins [[lein-midje "2.0.1"]]}}
  :immutant {:nrepl-port 4242
             :context-path "/"})

;; About exclusions:
;; [net.cgrand/moustache "1.1.0"] would like [ring/ring-core "1.2.0-20120922.214238-3"],
;;   but I prefer to stay with [ring/ring-core "1.1.6"].
;; [net.cgrand/moustache "1.1.0"] would like [org.clojure/clojure "1.5.0-beta1"],
;;   but I prefer to stay with [org.clojure/clojure "1.4.0"].
;; [midje "1.4.0"] would bring in [org.clojure/core.incubator "0.1.0"],
;;   but [com.cemerick/friend "0.1.2"] asks for [org.clojure/core.incubator "0.1.1"].

