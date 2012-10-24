angular.module('Koolio', ['ngResource']);

function ProductController($scope, $resource, $location) {
    var resourceURI = $location.path() + '/api/products';
    var Model = $resource(resourceURI);
    $scope.results = Model.get();

    $scope.elementResource = function (formData) {
        return $resource(resourceURI + '/id/' + formData.id);
    };

    $scope.reset = function() {
        $scope.formData = {};
    };

    $scope.startEdit = function (row) {
        $scope.formData = row;
    }

    $scope.delete = function (formData) {
        $scope.elementResource(formData).delete(function() {
            // lets force a reload
            $scope.results = Model.get();
        });
    };

    $scope.save = function (formData) {
        new Model(formData).$save(function() {
            $scope.reset();
            // lets force a reload
            $scope.results = Model.get();
        });
    };
}
