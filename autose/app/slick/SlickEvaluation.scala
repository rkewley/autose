
package slick

import models.MdlEvaluation
import models.Mdl

trait EvaluationComponent  {
	this: Profile =>
	  
	import profile.simple._

	object Evaluation extends Table[MdlEvaluation]("Evaluation") with Crud[MdlEvaluation, Long]  {

      def vidEvaluation = column[Long]("idEvaluation", O.PrimaryKey)
      def vName = column[String]("Name")
      def vProgram = column[Long]("Program")
      def vYear = column[Long]("Year")
      def * = vidEvaluation.? ~ vName ~ vProgram ~ vYear<> (MdlEvaluation.apply _, MdlEvaluation.unapply _)
      def forInsert = vName ~ vProgram ~ vYear <> 
      ({t => MdlEvaluation(None , t._1, t._2, t._3)},
      {(vEvaluation: MdlEvaluation) => Some(vEvaluation.vName, vEvaluation.vProgram, vEvaluation.vYear)})

	  def allQuery = {
	    AppDB.database.withSession { implicit session: Session =>
	      Query(Evaluation)
	    }
	  }
	  
	  def all = {
	    AppDB.database.withSession { implicit session: Session =>
	      val q = Query(Evaluation)
	      q.elements.toList
	    }
	  }
	  
	  def selectQuery(pk: Long)(implicit session: Session) = {
	      val q = Query(Evaluation)
	      q.filter(p => p.vidEvaluation === pk)
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

	  def insert(vEvaluation: MdlEvaluation): Long = {
	    AppDB.database.withSession { implicit session: Session =>
	      Evaluation.forInsert insert MdlEvaluation(None, vEvaluation.vName, vEvaluation.vProgram, vEvaluation.vYear)

	    }
	  }
    

	  def update(vEvaluation: MdlEvaluation) {
	    AppDB.database.withSession { implicit session: Session =>
	      val q = selectQuery(vEvaluation.vidEvaluation.get)
	      val q2 = q.map(vEvaluation => vEvaluation.vName ~ vEvaluation.vProgram ~ vEvaluation.vYear)
	      q2.update(vEvaluation.vName, vEvaluation.vProgram, vEvaluation.vYear)
	    }
	  }

	}
}
