(ns durable-cells.core
  (:require-macros
    [javelin.core :refer [defc]])
  (:require [aaworker.lpc :refer [new-worker! register-notice-processor! mklocal!]]
            [javelin.core :refer [cell]]))

(def worker-file "dcells.js")

(defc error nil)
(defc loading nil)
(defc load-counter 0)
(defc save-counter 0)
(defc ready false)

(defn process-cell [entry]
  (swap! load-counter dec)
  (let [cell-str (entry 0)
        c (entry 1)
        cell-key (keyword cell-str)
        db-cell-str (str "db*" cell-str)
        db-cell (cell (symbol db-cell-str))
        db-cell-key (keyword db-cell-str)
        db-save (cell (symbol (str "db**" cell-str)))
        load-key (keyword (str "load-" cell-str))
        save-key (keyword (str "save-" cell-str))
        load
        (mklocal!
          "load-cell"
          worker-file
          db-cell
          error
          loading
          load-counter
          load-key)
        save
        (mklocal!
          "save-cell"
          worker-file
          db-save
          error
          loading
          save-counter
          save-key)]
    (add-watch db-cell db-cell-key
               (fn [_ _ _ n]
                 (reset! c n)))
    (add-watch c cell-key
               (fn [_ _ _ n]
                 (when (not= n @db-cell)
                   (save cell-str n))))
    (load cell-str)))

(defn process-cells [entries]
  (let [entry (first entries)]
    (when entry
      (process-cell (first entries))
      (recur (rest entries)))))

(defn open-durable-cells! [cell-dic]
  (new-worker! worker-file)
  (register-notice-processor! worker-file :ready
    (fn []
      (process-cells (seq cell-dic))
      (if (= 0 @load-counter)
        (reset! ready true)
        (add-watch load-counter :load-counter
                   (fn [_ _ _ v]
                     (if (= 0 v)
                       (reset! ready true))))))))
