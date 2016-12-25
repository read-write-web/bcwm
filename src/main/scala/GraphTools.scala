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

import java.io.StringReader
import java.net.{URL => jURL}
import java.nio.charset.Charset

import JenaGraphTools.utf8
import RDFMediaTypes._
import akka.http.scaladsl.model.{ContentTypeRange, HttpEntity}
import akka.http.scaladsl.unmarshalling.Unmarshaller
import akka.http.scaladsl.util.FastFuture
import org.w3.banana.RDF
import org.w3.banana.jena.Jena
import org.w3.banana.jena.io.JenaRDFReader
import org.w3.banana.sesame.Sesame

import scala.util.Try


/**
 * Created by hjs on 22/12/2013.
 */
trait GraphTools[Rdf<:RDF] {

   /** This returns a real Graph - no relative URIs */
   implicit def GraphUnmarshaller(base: jURL): Unmarshaller[HttpEntity,Rdf#Graph]

   implicit val Writers: Writers[Rdf]

  /**
   * todo: implement this
   * @return might return a Graph with relative URIs
   */

//   implicit def ResponseGraphUnmarshaller: FromResponseUnmarshaller[Rdf#Graph]

//  protected def inputStream(data: HttpData.NonEmpty, charset: Option[HttpCharset] ) = {
//    new ByteArrayInputStream(data.toByteArray)
//  }


}


object JenaGraphTools extends GraphTools[Jena] {
  import RDFMediaTypes._
  val utf8 = Charset.forName("UTF-8")

  implicit val Writers = JenaWriters

  /**
   * todo: implement this
   * @return might return a Graph with relative URIs
   */
//  implicit def ResponseGraphUnmarshaller: FromResponseUnmarshaller[Jena#Graph] = ???

  val pureRdfMediaRanges: List[ContentTypeRange] = RDFMediaTypes.pureRdfTypes.map{ mediaType =>
       ContentTypeRange(mediaType)
  }

  implicit def GraphUnmarshaller(base: jURL): Unmarshaller[HttpEntity,Jena#Graph] = {
    val rdfUnmarshaller = Unmarshaller.byteArrayUnmarshaller flatMapWithInput { (httpEntity, bytes) =>
      def stringReader = new StringReader(
        new String(
          bytes,
          httpEntity.contentType.charsetOption match {
            case Some(charSet) => Try(Charset.forName(charSet.value)).getOrElse(utf8)
            case None => utf8
          })
      )

      httpEntity.getContentType().mediaType match {
        case `application/rdf+xml` => FastFuture(
          JenaRDFReader.rdfxmlReader.read(stringReader, base.toString)
        )
        case `text/turtle` => FastFuture(
          JenaRDFReader.turtleReader.read(stringReader, base.toString)
        )
        case `application/n-triples` => FastFuture(
          JenaRDFReader.turtleReader.read(stringReader, base.toString)
        )
        case t => FastFuture.failed(
          Unmarshaller.UnsupportedContentTypeException(`application/rdf+xml`, `text/turtle`, `application/n-triples`)
        )
      }
    }
    //this should mean that if we don't get the right Content-Type, we don't even have to look at the bytes which chunked
    rdfUnmarshaller.forContentTypes(`application/rdf+xml`,`text/turtle`,`application/n-triples`)
  }

}

object SesameGraphTools extends GraphTools[Sesame] {


  val pureRdfMediaRanges: List[ContentTypeRange] = RDFMediaTypes.pureRdfTypes.map{ mediaType =>
    ContentTypeRange(mediaType)
  }

  implicit def GraphUnmarshaller(base: jURL): Unmarshaller[HttpEntity,Sesame#Graph] = {
    val rdfUnmarshaller = Unmarshaller.byteArrayUnmarshaller flatMapWithInput { (httpEntity, bytes) =>
      def stringReader = new StringReader(
        new String(
          bytes,
          httpEntity.contentType.charsetOption match {
            case Some(charSet) => Try(Charset.forName(charSet.value)).getOrElse(utf8)
            case None => utf8
          })
      )

      httpEntity.getContentType().mediaType match {
        case `application/rdf+xml` => FastFuture(
          Sesame.rdfXMLReader.read(stringReader, base.toString)
        )
        case `text/turtle` => FastFuture(
          Sesame.turtleReader.read(stringReader, base.toString)
        )
        case `application/n-triples` => FastFuture(
          Sesame.turtleReader.read(stringReader, base.toString)
        )
        case t => FastFuture.failed(
          Unmarshaller.UnsupportedContentTypeException(`application/rdf+xml`, `text/turtle`, `application/n-triples`)
        )
      }
    }
    //this should mean that if we don't get the right Content-Type, we don't even have to look at the bytes which chunked
    rdfUnmarshaller.forContentTypes(`application/rdf+xml`,`text/turtle`,`application/n-triples`)
  }



  implicit val Writers = SesameWriters

  /**
   * todo: implement this
   * @return might return a Graph with relative URIs
   */
//  implicit def ResponseGraphUnmarshaller = ???
}
