// initialise jquery stuff
$(document).ready(function() {
    // the "href" attribute of the modal trigger must specify the modal ID that wants to be triggered
    $('.modal').modal({
        dismissible: true
    });

    // initialize material selects
    $('select').material_select();


}); // end of document ready

// generic function to open a modal dialog. Specify the controller path which will supply the view, and the HTML divs to update
function openModal(sparkPath, dataDiv, containerDiv) {
    $.ajax({
        url: sparkPath,
        success: function(data) {
            $(dataDiv).html(data);
            $(containerDiv).modal('open')
        }
    })
}

function openAddSearchModal() {
    var name = $('#add-bridge-name').val()
    $('#add-bridge-name-pop').val(name);

    $.ajax({
        url: "/ajax/add-search/" + name,
        success: function(data, statusString, jqxhr) {
            if(jqxhr.getResponseHeader( 'spark-error-redirect' )) {
                window.location.href = jqxhr.getResponseHeader( 'spark-error-redirect' )
            } else {
                $("#sparql-results").html(data);
                $('#addSearchContainer').modal('open');
            }
        }
    })
}

function refineSearch() {
    var name = $('#add-bridge-name-pop').val()
    $.ajax({
        url: "/ajax/refine-search/" + name,
        success: function(data) {
            $("#sparql-results").html(data);
        }
    })
}