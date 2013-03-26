
package persistence

import models.MdlLessonLinks
import anorm._
import anorm.SqlParser._
import play.api.db._
import play.api.Play.current
import play.Logger

object SqlLessonLinks {

  val vLessonLinks = {
    get[Long]("LessonLinkNumber") ~
	get[String]("Link") ~
	get[String]("Description") ~
	get[Boolean]("IsFileLiink") ~
	get[Long]("Lesson") ~
	get[Long]("Faculty") map { case
    vLessonLinkNumber ~
		vLink ~
		vDescription ~
		vIsFileLiink ~
		vLesson ~
		vFaculty =>
    MdlLessonLinks(vLessonLinkNumber,
		vLink,
		vDescription,
		vIsFileLiink,
		vLesson,
		vFaculty)
    }
  }

  	def all: List[MdlLessonLinks] = DB.withConnection { implicit c =>
  		SQL("select * from `LessonLinks`").as(vLessonLinks *)
	}

	def select(vLessonLinkNumber: Long): MdlLessonLinks = DB.withConnection { implicit c =>
  		SQL("select * from `LessonLinks` WHERE `LessonLinkNumber` = {sqlLessonLinkNumber}").on(
  			'sqlLessonLinkNumber -> vLessonLinkNumber).as(vLessonLinks *).head
	}

  	def selectWhere(where: String): List[MdlLessonLinks] = DB.withConnection { implicit c =>
  		SQL("select * from `LessonLinks` WHERE " + where).as(vLessonLinks *)
	}

	def delete(vLessonLinkNumber: Long) = DB.withConnection { implicit c =>
  		SQL("DELETE FROM `LessonLinks` WHERE `LessonLinkNumber` = {sqlLessonLinkNumber}").on(
      'sqlLessonLinkNumber -> vLessonLinkNumber
  		).executeUpdate()
    }

	def insert(vLessonLinks: MdlLessonLinks) = DB.withConnection { implicit c =>
  		SQL("INSERT INTO `LessonLinks` (`Link`, `Description`, `IsFileLiink`, `Lesson`, `Faculty`) VALUES ({sqlLink}, {sqlDescription}, {sqlIsFileLiink}, {sqlLesson}, {sqlFaculty})").on('sqlLink -> vLessonLinks.vLink, 'sqlDescription -> vLessonLinks.vDescription, 'sqlIsFileLiink -> vLessonLinks.vIsFileLiink, 'sqlLesson -> vLessonLinks.vLesson, 'sqlFaculty -> vLessonLinks.vFaculty).executeInsert()
	}

	def update(vLessonLinks: MdlLessonLinks) = DB.withConnection { implicit c =>
  		SQL("UPDATE `LessonLinks` SET `Link` = {sqlLink}, `Description` = {sqlDescription}, `IsFileLiink` = {sqlIsFileLiink}, `Lesson` = {sqlLesson}, `Faculty` = {sqlFaculty} WHERE `LessonLinkNumber` = {sqlLessonLinkNumber}").on('sqlLessonLinkNumber -> vLessonLinks.vLessonLinkNumber, 'sqlLink -> vLessonLinks.vLink, 'sqlDescription -> vLessonLinks.vDescription, 'sqlIsFileLiink -> vLessonLinks.vIsFileLiink, 'sqlLesson -> vLessonLinks.vLesson, 'sqlFaculty -> vLessonLinks.vFaculty).executeUpdate()

  }

}