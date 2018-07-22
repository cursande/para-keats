(ns para-keats.generator)

(def test-string "There is not a fiercer hell than the failure in a great object.")

(def words (clojure.string/split test-string #" "))

(def word-transitions (partition-all 3 1 words))

(reduce (fn [p s]
          (merge-with clojure.set/union p
                      (let [[a b c] s]
                        {[a b]
                         (if c #{c} #{})})))
        {}
        word-transitions)
