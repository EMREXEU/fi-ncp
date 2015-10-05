angular.module('courseSelection')
    .controller('courseSelectionPreviewCtrl', function ($scope, $sce, $http, courseSelectionService, apiService) {
        apiService.getSubmitHtml(courseSelectionService.selectedCourseIds).then(function (html) {
            $scope.review = html;
        }); // we could also handle errors...

        $scope.numberOfCourses = 0;

        apiService.getElmoSelected(courseSelectionService.selectedCourseIds).then(function (reports) {
            angular.forEach(reports, function (report) {

                if (report.learningOpportunitySpecification) {
                    $scope.learner = report.learner;
                    report.numberOfCourses = calculateCourses(report.learningOpportunitySpecification);
                    $scope.numberOfCourses = $scope.numberOfCourses + report.numberOfCourses;
                }
            });

            //we want only reports with courses
            $scope.reports = reports.filter(function(report) {
                var goodReport = true;
                angular.forEach(report.learningOpportunitySpecification, function (object) {
                    if (!object.learningOpportunitySpecification)
                        goodReport = false;
                });
                return goodReport;
            });
        });

        $scope.courseNumberFilter = function(report) {
            return (report.learningOpportunitySpecification);
        };

        var calculateCourses = function (learningOpportunityArray, count) {
            var count = 0;
            angular.forEach(learningOpportunityArray, function (opportunity) {
                if (opportunity.learningOpportunitySpecification) {
                    count++;
                    if (opportunity.learningOpportunitySpecification.hasPart)
                        count = count + calculateCourses(opportunity.learningOpportunitySpecification.hasPart)
                }
            });
            return count;
        };


    })
;
