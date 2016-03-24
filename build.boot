(set-env!
  :dependencies '[[org.clojure/clojure                       "1.8.0"  :scope "provided"]
                  [org.clojure/clojurescript                 "1.8.34" :scope "provided"]
                  [adzerk/bootlaces                          "0.1.13" :scope "test"]
                  [adzerk/boot-cljs                          "1.7.228-1" :scope "test"]
                  [hoplon/hoplon                             "6.0.0-alpha13"]
                  [aatree/aaworker                           "0.1.3"]]
  :resource-paths #{"src/client" "src/worker"}
)

(require
  '[adzerk.boot-cljs            :refer [cljs]]
  '[adzerk.bootlaces            :refer :all])

(def +version+ "0.1.3")

(bootlaces! +version+ :dont-modify-paths? true)

(task-options!
  pom {:project     'aatree/durable-cells
       :version     +version+
       :description "Local Storage for Hoplon."
       :url         "https://github.com/aatree/durable-cells"
       :scm         {:url "https://github.com/aatree/durable-cells"}
       :license     {"EPL" "http://www.eclipse.org/legal/epl-v10.html"}})

(deftask dev
  "Build project for development."
  []
  (comp
    (cljs :optimizations :simple)
    (build-jar)
    (target)))

(deftask deploy-release
 "Build for release."
 []
 (comp
   (build-jar)
   (push-release)))
