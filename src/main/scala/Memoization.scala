import cats._
import cats.effect._
import cats.effect.implicits._
import cats.implicits._

import scala.concurrent.duration.DurationInt

object Memoization extends IOApp {
  case class Client(name: String, emailAddress: String)
  case class Email(body: String, recipients: List[String])

  trait EmailTemplates {
    def buildEmailForClient(templateId: String, client: Client): Email
  }

  // Long running computation
  def loadEmailTemplates(): IO[EmailTemplates] =
    IO.sleep(5.seconds) *>
    IO.println("Loading email templates...") *>
    IO.pure(new EmailTemplates {
      override def buildEmailForClient(templateId: String, client: Client): Email = {
        if(templateId == "negative-balance") Email(s"Dear ${client.name}: your account has a negative balance", List(client.emailAddress))
        else                                 Email(s"Dear ${client.name}: there is a problem with your account", List(client.emailAddress))
      }
    })

  trait Error extends Throwable
  object NegativeBalance extends Error
  object AccountExpired extends Error

  def processClient(client: Client): IO[Unit] =
    IO.println(s"Processing ${client.name}")
    //IO.raiseError(NegativeBalance)

  def sendEmail(email: Email): IO[Unit] =
  IO.println("Sending email")


  // Process each client
  // If there is anything wrong, build the corresponding email using the email templates
  def processClients(clients: List[Client]): IO[Unit] = {
    loadEmailTemplates().flatMap { templates =>
      clients.traverse_ { client =>
        processClient(client).handleErrorWith {
          case NegativeBalance =>
            val email = templates.buildEmailForClient("negative-balance", client)
            sendEmail(email)
          case _ =>
            val email = templates.buildEmailForClient("generic-error", client)
            sendEmail(email)
        }
      }
    }
  }

  def processClients2(clients: List[Client]): IO[Unit] = {
    clients.traverse_ { client =>
      processClient(client).handleErrorWith { error =>
        loadEmailTemplates().flatMap { templates =>
          error match {
            case NegativeBalance =>
              val email = templates.buildEmailForClient("negative-balance", client)
              sendEmail(email)
            case _ =>
              val email = templates.buildEmailForClient("generic-error", client)
              sendEmail(email)
          }
        }
      }
    }
  }

  def processClients3(clients: List[Client]): IO[Unit] = {
    loadEmailTemplates().memoize.flatMap { templatesIO =>
      clients.traverse_ { client =>
        processClient(client).handleErrorWith { error =>
          templatesIO.flatMap { templates =>
            error match {
              case NegativeBalance =>
                val email = templates.buildEmailForClient("negative-balance", client)
                sendEmail(email)
              case _ =>
                val email = templates.buildEmailForClient("generic-error", client)
                sendEmail(email)
            }
          }
        }
      }
    }
  }

  override def run(args: List[String]): IO[ExitCode] = {
    val clients = List(Client("Leandro", "leandro@mail.com"), Client("Martin", "martin@mail.com"))
    processClients3(clients).as(ExitCode.Success)
  }
}