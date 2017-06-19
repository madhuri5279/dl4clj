(ns ^{:doc "SparkTrainingStats is an interface that is used for accessing training statistics, for multiple TrainingMaster implementations.

The idea is that for debugging purposes, we want to collect a number of statistics related to the training. However, these statistics will vary, depending on which the type of training we are doing. Specifically, both the keys (number/names of stats) and their actual values (types/classes) can vary.

The interface here operates essentially as a Map<String,Object>. Note however that SparkTrainingStats instances may be nested: for example a ParameterAveragingTrainingMasterStats may have a CommonSparkTrainingStats instance which may in turn have a ParameterAveragingTrainingWorkerStats instance.

see: https://deeplearning4j.org/doc/org/deeplearning4j/spark/api/stats/SparkTrainingStats.html"}
    dl4clj.spark.api.interfaces.training-stats
  (:import [org.deeplearning4j.spark.api.stats SparkTrainingStats]))

(defn add-other-training-stats!
  "Combine the two training stats instances.

  :other-training-stats (SparkTrainingStats), training stats tracker to add to the first one
   - see: TBD

  returns the first instance with the other added in"
  [& {:keys [training-stats other-training-stats]}]
  (doto training-stats (.addOtherTrainingStats other-training-stats)))

(defn default-include-in-plots
  "When plotting statistics, we don't necessarily want to plot everything.

  :to-include (str), the key to include in the stat plot
   - need to look into possible options for to-include

  returns the training-stats instance"
  [& {:keys [training-stats stat-key-to-include]}]
  (doto training-stats (.defaultIncludeInPlots stat-key-to-include)))

(defn export-stat-files!
  "Export the stats as a collection of files.

  :path (str), the file path to export the files to

  :spark-context (sc), the spark context used in training
   - of type org.apache.spark.SparkContext
   - creation of these has not been implemented yet

  returns the training-stats instance"
  [& {:keys [training-stats path spark-context]}]
  (doto training-stats (.exportStatFiles path spark-context)))

(defn get-key-set-for-stats
  "returns the key set for the training stats instance"
  [training-stats]
  (.getKeySet training-stats))

(defn get-nested-training-stats
  "Return the nested training stats - if any."
  [training-stats]
  (.getNestedTrainingStats training-stats))

(defn get-short-name-for-key
  "Return a short (display) name for the given key.

  :stat-key (str), the identifier for the key you want the short name of"
  [& {:keys [training-stats stat-key]}]
  (.getShortNameForKey training-stats stat-key))

(defn get-value-for-key
  "Get the statistic value for this key"
  [& {:keys [training-stats stat-key]}]
  (.getValue training-stats stat-key))

(defn stats-as-string
  "get the stats as a string"
  [training-stats]
  (.statsAsString training-stats))