
package slick

import models.MdlProgramOutcomes
import models.Mdl

trait ProgramOutcomesComponent  {
	this: Profile =>
	  
	import profile.simple._

	object ProgramOutcomes extends Table[MdlProgramOutcomes]("ProgramOutcomes") with Crud[MdlProgramOutcomes, Long]  {

      def vProgramOutcomeNumber = column[Long]("ProgramOutcomeNumber", O.PrimaryKey, O.AutoInc)
      def vProgram = column[Long]("Program")
      def vProgramOutcome = column[String]("ProgramOutcome")
		  def vEvaluation = column[Long]("Evaluation")

		def * = vProgramOutcomeNumber.? ~ vProgram ~ vProgramOutcome ~ vEvaluation<> (MdlProgramOutcomes.apply _, MdlProgramOutcomes.unapply _)
      def forInsert = vProgram ~ vProgramOutcome ~ vEvaluation <>
      ({t => MdlProgramOutcomes(None , t._1, t._2, t._3)},
      {(vProgramOutcomes: MdlProgramOutcomes) => Some(vProgramOutcomes.vProgram, vProgramOutcomes.vProgramOutcome, vProgramOutcomes.vEvaluation)})

	  def allQuery = {
	    AppDB.database.withSession { implicit session: Session =>
	      Query(ProgramOutcomes)
	    }
	  }
	  
	  def all = {
	    AppDB.database.withSession { implicit session: Session =>
	      val q = Query(ProgramOutcomes)
	      q.elements.toList
	    }
	  }
	  
	  def selectByProgram(program: Long) = {
	    AppDB.database.withSession { implicit session: Session =>
	      val q = Query(ProgramOutcomes).filter(p => p.vProgram === program)
	      q.elements.toList
	    }
	  }
	  
	  def selectQuery(pk: Long)(implicit session: Session) = {
	      val q = Query(ProgramOutcomes)
	      q.filter(p => p.vProgramOutcomeNumber === pk)
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

	  def insert(vProgramOutcomes: MdlProgramOutcomes): Long = {
	    AppDB.database.withSession { implicit session: Session =>
	      ProgramOutcomes.forInsert returning ProgramOutcomes.vProgramOutcomeNumber insert MdlProgramOutcomes(None, vProgramOutcomes.vProgram, vProgramOutcomes.vProgramOutcome, vProgramOutcomes.vEvaluation)

	    }
	  }
    

	  def update(vProgramOutcomes: MdlProgramOutcomes) {
	    AppDB.database.withSession { implicit session: Session =>
	      val q = selectQuery(vProgramOutcomes.vProgramOutcomeNumber.get)
	      val q2 = q.map(vProgramOutcomes => vProgramOutcomes.vProgram ~ vProgramOutcomes.vProgramOutcome)
	      q2.update(vProgramOutcomes.vProgram, vProgramOutcomes.vProgramOutcome)
	    }
	  }

	}
}
