
package slick

import models.MdlKSAPerfIndicator
import models.Mdl

trait KSAPerfIndicatorComponent  {
	this: Profile =>
	  
	import profile.simple._

	object KSAPerfIndicator extends Table[MdlKSAPerfIndicator]("KSAPerfIndicator") with Crud[MdlKSAPerfIndicator, Long]  {

      def vidKSAPerfIndicator = column[Long]("idKSAPerfIndicator", O.PrimaryKey)
      def vKSA = column[Long]("KSA")
      def vPerformanceIndicator = column[Long]("PerformanceIndicator")
      def * = vidKSAPerfIndicator.? ~ vKSA ~ vPerformanceIndicator<> (MdlKSAPerfIndicator.apply _, MdlKSAPerfIndicator.unapply _)
      def forInsert = vKSA ~ vPerformanceIndicator <> 
      ({t => MdlKSAPerfIndicator(None , t._1, t._2)},
      {(vKSAPerfIndicator: MdlKSAPerfIndicator) => Some(vKSAPerfIndicator.vKSA, vKSAPerfIndicator.vPerformanceIndicator)})

	  def allQuery = {
	    AppDB.database.withSession { implicit session: Session =>
	      Query(KSAPerfIndicator)
	    }
	  }
	  
	  def all = {
	    AppDB.database.withSession { implicit session: Session =>
	      val q = Query(KSAPerfIndicator)
	      q.elements.toList
	    }
	  }
	  
	  def selectQuery(pk: Long)(implicit session: Session) = {
	      val q = Query(KSAPerfIndicator)
	      q.filter(p => p.vidKSAPerfIndicator === pk)
	  }

      def select(pk: Long) = {
	    AppDB.database.withSession { implicit session: Session =>
	      selectQuery(pk).elements.toList.headOption
	    }
	  }
      
      def selectByPerfIndicator(perfIndicator: Long) = {
	    AppDB.database.withSession { implicit session: Session =>
	      val q = Query(KSAPerfIndicator)
	      q.filter(p => p.vPerformanceIndicator === perfIndicator).elements.toList  
	    }
      }
      
      def joinKSAByPerfIndicator(perfIndicator: Long) = {
	    AppDB.database.withSession { implicit session: Session =>
	      println("Joining KSA for performance indicator: " + perfIndicator)
	      val result = for {
	        (ksa, piksa) <- AppDB.dal.KSA innerJoin AppDB.dal.KSAPerfIndicator on (_.vTopicObjectiveNumber === _.vKSA)
	           if piksa.vPerformanceIndicator === perfIndicator
	      } yield (piksa.vKSA, ksa.vObjective, ksa.vKSAB)
	      println("There are " + result.length + " results")
	      result.elements.toList
	    }	    
      }

	  
	  def delete(pk: Long) {
	    AppDB.database.withSession { implicit session: Session =>
	      selectQuery(pk).delete
	    }
	  }

	  def insert(vKSAPerfIndicator: MdlKSAPerfIndicator) {
	    AppDB.database.withSession { implicit session: Session =>
	      KSAPerfIndicator.forInsert insert MdlKSAPerfIndicator(None, vKSAPerfIndicator.vKSA, vKSAPerfIndicator.vPerformanceIndicator)

	    }
	  }
    

	  def update(vKSAPerfIndicator: MdlKSAPerfIndicator) {
	    AppDB.database.withSession { implicit session: Session =>
	      val q = selectQuery(vKSAPerfIndicator.vidKSAPerfIndicator.get)
	      val q2 = q.map(vKSAPerfIndicator => vKSAPerfIndicator.vKSA ~ vKSAPerfIndicator.vPerformanceIndicator)
	      q2.update(vKSAPerfIndicator.vKSA, vKSAPerfIndicator.vPerformanceIndicator)
	    }
	  }

	}
}
