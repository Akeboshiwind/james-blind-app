(ns blind-app.views
  (:require [re-frame.core :refer [subscribe dispatch]]
            [reagent.core :as r]
            [blind-app.subs :as s]
            [blind-app.events :as e]))

(defn file-input
  [label on-change]
  [:div
   [:input.inputfile {:type "file"
                      :name "file"
                      :id "file"
                      :on-change on-change}]
   [:label {:for "file"}
    [:strong
     label]]])

(defn preview
  []
  [:div
   [:img#preview {:src ""}]
   (if-let [image-file @(subscribe [::s/image-file])]
     (let [reader (new js/FileReader)]
       (set! (.-onload reader)
             (fn [e]
               (set! (.-src (.getElementById js/document "preview"))
                     (.-result (.-target e)))))
       (.readAsDataURL reader
                       image-file)))])

(defn analyze
  []
  [:div.analyzebutton
   {:on-click #(dispatch [::e/analyze-image])}
   "Analyze"])

(defn analysis
  []
  [:pre
   {:dangerouslySetInnerHTML
    {:__html @(subscribe [::s/analysis-or-error])}}])

(defn api-key-input
  []
  [:div
   [:input#api-key {:type "text"}]
   [:button
    {:on-click #(dispatch [::e/set-api-key (.-value (.getElementById js/document "api-key"))])}
    "Set API key"]])

(defn main-panel
  []
  [:div
   [file-input
    "Choose a file"
    (fn []
      (let [input (.getElementById js/document "file")]
        (if-let [file (aget (.-files input) 0)]
           (dispatch [::e/add-image "test" file]))))]
   [preview]
   [analysis]])
