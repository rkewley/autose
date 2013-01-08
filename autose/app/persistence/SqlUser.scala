
package persistence

import models._
import play.api.db._
import anorm._
import anorm.SqlParser._
import play.api.Play.current
import java.sql.Clob
import org.mindrot.jbcrypt.BCrypt

object SqlUser {
  
  object Clob {
    def unapply(clob: Clob): Option[String] = Some(clob.getSubString(1, clob.length.toInt))
  }


  val vUser = {
    get[String]("email") ~
	get[String]("password") ~
	get[String]("permissions") map { case
    vemail ~
		vpassword ~
		vpermissions =>
    MdlUser(vemail,
		vpassword,
		vpermissions)
    }
  }
  
  implicit val rowToPermission: Column[Permission] = {
    Column.nonNull[Permission] { (value, meta) =>
      value match {
        case Clob("Administrator") => Right(Administrator)
        case Clob("NormalUser") => Right(NormalUser)
        case _ => Left(TypeDoesNotMatch(
          "Cannot convert %s : %s to Permission for column %s".format(value, value.getClass, meta.column)))
      }
    }
  }
  
  def authenticate(email: String, password: String): Option[MdlUser] = {
    findByEmail(email).filter { account => BCrypt.checkpw(password, account.vpassword) }
  }
  
  def findByEmail(email: String): Option[MdlUser] = {
    DB.withConnection { implicit connection =>
      SQL("SELECT * FROM User WHERE email = {email}").on(
        'email -> email
      ).as(vUser.singleOpt)
    }
  }



  	def all: List[MdlUser] = DB.withConnection { implicit c =>
  		SQL("select * from `User`").as(vUser *)
	}

	def select(vemail: String): MdlUser = DB.withConnection { implicit c =>
  		SQL("select * from `User` WHERE `email` = {sqlemail}").on(
  			'sqlemail -> vemail).as(vUser *).head
	}

  	def selectWhere(where: String): List[MdlUser] = DB.withConnection { implicit c =>
  		SQL("select * from `User` WHERE " + where).as(vUser *)
	}

	def delete(vemail: String) = DB.withConnection { implicit c =>
  		SQL("DELETE FROM `User` WHERE `email` = {sqlemail}").on(
      'sqlemail -> vemail
  		).executeUpdate()
    }

	def insert(vUser: MdlUser) = DB.withConnection { implicit c =>
  		SQL("INSERT INTO `User` (`email`, `password`, `permissions`) VALUES ({sqlemail}, {sqlpassword}, {sqlpermissions})").on('sqlemail -> vUser.vemail, 'sqlpassword -> BCrypt.hashpw(vUser.vpassword, BCrypt.gensalt()), 'sqlpermissions -> vUser.vpermissions).executeInsert()
	}

	def update(vUser: MdlUser) = DB.withConnection { implicit c =>
  		SQL("UPDATE `User` SET `password` = {sqlpassword}, `permissions` = {sqlpermissions} WHERE `email` = {sqlemail}").on('sqlemail -> vUser.vemail, 'sqlpassword -> BCrypt.hashpw(vUser.vpassword, BCrypt.gensalt()), 'sqlpermissions -> vUser.vpermissions).executeUpdate()

  }

}