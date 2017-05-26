(ns dl4clj.datasets-test
  (:refer-clojure :exclude [reset!])
  (:require [clojure.test :refer :all]
            [dl4clj.datasets.datavec :refer :all]
            [dl4clj.datasets.rearrange :refer :all]
            [dl4clj.datasets.fetchers.default-dataset-fetchers :refer :all]
            [dl4clj.datasets.fetchers.base-data-fetcher :refer :all]
            [dl4clj.datasets.iterator.iterators :refer :all]
            [dl4clj.datasets.iterator.impl.default-datasets :refer :all]
            [dl4clj.datasets.iterator.impl.list-data-set-iterator :refer :all]
            [dl4clj.datasets.iterator.impl.move-window-data-set-fetcher :refer :all]
            [dl4clj.datasets.iterator.impl.multi-data-set-iterator-adapter :refer :all]
            [dl4clj.datasets.iterator.impl.singleton-multi-data-set-iterator :refer :all]
            [datavec.api.split :refer :all]
            [nd4clj.linalg.dataset.api.pre-processors :refer :all]
            [nd4clj.linalg.api.ds-iter :refer :all]
            [datavec.api.writeable :refer :all]
            [datavec.api.records.readers :refer :all]
            [dl4clj.utils :refer [array-of]])
  ;; image transforms have not been implemented so importing this default one for testing
  ;; https://deeplearning4j.org/datavecdoc/org/datavec/image/transform/package-summary.html
  (:import [org.datavec.image.transform ColorConversionTransform]))

(deftest dataset-fetchers-test
  (testing "dataset fetchers"
    ;; dl4clj.datasets.fetchers.default-dataset-fetchers
    (is (= org.deeplearning4j.datasets.fetchers.IrisDataFetcher (type (iris-fetcher))))
    (is (= org.deeplearning4j.datasets.fetchers.MnistDataFetcher (type (mnist-fetcher))))
    (is (= org.deeplearning4j.datasets.fetchers.MnistDataFetcher
           (type (mnist-fetcher :binarize? true))))
    (is (= org.deeplearning4j.datasets.fetchers.MnistDataFetcher
           (type
            (mnist-fetcher :binarize? true :train? true :shuffle? true :rng-seed 123))))
    ;; dl4clj.datasets.fetchers.base-data-fetcher
    (is (= java.lang.Integer (type (fetcher-cursor (iris-fetcher)))))
    (is (= java.lang.Boolean (type (has-more? (iris-fetcher)))))
    (is (= java.lang.Integer (type (input-column-length (iris-fetcher)))))
    (is (= org.deeplearning4j.datasets.fetchers.IrisDataFetcher
           (type (reset-fetcher! (iris-fetcher)))))
    (is (= java.lang.Integer (type (n-examples-in-ds (iris-fetcher)))))
    (is (= java.lang.Integer (type (n-outcomes-in-ds (iris-fetcher)))))))

