import RDFMediaTypes._
import akka.actor.ActorSystem
import org.w3.banana.jena.Jena
import org.w3.banana.sesame.Sesame
import org.w3.banana.{RDFOps, RDF}
import scala.concurrent.{Await, Future, ExecutionContext}
import scala.util.{Failure, Success}
import spray.http.HttpHeaders.Accept
import spray.http.HttpRequest
import spray.http.MediaTypes._

/*
 * Copyright 2013 Henry Story
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

/**
 * Created by hjs on 23/12/2013.
 */
class Fetcher[Rdf<:RDF](implicit val ops: RDFOps[Rdf],  graphTools: GraphTools[Rdf]) {
   def fetch(config: bcwm.Config) {
     System.err.println(s"config=$config")
     import spray.httpx.unmarshalling._
     import spray.client.pipelining._
     import scala.concurrent.duration._
     implicit val system = ActorSystem()

     implicit val ec: ExecutionContext = system.dispatcher // execution context for futures
     // execution context for futures
     implicit val timeout = 1 minute
     val uriStr = config.url.get.toString.split("#").head

     implicit val jgt: Unmarshaller[Rdf#Graph] = graphTools.GraphUnmarshaller(new java.net.URI(uriStr))

     val pipeline: HttpRequest => Future[Deserialized[Rdf#Graph]] = {
       addHeader(Accept(`text/turtle`.withQValue(0.9),
                         `application/n-triples`.withQValue(0.8),
                         `application/rdf+xml`.withQValue(0.7),
                         `text/html`.withQValue(0.3)))
         .~>(sendReceive)
         .~>(unmarshal[Deserialized[Rdf#Graph]])
     }
     val req = Get(uriStr)
     val response: Future[Deserialized[Rdf#Graph]] = pipeline(req)
     response.onComplete{
       case Success(Right(graph)) => {
         for (wr<-graphTools.Writers.getWriterFor(config.outType)) {
           wr.write(graph,System.out,"")
         }
       }
       case Success(Left(serialError)) => {
         System.err.println(s"problem serialising document from $uriStr: $serialError")
       }
       case Failure(e) => {
         System.err.println(s"error fetching document from $uriStr: $e")
       }
     }
     Await.result(response,2 minutes)
     system.shutdown()

   }
}

object SesameFetcher extends Fetcher[Sesame]()(Sesame.ops,SesameGraphTools)

object JenaFetcher extends Fetcher[Jena]()(Jena.ops,JenaGraphTools)