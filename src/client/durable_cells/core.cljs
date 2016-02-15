(ns durable-cells.core
  (:require-macros
    [javelin.core :refer [defc]])
  (:require [aaworker.lpc :refer [new-worker! register-notice-processor! mklocal!]]
            [javelin.core :refer [cell]]))

(def worker-file "dcells.js")

(defc error nil)
(defc loading nil)

(defn open-durable-cells! [f]
  (new-worker! worker-file)
  (register-notice-processor! worker-file :ready f))