(deftest ds-iteration-creation-test
  (testing "the creation of dataset iterators"
    ;; dl4clj.datasets.iterator.impl.default-datasets
    ;; cifar dataset
    (is (= org.deeplearning4j.datasets.iterator.impl.CifarDataSetIterator
           (type (new-cifar-data-set-iterator :batch-size 2 :n-examples 100))))
    (is (= org.deeplearning4j.datasets.iterator.impl.CifarDataSetIterator
           (type (new-cifar-data-set-iterator :batch-size 2 :img-dims [1 1 1]))))
    (is (= org.deeplearning4j.datasets.iterator.impl.CifarDataSetIterator
           (type (new-cifar-data-set-iterator :batch-size 2 :n-examples 100 :train? true))))
    (is (= org.deeplearning4j.datasets.iterator.impl.CifarDataSetIterator
           (type (new-cifar-data-set-iterator :batch-size 2 :n-examples 100
                                              :train? true :img-dims [1 1 1]))))
    (is (= org.deeplearning4j.datasets.iterator.impl.CifarDataSetIterator
           (type (new-cifar-data-set-iterator :batch-size 3 :n-examples 100
                                              :train? true :img-dims [3 3 3]
                                              :use-special-pre-process-cifar? true))))
    (is (= org.deeplearning4j.datasets.iterator.impl.CifarDataSetIterator
           (type (new-cifar-data-set-iterator :batch-size 3 :n-examples 100
                                              :train? true :img-dims [3 3 3]
                                              :use-special-pre-process-cifar? true
                                              :n-possible-labels 5
                                              :img-transform (ColorConversionTransform.)))))

    ;; iris dataset
    (is (= org.deeplearning4j.datasets.iterator.impl.IrisDataSetIterator
           (type (new-iris-data-set-iterator :batch 2 :n-examples 100))))

    ;; lfwd
    (is (= org.deeplearning4j.datasets.iterator.impl.LFWDataSetIterator
           (type (new-lfw-data-set-iterator :img-dims [1 1 1]))))
    (is (= org.deeplearning4j.datasets.iterator.impl.LFWDataSetIterator
           (type (new-lfw-data-set-iterator :batch-size 2 :n-examples 100))))
    (is (= org.deeplearning4j.datasets.iterator.impl.LFWDataSetIterator
           (type (new-lfw-data-set-iterator :img-dims [1 1 1] :batch-size 2
                                            :use-subset? true))))
    (is (= org.deeplearning4j.datasets.iterator.impl.LFWDataSetIterator
           (type (new-lfw-data-set-iterator :img-dims [1 1 1] :batch-size 2
                                            :n-examples 100))))
    (is (= org.deeplearning4j.datasets.iterator.impl.LFWDataSetIterator
           (type (new-lfw-data-set-iterator :img-dims [1 1 1] :batch-size 2
                                            :n-examples 100 :train? true
                                            :split-train-test 0.50))))
    (is (= org.deeplearning4j.datasets.iterator.impl.LFWDataSetIterator
           (type (new-lfw-data-set-iterator :n-labels 5 :batch-size 2
                                            :n-examples 100 :train? true
                                            :split-train-test 0.50))))
    (is (= org.deeplearning4j.datasets.iterator.impl.LFWDataSetIterator
           (type (new-lfw-data-set-iterator :img-dims [1 1 1] :batch-size 2
                                            :n-examples 100 :train? true
                                            :split-train-test 0.50 :n-labels 5
                                            :use-subset? true :rng 123))))
    (is (= org.deeplearning4j.datasets.iterator.impl.LFWDataSetIterator
           (type (new-lfw-data-set-iterator :img-dims [1 1 1] :batch-size 2
                                            :n-examples 100 :train? true
                                            :split-train-test 0.50 :n-labels 5
                                            :use-subset? true :rng 123))))
    (is (= org.deeplearning4j.datasets.iterator.impl.LFWDataSetIterator
           (type (new-lfw-data-set-iterator :img-dims [1 1 1] :batch-size 2
                                            :n-examples 100 :train? true
                                            :split-train-test 0.50 :n-labels 5
                                            :use-subset? true :rng 123
                                            :label-generator (new-parent-path-label-generator)))))
    (is (= org.deeplearning4j.datasets.iterator.impl.LFWDataSetIterator
           (type (new-lfw-data-set-iterator :img-dims [1 1 1] :batch-size 2
                                            :n-examples 100 :train? true
                                            :split-train-test 0.50 :n-labels 5
                                            :use-subset? true :rng 123
                                            :label-generator (new-parent-path-label-generator)
                                            :img-transform (ColorConversionTransform.)))))

    ;;mnist
    (is (= org.deeplearning4j.datasets.iterator.impl.MnistDataSetIterator
           (type (new-mnist-data-set-iterator :batch-size 5 :train? true
                                              :seed 123))))
    (is (= org.deeplearning4j.datasets.iterator.impl.MnistDataSetIterator
           (type (new-mnist-data-set-iterator :batch 5 :n-examples 100))))
    (is (= org.deeplearning4j.datasets.iterator.impl.MnistDataSetIterator
           (type (new-mnist-data-set-iterator :batch 5 :n-examples 100 :binarize? true))))
    (is (= org.deeplearning4j.datasets.iterator.impl.MnistDataSetIterator
           (type (new-mnist-data-set-iterator :batch 5 :n-examples 100 :binarize? true
                                               :train? true :shuffle? true :rng-seed 123))))

    ;; raw mnist
    (is (= org.deeplearning4j.datasets.iterator.impl.RawMnistDataSetIterator
           (type (new-raw-mnist-data-set-iterator :batch 5 :n-examples 100))))

    ;; dl4clj.datasets.iterator.impl.list-data-set-iterator
    (is (= org.deeplearning4j.datasets.iterator.impl.ListDataSetIterator
           (type (new-list-data-set-iterator
                  :data-set [(new-raw-mnist-data-set-iterator :batch 5 :n-examples 100)]))))
    (is (= org.deeplearning4j.datasets.iterator.impl.ListDataSetIterator
           (type (new-list-data-set-iterator
                  :data-set [(new-raw-mnist-data-set-iterator :batch 5 :n-examples 100)]
                  :batch 6))))

    ;; dl4clj.datasets.iterator.impl.move-window-data-set-fetcher
    ;; figure out what  Only rotating matrices means
    #_(is (= "" (type (new-moving-window-data-set-fetcher
                     :data-set (next-data-point (reset-fetcher! (new-mnist-data-set-iterator :batch 5 :n-examples 100)))
                     :window-rows 1
                     :window-columns 1))))
    #_(is (= "" (type (fetch! :ds-fetcher "" :n-examples 10))))

    ;; dl4clj.datasets.iterator.impl.multi-data-set-iterator-adapter
    (is (= org.deeplearning4j.datasets.iterator.impl.MultiDataSetIteratorAdapter
           (type (new-multi-data-set-iterator-adapter
                  (new-mnist-data-set-iterator :batch 5 :n-examples 100)))))

    ;; dl4clj.datasets.iterator.impl.singleton-multi-data-set-iterator
    (is (= org.deeplearning4j.datasets.iterator.impl.SingletonMultiDataSetIterator
           (type (new-singleton-multi-data-set-iterator
                  (next-data-point
                   (reset-fetcher!
                    (new-multi-data-set-iterator-adapter
                     (new-mnist-data-set-iterator :batch 5 :n-examples 100))))))))))

