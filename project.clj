(defproject tlog "0.1.0-SNAPSHOT"
  :description "A blog."
  :url "https://github.com/thorwil/tlog"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]

                 ;; Not needed when using "lein immutant run", but for "lein midje":
                 [org.immutant/immutant "0.9.0" :exclusions [org.clojure/java.jdbc]]
                 
                 [ring/ring-core "1.1.6"]
		 [net.cgrand/moustache "1.1.0" :exclusions [org.clojure/clojure ring/ring-core]]
                 [korma "0.3.0-beta9"]
                 [postgresql/postgresql "9.1-901-1.jdbc4"]
                 [com.cemerick/friend "0.1.3"]
                 [hiccup "1.0.1"]
                 [org.clojure/data.json "0.2.0"]
                 [org.clojure/tools.nrepl "0.2.0-beta10"]]
  :profiles {:dev {:dependencies [[midje "1.5.0" :exclusions [org.clojure/core.incubator]]]
                   :plugins [[lein-midje "3.0.0"]]}}
  :immutant {:nrepl-port 4242
             :context-path "/"})

;; About exclusions:
;; [net.cgrand/moustache "1.1.0"] would like [ring/ring-core "1.2.0-20120922.214238-3"],
;;   but I prefer to stay with [ring/ring-core "1.1.6"].
;; [net.cgrand/moustache "1.1.0"] would like [org.clojure/clojure "1.5.0-beta1"],
;;   but I prefer [org.clojure/clojure "1.5.0-RC15"].
;; [midje "1.4.0"] would bring in [org.clojure/core.incubator "0.1.0"],
;;   but [com.cemerick/friend "0.1.3"] asks for [org.clojure/core.incubator "0.1.1"]
;;   and shall have it.
;; [org.immutant/immutant "0.7.0"]  would like [org.clojure/java.jdbc "0.2.3"],
;;   but I prefer to let [korma "0.3.0-beta9"] have [org.clojure/java.jdbc "0.1.0"].
;;   The other way around leads to an FileNotFoundException, anyway.
