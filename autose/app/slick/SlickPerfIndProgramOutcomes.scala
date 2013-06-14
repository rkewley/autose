
package slick

import models.MdlPerfIndProgramOutcomes
import models.Mdl

trait PerfIndProgramOutcomesComponent  {
	this: Profile =>
	  
	import profile.simple._

	object PerfIndProgramOutcomes extends Table[MdlPerfIndProgramOutcomes]("PerfIndProgramOutcomes") with Crud[MdlPerfIndProgramOutcomes, Long]  {

      def vidPerfIndProgramOutcomes = column[Long]("idPerfIndProgramOutcomes", O.PrimaryKey)
      def vPerformanceIndicator = column[Long]("PerformanceIndicator")
      def vProgramOutcome = column[Long]("ProgramOutcome")
      def * = vidPerfIndProgramOutcomes.? ~ vPerformanceIndicator ~ vProgramOutcome<> (MdlPerfIndProgramOutcomes.apply _, MdlPerfIndProgramOutcomes.unapply _)
      def forInsert = vPerformanceIndicator ~ vProgramOutcome <> 
      ({t => MdlPerfIndProgramOutcomes(None , t._1, t._2)},
      {(vPerfIndProgramOutcomes: MdlPerfIndProgramOutcomes) => Some(vPerfIndProgramOutcomes.vPerformanceIndicator, vPerfIndProgramOutcomes.vProgramOutcome)})

	  def allQuery = {
	    AppDB.database.withSession { implicit session: Session =>
	      Query(PerfIndProgramOutcomes)
	    }
	  }
	  
	  def all = {
	    AppDB.database.withSession { implicit session: Session =>
	      val q = Query(PerfIndProgramOutcomes)
	      q.elements.toList
	    }
	  }
	  
	  def selectQuery(pk: Long)(implicit session: Session) = {
	      val q = Query(PerfIndProgramOutcomes)
	      q.filter(p => p.vidPerfIndProgramOutcomes === pk)
	  }

      def select(pk: Long) = {
	    AppDB.database.withSession { implicit session: Session =>
	      selectQuery(pk).elements.toList.headOption
	    }
	  }
      
      def selectByProgramOutcome(programOutcome: Long) = {
	    AppDB.database.withSession { implicit session: Session =>
	      val q = Query(PerfIndProgramOutcomes)
	      q.filter(p => p.vProgramOutcome === programOutcome).elements.toList  
	    }
      }
	  
	  def delete(pk: Long) {
	    AppDB.database.withSession { implicit session: Session =>
	      selectQuery(pk).delete
	    }
	  }

	  def insert(vPerfIndProgramOutcomes: MdlPerfIndProgramOutcomes) {
	    AppDB.database.withSession { implicit session: Session =>
	      PerfIndProgramOutcomes.forInsert insert MdlPerfIndProgramOutcomes(None, vPerfIndProgramOutcomes.vPerformanceIndicator, vPerfIndProgramOutcomes.vProgramOutcome)

	    }
	  }
    

	  def update(vPerfIndProgramOutcomes: MdlPerfIndProgramOutcomes) {
	    AppDB.database.withSession { implicit session: Session =>
	      val q = selectQuery(vPerfIndProgramOutcomes.vidPerfIndProgramOutcomes.get)
	      val q2 = q.map(vPerfIndProgramOutcomes => vPerfIndProgramOutcomes.vPerformanceIndicator ~ vPerfIndProgramOutcomes.vProgramOutcome)
	      q2.update(vPerfIndProgramOutcomes.vPerformanceIndicator, vPerfIndProgramOutcomes.vProgramOutcome)
	    }
	  }

	}
}
