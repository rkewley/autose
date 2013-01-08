
package persistence

import models.MdlFaculty
import anorm._
import anorm.SqlParser._
import play.api.db._
import play.api.Play.current
import play.Logger

object SqlFaculty {

  val vFaculty = {
    get[String]("LastName") ~
	get[String]("First Name") ~
	get[String]("Title") ~
	get[String]("Office Number") ~
	get[String]("Office Phone") ~
	get[String]("Branch of Service") ~
	get[String]("Email") ~
	get[String]("FacultyPhotoFile") ~
	get[String]("Biography") map { case
    vLastName ~
		vFirstName ~
		vTitle ~
		vOfficeNumber ~
		vOfficePhone ~
		vBranchofService ~
		vEmail ~
		vFacultyPhotoFile ~
		vBiography =>
    MdlFaculty(vLastName,
		vFirstName,
		vTitle,
		vOfficeNumber,
		vOfficePhone,
		vBranchofService,
		vEmail,
		vFacultyPhotoFile,
		vBiography)
    }
  }

  	def all: List[MdlFaculty] = DB.withConnection { implicit c =>
  		SQL("select * from `Faculty`").as(vFaculty *)
	}

	def select(vEmail: String): MdlFaculty = DB.withConnection { implicit c =>
  		SQL("select * from `Faculty` WHERE `Email` = {sqlEmail}").on(
  			'sqlEmail -> vEmail).as(vFaculty *).head
	}

	def delete(vEmail: String) = DB.withConnection { implicit c =>
  		SQL("DELETE FROM `Faculty` WHERE `Email` = {sqlEmail}").on(
      'sqlEmail -> vEmail
  		).executeUpdate()
    }

	def insert(vFaculty: MdlFaculty) = DB.withConnection { implicit c =>
  		SQL("INSERT INTO `Faculty` (`LastName`, `First Name`, `Title`, `Office Number`, `Office Phone`, `Branch of Service`, `Email`, `FacultyPhotoFile`, `Biography`) VALUES ({sqlLastName}, {sqlFirstName}, {sqlTitle}, {sqlOfficeNumber}, {sqlOfficePhone}, {sqlBranchofService}, {sqlEmail}, {sqlFacultyPhotoFile}, {sqlBiography})").on('sqlLastName -> vFaculty.vLastName, 'sqlFirstName -> vFaculty.vFirstName, 'sqlTitle -> vFaculty.vTitle, 'sqlOfficeNumber -> vFaculty.vOfficeNumber, 'sqlOfficePhone -> vFaculty.vOfficePhone, 'sqlBranchofService -> vFaculty.vBranchofService, 'sqlEmail -> vFaculty.vEmail, 'sqlFacultyPhotoFile -> vFaculty.vFacultyPhotoFile, 'sqlBiography -> vFaculty.vBiography).executeInsert()
	}

	def update(vFaculty: MdlFaculty) = DB.withConnection { implicit c =>
  		SQL("UPDATE `Faculty` SET `LastName` = {sqlLastName}, `First Name` = {sqlFirstName}, `Title` = {sqlTitle}, `Office Number` = {sqlOfficeNumber}, `Office Phone` = {sqlOfficePhone}, `Branch of Service` = {sqlBranchofService}, `FacultyPhotoFile` = {sqlFacultyPhotoFile}, `Biography` = {sqlBiography} WHERE `Email` = {sqlEmail}").on('sqlLastName -> vFaculty.vLastName, 'sqlFirstName -> vFaculty.vFirstName, 'sqlTitle -> vFaculty.vTitle, 'sqlOfficeNumber -> vFaculty.vOfficeNumber, 'sqlOfficePhone -> vFaculty.vOfficePhone, 'sqlBranchofService -> vFaculty.vBranchofService, 'sqlEmail -> vFaculty.vEmail, 'sqlFacultyPhotoFile -> vFaculty.vFacultyPhotoFile, 'sqlBiography -> vFaculty.vBiography).executeUpdate()

  }

}