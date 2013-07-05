
package slick

import models.MdlLessonsSlick
import models.Mdl

trait LessonsComponent  {
	this: Profile =>
	  
	import profile.simple._

	object Lessons extends Table[MdlLessonsSlick]("Lessons") with Crud[MdlLessonsSlick, Long]  {

      def vLessonIndex = column[Long]("LessonIndex", O.PrimaryKey)
      def vLessonNumber = column[Long]("LessonNumber")
      def vLessonName = column[String]("LessonName")
      def vAssignment = column[String]("Assignment")
      def vLocation = column[String]("Location")
      def vidCourse = column[Long]("idCourse")
      def vDuration = column[Long]("Duration")
      def vLab = column[Boolean]("Lab")
      def vLessonSummary = column[String]("LessonSummary")
      def * = vLessonIndex.? ~ vLessonNumber ~ vLessonName ~ vAssignment ~ vLocation ~ vidCourse ~ vDuration ~ vLab ~ vLessonSummary<> (MdlLessonsSlick.apply _, MdlLessonsSlick.unapply _)
      def forInsert = vLessonNumber ~ vLessonName ~ vAssignment ~ vLocation ~ vidCourse ~ vDuration ~ vLab ~ vLessonSummary <> 
      ({t => MdlLessonsSlick(None , t._1, t._2, t._3, t._4, t._5, t._6, t._7, t._8)},
      {(vLessons: MdlLessonsSlick) => Some(vLessons.vLessonNumber, vLessons.vLessonName, vLessons.vAssignment, vLessons.vLocation, vLessons.vidCourse, vLessons.vDuration, vLessons.vLab, vLessons.vLessonSummary)})

	  def allQuery = {
	    AppDB.database.withSession { implicit session: Session =>
	      Query(Lessons)
	    }
	  }
	  
	  def all = {
	    AppDB.database.withSession { implicit session: Session =>
	      val q = Query(Lessons)
	      q.elements.toList
	    }
	  }
	  
	  def selectQuery(pk: Long)(implicit session: Session) = {
	      val q = Query(Lessons)
	      q.filter(p => p.vLessonIndex === pk)
	  }

      def select(pk: Long) = {
	    AppDB.database.withSession { implicit session: Session =>
	      selectQuery(pk).elements.toList.headOption
	    }
	  }
	  
	  def delete(pk: Long) {
	    AppDB.database.withSession { implicit session: Session =>
	      selectQuery(pk).delete
	    }
	  }

	  def insert(vLessons: MdlLessonsSlick) {
	    AppDB.database.withSession { implicit session: Session =>
	      Lessons.forInsert insert MdlLessonsSlick(None, vLessons.vLessonNumber, vLessons.vLessonName, vLessons.vAssignment, vLessons.vLocation, vLessons.vidCourse, vLessons.vDuration, vLessons.vLab, vLessons.vLessonSummary)

	    }
	  }
    

	  def update(vLessons: MdlLessonsSlick) {
	    AppDB.database.withSession { implicit session: Session =>
	      val q = selectQuery(vLessons.vLessonIndex.get)
	      val q2 = q.map(vLessons => vLessons.vLessonNumber ~ vLessons.vLessonName ~ vLessons.vAssignment ~ vLessons.vLocation ~ vLessons.vidCourse ~ vLessons.vDuration ~ vLessons.vLab ~ vLessons.vLessonSummary)
	      q2.update(vLessons.vLessonNumber, vLessons.vLessonName, vLessons.vAssignment, vLessons.vLocation, vLessons.vidCourse, vLessons.vDuration, vLessons.vLab, vLessons.vLessonSummary)
	    }
	  }

	}
}