(deftest ds-iteration-interaction-fns-test
  (testing "the api fns for ds iterators"
    (let [iter (new-mnist-data-set-iterator :batch 5 :n-examples 100)
          iter-w-labels (new-lfw-data-set-iterator :img-dims [1 1 1] :batch-size 2
                                                   :n-examples 100 :train? true
                                                   :split-train-test 0.50 :n-labels 5
                                                   :use-subset? true :rng 123
                                                   :label-generator (new-parent-path-label-generator)
                                                   :img-transform (ColorConversionTransform.))
          cifar-iter (new-cifar-data-set-iterator :batch-size 2 :n-examples 100)]
      ;; nd4clj.linalg.api.ds-iter
      (is (= java.lang.Boolean (type (async-supported? iter))))
      (is (= java.lang.Integer (type (get-batch-size iter))))
      (is (= java.lang.Integer (type (get-current-cursor iter))))
      (is (= java.util.ArrayList (type (get-labels iter-w-labels))))
      (is (= org.deeplearning4j.datasets.iterator.impl.MnistDataSetIterator
             (type (set-pre-processor! :iter iter
                                       :pre-processor (new-min-max-normalization-ds-preprocessor)))))
      (is (= org.nd4j.linalg.dataset.api.preprocessor.NormalizerMinMaxScaler
             (type (new-min-max-normalization-ds-preprocessor))
             (type (get-pre-processor iter))))
      (is (= java.lang.Integer (type (get-input-columns iter))))
      (is (= org.nd4j.linalg.dataset.DataSet
             (type (next-n-examples :iter iter-w-labels :n 2))))
      (is (= java.lang.Integer (type (get-num-examples iter))))
      (is (= (type iter-w-labels) (type (reset-iter! iter-w-labels))))
      (is (= java.lang.Boolean (type (reset-supported? iter-w-labels))))
      (is (= java.lang.Integer (type (get-total-examples iter))))
      (is (= java.lang.Integer (type (get-total-outcomes iter))))

      ;; dl4clj.datasets.iterator.impl.default-datasets
      (is (= java.lang.Boolean (type (has-next? iter))))
      (is (= org.nd4j.linalg.dataset.DataSet (type (next-data-point iter-w-labels))))
      ;; this is going to fail when this is running in an enviro with gpus or spark I think
      ;; will need to see if that is the case in some way
      (is (= org.nd4j.linalg.cpu.nativecpu.NDArray
             (type (get-feature-matrix (next-data-point (reset-iter! iter-w-labels))))))
      (is (= (type cifar-iter) (type (train-cifar-iter! cifar-iter))))
      (is (= (type cifar-iter) (type (test-cifar-iter! :iter cifar-iter))))
      (is (= (type cifar-iter) (type (test-cifar-iter! :iter cifar-iter
                                                       :n-examples 100))))
      (is (= (type cifar-iter) (type (test-cifar-iter! :iter cifar-iter
                                                       :n-examples 100
                                                       :batch-size 5)))))))

