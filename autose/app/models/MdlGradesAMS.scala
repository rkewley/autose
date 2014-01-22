    
 package models
    
 case class MdlGradesAMS (
 	vidGradesAMS : Option[Long],
	vStudent : Long,
	vGradedEventAMS : Long,
	vPoints : Double,
	vStudentId : String
    ) extends Mdl[Long] {
    
      def this() = this(Option(0), 0, 0, 0.0, "")

  	  def validate: Boolean = true
    
	  def validationErrors: String = ""
    
      def selectIdentifier: (String, String) = vidGradesAMS.get.toString -> vidGradesAMS.get.toString
    
      def compare(a: MdlGradesAMS, b: MdlGradesAMS) = a.vidGradesAMS.get.compareTo(b.vidGradesAMS.get)
        
      def primaryKey = vidGradesAMS
}
    