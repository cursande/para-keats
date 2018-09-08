(ns para-keats.generator
  (:require [clj-http.client :as client]
            [cheshire.core :refer [parse-string]]
            [clojure.string :refer [split-lines replace-first blank?]]
            [com.lemonodor.pronouncing :refer [phones-for-word syllable-count]]))

(defn match-last-word [line]
  (let [match (re-find #"(\w+)(?:\W+$|\W+\n|\n|$)" line)]
    (last match)))

(defn text->last-words [text]
  (let [lines (into [] (remove (fn [s] (blank? s))
                               (split-lines text)))]
    (map (fn [s] (match-last-word s))
         lines)))

(defn filter-by-syllables [word res]
  (let [syllables (->> word
                       (phones-for-word)
                       (map syllable-count)
                       last)
        filtered-words (filter (fn [w] (= (:numSyllables w) syllables))
                               res)]
    (if (seq filtered-words)
      (:word (rand-nth filtered-words))
      (:word (rand-nth res)))))

(defn fetch-rhymes [last-words]
  (let [base-url "https://api.datamuse.com/words?rel_rhy="]
    (pmap (fn [word]
           (as-> [word] res
             (:body (client/get (str base-url word) {:as :json}))
             (if (empty? res)
               word
               (filter-by-syllables word res))))
         last-words)))

(defn gen-word-regex [word]
  (re-pattern (str word "(\\W+\\n|\\W+$)|" word "(\\n|$)")))

(defn word-swap [text]
  (let [last-words (text->last-words text)
        match-replace-pairs (map (fn [m, r] [(gen-word-regex m),(str r "$1$2")])
                                 last-words
                                 (fetch-rhymes last-words))]
    (reduce (fn [s, m-r] (apply replace-first s m-r))
            text
            match-replace-pairs)))

(defn -main [arg]
  (let [new-text (word-swap (slurp arg))]
    (print new-text)
    (spit "resources/output/result" new-text :append true)
    (shutdown-agents)))
