    
 package models
    
 case class MdlFaculty (
 	vidFaculty : Long,
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
    
      def this() = this(0, "", "", "", "", "", "", "", "", "")
      
      def updatePhotoFile(newPhotoFilePath: String) = new MdlFaculty(vidFaculty, vLastName, vFirstName, vTitle, vOfficeNumber, vOfficePhone,
          vBranchofService, vEmail, newPhotoFilePath, vBiography)

  	  def validate: Boolean = true
    
	  def validationErrors: String = ""
    
      def selectIdentifier: (String, String) = vidFaculty.toString -> (vLastName + ", " + vFirstName + " " + vTitle)
    
      def compare(a: MdlFaculty, b: MdlFaculty) = a.vLastName.compareTo(b.vLastName)
}
    