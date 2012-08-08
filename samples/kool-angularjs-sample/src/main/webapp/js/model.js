function PersonCtrl($scope) {
    $scope.people = [
        {name: "James", city: "Mells"},
        {name: "Hiram", city: "Tampa"}
    ];

    $scope.addPerson = function(person) {
        $scope.people.push(person);
        $scope.person = {};
    };
}
