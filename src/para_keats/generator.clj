(ns para-keats.generator
  (:require [clj-http.client :as client]
            [clojure.string :refer [split replace-first blank?]]))

(defn text->last-words [s]
  (let [lines (into [] (remove (fn [s] (blank? s))
                               (split s #"[;|,|\n]")))]
    (map (fn [s] (last (split s #" ")))
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

(defn word-swap [text]
  (let [last-words (text->last-words text)
        with-rhymes (map (fn [a, b] [(re-pattern (str a #"[;|,|\n]")), b])
                         last-words
                         (fetch-rhymes last-words))]
    (reduce (fn [s, r] (if (some? (re-find (first r) (last r)))
                         s
                         (apply replace-first s r)))
            text
            with-rhymes)))
