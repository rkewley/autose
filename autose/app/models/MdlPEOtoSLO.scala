    
 package models
 
 import play.api.data.Forms._
 import play.api.data._
 import play.api.data.format.Formats._

    
 case class MdlPEOtoSLO (
 	vidPEOtoSLO : Option[Long],
	vPEO : Long,
	vSLO : Long
    ) extends Mdl[Long] {
    
      def this() = this(Option(0), 0, 0)

  	  def validate: Boolean = true
    
	  def validationErrors: String = ""
    
      def selectIdentifier: (String, String) = vidPEOtoSLO.get.toString -> vidPEOtoSLO.get.toString
    
      def compare(a: MdlPEOtoSLO, b: MdlPEOtoSLO) = a.vidPEOtoSLO.get.compareTo(b.vidPEOtoSLO.get)
        
      def primaryKey = vidPEOtoSLO
}
 
object MdlPEOtoSLOList {
  val formList = Form[MdlPEOtoSLOList](
    mapping (
	"fidPEOtoSLO" -> optional(of[Long]),
	"fPEO" -> of[Long],
	"fSLO" -> list(of[Long])
    )(MdlPEOtoSLOList.apply)(MdlPEOtoSLOList.unapply)
  )    
  }
 
 case class MdlPEOtoSLOList (
 	vidPEOtoSLO : Option[Long],
	vPEO : Long,
	vSLO : List[Long]
    ) {
    
    def getList = {
      vSLO.map(slo => 
        new MdlPEOtoSLO(vidPEOtoSLO, vPEO, slo))
    }
 }
 

    