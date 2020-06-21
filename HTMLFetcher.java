import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

public class HTMLFetcher {

	/**
	 * Given a map of headers (as returned either by
	 * {@link URLConnection#getHeaderFields()} or by
	 * {@link HttpsFetcher#fetchURL(URL)}, determines if the content type of the
	 * response is HTML.
	 *
	 * @param headers map of HTTP headers
	 * @return true if the content type is html
	 *
	 * @see URLConnection#getHeaderFields()
	 * @see HttpsFetcher#fetchURL(URL)
	 */
	public static boolean isHTML(Map<String, List<String>> headers) {
		if (headers.keySet().contains("Content-Type")) {
			List<String> s = headers.get("Content-Type");
			for (String value : s) {
				if (value.contains("html")) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Given a map of headers (as returned either by
	 * {@link URLConnection#getHeaderFields()} or by
	 * {@link HttpsFetcher#fetchURL(URL)}, returns the status code as an int value.
	 * Returns -1 if any issues encountered.
	 *
	 * @param headers map of HTTP headers
	 * @return status code or -1 if unable to determine
	 *
	 * @see URLConnection#getHeaderFields()
	 * @see HttpsFetcher#fetchURL(URL)
	 */
	public static int getStatusCode(Map<String, List<String>> headers) {
		for (String k : headers.keySet()) {
			List<String> valueList = headers.get(k);
			for (String value : valueList) {
				if (value.contains("404")) {
					return 404;
				}
				if (value.contains("200")) {
					return 200;
				}
				if (value.contains("410")) {
					return 410;
				}
			}
		}
		return -1;
	}

	/**
	 * Given a map of headers (as returned either by
	 * {@link URLConnection#getHeaderFields()} or by
	 * {@link HttpsFetcher#fetchURL(URL)}, returns whether the status code
	 * represents a redirect response *and* the location header is properly
	 * included.
	 *
	 * @param headers map of HTTP headers
	 * @return true if the HTTP status code is a redirect and the location header is
	 *         non-empty
	 *
	 * @see URLConnection#getHeaderFields()
	 * @see HttpsFetcher#fetchURL(URL)
	 */
	public static boolean isRedirect(Map<String, List<String>> headers) {

		for (String j : headers.keySet()) {
			List<String> valueList = headers.get(j);
			for (String value : valueList) {
				if (value.contains("redirect")) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Uses {@link HttpsFetcher#fetchURL(URL)} to fetch the headers and content of
	 * the specified url. If the response was HTML, returns the HTML as a single
	 * {@link String}. If the response was a redirect and the value of redirects is
	 * greater than 0, will return the result of the redirect (decrementing the
	 * number of allowed redirects). Otherwise, will return {@code null}.
	 *
	 * @param url       the url to fetch and return as html
	 * @param redirects the number of times to follow a redirect response
	 * @return the html as a single String if the response code was ok, otherwise
	 *         null
	 * @throws IOException
	 *
	 * @see #isHTML(Map)
	 * @see #getStatusCode(Map)
	 * @see #isRedirect(Map)
	 */
	public static String fetchHTML(URL url, int redirects) throws IOException {
		Map<String, List<String>> headers = HttpsFetcher.fetchURL(url);
		if (getStatusCode(headers) == 200 && isHTML(headers)) {
			List<String> content = headers.get("Content");
			String stringLine = String.join("\n", content);
			return stringLine;
		}

		while (redirects > 0) {
			if (url.toString().contains("redirect") && redirects <= 0) {
				return null;
			}

			List<String> listURL = headers.get("Location");
			String stringURL = String.join("", listURL);
			redirects--;
			return fetchHTML(new URL(stringURL), redirects);
		}
		return null;

	}

	/**
	 * @see #fetchHTML(URL, int)
	 */
	public static String fetchHTML(String url) throws IOException {
		return fetchHTML(new URL(url), 0);
	}

	/**
	 * @see #fetchHTML(URL, int)
	 */
	public static String fetchHTML(String url, int redirects) throws IOException {
		return fetchHTML(new URL(url), redirects);
	}

	/**
	 * @see #fetchHTML(URL, int)
	 */
	public static String fetchHTML(URL url) throws IOException {
		return fetchHTML(url, 0);
	}

}