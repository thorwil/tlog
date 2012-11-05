(defproject tlog "0.1.0-SNAPSHOT"
  :description "A blog."
  ; :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.4.0"]
		 [net.cgrand/moustache "1.1.0"]
                 [org.clojure/tools.nrepl "0.2.0-beta10"]]
  :immutant {:nrepl-port 4242
             :context-path "/"})