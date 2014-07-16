(defproject Thirty-Three "0.1.0-SNAPSHOT"
  :description "yet another 2048 clone"
  :url "https://github.com/zackmdavis/Thirty-Three"
  :license {:name "MIT License"
            :url "http://opensource.org/licenses/MIT"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                ;[org.clojure/math.combinatorics "0.0.7"] <= needed fn inlined in
                 [jline "0.9.94"]                        ; combinatorics-library
                 [om "0.5.0"]                            ; because reasons
                 [org.clojure/clojurescript "0.0-2156"]]
  :plugins [[lein-cljsbuild "1.0.2"]
            [com.jakemccrary/lein-test-refresh "0.5.0"]]
  :cljsbuild { 
    :builds [{:id "thirty-three"
              :source-paths ["src"]
              :compiler {
                :output-to "thirty-three.js"
                :optimizations :none
                :source-map true}}]})
