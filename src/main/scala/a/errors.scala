package a

/**
 *
 * Created by gavares on 9/12/15.
 */

case class IllegalOperationError(stateName: String, opName: String) extends Exception {
  override def getMessage = s"You cannot invoke operation $opName while in state $stateName"
}


case class PermissionDeniedError(userId: String, cmdName: String) extends Exception {
  override def getMessage = s"User $userId cannot $cmdName"
}
