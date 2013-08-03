package slick

import scala.slick.driver.ExtendedProfile
 
class DAL(override val profile: ExtendedProfile) extends ProgramsComponent 
	with ProgramOutcomesComponent 
	with GradedRequirementsComponent
	with EventTypeComponent
	with SubGradedEventComponent
	with KSAGradedEventComponent
	with KSASubGradedEventComponent
	with KSAPerfIndicatorComponent
	with PerformanceIndicatorComponent
	with GradedRequirementLinksComponent
	with ProgramEducationalObjectivesComponent
	with PEOtoSLOComponent 
	with KSAComponent
	with CoursesSlickComponent
	with LessonsComponent
	with LessonKSAComponent
	with DefinitionsComponent
	with Profile {
 
  import profile.simple._
 
}