(ns Thirty-Three.combinatorics-library)

;; maybe??
(defn cartesian-product
  "All the ways to take one item from each sequence, copied from Mark
Engelberg's clojure.math.combinatorics library:
https://github.com/clojure/math.combinatorics/blob/efb48780/src/main/clojure/clojure/math/combinatorics.clj#L117"
  [& seqs]
  (let [v-original-seqs (vec seqs)
	step
	(fn step [v-seqs]
	  (let [increment
		(fn [v-seqs]
		  (loop [i (dec (count v-seqs)), v-seqs v-seqs]
		    (if (= i -1) nil
			(if-let [rst (next (v-seqs i))]
			  (assoc v-seqs i rst)
			  (recur (dec i) (assoc v-seqs i (v-original-seqs i)))))))]
	    (when v-seqs
	       (cons (map first v-seqs)
		     (lazy-seq (step (increment v-seqs)))))))]
    (when (every? seq seqs)
      (lazy-seq (step v-original-seqs)))))
