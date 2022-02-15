import cats._
import cats.effect._
import cats.implicits._
import cats.effect.implicits._

case object TaglessFinalApp extends IOApp {
  case class User(username: String, age: Int)
  object User {
    implicit val showUser = Show.show[User](u => s"${u.username} is ${u.age} years old")
  }
  trait UserRepo {
    def addUser(user: User): IO[Unit]
    def getUsers(): IO[List[User]]
  }

  object UserRepo {
    def impl: IO[UserRepo] =
      Ref.of[IO, Map[String, User]](Map.empty).map { ref =>
        new UserRepo {
          override def addUser(user: User): IO[Unit] =
            ref.update(_ + (user.username -> user))

          override def getUsers(): IO[List[User]] =
            ref.get.map(_.values.toList)
        }
      }
  }

  trait UserService {
    def printUserInfo(users: List[User]): IO[Unit]
  }

  object UserService {
    def impl: UserService = new UserService {
      override def printUserInfo(users: List[User]): IO[Unit] = {
        users.parTraverse_ { user =>
          IO.println(user.show)
        }
      }
    }
  }

  override def run(args: List[String]): IO[ExitCode] = {
    for {
      userRepo <- UserRepo.impl
      userService = UserService.impl
      users = List(User("leandro", 18), User("eugenia", 32), User("majo", 25))
      _ <- users.traverse_(userRepo.addUser)
      savedUsers <- userRepo.getUsers()
      _ <- userService.printUserInfo(savedUsers)
    } yield ExitCode.Success
  }
}