angular.module('courseSelection')
    .controller('courseSelectionPreviewCtrl',  function ($scope, $sce, $http, courseSelectionService, apiService) {
        apiService.getSubmitHtml(courseSelectionService.selectedCourseIds).then(function(html) {
            $scope.review = html;
        }); // we could also handle errors...

        $scope.numberOfCourses = 0;

        apiService.getElmoSelected(courseSelectionService.selectedCourseIds).then(function(reports){
            angular.forEach(reports, function (report) {
                $scope.learner = report.learner;
                $scope.numberOfCourses = $scope.numberOfCourses + calculateCourses(report.learningOpportunitySpecification.hasPart);
            });
            $scope.reports = reports;
        });

        var calculateCourses = function (learningOpportunityArray, count) {
            var count = 0;
            angular.forEach(learningOpportunityArray, function (opportunity) {
                count++;
                if (opportunity.learningOpportunitySpecification.hasPart)
                    count = count + calculateCourses(opportunity.hasPart)
            });
            return count;
        };


    });
