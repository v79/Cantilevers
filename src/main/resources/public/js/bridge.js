// initialise jquery stuff
$(document).ready(function () {
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
		success: function (response, statusText, xhr) {
			$('#results').html(response);
			$('#bridge-name-heading').html(bridgeName);
		}
	});
}

//function showBridgePreview(wikiDataID) {
//    $('#spinner').hide();
//    var preview = { "wikiDataID": wikiDataID};
//    $.ajax({
//        url: 'ajax/getPreview',
//        method: 'get',
//        data: preview,
//        success: function(response, statusText, xhr) {
//            $('#preview').html(response);
//            $('#preview').show();
//        }
//    });
//}


function expand(wikiDataID, rowNum) {
	var activeHeader = $('.collapsible-header');
	var rowClicked = activeHeader[rowNum];
	var rowClickedIsActive = rowClicked.classList.contains('active');

	if (rowClickedIsActive) {
		$('#expandArrow_' + rowNum).html('expand_more');
		$('.collapsible').collapsible('close', rowNum);
	} else {

		$('#spinner').show();
		var len = activeHeader.length;
		for (i = 0; i < len; i++) {
			$('.collapsible').collapsible('close', i);
		}
		var preview = {"wikiDataID": wikiDataID};
		$.ajax({
			url: 'ajax/getPreview',
			method: 'get',
			data: preview,
			success: function (response, statusText, xhr) {
				$('#spinner').hide();
				$('#col_' + wikiDataID).html(response);
				$('.collapsible').collapsible('open', rowNum);
				$('.bridgeCollapsibleArrow').html('expand_more');
				$('#expandArrow_' + rowNum).html('expand_less');
			}
		});
	}
}