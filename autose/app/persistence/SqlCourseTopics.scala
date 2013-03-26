
package persistence

import models.MdlCourseTopics
import anorm._
import anorm.SqlParser._
import play.api.db._
import play.api.Play.current
import play.Logger

object SqlCourseTopics {

  val vCourseTopics = {
    get[String]("CourseIDNumber") ~
	get[Long]("idCourse") ~
	get[String]("CourseName") ~
	get[Long]("LessonNumber") ~
	get[Long]("LessonIndex") ~
	get[String]("LessonName") ~
	get[String]("Topic") ~
	get[Long]("TopicsIndex") ~
	get[String]("Objective") map { case
    vCourseIDNumber ~
		vidCourse ~
		vCourseName ~
		vLessonNumber ~
		vLessonIndex ~
		vLessonName ~
		vTopic ~
		vTopicIndex ~
		vObjective =>
    MdlCourseTopics(vCourseIDNumber,
		vidCourse,
		vCourseName,
		vLessonNumber,
		vLessonIndex,
		vLessonName,
		vTopic,
		vTopicIndex,
		vObjective)
    }
  }

  	def all: List[MdlCourseTopics] = DB.withConnection { implicit c =>
  		SQL("select * from `CourseTopics`").as(vCourseTopics *)
	}

  	def selectWhere(where: String): List[MdlCourseTopics] = DB.withConnection { implicit c =>
  	    Logger.debug(where)
  		SQL("select * from `CourseTopics` WHERE " + where).as(vCourseTopics *)
	}

}