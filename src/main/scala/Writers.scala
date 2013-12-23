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

import RDFMediaTypes._
import org.w3.banana.jena.{JenaRDFWriter, Jena}
import org.w3.banana.sesame.{Sesame, SesameRDFWriter}
import org.w3.banana.{RDFWriter, RDF}
import scala.collection.immutable.HashMap
import spray.http.MediaType

/**
 * Created by hjs on 23/12/2013.
 */

trait Writers[Rdf<:RDF] {
  def getWriterFor(mediaType: MediaType): Option[RDFWriter[Rdf,_]]
}

object JenaWriters extends Writers[Jena] {
  import JenaRDFWriter._
  val writers = HashMap(`text/turtle`->turtleWriter,
                         `application/n-triples`->turtleWriter,
                        `text/n3`->turtleWriter,
                        `application/rdf+xml`->rdfxmlWriter)

  def getWriterFor(mediaType: MediaType) = writers.get(mediaType)
}

object SesameWriters extends Writers[Sesame] {
  import SesameRDFWriter._
  val writers = HashMap(`text/turtle`->turtleWriter,
                        `application/n-triples`->turtleWriter,
                        `text/n3`->turtleWriter,
                        `application/rdf+xml`->rdfxmlWriter)

  def getWriterFor(mediaType: MediaType) = writers.get(mediaType)
}