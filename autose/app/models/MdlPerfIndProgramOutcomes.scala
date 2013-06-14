    
 package models
 
 import play.api.data.Forms._
 import play.api.data._
 import play.api.data.format.Formats._

 case class MdlPerfIndProgramOutcomes (
 	vidPerfIndProgramOutcomes : Option[Long],
	vPerformanceIndicator : Long,
	vProgramOutcome : Long
    ) extends Mdl[Long] {
    
      def this() = this(Option(0), 0, 0)

  	  def validate: Boolean = true
    
	  def validationErrors: String = ""
    
      def selectIdentifier: (String, String) = vidPerfIndProgramOutcomes.get.toString -> vidPerfIndProgramOutcomes.get.toString
    
      def compare(a: MdlPerfIndProgramOutcomes, b: MdlPerfIndProgramOutcomes) = a.vidPerfIndProgramOutcomes.get.compareTo(b.vidPerfIndProgramOutcomes.get)
        
      def primaryKey = vidPerfIndProgramOutcomes
}
 
object MdlPerfIndProgramOutcomesList {
  val formList = Form[MdlPerfIndProgramOutcomesList](
    mapping (
	"fidPerfIndProgramOutcomes" -> optional(of[Long]),
	"fPerformanceIndicator" -> list(of[Long]),
	"fProgramOutcome" -> of[Long]
    )(MdlPerfIndProgramOutcomesList.apply)(MdlPerfIndProgramOutcomesList.unapply)
  )
  }
 
 case class MdlPerfIndProgramOutcomesList (
 	vidPerfIndProgramOutcomes : Option[Long],
	vPerformanceIndicator : List[Long],
	vProgramOutcome : Long
    ) {
    
    def getList = {
      vPerformanceIndicator.map(pi => 
        new MdlPerfIndProgramOutcomes(vidPerfIndProgramOutcomes, pi, vProgramOutcome))
    }
 }
 
 
