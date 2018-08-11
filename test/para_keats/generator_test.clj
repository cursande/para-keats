(ns para-keats.generator-test
  (:require [clojure.test :refer :all]
            [para-keats.generator :refer :all]
            [clj-http.fake :refer :all]))

(deftest test-text->last-words
  (testing "it takes a string, splits it into lines and returns a sequence of the final words"
    (let [test-string "Season of mists and mellow fruitfulness,
          Close bosom-friend of the maturing sun;
          Conspiring with him how to load and bless
          With fruit the vines that round the thatch-eves run;"]
      (is (= (lazy-seq ["fruitfulness" "sun" "bless" "run"])
             (text->last-words test-string))))))

(deftest test-fetch-rhymes
  (testing "it makes a request to the Datamuse API with each word, returning a sequence with a
rhyming word for each or the original word if nothing was returned in the response"
    (let [test-words (lazy-seq ["dry" "low"])]
      (with-fake-routes {
                         "https://api.datamuse.com/words?rel_rhy=dry"
                         {:get (fn [req]
                                 {:status 200
                                  :headers {}
                                  :body (slurp "test/fixtures/datamuse_get_1")})}
                         "https://api.datamuse.com/words?rel_rhy=low"
                         {:get (fn [req]
                                 {:status 200
                                  :headers {}
                                  :body (slurp "test/fixtures/datamuse_get_2")})}
                         }
        (is (or (lazy-seq ["lie" "go"])
                (lazy-seq ["buy" "go"])
                (lazy-seq ["lie" "blow"])
                (lazy-seq ["buy" "blow"]))
            (fetch-rhymes test-words))))))
