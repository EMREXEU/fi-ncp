export default {
  // Production: Angular in port 9001, NCP app in port 9001
  getAllCoursesUrl: "../api/courses/",
  getSelectedCoursesUrl: "../api/review/"

  // Test hack: bypass shibboleth auth using test-api: Angular in port 9001, NCP app in port 9001
  // getAllCoursesUrl: "../test/api/courses/?sessionId=1234&returnUrl=234123&unique-id=urn:mace:terena.org:schac:personalUniqueID:fi:FIC:180766-2213&SHIB_funetEduPersonLearnerId=1.2.246.562.24.17488477125&SHIB_schacHomeOrganization=oamk.fi&SHIB_schacHomeOrganizationId=02536",
  // getSelectedCoursesUrl: "../test/api/review/?sessionId=1234&returnUrl=234123&unique-id=urn:mace:terena.org:schac:personalUniqueID:fi:FIC:180766-2213&SHIB_funetEduPersonLearnerId=1.2.246.562.24.17488477125&SHIB_schacHomeOrganization=oamk.fi&SHIB_schacHomeOrganizationId=02536"

  // Angular standalone in port 4200 requires API from NCP app in port 9001
  // getAllCoursesUrl: "http://localhost:9001/test/api/courses/",
  // getSelectedCoursesUrl: "http://localhost:9001/test/api/review/"
}
