package a.events

import a.Timestamped


/**
 *
 * Created by gavares on 9/12/15.
 */
sealed trait OrderEvent extends Timestamped
object OrderEvents {
  case class PaymentUpdated(status: String) extends OrderEvent
  case class ShippingUpdated(status: String, address: String) extends OrderEvent
  case class OrderCancelled(reason: String) extends OrderEvent
}
