package framework.utils.filters;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.DocumentFilter;

/**
 * Integer filter for filtering out unnecessary text that does not
 * constitute as a number
 * 
 * @author Daniel Ricci {@literal <thedanny09@icloud.com>}
 */
public class DocumentIntegerFilter extends DocumentFilter {

    @Override public void insertString(FilterBypass filter, int offset, String string, AttributeSet attributeSet) throws BadLocationException {

        Document doc = filter.getDocument();

        StringBuilder builder = new StringBuilder();
        builder.append(doc.getText(0, doc.getLength()));
        builder.insert(offset, string);

        if (isNumeral(builder.toString())) {
            super.insertString(filter, offset, string, attributeSet);
        } else {
            // warn the user and don't allow the insert
        }
    }

    /**
     * Verifies if the specified text is a number
     * 
     * @param text The text to verify
     * 
     * @return TRUE if the text is a numeral, FALSE if the text is not a numeral
     */
    private boolean isNumeral(String text) {
        try {
            // Verify if the text is empty
            if(!text.isEmpty()) {

                // Try and convert to an integer value, if it fails
                // then it will go inside of the catch block
                // Note: This method does not support decimals right now
                Integer.parseInt(text);
            }
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Override public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {

        Document doc = fb.getDocument();
        StringBuilder sb = new StringBuilder();
        sb.append(doc.getText(0, doc.getLength()));
        sb.replace(offset, offset + length, text);

        if (isNumeral(sb.toString())) {
            super.replace(fb, offset, length, text, attrs);
        } else {
            // warn the user and don't allow the insert
        }

    }

    @Override public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
        Document doc = fb.getDocument();
        StringBuilder sb = new StringBuilder();
        sb.append(doc.getText(0, doc.getLength()));
        sb.delete(offset, offset + length);

        if (isNumeral(sb.toString())) {
            super.remove(fb, offset, length);
        } else {
            // warn the user and don't allow the insert
        }
    }
}
