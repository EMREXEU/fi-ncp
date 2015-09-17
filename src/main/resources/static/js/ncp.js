app = angular.module('fi-ncp', ['ngRoute'])
    .directive('learningOpportunityCollection', function () {
        return {
            restrict: "E",
            replace: true,
            scope: {
                collection: '='
            },
            controller: function($scope) {
                console.log('COLLECTION ' + JSON.stringify($scope.collection));
            },
            templateUrl: "partials/learning-opportunity-collection.html"
        }
    })
    .directive('learningOpportunity', function ($compile) {
        return {
            restrict: "E",
            replace: true,
            scope: {
                member: '='
            },
            controller: function($scope) {
                console.log('MEMBER ' + JSON.stringify($scope.member));
            },
            templateUrl: "partials/learning-opportunity.html",
            link: function (scope, element, attrs) {
                if (angular.isArray(scope.member.hasPart)) {
                    element.append("<learning-opportunity-collection collection='member.hasPart'></learning-opportunity-collection>");
                    $compile(element.contents())(scope)
                }
            }
        }
    })

app.config(function ($routeProvider, $httpProvider) {

    $routeProvider.
        when('/', {
            templateUrl: 'partials/courseSelection.html',
            controller: 'courseSelection',
            resolve: {
                response: function ($http) {
                    return $http.get('/api/elmo/').success(function (response) {
                        return response;
                    });
                }
            }
        }).
        when('/login', {
            templateUrl: 'partials/login.html',
            controller: 'login'
        }).
        when('/doLogin', {
            //templateUrl: 'partials/login.html',
            controller: 'doLogin'
        }).
        when('/norex', {
            templateUrl: 'partials/login.html',
            controller: 'norex'
        }).
        when('/elmo', {
            templateUrl: 'partials/elmo.html',
            controller: 'elmo'
        }).
        otherwise({
            redirectTo: '/'
        });

    $httpProvider.defaults.headers.common['X-Requested-With'] = 'XMLHttpRequest';

})
;

app.controller('courseSelection', function($scope, response) {
    var report = response.data.elmo.report;
    if (!angular.isArray(report.learningOpportunitySpecification))
        report.learningOpportunitySpecification = [{learningOpportunitySpecification : report.learningOpportunitySpecification}];
    $scope.learningOpportunities = report.learningOpportunitySpecification;
});

app.controller('norex', function ($scope, $http) {
    $http.post('/norex/').success(function (data) {
    });
});

app.controller('login', function ($scope, $http) {
    $http.get('/login/').success(function (data) {
        console.log(data);
        $scope.greeting = data;
    });
});

app.controller('doLogin', function ($scope, $http, $location) {
    $http.post('/doLogin/').success(function (data) {
        $scope.greeting = data;
        $location.path("/courseSelection");
    })
});

app.controller('elmo', function ($scope, $http) {
    $http.post('/elmo/').success(function (data) {
        console.log(data);
        $scope.greeting = data;
    })
});




