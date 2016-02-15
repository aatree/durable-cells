(ns durable-cells.client-macros
  (:require [javelin.core :refer [defc]]))

(defmacro defdcell! [cell-sym initial-value]
  (let [cell-str (str `~cell-sym)
        cell-key (keyword cell-str)
        db-cell-sym (symbol (str "db*" cell-str))
        db-cell-key (keyword db-cell-sym)
        db-save-sym (symbol (str "db**" cell-str))
        load-sym (symbol (str "load-" cell-str))
        load-key (keyword (str "load-" cell-str))
        save-sym (symbol (str "save-" cell-str))
        save-key (keyword (str "save-" cell-str))]
    `(do
       (defc ~cell-sym ~initial-value)
       (let [~db-cell-sym (javelin.core/cell nil)
             ~load-sym
             (aaworker.lpc/mklocal!
               "load-cell"
               durable-cells.core/worker-file
               ~db-cell-sym
               durable-cells.core/error
               durable-cells.core/loading
               ~load-key)
             ~db-save-sym (javelin.core/cell nil)
             ~save-sym
             (aaworker.lpc/mklocal!
               "save-cell"
               durable-cells.core/worker-file
               ~db-save-sym
               durable-cells.core/error
               durable-cells.core/loading
               ~save-key)]
         (add-watch ~db-cell-sym ~db-cell-key
                    (fn [~'_ ~'_ ~'_ ~'n]
                      (reset! ~cell-sym ~'n)))
         (add-watch ~cell-sym ~cell-key
                    (fn [~'_ ~'_ ~'_ ~'n]
                      (when (not= ~'n @~db-cell-sym)
                        (~save-sym ~cell-str ~'n))))
         (~load-sym ~cell-str)))))
