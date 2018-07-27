(ns blind-app.db
  (:require [cljs.spec.alpha :as s]
            [clojure.string :as string]))

(defn file?
  [f]
  (= (type f) js/File))

(defn image?
  [f]
  (and (file? f)
       (#{"image/gif" "image/jpeg" "image/png"} (.-type f))))

(s/def ::name string?)
(s/def ::file image?)

(s/def ::image (s/or :nil nil?
                     :image (s/keys :req-un [::name ::file])))

(s/def ::azure-key (s/or :nil nil?
                         :key string?))

(s/def ::analysis (s/or :nil nil?
                        :analysis string?))

(s/def ::error (s/or :nil nil?
                     :analysis string?))

(s/def ::db (s/keys :req-un [::image
                             ::azure-key
                             ::analysis
                             ::error]))

(def default-db
  {:image nil
   :azure-key "REPLACE ME"
   :analysis nil
   :error nil})
