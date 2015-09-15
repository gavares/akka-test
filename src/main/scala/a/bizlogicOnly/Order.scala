package a.bizlogicOnly

import a.IllegalOperationError
import org.joda.time.DateTime
import org.joda.time.DateTime.now

/**
 * An order class that doesn't know about a.events and only contains business logic
 *
 * Created by gavares on 9/12/15.
 */
case class Order(id: String, items: Seq[String], shippingDetail: (String,String),
                 paymentDetail: String, notes: Seq[String] = Nil)


sealed trait StatefulOrder {
  def order: Order
  def id = order.id
  def stateName: String

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
}



case class ShippedOrder(order: Order) extends StatefulOrder {
  override val stateName = "ShippedOrder"

  override def updatePaymentStatus(status: String): StatefulOrder = {
    val updatedOrder = order.copy(paymentDetail = "Settled")
    if(status == "Settled") PaymentSettledOrder(updatedOrder)
    else PaymentFailedOrder(updatedOrder)
  }
}



case class PaymentFailedOrder(order: Order) extends StatefulOrder {
  override val stateName = "PaymentFailedOrder"
}



case class PaymentSettledOrder(order: Order) extends StatefulOrder {
  override val stateName = "PaymentSettledOrder"
}



case class CancelledOrder(order: Order, reason: String, cancelledAt: DateTime = now) extends StatefulOrder {
  override val stateName = "CancelledOrder"
}


