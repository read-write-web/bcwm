
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

import java.net.URL
import java.nio.file.FileSystems

import RDFMediaTypes._
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers.Accept
import akka.http.scaladsl.settings.ConnectionPoolSettings
import akka.http.scaladsl.unmarshalling.Unmarshaller
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.FileIO
import org.w3.banana.jena.Jena
import org.w3.banana.sesame.Sesame
import org.w3.banana.{RDF, RDFOps}

import scala.concurrent.{Await, ExecutionContext, Future}
import scala.util.{Failure, Success}



class Fetcher[Rdf<:RDF](implicit val ops: RDFOps[Rdf],  graphTools: GraphTools[Rdf]) {
  def fetch(config: bcwm.Config) {
    System.err.println(s"config=$config")
   import scala.concurrent.duration._
    implicit val system = ActorSystem()
    implicit val materializer = ActorMaterializer()


    implicit val ec: ExecutionContext = system.dispatcher // execution context for futures
    // execution context for futures
    implicit val timeout = 1 minute
    val uriStr = config.url.get.toString.split("#").head
    val urlDoc = new URL(uriStr)
    val base = if (urlDoc.getProtocol.equalsIgnoreCase("file")) {
      config.base.getOrElse(urlDoc)
    } else urlDoc

    implicit val jgt: Unmarshaller[HttpEntity,Rdf#Graph] = graphTools.GraphUnmarshaller(base)

    def fetch(url: Uri): Future[HttpResponse] = {
     Http().singleRequest(
       settings = RDFMediaTypes.clientPoolSettings(ConnectionPoolSettings(system)),
       request = HttpRequest(
         uri = url,
         headers = List(
           Accept(
             `text/turtle`.withQValue(0.9),
             `application/n-triples`.withQValue(0.8),
             `application/rdf+xml`.withQValue(0.7),
             MediaTypes.`text/html`.withQValue(0.3)
           )
         )
       )
     )
    }



    val response: Future[Rdf#Graph] = if (!urlDoc.getProtocol.equalsIgnoreCase("file")) {
      fetch(Uri(urlDoc.toString)).flatMap(httpresponse=> jgt(httpresponse.entity))
    } else {
        jgt(HttpEntity(
          `application/rdf+xml`.asInstanceOf[MediaType.WithOpenCharset].toContentType(HttpCharsets.`UTF-8`),
          FileIO.fromPath(FileSystems.getDefault().getPath(urlDoc.getPath)))
        )
    }

    response.onComplete {
      case Success(graph) => {
        for (wr <- graphTools.Writers.getWriterFor(config.outType)) {
          wr.write(graph, System.out, "")
          System.out.flush()
        }
      }
      case Failure(e) => {
        System.err.println(s"error with doc from $uriStr: $e")
      }
    }
    Await.result(response, 2 minutes)
    system.shutdown()
  }


}

object SesameFetcher extends Fetcher[Sesame]()(Sesame.ops,SesameGraphTools)

object JenaFetcher extends Fetcher[Jena]()(Jena.ops,JenaGraphTools)