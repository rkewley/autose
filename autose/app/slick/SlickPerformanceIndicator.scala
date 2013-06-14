
package slick

import models.MdlPerformanceIndicator
import models.Mdl

trait PerformanceIndicatorComponent  {
	this: Profile =>
	  
	import profile.simple._

	object PerformanceIndicator extends Table[MdlPerformanceIndicator]("TerminalLearningObjective") with Crud[MdlPerformanceIndicator, Long]  {

      def vidTerminalLearningObjective = column[Long]("idTerminalLearningObjective", O.PrimaryKey)
      def vTerminalLearningObjective = column[String]("TerminalLearningObjective")
      def vTopic = column[Long]("Topic")
      def vProgram = column[Long]("Program")
      def * = vidTerminalLearningObjective.? ~ vTerminalLearningObjective ~ vTopic ~ vProgram<> (MdlPerformanceIndicator.apply _, MdlPerformanceIndicator.unapply _)
      def forInsert = vTerminalLearningObjective ~ vTopic ~ vProgram <> 
      ({t => MdlPerformanceIndicator(None , t._1, t._2, t._3)},
      {(vTerminalLearningObjective: MdlPerformanceIndicator) => Some(vTerminalLearningObjective.vTerminalLearningObjective, vTerminalLearningObjective.vTopic, vTerminalLearningObjective.vProgram)})

	  def allQuery = {
	    AppDB.database.withSession { implicit session: Session =>
	      Query(PerformanceIndicator)
	    }
	  }
	  
	  def all = {
	    AppDB.database.withSession { implicit session: Session =>
	      val q = Query(PerformanceIndicator)
	      q.elements.toList
	    }
	  }
	  
	  def selectQuery(pk: Long)(implicit session: Session) = {
	      val q = Query(PerformanceIndicator)
	      q.filter(p => p.vidTerminalLearningObjective === pk)
	  }

      def select(pk: Long) = {
	    AppDB.database.withSession { implicit session: Session =>
	      selectQuery(pk).elements.toList.headOption
	    }
	  }
      
      def selectByProgram(idProgram: Long) = {
	    AppDB.database.withSession { implicit session: Session =>
	      val q = Query(PerformanceIndicator)
	      q.filter(p => p.vProgram === idProgram).elements.toList
	    }
      }
	  
      def selectByTopic(idTopic: Long) = {
	    AppDB.database.withSession { implicit session: Session =>
	      val q = Query(PerformanceIndicator)
	      q.filter(p => p.vTopic === idTopic).elements.toList
	    }
      }

      def delete(pk: Long) {
	    AppDB.database.withSession { implicit session: Session =>
	      selectQuery(pk).delete
	    }
	  }

	  def insert(vTerminalLearningObjective: MdlPerformanceIndicator) {
	    AppDB.database.withSession { implicit session: Session =>
	      PerformanceIndicator.forInsert insert MdlPerformanceIndicator(None, vTerminalLearningObjective.vTerminalLearningObjective, vTerminalLearningObjective.vTopic, vTerminalLearningObjective.vProgram)

	    }
	  }
    

	  def update(vTerminalLearningObjective: MdlPerformanceIndicator) {
	    AppDB.database.withSession { implicit session: Session =>
	      val q = selectQuery(vTerminalLearningObjective.vidTerminalLearningObjective.get)
	      val q2 = q.map(vTerminalLearningObjective => vTerminalLearningObjective.vTerminalLearningObjective ~ vTerminalLearningObjective.vTopic ~ vTerminalLearningObjective.vProgram)
	      q2.update(vTerminalLearningObjective.vTerminalLearningObjective, vTerminalLearningObjective.vTopic, vTerminalLearningObjective.vProgram)
	    }
	  }

	}
}
