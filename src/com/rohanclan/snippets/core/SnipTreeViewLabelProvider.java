/*
 * Created on Feb 27, 2004
 *
 * The MIT License
 * Copyright (c) 2004 Rob Rohan
 *
 * Permission is hereby granted, free of charge, to any person obtaining a 
 * copy of this software and associated documentation files (the "Software"), 
 * to deal in the Software without restriction, including without limitation 
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, 
 * and/or sell copies of the Software, and to permit persons to whom the Software 
 * is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in 
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR 
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, 
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE 
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER 
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, 
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE 
 * SOFTWARE.
 */
package com.rohanclan.snippets.core;

/**
 * @author Rob
 *
 * Provide icons and labels for the snip viewer tree
 * 
 * This class was influenced by the aricle:
 * How to use the JFace Tree Viewer
 * By Chris Grindstaff, Applied Reasoning (chrisg at appliedReasoning.com)
 * May 5, 2002
 */
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import com.rohanclan.snippets.PluginImages;
//import com.rohanclan.cfml.util.CFPluginImages;
import java.io.File;

public class SnipTreeViewLabelProvider extends LabelProvider 
{	
    private SnipReader snipReader = new SnipReader();
    
	/**
	 * Get the image for element
	 * @see ILabelProvider#getImage(Object)
	 */
	public Image getImage(Object element) 
	{
		if(element instanceof File)
		{
			if( !((File)element).isDirectory() )
			{	
				String fname = ((File)element).getName(); 
				//an xml file assume its a snip
				if(fname.endsWith(".xml") || fname.endsWith(".XML"))
				{
				    // Check to see if it's a file template
				    snipReader.read(((File)element).getAbsolutePath());
					return PluginImages.get(PluginImages.ICON_SNIPPET);
				}
			}
			else
			{
				return PluginImages.get(PluginImages.ICON_FOLDER);
				//its a directory
			}
		}
		return null;
	}

	/**
	 * Get the text display for element
	 * @see ILabelProvider#getText(Object)
	 */
	public String getText(Object element) 
	{	
		//if this is a file
		if(element instanceof File)
		{
			String fname = ((File)element).getName();
			//and its not a directory trim off the extension
			if( !((File)element).isDirectory() )
			{
				return fname.substring(0,fname.length() - 4);
			}
			//if a directory just show the name
			else
			{	
				return fname;
			}
		}
		return "";
	}
	
	/**
	 * 
	 * @param element
	 * @return
	 */
	protected RuntimeException unknownElement(Object element) 
	{
		return new RuntimeException(
			"Unknown type of element in tree of type " + element.getClass().getName()
		);
	}
	
	/**
	 * 
	 */
	public void dispose(){;}
}

