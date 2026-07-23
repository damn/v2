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

(defn- public-vars []
  (->> (ns-publics 'com.github.damn.v2)
       vals
       (sort-by (comp :line meta))))

(defn- var-details [v]
  (let [m (meta v)
        name (name (:name m))
        doc (or (md-doc (:doc m)) "_No docstring._")]
    (str "<details>\n"
         "<summary><code>" name "</code></summary>\n\n"
         "**Arglists:** `" (arglists-str v) "`\n\n"
         doc
         "\n\n"
         "</details>\n")))

(defn- render []
  (let [ns-doc (md-doc (:doc (meta (find-ns 'com.github.damn.v2))))]
    (str "# v2\n\n"
         (or ns-doc "2D vector math on `[x y]` pairs.")
         "\n\n"
         "```clojure\n"
         "(require '[com.github.damn.v2 :as v2])\n"
         "```\n\n"
         "> Generated from API docstrings. Run `lein gen-readme` after editing them.\n\n"
         "## Install\n\n"
         "```clojure\n"
         ";; project.clj\n"
         ":repositories [[\"jitpack\" \"https://jitpack.io\"]]\n"
         ":dependencies [[com.github.damn/v2 \"COMMIT_OR_TAG\"]]\n"
         "```\n\n"
         "Lookup: https://jitpack.io/#damn/v2\n\n"
         "## API\n\n"
         "Click a name to expand docs and examples (covered by unit tests).\n\n"
         (str/join "\n" (map var-details (public-vars)))
         "\n## License\n\n"
         "MIT\n")))

(defn -main [& _]
  (let [out (io/file root "README.md")]
    (spit out (render))
    (println "Wrote" (.getPath out))))
