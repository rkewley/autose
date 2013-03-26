
package persistence

import models.MdlFaculty
import anorm._
import anorm.SqlParser._
import play.api.db._
import play.api.Play.current
import play.Logger

object SqlFaculty {

  val vFaculty = {
    get[Long]("idFaculty") ~
	get[String]("LastName") ~
	get[String]("First Name") ~
	get[String]("Title") ~
	get[String]("Office Number") ~
	get[String]("Office Phone") ~
	get[String]("Branch of Service") ~
	get[String]("Email") ~
	get[String]("FacultyPhotoFile") ~
	get[String]("Biography") map { case
    vidFaculty ~
		vLastName ~
		vFirstName ~
		vTitle ~
		vOfficeNumber ~
		vOfficePhone ~
		vBranchofService ~
		vEmail ~
		vFacultyPhotoFile ~
		vBiography =>
    MdlFaculty(vidFaculty,
		vLastName,
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

	def select(vidFaculty: Long): MdlFaculty = DB.withConnection { implicit c =>
  		SQL("select * from `Faculty` WHERE `idFaculty` = {sqlidFaculty}").on(
  			'sqlidFaculty -> vidFaculty).as(vFaculty *).head
	}

  	def selectWhere(where: String): List[MdlFaculty] = DB.withConnection { implicit c =>
  		SQL("select * from `Faculty` WHERE " + where).as(vFaculty *)
	}

	def delete(vidFaculty: Long) = DB.withConnection { implicit c =>
  		SQL("DELETE FROM `Faculty` WHERE `idFaculty` = {sqlidFaculty}").on(
      'sqlidFaculty -> vidFaculty
  		).executeUpdate()
    }

	def insert(vFaculty: MdlFaculty) = DB.withConnection { implicit c =>
  		SQL("INSERT INTO `Faculty` (`LastName`, `First Name`, `Title`, `Office Number`, `Office Phone`, `Branch of Service`, `Email`, `FacultyPhotoFile`, `Biography`) VALUES ({sqlLastName}, {sqlFirstName}, {sqlTitle}, {sqlOfficeNumber}, {sqlOfficePhone}, {sqlBranchofService}, {sqlEmail}, {sqlFacultyPhotoFile}, {sqlBiography})").on('sqlLastName -> vFaculty.vLastName, 'sqlFirstName -> vFaculty.vFirstName, 'sqlTitle -> vFaculty.vTitle, 'sqlOfficeNumber -> vFaculty.vOfficeNumber, 'sqlOfficePhone -> vFaculty.vOfficePhone, 'sqlBranchofService -> vFaculty.vBranchofService, 'sqlEmail -> vFaculty.vEmail, 'sqlFacultyPhotoFile -> vFaculty.vFacultyPhotoFile, 'sqlBiography -> vFaculty.vBiography).executeInsert()
	}

	def update(vFaculty: MdlFaculty) = DB.withConnection { implicit c =>
  		SQL("UPDATE `Faculty` SET `LastName` = {sqlLastName}, `First Name` = {sqlFirstName}, `Title` = {sqlTitle}, `Office Number` = {sqlOfficeNumber}, `Office Phone` = {sqlOfficePhone}, `Branch of Service` = {sqlBranchofService}, `Email` = {sqlEmail}, `FacultyPhotoFile` = {sqlFacultyPhotoFile}, `Biography` = {sqlBiography} WHERE `idFaculty` = {sqlidFaculty}").on('sqlidFaculty -> vFaculty.vidFaculty, 'sqlLastName -> vFaculty.vLastName, 'sqlFirstName -> vFaculty.vFirstName, 'sqlTitle -> vFaculty.vTitle, 'sqlOfficeNumber -> vFaculty.vOfficeNumber, 'sqlOfficePhone -> vFaculty.vOfficePhone, 'sqlBranchofService -> vFaculty.vBranchofService, 'sqlEmail -> vFaculty.vEmail, 'sqlFacultyPhotoFile -> vFaculty.vFacultyPhotoFile, 'sqlBiography -> vFaculty.vBiography).executeUpdate()

  }

}