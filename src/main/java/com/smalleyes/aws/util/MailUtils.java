package com.smalleyes.aws.util;

import java.util.List;

import jodd.mail.Email;
import jodd.mail.SendMailSession;
import jodd.mail.SmtpSslServer;

public class MailUtils {

	private static final String FROM_MAIL = "zhexin_admin@qq.com";
	private static final String PASSWORD = "youxiang123";
	private static final String SMTP = "smtp.exmail.qq.com";

	public static void sendTextMail(String title, String message, List<String> toMails) {
		String[] tos = toMails.toArray(new String[toMails.size()]);
		Email email = Email.create().from(FROM_MAIL).to(tos).subject(title).addText(message);
		send(email);
	}

	public static void sendHtmlMail(String title, String message, List<String> toMails) {
		String[] tos = toMails.toArray(new String[toMails.size()]);
		Email email = Email.create().from(FROM_MAIL).to(tos).subject(title).addHtml(message, "utf-8");
		send(email);
	}

	private static void send(Email email) {
		SendMailSession mailSession = new SmtpSslServer(SMTP, FROM_MAIL, PASSWORD).createSession();
		mailSession.open();
		mailSession.sendMail(email);
		mailSession.close();
	}
}
