import cats._
import cats.data.Validated.{Invalid, Valid}
import cats.data.{EitherT, Validated, ValidatedNec}
import cats.implicits._
import cats.effect._
import cats.effect.implicits._

import java.io.{FileInputStream, FileNotFoundException}
import scala.util.control.NonFatal

object ErrorHandlingAppExercise extends IOApp {
  object Validations {
    type Valid[A] = ValidatedNec[String, A]

    def validateExtension(filename: String): Valid[String] =
      Validated.condNec(
        filename.endsWith(".txt"),
        filename,
        s"Invalid extension for file $filename. Only txt files allowed."
      )

    def validateLength(expectedLength: Int, s: String): Valid[String] =
      Validated.condNec(
        s.length <= expectedLength,
        s,
        s"The string $s is over $expectedLength characters long"
      )

    def validateFileName(filename: String): Valid[String] =
      (validateExtension(filename), validateLength(128, filename)).mapN { case (_, s) => s }
  }

  object Service {

    sealed trait DomainError
    case class TextFileNotFound(filename: String) extends DomainError

    def countWords(contents: String): Int =
      contents.split("\\W+").length

    def loadFile(filename: String): IO[Either[DomainError, String]] = {
      def loadFileContents(filename: String): IO[Array[Byte]] = {
        IO.blocking(new FileInputStream(filename))
          .bracket { fis =>
            IO.blocking(
              Iterator
                .continually(fis.read)
                .takeWhile(_ != -1)
                .map(_.toByte)
                .toArray
            )
          } { fis =>
            IO.blocking(fis.close())
          }
      }

      /* 1 */
      // Implement a load file function that loads all the contents of a file into a String
      // If the file does not exist, capture that with the domain error TextFileNotFound
//      loadFileContents(filename).map { bytes =>
//        new String(bytes).asRight[DomainError]
//      }.handleErrorWith {
//        case _: FileNotFoundException => TextFileNotFound(filename).asLeft[String].pure[IO]
//        case t: Throwable => IO.raiseError(t)
//      }
      IO.raiseError(new StackOverflowError("boom"))
    }
  }

  override def run(args: List[String]): IO[ExitCode] = {
    import Validations._
    import Service._
    /* 2 */

    // Use args(0) as the filename to load (assume it is always provided)
    // Validate the filename, and output any problem via the console
    // If loaded successfully, output the number of words in the file (use Service.countWords)
    // If a domain error occurs, communicate it to the user via the console
    // If a technical, nonfatal error occurs, output "Something went wrong" to the console
    // If a fatal error occurs, just reraise it and let everything fail
    (validateFileName(args(0)) match {
      case Valid(filename) =>
        loadFile(filename).flatMap {
          case Right(contents) =>
            val noWords = countWords(contents)
            IO.println(s"Word count: $noWords").as(ExitCode.Success)

          case Left(TextFileNotFound(filename)) =>
            IO.println(s"Text file not found: $filename").as(ExitCode.Error)
        }
      case Invalid(errors) =>
        IO.println(errors.mkString_("\n")).as(ExitCode.Error)
    }).handleErrorWith {
      case NonFatal(e) => IO.println("Something went wrong").as(ExitCode.Error)
      case t: Throwable => IO.raiseError(t)
    }
  }

}