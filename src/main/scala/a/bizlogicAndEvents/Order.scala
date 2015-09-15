package a.bizlogicAndEvents

import a.IllegalOperationError
import a.bizlogicOnly.ShoppingCart
import a.events.OrderEvent
import a.events.OrderEvents.{PaymentUpdated, OrderCancelled, ShippingUpdated}
import org.joda.time.DateTime
import org.joda.time.DateTime.now

/**
 *
 * Created by gavares on 9/11/15.
 */
case class Order(id: String, items: Seq[String], shippingDetail: (String,String),
                 paymentDetail: String, notes: Seq[String] = Nil)


object Order {
  def fromShoppingCart(cart: ShoppingCart): Order = Order(cart.id, cart.items, ("NotShipped", cart.address.get), "Authorized", Nil)
}

sealed trait StatefulOrder {
  def order: Order
  def id = order.id
  def stateName: String

  def applyEvent: PartialFunction[OrderEvent, StatefulOrder]

  def updateShipping(status: String, addr: String): StatefulOrder = illegalOp("updateShipping")
  def updatePaymentStatus(status: String): StatefulOrder = illegalOp("updatePaymentStatus")
  def cancel(reason: String): StatefulOrder = illegalOp("cancel")

  def illegalOp(op: String) = throw new IllegalOperationError(stateName, op)
}



case class UnshippedOrder(order: Order) extends StatefulOrder {
  def stateName = "UnshippedOrder"
  override def cancel(reason: String): StatefulOrder = CancelledOrder(order, reason)
  override def updateShipping(status: String, addr: String): StatefulOrder = status match {
    case "Shipped" =>
      val shipDetail = (status, addr)
      val updatedOrder = order.copy(shippingDetail = shipDetail)
      ShippedOrder(updatedOrder)
  }

  override def applyEvent = {
    case e: ShippingUpdated => updateShipping(e.status, e.address)
    case e: OrderCancelled => cancel(e.reason)
  }
}



case class ShippedOrder(order: Order) extends StatefulOrder {
  override val stateName = "ShippedOrder"

  override def updatePaymentStatus(status: String): StatefulOrder = {
    val updatedOrder = order.copy(paymentDetail = "Settled")
    if(status == "Settled") PaymentSettledOrder(updatedOrder)
    else PaymentFailedOrder(updatedOrder)
  }

  override def applyEvent = {
    case e: PaymentUpdated => updatePaymentStatus(e.status)
  }
}



case class PaymentFailedOrder(order: Order) extends StatefulOrder {
  override val stateName = "PaymentFailedOrder"
  override def applyEvent = {
    case e: PaymentUpdated => ShippedOrder(order).updatePaymentStatus(e.status)
  }
}



case class PaymentSettledOrder(order: Order) extends StatefulOrder {
  override val stateName = "PaymentSettledOrder"
  override def applyEvent = {
    case _ => this
  }
}



case class CancelledOrder(order: Order, reason: String, cancelledAt: DateTime = now) extends StatefulOrder {
  override val stateName = "CancelledOrder"
  override def applyEvent = {
    case _ => this
  }
}


