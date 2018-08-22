(ns para-keats.generator
  (:require [clj-http.client :as client]
            [cheshire.core :refer [parse-string]]
            [clojure.string :refer [split-lines replace-first blank?]]))

(defn match-last-word [line]
  (let [match (re-find #"(\w+)(?:\W+$|$)" line)]
    (last match)))

(defn text->last-words [text]
  (let [lines (into [] (remove (fn [s] (blank? s))
                               (split-lines text)))]
    (map (fn [s] (match-last-word s))
         lines)))

(defn fetch-rhymes [last-words]
  (let [base-url "https://api.datamuse.com/words?rel_rhy="]
    (map (fn [word]
           (as-> [word] res
             (:body (client/get (str base-url word) {:as :json}))
             (if (empty? res)
               word
               (:word (rand-nth res)))))
         last-words)))

(defn gen-word-regex [word]
  (re-pattern (str word "(\\W+\\n|\\W+$)|" word "(\\n|$)")))

(defn word-swap [text]
  (let [last-words (text->last-words text)
        match-replace-pairs (map (fn [a, b] [(gen-word-regex a),(str b "$1$2")])
                                 last-words
                                 (fetch-rhymes last-words))]
    (reduce (fn [s, m-r] (apply replace-first s m-r))
            text
            match-replace-pairs)))

(defn -main [arg]
  (let [new-text (word-swap (slurp arg))]
    (print new-text)
    (spit "resources/output/result" new-text :append true)))
