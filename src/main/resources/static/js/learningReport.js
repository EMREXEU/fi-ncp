angular.module('learningReport', [])
    .directive('learningReportDirective', function () {
        return {
            //templateUrl: 'partials/learningReport.html',
            template: '<div>hellasdfasdfo</div>'

        }});

/*

// learningOpportunity must be an array for working recursion..
if (!angular.isArray(report.learningOpportunitySpecification))
    report.learningOpportunitySpecification = [{learningOpportunitySpecification: report.learningOpportunitySpecification}];

flatArray = recursiveOpportunityFlattening(report.learningOpportunitySpecification);

$scope.flattenedLearningOpportunities = flatArray;


var flatArray = [];
var selectedCourseIds = [];
$scope.educationInstitutionOptions = {'Helsinki University' : true, 'Oulu AMK' : true};
$scope.typeOptions = {};
$scope.levelOptions = [];

var recursiveOpportunityFlattening = function (learningOpportunityArray, partOf) {
    angular.forEach(learningOpportunityArray, function (opportunityWrapper) {
        var opportunity = opportunityWrapper.learningOpportunitySpecification;

        // Add properties for table
        opportunity.selected = true;

        if (angular.isArray(opportunity.identifier))
            angular.forEach(opportunity.identifier, function (identifier) {
                if (identifier.type == "elmo")
                    opportunity.elmoIdentifier = identifier.content;
            })
        else
            opportunity.elmoIdentifier = opportunity.identifier.content;

        if (partOf)
            opportunity.partOf = partOf.elmoIdentifier
        else
            opportunity.partOf = '-';

        // Update filter options
        if (opportunity.type)
            $scope.typeOptions[opportunity.type] = true;
        if (opportunity.level && $scope.levelOptions.indexOf(opportunity.level) < 1)
            $scope.levelOptions.push(opportunity.level);

        flatArray.push(opportunity);

        selectedCourseIds.push(opportunity.elmoIdentifier); // all items are first selected

        // Recursion. Nomnom
        if (opportunity.hasPart)
            recursiveOpportunityFlattening(opportunity.hasPart, opportunity)
    });
    return flatArray;
};


$scope.learner = report.learner;

angular.forEach(report.issuer.title, function(title) {
    if (title['xml:lang'] === 'fi')
        $scope.issuerName = title['content'];
});



flatArray = recursiveOpportunityFlattening(report.learningOpportunitySpecification);

$scope.flattenedLearningOpportunities = flatArray;

$scope.checkBoxChanged = function (opportunity) {
    if (opportunity.selected)
        $scope.addId(opportunity.elmoIdentifier)
    else
        $scope.removeId(opportunity.elmoIdentifier);
};
    */