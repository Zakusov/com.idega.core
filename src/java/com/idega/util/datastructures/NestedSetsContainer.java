/*
 * $Id: NestedSetsContainer.java,v 1.3 2006/04/09 12:13:12 laddi Exp $
 * Created on 5.9.2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.util.datastructures;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.Set;

/**
 * For a small introduction on nested sets see: 
 * @see <a href="http://www.intelligententerprise.com/001020/celko1_1.jhtml?_requestid=224927">Nested Sets</a>
 * 
 *  Last modified: $Date: 2006/04/09 12:13:12 $ by $Author: laddi $
 * 
 * @author <a href="mailto:gummi@idega.com">Gudmundur Agust Saemundsson</a>
 * @version $Revision: 1.3 $
 */
public class NestedSetsContainer {
	
	Vector container = new Vector();
	/**
	 * 
	 */
	public NestedSetsContainer() {
		super();
	}

	/**
	 * @param c
	 */
	public NestedSetsContainer(Collection c) {
		this();
		this.container=new Vector(c);
	}
	

	public Set subSet(Object topElement){
		Set set = new HashSet();
		NestedSetNode topNode = new NestedSetNode(topElement,null,-1,-1);
		int index = this.container.indexOf(topNode);
		while(index!=-1){
			List subtree = this.container;//container.subList(index,container.size());  //if the container is sorted right this sublist should contain the whole subtree
			NestedSetNode node = (NestedSetNode)this.container.get(index);
			if(node.hasChildren()){ // has children
				int l = node.getLeft();
				int r = node.getRight();
				Iterator iter = subtree.iterator();
				while (iter.hasNext()) {
					NestedSetNode element = (NestedSetNode) iter.next();
					if(l < element.getLeft() && r>element.getRight()){
						set.add(element.getObject());
					} else {
						continue;  //break;  //if the container is sorted right it should be ok to have break here
					}
				}
			}
			index = this.container.indexOf(topNode,index+1);
		}
		
		return set;
	}
	
	public boolean contains(Object obj){
		return this.container.contains(new NestedSetNode(obj,null,-1,-1));
	}
	
	public void add(NestedSetsContainer siblingTree){
		if(this.container.isEmpty()){
			this.container = new Vector(siblingTree.container);
		} else {
			int maxRight = ((NestedSetNode)this.container.get(0)).getRight();
			Iterator iter = siblingTree.container.iterator();
			while(iter.hasNext()){
				NestedSetNode node = (NestedSetNode)iter.next();
				node.setLeft(node.getLeft()+maxRight);
				node.setRight(node.getRight()+maxRight);
				this.container.add(node);
			}
		}
	}
	
	public String toString(Object obj){
		StringBuffer s = new StringBuffer();
		for (Iterator iter = this.container.iterator(); iter.hasNext();) {
			NestedSetNode node = (NestedSetNode) iter.next();
			if(obj == null || (node.hashCode()==obj.hashCode() && node.equals(obj))){
				s.append(node);
				s.append("\n");
			}
		}
		return s.toString();
	}
	
	public String toString(){
		return toString(null);
	}
}
