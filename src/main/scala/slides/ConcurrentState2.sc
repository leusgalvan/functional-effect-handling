import cats.effect._
import cats.effect.implicits._
import cats._
import cats.implicits._
import cats.effect.unsafe.implicits.global

case class Image(name: String, bytes: Array[Byte])

def loadImage(name: String): IO[Image] = IO.println(s"Loading image $name") *> IO.pure(Image(name, Array()))

def getImage(name: String, cacheRef: Ref[IO, Map[String, Image]]): IO[Image] = {
  cacheRef.get.flatMap { cache =>
    cache.get(name) match {
      case Some(img) => img.pure[IO]
      case None =>
        loadImage(name).flatTap { newImg =>
          cacheRef.update(_ + (name -> newImg))
        }
    }
  }
}

def program: IO[List[Image]] = for {
  cacheRef  <- Ref.of[IO, Map[String, Image]](Map.empty)
  imageNames = List("cat", "dog", "cat")
  images    <- imageNames.traverse(getImage(_, cacheRef))
} yield images

program.unsafeRunSync()