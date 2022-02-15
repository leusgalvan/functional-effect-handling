import cats.effect._
import cats.effect.implicits._
import cats._
import cats.implicits._

import scala.concurrent.duration.DurationInt

object DeferredApp extends IOApp {
  case class Item(id: Long)
  // Long running
  def loadItems(): IO[List[Item]] =
    // IO.raiseError(new Exception("Failed to load items"))
    (IO.println("Loading items") *>
    IO.sleep(2.seconds) *>
    IO.println("Items loaded"))
      .as(List(Item(1), Item(2)))

  // Long running
  def initUi(): IO[Unit] =
    IO.println("Initializing UI") *>
    IO.sleep(2.seconds) *>
    IO.println("UI initialized")


  def showItems(items: List[Item]): IO[Unit] = IO.println("Showing items")

  def showError(): IO[Unit] = IO.println("Showing error")

//  def setupUi(): IO[Unit] =
//    initUi() *> loadItems().flatMap(items => showItems(items)).handleErrorWith(_ => showError())

//  def setupUi(): IO[Unit] =
//    (initUi(), loadItems())
//      .parMapN { case (_, items) =>
//        showItems(items)
//      }
//      .flatten
//      .handleErrorWith(_ => showError())

//  def setupUi(): IO[Unit] =
//    (initUi(), loadItems().attempt)
//      .parMapN {
//        case (_, Right(items)) => showItems(items)
//        case (_, Left(error)) => showError()
//      }
//      .flatten

  def handleUi(defItems: Deferred[IO, Either[Throwable, List[Item]]]): IO[Unit] =
    initUi() *> defItems.get.flatMap {
      case Right(items) => showItems(items)
      case Left(e) => showError()
    }

  def handleItems(defItems: Deferred[IO, Either[Throwable, List[Item]]]): IO[Unit] =
    loadItems()
      .flatMap(items => defItems.complete(Right(items)))
      .handleErrorWith(e => defItems.complete(Left(e)))
      .void

  def setupUi(): IO[Unit] =
    Deferred[IO, Either[Throwable, List[Item]]].flatMap { defItems =>
      List(handleUi(defItems), handleItems(defItems)).parSequence_
    }

  override def run(args: List[String]): IO[ExitCode] =
    setupUi().as(ExitCode.Success)
}