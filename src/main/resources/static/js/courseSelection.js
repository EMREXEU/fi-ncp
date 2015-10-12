angular.module('courseSelection')
    .controller('courseSelectionCtrl',  function ($scope, $http, $sce, $location, apiService, courseSelectionService, helperService) {

        apiService.getElmoAll().then(function (reports) {
            // Collect data from reports
            angular.forEach(reports, function (report) {
                $scope.learner = report.learner;

                var issuerTitle = helperService.getRightLanguage(report.issuer.title);
                $scope.educationInstitutionOptions[issuerTitle] = true;

                findOptionsRecursively(report.learningOpportunitySpecification);
            });

            $scope.reports = reports;
        });

        $scope.educationInstitutionOptions = {}; // {'Helsinki University' : true, 'Oulu AMK' : true};
        $scope.typeOptions = {};
        $scope.levelOptions = ["Any"];

        $scope.issuerFilter = function(report) {
            var title = helperService.getRightLanguage(report.issuer.title);
            return $scope.educationInstitutionOptions[title];
        };

        var findOptionsRecursively = function (learningOpportunityArray, partOf) {
            angular.forEach(learningOpportunityArray, function (opportunityWrapper) {
                var opportunity = opportunityWrapper.learningOpportunitySpecification;

                if (opportunity.type)
                    $scope.typeOptions[opportunity.type] = true;

                if (opportunity.level) {
                    var indexOf = $scope.levelOptions.indexOf(opportunity.level)
                    if (indexOf < 0)
                        $scope.levelOptions.push(opportunity.level);
                }

                if (opportunity.hasPart)
                    findOptionsRecursively(opportunity.hasPart, opportunity)
            });
            return;
        };


        $scope.sendIds = function () {
            $location.path('preview');
        };
    });
