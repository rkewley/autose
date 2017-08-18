
package slick

import models.MdlLessonObjective
import models.Mdl

trait LessonObjectiveComponent  {
	this: Profile =>
	  
	import profile.simple._

	object LessonObjective extends Table[MdlLessonObjective]("LessonObjective") with Crud[MdlLessonObjective, Long]  {

      def vidLessonObjective = column[Long]("idLessonObjective", O.PrimaryKey)
      def vLesson = column[Long]("Lesson")
      def vSupportedCourseObjective = column[Long]("SupportedCourseObjective")
      def vLessonObjective = column[String]("LessonObjective")
      def * = vidLessonObjective.? ~ vLesson ~ vSupportedCourseObjective ~ vLessonObjective<> (MdlLessonObjective.apply _, MdlLessonObjective.unapply _)
      def forInsert = vLesson ~ vSupportedCourseObjective ~ vLessonObjective <> 
      ({t => MdlLessonObjective(None , t._1, t._2, t._3)},
      {(vLessonObjective: MdlLessonObjective) => Some(vLessonObjective.vLesson, vLessonObjective.vSupportedCourseObjective, vLessonObjective.vLessonObjective)})

	  def allQuery = {
	    AppDB.database.withSession { implicit session: Session =>
	      Query(LessonObjective)
	    }
	  }
	  
	  def all = {
	    AppDB.database.withSession { implicit session: Session =>
	      val q = Query(LessonObjective)
	      q.elements.toList
	    }
	  }
	  
	  def selectQuery(pk: Long)(implicit session: Session) = {
	      val q = Query(LessonObjective)
	      q.filter(p => p.vidLessonObjective === pk)
	  }

		def select(pk: Long) = {
	    AppDB.database.withSession { implicit session: Session =>
	      selectQuery(pk).elements.toList.headOption
	    }
	  }

		def selectForLesson(vLessonId: Long): List[MdlLessonObjective] = {
			AppDB.database.withSession { implicit session: Session =>
				val q = Query(LessonObjective)
				q.filter(p => p.vLesson === vLessonId).elements.toList
			}
		}
	  
	  def delete(pk: Long) {
	    AppDB.database.withSession { implicit session: Session =>
	      selectQuery(pk).delete
	    }
	  }

	  def insert(vLessonObjective: MdlLessonObjective): Long = {
	    AppDB.database.withSession { implicit session: Session =>
	      LessonObjective.forInsert insert MdlLessonObjective(None, vLessonObjective.vLesson, vLessonObjective.vSupportedCourseObjective, vLessonObjective.vLessonObjective)

	    }
	  }
    

	  def update(vLessonObjective: MdlLessonObjective) {
	    AppDB.database.withSession { implicit session: Session =>
	      val q = selectQuery(vLessonObjective.vidLessonObjective.get)
	      val q2 = q.map(vLessonObjective => vLessonObjective.vLesson ~ vLessonObjective.vSupportedCourseObjective ~ vLessonObjective.vLessonObjective)
	      q2.update(vLessonObjective.vLesson, vLessonObjective.vSupportedCourseObjective, vLessonObjective.vLessonObjective)
	    }
	  }

	}
}
