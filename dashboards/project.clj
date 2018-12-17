(defproject sunjeetsonboardingroot-dashboards "1.0"
  :license {:name "Netflix Proprietary"
            :url "http://netflix.com"}
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [netflix/hyperion "1.9"]
                 ]
  :source-paths ["src/main/clojure"]
  :repositories [["releases"  "https://artifacts.netflix.com/nfrepo-releases-pom"]]
  :main hyperion.main
)
