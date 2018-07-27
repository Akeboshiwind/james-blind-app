(ns blind-app.subs
  (:require [re-frame.core :refer [reg-sub]]))

(reg-sub
 ::image
 (fn [db]
   (:image db)))

(reg-sub
 ::image-name
 :<- [::image]
 (fn [image]
   (:name image)))

(reg-sub
 ::image-file
 :<- [::image]
 (fn [image]
   (:file image)))

(reg-sub
 ::image-as-data-url
 :<- [::image-file]
 (fn [[image-file] _]
   (let [reader (new js/FileReader)]
     (set! (.-onload reader)
           (fn [e]
             (.-result (.-target e))))
     (.readAsDataURL reader
                     image-file))))

(reg-sub
 ::analysis
 (fn [db]
   (:analysis db)))

(reg-sub
 ::error
 (fn [db]
   (:error db)))

(def not-nil? (complement nil?))

(reg-sub
 ::analysis-or-error
 :<- [::analysis]
 :<- [::error]
 (fn [[analysis error] _]
   (cond
     (not-nil? analysis) analysis
     (not-nil? error) error
     :else "")))
