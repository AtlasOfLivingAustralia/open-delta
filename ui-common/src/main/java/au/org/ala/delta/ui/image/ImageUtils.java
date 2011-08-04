package au.org.ala.delta.ui.image;

import org.apache.commons.lang.StringUtils;

import au.org.ala.delta.model.image.Image;

public class ImageUtils {

    /**
     * @param image
     *            the image to get the text for.
     * @return the subject text of an image, or the filename if none has been
     *         specified.
     */
    public static String subjectTextOrFileName(Image image) {
        String text = image.getSubjectText();
        if (StringUtils.isEmpty(text)) {
            text = image.getFileName();
        }
        return text;
    }

}
