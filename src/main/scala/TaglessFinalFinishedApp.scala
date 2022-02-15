import cats._
import cats.effect._
import cats.effect.implicits._
import cats.effect.std.Console
import cats.implicits._

case object TaglessFinalFinishedApp extends IOApp {

  case class User(username: String, age: Int)

  object User {
    implicit val showUser = Show.show[User](u => s"${u.username} is ${u.age} years old")
  }

  trait UserRepo[F[_]] {
    def addUser(user: User): F[Unit]

    def getUsers(): F[List[User]]
  }

  object UserRepo {
    def impl[F[_]: Sync]: F[UserRepo[F]] =
      Ref.of[F, Map[String, User]](Map.empty).map { ref =>
        new UserRepo[F] {
          override def addUser(user: User): F[Unit] =
            ref.update(_ + (user.username -> user))

          override def getUsers(): F[List[User]] =
            ref.get.map(_.values.toList)
        }
      }
  }

  trait UserService[F[_]] {
    def printUserInfo(users: List[User]): F[Unit]
  }

  object UserService {
    def impl[F[_]: Console: Parallel]: UserService[F] = new UserService[F] {
      override def printUserInfo(users: List[User]): F[Unit] = {
        users.parTraverse_ { user =>
          Console[F].println(user.show)
        }
      }
    }
  }

  override def run(args: List[String]): IO[ExitCode] = {
    for {
      userRepo <- UserRepo.impl[IO]
      userService = UserService.impl[IO]
      users = List(User("leandro", 18), User("eugenia", 32), User("majo", 25))
      _ <- users.traverse_(userRepo.addUser)
      savedUsers <- userRepo.getUsers()
      _ <- userService.printUserInfo(savedUsers)
    } yield ExitCode.Success
  }
}