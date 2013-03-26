
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
	get[Long]("Instructor") map { case
    vidCourseOfferings ~
		vCourse ~
		vClassHour ~
		vSection ~
		vLocation ~
		vInstructor =>
    MdlCourseOfferings(vidCourseOfferings,
		vCourse,
		vClassHour,
		vSection,
		vLocation,
		vInstructor)
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
  		SQL("INSERT INTO `CourseOfferings` (`Course`, `ClassHour`, `Section`, `Location`, `Instructor`) VALUES ({sqlCourse}, {sqlClassHour}, {sqlSection}, {sqlLocation}, {sqlInstructor})").on('sqlCourse -> vCourseOfferings.vCourse, 'sqlClassHour -> vCourseOfferings.vClassHour, 'sqlSection -> vCourseOfferings.vSection, 'sqlLocation -> vCourseOfferings.vLocation, 'sqlInstructor -> vCourseOfferings.vInstructor).executeInsert()
	}

	def update(vCourseOfferings: MdlCourseOfferings) = DB.withConnection { implicit c =>
  		SQL("UPDATE `CourseOfferings` SET `Course` = {sqlCourse}, `ClassHour` = {sqlClassHour}, `Section` = {sqlSection}, `Location` = {sqlLocation}, `Instructor` = {sqlInstructor} WHERE `idCourseOfferings` = {sqlidCourseOfferings}").on('sqlidCourseOfferings -> vCourseOfferings.vidCourseOfferings, 'sqlCourse -> vCourseOfferings.vCourse, 'sqlClassHour -> vCourseOfferings.vClassHour, 'sqlSection -> vCourseOfferings.vSection, 'sqlLocation -> vCourseOfferings.vLocation, 'sqlInstructor -> vCourseOfferings.vInstructor).executeUpdate()

  }

}