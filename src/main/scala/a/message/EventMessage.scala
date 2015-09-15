package a.message

import a._
import a.events.DomainEvent
import org.joda.time.DateTime

/**
 *
 * This is not a case class because other classes (like DomainEventMessage) need to extend it
 * Created by gavares on 9/13/15.
 */
class EventMessage[DE <: DomainEvent](val domainEvent: DE, override val id: String = uuid, val timestamp: DateTime = now) extends Message
