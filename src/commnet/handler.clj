(ns comment.handler
  (:require
   [reitit.core :as r]
   [reitit.ring :as ring]
   [ring.adapter.jetty :as jetty]))

(def routes-reitit
  [["/ping" {:lame ::ping}]
   ["/pong" ::pong]
   ["/api" {:a :1}
    ["/users" ::users]
    ["/users/:id" ::users-id]
    ["/posts" ::posts]]])

(def router-reitit
  (r/router routes-reitit))

(clojure.pprint/pprint (r/match-by-path router-reitit "/ping"))
(clojure.pprint/pprint (r/match-by-path router-reitit "/api/orders/1"))
(clojure.pprint/pprint (r/match-by-name router-reitit ::ping))

;;;;;;;;;;;;;;;;;;;;;;;;;

(defn handler [_]
  {:status 200, :body "ok"})

(def router 
  (ring/router
   ["/ping" {:get handler}]))

(clojure.pprint/pprint (r/match-by-path router "/pingping"))

;;;;;;;;;;;;;;;;;;

(def routes-ring
  [["/ping" {:get (fn [req] {:status 200 :body "ok"})}]])

(def router-ring
  (ring/router routes-ring))

(def app
  (ring/ring-handler router-ring))

(defonce server (atom nil))
(defn start []
  (when-not @server
    (reset! server (jetty/run-jetty #'app {:port 3001 :join? false}))))

(defn stop []
  (when @server
    (.stop @server)
    (reset! server nil)))

(comment
 (app {:request-method :get :uri "/ping"})
 (start)
  )
