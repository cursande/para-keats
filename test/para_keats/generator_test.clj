(ns para-keats.generator-test
  (:require [clojure.test :refer :all]
            [para-keats.generator :refer :all]
            [clojure.string :refer [replace-first]]
            [cheshire.core :refer [parse-string]]
            [clj-http.fake :refer :all]))

(deftest test-match-last-word
  (testing
      "it takes a string and returns the final word on the line, with or without punctuation following it"
    (let [test-string-1 "Young buds sleep in the root's white core."
          test-string-2 "That blue Topshop top you've got on is nice"]
      (is (= ["core" "nice"]
             [(match-last-word test-string-1) (match-last-word test-string-2)])))))

(deftest test-text->last-words
  (testing
      "it takes a string, splits it into lines and returns a sequence of the final words"
    (let [test-string
          "Season of mists and mellow fruitfulness,
          Close bosom-friend of the maturing sun;
          Conspiring with him how to load and bless
          With fruit the vines that round the thatch-eves run;"]
      (is (= (lazy-seq ["fruitfulness" "sun" "bless" "run"])
             (text->last-words test-string))))))

(deftest test-filter-by-syllables
  (testing
      "it randomly selects a word in the response that has the same number of syllables
(any random word if none share the same syllable number)"
    (let [test-word "fly"
          test-res (parse-string (slurp "test/fixtures/datamuse_dry") true)]
      (is (or "lie" "buy")
          (filter-by-syllables test-word test-res)))))

(deftest test-fetch-rhymes
  (testing
      "it makes a request to the Datamuse API with each word, returning a sequence with a
rhyming word for each or the original word if nothing was returned in the response"
    (let [test-words (lazy-seq ["dry" "low" "cromulent"])]
      (with-fake-routes {
                         "https://api.datamuse.com/words?rel_rhy=dry"
                         {:get (fn [req]
                                 {:status 200 :body (slurp "test/fixtures/datamuse_dry")})}
                         "https://api.datamuse.com/words?rel_rhy=low"
                         {:get (fn [req]
                                 {:status 200 :body (slurp "test/fixtures/datamuse_low")})}
                         "https://api.datamuse.com/words?rel_rhy=cromulent"
                         {:get (fn [req]
                                 {:status 200 :body "[]"})}
                         }
        (is (or (lazy-seq ["lie" "go" "cromulent"])
                (lazy-seq ["buy" "go" "cromulent"]))
            (fetch-rhymes test-words))))))

(deftest test-gen-word-regex
  (testing
      "it takes a word and constructs a regex pattern with it for matching the last word in a line"
    (let [test-word "bat"
          test-string
          "I wish I had a bat in a hat with a bat,\n And a six four Impala"]
      (is (= "I wish I had a bat in a hat with a rat,\n And a six four Impala"
             (replace-first test-string (gen-word-regex test-word) "rat$1$2"))))))

(deftest test-word-swap
  (testing
      "it pulls out the last word of each line in a string and replaces it with a rhyming word (or words) found via API"
    (let [test-string
          "And I was green, greener than the hill
          Where the flowers grew and the sun shone still
          Now I'm darker than the deepest sea
          Just hand me down, give me a place to be."]
      (with-fake-routes {
                         "https://api.datamuse.com/words?rel_rhy=hill"
                         {:get (fn [req]
                                 {:status 200 :body (slurp "test/fixtures/datamuse_hill")})}
                         "https://api.datamuse.com/words?rel_rhy=still"
                         {:get (fn [req]
                                 {:status 200 :body (slurp "test/fixtures/datamuse_still")})}
                         "https://api.datamuse.com/words?rel_rhy=sea"
                         {:get (fn [req]
                                 {:status 200 :body (slurp "test/fixtures/datamuse_sea")})}
                         "https://api.datamuse.com/words?rel_rhy=be"
                         {:get (fn [req]
                                 {:status 200 :body (slurp "test/fixtures/datamuse_be")})}
                         }
        (is
         (= "And I was green, greener than the dollar bill
          Where the flowers grew and the sun shone ill
          Now I'm darker than the deepest emcee
          Just hand me down, give me a place to partee."
            (word-swap test-string)))))))
