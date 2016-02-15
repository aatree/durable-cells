(ns durable-cells.dc-api
  (:require-macros
    [aaworker.worker-macros :refer [deflapc!]])
  (:require
    [aaworker.api :as api]))

(def db (atom nil))

(deflapc! load-cell [cell-name]
          (let [request (-> @db
                            (.transaction "cells")
                            (.objectStore "cells")
                            (.get cell-name))]
            (set! (.-onerror request) failure)
            (set! (.-onsuccess request)
                  (fn [_]
                    (let [result (.-result request)]
                      (success result))))))

(deflapc! save-cell [cell-name value]
          (let [request (-> @db
                            (.transaction "cells" "readwrite")
                            (.objectStore "cells")
                            (.put value cell-name))]
            (set! (.-onerror request) failure)
            (set! (.-onsuccess request)
                  (fn [_]
                    (success (.-result request))))))

(defn start []
  (set! cljs.core/*print-fn* #(.log js/console %))
  (let [databaseName "durable-cells"
        indexedDB (.-indexedDB js/self)
        request (.open indexedDB databaseName)]
    (set! (.-onupgradeneeded request)
          (fn [event]
            (let [db (-> event .-target .-result)
                  object-store (.createObjectStore db "cells")])))
    (set! (.-onerror request)
          (fn [event]
            (aaworker.api/send-notice
              :alert
              (str "Unable to open indexedDB "
                   databaseName
                   (-> event .-target .-errorCode)))))
    (set! (.-onsuccess request)
          (fn [event]
            (reset! db (-> event .-target .-result))
            (api/process-requests)))))