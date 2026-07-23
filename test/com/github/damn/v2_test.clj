(ns com.github.damn.v2-test
  "Tests lock the examples from `com.github.damn.v2` docstrings / README."
  (:require [clojure.test :refer :all]
            [com.github.damn.v2 :as v2]))

(set! *unchecked-math* :warn-on-boxed)

(def ^:private eps 1.0e-6)

(defn- nearly-equal-num?
  ([x y] (nearly-equal-num? x y eps))
  ([a b epsilon]
   (<= (Math/abs (- (double a) (double b)))
       (double epsilon))))

(defn- nearly-equal-v2? [[x1 y1] [x2 y2]]
  (and (nearly-equal-num? x1 x2)
       (nearly-equal-num? y1 y2)))

(deftest add-examples
  (is (= [4 6] (v2/add [1 2] [3 4]))))

(deftest move-examples
  (is (nearly-equal-v2?
       (v2/move [0 0] {:direction [1 0] :speed 10 :delta-time 0.5})
       [5.0 0.0])))

(deftest scale-examples
  (is (nearly-equal-v2? (v2/scale [2 4] 0.5) [1.0 2.0]))
  (is (nearly-equal-v2? (v2/scale [1 3] 0.5) [0.5 1.5]))
  (is (nearly-equal-v2? (v2/scale [2 1.2] -3) [-6.0 -3.6]))
  (is (nearly-equal-v2? (v2/scale [0 0] 10) [0.0 0.0])))

(deftest dot-examples
  (is (= 0 (v2/dot [1 0] [0 1])))
  (is (= 1 (v2/dot [1 0] [1 0])))
  (is (= 23 (v2/dot [2 3] [4 5]))))

(deftest crs-examples
  (is (= 1 (v2/crs [1 0] [0 1])))
  (is (= 0 (v2/crs [1 0] [1 0]))))

(deftest length-examples
  (is (nearly-equal-num? (v2/length [3 4]) 5.0))
  (is (nearly-equal-num? (v2/length [0 0]) 0.0))
  (is (nearly-equal-num? (v2/length [1.2 0.1]) 1.2041595))
  (is (nearly-equal-num? (v2/length [1.2 -0.1]) 1.2041595)))

(deftest normalise-examples
  (is (nearly-equal-v2? (v2/normalise [3 4]) [0.6 0.8]))
  (is (= [0 0] (v2/normalise [0 0]))))

(deftest normal-vectors-examples
  (let [[left right] (v2/normal-vectors [1 0])]
    (is (nearly-equal-v2? left [0.0 1]))
    (is (nearly-equal-v2? right [0.0 -1.0]))))

(deftest direction-examples
  (is (nearly-equal-v2? (v2/direction [0 0] [3 4]) [0.6 0.8]))
  (is (nearly-equal-v2? (v2/direction [1 1] [1 1]) [0.0 0.0])))

(deftest distance-examples
  (is (nearly-equal-num? (v2/distance [0 0] [3 4]) 5.0)))

(deftest angle-deg-examples
  (is (nearly-equal-num? (v2/angle-deg [1 0] [0 1]) 270.0))
  (is (nearly-equal-num? (v2/angle-deg [0 1] [0 1]) 0.0)))

(deftest angle-from-vector-examples
  (is (nearly-equal-num? (v2/angle-from-vector [0 1]) 0.0))
  (is (nearly-equal-num? (v2/angle-from-vector [-1 0]) 90.0))
  (is (nearly-equal-num? (v2/angle-from-vector [0 -1]) 180.0))
  (is (nearly-equal-num? (v2/angle-from-vector [1 0]) 270.0))
  (is (nearly-equal-num? (v2/angle-from-vector [1 1]) 315.0))
  (is (nearly-equal-num? (v2/angle-from-vector [1 -1]) 225.0))
  (is (nearly-equal-num? (v2/angle-from-vector [-1 -1]) 135.0))
  (is (nearly-equal-num? (v2/angle-from-vector [-1 1]) 45.0)))

(deftest double-ray-endpositions-examples
  (let [[s1 t1 s2 t2] (v2/double-ray-endpositions [0 0] [10 0] 0.5)]
    (is (nearly-equal-v2? s1 [0.0 0.26]))
    (is (nearly-equal-v2? t1 [10.0 0.26]))
    (is (nearly-equal-v2? s2 [0.0 -0.26]))
    (is (nearly-equal-v2? t2 [10.0 -0.26]))))
