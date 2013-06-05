
package slick

import models.MdlGradedRequirements
import models.Mdl

trait GradedRequirementsComponent  {
	this: Profile =>
	  
	import profile.simple._

	object GradedRequirements extends Table[MdlGradedRequirements]("GradedRequirements") with Crud[MdlGradedRequirements, Long]  {

      def vGradedEventIndex = column[Long]("GradedEventIndex", O.PrimaryKey)
      def vCourse = column[Long]("Course")
      def vGradedEventName = column[String]("GradedEventName")
      def vEventDescription = column[String]("EventDescription")
      def vTypeOfEvent = column[String]("TypeOfEvent")
      def vPoints = column[Double]("Points")
      def vLessonassigned = column[Long]("Lessonassigned")
      def vLessoncompleted = column[Long]("Lessoncompleted")
      def * = vGradedEventIndex.? ~ vCourse ~ vGradedEventName ~ vEventDescription ~ vTypeOfEvent ~ vPoints ~ vLessonassigned ~ vLessoncompleted<> (MdlGradedRequirements.apply _, MdlGradedRequirements.unapply _)
      def forInsert = vCourse ~ vGradedEventName ~ vEventDescription ~ vTypeOfEvent ~ vPoints ~ vLessonassigned ~ vLessoncompleted <> 
      ({t => MdlGradedRequirements(None , t._1, t._2, t._3, t._4, t._5, t._6, t._7)},
      {(vGradedRequirements: MdlGradedRequirements) => Some(vGradedRequirements.vCourse, vGradedRequirements.vGradedEventName, vGradedRequirements.vEventDescription, vGradedRequirements.vTypeOfEvent, vGradedRequirements.vPoints, vGradedRequirements.vLessonassigned, vGradedRequirements.vLessoncompleted)})

	  def allQuery = {
	    AppDB.database.withSession { implicit session: Session =>
	      Query(GradedRequirements)
	    }
	  }
	  
	  def all = {
	    AppDB.database.withSession { implicit session: Session =>
	      val q = Query(GradedRequirements)
	      q.elements.toList
	    }
	  }
	  
	  def selectQuery(pk: Long)(implicit session: Session) = {
	      val q = Query(GradedRequirements)
	      q.filter(p => p.vGradedEventIndex === pk)
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

	  def insert(vGradedRequirements: MdlGradedRequirements) {
	    AppDB.database.withSession { implicit session: Session =>
	      GradedRequirements.forInsert insert MdlGradedRequirements(None, vGradedRequirements.vCourse, vGradedRequirements.vGradedEventName, vGradedRequirements.vEventDescription, vGradedRequirements.vTypeOfEvent, vGradedRequirements.vPoints, vGradedRequirements.vLessonassigned, vGradedRequirements.vLessoncompleted)

	    }
	  }
    

	  def update(vGradedRequirements: MdlGradedRequirements) {
	    AppDB.database.withSession { implicit session: Session =>
	      val q = selectQuery(vGradedRequirements.vGradedEventIndex.get)
	      val q2 = q.map(vGradedRequirements => vGradedRequirements.vCourse ~ vGradedRequirements.vGradedEventName ~ vGradedRequirements.vEventDescription ~ vGradedRequirements.vTypeOfEvent ~ vGradedRequirements.vPoints ~ vGradedRequirements.vLessonassigned ~ vGradedRequirements.vLessoncompleted)
	      q2.update(vGradedRequirements.vCourse, vGradedRequirements.vGradedEventName, vGradedRequirements.vEventDescription, vGradedRequirements.vTypeOfEvent, vGradedRequirements.vPoints, vGradedRequirements.vLessonassigned, vGradedRequirements.vLessoncompleted)
	    }
	  }

	}
}
