// initialise jquery stuff
$(document).ready(function() {
    // the "href" attribute of the modal trigger must specify the modal ID that wants to be triggered
    $('.modal').modal({
        dismissible: true
    });

    // initialize material selects
    $('select').material_select();
    Materialize.updateTextFields();
    triggerSearch();



}); // end of document ready

function triggerSearch() {
  var serializedData = $('#bridge-refine-search-form').serialize();
  console.log(serializedData);
  $.ajax({
    url: 'ajax/triggerSearch',
    method: 'post',
    data: serializedData,
    success: function(response, statusText, xhr) {
        $('#results').html(response);
    }
  });
}

function showBridgePreview(wikiDataID) {
    var preview = { "wikiDataID": wikiDataID};
    $.ajax({
        url: 'ajax/getPreview',
        method: 'get',
        data: preview,
        success: function(response, statusText, xhr) {
            $('#preview').html(response);
            $('#preview').show();
        }
    });

}