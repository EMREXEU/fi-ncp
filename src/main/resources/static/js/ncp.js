app = angular.module('fi-ncp', ['ngRoute', 'api', 'courseSelection','learningReport', '720kb.datepicker', 'helper'])

app.config(function ($routeProvider, $httpProvider) {

    $routeProvider.
        when('/', {
            templateUrl: 'partials/courseSelection.html',
            controller: 'courseSelectionCtrl'

        }).
        when('/preview', {
            templateUrl: 'partials/courseSelectionPreview.html',
            controller: 'courseSelectionPreviewCtrl'
        }).
        when('/login', {
            templateUrl: 'partials/login.html',
            controller: 'login'
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

});

app.controller('norex', function ($scope, $http) {
    $http.post('/norex/').success(function (data) {
    });
});

app.controller('login', function ($scope, $http) {
    $http.get('/login/').success(function (data) {
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
        $scope.greeting = data;
    })
});




