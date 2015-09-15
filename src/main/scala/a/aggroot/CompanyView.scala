package a.aggroot

import a.aggroot.CompanyView.Protocol.GetCompany
import a.events.CompanyEvent
import a.message.EventMessage
import akka.actor.ActorLogging
import akka.persistence.PersistentView

import scala.concurrent.stm.Ref

/**
 *
 * Created by gavares on 9/14/15.
 */

object CompanyView{
  object Protocol {
    case class GetCompany(Id: String)
  }
}

case class CompanyView(stateRef: Ref[CompanyMgr]) extends PersistentView with ActorLogging {

  override def persistenceId: String = CompanyMgrAggRoot.PersistenceId
  override def viewId: String = "companiesView"

  override def receive: Receive = {
    case em: EventMessage[CompanyEvent] =>
      log.debug("processing event: {}", em.domainEvent)
      stateRef.single.transform( _.apply(em.domainEvent) )

    case GetCompany(cId) =>
      log.debug("GetCompany: {}", cId)
      sender ! stateRef.single.get.withId(cId)
  }
}
