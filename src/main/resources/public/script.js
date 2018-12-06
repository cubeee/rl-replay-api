var replays = document.getElementById('replays');

var replayShellTemplate = document.getElementById('replay-shell-template').innerHTML;
var renderReplayShell = Handlebars.compile(replayShellTemplate);

var replayErrorTemplate = document.getElementById('replay-error-template').innerHTML;
var renderError = Handlebars.compile(replayErrorTemplate);

var replayTemplate = document.getElementById('replay-template').innerHTML;
var renderReplay = Handlebars.compile(replayTemplate);

Handlebars.registerHelper('float', function (text) {
    return new Handlebars.SafeString(parseFloat(text).toFixed(1));
});

+function ($) {
    'use strict';

    var enablePopovers = function () {
        $('[data-toggle="popover"]').popover({
            html: true,
            placement: 'right',
            trigger: 'click hover'
        });
    };

    enablePopovers();

    var dropZone = document.getElementById('drop-zone');
    var uploadForm = document.getElementById('upload-form');

    var startUpload = function (file) {
        var formData = new FormData();
        formData.append('replay', file);

        var shellId = addReplayShell();

        $.ajax({
            url: '/upload',
            method: 'POST',
            data: formData,
            mimeType: "multipart/form-data",
            contentType: false,
            processData: false,
            cache: false
        }).done(function (replay) {
            addReplay(shellId, replay);
        }).fail(function (response) {
            var responseJson = JSON.parse(response.responseText);
            var error = response.responseText;
            if (responseJson["error"]) {
                error = responseJson["error"];
            }
            showError(shellId, error);
        });
    };

    uploadForm.addEventListener('submit', function (e) {
        var uploadFiles = document.getElementById('replay-file').files;
        e.preventDefault();

        startUpload(uploadFiles[0]);
    });

    dropZone.ondrop = function (e) {
        e.preventDefault();
        this.className = 'upload-drop-zone';

        startUpload(e.dataTransfer.files[0]);
    };

    dropZone.ondragover = function () {
        this.className = 'upload-drop-zone drop';
        return false;
    };

    dropZone.ondragleave = function () {
        this.className = 'upload-drop-zone';
        return false;
    };

    function addReplayShell() {
        var id = Math.floor(Math.random() * 10000000);

        var newReplay = document.createElement('div');
        newReplay.className = 'col-6';
        newReplay.id = 'replay-' + id;
        newReplay.innerHTML = renderReplayShell();

        replays.insertBefore(newReplay, replays.firstChild);

        return id;
    }

    function showError(id, error) {
        var shell = document.getElementById('replay-' + id);
        shell.innerHTML = renderError();
    }

    function addReplay(shellId, replayJson) {
        var replay = JSON.parse(replayJson);
        console.log(replay);

        replay["teams"][0]["players"] = sortPlayers(replay["teams"][0]["players"]);
        replay["teams"][1]["players"] = sortPlayers(replay["teams"][1]["players"]);

        var shell = document.getElementById("replay-" + shellId);
        shell.innerHTML = renderReplay(replay);

        enablePopovers();
    }

    function sortPlayers(players) {
        return Object.keys(players).sort(function (a, b) {
            return players[b]["score"] - players[a]["score"];
        }).map(function (value) {
            return players[value];
        });
    }
}(jQuery);