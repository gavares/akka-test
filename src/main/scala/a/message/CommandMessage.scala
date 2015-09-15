package a.message

import a.commands.Command
import org.joda.time.DateTime
import a._

/**
 *
 * Created by gavares on 9/13/15.
 */
case class CommandMessage[C <: Command](cmd: C, id: String = uuid, timestamp: DateTime = now)
  extends Message
  with EntityMessage
{

  def entityId = cmd.aggId

  def payload = cmd

}
