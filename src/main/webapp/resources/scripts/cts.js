$(document).ready(function(){
    $('[data-href]').click(function(){
        window.location = $(this).data('href');
        return false;
    });
});

$(document).ready(function() {
	setTimeout(function() { $(".alert").fadeOut('slow'); }, 10000);
	$(".alert").click(function() {
		$(this).closest(".alert").slideUp();
		return false;
	})
});

$(document).ready(function() {
	$('#filter').change(function() {
		$('#templates option').each(function() {
			if($('#filter').val() == 'ALL')
				$(this).show();
			else {							
				if($(this).attr('data-type') != $('#filter').val()) {		
					$(this).hide();
					$(this).removeAttr('selected');	
				} else {
					$(this).show();
				}
			}
		})
	});
});

$(document).ready(function() {
	var update = function() {
		$('#certificationTemplates option').each(function() {
			if($(this).attr('data-type') != $('#filterCertification').val()) {		
				$(this).hide();
			} else {
				$(this).show();
			}
		})
	};
	
	$('#filterCertification').change(update);
	$('#certificationTemplates').ready(update);
});


$(document).ready(function() {
	$('[data-tab]').click( function() {
		$(this).addClass('active').siblings('[data-tab]').removeClass('active');
		
		$('[data-content=' + $(this).data('tab') + ']')
			.addClass('active')
			.siblings('[data-content]')
			.removeClass('active');
		
	});
});