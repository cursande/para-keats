(ns para-keats.generator-test
  (:require [clojure.test :refer :all]
            [para-keats.generator :refer :all]))

(deftest test-text->last-words
  (testing "it takes a string, splits it into lines and returns a sequence of the final words"
    (let [test-string "Season of mists and mellow fruitfulness,
          Close bosom-friend of the maturing sun;
          Conspiring with him how to load and bless
          With fruit the vines that round the thatch-eves run;"]
      (is (= (lazy-seq ["fruitfulness" "sun" "bless" "run"])
             (text->last-words test-string))))))
