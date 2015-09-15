package a.commands

import a.Timestamped


/**
 *
 * Created by gavares on 9/12/15.
 */
sealed trait ShoppingCartCommand extends Timestamped
object ShoppingCartCommands {
  case class AddItem(item: String, quantity: Int) extends ShoppingCartCommand
  case class RemoveItem(item: String, quantity: Int) extends ShoppingCartCommand
  case class SetShippingAddress(addr: String) extends ShoppingCartCommand
  case class AddPayment(gateway: String, amount: Int, status: String) extends ShoppingCartCommand
  case object Checkout extends ShoppingCartCommand
}
