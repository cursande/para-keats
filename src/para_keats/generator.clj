(ns para-keats.generator)

(defn word-chain [word-transitions]
  (reduce (fn [p s]
            (merge-with clojure.set/union p
                        (let [[a b c] s]
                          {[a b] (if c #{c} #{})})))
          {}
          word-transitions))

(defn text->word-swap [s]
  (let [lines (into [] (remove (fn [s] (= s "")) (clojure.string/split s #"[;|,|\n]")))
        last-words (map (fn [s] (last (clojure.string/split s #" "))) lines)]
    last-words))

(def test-string
  "Season of mists and mellow fruitfulness,
  Close bosom-friend of the maturing sun;
  Conspiring with him how to load and bless
  With fruit the vines that round the thatch-eves run;")

(text->word-swap test-string)
