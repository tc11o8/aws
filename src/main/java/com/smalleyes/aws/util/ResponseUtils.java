package com.smalleyes.aws.util;

import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ResponseUtils {
	
	public static final Logger log = LoggerFactory.getLogger(ResponseUtils.class);

	public static void renderText(HttpServletResponse response, String text) {
		render(response, "text/plain;charset=UTF-8", text);
	}

	public static void renderJson(HttpServletResponse response, String text) {
		render(response, "application/json;charset=UTF-8", text);
	}

	public static void renderXml(HttpServletResponse response, String text) {
		render(response, "text/xml;charset=UTF-8", text);
	}

	public static void renderJsonGzip(HttpServletResponse response, String text) {
		renderGzip(response, "text/xml;charset=UTF-8", text);
	}

	/**
	 * 发送内容。使用UTF-8编码。
	 * 
	 * @param response
	 * @param contentType
	 * @param text
	 */
	public static void render(HttpServletResponse response, String contentType, String text) {
		String str = !StringUtils.isBlank(text) ? text : "无";
		response.setContentType(contentType);
		response.setHeader("Pragma", "No-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0);
		try {
			response.getWriter().write(str);
			response.getWriter().flush();
			response.getWriter().close();
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
	}

	/**
	 * 发送内容。使用UTF-8编码。
	 * 
	 * @param response
	 * @param contentType
	 * @param text
	 */
	public static void renderGzip(HttpServletResponse response, String contentType, String text) {
		String str = !StringUtils.isBlank(text) ? text : "";
		response.setContentType(contentType);
		response.setHeader("Pragma", "No-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0);
		response.setHeader("Accept-Encoding", "gzip");

		// try {
		// GZIPOutputStream out =new
		// GZIPOutputStream(response.getOutputStream());
		// out.write(str.getBytes());
		// out.flush();
		// out.close();
		// } catch (IOException e) {
		// log.error(e.getMessage(), e);
		// }

		try {
			response.getWriter().write(str);
			response.getWriter().flush();
			response.getWriter().close();
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
	}

}
