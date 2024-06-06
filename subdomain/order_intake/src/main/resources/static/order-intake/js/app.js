var stompClient = null;

function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    if (connected) {
        $("#conversation").show();
    }
    else {
        $("#conversation").hide();
    }
    $("#messages").html("");
}

function connect() {
    var socket = new SockJS('/endpoint');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        setConnected(true);
        console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/order-intake/ordersSoFar', function (ordersSoFar) {
            console.log('received: ' + ordersSoFar)
            showOrder(JSON.parse(ordersSoFar.body).content);
        });
        stompClient.subscribe('/topic/order-intake/failuresSoFar', function (failuresSoFar) {
            console.log('received: ' + failuresSoFar)
            showFailure(JSON.parse(failuresSoFar.body).content);
        });
    });
}

function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}

function showOrder(count) {
    $("#ordersSoFar").text(count);
}

function showFailure(count) {
    $("#failuresSoFar").text(count);
}

$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $( "#connect" ).click(function() { connect(); });
    $( "#disconnect" ).click(function() { disconnect(); });
    $( "#send" ).click(function() { sendName(); });
});