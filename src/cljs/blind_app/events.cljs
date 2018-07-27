(ns blind-app.events
  (:require [re-frame.core :refer [reg-event-db reg-event-fx] :as rf]
            [cljs.spec.alpha :as s]
            [blind-app.db :as db]
            [day8.re-frame.http-fx]
            [ajax.core :as ajax]
            [expound.alpha :as expound]
            [clojure.string :as string]))

(defn check-and-throw
  "Throws an exception if `db` doesn't match the Spec `a-spec`."
  [a-spec db]
  (when-not (s/valid? a-spec db)
    (throw (ex-info (str "spec check failed: " (expound/expound-str a-spec db)) {}))))

(def check-spec-interceptor (rf/after (partial check-and-throw :blind-app.db/db)))

(defn post-request
  [uri params on-success on-failure]
  {:method          :post
   :uri             uri
   :params          params
   :timeout         5000
   :format          (ajax/json-request-format)
   :response-format (ajax/json-response-format {:keywords? true})
   :on-success      on-success
   :on-failure      on-failure})

(reg-event-db
 ::initialize-db
 [check-spec-interceptor]
 (fn [_ _]
   db/default-db))

(reg-event-fx
 ::add-image
 [check-spec-interceptor
  rf/trim-v]
 (fn [cofx [name image]]
   (let [db (:db cofx)]
     {:dispatch [::analyze-image]
      :db (assoc db
                 :image {:name name
                         :file image}
                 :analysis nil
                 :error nil)})))

(reg-event-fx
 ::analyze-image
 [check-spec-interceptor]
 (fn [cofx _]
   (let [db (:db cofx)]
     (if-let [azure-key (:azure-key db)]
       {:db (assoc db :error nil)
        :http-xhrio {:method          :post
                     :uri             "https://northeurope.api.cognitive.microsoft.com/vision/v1.0/describe"
                     :headers         {:ocp-apim-subscription-key azure-key}
                     :timeout         5000
                     :body            (let [image (:image db)
                                            name (:name image)
                                            file (:file image)]
                                        (doto (js/FormData.)
                                          (.append "file" file name)))
                     :response-format (ajax/json-response-format {:keywords? true})
                     :on-success      [::analyze-image-success]
                     :on-failure      [::analyze-image-failure]}}
       {:dispatch [::no-azure-key-set]}))))

(defn pretty-map
  [map]
  (-> map
      (cljs.pprint/pprint)
      (with-out-str)
      (string/replace #"\n" "<br>")))

(reg-event-db
 ::analyze-image-success
 [check-spec-interceptor
  rf/trim-v]
 (fn [db [ret]]
   (assoc db
          :analysis (pretty-map ret)
          :error nil)))

(reg-event-db
 ::analyze-image-failure
 [check-spec-interceptor
  rf/trim-v]
 (fn [db [ret]]
   (assoc db
          :analysis nil
          :error (pretty-map ret))))

(reg-event-db
 ::no-azure-key-set
 [check-spec-interceptor
  rf/trim-v]
 (fn [db [ret]]
   (assoc db
          :analysis nil
          :error "No azure key set!")))

(reg-event-db
 ::set-api-key
 [check-spec-interceptor
  rf/trim-v]
 (fn [db [key]]
   (assoc db
          :azure-key key)))
