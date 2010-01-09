(ns wari-test
  (:use clojure.test
	wari))

(deftest where-stones-fall-test
  (is (= (where-stones-fall 3 5 8)
	 [4 6 7 8 9 10 11 12]))
  (is (= (where-stones-fall 10 12 20)
	 [11 13 0 1 2 3 4 5 6 7 8 9 10 11 13 0 1 2 3 4])))

