
package slick

import models.MdlPerformanceIndicator
import models.Mdl

trait PerformanceIndicatorComponent  {
	this: Profile =>
	  
	import profile.simple._

	object PerformanceIndicator extends Table[MdlPerformanceIndicator]("PerformanceIndicator") with Crud[MdlPerformanceIndicator, Long]  {

      def vidPerformanceIndicator = column[Long]("idPerformanceIndicator", O.PrimaryKey, O.AutoInc)
      def vPerformanceIndicator = column[String]("PerformanceIndicator")
      def vProgramOutcome = column[Long]("ProgramOutcome")
      def * = vidPerformanceIndicator.? ~ vPerformanceIndicator ~ vProgramOutcome<> (MdlPerformanceIndicator.apply _, MdlPerformanceIndicator.unapply _)
      def forInsert = vPerformanceIndicator ~ vProgramOutcome <> 
      ({t => MdlPerformanceIndicator(None , t._1, t._2)},
      {(vPerformanceIndicator: MdlPerformanceIndicator) => Some(vPerformanceIndicator.vPerformanceIndicator, vPerformanceIndicator.vProgramOutcome)})

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
	      q.filter(p => p.vidPerformanceIndicator === pk)
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

	  def insert(vPerformanceIndicator: MdlPerformanceIndicator): Long = {
	    AppDB.database.withSession { implicit session: Session =>
	      PerformanceIndicator.forInsert returning PerformanceIndicator.vidPerformanceIndicator insert MdlPerformanceIndicator(None, vPerformanceIndicator.vPerformanceIndicator, vPerformanceIndicator.vProgramOutcome)

	    }
	  }
	  
	  def selectByProgramOutcome(programOutcome: Long) = {
	    AppDB.database.withSession { implicit session: Session =>
	      val q = Query(PerformanceIndicator)
	      q.filter(p => p.vProgramOutcome === programOutcome).elements.toList
	    }	    
	  }
	  
      def selectPerformanceIndicatorsByTopic(topic: Long) = {
	    AppDB.database.withSession { implicit session: Session =>
	      val result = for {
	        (ksa, ksapi) <- AppDB.dal.KSA innerJoin AppDB.dal.KSAPerfIndicator on (_.vTopicObjectiveNumber === _.vKSA)
	           if ksa.vTopic === topic
	      } yield (ksapi.vPerformanceIndicator)
	      val result2 = for {
	        (pi, piIndex) <- AppDB.dal.PerformanceIndicator innerJoin result on (_.vidPerformanceIndicator === _)
	      } yield (pi.vProgramOutcome, pi.vPerformanceIndicator)
	      val result3 = for {
	        (pi2, po) <- result2 innerJoin AppDB.dal.ProgramOutcomes on (_._1 === _.vProgramOutcomeNumber)
	      } yield (po.vProgram, pi2._2)
	      val result4 = for {
	        (pipo, pgm) <- result3 innerJoin AppDB.dal.Programs on (_._1 === _.vidPrograms)
	      } yield (pgm.vProgram, pipo._2)
	      result3.elements.toList.distinct
	    }	    
      }
    

	  def update(vPerformanceIndicator: MdlPerformanceIndicator) {
	    AppDB.database.withSession { implicit session: Session =>
	      val q = selectQuery(vPerformanceIndicator.vidPerformanceIndicator.get)
	      val q2 = q.map(vPerformanceIndicator => vPerformanceIndicator.vPerformanceIndicator ~ vPerformanceIndicator.vProgramOutcome)
	      q2.update(vPerformanceIndicator.vPerformanceIndicator, vPerformanceIndicator.vProgramOutcome)
	    }
	  }

	}
}
