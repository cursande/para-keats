(defproject para-keats "0.1.0-SNAPSHOT"
  :description "John Keats mashup - adapted from http://howistart.org/posts/clojure/1/index.html"
  :url "https://github.com/cursande/para-keats"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [clj-http "3.9.1"]
                 [cheshire "5.8.0"]]
  :profiles {:dev
             {:plugins [[com.jakemccrary/lein-test-refresh "0.7.0"]]}})
