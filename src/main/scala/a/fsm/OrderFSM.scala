package a.fsm

import a.SystemEvents
import a.commands.OrderCommands.{UpdatePayment, CancelOrder, UpdateShipping}
import a.events.OrderEvents.{PaymentUpdated, OrderCancelled, ShippingUpdated}
import a.fsm.OrderFSM._
import akka.persistence.fsm.PersistentFSM
import akka.persistence.fsm.PersistentFSM.FSMState
import a.bizlogicAndEvents.StatefulOrder
import a.events.OrderEvent

import scala.reflect.ClassTag

/**
 *
 * Created by gavares on 9/12/15.
 */

object OrderFSM {
  sealed trait OrderState extends FSMState
  case object AwaitingFulfilment extends OrderState {
    override def identifier: String = "AwaitingFulfilment"
  }
  case object Shipped extends OrderState {
    override def identifier: String = "Shipped"
  }
  case object PaymentSettled extends OrderState {
    override def identifier: String = "PaymentSettled"
  }
  case object Error extends OrderState {
    override def identifier: String = "Error"
  }
  case object Cancelled extends OrderState {
    override def identifier: String = "Cancelled"
  }
}



case class OrderFSM(initialState: StatefulOrder) extends PersistentFSM[OrderState, StatefulOrder, OrderEvent] {

  override def persistenceId = initialState.id

  startWith(AwaitingFulfilment, initialState)

  when(AwaitingFulfilment) {

    case Event(UpdateShipping(status, addr), _) =>
      goto(Shipped) applying ShippingUpdated(status, addr) andThen {
        case data => context.system.eventStream publish SystemEvents.OrderHasShipped(data.id)
      }

    case Event(CancelOrder(reason), _) =>
      goto(Cancelled) applying OrderCancelled(reason)
  }


  when(Shipped) {

    case Event(UpdatePayment(status), _) =>
      val nextState = status match {
        case "Settled" => PaymentSettled
        case "Failed" => Error
      }
      goto(nextState) applying PaymentUpdated(status)
  }

  override implicit def domainEventClassTag: ClassTag[OrderEvent] = scala.reflect.classTag[OrderEvent]

  override def applyEvent(evt: OrderEvent, data: StatefulOrder): StatefulOrder = data.applyEvent(evt)
}

