package a.events

import a.Timestamped


/**
 *
 * Created by gavares on 9/13/15.
 */
sealed trait ProductEvent extends Timestamped with DomainEvent {
  def companyId: String
  def sku: String
}

object ProductEvents {
  case class ProductCreated(companyId: String, sku: String, displayName: String, desc: String, internalDesc: String) extends ProductEvent
  case class ProductActivated(companyId: String, sku: String) extends ProductEvent
  case class ProductDeactivated(companyId: String, sku: String) extends ProductEvent
  case class ProductNameUpdated(companyId: String, sku: String, name: String) extends ProductEvent
  case class ProductDescUpdated(companyId: String, sku: String, desc: String) extends ProductEvent
  case class ProductInternalDescUpdated(companyId: String, sku: String, internalDesc: String) extends ProductEvent
}
