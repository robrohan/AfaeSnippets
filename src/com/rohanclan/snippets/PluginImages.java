/*
 * Created on Jan 31, 2004
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
package com.rohanclan.snippets;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author Rob
 */
public class PluginImages 
{	
	/** the path to the icons. i.e. file://C/blah/plugin/icons/ */
	protected static URL iconBaseURL;
	
	protected static final ImageRegistry IMAGE_REGISTRY = new ImageRegistry();
	
	/** directory offset for objects */
	public static final String DIR_MODEL_OBJECTS 		= "obj16";
	/** directory offset for toolbar icons */
	public static final String DIR_TOOLBAR 		 	= "ctool16";
	
	//toobar
	public static final String ICON_CREATE				= "create.gif";
	public static final String ICON_DELETE				= "delete.gif";
	public static final String ICON_FOLDER_DELETE		= "folder_delete.gif";
	public static final String ICON_INSERT				= "insert.gif";
	public static final String ICON_REFRESH			= "refresh.gif";
	
	//object
	public static final String ICON_FOLDER				= "folder.gif";
	public static final String ICON_SNIPPET			= "snippet.gif";
	
	private PluginImages(){;}
	
	public static void init()
	{
		if(iconBaseURL == null)
		{
			String pathSuffix = "icons/";
			try 
			{
				iconBaseURL = new URL(
					SnippetsPlugin.getDefault().getBundle().getEntry("/"),
					pathSuffix
				);
			} 
			catch (MalformedURLException e) 
			{
				e.printStackTrace(System.err);
			}
			
			//load up images on our first go round
			addImageToRegistry(DIR_TOOLBAR, ICON_CREATE);
			addImageToRegistry(DIR_TOOLBAR, ICON_DELETE);
			addImageToRegistry(DIR_TOOLBAR, ICON_FOLDER_DELETE);
			addImageToRegistry(DIR_TOOLBAR, ICON_INSERT);
			addImageToRegistry(DIR_TOOLBAR, ICON_REFRESH);
					
			addImageToRegistry(DIR_MODEL_OBJECTS,ICON_FOLDER);
			addImageToRegistry(DIR_MODEL_OBJECTS,ICON_SNIPPET);
		}
	}
	
	/**
	 * gets an image for the registry
	 */
	public static Image get(String key) {
		return IMAGE_REGISTRY.get(key);
	}

	/** 
	 * gets a handle to the registry 
	 */
	public static ImageRegistry getImageRegistry() 
	{
		return IMAGE_REGISTRY;
	}
	 
	/**
	 * add and image to the image registry
	 */
	protected static ImageDescriptor addImageToRegistry(String offset, String name) 
	{
		try 
		{
			//System.err.println(createIconFileURL(offset,name));
			ImageDescriptor result = ImageDescriptor.createFromURL(
				createIconFileURL(
					offset,
					name
				)
			);
			
			//System.err.println("adding: " + name + " " + result.toString());
			IMAGE_REGISTRY.put(name, result);
			
			return result;	
		} 
		catch (MalformedURLException e) 
		{
			e.printStackTrace(System.err);
			return ImageDescriptor.getMissingImageDescriptor();
		}
	}

	/** 
	 * create an image descriptor from an offset and name 
	 */
	protected static ImageDescriptor createDescriptor(String offset, String name)
	{
		try 
		{
			return ImageDescriptor.createFromURL(
				createIconFileURL(offset, name)
			);
		}
		catch (MalformedURLException e) 
		{
			return ImageDescriptor.getMissingImageDescriptor();
		}
	}

	/** 
	 * creates a url to an icon using the base, offset and the name 
	 */
	protected static URL createIconFileURL(String offset, String name) throws MalformedURLException 
	{
		if(iconBaseURL == null) throw new MalformedURLException();

		if(offset != null)
		{	
			StringBuffer buffer = new StringBuffer(offset);
			buffer.append('/');
			buffer.append(name);
			return new URL(iconBaseURL, buffer.toString());
		}
		else
		{
			return new URL(iconBaseURL, name);
		}
	}
	
	/**
	 * Creates an overlay dot at the bottom right of the given image.
	 * The dot is offset at 1px from the right-most and bottom-most non-transparent pixel of the image.
	 * Evaluation of the right-most and bottom-most non-transparent pixel is performed by reading left from the
	 * center of the image y axis and up from the center of the image x axis respectively.
	 * @param image The image that the overlay will be added to
	 * @param color The color that the overlay should be.
	 * @return
	 */
	public static Image addOverlay(Image image, RGB color) {
	    int overlayColor = image.getImageData().palette.getPixel(color);
        ImageData fullImageData = image.getImageData();
        ImageData transparency = fullImageData.getTransparencyMask();
        
        int width = fullImageData.width;
        int height = fullImageData.height;
        int midX = java.lang.Math.round(width/2);
        int midY = java.lang.Math.round(height/2);
        for (int i=width-1;i>=0;i--) {
            int pixelColor = fullImageData.getPixel(i,midX);
            int transColor = transparency.getPixel(i,midY);
            if (pixelColor != transColor) {
                width = i;
                break;
            }
        }
        for (int i = height-1;i>=0;i--) {
            int pixelColor = fullImageData.getPixel(midX,i);
            int transColor = transparency.getPixel(midY,i);
            if (pixelColor != transColor) {
                height = i;
                break;
            }
        }
        int minX = width-4;
        int maxX = width-1;
        int minY = height-4;
        int maxY = height-1;
        for (int i=minX;i<=maxX;i++) {
            for (int j=minY;j<=maxY;j++) {
                boolean repaint = true;
                if (i == minX 
                        || i == maxX) {
                    if (j == minY) {
                        repaint = false;
                    } else if (j == maxY) {
                        repaint = false;
                    }
                }
                
                if (repaint) {
                    fullImageData.setPixel(i,j,overlayColor);
                }
            }
        }
        Image fullImage = new Image(Display.getCurrent(),fullImageData);
        return fullImage;
    }
	
}