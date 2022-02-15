import cats.effect._
import cats.implicits._

import java.io._
import java.net.{HttpURLConnection, URL}

object ResourceSafetyExerciseSolution extends IOApp {
  def readerResource(connection: HttpURLConnection): Resource[IO, BufferedReader] = {
    Resource.fromAutoCloseable(IO.blocking(new BufferedReader(new InputStreamReader(connection.getInputStream))))
  }

  def connectionResource(targetURL: String): Resource[IO, HttpURLConnection] = {
    Resource.make(createConnection(targetURL))(conn => IO.blocking(conn.disconnect()))
  }

  def resources(targetURL: String): Resource[IO, (HttpURLConnection, BufferedReader)] = {
    for {
      connection <- connectionResource(targetURL)
      reader <- readerResource(connection)
    } yield (connection, reader)
  }

  def createConnection(targetURL: String): IO[HttpURLConnection] =
    IO.blocking {
      val connection = new URL(targetURL).openConnection().asInstanceOf[HttpURLConnection]
      connection.setRequestMethod("GET")
      connection
    }

  def readOutput(reader: BufferedReader): IO[String] =
    IO.blocking {
      Iterator
        .continually(reader.readLine)
        .takeWhile(_ != null)
        .mkString("\n")
    }

  def httpGet(targetURL: String): IO[String] = {
    resources(targetURL).use { case (_, reader) =>
      readOutput(reader)
    }
  }

  override def run(args: List[String]): IO[ExitCode] = {
    httpGet("http://www.google.com")
      .flatTap(IO.println)
      .as(ExitCode.Success)
  }
}
