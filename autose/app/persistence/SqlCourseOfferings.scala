
package persistence

import models.MdlCourseOfferings
import anorm._
import anorm.SqlParser._
import play.api.db._
import play.api.Play.current
import play.Logger

object SqlCourseOfferings {

  val vCourseOfferings = {
    get[Long]("idCourseOfferings") ~
	get[Long]("Course") ~
	get[String]("ClassHour") ~
	get[Long]("Section") ~
	get[String]("Location") ~
	get[String]("InstructorEmail") map { case
    vidCourseOfferings ~
		vCourse ~
		vClassHour ~
		vSection ~
		vLocation ~
		vInstructorEmail =>
    MdlCourseOfferings(vidCourseOfferings,
		vCourse,
		vClassHour,
		vSection,
		vLocation,
		vInstructorEmail)
    }
  }

  	def all: List[MdlCourseOfferings] = DB.withConnection { implicit c =>
  		SQL("select * from `CourseOfferings`").as(vCourseOfferings *)
	}

	def select(vidCourseOfferings: Long): MdlCourseOfferings = DB.withConnection { implicit c =>
  		SQL("select * from `CourseOfferings` WHERE `idCourseOfferings` = {sqlidCourseOfferings}").on(
  			'sqlidCourseOfferings -> vidCourseOfferings).as(vCourseOfferings *).head
	}

  	def selectWhere(where: String): List[MdlCourseOfferings] = DB.withConnection { implicit c =>
  		SQL("select * from `CourseOfferings` WHERE " + where).as(vCourseOfferings *)
	}

	def delete(vidCourseOfferings: Long) = DB.withConnection { implicit c =>
  		SQL("DELETE FROM `CourseOfferings` WHERE `idCourseOfferings` = {sqlidCourseOfferings}").on(
      'sqlidCourseOfferings -> vidCourseOfferings
  		).executeUpdate()
    }

	def insert(vCourseOfferings: MdlCourseOfferings) = DB.withConnection { implicit c =>
  		SQL("INSERT INTO `CourseOfferings` (`Course`, `ClassHour`, `Section`, `Location`, `InstructorEmail`) VALUES ({sqlCourse}, {sqlClassHour}, {sqlSection}, {sqlLocation}, {sqlInstructorEmail})").on('sqlCourse -> vCourseOfferings.vCourse, 'sqlClassHour -> vCourseOfferings.vClassHour, 'sqlSection -> vCourseOfferings.vSection, 'sqlLocation -> vCourseOfferings.vLocation, 'sqlInstructorEmail -> vCourseOfferings.vInstructorEmail).executeInsert()
	}

	def update(vCourseOfferings: MdlCourseOfferings) = DB.withConnection { implicit c =>
  		SQL("UPDATE `CourseOfferings` SET `Course` = {sqlCourse}, `ClassHour` = {sqlClassHour}, `Section` = {sqlSection}, `Location` = {sqlLocation}, `InstructorEmail` = {sqlInstructorEmail} WHERE `idCourseOfferings` = {sqlidCourseOfferings}").on('sqlidCourseOfferings -> vCourseOfferings.vidCourseOfferings, 'sqlCourse -> vCourseOfferings.vCourse, 'sqlClassHour -> vCourseOfferings.vClassHour, 'sqlSection -> vCourseOfferings.vSection, 'sqlLocation -> vCourseOfferings.vLocation, 'sqlInstructorEmail -> vCourseOfferings.vInstructorEmail).executeUpdate()

  }

}