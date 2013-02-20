
			
			function getNewOptions(url, selector) {
			  $.getJSON(url,
          		function(data) {
              	  // data is a JSON list, so we can iterate over it
              	  selector.empty();
              	  $.each(data, function(key, val) {
					selector.append($("<option/>", {
							value: key,
							text: val
					}));                  
          	      });
       		  });
			}



