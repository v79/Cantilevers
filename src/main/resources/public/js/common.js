// initialise jquery stuff
$(document).ready(function() {
    // the "href" attribute of the modal trigger must specify the modal ID that wants to be triggered
    $('.modal').modal({
        dismissible: true
    });

    // initialize material components
    $('select').material_select();
    $('.collapsible').collapsible();
    Materialize.updateTextFields();

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

// Call the validation route given in validatorPath to validate the form in formName.
// If validation fails, update containerDiv through AJAX.
// If it succeeds, redirect to the given route
function validate(validatorPath, formName, containerDiv, redirectPath) {
    var serializedData = $('#' + formName).serialize();
    console.log(serializedData)
    // first, post to validator
    $.ajax({
        url: validatorPath,
        method: 'post',
        data: serializedData,
        success: function(response, statusText, xhr) {
            // if valid, move on to next action
            if(!response) {
                window.location.href = redirectPath
            } else {
                // if in error, form should be re-rendered
                $('#' + containerDiv).html(response);
                Materialize.updateTextFields();
            }
        }
    });
}

// Call validation by providing an object to use named parameters
function validateAndRedirect(params) {
    validate(params.validator, params.form, params.div, params.success);
}

function openAddSearchModal() {
    var name = $('#add-bridge-name').val()
    $('#add-bridge-name-pop').val(name);

    $.ajax({
        url: "/ajax/add-search/" + name,
        success: function(response, statusString, jqxhr) {
            if(jqxhr.getResponseHeader( 'spark-error-redirect' )) {
                window.location.href = jqxhr.getResponseHeader( 'spark-error-redirect' )
            } else {
                $("#sparql-results").html(response);
                $('#addSearchContainer').modal('open');
            }
        }
    })
}

function refineSearch() {
    var name = $('#add-bridge-name-pop').val()
    $.ajax({
        url: "/ajax/refine-search/" + name,
        success: function(response, statusString, jqxhr) {
            $("#sparql-results").html(data);
        }
    })
}