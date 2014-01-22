    
 package models
    
 case class MdlGradesSubEventAMS (
 	vidGradesSubEventAMS : Option[Long],
	vStudent : Long,
	vSubEventAMS : Long,
	vPoints : Double,
	vStudentID : String
    ) extends Mdl[Long] {
    
      def this() = this(Option(0), 0, 0, 0.0, "")

  	  def validate: Boolean = true
    
	  def validationErrors: String = ""
    
      def selectIdentifier: (String, String) = vidGradesSubEventAMS.get.toString -> vidGradesSubEventAMS.get.toString
    
      def compare(a: MdlGradesSubEventAMS, b: MdlGradesSubEventAMS) = a.vidGradesSubEventAMS.get.compareTo(b.vidGradesSubEventAMS.get)
        
      def primaryKey = vidGradesSubEventAMS
}
    