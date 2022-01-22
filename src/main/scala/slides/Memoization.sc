import cats.effect._
import cats.effect.implicits._
import cats.implicits._
import cats.effect.unsafe.implicits.global

case class Client(emailAddress: String)

case class Email(body: String, recipients: List[String])

trait EmailTemplates {
  def buildEmailForClient(templateId: String, client: Client): Email
}

def loadEmailTemplates(): IO[EmailTemplates] = {
  IO.println("Loading templates...") *>
  IO(
    new EmailTemplates {
      override def buildEmailForClient(templateId: String, client: Client): Email =
        Email("", Nil)
    }
  )
}

def processClient(client: Client): IO[Unit] = IO.raiseError(new Exception("boom"))

def sendMail(email: Email): IO[Unit] = IO.println("Sending mail...")

def processClients(clients: List[Client]): IO[Unit] = {
  loadEmailTemplates().flatMap { emailTemplates =>
    clients.parTraverse { client =>
      processClient(client).handleErrorWith { _ =>
        val email = emailTemplates.buildEmailForClient("...", client)
        sendMail(email)
      }
    }.void
  }
}

def processClients2(clients: List[Client]): IO[Unit] = {
  clients.parTraverse { client =>
    processClient(client).handleErrorWith { _ =>
      loadEmailTemplates().flatMap { emailTemplates =>
        val email = emailTemplates.buildEmailForClient("...", client)
        sendMail(email)
      }
    }
  }.void
}

def processClients3(clients: List[Client]): IO[Unit] = {
  loadEmailTemplates().memoize.flatMap { emailTemplatesIO =>
    clients.parTraverse { client =>
      processClient(client).handleErrorWith { _ =>
        emailTemplatesIO.flatMap { emailTemplates =>
          val email = emailTemplates.buildEmailForClient("...", client)
          sendMail(email)
        }
      }
    }.void
  }
}

//processClients(List(Client("a"), Client("b"), Client("c"))).unsafeRunSync()
//processClients2(List(Client("a"), Client("b"), Client("c"))).unsafeRunSync()
processClients3(List(Client("a"), Client("b"), Client("c"))).unsafeRunSync()