(ns para-keats.generator)

(def words (clojure.string/split test-string #" "))

(def word-transitions (partition-all 3 1 words))

(defn word-chain [word-transitions]
  (reduce (fn [p s]
            (merge-with clojure.set/union p
                        (let [[a b c] s]
                          {[a b] (if c #{c} #{})})))
          {}
          word-transitions))
