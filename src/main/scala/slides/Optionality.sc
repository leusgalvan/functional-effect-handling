case class Person(id: Int, name: String)
case class Account(id: Int, balance: Double, owner: Person)
case class Transfer(source: Account, dest: Account, amount: Double)
def findPersonById(id: Int): Option[Person] = ???
def findAccountByOwner(owner: Person): Option[Account] = ???
def findTopTransferBySource(source: Account): Option[Transfer] = ???
def findTopTransferByOwnerId(ownerId: Int): Option[Transfer] =
  for {
    owner       <- findPersonById(ownerId)
    account     <- findAccountByOwner(owner)
    topTransfer <- findTopTransferBySource(account)
  } yield topTransfer

def getName()