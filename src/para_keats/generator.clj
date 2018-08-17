(ns para-keats.generator
  (:require [clj-http.client :as client]
            [clojure.string :refer [split split-lines replace-first blank?]]))

(defn match-last-word [line]
  (let [match (re-find #"(\w+)$|(\w+)\W$" line)]
    (if (nil? (nth match 2))
      (nth match 1)
      (nth match 2))))

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
  (re-pattern (str "(" word ")$|(" word ")\\W$")))

(defn word-swap [text]
  (let [last-words (text->last-words text)
        search-replace-pairs (map (fn [a, b] [(gen-word-regex a), b])
                                  last-words
                                  (fetch-rhymes last-words))]
    (reduce (fn [s, r] (if (some? (re-find (first r) (last r)))
                         s
                         (apply replace-first s r)))
            text
            search-replace-pairs)))
