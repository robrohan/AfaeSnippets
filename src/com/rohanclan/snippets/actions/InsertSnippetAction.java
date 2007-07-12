/*
 * Created on Feb 18, 2004
 *
 * The MIT License
 * Copyright (c) 2004 Stephen Milligan, Rob Rohan
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
package com.rohanclan.snippets.actions;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.FindReplaceDocumentAdapter;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.texteditor.ITextEditor;

import com.rohanclan.afae.editor.AfaeEditorTools;
import com.rohanclan.afae.editor.actions.Encloser;
import com.rohanclan.snippets.core.SnipKeySequence;
import com.rohanclan.snippets.core.SnipReader;
import com.rohanclan.snippets.core.SnipVarParser;

public class InsertSnippetAction extends Encloser implements IEditorActionDelegate {
	protected ITextEditor editor = null;
	protected String start = "";
	protected String end = "";

	public InsertSnippetAction() {;}

	public void run() {
		run(null);
	}

	public void run(IAction action) {
		
		if (editor != null && editor.isEditable()) {
			SnipKeySequence keyCombos = new SnipKeySequence();
			String sequence = "";
			
			IDocument doc = editor.getDocumentProvider().getDocument(editor.getEditorInput());
			ISelection sel = editor.getSelectionProvider().getSelection();

			int cursorOffset = ((ITextSelection) sel).getOffset();
			int lastSpaceOffset = -1;
			// int nextSpaceOffset = -1;
			FindReplaceDocumentAdapter finder = new FindReplaceDocumentAdapter(doc);

			try {
				IRegion lastSpace = finder.find(cursorOffset - 1, "[^\\*0-9a-zA-Z_-]", false, false, false, true);

				if (lastSpace == null) {
					lastSpaceOffset = 0;
				} else {
					lastSpaceOffset = lastSpace.getOffset() + 1;
				}

				// System.out.println("Last Space at" + lastSpaceOffset);
				// System.out.println("Cursot at" + cursorOffset);

				if (cursorOffset > lastSpaceOffset) {
					// ok, it could be valid, but we need to check what comes
					// after the cursor.
					if (cursorOffset != doc.getLength()) {
						// System.out.println("yep");
						IRegion nextSpace = finder.find(cursorOffset - 1, "[^\\*0-9a-zA-Z_-]", true, false, false, true);

						if (nextSpace != null && nextSpace.getOffset() == cursorOffset) {
							// System.out.println("Next space bit");
							sequence = doc.get().substring(lastSpaceOffset, cursorOffset);
						}
					} else {
						sequence = doc.get().substring(lastSpaceOffset, cursorOffset);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			if (sequence.length() > 0) {
				String[] stringArray = sequence.split("\\*");
				String trigger = stringArray[0];

				// System.out.println(trigger);

				int loopcount = 1;
				if (stringArray.length > 1)	{
					loopcount = Integer.parseInt(stringArray[1].trim());
				}

				String fileName = keyCombos.getKeySequence(trigger);
				//System.out.println(fileName);
				
				this.insertSnippetFile(fileName, loopcount, sequence);
			}
		}
	}

	/**
	 * Inserts the filename snippet, loopcount number of times and assumes that
	 * replacesql triggered the calling and removes it from the current area in
	 * the document (use "" if calling from outside)
	 * @param fileName
	 * @param loopcount
	 * @param replaceseq
	 */
	public void insertSnippetFile(String fileName, int loopcount, String replaceseq){
		SnipReader snipReader = new SnipReader();
		IFile activeFile = null;

		if(this.editor.getEditorInput() instanceof IFileEditorInput) {
			activeFile = ((IFileEditorInput) this.editor.getEditorInput()).getFile();
		}
		IDocument doc = editor.getDocumentProvider().getDocument(editor.getEditorInput());
		ISelection sel = editor.getSelectionProvider().getSelection();
		
		//snipReader.read(keyCombos.getSnippetFolder() + fileName);
		if (fileName == null)
			return;

		snipReader.read(fileName);
		
		// String snippet = "";
		int finalCursorOffset = -1;

		for (int i = 0; i < loopcount; i++)	{
			start = SnipVarParser.parse(snipReader.getSnipStartBlock(), activeFile, this.editor.getSite().getShell());
			end = SnipVarParser.parse(snipReader.getSnipEndBlock(), activeFile, this.editor.getSite().getShell());

			if(start == null || end == null) {
				// snippet = null;
				break;
			} else {
				// snippet = start+end;
				try	{
					String preaddstring = AfaeEditorTools.getPrevLineWhiteSpace(doc, ((ITextSelection) sel).getOffset());

					int orgoffset = ((ITextSelection) sel).getOffset();
					int orgseqlen = replaceseq.length();
					
					int cursor_move_offset = (orgoffset - orgseqlen + start.length());
					
					//if the end bit start with a newline start it out with the proper indention
					//and setup the reposition value to move to the right place
					if(end.startsWith("\n")) {
						end = AfaeEditorTools.addIndentation(end, preaddstring);
						cursor_move_offset += preaddstring.length();
					}
					
					enclose(
						doc, 
					    (ITextSelection)sel, 
					    start,
					    end
					);
					
					// remove the trigger text
					doc.replace(orgoffset - orgseqlen, orgseqlen, "");

					// move the cursor back to where one would think it
					// would be
					editor.setHighlightRange( cursor_move_offset, 0, true);
				} catch (BadLocationException e) {
					e.printStackTrace();
				}
			}
		}
		if (finalCursorOffset > 0) {
			editor.setHighlightRange(finalCursorOffset, 0, true);
		}
	}
	
	
	public void setActiveEditor(IAction action, IEditorPart targetEditor) {
		// System.err.println(targetEditor);
		// //System.out.println( "Changin (" + start + ")(" + end + ")" );
		if (targetEditor instanceof ITextEditor) { // || targetEditor
													// instanceof CFMLEditor ){
			editor = (ITextEditor) targetEditor;
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {
		;
	}
}
