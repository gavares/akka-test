package a.bizlogicOnly

import a.commands.ShoppingCartCommand
import a.commands.ShoppingCartCommands._
import a.events.ShoppingCartEvent
import a.events.ShoppingCartEvents.{PaymentAdded, AddressAdded, ItemRemoved, ItemAdded}
import akka.persistence.PersistentActor
import com.typesafe.scalalogging.LazyLogging

import scala.util.{Try, Success, Failure}

/**
 *
 * Created by gavares on 9/12/15.
 */
case class ShoppingCartPersistence(var state: ShoppingCart) extends PersistentActor with LazyLogging{

  override def persistenceId = id
  def id = self.path.name

  override def receiveRecover: Receive = {
    case evt: ShoppingCartEvent => handleEvt(evt)
  }

  private def handleEvt(evt: ShoppingCartEvent) = evt match {
    case ItemAdded(item, q)  => state = state.addItem(item, q)
    case ItemRemoved(item, q)  => state = state.removeItem(item, q)
    case AddressAdded(addr)  => state.setAddress(addr)
    case PaymentAdded(gateway, amount, status) => state.addPayment(gateway, amount, status)
  }

  override def receiveCommand: Receive = {
    case c: ShoppingCartCommand => handleCmd(c)
  }

  private def handleCmd(cmd: ShoppingCartCommand) = cmd match {
    case AddItem(item, quantity) =>
      val evt = ItemAdded(item, quantity)
      val blk = { state.addItem(item, quantity) }
      updateAndSave(evt)(blk)

    case RemoveItem(item, quantity) =>
    case SetShippingAddress(addr) =>
    case AddPayment(gateway, amount, status) =>
    case Checkout =>
  }


  private def updateAndSave(evt: ShoppingCartEvent)(blk: => ShoppingCart) = Try(blk) match {
    case Success(updatedSC) => persist(evt) { persisted =>
      logger.debug(s"persisted: $persisted")
      this.state = updatedSC
    }

    case Failure(ex) => sender() ! ex
  }
}
