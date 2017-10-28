(ns coloring-india.core
  (:require [clojure.java.io :as io]
            [clojure.core.logic :refer :all]
            [clojure.core.logic.fd :as fd]
            [cheshire.core :refer :all]
            [clojure.core.logic.pldb :as pldb]))

(pldb/db-rel coord lat long name)
(pldb/db-rel color x)

(defn generate-lvars
  [names]
  (into {} (for [name names]
             [name (lvar)])))

(defn create-db
  [file]
  (let [res (->> file
                 io/resource
                 slurp
                 parse-string
                 (map (fn [[lat long name]] [lat long (keyword name)])))]
  (reduce
   (fn [db c]
     (apply pldb/db-fact db coord c))
   (pldb/db)
   res)))

(defn adjacent
  [x y]
  (all
   (fresh [a b]
     (coord a b x)
     (coord a b y)
     (!= x y))))

(defn neighbor
  [state-a state-b]
  (all
   (color state-a)
   (color state-b)
   (!= state-a state-b)))

(defn adjacent-districts
  [db]
  (->> (pldb/with-db db (run* [q]
                          (fresh [x y]
                            (adjacent x y)
                            (== q [x y]))))
       (map sort)
       distinct))

(defn get-colors
  [n]
  (take n (shuffle ["red" "blue" "orchid" "sandybrown" "blue" "pink"
                    "DarkSlateGray" "DimGray" "Brown" "Goldenrod" "YellowGreen" "OliveDrab"
                    "LightSeaGreen" "Chartreuse" "Indigo" "Fuchsia" "Tomato" "DarkOrange"
                    ])))

(defn generate-colors
  [n]
  (reduce (fn [db c]
            (pldb/db-fact db color c))
          (pldb/db)
   (get-colors n)))

(defn process-state
  [state n]
  (let [single-state-color ["yellow"]
        file (str "data/" state "-out.json")
        db (create-db file)
        colors (generate-colors n)
        adjacent (adjacent-districts db)
        districts (if (empty? adjacent) [state] (sort (distinct (apply concat adjacent))))
        lvars (generate-lvars districts)
        probs (mapv (fn [[x y]] (neighbor (x lvars) (y lvars))) adjacent)
        sols (pldb/with-db colors (run 1 [q]
                                    (and* probs)
                                    (== q lvars)))
        ]
     (->> (if (empty? adjacent) ["yellow"] (map second (sort (first sols))))
          (zipmap (map (comp clojure.string/upper-case name) districts)))))
