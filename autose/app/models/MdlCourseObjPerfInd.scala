    
 package models
    
 case class MdlCourseObjPerfInd (
 	vidCourseObjPerfInd : Option[Long],
	vCourseObjective : Long,
	vPerformanceIndicator : Long
    ) extends Mdl[Long] {
    
      def this() = this(Option(0), 0, 0)

  	  def validate: Boolean = true
    
	  def validationErrors: String = ""
    
      def selectIdentifier: (String, String) = vidCourseObjPerfInd.get.toString -> vidCourseObjPerfInd.get.toString
    
      def compare(a: MdlCourseObjPerfInd, b: MdlCourseObjPerfInd) = a.vidCourseObjPerfInd.get.compareTo(b.vidCourseObjPerfInd.get)
        
      def primaryKey = vidCourseObjPerfInd
}
    