angular.module('courseSelection')
    .controller('courseSelectionCtrl',  function ($scope, $http, $sce, $location, response, courseSelectionService) {

        var reports = response.data.elmo.report;

        // Report must be an array...
        if (!angular.isArray(reports))
            reports = [reports];

        $scope.reports = reports;

        $scope.educationInstitutionOptions = {}; // {'Helsinki University' : true, 'Oulu AMK' : true};
        $scope.typeOptions = {};
        $scope.levelOptions = ["Any"];

        $scope.issuerFilter = function(report) {
            var title = courseSelectionService.getRightLanguage(report.issuer.title);
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


        // Collect data from reports
        angular.forEach(reports, function (report) {
            $scope.learner = report.learner;

            var issuerTitle = courseSelectionService.getRightLanguage(report.issuer.title);
            $scope.educationInstitutionOptions[issuerTitle] = true;

            var hasPart = [];

            // let's modify array to make it compatible with recursion
            if (!angular.isArray(report.learningOpportunitySpecification))
                hasPart.push({learningOpportunitySpecification: report.learningOpportunitySpecification})
            else
                angular.forEach(report.learningOpportunitySpecification, function (specification) {
                    hasPart.push({learningOpportunitySpecification: specification});
                });
            report.learningOpportunitySpecification = hasPart;
            findOptionsRecursively(hasPart);
        });


        $scope.sendIds = function () {
            $location.path('preview');
        };
    });