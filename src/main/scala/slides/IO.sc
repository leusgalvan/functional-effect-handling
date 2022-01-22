import cats.effect.IO

def greet(name: String): Unit =
  println(s"Hello $name!")

greet("Leandro")

def greet(name: String): IO[Unit] =
  IO.delay(println(s"Hello $name!"))

greet("Leandro").unsafeRunSync()