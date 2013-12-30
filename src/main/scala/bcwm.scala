/**
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

import java.io.File
import java.net.{URI, URL}
import scala.Some
import spray.http._


/**
 * b•cwm : re-implementation of TimBL's cwm in Scala using banana library
 *
 * http://www.w3.org/2000/10/swap/doc/cwm.html
 *
 */
object bcwm {
  import RDFMediaTypes._

  //see scopt lib: https://github.com/scopt/scopt
  val parser = new scopt.OptionParser[Config]("bcwm") {
    head("banana cwm","0.1")

    opt[String]('i', "in")
      .optional()
      .valueName("{ttl,nt,rdf,html}")
      .action { (ext, c) =>
      c.copy(inType = MediaTypes.forExtension(ext))
    }.validate { ext =>
      MediaTypes.forExtension(ext) match {
        case None => failure("Option --in must have as value one of 'ttl', 'ntriples', 'rdf' or 'html'")
        case Some(mime) if !rdfMimeTypes.contains(mime) => failure(s"Option --in $mime does not specify an allowed mime type")
        case _ => success
      }
    }.text("input document type")

    opt[String]("lib")
      .optional()
      .valueName("{sesame,jena}")
      .action { (name, c) =>
      c.copy(lib=name)
    }.validate{ name =>
      if (name == "sesame" || name == "jena") success
      else failure("Option --lib must be one of 'jena' or 'sesame'")
    }.text("parsing library")

    opt[String]('o', "out")
      .optional()
      .valueName("{ttl,nt,rdf}")
      .action { (ext, c) => c.copy(outType = MediaTypes.forExtension(ext).getOrElse(`text/turtle`)) }
      .validate { ext =>
      MediaTypes.forExtension(ext) match {
        case None => failure("Option --in must have as value one of 'ttl', 'ntriples', 'rdf' or 'html'")
        case Some(mime) if !pureRdfTypes.contains(mime) => failure(s"Option --in $mime does not specify an allowed mime type")
        case _ => success
      }
    }.text("output document type")

    opt[URI]('b',"base")
      .optional()
      .valueName("<url>")
      .action{ (url, c) => c.copy(base=Some(url.toURL))}
      .text("set base url for local documents")

    arg[URI]("<url|file>")
      .required()
      .action { (uri, c) =>
        c.copy(url =
          Some(if (uri.isAbsolute) uri.toURL else new URL(new File(".").toURI.toURL, uri.getPath)))
      }
      .text("relative url for local files or full url for remote documents")

    help("help")
      .text("""
              | bcwm aims to implement the commands and functionality of Tim Berners Lee and Dan Connolly's cwm .
              | http://www.w3.org/2000/10/swap/doc/cwm.html
            """.stripMargin)

  }


  def main(args: Array[String]) {
    parser.parse(args, Config()) map { config =>
      if (config.lib.equalsIgnoreCase("sesame"))
        SesameFetcher.fetch(config)
      else JenaFetcher.fetch(config)
    } getOrElse {
      println("wrong arguments!")
      // arguments are bad, error message will have been displayed
    }
  }


  case class Config(inType: Option[MediaType]=None,
    outType: MediaType=`text/turtle`,
    url: Option[URL]=None,
    base: Option[URL]=None,
    lib: String="sesame")

}



