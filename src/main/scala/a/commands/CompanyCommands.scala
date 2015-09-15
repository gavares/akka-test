package a.commands

/**
 *
 * Created by gavares on 9/12/15.
 */
sealed trait CompanyCommand extends Command {
  def aggId = companyId
  def companyId: String
}

object CompanyCommands {
  case class CreateCompany(companyId: String, name: String, createdBy: String) extends CompanyCommand
  case class SetName(companyId: String, name: String, updatedBy: String) extends CompanyCommand
  case class AddUser(companyId: String, userId: String, updatedBy: String) extends CompanyCommand
}


