(ns generate-readme
  "Emit README.md from `com.github.damn.v2` ns/var docstrings."
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [com.github.damn.v2]))

(def ^:private root
  (-> (io/file ".") .getCanonicalFile))

(defn- md-doc
  "Docstring → markdown: strip common indent, tag example fences as clojure."
  [doc]
  (when doc
    (let [lines (str/split-lines doc)
          stripped (map (fn [line]
                          (if (str/starts-with? line "  ")
                            (subs line 2)
                            line))
                        lines)
          tagged (loop [in-fence? false
                        acc []
                        [line & more] stripped]
                   (if-not line
                     acc
                     (cond
                       (and (not in-fence?) (= line "```"))
                       (recur true (conj acc "```clojure") more)

                       (and in-fence? (= line "```"))
                       (recur false (conj acc "```") more)

                       :else
                       (recur in-fence? (conj acc line) more))))]
      (str/join "\n" tagged))))

(defn- arglists-str [v]
  (->> (:arglists (meta v))
       (map pr-str)
       (str/join " ")))

(defn- image-for [sym-name]
  (let [rel (str "doc/images/" sym-name ".svg")
        f (io/file root rel)]
    (when (.isFile f)
      (str "![" sym-name "](" rel ")"))))

(defn- public-vars []
  (->> (ns-publics 'com.github.damn.v2)
       vals
       (sort-by (comp :line meta))))

(defn- render []
  (let [ns-doc (md-doc (:doc (meta (find-ns 'com.github.damn.v2))))
        sections
        (for [v (public-vars)
              :let [m (meta v)
                    name (name (:name m))
                    doc (md-doc (:doc m))
                    img (image-for name)]]
          (str "### `" name "`\n\n"
               "**Arglists:** `" (arglists-str v) "`\n\n"
               (or doc "_No docstring._")
               (when img (str "\n\n" img))
               "\n"))]
    (str "# v2\n\n"
         (or ns-doc "2D vector math on `[x y]` pairs.")
         "\n\n"
         "```clojure\n"
         "(require '[com.github.damn.v2 :as v2])\n"
         "```\n\n"
         "> This file is generated from API docstrings. Run `lein gen-readme` after editing them.\n\n"
         "## Install\n\n"
         "```clojure\n"
         ";; project.clj\n"
         ":repositories [[\"jitpack\" \"https://jitpack.io\"]]\n"
         ":dependencies [[com.github.damn/v2 \"COMMIT_OR_TAG\"]]\n"
         "```\n\n"
         "Lookup: https://jitpack.io/#damn/v2\n\n"
         "## API\n\n"
         "Examples in each docstring are locked by the unit tests.\n\n"
         (str/join "\n" sections)
         "## License\n\n"
         "MIT\n")))

(defn -main [& _]
  (let [out (io/file root "README.md")]
    (spit out (render))
    (println "Wrote" (.getPath out))))
