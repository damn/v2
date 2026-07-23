(ns com.github.damn.v2-test
  (:require [clojure.test :refer :all]
            [com.github.damn.v2 :as v2]))

(set! *unchecked-math* :warn-on-boxed)

(def ^:private eps 0.000001)

(defn- nearly-equal-num?
  ([x y] (nearly-equal-num? x y eps))
  ([a b epsilon]
   (<= (Math/abs (- (double a) (double b)))
       (double epsilon))))

(defn- nearly-equal-v2? [[x1 y1] [x2 y2]]
  (and (nearly-equal-num? x1 x2)
       (nearly-equal-num? y1 y2)))

(deftest vecs-nearly-equal?
  (is (nearly-equal-v2? [0.0000011 0.0123]
                        [0.000001 0.0123])))

(deftest scale
  (is (nearly-equal-v2? (v2/scale [1 3] 0.5)
                        [0.5 1.5]))
  (is (nearly-equal-v2? (v2/scale [2 1.2] -3)
                        [-6.0 -3.6000001]))
  (is (nearly-equal-v2? (v2/scale [0 0] 10)
                        [0.0 0.0])))

(deftest length
  (is (nearly-equal-num? (v2/length [1.2 0.1])
                         1.2041595))
  (is (nearly-equal-num? (v2/length [1.2 -0.1])
                         1.2041595)))
