package a.aggroot

import a._
import a.commands.CompanyCommand
import a.commands.CompanyCommands.{AddUser, SetName, CreateCompany}
import a.events.CompanyEvent
import a.events.CompanyEvents.{UserAdded, CompanyNameUpdated, CompanyCreated}

/**
 *
 * Created by gavares on 9/14/15.
 */
case class CompanyMgr(companies: Map[String, Company] = Map.empty)
  extends AggregateState[CompanyEvent, CompanyMgr]
{
  def exists(cId: String) = companies.contains(cId)
  def withId(cId: String) = companies.get(cId)

  override def apply(e: CompanyEvent) = e match {
    case evt: CompanyCreated =>
      val c = new Company(evt.id, evt.name, userIds = Seq(evt.createdBy))
      copy(companies = companies + (evt.id -> c))

    case evt: CompanyEvent => withId(evt.id) match {
      case Some(c) => copy(companies = companies + (c.id -> c.apply(evt)))
      case _ => this
    }
  }
}

object CompanyMgrAggRoot {
  val PersistenceId = "companies"
  def pathForCompany(cId: String) = s"$PersistenceId/$cId"
}

class CompanyMgrAggRoot extends AggRoot[CompanyCommand, CompanyEvent, CompanyMgr]{

  override def handleCommand(cmd: CompanyCommand): Unit = {
    val (user, evt) = cmd match {
      case CreateCompany(_, name, createdBy) => (createdBy, CompanyCreated(uuid, name, createdBy))
      case SetName(_, name, updatedBy) => (updatedBy, CompanyNameUpdated(cmd.companyId, name))
      case AddUser(_, uId, addedBy) => (addedBy, UserAdded(cmd.companyId, uId))
    }

    doPersist(evt)
  }


  override val stateFactory= new AggregateStateFactory[CompanyEvent, CompanyMgr] {
    override def apply: PartialFunction[CompanyEvent, CompanyMgr] = {
      case c => CompanyMgr().apply(c)
    }
  }
}
