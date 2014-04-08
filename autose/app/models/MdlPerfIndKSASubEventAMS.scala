    
 package models
 
 import play.api.data.Forms._
 import play.api.data._
 import play.api.data.format.Formats._

 case class MdlPerfIndKSASubEventAMS (
 	vidPerfIndKSASubEventAMS : Option[Long],
	vPerformanceIndicator : Long,
	vKSASubEventAMS : Long
    ) extends Mdl[Long] {
    
      def this() = this(Option(0), 0, 0)

  	  def validate: Boolean = true
    
	  def validationErrors: String = ""
    
      def selectIdentifier: (String, String) = vidPerfIndKSASubEventAMS.get.toString -> vidPerfIndKSASubEventAMS.get.toString
    
      def compare(a: MdlPerfIndKSASubEventAMS, b: MdlPerfIndKSASubEventAMS) = a.vidPerfIndKSASubEventAMS.get.compareTo(b.vidPerfIndKSASubEventAMS.get)
        
      def primaryKey = vidPerfIndKSASubEventAMS
}
    
object MdlPerfIndKSASubEventAMSList {
  val formList = Form[MdlPerfIndKSASubEventAMSList](
    mapping (
	"fidPerfIndKSASubEventAMS" -> optional(of[Long]),
	"fPerformanceIndicator" -> of[Long],
	"fKSASubEventAMS" -> list(of[Long])
    )(MdlPerfIndKSASubEventAMSList.apply)(MdlPerfIndKSASubEventAMSList.unapply)
  )    
  }
 
 case class MdlPerfIndKSASubEventAMSList (
 	vidPerfIndKSASubEventAMS : Option[Long],
	vPerformanceIndicator : Long,
	vKSASubEventAMS : List[Long]
    ) {
    
    def getList = {
      vKSASubEventAMS.map(ksaSubEvent => 
        new MdlPerfIndKSASubEventAMS(vidPerfIndKSASubEventAMS, vPerformanceIndicator, ksaSubEvent))
    }
 }
 
 
 
 
 