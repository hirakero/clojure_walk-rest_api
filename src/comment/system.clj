 (ns comment.system
  (:require [integrant.core :as ig]
            [comment.handler :as handler]
            [ring.adapter.jetty :as jetty]))

(def system-config
  {:comment/jetty {:handler (ig/ref :comment/handler)
                   :port 3001}
   :comment/handler (:db (ig/ref :comment/db))
   :comment/db nil})

(defmethod ig/init-key :comment/jetty [k {:keys [handler port]}]
  (println "init jetty")
  (jetty/run-jetty handler {:port port :join? false}))

(defmethod ig/init-key :comment/handler [k {:keys [db]}]
  (println "inti handler")
  (handler/create-app db))

(defmethod ig/init-key :comment/db [k _]
  (println "int db")
  nil)

(defmethod ig/halt-key! :comment/jetty [_ jetty]
  (println "halt jetty")
  (.stop jetty))

(defn -main []
  (ig/init system-config))

(comment
  (def system (ig/init system-config))
  (ig/halt! system)
  )