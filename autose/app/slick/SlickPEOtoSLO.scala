
package slick

import models.MdlPEOtoSLO

trait PEOtoSLOComponent  {
	this: Profile =>
	  
	import profile.simple._

	object PEOtoSLO extends Table[MdlPEOtoSLO]("PEOtoSLO") with Crud[MdlPEOtoSLO, Long]  {

      def vidPEOtoSLO = column[Long]("idPEOtoSLO", O.PrimaryKey, O.AutoInc)
      def vPEO = column[Long]("PEO")
      def vSLO = column[Long]("SLO")
      def * = vidPEOtoSLO.? ~ vPEO ~ vSLO<> (MdlPEOtoSLO.apply _, MdlPEOtoSLO.unapply _)
      def forInsert = vPEO ~ vSLO <> 
      ({t => MdlPEOtoSLO(None , t._1, t._2)},
      {(vPEOtoSLO: MdlPEOtoSLO) => Some(vPEOtoSLO.vPEO, vPEOtoSLO.vSLO)})

	  def allQuery = {
	    AppDB.database.withSession { implicit session: Session =>
	      Query(PEOtoSLO)
	    }
	  }
	  
	  def all = {
	    AppDB.database.withSession { implicit session: Session =>
	      val q = Query(PEOtoSLO)
	      q.elements.toList
	    }
	  }
	  
	  def selectByPEO(peo: Long) = {
	    AppDB.database.withSession { implicit session: Session =>
	      val q = Query(PEOtoSLO).filter(p => p.vPEO === peo)
	      q.elements.toList
	    }
	  }
	  
	  def selectQuery(pk: Long)(implicit session: Session) = {
	      val q = Query(PEOtoSLO)
	      q.filter(p => p.vidPEOtoSLO === pk)
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

	  def insert(vPEOtoSLO: MdlPEOtoSLO): Long = {
	    AppDB.database.withSession { implicit session: Session =>
	      PEOtoSLO.forInsert returning PEOtoSLO.vidPEOtoSLO insert MdlPEOtoSLO(None, vPEOtoSLO.vPEO, vPEOtoSLO.vSLO)

	    }
	  }
    

	  def update(vPEOtoSLO: MdlPEOtoSLO) {
	    AppDB.database.withSession { implicit session: Session =>
	      val q = selectQuery(vPEOtoSLO.vidPEOtoSLO.get)
	      val q2 = q.map(vPEOtoSLO => vPEOtoSLO.vPEO ~ vPEOtoSLO.vSLO)
	      q2.update(vPEOtoSLO.vPEO, vPEOtoSLO.vSLO)
	    }
	  }

	}
}
