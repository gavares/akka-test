package a.bizlogicOnly

import a.events.ShoppingCartEvents.PaymentAdded

/**
 *
 * Created by gavares on 9/11/15.
 */

case class ShoppingCart(id: String, items: Seq[String] = Nil, address: Option[String] = None, payments: Seq[PaymentAdded] = Nil) {

  def addItem(item: String, quantity: Int): ShoppingCart = copy(items = items :+ item)

  def removeItem(item: String, quantity: Int): ShoppingCart = copy(items = items.filterNot(_ == item))

  def setAddress(addr: String): ShoppingCart = copy(address = Some(addr))

  def addPayment(gateway: String, amnt: Int, status: String): ShoppingCart =
    copy(payments = payments :+ PaymentAdded(gateway, amnt, status))

}
