//	Constants
var ADVANCED_PROPERTIES = new Array();

var CHOOSER_VALUE_VIEWER_ID = null;

/** Logic for Choosers/Advanced handlers starts **/
function saveSelectedValues(message, instanceId, method, needsReload, reloadMessage, actionAfter) {
	showLoadingMessage(message);
	var values = new Array();
	var advancedProperty = null;
	for (var i = 0; i < ADVANCED_PROPERTIES.length; i++) {
		advancedProperty = ADVANCED_PROPERTIES[i];
		values.push(advancedProperty.value);
	}

	ChooserService.updateHandler(values, {
		callback: function(result) {
			saveSelectedValuesCallback(result, message, instanceId, method, needsReload, reloadMessage, actionAfter);
		}
	});
}

function saveSelectedValuesCallback(result, message, instanceId, method, needsReload, reloadMessage, actionAfter) {
	closeLoadingMessage();
	if (result && instanceId != null && method != null) {
		showLoadingMessage(message);
		ChooserService.setModuleProperty(instanceId, method, ADVANCED_PROPERTIES, {
			callback: function(savedSuccessfully) {
				setModulePropertyCallback(savedSuccessfully, instanceId, needsReload, reloadMessage, actionAfter);
			}
		});
	}
	else {
		closeLoadingMessage();
		return;
	}
}

function setModulePropertyCallback(result, instanceId, needsReload, reloadMessage, actionAfter) {
	closeLoadingMessage();
	if (!result) {
		return;
	}
	ADVANCED_PROPERTIES = new Array();
	if (needsReload) {
		var activePropertyBoxId = null;
		try {
			activePropertyBoxId = getActivePropertyBoxId();
			if (activePropertyBoxId != null) {
				var box = document.getElementById(activePropertyBoxId);
				if (box != null) {
					box.className = 'modulePropertyIsSet';
				}
			}
		} catch(ex) {}
		var actionOnClose = function() {
			showLoadingMessage(reloadMessage);
			reloadPage();
		};
		addActionForMoodalBoxOnCloseEvent(actionOnClose);
		return;
	}
	if (actionAfter != null) {
		actionAfter();
	}
}

function addChooserObject(chooserObject, objectClass, hiddenInputAttribute, chooserValueViewerId, message) {
	var container = chooserObject.parentNode;
	
	CHOOSER_VALUE_VIEWER_ID = null;
	var attributes = chooserObject.attributes;
	if (attributes != null) {
		if (attributes.getNamedItem(chooserValueViewerId) != null) {
			CHOOSER_VALUE_VIEWER_ID = attributes.getNamedItem(chooserValueViewerId).value;
		}
	}
	
	var chooser = null;
	var choosers = getNeededElementsFromListById(container.childNodes, 'chooser_presentation_object');
	if (choosers != null) {
		if (choosers.length > 0) {
			chooser = choosers[0];
		}
	}
	if (chooser == null) {
		showLoadingMessage(message);
		ChooserService.getRenderedPresentationObject(objectClass, hiddenInputAttribute, false, {
			callback: function(renderedObject) {
				getRenderedPresentationObjectCallback(renderedObject, container);
			}
		});
	}
	else {
		if (chooser.style.display == null) {
			chooser.style.display = 'block';
		}
		else {
			if (chooser.style.display == 'block') {
				chooser.style.display = 'none';
			}
			else {
				chooser.style.display = 'block';
			}
		}
	}
}

function getRenderedPresentationObjectCallback(renderedObject, container) {
	closeLoadingMessage();
	insertNodesToContainer(renderedObject, container);
}

function chooseObject(element, attributeId, attributeValue) {
	if (element == null) {
		return null;
	}
	var attributes = element.attributes;
	if (attributes == null) {
		return null;
	}
	var id = attributeId;
	var value = null;
	if (attributes.getNamedItem(attributeId) != null) {
		value = attributes.getNamedItem(attributeId).value;
	}
	
	if (value == null) {
		return null;
	}
	
	addAdvancedProperty(id, value);
	
	return value;
}

function chooseObjectWithHidden(element, attributeId, attributeValue, hiddenName) {
	var value = chooseObject(element, attributeId, attributeValue);
	if (value == null) {
		return;
	}
	var forms = document.getElementsByTagName('form');
	if (forms == null) {
		return;
	}
	if (forms.length == 0) {
		return;
	}
	
	var form = forms[0];
	if (form == null) {
		return;
	}
	
	var hidden = document.getElementById(hiddenName);
	if (hidden == null) {
		hidden = document.createElement('input');
		hidden.setAttribute('type', 'hidden');
		hidden.setAttribute('id', hiddenName);
		hidden.setAttribute('name', hiddenName);
		form.appendChild(hidden);
	}
	hidden.setAttribute('value', value);
}

function setChooserView(element, attributeValue) {
	if (element == null || attributeValue == null || CHOOSER_VALUE_VIEWER_ID == null) {
		return;
	}
	var attributes = element.attributes;
	if (attributes == null) {
		return;
	}
	var value = null;
	if (attributes.getNamedItem(attributeValue) != null) {
		value = attributes.getNamedItem(attributeValue).value;
	}
	if (value == null) {
		alert('Error: no value found to set!');
		return;
	}
	
	var viewer = document.getElementById(CHOOSER_VALUE_VIEWER_ID);
	if (viewer == null) {
		return;
	}
	viewer.value = value;
}

function addAdvancedProperty(id, value) {
	if (ADVANCED_PROPERTIES == null) {
		ADVANCED_PROPERTIES = new Array();
	}
	if (existAdvancedProperty(id)) {
		removeAdvancedProperty(id);	//	Removing old value
	}
	ADVANCED_PROPERTIES.push(new AdvancedProperty(id, value));
}

function removeAdvancedProperty(id) {
	if (id == null) {
		return;
	}
	if (ADVANCED_PROPERTIES == null) {
		return;
	}
	var needless = new Array();
	for (var i = 0; i < ADVANCED_PROPERTIES.length; i++) {
		if (ADVANCED_PROPERTIES[i].id == id) {
			needless.push(ADVANCED_PROPERTIES[i]);
		}
	}
	for (var i = 0; i < needless.length; i++) {
		removeElementFromArray(ADVANCED_PROPERTIES, needless[i]);
	}
}

function removeAllAdvancedProperties() {
	ADVANCED_PROPERTIES = new Array();
}

function existAdvancedProperty(id) {
	var property = getAdvancedProperty(id);
	if (property == null) {
		return false;
	}
	return true;
}

function getAdvancedProperty(id) {
	if (id == null) {
		return null;
	}
	for (var i = 0; i < ADVANCED_PROPERTIES.length; i++) {
		if (ADVANCED_PROPERTIES[i].id == id) {
			return ADVANCED_PROPERTIES[i];
		}
	}
	return null;
}

function AdvancedProperty(id, value) {
	this.id = id;
	this.value = value;
}
/** Logic for Choosers/Advanced handlers ends **/