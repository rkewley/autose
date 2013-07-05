
package slick

import models.MdlKSA
import models.Mdl

trait KSAComponent  {
	this: Profile =>
	  
	import profile.simple._

	object KSA extends Table[MdlKSA]("TopicObjectives") with Crud[MdlKSA, Long]  {

      def vTopicObjectiveNumber = column[Long]("TopicObjectiveNumber", O.PrimaryKey)
      def vObjective = column[String]("Objective")
      def vTopic = column[Long]("Topic")
      def vKSAB = column[String]("KSAB")
      def * = vTopicObjectiveNumber.? ~ vObjective ~ vTopic ~ vKSAB<> (MdlKSA.apply _, MdlKSA.unapply _)
      def forInsert = vObjective ~ vTopic ~ vKSAB <> 
      ({t => MdlKSA(None , t._1, t._2, t._3)},
      {(vTopicObjectives: MdlKSA) => Some(vTopicObjectives.vObjective, vTopicObjectives.vTopic, vTopicObjectives.vKSAB)})

	  def allQuery = {
	    AppDB.database.withSession { implicit session: Session =>
	      Query(KSA)
	    }
	  }
	  
	  def all = {
	    AppDB.database.withSession { implicit session: Session =>
	      val q = Query(KSA)
	      q.elements.toList
	    }
	  }
	  
	  def selectQuery(pk: Long)(implicit session: Session) = {
	      val q = Query(KSA)
	      q.filter(p => p.vTopicObjectiveNumber === pk)
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
	  
	  def insert(vTopicObjectives: MdlKSA) {
	    AppDB.database.withSession { implicit session: Session =>
	      KSA.forInsert insert MdlKSA(None, vTopicObjectives.vObjective, vTopicObjectives.vTopic, vTopicObjectives.vKSAB)

	    }
	  }
    

	  def update(vTopicObjectives: MdlKSA) {
	    AppDB.database.withSession { implicit session: Session =>
	      val q = selectQuery(vTopicObjectives.vTopicObjectiveNumber.get)
	      val q2 = q.map(vTopicObjectives => vTopicObjectives.vObjective ~ vTopicObjectives.vTopic ~ vTopicObjectives.vKSAB)
	      q2.update(vTopicObjectives.vObjective, vTopicObjectives.vTopic, vTopicObjectives.vKSAB)
	    }
	  }
	  
	  class KSALessonData(val lessonIndex: Long, val lessonNumber: Long)
	  class KSASubEventData(val gradedEventIndex: Long, val description: String)
	  class KSAGradedEventData(val eventIndex: Long, val eventName: String, val subEvents: List[KSASubEventData])
	  class KSATeachData(val courseIndex: Long, val courseNumber: String, val lessons: List[KSALessonData], val gradedEvents: List[KSAGradedEventData])

	  def buildKSATeachData(idKSA: Long) = {
	    val subEventsResults = AppDB.dal.KSASubGradedEvent.joinSubGradedEventByKSA(idKSA)
	    val gradedEventResults = AppDB.dal.KSAGradedEvent.joinGradedEventByKSA(idKSA)
	    val lessonResults = AppDB.dal.LessonKSA.joinLessonsByKSA(idKSA)
	    val courseList: List[Long] = (gradedEventResults.map(_._3) ::: lessonResults.map(_._3)).distinct
	    val teachData = courseList.map { courseId =>
	      val gradedEvents = gradedEventResults.filter(result => result._3 == courseId).map { gradedEventResult =>
	        val subEvents = subEventsResults.filter(result => result._3 == gradedEventResult._1).map { subEventResult => 
	          new KSASubEventData(subEventResult._1, subEventResult._2)
	        }
	        new KSAGradedEventData(gradedEventResult._1, gradedEventResult._2, subEvents)
	      }
	      val lessons = lessonResults.filter(result => result._3 == courseId).map { lessonResult =>
	        new KSALessonData(lessonResult._1, lessonResult._2)
	      }
	      val courseNumber = AppDB.dal.Courses.select(courseId).get.vCourseIDNumber
	      new KSATeachData(courseId, courseNumber, lessons, gradedEvents)
	    }
	    teachData
	  }
	}
}
