angular.module('courseSelection')
    .controller('courseSelectionPreviewCtrl', function ($scope, $sce, $http, courseSelectionService, apiService, helperService) {
        apiService.getSubmitHtml(courseSelectionService.selectedCourseIds).then(function (html) {
            $scope.review = html;
        }); // we could also handle errors...

        $scope.numberOfCourses = 0;

        apiService.getElmoSelected(courseSelectionService.selectedCourseIds).then(function (reports) {

            var reports = helperService.calculateAndFilter(reports);
            angular.forEach(reports, function(report){
                $scope.numberOfCourses += report.numberOfCourses;
            });
            $scope.reports = reports;
        });

        // there are learning opportunitites in report
        $scope.courseNumberFilter = function(report) {
            return (report.learningOpportunitySpecification);
        };

    })
;
