package a.bizlogicOnly

import a.events.CompanyEvent

/**
 *
 * Created by gavares on 9/11/15.
 */
case class Company(id: String, name: String = "", users: Seq[String] = Seq.empty) {
  type Evt = CompanyEvent

  def setName(newName: String) = copy(name = newName)

  def addUser(newUser: String) = {
    if(users.contains(newUser)) this
    else copy(users = users :+ newUser)
  }
}


