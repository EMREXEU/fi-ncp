app = angular.module('hello', [ 'ngRoute' ]);

app.config(function($routeProvider, $httpProvider, $locationProvider) {

    $routeProvider.
        when('/', {
            templateUrl : 'partials/home.html',
            controller : 'home'
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
            templateUrl : 'partials/login.html',
            controller : 'norex'
        }).
        when('/elmo', {
            templateUrl : 'partials/elmo.html',
            controller : 'elmo'
        }).
        otherwise({
            redirectTo: '/'
        });

    $httpProvider.defaults.headers.common['X-Requested-With'] = 'XMLHttpRequest';

});

app.controller(
    'home',
    function($scope, $http) {

        $http.get('/resource/').success(function(data) {
            console.log("HOME");
            $scope.greeting = data;
        })

});

app.controller(
    'norex',
    function($scope, $http, $location) {
        $http.post('/norex/').success(function(data) {
            console.log("NOREX");
        })
});

app.controller(
    'login',
    function($scope, $http) {
        $http.get('/login/').success(function(data) {
            $location.path('/elmo/');
            console.log(data);
            $scope.greeting = data;
        })
    }
);

app.controller(
    'doLogin',
    function($scope, $http, $location, $window) {
        $http.post('/doLogin/').success(function(data) {
            $scope.greeting = data;
            $window.location.href= "#elmo";
        })
    }
);

app.controller(
    'elmo',
    function($scope, $http) {
        $http.post('/elmo/').success(function(data) {
            console.log(data);
            $scope.greeting = data;
        })
    }
);



