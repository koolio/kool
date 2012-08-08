angular.module('Koolio', ['ngResource']);

function ProductController($scope, $resource) {
    var Model = $resource('rest/products');
    $scope.results = Model.get();

    $scope.save = function (formData) {
        new Model(formData).$save();
        $scope.formData = {};
        // lets force a reload
        $scope.results = Model.get();
    };
}

function ChatController($scope, $resource) {
    $scope.status = "name";
    $scope.disabled = true;
    $scope.connectionStatus = 'Connecting...';

    var Message = $resource('rest/chat');

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
    var request = { url:document.location.toString() + 'rest/chat/events',
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
        console.log("Got message " + json);
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