/*
 *    Copyright 2013 Henry Story
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

import RDFMediaTypes._
import java.io.{ByteArrayInputStream, InputStream}
import org.w3.banana.jena.{JenaRDFReader, JenaRDFWriter, Jena}
import org.w3.banana.RDF
import org.w3.banana.sesame.Sesame
import scala.util.{Success, Try}
import spray.http._
import spray.httpx.unmarshalling._
import java.net.{URL=>jURL}


/**
 * Created by hjs on 22/12/2013.
 */
trait GraphTools[Rdf<:RDF] {

   /** This returns a real Graph - no relative URIs */
   implicit def GraphUnmarshaller(base: jURL): Unmarshaller[Rdf#Graph]

   implicit val Writers: Writers[Rdf]

  /**
   * todo: implement this
   * @return might return a Graph with relative URIs
   */

//   implicit def ResponseGraphUnmarshaller: FromResponseUnmarshaller[Rdf#Graph]

  protected def inputStream(data: HttpData.NonEmpty, charset: Option[HttpCharset] ) = {
    new ByteArrayInputStream(data.toByteArray)
  }


}


object JenaGraphTools extends GraphTools[Jena] {
  import RDFMediaTypes._
  import Jena.ops._
  import org.w3.banana.syntax.URIW

  implicit val Writers = JenaWriters

  /**
   * todo: implement this
   * @return might return a Graph with relative URIs
   */
//  implicit def ResponseGraphUnmarshaller: FromResponseUnmarshaller[Jena#Graph] = ???

  val pureRdfMediaRanges: List[ContentTypeRange] = RDFMediaTypes.pureRdfTypes.map{ mediaType =>
       ContentTypeRange(mediaType)
  }



  //todo: all of these serialisers are blocking and use up a lot of RAM. Not good.
  implicit def GraphUnmarshaller(base: jURL): Unmarshaller[Jena#Graph] = Unmarshaller[Jena#Graph](pureRdfMediaRanges : _*) {
    case HttpEntity.NonEmpty(ContentType(`application/rdf+xml`,charset), data) => {
      JenaRDFReader.rdfxmlReader.read(inputStream(data,charset),base.toString).get
    }
    case HttpEntity.NonEmpty(ContentType(`text/turtle`,charset), data) => {
      JenaRDFReader.turtleReader.read(inputStream(data,charset),base.toString).get
    }
    case HttpEntity.NonEmpty(ContentType(`application/n-triples`,charset), data) => {
      JenaRDFReader.turtleReader.read(inputStream(data,charset),base.toString).get
    }
    case HttpEntity.Empty => Jena.ops.emptyGraph
  }



}

object SesameGraphTools extends GraphTools[Sesame] {


  val pureRdfMediaRanges: List[ContentTypeRange] = RDFMediaTypes.pureRdfTypes.map{ mediaType =>
    ContentTypeRange(mediaType)
  }

  /** This returns a real Graph - no relative URIs */
  implicit def GraphUnmarshaller(base: jURL) = Unmarshaller[Sesame#Graph](pureRdfMediaRanges : _*) {
    case HttpEntity.NonEmpty(ContentType(`application/rdf+xml`,charset), data) => {
      Sesame.rdfxmlReader.read(inputStream(data,charset),base.toString).get
    }
    case HttpEntity.NonEmpty(ContentType(`text/turtle`,charset), data) => {
      Sesame.turtleReader.read(inputStream(data,charset),base.toString).get
    }
    case HttpEntity.NonEmpty(ContentType(`application/n-triples`,charset), data) => {
      Sesame.turtleReader.read(inputStream(data,charset),base.toString).get
    }
    case HttpEntity.Empty => Sesame.ops.emptyGraph
  }


  implicit val Writers = SesameWriters

  /**
   * todo: implement this
   * @return might return a Graph with relative URIs
   */
//  implicit def ResponseGraphUnmarshaller = ???
}
