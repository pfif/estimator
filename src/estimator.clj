(ns estimator
  (:require [java-time :as t]
            [com.stuartsierra.frequencies :as freq]))

(defn collect-new-duration []
  (let [start-time (t/local-date-time)]
    (println "Please press enter when the current iteration is done")
    (read-line)
    (t/duration start-time (t/local-date-time)))
  )

(defn show-estimation [durations remaining_iterations]
  (let [stats (->> durations
                   (map (fn [duration] (t/as duration :seconds)))
                   (frequencies)
                   (freq/stats)
                   )
        representative_duration (-> stats
                                    (get :percentiles)
                                    (get 75)
                                    (t/duration :seconds))
        now (t/local-date-time)
        endtime (t/plus now (t/multiply-by representative_duration remaining_iterations))]
    (-> (format "Remaining iterations: %s\n\nEstimated end time: %s\n"
                remaining_iterations
                endtime)
        (println)
        )
    )
  )

(defn estimates [durations remaining_iterations]
  (let [durations (conj durations (collect-new-duration))
        remaining_iterations (- remaining_iterations 1)]
    (show-estimation durations remaining_iterations)
    (when (> remaining_iterations 0)
      (recur durations remaining_iterations)))
  )

(defn -main [& args]
  (println "Welcome to estimator")
  (let [iterations (Integer/parseInt (first args))]
    (estimates [] iterations)
    )
  )
