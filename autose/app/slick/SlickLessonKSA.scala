
package slick

import models.MdlLessonKSA
import models.Mdl

trait LessonKSAComponent  {
	this: Profile =>
	  
	import profile.simple._

	object LessonKSA extends Table[MdlLessonKSA]("LessonTopicObjectives") with Crud[MdlLessonKSA, Long]  {

      def vidLessonTopicObjectives = column[Long]("idLessonTopicObjectives", O.PrimaryKey)
      def vLesson = column[Long]("Lesson")
      def vTopicObjective = column[Long]("TopicObjective")
      def * = vidLessonTopicObjectives.? ~ vLesson ~ vTopicObjective<> (MdlLessonKSA.apply _, MdlLessonKSA.unapply _)
      def forInsert = vLesson ~ vTopicObjective <> 
      ({t => MdlLessonKSA(None , t._1, t._2)},
      {(vLessonTopicObjectives: MdlLessonKSA) => Some(vLessonTopicObjectives.vLesson, vLessonTopicObjectives.vTopicObjective)})

	  def allQuery = {
	    AppDB.database.withSession { implicit session: Session =>
	      Query(LessonKSA)
	    }
	  }
	  
	  def all = {
	    AppDB.database.withSession { implicit session: Session =>
	      val q = Query(LessonKSA)
	      q.elements.toList
	    }
	  }
	  
	  def selectQuery(pk: Long)(implicit session: Session) = {
	      val q = Query(LessonKSA)
	      q.filter(p => p.vidLessonTopicObjectives === pk)
	  }

      def select(pk: Long) = {
	    AppDB.database.withSession { implicit session: Session =>
	      selectQuery(pk).elements.toList.headOption
	    }
	  }
	  
      def joinLessonsByKSAQuery(idKSA: Long) = {
	    AppDB.database.withSession { implicit session: Session =>
	      val result = for {
	        (lesson, lsnksa) <- AppDB.dal.Lessons innerJoin AppDB.dal.LessonKSA on (_.vLessonIndex === _.vLesson)
	           if lsnksa.vTopicObjective === idKSA
	      } yield (lesson.vLessonIndex, lesson.vLessonNumber, lesson.vidCourse)
	      result
	    }	    
      }

      def joinLessonsByKSA(idKSA: Long) = {
	    AppDB.database.withSession { implicit session: Session =>
          joinLessonsByKSAQuery(idKSA).elements.toList
	    }
      }
      
	  def delete(pk: Long) {
	    AppDB.database.withSession { implicit session: Session =>
	      selectQuery(pk).delete
	    }
	  }

	  def insert(vLessonTopicObjectives: MdlLessonKSA) {
	    AppDB.database.withSession { implicit session: Session =>
	      LessonKSA.forInsert insert MdlLessonKSA(None, vLessonTopicObjectives.vLesson, vLessonTopicObjectives.vTopicObjective)

	    }
	  }
    

	  def update(vLessonTopicObjectives: MdlLessonKSA) {
	    AppDB.database.withSession { implicit session: Session =>
	      val q = selectQuery(vLessonTopicObjectives.vidLessonTopicObjectives.get)
	      val q2 = q.map(vLessonTopicObjectives => vLessonTopicObjectives.vLesson ~ vLessonTopicObjectives.vTopicObjective)
	      q2.update(vLessonTopicObjectives.vLesson, vLessonTopicObjectives.vTopicObjective)
	    }
	  }

	}
}
