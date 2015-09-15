package a.message

import a.events.DomainEvent
import org.joda.time.DateTime
import a._

/**
 *
 * Created by gavares on 9/13/15.
 */
case class DomainEventMessage[DE <: DomainEvent](aggId: String, event: DE, override val id: String = uuid,
                              override val timestamp: DateTime = now) extends EventMessage(event, id, timestamp) {

}

object DomainEventMessage {
  def apply[DE <: DomainEvent](aggId: String, em: EventMessage[DE]): DomainEventMessage[DE] = {
    DomainEventMessage[DE](aggId, em.domainEvent, em.id, em.timestamp)
      .withMetaData(em.metaData)
  }
}
