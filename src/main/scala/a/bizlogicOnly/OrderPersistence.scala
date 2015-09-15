package a.bizlogicOnly

import a.commands.OrderCommand
import a.commands.OrderCommands.{CancelOrder, UpdatePayment, UpdateShipping}
import a.events.OrderEvent
import a.events.OrderEvents.{OrderCancelled, ShippingUpdated, PaymentUpdated}
import akka.persistence.PersistentActor
import com.typesafe.scalalogging.LazyLogging

import scala.util.{Failure, Success, Try}

/**
 *
 * Created by gavares on 9/12/15.
 */
class OrderPersistence(var state: StatefulOrder) extends PersistentActor with LazyLogging {

  def id: String = self.path.name
  override def persistenceId: String = id

  override def receiveRecover: Receive = {
    case event: OrderEvent => handleEvent(event)
  }

  private def handleEvent(event: OrderEvent) = event match {
    case PaymentUpdated(status) => state = state.updatePaymentStatus(status)
    case ShippingUpdated(status, address) => state = state.updateShipping(status, address)
    case OrderCancelled(reason) => state.cancel(reason)
  }


  override def receiveCommand: Receive = {
    case c: OrderCommand => handleCommand(c)
  }

  private def handleCommand(cmd: OrderCommand) = cmd match {
    case UpdateShipping(status, address) => doPersist( ShippingUpdated(status, address) ) {
      state.updateShipping(status, address)
    }

    case UpdatePayment(status) => state = state.updatePaymentStatus(status)
    case CancelOrder(reason) => state = state.cancel(reason)
  }


  private def doPersist(evt: OrderEvent)(blk: => StatefulOrder) = Try( blk ) match {
    case Failure(ex) => sender() ! ex
    case Success(updatedState) => persist( evt ) { persisted =>
      logger.debug(s"persisted event: $evt")
    }
  }
}
