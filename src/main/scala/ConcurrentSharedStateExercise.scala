import cats._
import cats.implicits._
import cats.effect._
import cats.effect.implicits._

object ConcurrentSharedStateExercise extends IOApp {
  case class User(username: String, age: Int, friends: List[User])

  // use ref to hold the current oldest user
  def findOldest(user: User): IO[User] = {
    ???
  }

  override def run(args: List[String]): IO[ExitCode] = {
    val a = User("a", 60, Nil)
    val b = User("b", 35, Nil)
    val c = User("c", 45, Nil)
    val d = User("d", 50, List(a, b))
    val e = User("e", 55, List(c))
    val f = User("f", 15, List(d, e))

    findOldest(f).flatTap(IO.println).as(ExitCode.Success)
  }
}