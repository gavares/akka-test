package a.commands

/**
 *
 * Created by gavares on 9/12/15.
 */
sealed trait OrderCommand
object OrderCommands {
  case class UpdateShipping(status: String, address: String) extends OrderCommand
  case class UpdatePayment(status: String) extends OrderCommand
  case class CancelOrder(reason: String) extends OrderCommand
}