(deftest label-generators-test
  (testing "the creation of label generators and their functionality"
    (is (= org.datavec.api.io.labels.ParentPathLabelGenerator
           (type (new-parent-path-label-generator))))
    (is (= org.datavec.api.io.labels.PatternPathLabelGenerator
           (type (new-pattern-path-label-generator :pattern "."))))
    (is (= org.datavec.api.io.labels.PatternPathLabelGenerator
           (type (new-pattern-path-label-generator :pattern "." :pattern-position 0))))
    (is (= org.datavec.api.writable.Text
           (type
            (get-label-for-path :label-generator (new-parent-path-label-generator)
                                :path "resources/paravec/labeled/finance"))))))

(deftest path-filter-tests
  (testing "the creation of path filters and their functionality"
    (is (= org.datavec.api.io.filters.BalancedPathFilter
           (type
            (new-balanced-path-filter :rng (new java.util.Random)
                                      :extensions [".txt"]
                                      :label-generator (new-parent-path-label-generator)
                                      :max-paths 1
                                      :max-labels 3
                                      :min-paths-per-label 1
                                      :max-paths-per-label 1
                                      :labels []))))
    (is (= org.datavec.api.io.filters.RandomPathFilter
           (type
            (new-random-path-filter :rng (new java.util.Random)
                                    :extensions [".txt"]
                                    :max-paths 2))))
    (is (= (type
            (array-of :data [(java.net.URI/create "foo")]
                      :java-type java.net.URI))
           (type
            (filter-paths :path-filter (new-random-path-filter :rng (new java.util.Random)
                                                               :extensions [".txt"]
                                                               :max-paths 2)
                          :paths [(java.net.URI/create "foo")]))))))



