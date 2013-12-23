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

import spray.http.MediaTypes._
import spray.http.{MediaTypes, MediaType}


/**
 * Created by hjs on 22/12/2013.
 */
object RDFMediaTypes {

  import MediaTypes.{`text/html`, `application/xhtml+xml`}

  def pureRdfTypes = `text/turtle` :: `application/n-triples` :: `application/rdf+xml` :: `text/n3` :: `text/rdf+n3` :: Nil
  def htmlRdfTypes =  `text/html` :: `application/xhtml+xml` :: Nil
  def rdfMimeTypes =   pureRdfTypes ::: htmlRdfTypes

  def sparqlMimeTypes = `application/sparql-query` :: `application/sparql-update` ::
    `application/sparql-results+xml` :: `application/sparql-results+json` :: Nil


  val `text/n3` = MediaType
    .custom(
      mainType = "text",
      subType = "n3",
      compressible = true,
      binary = false,
      fileExtensions = Seq("n3")
           )

  val `text/rdf+n3` = MediaType
    .custom(mainType = "text",
      subType = "rdf+n3",
      compressible = true,
      binary = false,
      fileExtensions = Seq("n3old"))

  //
  val `text/turtle` = MediaType
    .custom(
      mainType = "text",
      subType = "turtle",
      compressible = true,
      binary = false,
      fileExtensions = Seq("ttl", "turtle")
           )

  // http://www.w3.org/TR/n-triples/
  val `application/n-triples` = MediaType
    .custom(
      mainType = "application",
      subType = "n-triples",
      compressible = true,
      binary = false,
      fileExtensions = Seq("ntriples")
           )

  val `application/rdf+xml` = MediaType
    .custom(
      mainType = "application",
      subType = "rdf+xml",
      compressible = true,
      binary = false,
      fileExtensions = Seq("rdf")
           )

  val `application/sparql-query` = MediaType
    .custom(
      mainType = "application",
      subType = "sparql-query",
      compressible = true,
      binary = false,
      fileExtensions = Seq("sparql-query")
           )

  val `application/sparql-update` = MediaType
    .custom(
      mainType = "application",
      subType = "sparql-update",
      compressible = true,
      binary = false,
      fileExtensions = Seq("sparql-update")
           )

  val `application/sparql-results+xml` = MediaType
    .custom(
      mainType = "application",
      subType = "sparql-results+xml",
      compressible = true,
      binary = false,
      fileExtensions = Seq("sparql-results+xml")
           )

  val `application/sparql-results+json` = MediaType
    .custom(
      mainType = "application",
      subType = "sparql-results+json",
      compressible = true,
      binary = false,
      fileExtensions = Seq("sparql-results+json")
           )

  for (tp <- pureRdfTypes ++ sparqlMimeTypes) {
    MediaTypes.register(tp)
  }



}