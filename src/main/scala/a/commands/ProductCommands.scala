package a.commands

import a.Timestamped


/**
 *
 * Created by gavares on 9/13/15.
 */
sealed trait ProductCommand extends Timestamped with Command {
  def companyId: String
  def sku: String
  def aggId = sku
  def userId: String
}

object ProductCommands {
  case class CreateProduct(companyId: String, sku: String, name: String, desc: String, internalDesc: String, userId: String) extends ProductCommand
  case class Deactivate(companyId: String, sku: String, userId: String) extends ProductCommand
  case class Activate(companyId: String, sku: String, userId: String) extends ProductCommand
  case class SetName(companyId: String, sku: String, name: String, userId: String) extends ProductCommand
  case class SetDesc(companyId: String, sku: String, desc: String, userId: String) extends ProductCommand
  case class SetInternalDesc(companyId: String, sku: String, internalDesc: String, userId: String) extends ProductCommand
}