(deftest file-split-testing
  (testing "base level io stuffs"
    ;; file split
    (is (= org.datavec.api.split.FileSplit
           (type (new-filesplit :root-dir "resources/poker"))))
    (is (= org.datavec.api.split.FileSplit
           (type (new-filesplit :root-dir "resources/poker"
                                :rng-seed (new java.util.Random)))))
    (is (= org.datavec.api.split.FileSplit
           (type (new-filesplit :root-dir "resources/poker"
                                :allow-format ".csv"))))
    (is (= org.datavec.api.split.FileSplit
           (type (new-filesplit :root-dir "resources/poker"
                                :allow-format ".csv"
                                :recursive? true))))
    (is (= org.datavec.api.split.FileSplit
           (type (new-filesplit :root-dir "resources/poker"
                                :allow-format ".csv"
                                :rng-seed (new java.util.Random)))))
    (is (= java.io.File (type (get-root-dir (new-filesplit :root-dir "resources/poker")))))

    ;; collection input split
    (is (= org.datavec.api.split.CollectionInputSplit
           (type (new-collection-input-split :coll [(new java.net.URI "foo")
                                                    (new java.net.URI "baz")]))))

    ;; input stream input split
    (with-open [data (clojure.java.io/input-stream "resources/poker/poker-hand-testing.csv")
                other-data (clojure.java.io/input-stream "resources/poker/poker-hand-training.csv")]
      (is (= org.datavec.api.split.InputStreamInputSplit
             (type (new-input-stream-input-split
                    :in-stream data
                    :file-path "resources/poker/poker-hand-testing.csv"))))
      (is (= org.datavec.api.split.InputStreamInputSplit
             (type (new-input-stream-input-split
                    :in-stream data))))

      (is (= java.io.BufferedInputStream
             (type (get-is (new-input-stream-input-split
                            :in-stream data)))))
      (= org.datavec.api.split.InputStreamInputSplit
         (type (set-is! :input-stream-input-split (new-input-stream-input-split
                                                   :in-stream data)
                        :is other-data))))

    ;; list string input split
    (is (= org.datavec.api.split.ListStringSplit
           (type (new-list-string-split :data (list "foo" "baz")))))
    (is (= (list "foo" "baz")
           (get-list-string-split-data
            (new-list-string-split :data (list "foo" "baz")))))

    ;; numbered file input split
    (is (= org.datavec.api.split.NumberedFileInputSplit
           (type
            (new-numbered-file-input-split :base-string "f_%d.txt"
                                           :inclusive-min-idx 0
                                           :inclusive-max-idx 10))))

    ;; string split
    (is (= org.datavec.api.split.StringSplit
           (type (new-string-split :data "foo baz bar"))))
    (is (= "foo baz bar" (get-list-string-split-data
                          (new-string-split :data "foo baz bar"))))

    ;; transform split
    (is (= org.datavec.api.split.TransformSplit
           (type
            (new-transform-split :base-input-split (new-collection-input-split
                                                    :coll [(new java.net.URI "foo")
                                                           (new java.net.URI "baz")])
                                 :to-be-replaced "foo"
                                 :replaced-with "oof"))))

    ;; sample
    (is (= (type (new-collection-input-split
                  :coll [(new java.net.URI "foo")
                         (new java.net.URI "baz")]))
           (type (first (sample :split (new-collection-input-split
                                        :coll [(new java.net.URI "foo")
                                               (new java.net.URI "baz")])
                                :weights [50 50]

                                :path-filter (new-random-path-filter
                                              :rng (new java.util.Random)
                                              :extensions [".txt"]
                                              :max-paths 2))))))
    (is (= (type (new-collection-input-split
                  :coll [(new java.net.URI "foo")
                         (new java.net.URI "baz")]))
           (type (first (sample :split (new-collection-input-split
                                        :coll [(new java.net.URI "foo")
                                               (new java.net.URI "baz")])
                                :weights [50 50])))))
    (is (= 2 (count (sample :split (new-collection-input-split
                                    :coll [(new java.net.URI "foo")
                                           (new java.net.URI "baz")])
                            :weights [50 50]))))))

(deftest input-split-interface-testing
  (testing "the interfaces used by input splits"
    ;; datavec.api.writeable
    (let [f-split (new-filesplit :root-dir "resources/poker")]

      (is (= java.lang.Long (type (length f-split))))
      (is (= java.net.URI (type (first (locations f-split)))))
      (is (= org.datavec.api.util.files.UriFromPathIterator
             (type (locations-iterator f-split))))
      (is (= org.nd4j.linalg.collection.CompactHeapStringList$CompactHeapStringListIterator
             (type (locations-path-iterator f-split))))
      (is (= (type f-split) (type (reset-input-split! f-split)))))))

(deftest record-readers-test
  (testing "the creation of record readers"
    ;; datavec.api.records.readers


    ))

(deftest pre-processors-test
  (testing "testing the creation of pre-processors"
    ;; nd4clj.linalg.dataset.api.pre-processors

    ))

(deftest rr-ds-iterator-creation-test
  (testing "the creation of record readers dataset iterators"
    ;; lets test bottom level first then work up to this
    ;; nd4clj.linalg.api.ds-iter
    ))

(deftest ds-iterators-test
  (testing "the creation of various dataset iterators"
    ;; dl4clj.datasets.iterator.iterators

    ))

(deftest rearrange-test
  (testing "the rearrange ns"
    ;; currently can't accurately test without the unstructed dataset these fns want
    #_(let [formatter (new-unstructured-formatter
                       :destination-root-dir (str "resources/tmp/rearrange/"
                                                  (java.util.UUID/randomUUID))
                       :src-root-dir "resources/poker"
                       :labeling-type :name
                       :percent-train 50)]
        (is (= org.deeplearning4j.datasets.rearrange.LocalUnstructuredDataFormatter
               (type formatter)))
        (is (= "" (get-new-destination :unstructured-formatter formatter
                                       :file-path (str "resources/tmp/rearrange/"
                                                       (java.util.UUID/randomUUID))
                                       :train? true)))

        )))
