import cats._
import cats.effect._
import cats.implicits._
import cats.effect.implicits._
import cats.effect.unsafe.implicits.global

import scala.concurrent.duration.DurationInt

//    case class Person(id: Long, name: String)
//    def getPerson(id: Long): IO[Person] = IO.sleep(100.millis) *> IO.pure(Person(id, s"Person no $id"))
//    val personIds = (1 to 50).toList
//    personIds.parTraverse(id => getPerson(id)).flatTap(IO.println).as(ExitCode.Success)

case class Image(bytes: List[Byte])

def fetchHttp(n: Int): IO[List[Image]] =
  IO.sleep(100.millis) *> IO.pure((1 to n).toList.map(i => Image(List(i.toByte))))

def fetchDb(n: Int): IO[List[Image]] =
  IO.sleep(100.millis) *> IO.pure((1 to n).toList.map(i => Image(List((10 + i).toByte))))

val n = 50
(fetchHttp(n), fetchDb(n)).mapN { case (httpImages, dbImages) =>
  httpImages ++ dbImages
}.flatTap(IO.println).as(ExitCode.Success)