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
  var bridgeName = $('#refine-search-name').val();
  var serializedData = $('#bridge-refine-search-form').serialize();
  console.log(serializedData);
  $.ajax({
    url: 'ajax/triggerSearch',
    method: 'post',
    data: serializedData,
    success: function(response, statusText, xhr) {
        $('#results').html(response);
        $('#bridge-name-heading').html(bridgeName);
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

function expand(wikiDataID, rowNum) {
    var activeHeader = $('.collapsible-header');
    if(activeHeader.hasClass('active')) {
        $('#expandArrow_' + rowNum).html('expand_more');
        $('.collapsible').collapsible('close',rowNum);
    } else {
        var preview = { "wikiDataID": wikiDataID};
           $.ajax({
               url: 'ajax/getPreview',
               method: 'get',
               data: preview,
               success: function(response, statusText, xhr) {
                    $('#col_' + wikiDataID).html(response);
                    $('.collapsible').collapsible('open',rowNum);
                    $('.bridgeCollapsibleArrow').html('expand_more');
                    $('#expandArrow_' + rowNum).html('expand_less');
               }
           });
   }
}