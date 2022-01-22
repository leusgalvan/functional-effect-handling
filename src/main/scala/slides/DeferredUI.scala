package slides

import cats.effect._
import cats.effect.implicits._
import cats._
import cats.data.StateT
import cats.implicits._
import cats.effect.unsafe.implicits.global

import scala.concurrent.duration.{DurationInt, FiniteDuration}

object DeferredUI {
  case class Item(id: Int)

  def loadItems(): IO[List[Item]] = (IO.println("Loading items") *> IO.sleep(2.seconds) *> IO.println("Items loaded")).as(List(Item(1), Item(2)))//IO.println("Loading items") *> IO.raiseError(new Exception("boom"))

  def initUi(): IO[Unit] = IO.println("Initializing UI") *> IO.sleep(2.seconds) *> IO.println("Finished UI")

  def showItems(items: List[Item]): IO[Unit] = IO.unit

  def showError(): IO[Unit] = IO.println("Showing error")

  def handleUI(defItems: Deferred[IO, List[Item]]): IO[Unit] =
    initUi() *> defItems.get.flatMap(showItems)

  def handleItems(defItems: Deferred[IO, List[Item]]): IO[Unit] =
    loadItems().flatMap(defItems.complete).void

  def setupUI(): IO[Unit] =
    Deferred[IO, List[Item]].flatMap { deferItems =>
      (handleUI(deferItems), handleItems(deferItems)).parSequence.void
    }


  def timeIt[A](expr: IO[A]): IO[FiniteDuration] = {
    for {
      start <- IO.monotonic
      _     <- expr
      end   <- IO.monotonic
    } yield end - start
  }

  def main(args: Array[String]): Unit = {
    timeIt(setupUI()).flatTap(d => IO.println(d.toSeconds)).unsafeRunSync()
  }
}
