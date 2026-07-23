# v2

2D vector math on `[x y]` pairs for Clojure.

```clojure
(require '[com.github.damn.v2 :as v2])

(v2/add [1 2] [3 4])    ;; => [4 6]
(v2/length [3 4])       ;; => 5.0
(v2/direction [0 0] [1 1])
```

## Install

```clojure
;; project.clj
[com.github.damn/v2 "0.1.0-SNAPSHOT"]
```

Local checkout while developing:

```bash
lein install   # from this repo
# or symlink into the consumer's checkouts/
```

## License

MIT
