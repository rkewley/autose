
package persistence

import models.MdlCourseLinks
import anorm._
import anorm.SqlParser._
import play.api.db._
import play.api.Play.current
import play.Logger

object SqlCourseLinks {

  val vCourseLinks = {
    get[Long]("idCourseLinks") ~
	get[Long]("Course") ~
	get[String]("Link") ~
	get[String]("DisplayDescription") ~
	get[Boolean]("IsFileLink") ~
	get[Long]("Faculty") map { case
    vidCourseLinks ~
		vCourse ~
		vLink ~
		vDisplayDescription ~
		vIsFileLink ~
		vFaculty =>
    MdlCourseLinks(vidCourseLinks,
		vCourse,
		vLink,
		vDisplayDescription,
		vIsFileLink,
		vFaculty)
    }
  }

  	def all: List[MdlCourseLinks] = DB.withConnection { implicit c =>
  		SQL("select * from `CourseLinks`").as(vCourseLinks *)
	}

	def select(vidCourseLinks: Long): MdlCourseLinks = DB.withConnection { implicit c =>
  		SQL("select * from `CourseLinks` WHERE `idCourseLinks` = {sqlidCourseLinks}").on(
  			'sqlidCourseLinks -> vidCourseLinks).as(vCourseLinks *).head
	}

  	def selectWhere(where: String): List[MdlCourseLinks] = DB.withConnection { implicit c =>
  		SQL("select * from `CourseLinks` WHERE " + where).as(vCourseLinks *)
	}

	def delete(vidCourseLinks: Long) = DB.withConnection { implicit c =>
  		SQL("DELETE FROM `CourseLinks` WHERE `idCourseLinks` = {sqlidCourseLinks}").on(
      'sqlidCourseLinks -> vidCourseLinks
  		).executeUpdate()
    }

	def insert(vCourseLinks: MdlCourseLinks) = DB.withConnection { implicit c =>
  		SQL("INSERT INTO `CourseLinks` (`Course`, `Link`, `DisplayDescription`, `IsFileLink`, `Faculty`) VALUES ({sqlCourse}, {sqlLink}, {sqlDisplayDescription}, {sqlIsFileLink}, {sqlFaculty})").on('sqlCourse -> vCourseLinks.vCourse, 'sqlLink -> vCourseLinks.vLink, 'sqlDisplayDescription -> vCourseLinks.vDisplayDescription, 'sqlIsFileLink -> vCourseLinks.vIsFileLink, 'sqlFaculty -> vCourseLinks.vFaculty).executeInsert()
	}

	def update(vCourseLinks: MdlCourseLinks) = DB.withConnection { implicit c =>
  		SQL("UPDATE `CourseLinks` SET `Course` = {sqlCourse}, `Link` = {sqlLink}, `DisplayDescription` = {sqlDisplayDescription}, `IsFileLink` = {sqlIsFileLink}, `Faculty` = {sqlFaculty} WHERE `idCourseLinks` = {sqlidCourseLinks}").on('sqlidCourseLinks -> vCourseLinks.vidCourseLinks, 'sqlCourse -> vCourseLinks.vCourse, 'sqlLink -> vCourseLinks.vLink, 'sqlDisplayDescription -> vCourseLinks.vDisplayDescription, 'sqlIsFileLink -> vCourseLinks.vIsFileLink, 'sqlFaculty -> vCourseLinks.vFaculty).executeUpdate()

  }

}