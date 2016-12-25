import akka.http.scaladsl.model.{MediaType, MediaTypes}
import akka.http.scaladsl.settings.{ClientConnectionSettings, ConnectionPoolSettings, ParserSettings, ServerSettings}

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


/**
 * Created by hjs on 22/12/2013.
 */
object RDFMediaTypes {

  import akka.http.scaladsl.model.MediaTypes.{`application/xhtml+xml`, `text/html`}

  def pureRdfTypes = `text/turtle` :: `application/n-triples` :: `application/rdf+xml` :: `text/n3` :: `text/rdf+n3` :: Nil
  def htmlRdfTypes =  `text/html` :: `application/xhtml+xml` :: Nil
  def rdfMimeTypes =   pureRdfTypes ::: htmlRdfTypes

  def sparqlMimeTypes = `application/sparql-query` :: `application/sparql-update` ::
    `application/sparql-results+xml` :: `application/sparql-results+json` :: Nil


  val `text/n3` = MediaType
    .customWithOpenCharset(
      mainType = "text",
      subType = "n3",
      fileExtensions = List("n3")
    )

  val `text/rdf+n3` = MediaType
    .customWithOpenCharset(
      mainType = "text",
      subType = "rdf+n3",
      fileExtensions = List("n3")
    )
  
  //
  val `text/turtle`: MediaType = MediaType
    .customWithOpenCharset(
      mainType = "text",
      subType = "turtle",
      fileExtensions = List("ttl", "turtle")
    )

  // http://www.w3.org/TR/n-triples/
  val `application/n-triples`: MediaType = MediaType
    .customWithOpenCharset(
      mainType = "application",
      subType = "n-triples",
      fileExtensions = List("ntriples")
           )

  val `application/rdf+xml`: MediaType = MediaType
    .customWithOpenCharset(
      mainType = "application",
      subType = "rdf+xml",
      fileExtensions = List("rdf")
    )

  val `application/sparql-query`: MediaType = MediaType
    .customWithOpenCharset(
      mainType = "application",
      subType = "sparql-query",
      fileExtensions = List("sparql-query")
    )

  val `application/sparql-update`: MediaType = MediaType
    .customWithOpenCharset(
      mainType = "application",
      subType = "sparql-update",
      fileExtensions = List("sparql-update")
    )

  val `application/sparql-results+xml`: MediaType = MediaType
    .customWithOpenCharset(
      mainType = "application",
      subType = "sparql-results+xml",
      fileExtensions = List("sparql-results+xml")
    )

  val `application/sparql-results+json`: MediaType = MediaType
    .customWithOpenCharset(
      mainType = "application",
      subType = "sparql-results+json",
      fileExtensions = List("sparql-results+json")
    )

  def parserSettings(parserSettings: ParserSettings) = parserSettings.withCustomMediaTypes(pureRdfTypes ++ sparqlMimeTypes :_*)
  def serverSettings(serverSettings: ServerSettings) = serverSettings.withParserSettings(serverSettings.parserSettings)
  def clientSettings(clientConnectionSettings: ClientConnectionSettings): ClientConnectionSettings =
    clientConnectionSettings.withParserSettings(clientConnectionSettings.parserSettings)
  def clientPoolSettings(cps: ConnectionPoolSettings): ConnectionPoolSettings = cps.withConnectionSettings(clientSettings(cps.connectionSettings))

  def forExtensionOption(ext: String): Option[MediaType] =
    rdfMimeTypes.find(_.fileExtensions.contains(ext)) orElse {
      MediaTypes.forExtensionOption(ext)
    }

}