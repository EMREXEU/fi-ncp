angular.module('learningReport', [])
    .directive('learningReportDirective', function () {
        return {
            restrict: "E",
            replace: true,
            scope: {report: '=',
                    addId: '&',
                    removeId: '&',
                    selectedLanguage: '='},
            templateUrl: 'partials/learningReport.html',
            controller: function ($scope) {

                if (!angular.isArray($scope.report.learningOpportunitySpecification))
                    $scope.report.learningOpportunitySpecification = [{learningOpportunitySpecification: $scope.report.learningOpportunitySpecification}];

                function recursiveOpportunityFlattening(learningOpportunityArray, partOf) {
                    angular.forEach(learningOpportunityArray, function (opportunityWrapper) {
                        var opportunity = opportunityWrapper.learningOpportunitySpecification;

                        // Add properties for table
                        opportunity.selected = true;

                        // Add Elmo identifier
                        if (angular.isArray(opportunity.identifier))
                            angular.forEach(opportunity.identifier, function (identifier) {
                                if (identifier.type == "elmo")
                                    opportunity.elmoIdentifier = identifier.content;
                            })
                        else
                            opportunity.elmoIdentifier = opportunity.identifier.content;

                        // Find parents Elmo identifier
                        if (partOf)
                            opportunity.partOf = partOf.elmoIdentifier
                        else
                            opportunity.partOf = '-';

                        flatArray.push(opportunity);
                        $scope.addId({id: opportunity.elmoIdentifier}); // all are selected at the beginning

                        // Recursion
                        if (opportunity.hasPart)
                            recursiveOpportunityFlattening(opportunity.hasPart, opportunity)
                    });
                    return flatArray;
                };

                var flatArray = [];
                flatArray = recursiveOpportunityFlattening($scope.report.learningOpportunitySpecification);
                $scope.flattenedLearningOpportunities = flatArray;

                // copy paste.. could be in some service
                function getRightLanguage(titles) {
                    var result = "";
                    angular.forEach(titles, function (title) {
                        if (title['xml:lang'] === $scope.selectedLanguage)
                            result = title['content'];
                    });
                    return result;
                }

                $scope.issuerName = getRightLanguage($scope.report.issuer.title);

                $scope.checkBoxChanged = function (opportunity) {
                    if (opportunity.selected)
                        $scope.addId({id: opportunity.elmoIdentifier})
                    else
                        $scope.removeId({id: opportunity.elmoIdentifier});
                }
            }
        }})
