/*
 * Created on 	: 26-Aug-2004
 * Created by 	: Mark Drew
 * File		  	: SnipSmartDialog.java
 * Description	: This class is used to display SnipDialog on insertion. It should be cleverer to do more functions
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

import org.eclipse.core.resources.IFile;
import org.eclipse.swt.widgets.Shell;

import java.util.*;
/**
 * @author Administrator
 *
 */
public class SnipSmartDialog 
{    
    /*
     * This is the constructor
     */
    public SnipSmartDialog() 
    {
		super();
	}
    
    public static String parse(String str, IFile activeFile, Shell shell ) 
    {
        String newStr = str;
        ArrayList<SnipVarItem> list = new ArrayList<SnipVarItem>();
        int position = 0;
        
        while(newStr.indexOf("$${",position) >= 0) {
			int expressionStart = newStr.indexOf("$${",position)+3;
			int expressionEnd = newStr.indexOf("}",expressionStart);
			String expression = newStr.substring(expressionStart,expressionEnd);
			String stringArray[] = expression.split(":");
			String variable = stringArray[0];
			String defaultValue = "";
			if (stringArray.length > 1) {
			     defaultValue = stringArray[1]; 
			}
			String optionValArray[] = defaultValue.split("\\|");
			
			SnipVarItem item = new SnipVarItem(variable,optionValArray,expression);
			
			Iterator<SnipVarItem> i = list.iterator();
			
			boolean duplicateItem = false;
			while(i.hasNext()) {
			    if (((SnipVarItem)i.next()).getOriginal().equalsIgnoreCase(expression)) {
			        duplicateItem = true;
			    }
			}
			
			if (!duplicateItem) {
				list.add(item);
			}
			
			position = expressionEnd;
        }
        
        if (list.iterator().hasNext()) {
	        
	        SnipDialog dia = new SnipDialog(shell);
		    dia.setItemList(list);
		    dia.setTitle("Replace  variables");
		    if(dia.open() == org.eclipse.jface.window.Window.OK){
		        try {
			        Iterator<SnipVarItem> i = list.iterator();
			        while (i.hasNext()) {
			            SnipVarItem item = (SnipVarItem)i.next();
			            String original = "$${" + item.getOriginal() + "}";
			            String replacement = item.getReplacement();
			            newStr = doReplacement(newStr,original,replacement);
			        }
		        }
		        catch(Exception e) {
		            e.printStackTrace();
		        }
		        
		        dia.close();
		    }
		    else {
		        return null;
		    }
        }
        return newStr;
    }

    private static String doReplacement(String oldStr,String original,String replacement) 
    {
	    	StringBuffer buffer = new StringBuffer(oldStr);
	    	int fromOffset = 0;
	    	while(true) {
	    		fromOffset = buffer.indexOf(original,fromOffset); 
	    		if (fromOffset >= 0) {
		    		buffer.replace(fromOffset,fromOffset+original.length(),replacement);
		    		fromOffset = fromOffset + replacement.length();
	    		} else {
	    			break;
	    		}
	    	}
	    	return buffer.toString();
    }
}
