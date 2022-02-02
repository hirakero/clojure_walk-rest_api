(ns comment.handler
  (:require
   [reitit.core :as r]
   [reitit.ring :as ring]
   [reitit.swagger :as swagger]
   [reitit.swagger-ui :as swagger-ui]
   [reitit.ring.middleware.muuntaja :as muuntaja]
   [reitit.coercion.spec]
   [reitit.dev.pretty :as pretty]
   [reitit.ring.coercion :as coercion]
   [reitit.ring.middleware.exception :as exception]
   [muuntaja.core :as m]
   [ring.adapter.jetty :as jetty]))

(def ok (constantly {:status 200 :body "ok"}))

(def routes
  [["/swagger.json"
    {:get {:no-doc true
           :swagger {:info {:title "comment system API"
                            :description "comment system API"}}
           :handler (swagger/create-swagger-handler)}}]
   
   ["/comments"
    {:swagger {:tags ["comments"]}}

    [""
     {:get {:summary "get all comments"
            :handler ok}

      :post {:summary "create a new comment"
             :parameters {:body {:name string?
                                 :slug string?
                                 :text string?
                                 :parent-comment-id int?}}
             :responses {200 {:body string?}}
             :handler ok}}]

    ["/:slug"
     {:get {:summary "get comments by slug"
            :parameters {:path {:slug string?}}
            :handler ok}}]

    ["/id/:id"
     {:get {:summary "get a comments by id"
            :parameters {:path {:id int?}}
            :handler ok}

      :put {:summary "update a comment by the moderator"
            :parameters {:path {:id int?}}
            :handler ok}

      :delete {:summary "delete a comment by the moderator"
               :parameters {:path {:id int?}}
               :handler ok}}]]])

 (defn create-app  [db]
  (ring/ring-handler
   (ring/router routes
                {:exception pretty/exception
                 :data {:coercion reitit.coercion.spec/coercion
                        :muuntaja m/instance
                        :middleware [swagger/swagger-feature
                                     muuntaja/format-middleware
                                     exception/exception-middleware ;! coercion より先に
                                     coercion/coerce-request-middleware
                                     coercion/coerce-response-middleware]}})
   (ring/routes
    (swagger-ui/create-swagger-ui-handler
     {:path "/"}))))

