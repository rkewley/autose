    
 package models
    
 case class MdlFaculty (
 	vLastName : String,
	vFirstName : String,
	vTitle : String,
	vOfficeNumber : String,
	vOfficePhone : String,
	vBranchofService : String,
	vEmail : String,
	vFacultyPhotoFile : String,
	vBiography : String
    )  {
    
      def this() = this("", "", "", "", "", "", "", "", "")

  	  def validate: Boolean = true
    
	  def validationErrors: String = ""
    
      def selectIdentifier: (String, String) = vEmail.toString -> (vTitle + " " + vFirstName + " " + vLastName)
}
    