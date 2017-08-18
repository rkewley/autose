
package slick

import models.MdlEvalCourseObjSubEvent
import models.Mdl

trait EvalCourseObjSubEventComponent  {
	this: Profile =>
	  
	import profile.simple._

	object EvalCourseObjSubEvent extends Table[MdlEvalCourseObjSubEvent]("EvalCourseObjSubEvent") with Crud[MdlEvalCourseObjSubEvent, Long]  {

      def vidEvalCourseObjSubEvent = column[Long]("idEvalCourseObjSubEvent", O.PrimaryKey)
      def vEvaluation = column[Long]("Evaluation")
      def vCourseObjSubEvent = column[Long]("CourseObjSubEvent")
      def * = vidEvalCourseObjSubEvent.? ~ vEvaluation ~ vCourseObjSubEvent<> (MdlEvalCourseObjSubEvent.apply _, MdlEvalCourseObjSubEvent.unapply _)
      def forInsert = vEvaluation ~ vCourseObjSubEvent <> 
      ({t => MdlEvalCourseObjSubEvent(None , t._1, t._2)},
      {(vEvalCourseObjSubEvent: MdlEvalCourseObjSubEvent) => Some(vEvalCourseObjSubEvent.vEvaluation, vEvalCourseObjSubEvent.vCourseObjSubEvent)})

	  def allQuery = {
	    AppDB.database.withSession { implicit session: Session =>
	      Query(EvalCourseObjSubEvent)
	    }
	  }
	  
	  def all = {
	    AppDB.database.withSession { implicit session: Session =>
	      val q = Query(EvalCourseObjSubEvent)
	      q.elements.toList
	    }
	  }
	  
	  def selectQuery(pk: Long)(implicit session: Session) = {
	      val q = Query(EvalCourseObjSubEvent)
	      q.filter(p => p.vidEvalCourseObjSubEvent === pk)
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

	  def insert(vEvalCourseObjSubEvent: MdlEvalCourseObjSubEvent): Long = {
	    AppDB.database.withSession { implicit session: Session =>
	      EvalCourseObjSubEvent.forInsert insert MdlEvalCourseObjSubEvent(None, vEvalCourseObjSubEvent.vEvaluation, vEvalCourseObjSubEvent.vCourseObjSubEvent)

	    }
	  }
    

	  def update(vEvalCourseObjSubEvent: MdlEvalCourseObjSubEvent) {
	    AppDB.database.withSession { implicit session: Session =>
	      val q = selectQuery(vEvalCourseObjSubEvent.vidEvalCourseObjSubEvent.get)
	      val q2 = q.map(vEvalCourseObjSubEvent => vEvalCourseObjSubEvent.vEvaluation ~ vEvalCourseObjSubEvent.vCourseObjSubEvent)
	      q2.update(vEvalCourseObjSubEvent.vEvaluation, vEvalCourseObjSubEvent.vCourseObjSubEvent)
	    }
	  }

	}
}
