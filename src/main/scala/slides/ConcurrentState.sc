import cats.effect._
import cats.effect.implicits._
import cats._
import cats.implicits._
import cats.effect.unsafe.implicits.global

import java.time.{LocalDateTime, ZonedDateTime}

trait Json
case class ApiCredentials(username: String, password: String)
case class Token(value: String, expiresOn: ZonedDateTime) {
  def isExpired: IO[Boolean] = ???
}

def getFreshToken(credentials: ApiCredentials): IO[Token]

def doGet(uri: String, token: Token): IO[Json]

case class Account(id: String, owner: String, balance: Double)
object Account {
  def fromJson(json: Json): Account = ???
}

def accountsUri(id: String) = s"http://myapi.com/accounts/$id"

def getAccount(id: String, creds: ApiCredentials): IO[Account] = {
  for {
    token <- getFreshToken(creds)
    accountJson <- doGet(accountsUri(id), token)
  } yield Account.fromJson(accountJson)
}

def getToken(credentials: ApiCredentials, tokenRef: Ref[IO, Token]): IO[Token] = {
  tokenRef.get {
    
  }
  tokenRef.modify { currentToken =>
    currentToken.isExpired.flatMap { isExpired =>
      val newToken =
        if(isExpired) getFreshToken(credentials)
        else          IO.pure(currentToken)
      (newToken, newToken)
    }
  }
}

def getAccount2(id: String, creds: ApiCredentials, tokenRef: Ref[IO, Token]): IO[Account] = {
  for {
    token <- getToken(creds, tokenRef)
    accountJson <- doGet(accountsUri(id), token)
  } yield Account.fromJson(accountJson)
}

def loadCredentials(): IO[ApiCredentials]

def myProgram: IO[List[Account]] =
  loadCredentials().flatMap { creds =>
    List("a1", "b2", "c3").parTraverse(getAccount(_, creds))
  }
