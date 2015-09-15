package a.events

import a.Timestamped


/**
 *
 * Created by gavares on 9/12/15.
 */
sealed trait ShoppingCartEvent extends Timestamped
object ShoppingCartEvents {
  case class ItemAdded(item: String, quantity: Int) extends ShoppingCartEvent
  case class ItemRemoved(item: String, quantity: Int) extends ShoppingCartEvent
  case class AddressAdded(addr: String) extends ShoppingCartEvent
  case class PaymentAdded(gateway: String, amount: Int, status: String) extends ShoppingCartEvent
}

