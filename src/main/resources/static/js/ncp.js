app = angular.module('fi-ncp', ['ngRoute'])
    .directive('learningOpportunity', function () {
        return {
            scope: {opportunity: '='},
            restrict: 'E',
            templateUrl: 'partials/learning-opportunity.html'
        };
    });
;

app.config(function ($routeProvider, $httpProvider) {

    $routeProvider.
        when('/', {
            templateUrl: 'partials/home.html',
            controller: 'home'
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
        when('/courseselection', {
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

app.controller('home', function ($scope, $http) {
    $http.get('/resource/').success(function (data) {
        console.log("HOME");
        $scope.greeting = data;
    });
});

app.controller('home', function($scope, $http) {

        $http.get('/resource/').success(function(data) {
            console.log("HOME");
            $scope.greeting = data;
        })
});

app.controller('courseSelection', function($scope, response) {
    $scope.report = response.data.elmo.report;
    console.log($scope.report);
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




