
			
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


			function getNewCheckboxes(url, checkboxdiv, chckBoxGroupName) {
			  $.getJSON(url,
          		function(data) {
              	  // data is a JSON list, so we can iterate over it
              	  var iterator = 0;
              	  checkboxdiv.empty();
              	  $.each(data, function(key, val) {
              	    var cbString = '<input type = "checkbox" name="' + chckBoxGroupName + '[' + iterator + ']" value="' + key + '" />' + key + ' ' + val + '<br />'
					checkboxdiv.append(cbString);              
					iterator = iterator + 1;
          	      });
       		  });
			}

