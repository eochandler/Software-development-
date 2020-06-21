import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Cleans simple, validating HTML 4/5 into plain-text words using regular
 * expressions.
 *
 * @see <a href="https://validator.w3.org/">validator.w3.org</a>
 * @see <a href="https://www.w3.org/TR/html51/">HTML 5.1 Specification</a>
 * @see <a href="https://www.w3.org/TR/html401/">HTML 4.01 Specification</a>
 *
 * @see java.util.regex.Pattern
 * @see java.util.regex.Matcher
 * @see java.lang.String#replaceAll(String, String)
 */
public class HTMLCleaner {

	/**
	 * Replaces all HTML entities with an empty string. For example,
	 * "2010&ndash;2012" will become "20102012".
	 *
	 * @param html text including HTML entities to remove
	 * @return text without any HTML entities
	 */
	public static String stripEntities(String html) {
		Pattern pattern = Pattern.compile("\\w+");
		Matcher matcher = pattern.matcher(html);
		while (matcher.find()) {
			System.out.print("Start index: " + matcher.start());
			System.out.print(" End index: " + matcher.end() + " ");
			System.out.println(matcher.group());
		}

		return matcher.group();
	}

	/**
	 * Replaces all HTML tags with an empty string. For example, "A<b>B</b>C" will
	 * become "ABC".
	 *
	 * @param html text including HTML tags to remove
	 * @return text without any HTML tags
	 */
	public static String stripTags(String html) {
		return html.replaceAll("\\<[^>]*>", "");
	}

	/**
	 * Replaces all HTML comments with a single space. For example, "A<!-- B -->C"
	 * will become "A C".
	 *
	 * @param html text including HTML comments to remove
	 * @return text without any HTML comments
	 */
	public static String stripComments(String html) {
		html = html.replaceAll("\n", "");
		return html.replaceAll("<!--.*?-->", " ");
	}

	/**
	 * Replaces everything between the element tags and the element tags themselves
	 * with a single space. For example, consider the html code: *
	 *
	 * <pre>
	 * &lt;style type="text/css"&gt;body { font-size: 10pt; }&lt;/style&gt;
	 * </pre>
	 *
	 * If removing the "style" element, all of the above code will be removed, and
	 * replaced with a single space.
	 *
	 * @param html text including HTML elements to remove
	 * @param name name of the HTML element (like "style" or "script")
	 * @return text without that HTML element
	 */
	public static String stripElement(String html, String name) {

		return html.replaceAll("\\</*" + name + ">*" + ".*" + "\\</*" + name + "*>", " ");

//		return html.replaceAll("/<script\\b[^>]*>(.*?)<\\/script>/i","" );
		// System.out.println(html + ")))))))))\n");
//		if (html != null) {
//			String re = "<" + name + ">(.+)</" + name + ">";
//			Pattern pattern = Pattern.compile(re);
//			Matcher matcher = pattern.matcher(html);
//			if (matcher.find()) {
//				return html.replace(matcher.group(1), ""); 
//			}
//		}
//		return null;
	}

	/**
	 * Removes all HTML (including any CSS and JavaScript).
	 *
	 * @param html text including HTML to remove
	 * @return text without any HTML, CSS, or JavaScript
	 */
	public static String stripHTML(String html) {
		// THIS METHOD IS PROVIDED FOR YOU -- DO NOT MODIFY!
		html = stripComments(html);

		html = stripElement(html, "head");
		html = stripElement(html, "style");
		html = stripElement(html, "script");
		html = stripTags(html);
		html = stripEntities(html);

		return html;
	}
}