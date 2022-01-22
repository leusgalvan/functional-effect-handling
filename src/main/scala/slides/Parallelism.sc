import cats.effect._
import cats.effect.implicits._
import cats.implicits._
import cats.effect.unsafe.implicits.global

import scala.concurrent.duration._

case class Person(id: Long, name: String)

class PersonService {
  def createPerson(name: String): IO[Person] = IO.pure(Person(1, name))

  def createAll(names: List[String]): IO[List[Person]] = {
    //names.traverse(createPerson)
    //names.parTraverse(createPerson)
    names.map(createPerson).sequence
  }
}

val repo = new PersonService
repo.createAll(List("Leo", "Euge"))
  .unsafeRunSync()

case class Quote(author: String, text: String)
class QuotesService {
  def kittenQuotes(n: Int): IO[List[Quote]] =
    IO(List(Quote("a1", "k1"), Quote("a2", "k2")))
  def puppiesQuotes(n: Int): IO[List[Quote]] =
  IO(List(Quote("a3", "p1"), Quote("a4", "p2")))

  def mixedQuotes(n: Int): IO[List[Quote]] =
    (kittenQuotes(n), puppiesQuotes(n)).parMapN { (kittens, puppies) =>
      kittens ++ puppies
    }
}

val quotesService = new QuotesService
quotesService.mixedQuotes(10).unsafeRunSync()

case class Image(data: List[Byte])
class ImagesService {
  def fetchFromDb(n: Int): IO[List[Image]] =
    IO.println("Starting db") >>
    IO.sleep(100.millis) >>
    IO(List(Image(List(1,2,3,4,5))))

  def fetchFromHttp(n: Int): IO[List[Image]] =
    IO.raiseError(new Throwable("boom"))

  def fetchFromFastest(n: Int): IO[List[Image]] = {
    IO.race(fetchFromDb(10), fetchFromHttp(10))
      .map(_.fold(identity, identity))
  }
}

val imagesService = new ImagesService
imagesService.fetchFromFastest(10).unsafeRunSync()