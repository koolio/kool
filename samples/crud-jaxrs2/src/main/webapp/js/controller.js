angular.module('Koolio', ['ngResource']);

function ProductController($scope, $resource, $location) {
    var resourceURI = $location.path() + '/rest/products';
    var Model = $resource(resourceURI);
    $scope.results = Model.get();

    $scope.elementResource = function (formData) {
        return $resource(resourceURI + '/id/' + formData.id);
    };

    $scope.reset = function() {
        $scope.formData = {};
    };

    $scope.edit = function (row) {
        $scope.formData = row;
    };

    $scope.delete = function (formData) {
        console.log('deleting ' + formData);
        $scope.elementResource(formData).delete();
        // lets force a reload
        $scope.results = Model.get();
    };

    $scope.save = function (formData) {
        new Model(formData).$save();
        $scope.reset();
        // lets force a reload
        $scope.results = Model.get();
    };
}
