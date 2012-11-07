(defproject tlog "0.1.0-SNAPSHOT"
  :description "A blog."
  ; :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.4.0"]
		 [net.cgrand/moustache "1.1.0"]
                 [org.clojure/java.jdbc "0.2.3"]
                 [postgresql/postgresql "9.1-901-1.jdbc4"]
                 [org.clojure/tools.nrepl "0.2.0-beta10"]]
  :immutant {:nrepl-port 4242
             :context-path "/"})