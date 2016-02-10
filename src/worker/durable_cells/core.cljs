(ns durable-cells.core
  (:require
    [aaworker.api :as api]))

(def db (atom nil))

(defn load-cell [success failure cell-name]
  (let [request (-> @db
                    (.transaction "cells")
                    (.objectStore "cells")
                    (.get cell-name))]
    (set! (.-onerror request) failure)
    (set! (.-onsuccess request)
          (fn [_]
            (let [result (.-result request)]
              (if result
                (success (.-first result))
                (success nil)))))))

(defn save-cell [success failure cell-name value]
  (let [request (-> @db
                    (.transaction "cells" "readwrite")
                    (.objectStore "cells")
                    (.put value cell-name))]
    (set! (.-onerror request) failure)
    (set! (.-onsuccess request)
          (fn [_]
            (success (.-result request))))))

(defn start [databaseName]
  (let [indexedDB (.-indexedDB js/self)
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