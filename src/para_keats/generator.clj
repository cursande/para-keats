(ns para-keats.generator
  (:require [clj-http.client :as client]
            [clojure.string :refer [split]]
            [cheshire.core :refer [parse-string]]))


(defn text->word-swap [s]
  (let [lines (into [] (remove (fn [s] (= s "")) (split s #"[;|,|\n]")))]
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
