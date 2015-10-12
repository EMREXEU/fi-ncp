angular.module('courseSelection')
    .controller('courseSelectionPreviewCtrl', function ($scope, $sce, $http, courseSelectionService, apiService, helperService) {
        apiService.getSubmitHtml(courseSelectionService.selectedCourseIds).then(function (html) {
            $scope.review = html;
        }); // we could also handle errors...

        $scope.numberOfCourses = 0;

        apiService.getElmoSelected(courseSelectionService.selectedCourseIds).then(function (reports) {
            angular.forEach(reports, function (report) {

                if (report.learningOpportunitySpecification) {
                    $scope.learner = report.learner;
                    report.numberOfCourses = helperService.calculateCourses(report.learningOpportunitySpecification);
                    $scope.numberOfCourses = $scope.numberOfCourses + report.numberOfCourses;
                }
            });

            //we want only reports with courses
            $scope.reports = helperService.filterProperReports(reports);
        });

        // there are learning opportunitites in report
        $scope.courseNumberFilter = function(report) {
            return (report.learningOpportunitySpecification);
        };

    })
;
