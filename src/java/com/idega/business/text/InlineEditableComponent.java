package com.idega.business.text;

import java.io.Serializable;

import com.idega.presentation.IWContext;
import com.idega.presentation.PresentationObject;

public interface InlineEditableComponent extends Serializable {

	public static final String SPRING_BEAN_IDENTIFIER = "inlineEditableComponentBean";
	
	public abstract void makeInlineEditable(IWContext iwc, PresentationObject component);
	
}
