package com.wpf.service;

public interface MailService {
    void sendSimpleMail(String to, String subject, String content);

    void sendHtmlMail(String to, String subject, String content);

    void sendAttachmentsMail(String to, String subject, String content, String filePath);

    void sendAttachmentsMail(String to, String subject, String content, String[] filePathList);

    void sendInlineResourceMail(String to, String subject, String content, String rscPath, String rscId);

    void sendInlineResourceMail(String to, String subject, String content, String[] rscPathList, String[] rscIdList);
}
