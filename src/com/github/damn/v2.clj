(ns com.github.damn.v2
  "2D vector math on `[x y]` pairs.

  Vectors are plain Clojure vectors of two numbers. Operations return new
  vectors (or numbers); nothing is mutated.

  Examples assume `(require '[com.github.damn.v2 :as v2])`."
  (:require [clojure.math :as math]))

(defn add
  "Component-wise sum of two vectors.

  ```
  (v2/add [1 2] [3 4])
  ;; => [4 6]
  ```"
  [v1 v2]
  (mapv + v1 v2))

(defn move
  "Translate `position` along a unit (or any) `direction` by `speed * delta-time`.

  ```
  (v2/move [0 0] {:direction [1 0] :speed 10 :delta-time 0.5})
  ;; => [5.0 0.0]
  ```"
  [position {:keys [direction speed delta-time]}]
  (mapv #(+ %1 (* %2 speed delta-time)) position direction))

(defn scale
  "Multiply both components by `scalar`.

  ```
  (v2/scale [2 4] 0.5)
  ;; => [1.0 2.0]
  ```"
  [[x y] scalar]
  [(* x scalar)
   (* y scalar)])

(defn dot
  "Dot (scalar) product. `0` when perpendicular, positive when same-ish direction.

  ```
  (v2/dot [1 0] [0 1])  ;; => 0
  (v2/dot [1 0] [1 0])  ;; => 1
  (v2/dot [2 3] [4 5])  ;; => 23
  ```"
  [[this-x this-y]
   [x y]]
  (+ (* this-x x)
     (* this-y y)))

(defn crs
  "2D cross product (z-component of 3D cross). Positive when `v2` is
  counter-clockwise from `v1`.

  ```
  (v2/crs [1 0] [0 1])  ;; => 1
  (v2/crs [1 0] [1 0])  ;; => 0
  ```"
  [[this-x this-y] [x y]]
  (- (* this-x y)
     (* this-y x)))

(defn length
  "Euclidean length (magnitude).

  ```
  (v2/length [3 4])  ;; => 5.0
  (v2/length [0 0])  ;; => 0.0
  ```"
  [[x y]]
  (math/sqrt (+ (* x x)
                (* y y))))

(defn normalise
  "Unit vector in the same direction. Zero vector stays `[0 0]`.

  ```
  (v2/normalise [3 4])  ;; => [0.6 0.8]
  (v2/normalise [0 0])  ;; => [0 0]
  ```"
  [[x y :as v]]
  (let [len (length v)]
    (if (zero? len)
      v
      [(/ x len)
       (/ y len)])))

(defn normal-vectors
  "Two vectors perpendicular to `v` (left-hand and right-hand normals).
  Not normalised; same length as `v`.

  ```
  (v2/normal-vectors [1 0])
  ;; => [[0.0 1] [0 -1.0]]
  ```"
  [[x y]]
  [[(- (float y))         x]
   [          y (- (float x))]])

(defn direction
  "Unit vector from point `from` toward point `to`. Equal points → `[0.0 0.0]`.

  ```
  (v2/direction [0 0] [3 4])  ;; => [0.6 0.8]
  (v2/direction [1 1] [1 1])  ;; => [0.0 0.0]
  ```"
  [[sx sy] [tx ty]]
  (normalise [(- (float tx) (float sx))
              (- (float ty) (float sy))]))

(defn distance
  "Euclidean distance between two points.

  ```
  (v2/distance [0 0] [3 4])  ;; => 5.0
  ```"
  [[x1 y1]
   [x2 y2]]
  (let [x-d (- x2 x1)
        y-d (- y2 y1)]
    (math/sqrt (+ (* x-d x-d)
                  (* y-d y-d)))))

(defn angle-deg
  "Angle in degrees from `reference` to `this`, counter-clockwise, in `[0, 360)`.

  ```
  (v2/angle-deg [1 0] [0 1])  ;; => 270.0
  (v2/angle-deg [0 1] [0 1])  ;; => 0.0
  ```"
  [this reference]
  (let [angle (math/to-degrees
               (math/atan2 (crs reference this)
                           (dot reference this)))]
    (if (neg? angle)
      (+ angle 360)
      angle)))

(defn angle-from-vector
  "Heading of `v` in degrees where up `[0 1]` is `0`, counter-clockwise
  (left `[-1 0]` is `90`, down `[0 -1]` is `180`, right `[1 0]` is `270`).

  ```
  (v2/angle-from-vector [0 1])   ;; => 0.0
  (v2/angle-from-vector [-1 0])  ;; => 90.0
  (v2/angle-from-vector [0 -1])  ;; => 180.0
  (v2/angle-from-vector [1 0])   ;; => 270.0
  ```"
  [v]
  (angle-deg v [0 1]))

(defn double-ray-endpositions
  "Two parallel segment endpoints offset left/right of the line from `start`
  to `target` by half of `(path-w + 0.02)`.

  Returns `[start1 target1 start2 target2]`. `path-w` must be `< 0.98`.

  ```
  (v2/double-ray-endpositions [0 0] [10 0] 0.5)
  ;; => [[0.0 0.26] [10.0 0.26] [0.0 -0.26] [10.0 -0.26]]
  ```"
  [[start-x start-y]
   [target-x target-y]
   path-w]
  {:pre [(< path-w 0.98)]}
  (let [path-w (+ path-w 0.02)
        v (direction [start-x start-y]
                     [target-x target-y])
        [normal1 normal2] (normal-vectors v)
        normal1 (scale normal1 (/ path-w 2))
        normal2 (scale normal2 (/ path-w 2))
        start1  (add [start-x  start-y]  normal1)
        start2  (add [start-x  start-y]  normal2)
        target1 (add [target-x target-y] normal1)
        target2 (add [target-x target-y] normal2)]
    [start1 target1 start2 target2]))
