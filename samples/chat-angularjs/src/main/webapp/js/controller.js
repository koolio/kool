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

function ChatController($scope, $resource, $location) {
    $scope.status = "name";
    $scope.disabled = true;
    $scope.connectionStatus = 'Connecting...';

    var resourceUrl = $location.path() + '/rest/chat';
    var Message = $resource(resourceUrl);
    $scope.collection = Message.get();

    $scope.post = function (message) {
        if ($scope.me == null) {
            $scope.me = message.text;
            message.text = $scope.me + ' has logged in';
            $scope.status = "message";
        }
        message.author = $scope.me

        var postUsingSocket = true;
        if (postUsingSocket) {
            $scope.subscription.push(jQuery.stringifyJSON(message));
            $scope.message = {};
        } else {
            var m = new Message(message);
            m.$save();
            $scope.message = {};
        }
    };

    $scope.authorStyle = function (authorName) {
        return {color:(authorName == $scope.me) ? 'blue' : 'darkRed' };
    };

    // Atomsphere socket stuff
    var socket = $.atmosphere;
    var request = { url: resourceUrl + '/events',
        contentType:"application/json",
        logLevel:'debug',
        transport:'websocket',
        fallbackTransport:'long-polling'};


    request.onOpen = function (response) {
        $scope.connectionStatus = 'connected using ' + response.transport;
        $scope.disabled = false;
        $scope.$apply();
    };

    request.onMessage = function (response) {
        var message = response.responseBody;
        try {
            var json = jQuery.parseJSON(message);
        } catch (e) {
            console.log('This doesn\'t look like a valid JSON: ', message.data);
            return;
        }
        $scope.collection.messages.push(json);
        $scope.$apply();
    };

    request.onClose = function (response) {
        logged = false;
    };

    request.onError = function (response) {
        $scope.connectionStatus = 'Sorry, but there\'s some problem with your socket or the server is down';
        $scope.$apply();
    };

    $scope.subscription = socket.subscribe(request);
}