(ns com.github.damn.v2
  (:require [clojure.math :as math]))

(defn sub
  [[x1 y1] [x2 y2]]
  [(float (- x1 x2)) (float (- y1 y2))])

(defn scl
  [[x y] a]
  (if (number? a)
    [(float (* x a)) (float (* y a))]
    (let [[sx sy] a]
      [(float (* x sx)) (float (* y sy))])))

(defn scl2
  [[x y] sx sy]
  [(float (* x sx)) (float (* y sy))])

(defn len2
  [[x y]]
  (float (+ (* x x) (* y y))))

(defn rotate-rad
  [[x y] radians]
  (let [r (float radians)
        cos (float (Math/cos r))
        sin (float (Math/sin r))]
    [(float (- (* x cos) (* y sin)))
     (float (+ (* x sin) (* y cos)))]))

(defn set-length
  [v len]
  (let [len (float len)
        old-len2 (len2 v)
        target-len2 (* len len)]
    (if (or (zero? old-len2) (== old-len2 target-len2))
      v
      (scl v (float (Math/sqrt (/ target-len2 old-len2)))))))

(defn add
  [v1 v2]
  (mapv + v1 v2))

(defn scale
  [[x y] scalar]
  [(* x scalar)
   (* y scalar)])

(defn dot
  [[this-x this-y]
   [x y]]
  (+ (* this-x x)
     (* this-y y)))

(defn crs
  [[this-x this-y] [x y]]
  (- (* this-x y)
     (* this-y x)))

(defn length
  [[x y]]
  (math/sqrt (+ (* x x)
                (* y y))))

(defn normalise
  [[x y :as v]]
  (let [len (length v)]
    (if (zero? len)
      v
      [(/ x len)
       (/ y len)])))

(defn normal-vectors
  [[x y]]
  [[(- (float y))         x]
   [          y (- (float x))]])

(defn direction
  [[sx sy] [tx ty]]
  (normalise [(- (float tx) (float sx))
              (- (float ty) (float sy))]))

(defn distance
  [[x1 y1]
   [x2 y2]]
  (let [x-d (- x2 x1)
        y-d (- y2 y1)]
    (math/sqrt (+ (* x-d x-d)
                  (* y-d y-d)))))
