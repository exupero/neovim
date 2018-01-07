(ns neovim.core
  (:refer-clojure :exclude [eval])
  (:require [clojure.string :as string]
            [clojure.walk :refer [prewalk-replace prewalk]]
            [neovim-client.nvim :as nvim]
            [neovim-client.1.api :as api]))

(defn client [& args]
  (apply nvim/new args))

(defn call? [x]
  (:nvim-call (meta x)))

(defn flatten-calls [calls]
  (let [f #(and (seq %) (not (call? %)))]
    (filter (complement f) (tree-seq f seq calls))))

(defn exec-atomic [client calls]
  (if-let [deps (seq (into #{}
                           (mapcat (fn [[_ args]]
                                     (filter call? args)))
                           calls))]
    (let [[values err] (exec-atomic client deps)]
      (if err
        [values err]
        (api/call-atomic client (prewalk-replace (zipmap deps values) calls))))
    (api/call-atomic client calls)))

(defn structure-results [calls results]
  (let [results (atom results)]
    (prewalk
      (fn [node]
        (if (call? node)
          (let [[x] @results]
            (swap! results rest)
            x)
          node))
      calls)))

(defn exec [client call-or-calls]
  (if (call? call-or-calls)
    (ffirst (exec-atomic client [call-or-calls]))
    (->> (flatten-calls call-or-calls)
      (exec-atomic client)
      first
      (structure-results call-or-calls))))

(defmacro defcall [nm args]
  `(defn ~nm ~args
     ^:nvim-call [~(str "nvim_" (string/replace (name nm) "-" "_")) ~args]))

(defcall command [command])
(defcall get-hl-by-name [name rgb])
(defcall get-hl-by-id [hl-id rgb])
(defcall feedkeys [keys mode escape-csi])
(defcall input [keys])
(defcall replace-termcodes [str from-part do-lt special])
(defcall command-output [str])
(defcall eval [expr])
(defcall call-function [fname args])
(defcall execute-lua [code args])
(defcall strwidth [text])
(defcall list-runtime-paths [])
(defcall set-current-dir [dir])
(defcall get-current-line [])
(defcall set-current-line [line])
(defcall del-current-line [])
(defcall get-var [name])
(defcall set-var [name value])
(defcall del-var [name])
(defcall get-vvar [name])
(defcall get-option [name])
(defcall set-option [name value])
(defcall out-write [str])
(defcall err-write [str])
(defcall err-writeln [str])
(defcall list-bufs [])
(defcall get-current-buf [])
(defcall set-current-buf [buffer])
(defcall list-wins [])
(defcall get-current-win [])
(defcall set-current-win [window])
(defcall list-tabpages [])
(defcall get-current-tabpage [])
(defcall set-current-tabpage [tabpage])
(defcall subscribe [event])
(defcall unsubscribe [event])
(defcall get-color-by-name [name])
(defcall get-color-map [])
(defcall get-mode [])
(defcall get-keymap [mode])
(defcall get-api-info [])
(defcall call-atomic [calls])
(defcall parse-expression [expr flags highlight])
(defcall buf-line-count [buffer])
(defcall buf-get-lines [buffer start end strict-indexing])
(defcall buf-set-lines [buffer start end strict-indexing replacement])
(defcall buf-get-var [buffer name])
(defcall buf-get-changedtick [buffer])
(defcall buf-get-keymap [buffer mode])
(defcall buf-set-var [buffer name value])
(defcall buf-del-var [buffer name])
(defcall buf-get-option [buffer name])
(defcall buf-set-option [buffer name value])
(defcall buf-get-name [buffer])
(defcall buf-set-name [buffer name])
(defcall buf-is-valid [buffer])
(defcall buf-get-mark [buffer name])
(defcall buf-add-highlight [buffer src-id hl-group line col-start col-end])
(defcall buf-clear-highlight [buffer src-id line-start line-end])
(defcall win-get-buf [window])
(defcall win-get-cursor [window])
(defcall win-set-cursor [window pos])
(defcall win-get-height [window])
(defcall win-set-height [window height])
(defcall win-get-width [window])
(defcall win-set-width [window width])
(defcall win-get-var [window name])
(defcall win-set-var [window name value])
(defcall win-del-var [window name])
(defcall win-get-option [window name])
(defcall win-set-option [window name value])
(defcall win-get-position [window])
(defcall win-get-tabpage [window])
(defcall win-get-number [window])
(defcall win-is-valid [window])
(defcall tabpage-list-wins [tabpage])
(defcall tabpage-get-var [tabpage name])
(defcall tabpage-set-var [tabpage name value])
(defcall tabpage-del-var [tabpage name])
(defcall tabpage-get-win [tabpage])
(defcall tabpage-get-number [tabpage])
(defcall tabpage-is-valid [tabpage])
(defcall ui-attach [width height options])
(defcall ui-detach [])
(defcall ui-try-resize [width height])
(defcall ui-set-option [name value])
