package a.aggroot

import a.events.CompanyEvent
import a.events.CompanyEvents.{CompanyNameUpdated, UserAdded}

/**
 *
 * Created by gavares on 9/14/15.
 */
case class Company(id: String, name: String, url: Option[String] = None, userIds: Seq[String] = Nil) extends AggregateState[CompanyEvent, Company] {

  def userCanWrite(user: String) = userIds.contains(user)

  def apply(evt: CompanyEvent): Company = evt match {
    case CompanyNameUpdated(_, n) => copy(name = n)
    case UserAdded(_, userId) =>
      val updatedUsers = userIds.filterNot(_ == userId) :+ userId
      copy(userIds = updatedUsers)
  }

}

