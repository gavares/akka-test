package a.events

/**
 *
 * Created by gavares on 9/12/15.
 */
sealed trait CompanyEvent extends DomainEvent {
  def id: String
}
object CompanyEvents {
  case class CompanyCreated(id: String, name: String, createdBy: String) extends CompanyEvent
  case class CompanyNameUpdated(id: String, name: String) extends CompanyEvent
  case class UserAdded(id: String, name: String) extends CompanyEvent
}
